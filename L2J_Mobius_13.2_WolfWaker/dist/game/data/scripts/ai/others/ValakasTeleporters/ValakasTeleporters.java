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
package ai.others.ValakasTeleporters;

import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.managers.ScriptManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.groups.CommandChannel;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.bosses.Valakas.Valakas;

/**
 * Valakas Teleporters AI<br>
 * - Boromir: Direct Teleport to Hall.<br>
 * - Heart: CC Entry Only with strict checks.
 * @author Notorion
 */
public class ValakasTeleporters extends Script
{
	// NPCs
	private static final int BOROMIR = 34745;
	private static final int HEART_OF_VOLCANO = 31385;
	private static final int TELEPORT_CUBIC = 31759;
	private static final int VALAKAS_ID = 29415;
	
	// Locations
	private static final Location ENTER_HALL_OF_FLAMES = new Location(190400, -107501, -1016);
	private static final Location TELEPORT_INTO_VALAKAS_LAIR = new Location(210604, -114980, -1662);
	private static final Location TELEPORT_OUT_OF_VALAKAS_LAIR = new Location(150037, -57720, -2976);
	
	// Configs
	private static final int MIN_CC_MEMBERS = 49; // 49 players
	private static final int MAX_GLOBAL_PLAYERS = 200;
	private static final int CHECK_RADIUS = 2500;
	private static final int MIN_LEVEL = 120;
	private static final int VALAKAS_WAIT_TIME = 10; // 20min
	private static final int VALAKAS_ZONE_ID = 12010;
	
	private static final Object ENTRY_LOCK = new Object();
	
	private ValakasTeleporters()
	{
		addStartNpc(BOROMIR, HEART_OF_VOLCANO, TELEPORT_CUBIC);
		addTalkId(BOROMIR, HEART_OF_VOLCANO, TELEPORT_CUBIC);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals("teleport_hall"))
		{
			player.teleToLocation(ENTER_HALL_OF_FLAMES);
			return null;
		}
		else if (event.equals("teleport_lair"))
		{
			synchronized (ENTRY_LOCK)
			{
				// 1. Boss Status Check.
				final int status = GrandBossManager.getInstance().getStatus(VALAKAS_ID);
				if (status == 2) // In battle
				{
					return "31385-02.htm";
				}
				
				if (status >= 3) // Dead / Cooldown
				{
					return "31385-04.htm";
				}
				
				// 2. Command Channel Check.
				final CommandChannel cc = player.getCommandChannel();
				if ((cc == null) || (cc.getMemberCount() < MIN_CC_MEMBERS))
				{
					playSound(player, "ItemSound3.sys_impossible");
					return "31385-01.htm";
				}
				
				// Only the CC leader can request entry.
				if (!cc.getLeader().equals(player))
				{
					playSound(player, "ItemSound3.sys_impossible");
					player.sendMessage("Only the Command Channel Leader can attempt entry.");
					return null;
				}
				
				// 3. Dynamic Player Count (Fix ZoneManager).
				int playersInside = 0;
				final ZoneType zone = ZoneManager.getInstance().getZoneById(VALAKAS_ZONE_ID);
				if (zone != null)
				{
					playersInside = zone.getPlayersInside().size();
				}
				
				// Check global limit of 200.
				if ((playersInside + cc.getMemberCount()) > MAX_GLOBAL_PLAYERS)
				{
					playSound(player, "ItemSound3.sys_impossible");
					return "31385-03.htm";
				}
				
				// 4. Verification.
				for (Player member : cc.getMembers())
				{
					if ((member == null) || !member.isOnline())
					{
						continue;
					}
					
					if (!member.isInsideRadius3D(npc, CHECK_RADIUS) || (member.getLevel() < MIN_LEVEL))
					{
						playSound(player, "ItemSound3.sys_impossible");
						player.sendMessage("Entry failed. The member " + member.getName() + " is too far away or below level " + MIN_LEVEL + ".");
						return null;
					}
				}
				
				if (cc.getMemberCount() < MIN_CC_MEMBERS)
				{
					playSound(player, "ItemSound3.sys_impossible");
					return "31385-01.htm";
				}
				
				// 5. Start Spawn if first entry.
				if (status == 0)
				{
					GrandBossManager.getInstance().setStatus(VALAKAS_ID, 1);
					
					final ExShowScreenMessage screenMsg = new ExShowScreenMessage(NpcStringId.WHO_DARES_CHALLENGE_VALAKAS_FOOLISH_FANCIES_I_M_UNBEATABLE, ExShowScreenMessage.TOP_CENTER, 7000);
					npc.broadcastPacket(screenMsg);
					
					for (Player member : cc.getMembers())
					{
						if (member != null)
						{
							member.sendPacket(screenMsg);
						}
					}
					
					final Script valakasAi = (Script) ScriptManager.getInstance().getScript(Valakas.class.getSimpleName());
					if (valakasAi != null)
					{
						final GrandBoss valakasBoss = GrandBossManager.getInstance().getBoss(VALAKAS_ID);
						valakasAi.startQuestTimer("beginning", VALAKAS_WAIT_TIME * 60000, valakasBoss, null);
					}
				}
				
				// 6. Teleport CC members.
				for (Player member : cc.getMembers())
				{
					if ((member != null) && member.isOnline())
					{
						member.teleToLocation(TELEPORT_INTO_VALAKAS_LAIR, true);
					}
				}
			}
			return null;
		}
		
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		switch (npc.getId())
		{
			case BOROMIR:
			{
				return "34745.htm";
			}
			case HEART_OF_VOLCANO:
			{
				final int status = GrandBossManager.getInstance().getStatus(VALAKAS_ID);
				if ((status == 0) || (status == 1))
				{
					return "31385.htm";
				}
				else if (status == 2)
				{
					return "31385-02.htm";
				}
				else
				{
					return "31385-04.htm";
				}
			}
			case TELEPORT_CUBIC:
			{
				player.teleToLocation(TELEPORT_OUT_OF_VALAKAS_LAIR, true);
				return null;
			}
		}
		
		return super.onTalk(npc, player);
	}
	
	public static void main(String[] args)
	{
		new ValakasTeleporters();
	}
}
