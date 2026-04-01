/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ai.others.CursedWeaponDefense;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.managers.CursedWeaponsManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.CursedWeapon;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.creature.OnCreatureDamageReceived;
import org.l2jmobius.gameserver.model.events.returns.DamageReturn;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jmobius.gameserver.network.serverpackets.NpcSay;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.util.Broadcast;

/**
 * CursedWeaponDefense AI
 * @version Prelude of War 2019
 * @author Notorion
 */
public class CursedWeaponDefense extends Script
{
	private static final Logger _log = Logger.getLogger(CursedWeaponDefense.class.getName());
	private static final Logger LOGGER = Logger.getLogger(CursedWeaponDefense.class.getName());
	
	// Zariche.
	public static final int ZARICHE_BOX = 24370; // Zariche's Treasure Chest.
	public static final int ZARICHE_PRIEST_HAND = 24367; // Priest of Purification - Hand.
	public static final int ZARICHE_PRIEST_PRAYER = 24366; // Priest of Purification - Prayer.
	private static final int ZARICHE_WEAPON_ID = 8190; // Demonic Sword Zariche.
	
	// Akamanah.
	public static final int AKAMANAH_BOX = 24371; // Akamanah's Treasure Chest.
	public static final int AKAMANAH_PRIEST_HAND = 24369; // Priest of Purification - Hand.
	public static final int AKAMANAH_PRIEST_PRAYER = 24368; // Priest of Purification - Prayer.
	private static final int AKAMANAH_WEAPON_ID = 8689; // Blood Sword Akamanah.
	
	// Skills.
	private static final int SKILL_PRAYER = 32819; // Purifying Prayer - Npcs IDs 34366 and 34368.
	private static final int SKILL_HAND = 32820; // Purifying Hand - Npcs IDs 34367 and 34369.
	private static final int SKILL_PETRIFICATION = 32832; // Petrification - Debuff.
	
	// Damage from Priests against Treasure Chest NPCs.
	private static final int DAMAGE_PRIEST_VS_BOX = 300;
	
	// Messages - NpcStringId.
	// Zariche Priest: "Smash the treasure chest to destroy the Demonic Sword Zariche completely!"
	private static final int MSG_PRIEST_ZARICHE = 1803749;
	
	// Akamanah Priest: "Smash the treasure chest to destroy the Blood Sword Akamanah completely!"
	private static final int MSG_PRIEST_AKAMANAH = 1803750;
	
	// Zariche Treasure Chest: "Punish that wicked Priest of Purification!"
	private static final int MSG_BOX_ZARICHE = 1803751;
	
	// Akamanah Treasure Chest: "Punish that wicked Priest of Purification!"
	private static final int MSG_BOX_AKAMANAH = 1803752;
	
	// Config for the visual effect of Zariche/Akamanah Treasure Chest NPCs.
	private static final int VISUAL_STATE_ACTIVE = 1; // Effect 1: Activated (Glow + Particles) - Vulnerable.
	private static final int VISUAL_STATE_PASSIVE = 2; // Effect 2: Petrified NPCs (Dark Particle) - Protected.
	
	// Respawn map.
	private final Map<String, Long> _priestRespawnTimes = new ConcurrentHashMap<>();
	
	// Protection rules - Player holder tolerance time in peace zone during the 18h/22h29 phase.
	private static final int LIMIT_TOLERANCE = 10;
	
	private int _zaricheTimer = 0;
	private int _akamanahTimer = 0;
	
	// Spawn control - Treasure Chest NPCs.
	private long _zaricheBoxDeathTime = 0;
	private long _akamanahBoxDeathTime = 0;
	private static final long RESPAWN_DELAY = 300000; // 5 Minutes.
	
	// Event phase control.
	private boolean _cleanupDone = false;
	private boolean _rewardsSent = false;
	private boolean _forceRespawnDone = false;
	private boolean _endEventTriggered = false;
	
	private volatile boolean _isZaricheProtected = true;
	private volatile boolean _isAkamanahProtected = true;
	
	private final List<Npc> _zarichePriests = new CopyOnWriteArrayList<>();
	private final List<Npc> _akamanahPriests = new CopyOnWriteArrayList<>();
	
	// Location.
	// NPC Zariche's Treasure Chest.
	private static final Location LOC_ZARICHE_BOX = new Location(66766, 24458, -3735);
	
	// Zariche Priest of Purification - Hand.
	private static final Location[] LOC_ZARICHE_HAND =
	{
		new Location(66343, 24881, -3744),
		new Location(67209, 24869, -3744)
	};
	
	// Zariche Priest of Purification - Prayer.
	private static final Location[] LOC_ZARICHE_PRAYER =
	{
		new Location(67171, 24031, -3744),
		new Location(66339, 24039, -3744)
	};
	
	// Npc Akamanah's Treasure Chest.
	private static final Location LOC_AKAMANAH_BOX = new Location(66822, 27432, -3735);
	
	// Akamanah Priest of Purification - Hand.
	private static final Location[] LOC_AKAMANAH_HAND =
	{
		new Location(67265, 27825, -3744),
		new Location(66411, 27829, -3744)
	};
	
	// Akamanah Priest of Purification - Prayer.
	private static final Location[] LOC_AKAMANAH_PRAYER =
	{
		new Location(66394, 26995, -3744),
		new Location(67241, 26988, -3744)
	};
	
	private Npc _zaricheBoxInstance = null;
	private Npc _akamanahBoxInstance = null;
	
	private final Set<Integer> _cachedOwners = ConcurrentHashMap.newKeySet();
	private volatile long _lastOwnerCacheUpdate = 0;
	private static final long OWNER_CACHE_DURATION = 2000L;
	
	// Control of Test and Production modes:
	// false = PRODUCTION MODE (Follows Tuesday and Timetable)
	// true = TEST MODE
	private static boolean _isAdminTestMode = false;
	private static int _adminPhase = 0; // 0=Stop, 1=Normal, 2=Special
	
	public CursedWeaponDefense()
	{
		int[] npcIds =
		{
			ZARICHE_BOX,
			AKAMANAH_BOX,
			ZARICHE_PRIEST_HAND,
			ZARICHE_PRIEST_PRAYER,
			AKAMANAH_PRIEST_HAND,
			AKAMANAH_PRIEST_PRAYER
		};
		
		registerFunction(e -> onCreatureDamageReceived((OnCreatureDamageReceived) e), EventType.ON_CREATURE_DAMAGE_RECEIVED, ListenerRegisterType.NPC, npcIds);
		addAttackId(ZARICHE_BOX, AKAMANAH_BOX, ZARICHE_PRIEST_HAND, ZARICHE_PRIEST_PRAYER, AKAMANAH_PRIEST_HAND, AKAMANAH_PRIEST_PRAYER);
		addKillId(ZARICHE_BOX, AKAMANAH_BOX, ZARICHE_PRIEST_HAND, ZARICHE_PRIEST_PRAYER, AKAMANAH_PRIEST_HAND, AKAMANAH_PRIEST_PRAYER);
		addSpawnId(ZARICHE_PRIEST_HAND, ZARICHE_PRIEST_PRAYER, AKAMANAH_PRIEST_HAND, AKAMANAH_PRIEST_PRAYER, ZARICHE_BOX, AKAMANAH_BOX);
		
		startQuestTimer("INITIAL_SPAWN", 3000, null, null);
		ThreadPool.scheduleAtFixedRate(this::monitorWeaponOwners, 1000, 1000);
		
		LOGGER.info(getClass().getSimpleName() + ": Script loaded successfully.");
	}
	
	// NOTE: Damage configuration (limits for players, pets, summons, and cursed weapon holders) is handled in the core file Creature.java.
	// This method only validates conditions and applies restrictions based on NPC type.
	@RegisterEvent(EventType.ON_CREATURE_DAMAGE_RECEIVED)
	@RegisterType(ListenerRegisterType.NPC)
	public DamageReturn onCreatureDamageReceived(OnCreatureDamageReceived event)
	{
		if (!(event.getTarget() instanceof Npc))
		{
			return null;
		}
		
		final Npc npc = (Npc) event.getTarget();
		final int npcId = npc.getId();
		
		// Protection petrification/Invul.
		if (npc.isAffectedBySkill(SKILL_PETRIFICATION) || npc.isInvul())
		{
			return new DamageReturn(false, true, false, 0);
		}
		
		// Attack validation.
		Creature attacker = event.getAttacker();
		
		// Player/Pet/Summon verification.
		if (!(attacker instanceof Player))
		{
			// Against vulnerable Treasure Chest - Pets/Summons can deal limited damage of 20 (applied in Creature.java).
			if ((npcId == ZARICHE_BOX) || (npcId == AKAMANAH_BOX))
			{
				return null;
			}
			
			// Pets/Summons can never deal damage against Priest of Purification NPCs.
			if (((npcId >= ZARICHE_PRIEST_HAND) && (npcId <= AKAMANAH_PRIEST_PRAYER)) || (npcId == ZARICHE_PRIEST_HAND) || (npcId == ZARICHE_PRIEST_PRAYER) || (npcId == AKAMANAH_PRIEST_HAND) || (npcId == AKAMANAH_PRIEST_PRAYER))
			{
				return new DamageReturn(false, true, false, 0);
			}
			
			return null;
		}
		
		// Validation for players.
		Player player = (Player) attacker;
		
		final int weaponId = player.getActiveWeaponInstance() != null ? player.getActiveWeaponInstance().getTemplate().getId() : 0;
		
		boolean holdingZariche = (weaponId == ZARICHE_WEAPON_ID);
		boolean holdingAkamanah = (weaponId == AKAMANAH_WEAPON_ID);
		boolean holdingCursedWeapon = (holdingZariche || holdingAkamanah);
		
		// Always allowed for player to deal damage against Treasure Chest in vulnerable mode.
		// Regular player damage limit 20 - Player holding cursed sword damage limit 200 (Creature.java).
		if ((npcId == ZARICHE_BOX) || (npcId == AKAMANAH_BOX))
		{
			return null;
		}
		
		// Holder of cursed sword can only deal damage against Priest of Purification with a damage limit of 1500.
		if ((npcId == ZARICHE_PRIEST_HAND) || (npcId == ZARICHE_PRIEST_PRAYER) || (npcId == AKAMANAH_PRIEST_HAND) || (npcId == AKAMANAH_PRIEST_PRAYER))
		{
			if (holdingCursedWeapon)
			{
				return null;
			}
			
			return new DamageReturn(false, true, false, 0);
		}
		
		return null;
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		// Logic for Treasure Chests (Zariche/Akamanah).
		if ((npc.getId() == ZARICHE_BOX) || (npc.getId() == AKAMANAH_BOX))
		{
			if (npc instanceof Attackable)
			{
				Attackable box = (Attackable) npc;
				
				box.clearAggroList();
				box.stopHating(attacker);
				box.abortAttack();
				box.abortCast();
				box.setTarget(null);
				
				if (box.hasAI())
				{
					box.getAI().setIntention(Intention.IDLE);
				}
			}
			return;
		}
		
		if (npc instanceof Attackable)
		{
			Attackable attackable = (Attackable) npc;
			attackable.stopHating(attacker);
			attackable.abortAttack();
			attackable.abortCast();
			attackable.setTarget(null);
		}
	}
	
	// Spawn and Config.
	public void spawnDefense()
	{
		if (_zaricheBoxInstance == null)
		{
			respawnBox(true);
		}
		
		restoreMissingPriests(true);
		
		if (_akamanahBoxInstance == null)
		{
			respawnBox(false);
		}
		
		restoreMissingPriests(false);
	}
	
	private void respawnBox(boolean isZariche)
	{
		if (isZariche)
		{
			if ((_zaricheBoxInstance != null) && !_zaricheBoxInstance.isDead())
			{
				return;
			}
			
			_zaricheBoxInstance = addSpawn(ZARICHE_BOX, LOC_ZARICHE_BOX.getX(), LOC_ZARICHE_BOX.getY(), LOC_ZARICHE_BOX.getZ(), 0, false, 0);
			configureBox(_zaricheBoxInstance);
			_isZaricheProtected = true;
			_zaricheTimer = 0;
		}
		else
		{
			if ((_akamanahBoxInstance != null) && !_akamanahBoxInstance.isDead())
			{
				return;
			}
			
			_akamanahBoxInstance = addSpawn(AKAMANAH_BOX, LOC_AKAMANAH_BOX.getX(), LOC_AKAMANAH_BOX.getY(), LOC_AKAMANAH_BOX.getZ(), 0, false, 0);
			configureBox(_akamanahBoxInstance);
			_isAkamanahProtected = true;
			_akamanahTimer = 0;
		}
	}
	
	private void restoreMissingPriests(boolean isZariche)
	{
		List<Npc> currentPriests = isZariche ? _zarichePriests : _akamanahPriests;
		currentPriests.removeIf(p -> (p == null) || p.isDead());
		
		// NPC group HAND.
		Location[] locsHand = isZariche ? LOC_ZARICHE_HAND : LOC_AKAMANAH_HAND;
		int idHand = isZariche ? ZARICHE_PRIEST_HAND : AKAMANAH_PRIEST_HAND;
		spawnGroup(currentPriests, locsHand, idHand, isZariche, "HAND");
		
		// NPC group PRAYER.
		Location[] locsPrayer = isZariche ? LOC_ZARICHE_PRAYER : LOC_AKAMANAH_PRAYER;
		int idPrayer = isZariche ? ZARICHE_PRIEST_PRAYER : AKAMANAH_PRIEST_PRAYER;
		spawnGroup(currentPriests, locsPrayer, idPrayer, isZariche, "PRAYER");
	}
	
	private void spawnGroup(List<Npc> priestList, Location[] locs, int npcId, boolean isZariche, String typeKey)
	{
		Location targetToLook = isZariche ? LOC_ZARICHE_BOX : LOC_AKAMANAH_BOX;
		
		for (int i = 0; i < locs.length; i++)
		{
			Location loc = locs[i];
			
			String spotId = (isZariche ? "ZARICHE_" : "AKAMANAH_") + typeKey + "_" + i;
			
			if (_priestRespawnTimes.containsKey(spotId))
			{
				long respawnTime = _priestRespawnTimes.get(spotId);
				if (System.currentTimeMillis() < respawnTime)
				{
					continue;
				}
				
				_priestRespawnTimes.remove(spotId);
			}
			
			boolean occupied = false;
			for (Npc p : priestList)
			{
				if (p.calculateDistance2D(loc) < 100)
				{
					occupied = true;
					break;
				}
			}
			
			if (!occupied)
			{
				Npc p = addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), 0, false, 0);
				int headingToBox = p.calculateHeadingTo(targetToLook);
				p.setHeading(headingToBox);
				priestList.add(p);
				
				// Applies protection/attack logic based on the current state.
				configurePriest(p, isZariche ? _isZaricheProtected : _isAkamanahProtected);
			}
		}
	}
	
	private void configureBox(Npc box)
	{
		if (box == null)
		{
			return;
		}
		
		if (box.hasAI())
		{
			box.getAI().setIntention(Intention.IDLE);
		}
		
		box.setImmobilized(true);
		box.setInvul(true);
		applySkill(box, SKILL_PETRIFICATION);
		
		// Defines initial state (Petrified = Effect 2).
		// No loops, just sets the state.
		box.setDisplayEffect(VISUAL_STATE_PASSIVE);
		box.broadcastInfo();
	}
	
	private void configurePriest(Npc priest, boolean protectedMode)
	{
		if (priest == null)
		{
			return;
		}
		
		priest.setImmobilized(true);
		cancelQuestTimer("ATTACK_LOOP", priest, null);
		
		if (protectedMode)
		{
			priest.setInvul(true);
			applySkill(priest, SKILL_PETRIFICATION);
		}
		else
		{
			priest.setInvul(false);
			removeSkill(priest, SKILL_PETRIFICATION);
			startQuestTimer("ATTACK_LOOP", 1000, priest, null);
		}
		
		priest.broadcastInfo();
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		// GM Commands.
		if (event.startsWith("admin_cmd_"))
		{
			// Only accepted if the player is GM.
			if ((player == null) || !player.isGM())
			{
				return null;
			}
			
			if (event.equals("admin_cmd_test_toggle"))
			{
				_isAdminTestMode = !_isAdminTestMode;
				if (!_isAdminTestMode)
				{
					_adminPhase = 0;
				}
				
				player.sendMessage("Cursed Defense Test Mode: " + (_isAdminTestMode ? "ON" : "OFF"));
			}
			else if (event.equals("admin_cmd_phase_1"))
			{
				if (!_isAdminTestMode)
				{
					player.sendMessage("Enable Test Mode first!");
					return null;
				}
				
				_adminPhase = 1; // Normal.
				player.sendMessage("Phase set to: NORMAL (Coward Rules)");
			}
			else if (event.equals("admin_cmd_phase_2"))
			{
				if (!_isAdminTestMode)
				{
					player.sendMessage("Enable Test Mode first!");
					return null;
				}
				
				_adminPhase = 2; // Special.
				player.sendMessage("Phase set to: SPECIAL (Priests Attack)");
			}
			else if (event.equals("admin_cmd_stop"))
			{
				_adminPhase = 0; // Stop.
				player.sendMessage("Phase set to: STOPPED (Statues)");
			}
			else if (event.equals("admin_cmd_reward"))
			{
				int count = 0;
				for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
				{
					if (cw.isActive())
					{
						cw.endOfLife();
						count++;
					}
				}
				
				if (count > 0)
				{
					player.sendMessage("Success: " + count + " Cursed Weapons have been finalized.");
					player.sendMessage("Reward emails have been sent by the Core.");
				}
				else
				{
					player.sendMessage("No Cursed Weapon was active at the moment.");
				}
			}
			
			return null;
		}
		
		if (event.equals("INITIAL_SPAWN"))
		{
			// Executes the initial spawn to create the Priests.
			spawnDefense();
			return null;
		}
		
		if (event.equals("ATTACK_LOOP") && (npc != null) && !npc.isDead())
		{
			boolean isZariche = ((npc.getId() == ZARICHE_PRIEST_HAND) || (npc.getId() == ZARICHE_PRIEST_PRAYER));
			
			if (isZariche ? _isZaricheProtected : _isAkamanahProtected)
			{
				// Ensures the NPC is visually petrified.
				if (!npc.isAffectedBySkill(SKILL_PETRIFICATION))
				{
					applySkill(npc, SKILL_PETRIFICATION);
				}
				
				// Rescheduling and checking the Cursed Sword holder in peace zones.
				startQuestTimer("ATTACK_LOOP", 3000, npc, null);
				return null;
			}
			
			int skillToCast = 0;
			if ((npc.getId() == ZARICHE_PRIEST_HAND) || (npc.getId() == AKAMANAH_PRIEST_HAND))
			{
				skillToCast = SKILL_HAND; // NPCs 24367, 24369.
			}
			else
			{
				skillToCast = SKILL_PRAYER; // NPCs 24366, 24368.
			}
			
			Npc targetBox = isZariche ? _zaricheBoxInstance : _akamanahBoxInstance;
			if ((targetBox != null) && !targetBox.isDead())
			{
				// Remove petrification if present.
				if (npc.isAffectedBySkill(SKILL_PETRIFICATION))
				{
					removeSkill(npc, SKILL_PETRIFICATION);
				}
				
				npc.setHeading(npc.calculateHeadingTo(targetBox));
				npc.setTarget(targetBox);
				npc.broadcastPacket(new MagicSkillUse(npc, targetBox, skillToCast, 1, 2500, 0));
				
				// Applying damage against Treasure Chest NPCs.
				double newHp = targetBox.getCurrentHp() - DAMAGE_PRIEST_VS_BOX;
				if (newHp <= 0)
				{
					targetBox.setCurrentHp(0);
					targetBox.doDie(npc);
				}
				else
				{
					targetBox.setCurrentHp(newHp);
				}
			}
			
			startQuestTimer("ATTACK_LOOP", 3700, npc, null);
		}
		
		else if (event.startsWith("RESPAWN_PRIEST_CHECK"))
		{
			boolean isZariche = event.contains("ZARICHE");
			restoreMissingPriests(isZariche);
			startQuestTimer(event, 30000, null, null);
		}
		// NPC speeches.
		else if (event.equals("SPEECH_BOX") && (npc != null) && !npc.isDead())
		{
			if (npc.getId() == ZARICHE_BOX)
			{
				if (_isZaricheProtected)
				{
					return null;
				}
			}
			else
			{
				if (_isAkamanahProtected)
				{
					return null;
				}
			}
			
			int stringId = (npc.getId() == ZARICHE_BOX) ? MSG_BOX_ZARICHE : MSG_BOX_AKAMANAH;
			npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.getNpcStringId(stringId)));
			
			// Random timed speeches.
			startQuestTimer("SPEECH_BOX", getRandom(45000, 90000), npc, null);
		}
		else if (event.equals("SPEECH_PRAYER") && (npc != null) && !npc.isDead())
		{
			boolean isZaricheSide = (npc.getId() == ZARICHE_PRIEST_PRAYER);
			
			if (isZaricheSide)
			{
				if (_isZaricheProtected)
				{
					return null;
				}
			}
			else
			{
				if (_isAkamanahProtected)
				{
					return null;
				}
			}
			
			int stringId = isZaricheSide ? MSG_PRIEST_ZARICHE : MSG_PRIEST_AKAMANAH;
			npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.getNpcStringId(stringId)));
			startQuestTimer("SPEECH_PRAYER", getRandom(25000, 32000), npc, null);
		}
		else if (event.equals("SPEECH_HAND") && (npc != null) && !npc.isDead())
		{
			boolean isZaricheSide = (npc.getId() == ZARICHE_PRIEST_HAND);
			
			if (isZaricheSide)
			{
				if (_isZaricheProtected)
				{
					return null;
				}
			}
			else
			{
				if (_isAkamanahProtected)
				{
					return null;
				}
			}
			
			int stringId = isZaricheSide ? MSG_PRIEST_ZARICHE : MSG_PRIEST_AKAMANAH;
			npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.getNpcStringId(stringId)));
			startQuestTimer("SPEECH_HAND", getRandom(25000, 65000), npc, null);
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		if (npc.getId() == ZARICHE_BOX)
		{
			CursedWeaponsManager.getInstance().addBoxLocation(CursedWeaponsManager.ZARICHE_BOX_NPC_ID, npc.getLocation());
		}
		else if (npc.getId() == AKAMANAH_BOX)
		{
			CursedWeaponsManager.getInstance().addBoxLocation(CursedWeaponsManager.AKAMANAH_BOX_NPC_ID, npc.getLocation());
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if ((npc.getId() == ZARICHE_PRIEST_HAND) || (npc.getId() == ZARICHE_PRIEST_PRAYER))
		{
			_zarichePriests.remove(npc);
			
			// Individual kill registration.
			boolean isHand = (npc.getId() == ZARICHE_PRIEST_HAND);
			registerIndividualDeath(npc, isHand ? LOC_ZARICHE_HAND : LOC_ZARICHE_PRAYER, true, isHand ? "HAND" : "PRAYER");
			startQuestTimer("RESPAWN_PRIEST_CHECK_ZARICHE", 10000, null, null);
		}
		else if ((npc.getId() == AKAMANAH_PRIEST_HAND) || (npc.getId() == AKAMANAH_PRIEST_PRAYER))
		{
			_akamanahPriests.remove(npc);
			
			// Individual kill registration.
			boolean isHand = (npc.getId() == AKAMANAH_PRIEST_HAND);
			registerIndividualDeath(npc, isHand ? LOC_AKAMANAH_HAND : LOC_AKAMANAH_PRAYER, false, isHand ? "HAND" : "PRAYER");
			
			startQuestTimer("RESPAWN_PRIEST_CHECK_AKAMANAH", 10000, null, null);
		}
		
		if ((npc.getId() == ZARICHE_BOX) || (npc.getId() == AKAMANAH_BOX))
		{
			boolean isZariche = (npc.getId() == ZARICHE_BOX);
			
			CursedWeapon weapon = CursedWeaponsManager.getInstance().getCursedWeapon(isZariche ? ZARICHE_WEAPON_ID : AKAMANAH_WEAPON_ID);
			
			if ((weapon != null) && (weapon.getPlayer() != null) && weapon.getPlayer().isOnline())
			{
				Player owner = weapon.getPlayer();
				long expBefore = owner.getExp();
				owner.doDie(npc);
				
				if (owner.getExp() < expBefore)
				{
					owner.addExpAndSp(expBefore - owner.getExp(), 0);
				}
				
				// owner.sendMessage("You failed to protect the Treasure Chest!");
			}
			
			// Marks the time of the kill.
			if (isZariche)
			{
				_zaricheBoxInstance = null;
				_zaricheBoxDeathTime = System.currentTimeMillis();
			}
			else
			{
				_akamanahBoxInstance = null;
				_akamanahBoxDeathTime = System.currentTimeMillis();
			}
			
			// Removes from the map.
			CursedWeaponsManager.getInstance().removeBoxLocation(isZariche ? CursedWeaponsManager.ZARICHE_BOX_NPC_ID : CursedWeaponsManager.AKAMANAH_BOX_NPC_ID, npc.getLocation());
			
			try
			{
				final CursedWeapon cw = CursedWeaponsManager.getInstance().getCursedWeapon(isZariche ? ZARICHE_WEAPON_ID : AKAMANAH_WEAPON_ID);
				if ((cw != null) && cw.isActivated())
				{
					CursedWeaponsManager.getInstance().drop(isZariche ? ZARICHE_WEAPON_ID : AKAMANAH_WEAPON_ID, null);
				}
			}
			catch (Exception e)
			{
			}
		}
		
		super.onKill(npc, killer, isSummon);
	}
	
	private void registerIndividualDeath(Npc deadNpc, Location[] locs, boolean isZariche, String typeKey)
	{
		for (int i = 0; i < locs.length; i++)
		{
			if (deadNpc.calculateDistance2D(locs[i]) < 300)
			{
				String spotId = (isZariche ? "ZARICHE_" : "AKAMANAH_") + typeKey + "_" + i;
				
				// 8 minutes (480.000 ms).
				_priestRespawnTimes.put(spotId, System.currentTimeMillis() + 480000);
				break;
			}
		}
	}
	
	private void monitorWeaponOwners()
	{
		try
		{
			// Cleanup.
			if ((_zaricheBoxInstance != null) && _zaricheBoxInstance.isDead())
			{
				onKill(_zaricheBoxInstance, null, false);
			}
			
			if ((_akamanahBoxInstance != null) && _akamanahBoxInstance.isDead())
			{
				onKill(_akamanahBoxInstance, null, false);
			}
			
			final long now = System.currentTimeMillis();
			if ((now - _lastOwnerCacheUpdate) > OWNER_CACHE_DURATION)
			{
				updateOwnerCache();
				_lastOwnerCacheUpdate = now;
			}
			
			// Total possession time of the cursed sword is 5 hours. At the end of the event the cursed sword will be removed from the player.
			// Removal due to inactivity of the holder during the event or in GM test mode (2 hours without a kill).
			for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
			{
				// Only check if the weapon is currently active.
				if (cw.isActive())
				{
					final Player player = cw.getPlayer();
					
					// Apply penalty only if the player is online.
					if (player != null)
					{
						final long lastKill = cw.getLastKillTime();
						if (lastKill == 0)
						{
							// If 0, the weapon was just acquired. Start counting from now.
							cw.setLastKillTime(now);
						}
						else if ((now - lastKill) >= 7200000) // 7200000ms = 2 hours
						{
							// Time expired. Notify player and remove the weapon using core function (custom).
							// player.sendMessage("Cursed Weapon: The demonic power abandoned you due to lack of blood.");
							cw.endOfLife();
						}
					}
				}
			}
			
			// ADMIN MODE.
			if (_isAdminTestMode)
			{
				if (_adminPhase == 0)
				{
					maintainStatueMode(true);
					maintainStatueMode(false);
					return;
				}
				
				if ((_zaricheBoxInstance == null) && ((now - _zaricheBoxDeathTime) > RESPAWN_DELAY))
				{
					respawnBox(true);
				}
				
				if ((_akamanahBoxInstance == null) && ((now - _akamanahBoxDeathTime) > RESPAWN_DELAY))
				{
					respawnBox(false);
				}
				
				if (_adminPhase == 2)
				{
					if (!_forceRespawnDone)
					{
						_forceRespawnDone = true;
						Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.S1).addString("TEST MODE: Special Event Started!"));
					}
					
					liftDefense(true);
					liftDefense(false);
				}
				else
				{
					_forceRespawnDone = false;
					
					// Zariche.
					if ((_zaricheBoxInstance != null) && !_zaricheBoxInstance.isDead())
					{
						checkRules(ZARICHE_WEAPON_ID, true);
					}
					else
					{
						restoreDefense(true);
					}
					
					// Akamanah.
					if ((_akamanahBoxInstance != null) && !_akamanahBoxInstance.isDead())
					{
						checkRules(AKAMANAH_WEAPON_ID, false);
					}
					else
					{
						restoreDefense(false);
					}
				}
				return;
			}
			
			// Production mode.
			Calendar cal = Calendar.getInstance();
			int currentSeconds = (cal.get(Calendar.HOUR_OF_DAY) * 3600) + (cal.get(Calendar.MINUTE) * 60) + cal.get(Calendar.SECOND);
			int currentDay = cal.get(Calendar.DAY_OF_WEEK);
			
			// Event runs only on Monday - Thursday.
			boolean isEventDay = (currentDay == Calendar.MONDAY) || (currentDay == Calendar.THURSDAY);
			
			// Event schedule.
			int tStartNormal = 18 * 3600; // 18:00 Phase 1.
			int tStartSpec = (22 * 3600) + (30 * 60); // 22:30 Special Phase 2.
			int tEndEvent = 23 * 3600; // 23:00 End.
			int tStopRespawn = (23 * 3600) + 600; // 23:10 Final respawn.
			
			boolean isEventRunning = isEventDay && ((currentSeconds >= tStartNormal) && (currentSeconds < tEndEvent));
			
			// Daily reset.
			if (isEventRunning && !_forceRespawnDone && (currentSeconds < tStartSpec))
			{
				_cleanupDone = false;
				_rewardsSent = false;
			}
			
			// Outside the event.
			if (!isEventRunning)
			{
				if (_endEventTriggered && !isEventDay)
				{
					_endEventTriggered = false;
				}
				
				if (isEventDay && (currentSeconds >= tEndEvent) && !_endEventTriggered)
				{
					_endEventTriggered = true;
					_forceRespawnDone = false;
					restoreDefense(true);
					restoreDefense(false);
				}
				
				// Cleanup 23:00, kill all NPCs Priests/Treasure.
				if (isEventDay && (currentSeconds >= tEndEvent) && (currentSeconds < tStopRespawn))
				{
					if (!_cleanupDone)
					{
						_cleanupDone = true;
						
						// Kill Treasure Chests.
						if ((_zaricheBoxInstance != null) && !_zaricheBoxInstance.isDead())
						{
							_zaricheBoxInstance.doDie(null);
						}
						
						if ((_akamanahBoxInstance != null) && !_akamanahBoxInstance.isDead())
						{
							_akamanahBoxInstance.doDie(null);
						}
						
						// Kill Priests.
						_zarichePriests.forEach(p ->
						{
							if ((p != null) && !p.isDead())
							{
								p.doDie(null);
							}
						});
						_akamanahPriests.forEach(p ->
						{
							if ((p != null) && !p.isDead())
							{
								p.doDie(null);
							}
						});
						
						// Clear timers.
						_priestRespawnTimes.clear();
					}
					
					return; // Blocks maintainStatueMode until 23:10.
				}
				
				// Respawn of Priests at 23:10.
				maintainStatueMode(true);
				maintainStatueMode(false);
				return;
			}
			
			// Special phase 22:30 - 23:00.
			if (currentSeconds >= tStartSpec)
			{
				// Removes only if on the ground (!Activated && Dropped).
				// Protects the owner holding the weapon to receive the reward.
				if (currentSeconds >= (tEndEvent - 70))
				{
					final int[] weapons =
					{
						ZARICHE_WEAPON_ID,
						AKAMANAH_WEAPON_ID
					};
					for (int wId : weapons)
					{
						CursedWeapon cw = CursedWeaponsManager.getInstance().getCursedWeapon(wId);
						if (cw != null)
						{
							if (!cw.isActivated() && cw.isDropped())
							{
								cw.endOfLife(); // Deletes immediately.
							}
						}
					}
				}
				
				// Check at 22:59 for delivering rewards to players holding the cursed sword.
				if ((currentSeconds >= (tEndEvent - 60)) && (currentSeconds < tEndEvent))
				{
					if (!_rewardsSent)
					{
						_rewardsSent = true;
						
						final int[] weapons =
						{
							ZARICHE_WEAPON_ID,
							AKAMANAH_WEAPON_ID
						};
						for (int wId : weapons)
						{
							final CursedWeapon cw = CursedWeaponsManager.getInstance().getCursedWeapon(wId);
							if ((cw != null) && cw.isActive())
							{
								cw.sendEventReward(); // Script CursedWeapon.java manages reward delivery
								
								// Player owner = cw.getPlayer();
								// if ((owner != null) && owner.isOnline())
								// {
								// owner.sendMessage("EVENT REWARD SENT! You will be killed at 23:00");
								// }
							}
						}
						
						restoreDefense(true);
						restoreDefense(false);
						Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.S1).addString("Cursed Weapon Event ending in 1 minute!"));
					}
					return;
				}
				
				// Logic 22h30min/22h58min.
				if (!_forceRespawnDone)
				{
					_forceRespawnDone = true;
					// Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.S1).addString("Cursed Weapon Special Event Started! Boxes are vulnerable!"));
				}
				
				liftDefense(true);
				
				if (((_zaricheBoxInstance == null) || _zaricheBoxInstance.isDead()) && ((now - _zaricheBoxDeathTime) > RESPAWN_DELAY))
				{
					respawnBox(true);
				}
				
				liftDefense(false);
				
				if (((_akamanahBoxInstance == null) || _akamanahBoxInstance.isDead()) && ((now - _akamanahBoxDeathTime) > RESPAWN_DELAY))
				{
					respawnBox(false);
				}
				
				restoreMissingPriests(true);
				restoreMissingPriests(false);
			}
			
			// Normal phase logic 18h/22h29min.
			else
			{
				_forceRespawnDone = false;
				
				// Zariche.
				if ((_zaricheBoxInstance != null) && !_zaricheBoxInstance.isDead())
				{
					checkRules(ZARICHE_WEAPON_ID, true);
				}
				else
				{
					restoreDefense(true);
					if ((now - _zaricheBoxDeathTime) > RESPAWN_DELAY)
					{
						respawnBox(true);
					}
				}
				
				// Akamanah.
				if ((_akamanahBoxInstance != null) && !_akamanahBoxInstance.isDead())
				{
					checkRules(AKAMANAH_WEAPON_ID, false);
				}
				else
				{
					restoreDefense(false);
					if ((now - _akamanahBoxDeathTime) > RESPAWN_DELAY)
					{
						respawnBox(false);
					}
				}
				
				// Ensures respawn of priests (8 min).
				restoreMissingPriests(true);
				restoreMissingPriests(false);
			}
		}
		catch (Exception e)
		{
			_log.warning("CursedDefense Monitor Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void maintainStatueMode(boolean isZariche)
	{
		if (isZariche)
		{
			if (_zaricheBoxInstance == null)
			{
				respawnBox(true);
			}
		}
		else
		{
			if (_akamanahBoxInstance == null)
			{
				respawnBox(false);
			}
		}
		
		restoreMissingPriests(isZariche);
		restoreDefense(isZariche);
	}
	
	// Helpers.
	private void checkRules(int weaponId, boolean isZariche)
	{
		int playerId = 0;
		final CursedWeapon weapon = CursedWeaponsManager.getInstance().getCursedWeapon(weaponId);
		if (weapon != null)
		{
			playerId = weapon.getPlayerId();
		}
		
		if (playerId == 0)
		{
			if (isZariche)
			{
				_zaricheTimer = 0;
			}
			else
			{
				_akamanahTimer = 0;
			}
			
			if (isZariche ? !_isZaricheProtected : !_isAkamanahProtected)
			{
				restoreDefense(isZariche);
			}
			return;
		}
		
		final Player player = World.getInstance().getPlayer(playerId);
		if ((player != null) && player.isInsideZone(ZoneId.PEACE)) // isOwnerCoward
		{
			if (isZariche)
			{
				_zaricheTimer++;
			}
			else
			{
				_akamanahTimer++;
			}
			
			final int currentTimer = isZariche ? _zaricheTimer : _akamanahTimer;
			if ((currentTimer >= LIMIT_TOLERANCE) && (isZariche ? _isZaricheProtected : _isAkamanahProtected))
			{
				liftDefense(isZariche);
			}
		}
		else
		{
			if (isZariche)
			{
				_zaricheTimer = 0;
			}
			else
			{
				_akamanahTimer = 0;
			}
			
			if (isZariche ? !_isZaricheProtected : !_isAkamanahProtected)
			{
				restoreDefense(isZariche);
			}
		}
	}
	
	private void liftDefense(boolean isZariche)
	{
		if (isZariche)
		{
			_isZaricheProtected = false; // Removes Zariche protection.
		}
		else
		{
			_isAkamanahProtected = false; // Removes Akamanah protection.
		}
		
		final Npc box = isZariche ? _zaricheBoxInstance : _akamanahBoxInstance; // Selects the corresponding Treasure Chest.
		if (box != null)
		{
			removeSkill(box, SKILL_PETRIFICATION); // Removes visual petrification.
			box.setInvul(false); // Removes invulnerability.
			box.setDisplayEffect(VISUAL_STATE_ACTIVE); // Sets visual state to active.
			if (getQuestTimer("SPEECH_BOX", box, null) == null)
			{
				startQuestTimer("SPEECH_BOX", getRandom(45000, 90000), box, null); // Starts random Treasure Chest speeches.
			}
		}
		
		final List<Npc> priests = isZariche ? _zarichePriests : _akamanahPriests; // Selects the corresponding Priests list.
		for (Npc priest : priests)
		{
			if ((priest != null) && !priest.isDead())
			{
				removeSkill(priest, SKILL_PETRIFICATION); // Removes visual petrification from Priest.
				priest.setInvul(false); // Removes invulnerability.
				
				if (getQuestTimer("ATTACK_LOOP", priest, null) == null)
				{
					startQuestTimer("ATTACK_LOOP", 1000, priest, null); // Starts Priest attack loop.
				}
				
				if ((priest.getId() == ZARICHE_PRIEST_PRAYER) || (priest.getId() == AKAMANAH_PRIEST_PRAYER))
				{
					if (getQuestTimer("SPEECH_PRAYER", priest, null) == null)
					{
						startQuestTimer("SPEECH_PRAYER", getRandom(25000, 32000), priest, null); // Starts PRAYER Priest speeches.
					}
				}
				else
				{
					if (getQuestTimer("SPEECH_HAND", priest, null) == null)
					{
						startQuestTimer("SPEECH_HAND", getRandom(25000, 65000), priest, null); // Starts HAND Priest speeches.
					}
				}
			}
		}
	}
	
	private void restoreDefense(boolean isZariche)
	{
		if (isZariche)
		{
			_isZaricheProtected = true; // Activates Zariche protection.
		}
		else
		{
			_isAkamanahProtected = true; // Activates Akamanah protection.
		}
		
		final Npc box = isZariche ? _zaricheBoxInstance : _akamanahBoxInstance; // Selects the corresponding Treasure Chest.
		if (box != null)
		{
			box.setInvul(true); // Makes the Treasure Chest invulnerable.
			applySkill(box, SKILL_PETRIFICATION); // Applies petrification visual effect.
			box.setDisplayEffect(VISUAL_STATE_PASSIVE); // Sets visual state to passive.
			cancelQuestTimer("SPEECH_BOX", box, null); // Cancels random Treasure Chest speeches.
		}
		
		final List<Npc> priests = isZariche ? _zarichePriests : _akamanahPriests; // Selects the corresponding Priests list.
		for (Npc priest : priests)
		{
			if ((priest != null) && !priest.isDead())
			{
				priest.setInvul(true); // Makes the Priest invulnerable.
				applySkill(priest, SKILL_PETRIFICATION); // Applies petrification visual effect.
				
				// CANCELS PRIESTS' SPEECHES AND ATTACKS.
				cancelQuestTimer("ATTACK_LOOP", priest, null); // Stops attacking.
				cancelQuestTimer("SPEECH_PRAYER", priest, null); // Stops PRAYER speeches.
				cancelQuestTimer("SPEECH_HAND", priest, null); // Stops HAND speeches.
			}
		}
	}
	
	private void updateOwnerCache()
	{
		_cachedOwners.clear();
		final CursedWeapon z = CursedWeaponsManager.getInstance().getCursedWeapon(ZARICHE_WEAPON_ID);
		if ((z != null) && (z.getPlayerId() > 0))
		{
			_cachedOwners.add(z.getPlayerId());
		}
		
		final CursedWeapon a = CursedWeaponsManager.getInstance().getCursedWeapon(AKAMANAH_WEAPON_ID);
		if ((a != null) && (a.getPlayerId() > 0))
		{
			_cachedOwners.add(a.getPlayerId());
		}
	}
	
	private void applySkill(Npc npc, int skillId)
	{
		if (npc == null)
		{
			return;
		}
		
		if (npc.isAffectedBySkill(skillId))
		{
			return;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(skillId, 1);
		if (skill != null)
		{
			skill.applyEffects(npc, npc);
		}
	}
	
	private void removeSkill(Npc npc, int skillId)
	{
		if (npc == null)
		{
			return;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(skillId, 1);
		if (skill != null)
		{
			npc.stopSkillEffects(skill);
		}
	}
	
	public static void main(String[] args)
	{
		new CursedWeaponDefense();
	}
}
