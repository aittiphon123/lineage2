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
package ai.areas.Hellbound.AI.CryptsOfDisgrace;

import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;

/**
 * Legend Orc Buff AI.
 * @author Altur
 */
public class LegendOrcBuff extends Script
{
	// NPCs
	private static final int LEGEND_ORC_BUFF = 18837;
	// Skills
	private static final SkillHolder BLESSED_FOCUS = new SkillHolder(6235, 1);
	
	public LegendOrcBuff()
	{
		addCreatureSeeId(LEGEND_ORC_BUFF);
	}
	
	@Override
	public void onCreatureSee(Npc npc, Creature creature)
	{
		if (creature.isPlayer())
		{
			BLESSED_FOCUS.getSkill().applyEffects(npc, creature);
		}
	}
	
	public static void main(String[] args)
	{
		new LegendOrcBuff();
	}
}
