/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.ai;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;

/**
 * Validates retail PTS Skill0X gates declared on the NPC parameters.
 * @author Altur
 */
public final class ParameterizedSkillGates
{
	private static final int PROBABILITY_SCALE = 10000;
	private static final int MAX_SKILL_SLOTS = 6;
	
	private ParameterizedSkillGates()
	{
	}
	
	/**
	 * @param caster the NPC that wants to cast
	 * @param skill the skill being evaluated
	 * @param target the current attack target (may be null)
	 * @return {@code true} if the skill is allowed to cast under retail conditions
	 */
	public static boolean allowsCast(Attackable caster, Skill skill, Creature target)
	{
		if ((caster == null) || (skill == null))
		{
			return true;
		}
		
		final StatSet params = caster.getTemplate() != null ? caster.getTemplate().getParameters() : null;
		if ((params == null) || params.isEmpty())
		{
			return true;
		}
		
		final int slot = findSlot(params, skill.getId());
		if (slot == 0)
		{
			return true;
		}
		
		return passesGates(slot, params, caster, target);
	}
	
	private static int findSlot(StatSet params, int skillId)
	{
		for (int slot = 1; slot <= MAX_SKILL_SLOTS; slot++)
		{
			final SkillHolder declared = params.getObject("Skill0" + slot + "_ID", SkillHolder.class);
			if ((declared != null) && (declared.getSkillId() == skillId))
			{
				return slot;
			}
		}
		return 0;
	}
	
	private static boolean passesGates(int slot, StatSet params, Attackable caster, Creature target)
	{
		final String prefix = "Skill0" + slot + "_";
		final int probability = params.getInt(prefix + "Probablity", 0);
		final boolean checksDistance = params.getInt(prefix + "Check_Dist", 0) == 1;
		final int highHp = params.getInt(prefix + "HighHP", 0);
		
		// Slots with no probability and no AI hint at all (no distance/HP gate, no AttackSplash,
		// no MainAttack, no Target) are reserved for retail script events.
		if (probability == 0)
		{
			final boolean hasAttackHint = (params.getInt(prefix + "AttackSplash", 0) == 1) || (params.getInt(prefix + "MainAttack", 0) == 1) || (params.getInt(prefix + "Target", 0) > 0);
			if (!checksDistance && (highHp == 0) && !hasAttackHint)
			{
				return false;
			}
		}
		else if (Rnd.get(PROBABILITY_SCALE) >= probability)
		{
			return false;
		}
		
		if (checksDistance && (target != null))
		{
			final int distMin = params.getInt(prefix + "Dist_Min", 0);
			final int distMax = params.getInt(prefix + "Dist_Max", Integer.MAX_VALUE);
			final int distance = (int) caster.calculateDistance2D(target);
			if ((distance < distMin) || (distance > distMax))
			{
				return false;
			}
		}
		
		if (highHp > 0)
		{
			final int hpTarget = params.getInt(prefix + "HPTarget", 0);
			final Creature hpSource = (hpTarget == 1) ? caster : target;
			if (hpSource == null)
			{
				return false;
			}
			final double hpPercent = (hpSource.getCurrentHp() * 100.0) / hpSource.getMaxHp();
			if (hpPercent <= highHp)
			{
				return false;
			}
		}
		
		return true;
	}
}
