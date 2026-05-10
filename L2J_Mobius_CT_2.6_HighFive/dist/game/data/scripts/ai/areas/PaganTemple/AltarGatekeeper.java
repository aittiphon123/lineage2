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
package ai.areas.PaganTemple;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;

/**
 * Altar Gatekeeper retail door-state announcer for Pagan Temple altar 3rd floor (32051).<br>
 * Four instances spawn simultaneously when the altar doors open; only the first one
 * shouts the retail NCSoft message to keep the burst from quadrupling the chat.
 * @author Altur
 */
public class AltarGatekeeper extends Script
{
	private static final int ALTAR_GATEKEEPER = 32051;
	private static final long SHOUT_COOLDOWN = 30_000L;

	private static volatile long _lastShoutAt;

	private AltarGatekeeper()
	{
		addSpawnId(ALTAR_GATEKEEPER);
	}

	@Override
	public void onSpawn(Npc npc)
	{
		final long now = System.currentTimeMillis();
		if ((now - _lastShoutAt) < SHOUT_COOLDOWN)
		{
			return;
		}
		_lastShoutAt = now;
		npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_DOOR_TO_THE_3RD_FLOOR_OF_THE_ALTAR_IS_NOW_OPEN);
	}

	public static void main(String[] args)
	{
		new AltarGatekeeper();
	}
}
