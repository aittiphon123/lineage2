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
package ai.areas.PaganTemple.PaganTeleporters;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;

/**
 * Pagan Temple teleport AI
 * @version Prelude of War Part 2 - 27 September 2019
 * @author Notorion
 */
public class PaganTeleporters extends Script
{
	// NPCs
	private static final int TRIOLS_MIRROR_1 = 32039;
	private static final int TRIOLS_MIRROR_2 = 32040;
	
	// Locations
	private static final Map<Integer, Location> TRIOLS_LOCS = new HashMap<>();
	static
	{
		TRIOLS_LOCS.put(TRIOLS_MIRROR_1, new Location(-12766, -35840, -10856));
		TRIOLS_LOCS.put(TRIOLS_MIRROR_2, new Location(36640, -51218, 718));
	}
	
	private static final int[] NPCS =
	{
		32034,
		32035,
		32036,
		32037,
		32039,
		32040
	};
	
	private PaganTeleporters()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
		addFirstTalkId(TRIOLS_MIRROR_1, TRIOLS_MIRROR_2);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "CLOSE_DOOR_1":
			{
				closeDoor(19160001, 0);
				break;
			}
			case "CLOSE_DOOR_2":
			{
				closeDoor(19160010, 0);
				closeDoor(19160011, 0);
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (TRIOLS_LOCS.containsKey(npc.getId()))
		{
			player.teleToLocation(TRIOLS_LOCS.get(npc.getId()));
		}
		
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		switch (npc.getId())
		{
			case 32034:
			{
				openDoor(19160001, 0);
				startQuestTimer("CLOSE_DOOR_1", 10000, null, null);
				return "openDoor.htm";
			}
			case 32035:
			{
				openDoor(19160001, 0);
				startQuestTimer("CLOSE_DOOR_1", 10000, null, null);
				return "FadedMark.htm";
			}
			case 32036:
			{
				openDoor(19160010, 0);
				openDoor(19160011, 0);
				startQuestTimer("CLOSE_DOOR_2", 10000, null, null);
				return "openDoor2.htm";
			}
			case 32037:
			{
				openDoor(19160010, 0);
				openDoor(19160011, 0);
				startQuestTimer("CLOSE_DOOR_2", 10000, null, null);
				return "FadedMark2.htm";
			}
		}
		return super.onTalk(npc, player);
	}
	
	public static void main(String[] args)
	{
		new PaganTeleporters();
	}
}
