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
package ai.areas.KashaOrc;

import java.util.Collection;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * Kasha Orc Elite Shaman AI.<br>
 * Shows strong aggression towards support characters (e.g., healers).
 * @author Notorion
 */
public class SupportHaterShaman extends Script
{
	private static final int SHAMAN_ID = 23901; // Kasha Orc Elite Shaman.
	
	// Behavior configuration.
	private static final int AGGRO_CHANCE = 80; // 80% chance to turn.
	private static final int HATE_AMOUNT = 50000; // Extreme Hatred.
	
	// 1. Vision: How far the Kasha Orc Elite Shaman can see the healer before starting to chase (Far).
	private static final int VISION_RANGE = 1500;
	
	// 2. Limit: How far it can go before resetting.
	private static final int LIMIT_RANGE = 899;
	
	// Leash reaction time.
	private static final int CHECK_INTERVAL = 3000;
	
	public SupportHaterShaman()
	{
		addSpawnId(SHAMAN_ID);
		addAttackId(SHAMAN_ID);
		addSkillSeeId(SHAMAN_ID);
		addKillId(SHAMAN_ID);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		// Records the initial point.
		npc.getVariables().set("HomeX", npc.getX());
		npc.getVariables().set("HomeY", npc.getY());
		npc.getVariables().set("HomeZ", npc.getZ());
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals("check_leash"))
		{
			if ((npc == null) || npc.isDead() || !npc.isInCombat())
			{
				return super.onEvent(event, npc, player);
			}
			
			final int homeX = npc.getVariables().getInt("HomeX", 0);
			final int homeY = npc.getVariables().getInt("HomeY", 0);
			final int homeZ = npc.getVariables().getInt("HomeZ", 0);
			if (homeX == 0)
			{
				return super.onEvent(event, npc, player);
			}
			
			// Distance.
			final double distFromHome = Math.sqrt(Math.pow(npc.getX() - homeX, 2) + Math.pow(npc.getY() - homeY, 2) + Math.pow(npc.getZ() - homeZ, 2));
			if (distFromHome > LIMIT_RANGE) // Moved too far away, RESET.
			{
				npc.abortAttack();
				npc.abortCast();
				npc.setTarget(null);
				npc.asAttackable().clearAggroList();
				npc.teleToLocation(homeX, homeY, homeZ);
			}
			else
			{
				startQuestTimer("check_leash", CHECK_INTERVAL, npc, null);
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		if (getQuestTimer("check_leash", npc, null) == null)
		{
			startQuestTimer("check_leash", CHECK_INTERVAL, npc, null);
		}
		
		super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public void onSkillSee(Npc npc, Player caster, Skill skill, Collection<WorldObject> targets, boolean isSummon)
	{
		// Only works if in combat.
		if (npc.isInCombat() && !npc.isDead())
		{
			boolean targetingMe = false;
			
			// Checks if the skill was a direct attack or nearby allies.
			if (targets != null)
			{
				for (WorldObject target : targets)
				{
					if (target == npc)
					{
						targetingMe = true;
						break;
					}
				}
			}
			
			// If the skill was not on me and the caster is not my current target.
			if (!targetingMe && (caster != npc.getTarget()))
			{
				// 80% chance to switch focus.
				if (Rnd.get(100) < AGGRO_CHANCE)
				{
					// Calculate distance to the caster (e.g., healer).
					final double distToCaster = Math.sqrt(Math.pow(npc.getX() - caster.getX(), 2) + Math.pow(npc.getY() - caster.getY(), 2));
					
					// Only reacts if within vision range.
					if (distToCaster < VISION_RANGE)
					{
						// Generate extreme hatred and switch target to the caster.
						npc.asAttackable().addDamageHate(caster, 0, HATE_AMOUNT);
						npc.setTarget(caster);
						npc.getAI().setIntention(Intention.ATTACK, caster);
					}
				}
			}
		}
		
		super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		cancelQuestTimer("check_leash", npc, null);
		super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new SupportHaterShaman();
	}
}
