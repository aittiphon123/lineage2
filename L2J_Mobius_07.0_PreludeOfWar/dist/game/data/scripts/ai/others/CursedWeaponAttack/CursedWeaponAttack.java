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
package ai.others.CursedWeaponAttack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.MapRegionData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.TeleportWhereType;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.actor.creature.OnCreatureAttack;
import org.l2jmobius.gameserver.model.events.holders.actor.creature.OnCreatureSkillUse;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLogout;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;

/**
 * Handles the mechanics of Cursed Weapon attacks and the Prison of Souls system. <br>
 * CursedWeaponAttack AI -
 * @version Prelude of War 2019
 * @author Notorion
 */
public class CursedWeaponAttack extends Script
{
	// Logger instance for this class
	private static final Logger LOGGER = Logger.getLogger(CursedWeaponAttack.class.getName());
	
	// Skill IDs
	private static final int SKILL_ID_ZARICHE = 35404; // Prison of Souls - Zariche.
	private static final int SKILL_ID_AKAMANAH = 35405; // Prison of Souls - Akamanah.
	
	// Mechanics constants
	private static final int HITS_TO_BREAK = 540; // 540 Hits required to break.
	private static final int DURATION_SECONDS = 120; // 2 Minutes duration.
	
	// Scar skills
	private static final int SKILL_SCAR_ZARICHE = 35521; // Soul Scar - Zariche - 130sec.
	private static final int SKILL_SCAR_AKAMANAH = 35522; // Soul Scar - Akamanah - 130sec.
	
	// Fallback location if teleport fails.
	private static final Location FALLBACK_LOC = new Location(17860, 170170, -3507);
	
	// Active prisoners map (ObjectId -> PrisonData).
	private static final Map<Integer, PrisonData> _prisoners = new ConcurrentHashMap<>();
	
	private static class PrisonData
	{
		final int skillId;
		final AtomicInteger hitCount = new AtomicInteger(0);
		final int victimObjectId;
		volatile ScheduledFuture<?> teleportTask;
		
		PrisonData(int skillId, int objId)
		{
			this.skillId = skillId;
			this.victimObjectId = objId;
		}
		
		synchronized void cancelTask()
		{
			if ((teleportTask != null) && !teleportTask.isDone())
			{
				teleportTask.cancel(false);
				teleportTask = null;
			}
		}
	}
	
	public CursedWeaponAttack()
	{
		LOGGER.info("CursedWeaponAttack initialized: registering global event Cursed Weapons Defense.");
		
		// 1. Skill Use.
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_CREATURE_SKILL_USE, event -> onCreatureSkillUse((OnCreatureSkillUse) event), this));
		
		// 2. Attack.
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_CREATURE_ATTACK, event -> onCreatureAttack((OnCreatureAttack) event), this));
		
		// 3. Player Logout.
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LOGOUT, event -> onPlayerLogout((OnPlayerLogout) event), this));
		
		// Player Login.
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LOGIN, event -> onPlayerLogin((OnPlayerLogin) event), this));
	}
	
	public void onCreatureSkillUse(OnCreatureSkillUse event)
	{
		if (!event.getCaster().isPlayer())
		{
			return;
		}
		
		final Player caster = event.getCaster().asPlayer();
		final Skill skill = event.getSkill();
		final int skillId = skill.getId();
		
		if ((skillId != SKILL_ID_ZARICHE) && (skillId != SKILL_ID_AKAMANAH) && _prisoners.isEmpty())
		{
			return;
		}
		
		Creature target = (Creature) caster.getTarget();
		
		if ((skillId == SKILL_ID_ZARICHE) || (skillId == SKILL_ID_AKAMANAH))
		{
			if ((target == null) || !target.isPlayer())
			{
				return;
			}
			
			final Player victim = target.asPlayer();
			
			// Holder protection block.
			if (victim.isCursedWeaponEquipped())
			{
				ThreadPool.schedule(() ->
				{
					victim.stopSkillEffects(skill);
					
					// Extra visual cleanup for safety.
					victim.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.ZARICHE_PRISION_AVE);
					
					// victim.sendMessage("The prison effect does not work on another cursed weapon holder.");
				}, 500);
				
				caster.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final PrisonData newData = new PrisonData(skillId, victim.getObjectId());
			final PrisonData existing = _prisoners.putIfAbsent(victim.getObjectId(), newData);
			
			if (existing != null)
			{
				caster.sendPacket(SystemMessageId.INVALID_TARGET);
				caster.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			// LOGGER.info("CursedWeaponAttack: " + victim.getName() + " imprisoned. Timer set to 120s.");
			
			newData.teleportTask = ThreadPool.schedule(() -> executePrisonEnd(newData), DURATION_SECONDS * 1000);
			return;
		}
		
		// Magic attack on prisoner.
		if (!_prisoners.isEmpty() && (target != null) && target.isPlayer() && skill.hasNegativeEffect())
		{
			checkHit(target.asPlayer(), caster);
		}
	}
	
	public void onCreatureAttack(OnCreatureAttack event)
	{
		if (_prisoners.isEmpty())
		{
			return;
		}
		
		final Creature target = event.getTarget();
		if ((target == null) || !target.isPlayer())
		{
			return;
		}
		
		checkHit(target.asPlayer(), event.getAttacker());
	}
	
	private void checkHit(Player victim, Creature attacker)
	{
		final PrisonData data = _prisoners.get(victim.getObjectId());
		if (data == null)
		{
			return;
		}
		
		final int hits = data.hitCount.incrementAndGet();
		
		// logger to confirm hits
		// LOGGER.info("CursedWeaponAttack: Hit " + hits + "/" + HITS_TO_BREAK + " in " + victim.getName());
		
		if (hits >= HITS_TO_BREAK)
		{
			liberate(victim, data, attacker);
		}
	}
	
	private void liberate(Player victim, PrisonData data, Creature attacker)
	{
		// LOGGER.info("CursedWeaponAttack: " + victim.getName() + " RELEASED!");
		
		data.cancelTask();
		_prisoners.remove(victim.getObjectId());
		
		final Skill s = SkillData.getInstance().getSkill(data.skillId, 1);
		if (s != null)
		{
			victim.stopSkillEffects(s);
		}
		
		int scarId = 0;
		
		if (data.skillId == SKILL_ID_ZARICHE)
		{
			scarId = SKILL_SCAR_ZARICHE; // 35521
		}
		else if (data.skillId == SKILL_ID_AKAMANAH)
		{
			scarId = SKILL_SCAR_AKAMANAH; // 35522
		}
		
		if (scarId > 0)
		{
			Skill scarSkill = SkillData.getInstance().getSkill(scarId, 1);
			if (scarSkill != null)
			{
				victim.stopSkillEffects(scarSkill);
			}
		}
		
		// victim.sendMessage("The Limit Barrier has been destroyed!");
		// if ((attacker != null) && attacker.isPlayer())
		// {
		// attacker.sendMessage("You helped break the Prison of Souls!");
		// }
	}
	
	private void executePrisonEnd(PrisonData expectedData)
	{
		try
		{
			final PrisonData currentData = _prisoners.get(expectedData.victimObjectId);
			if ((currentData == null) || (currentData != expectedData))
			{
				return;
			}
			
			// The ID remains imprisoned to avoid bugs during loading.
			final Player victim = World.getInstance().getPlayer(expectedData.victimObjectId);
			
			if ((victim == null) || !victim.isOnline())
			{
				return;
			}
			
			// Remove visual effects.
			final Skill s = SkillData.getInstance().getSkill(expectedData.skillId, 1);
			if (s != null)
			{
				victim.stopSkillEffects(s);
			}
			
			int scarId = 0;
			
			if (expectedData.skillId == SKILL_ID_ZARICHE)
			{
				scarId = SKILL_SCAR_ZARICHE;
			}
			
			else if (expectedData.skillId == SKILL_ID_AKAMANAH)
			{
				scarId = SKILL_SCAR_AKAMANAH;
			}
			
			if (scarId > 0)
			{
				Skill scarSkill = SkillData.getInstance().getSkill(scarId, 1);
				if (scarSkill != null)
				{
					victim.stopSkillEffects(scarSkill);
				}
			}
			
			if (!victim.isDead())
			{
				Location loc = MapRegionData.getInstance().getTeleToLocation(victim, TeleportWhereType.TOWN);
				if (loc == null)
				{
					loc = FALLBACK_LOC;
				}
				
				victim.teleToLocation(loc, true);
				// LOGGER.info("CursedWeaponAttack: teleported " + victim.getName());
			}
		}
		catch (Exception e)
		{
			LOGGER.warning("CursedWeaponAttack Error: " + e.getMessage());
		}
		finally
		{
			ThreadPool.schedule(() ->
			{
				_prisoners.remove(expectedData.victimObjectId);
			}, 2000); // Seconds of immunity against new imprisonment.
		}
	}
	
	public void onPlayerLogout(OnPlayerLogout event)
	{
		if (_prisoners.isEmpty())
		{
			return;
		}
		
		final PrisonData data = _prisoners.remove(event.getPlayer().getObjectId());
		if (data != null)
		{
			data.cancelTask();
		}
	}
	
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		// 1. Check if the player has the prison skill.
		final boolean hasZariche = player.getEffectList().isAffectedBySkill(SKILL_ID_ZARICHE);
		final boolean hasAkamanah = player.getEffectList().isAffectedBySkill(SKILL_ID_AKAMANAH);
		
		if (!hasZariche && !hasAkamanah)
		{
			return;
		}
		
		final int skillId = hasZariche ? SKILL_ID_ZARICHE : SKILL_ID_AKAMANAH;
		
		// 2. Read the actual remaining time on the buff.
		long remainingTime = 3000L; // Minimum safety.
		
		final BuffInfo info = player.getEffectList().getBuffInfoBySkillId(skillId);
		if (info != null)
		{
			long val = info.getTime() * 1000L;
			if (val > 0)
			{
				remainingTime = val;
			}
		}
		
		// LOGGER.info("CursedWeaponAttack: Fugitive " + player.getName() + " recaptured. Remaining time: " + (remainingTime / 1000) + "s");
		
		// 3. Reactivate the task in the script.
		final PrisonData newData = new PrisonData(skillId, player.getObjectId());
		_prisoners.put(player.getObjectId(), newData);
		
		newData.teleportTask = ThreadPool.schedule(() -> executePrisonEnd(newData), remainingTime);
	}
	
	public static void main(String[] args)
	{
		new CursedWeaponAttack();
	}
}
