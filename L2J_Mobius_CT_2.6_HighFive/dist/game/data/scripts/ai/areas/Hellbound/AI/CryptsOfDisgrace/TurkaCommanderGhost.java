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

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;

/**
 * Turka Commander's Ghost AI.
 * @author Altur
 */
public class TurkaCommanderGhost extends Script
{
	// NPCs
	private static final int TURKA_COMMANDER_GHOST = 22707;
	// Misc
	private static final String SHOUT_TIMER = "shout";
	
	public TurkaCommanderGhost()
	{
		addSpawnId(TURKA_COMMANDER_GHOST);
		addKillId(TURKA_COMMANDER_GHOST);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.WHO_HAS_AWAKENED_US_FROM_OUR_SLUMBER);
		startQuestTimer(SHOUT_TIMER, 5000, npc, null);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals(SHOUT_TIMER) && (npc != null) && !npc.isDead())
		{
			npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.ALL_WILL_PAY_A_SEVERE_PRICE_TO_ME_AND_THESE_HERE);
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.ALL_IS_VANITY_BUT_THIS_CANNOT_BE_THE_END);
	}
}
