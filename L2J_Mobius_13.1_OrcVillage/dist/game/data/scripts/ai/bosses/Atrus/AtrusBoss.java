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
package ai.bosses.Atrus;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.enums.FlyType;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExChangeClientEffectInfo;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.FlyToLocation;
import org.l2jmobius.gameserver.network.serverpackets.TargetUnselected;
import org.l2jmobius.gameserver.network.serverpackets.ValidateLocation;

/**
 * Atrus RaidBoss AI - Version 2025
 * @author Notorion
 */
public class AtrusBoss extends Script
{
	// NPC IDs
	private static final int ATRUS = 29433; // Atrus - RaidBoss
	private static final int CHAOS_SEAL = 29434; // Chaotic Seal - Npc
	private static final int POWER_SEAL = 29435; // Chaotic Seal - Npc
	
	// Weapon Polearm - Atrus phase 2
	private static final int WEAPON_POLEARM_ID = 83344; // Kasha Orc Boss' Elite Stormer
	
	// Zones
	private static final String ZONE_NAME_COMBAT = "AtrusZoneKashaOrcFortress"; // Atrus combat location
	private static final String ZONE_NAME_VISUAL = "AtrusVisualZone"; // Broad area for status 2 and on-screen message
	
	// Global variable
	private static final String ATRUS_VISUAL_VAR = "ATRUS_STORM_ACTIVE";
	
	// Locations
	private static final Location SPAWN_LOC = new Location(-24586, -109012, -3000, 56219); // Atrus location
	
	private static final Location SEAL_LOC_1 = new Location(-25670, -109090, -3034); // Chaotic Seal id 29434 - Spawn
	private static final Location SEAL_LOC_2 = new Location(-23558, -109121, -3033); // Chaotic Seal id 29434 - Spawn
	
	private static final Location POWER_SEAL_LOC_1 = new Location(-23507, -108706, -3033); // Chaotic Seal id 29435 - Spawn
	private static final Location POWER_SEAL_LOC_2 = new Location(-24452, -107999, -3033); // Chaotic Seal id 29435 - Spawn
	private static final Location POWER_SEAL_LOC_3 = new Location(-25601, -108986, -3032); // Chaotic Seal id 29435 - Spawn
	private static final Location POWER_SEAL_LOC_4 = new Location(-24066, -110065, -3032); // Chaotic Seal id 29435 - Spawn
	
	// Skills - Phase 1
	private static final SkillHolder KASHA_BASIC_ATTACK = new SkillHolder(62225, 1); // Kasha's Destruction (Blunt Weapon)
	private static final SkillHolder KASHA_WAVE = new SkillHolder(62226, 1); // Kasha's Wave (Blunt Weapon)
	private static final SkillHolder KASHA_SWEEP = new SkillHolder(62228, 1); // Kasha's Sweep (Blunt Weapon)
	private static final SkillHolder KASHA_PULL = new SkillHolder(62227, 1); // Kasha's Pull (Blunt Weapon)
	private static final SkillHolder KASHA_FRAY = new SkillHolder(62230, 1); // Kasha's Fray (Blunt Weapon)
	
	// Used for visual return effect
	private static final SkillHolder KASHA_VISUAL_JUMP = new SkillHolder(62229, 1); // Kasha's Direct Attack (Blunt Weapon)
	
	// Skills - Phase 2
	private static final SkillHolder KASHA_DESTRUCTION = new SkillHolder(62231, 1); // Kasha's Destruction (Stormer)
	private static final SkillHolder KASHA_INNER_STORM = new SkillHolder(62232, 1); // Kasha's Inner Storm (Stormer)
	private static final SkillHolder KASHA_OUTER_STORM = new SkillHolder(62233, 1); // Kasha's Outer Storm (Stormer)
	
	// Seals
	private static final SkillHolder CHAOS_CURE = new SkillHolder(62234, 1); // Chaos Cure
	
	// Vars
	private Npc _atrus = null;
	
	private final ZoneType _combatZone;
	private final ZoneType _visualZone;
	
	// Flags
	private boolean _isReturning = false;
	private boolean _isMechanicActive = false;
	private boolean _combatStarted = false;
	
	private boolean _phase1PermanentlyDisabled = false;
	
	// Triggers
	private boolean _hp90Triggered = false;
	private boolean _hp70Triggered = false;
	private boolean _hp60Triggered = false;
	private boolean _hp50Triggered = false;
	private boolean _hp40Triggered = false;
	
	private long _lastReturnAttempt = 0;
	private int _comboStep = 0;
	
	// Warmups Phase 2
	private long _outerStormWarmup = 0;
	private long _innerStormWarmup = 0;
	private long _nextStormAvailable = 0;
	
	private final List<Npc> _activeSeals = new ArrayList<>();
	private final List<Npc> _powerSeals = new ArrayList<>();
	
	public AtrusBoss()
	{
		_combatZone = ZoneManager.getInstance().getZoneByName(ZONE_NAME_COMBAT);
		_visualZone = ZoneManager.getInstance().getZoneByName(ZONE_NAME_VISUAL);
		
		addSpawnId(ATRUS, CHAOS_SEAL);
		addDespawnId(ATRUS);
		addAttackId(ATRUS);
		addKillId(ATRUS, CHAOS_SEAL, POWER_SEAL);
		
		if (_combatZone != null)
		{
			addEnterZoneId(_combatZone.getId());
		}
		if (_visualZone != null)
		{
			addEnterZoneId(_visualZone.getId());
		}
		
		GlobalVariablesManager.getInstance().set(ATRUS_VISUAL_VAR, false);
		
		startQuestTimer("CHECK_BOUNDARIES", 1000, null, null, true);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		if (npc.getId() == ATRUS)
		{
			_atrus = npc;
			_atrus.setRandomWalking(false);
			_atrus.setTargetable(true);
			
			if (_atrus.isAttackable())
			{
				_atrus.asAttackable().setCanReturnToSpawnPoint(false);
			}
			
			_isReturning = false;
			_isMechanicActive = false;
			_combatStarted = false;
			_phase1PermanentlyDisabled = false;
			
			_hp90Triggered = false;
			_hp70Triggered = false;
			_hp60Triggered = false;
			_hp50Triggered = false;
			_hp40Triggered = false;
			
			_lastReturnAttempt = 0;
			_comboStep = 0;
			_outerStormWarmup = 0;
			_innerStormWarmup = 0;
			_nextStormAvailable = 0;
			
			_activeSeals.clear();
			_powerSeals.clear();
			
			if (_combatZone != null)
			{
				for (Npc n : World.getInstance().getVisibleObjects(npc, Npc.class))
				{
					if ((n.getId() == CHAOS_SEAL) || (n.getId() == POWER_SEAL))
					{
						if (_combatZone.isInsideZone(n))
						{
							n.deleteMe();
						}
					}
				}
			}
			// 1. Activates the local visual of Kasha Orc Fortress
			setVisualActive(true);
			startQuestTimer("WATCH_VISUAL_STATUS", 1000, null, null, true);
		}
		else if (npc.getId() == CHAOS_SEAL)
		{
			if ((_atrus != null) && !_atrus.isDead())
			{
				npc.setTarget(_atrus);
				npc.setRunning();
				startQuestTimer("SEAL_CAST_ACTION", 1000, npc, null);
			}
		}
	}
	
	public String onDespawn(Npc npc)
	{
		if (npc.getId() == ATRUS)
		{
			performCleanup();
		}
		return null;
	}
	
	private void performCleanup()
	{
		GlobalVariablesManager.getInstance().set(ATRUS_VISUAL_VAR, false);
		// Turns off visual (Broadcast)
		setVisualActive(false);
		despawnSeals();
		despawnPowerSeals();
		cancelQuestTimers("WATCH_VISUAL_STATUS");
		cancelQuestTimers("START_SEAL_EVENT");
		cancelQuestTimers("FINISH_SAFETY_RETURN");
		cancelQuestTimers("EXECUTE_PULL_TO_CENTER");
		cancelQuestTimers("START_ROTATION_MECHANIC");
		cancelQuestTimers("AUTO_SKILL_LOOP");
		cancelQuestTimers("WARN_OUTER_STORM");
		cancelQuestTimers("WARN_INNER_STORM");
		
		_atrus = null;
		_isMechanicActive = false;
		_isReturning = false;
		_combatStarted = false;
		_phase1PermanentlyDisabled = false;
		_outerStormWarmup = 0;
		_innerStormWarmup = 0;
		_nextStormAvailable = 0;
	}
	
	private void setVisualActive(boolean active)
	{
		GlobalVariablesManager.getInstance().set(ATRUS_VISUAL_VAR, active);
		
		if (_visualZone == null)
		{
			return;
		}
		
		ExChangeClientEffectInfo packet = new ExChangeClientEffectInfo(0, 0, active ? 2 : 0);
		
		for (Player player : _visualZone.getPlayersInside())
		{
			if ((player != null) && player.isOnline())
			{
				player.broadcastPacket(packet);
			}
		}
	}
	
	@Override
	public void onEnterZone(Creature character, ZoneType zone)
	{
		if (character.isPlayer())
		{
			if ((_visualZone != null) && (zone.getId() == _visualZone.getId()))
			{
				boolean isActive = GlobalVariablesManager.getInstance().getBoolean(ATRUS_VISUAL_VAR, false);
				if (isActive)
				{
					character.sendPacket(new ExChangeClientEffectInfo(0, 0, 2));
				}
				else
				{
					character.sendPacket(new ExChangeClientEffectInfo(0, 0, 0));
				}
			}
		}
		super.onEnterZone(character, zone);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals("WATCH_VISUAL_STATUS"))
		{
			if (_atrus != null)
			{
				if (!_atrus.isSpawned())
				{
					performCleanup();
				}
			}
			else
			{
				cancelQuestTimers("WATCH_VISUAL_STATUS");
			}
			startQuestTimer("WATCH_VISUAL_STATUS", 1000, null, null);
		}
		else if (event.equals("CHECK_BOUNDARIES"))
		{
			if ((_atrus != null) && !_atrus.isDead() && _atrus.isSpawned() && !_isMechanicActive)
			{
				boolean outOfZone = (_combatZone != null) && !_combatZone.isInsideZone(_atrus);
				boolean tooFar = _atrus.calculateDistance2D(SPAWN_LOC) > 1450;
				
				if (_isReturning)
				{
					if (tooFar && ((System.currentTimeMillis() - _lastReturnAttempt) > 3000))
					{
						startSafetyReturn();
					}
				}
				else if (outOfZone || tooFar)
				{
					startSafetyReturn();
				}
			}
			startQuestTimer("CHECK_BOUNDARIES", 1000, null, null);
		}
		else if (event.equals("AUTO_SKILL_LOOP"))
		{
			if ((_atrus != null) && !_atrus.isDead() && !_isReturning && _combatStarted)
			{
				if (_atrus.isAttackingNow())
				{
					_atrus.abortAttack();
				}
				
				if (!_isMechanicActive && !_atrus.isCastingNow() && (_atrus.getAI().getIntention() != Intention.CAST))
				{
					manageSkills(_atrus);
				}
				
				startQuestTimer("AUTO_SKILL_LOOP", 100, _atrus, null);
			}
		}
		
		// Atrus rotation at specific HP (90%, 70%, 60%)
		else if (event.equals("START_ROTATION_MECHANIC"))
		{
			if ((_atrus != null) && !_atrus.isDead())
			{
				_isMechanicActive = true;
				
				if (_combatZone != null)
				{
					_combatZone.broadcastPacket(new ExShowScreenMessage(NpcStringId.DUE_TO_THE_CHAOTIC_SEAL_S_EFFECT_ATRUS_IS_UNLEASHING_THE_CHAOS_POWER, ExShowScreenMessage.TOP_CENTER, 7000, true));
				}
				
				_atrus.getAI().setIntention(Intention.IDLE);
				_atrus.abortAttack();
				_atrus.abortCast();
				_atrus.stopMove(null);
				_atrus.setTarget(null);
				
				if (_atrus.isAttackable())
				{
					_atrus.asAttackable().getAggroList().clear();
					_atrus.asAttackable().clearAggroList();
				}
				
				_atrus.setTargetable(false);
				_atrus.setInvul(true);
				
				if (_combatZone != null)
				{
					for (Player p : _combatZone.getPlayersInside())
					{
						if ((p != null) && (p.getTarget() == _atrus))
						{
							p.setTarget(null);
							p.sendPacket(new TargetUnselected(_atrus));
						}
					}
				}
				
				_atrus.doCast(KASHA_VISUAL_JUMP.getSkill());
				long jumpTime = KASHA_VISUAL_JUMP.getSkill().getHitTime() + 500;
				startQuestTimer("EXECUTE_PULL_TO_CENTER", jumpTime, _atrus, null);
			}
		}
		else if (event.equals("EXECUTE_PULL_TO_CENTER"))
		{
			if ((_atrus != null) && !_atrus.isDead())
			{
				_atrus.broadcastPacket(new FlyToLocation(_atrus, SPAWN_LOC, FlyType.CHARGE));
				_atrus.setXYZ(SPAWN_LOC.getX(), SPAWN_LOC.getY(), SPAWN_LOC.getZ());
				_atrus.broadcastPacket(new ValidateLocation(_atrus));
				
				startQuestTimer("SPAWN_POWER_SEAL_1", 2000, _atrus, null);
			}
		}
		
		else if (event.equals("ATTACK_POWER_SEAL"))
		{
			if ((npc != null) && !npc.isDead() && (_atrus != null) && !_atrus.isDead())
			{
				_atrus.setTarget(npc);
				_atrus.getAI().setIntention(Intention.CAST, KASHA_FRAY.getSkill(), npc);
			}
		}
		
		else if (event.equals("SPAWN_POWER_SEAL_1"))
		{
			spawnAndAttackPowerSeal(POWER_SEAL_LOC_1, "SPAWN_POWER_SEAL_2");
		}
		else if (event.equals("SPAWN_POWER_SEAL_2"))
		{
			spawnAndAttackPowerSeal(POWER_SEAL_LOC_2, "SPAWN_POWER_SEAL_3");
		}
		else if (event.equals("SPAWN_POWER_SEAL_3"))
		{
			spawnAndAttackPowerSeal(POWER_SEAL_LOC_3, "SPAWN_POWER_SEAL_4");
		}
		else if (event.equals("SPAWN_POWER_SEAL_4"))
		{
			spawnAndAttackPowerSeal(POWER_SEAL_LOC_4, "FINISH_ROTATION_MECHANIC");
		}
		
		else if (event.equals("FINISH_ROTATION_MECHANIC"))
		{
			if (_atrus != null)
			{
				despawnPowerSeals();
				_isMechanicActive = false;
				_atrus.setInvul(false);
				_atrus.setTargetable(true);
				_atrus.getAI().setIntention(Intention.ACTIVE);
			}
		}
		
		else if (event.equals("FINISH_SAFETY_RETURN"))
		{
			if ((_atrus != null) && !_atrus.isDead())
			{
				_isReturning = false;
				_atrus.setTargetable(true);
				
				if (_isMechanicActive)
				{
					_atrus.setInvul(true);
					startQuestTimer("SPAWN_SEALS_NOW", 500, _atrus, null);
				}
				else
				{
					_atrus.setInvul(false);
					_atrus.getAI().setIntention(Intention.ACTIVE);
				}
			}
		}
		
		else if (event.equals("START_SEAL_EVENT"))
		{
			startSealEvent();
		}
		else if (event.equals("SPAWN_SEALS_NOW"))
		{
			if (_atrus != null)
			{
				_atrus.setInvul(true);
				_atrus.setImmobilized(true);
				spawnSeals();
			}
		}
		
		else if (event.equals("SEAL_CAST_ACTION"))
		{
			if (npc != null)
			{
				if (_atrus != null)
				{
					npc.setTarget(_atrus);
					npc.doCast(CHAOS_CURE.getSkill());
					startQuestTimer("SEAL_CAST_ACTION", 1500, npc, null);
				}
				else
				{
					npc.deleteMe();
				}
			}
		}
		
		else if (event.equals("CHECK_EVENT_RESULT"))
		{
			finishSealEvent(false);
		}
		
		return super.onEvent(event, npc, player);
	}
	
	private void manageSkills(Npc npc)
	{
		if ((npc == null) || npc.isDead() || _isReturning || _isMechanicActive)
		{
			return;
		}
		if (npc.isCastingNow())
		{
			return;
		}
		if (!npc.isAttackable())
		{
			return;
		}
		
		if (_phase1PermanentlyDisabled || (npc.getCurrentHpPercent() <= 50))
		{
			managePhase2Routine(npc);
			return;
		}
		
		if ((npc.getCurrentHpPercent() > 50) && !_phase1PermanentlyDisabled)
		{
			managePhase1Routine(npc);
			return;
		}
	}
	
	private void managePhase2Routine(Npc npc)
	{
		Attackable monster = npc.asAttackable();
		Creature target = getValidTarget(monster);
		
		if (target == null)
		{
			npc.getAI().setIntention(Intention.ACTIVE);
			return;
		}
		
		npc.setTarget(target);
		double dist = npc.calculateDistance3D(target);
		long now = System.currentTimeMillis();
		
		boolean canLaunchStorm = now > _nextStormAvailable;
		boolean isWarmingUp = (_outerStormWarmup > 0) || (_innerStormWarmup > 0);
		
		if (canLaunchStorm || isWarmingUp)
		{
			if ((_outerStormWarmup == 0) && (_innerStormWarmup == 0) && canLaunchStorm && SkillCaster.checkUseConditions(npc, KASHA_OUTER_STORM.getSkill()))
			{
				if (_combatZone != null)
				{
					_combatZone.broadcastPacket(new ExShowScreenMessage(NpcStringId.ATRUS_IS_EMANATING_THE_CHAOS_POWER, ExShowScreenMessage.TOP_CENTER, 5000, true));
				}
				_outerStormWarmup = now + 5000;
				_nextStormAvailable = now + 20000;
			}
			else if ((_outerStormWarmup > 0) && (now >= _outerStormWarmup))
			{
				npc.doCast(KASHA_OUTER_STORM.getSkill());
				_outerStormWarmup = 0;
				return;
			}
			else if ((_innerStormWarmup == 0) && (_outerStormWarmup == 0) && canLaunchStorm && (dist < 600) && SkillCaster.checkUseConditions(npc, KASHA_INNER_STORM.getSkill()))
			{
				if (_combatZone != null)
				{
					_combatZone.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_CHAOS_POWER_IS_GATHERING_AROUND_ATRUS, ExShowScreenMessage.TOP_CENTER, 5000, true));
				}
				_innerStormWarmup = now + 5000;
				_nextStormAvailable = now + 20000;
			}
			else if ((_innerStormWarmup > 0) && (now >= _innerStormWarmup))
			{
				npc.doCast(KASHA_INNER_STORM.getSkill());
				_innerStormWarmup = 0;
				return;
			}
		}
		
		if (SkillCaster.checkUseConditions(npc, KASHA_DESTRUCTION.getSkill()))
		{
			npc.doCast(KASHA_DESTRUCTION.getSkill());
			return;
		}
	}
	
	private void managePhase1Routine(Npc npc)
	{
		Attackable monster = npc.asAttackable();
		Creature target = getValidTarget(monster);
		
		if (target == null)
		{
			npc.getAI().setIntention(Intention.ACTIVE);
			return;
		}
		
		npc.setTarget(target);
		SkillHolder skillToCast = null;
		
		switch (_comboStep)
		{
			case 0:
				skillToCast = KASHA_WAVE;
				break;
			case 1:
				skillToCast = KASHA_WAVE;
				break;
			case 2:
				skillToCast = KASHA_PULL;
				break;
			case 3:
				skillToCast = KASHA_SWEEP;
				break;
			case 4:
				skillToCast = KASHA_BASIC_ATTACK;
				break;
			case 5:
				skillToCast = KASHA_BASIC_ATTACK;
				break;
			case 6:
				skillToCast = KASHA_BASIC_ATTACK;
				break;
			default:
				skillToCast = KASHA_WAVE;
				_comboStep = 0;
				break;
		}
		
		if ((skillToCast != null) && SkillCaster.checkUseConditions(npc, skillToCast.getSkill()))
		{
			npc.doCast(skillToCast.getSkill());
			_comboStep++;
			if (_comboStep > 6)
			{
				_comboStep = 0;
			}
		}
	}
	
	private Creature getValidTarget(Attackable monster)
	{
		Creature target = null;
		if (Rnd.get(100) < 30)
		{
			List<Player> validPlayers = new ArrayList<>();
			if (_combatZone != null)
			{
				for (Player p : _combatZone.getPlayersInside())
				{
					if ((p != null) && !p.isDead() && !p.isInvul() && p.isSpawned())
					{
						validPlayers.add(p);
					}
				}
			}
			if (!validPlayers.isEmpty())
			{
				target = validPlayers.get(Rnd.get(validPlayers.size()));
			}
		}
		
		if (target == null)
		{
			target = monster.getMostHated();
		}
		
		if ((target != null) && target.isSummon())
		{
			Player owner = target.asSummon().getOwner();
			if ((owner != null) && (_combatZone != null) && _combatZone.isInsideZone(owner))
			{
				target = owner;
			}
		}
		
		if ((target != null) && (target.isDead() || ((_combatZone != null) && !_combatZone.isInsideZone(target))))
		{
			monster.getAggroList().remove(target);
			return null;
		}
		return target;
	}
	
	private void spawnAndAttackPowerSeal(Location loc, String nextEvent)
	{
		if ((_atrus == null) || _atrus.isDead())
		{
			return;
		}
		
		Npc seal = addSpawn(POWER_SEAL, loc, false, 0);
		_powerSeals.add(seal);
		
		_atrus.getAI().setIntention(Intention.IDLE);
		_atrus.abortAttack();
		_atrus.abortCast();
		_atrus.stopMove(null);
		
		int heading = _atrus.calculateHeadingTo(seal);
		_atrus.setHeading(heading);
		_atrus.broadcastPacket(new org.l2jmobius.gameserver.network.serverpackets.StopMove(_atrus));
		_atrus.broadcastPacket(new ValidateLocation(_atrus));
		
		Location jumpLoc = new Location(SPAWN_LOC.getX(), SPAWN_LOC.getY(), SPAWN_LOC.getZ() + 5);
		_atrus.broadcastPacket(new FlyToLocation(_atrus, jumpLoc, FlyType.CHARGE));
		
		_atrus.setTarget(seal);
		startQuestTimer("ATTACK_POWER_SEAL", 800, seal, null);
		startQuestTimer(nextEvent, 4000, _atrus, null);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		if (npc.getId() == ATRUS)
		{
			if (!_combatStarted)
			{
				_combatStarted = true;
				startQuestTimer("AUTO_SKILL_LOOP", 1000, npc, null);
			}
			
			if (_isReturning)
			{
				npc.abortAttack();
				npc.abortCast();
				npc.setTarget(null);
				return;
			}
			
			if ((attacker != null) && (_combatZone != null) && !_combatZone.isInsideZone(attacker))
			{
				attacker.teleToLocation(SPAWN_LOC);
				if (npc.isAttackable())
				{
					npc.asAttackable().getAggroList().remove(attacker);
				}
				npc.abortAttack();
				npc.stopMove(null);
				return;
			}
			
			if (_isMechanicActive)
			{
				return;
			}
			
			double hpPercent = npc.getCurrentHpPercent();
			
			if ((hpPercent <= 50) && !_phase1PermanentlyDisabled)
			{
				_phase1PermanentlyDisabled = true;
				
				if (!_hp50Triggered)
				{
					_hp50Triggered = true;
					
					npc.setRHandId(WEAPON_POLEARM_ID);
					
					if (_combatZone != null)
					{
						_combatZone.broadcastPacket(new ExShowScreenMessage(NpcStringId.ATRUS_IS_CHANGING_HIS_WEAPON_AND_ATTACK_STYLE, ExShowScreenMessage.TOP_CENTER, 7000, true));
					}
				}
			}
			
			// Trigger 90%
			if ((hpPercent <= 90) && (hpPercent > 68) && !_hp90Triggered)
			{
				_hp90Triggered = true;
				startQuestTimer("START_ROTATION_MECHANIC", 100, npc, null);
				return;
			}
			
			// Trigger 70%
			if ((hpPercent <= 70) && (hpPercent > 62) && !_hp70Triggered)
			{
				_hp70Triggered = true;
				startQuestTimer("START_ROTATION_MECHANIC", 100, npc, null);
				return;
			}
			
			// Trigger 60%
			if ((hpPercent <= 60) && (hpPercent > 53) && !_hp60Triggered)
			{
				_hp60Triggered = true;
				startQuestTimer("START_ROTATION_MECHANIC", 100, npc, null);
				return;
			}
			
			// Trigger 40%
			if ((hpPercent <= 40) && !_hp40Triggered)
			{
				_hp40Triggered = true;
				startSealEvent();
				return;
			}
		}
	}
	
	private void startSafetyReturn()
	{
		_lastReturnAttempt = System.currentTimeMillis();
		_isReturning = true;
		_combatStarted = false;
		
		if (_atrus.isAttackable())
		{
			_atrus.asAttackable().getAggroList().clear();
			_atrus.asAttackable().clearAggroList();
		}
		
		_atrus.getAI().setIntention(Intention.IDLE);
		_atrus.abortAttack();
		_atrus.abortCast();
		_atrus.stopMove(null);
		_atrus.setTarget(null);
		
		_atrus.setTargetable(false);
		_atrus.setInvul(true);
		
		if (_combatZone != null)
		{
			for (Player p : _combatZone.getPlayersInside())
			{
				if ((p != null) && (p.getTarget() == _atrus))
				{
					p.setTarget(null);
					p.sendPacket(new TargetUnselected(_atrus));
				}
			}
		}
		
		_atrus.broadcastPacket(new FlyToLocation(_atrus, SPAWN_LOC, FlyType.CHARGE));
		_atrus.setXYZ(SPAWN_LOC.getX(), SPAWN_LOC.getY(), SPAWN_LOC.getZ());
		_atrus.broadcastPacket(new ValidateLocation(_atrus));
		
		startQuestTimer("FINISH_SAFETY_RETURN", 2000, _atrus, null);
	}
	
	private void startSealEvent()
	{
		if ((_atrus == null) || _atrus.isDead())
		{
			return;
		}
		_isMechanicActive = true;
		
		if (_combatZone != null)
		{
			_combatZone.broadcastPacket(new ExShowScreenMessage(NpcStringId.ATRUS_IS_ABSORBING_THE_CHAOS_POWER_HE_SUMMONS_THE_CHAOTIC_SEAL_THAT_PROTECTS_HIM, ExShowScreenMessage.TOP_CENTER, 7000, true));
		}
		
		if (_atrus.calculateDistance2D(SPAWN_LOC) > 200)
		{
			_isReturning = true;
			startSafetyReturn();
		}
		else
		{
			startQuestTimer("SPAWN_SEALS_NOW", 100, _atrus, null);
		}
	}
	
	private void spawnSeals()
	{
		_isReturning = false;
		if (!_activeSeals.isEmpty())
		{
			despawnSeals();
		}
		
		_activeSeals.add(addSpawn(CHAOS_SEAL, SEAL_LOC_1, false, 0));
		_activeSeals.add(addSpawn(CHAOS_SEAL, SEAL_LOC_2, false, 0));
		
		startQuestTimer("CHECK_EVENT_RESULT", 10000, _atrus, null);
	}
	
	private void finishSealEvent(boolean success)
	{
		cancelQuestTimer("CHECK_EVENT_RESULT", _atrus, null);
		
		if (_combatZone != null)
		{
			if (success)
			{
				_combatZone.broadcastPacket(new ExShowScreenMessage(NpcStringId.YOU_HAVE_DESTROYED_THE_SEAL_AND_STOPPED_ATRUS_FROM_ABSORBING_THE_CHAOS_POWER, ExShowScreenMessage.TOP_CENTER, 5000, true));
			}
			else
			{
				_combatZone.broadcastPacket(new ExShowScreenMessage(NpcStringId.YOU_HAVE_FAILED_TO_DESTROY_THE_CHAOTIC_SEAL_AND_IT_HAS_RECOVERED_ATRUS_HEALTH, ExShowScreenMessage.TOP_CENTER, 5000, true));
			}
		}
		
		if (!success)
		{
			if (_combatZone != null)
			{
				for (Creature c : _combatZone.getCharactersInside())
				{
					if ((c != null) && !c.isDead() && (c.isPlayer() || c.isSummon()))
					{
						c.doDie(_atrus);
					}
				}
			}
			
			if ((_atrus != null) && !_atrus.isDead())
			{
				double currentHp = _atrus.getCurrentHp();
				double maxHp = _atrus.getMaxHp();
				if (((currentHp / maxHp) * 100) <= 5)
				{
					_atrus.setCurrentHp(maxHp * 0.20);
				}
				else
				{
					_atrus.setCurrentHp(Math.min(maxHp, currentHp + (maxHp * 0.05)));
				}
			}
		}
		
		despawnSeals();
		
		if (_atrus != null)
		{
			_atrus.setInvul(false);
			_atrus.setImmobilized(false);
			_isMechanicActive = false;
			startQuestTimer("START_SEAL_EVENT", 120000, _atrus, null);
		}
	}
	
	private void despawnSeals()
	{
		for (Npc seal : _activeSeals)
		{
			if (seal != null)
			{
				cancelQuestTimer("SEAL_CAST_ACTION", seal, null);
				seal.deleteMe();
			}
		}
		_activeSeals.clear();
	}
	
	private void despawnPowerSeals()
	{
		for (Npc seal : _powerSeals)
		{
			if (seal != null)
			{
				seal.deleteMe();
			}
		}
		_powerSeals.clear();
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.getId() == ATRUS)
		{
			performCleanup();
		}
		else if (npc.getId() == CHAOS_SEAL)
		{
			_activeSeals.remove(npc);
			if (_activeSeals.isEmpty() && _isMechanicActive)
			{
				finishSealEvent(true);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new AtrusBoss();
	}
}