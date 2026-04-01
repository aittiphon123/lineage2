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
package instances.ContinuedRaidBattle;

import java.util.List;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.TeleportWhereType;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;

/**
 * @URL https://l2wiki.com/essence/events_and_promos/2040.html
 * @author Serenitty
 */
public class ContinuedRaidBattle extends InstanceScript
{
	// Items
	private static final int TRIAL_BAMIUM = 97784;
	private static final int TRIAL_BEHEMOTH = 97785;
	private static final int TRIAL_GLAKIAS = 97786;
	
	// NPCs
	private static final int BEIRON = 34195;
	private static final int BAIUM = 18913;
	private static final int IMMORTAL_BAIUM = 18914;
	private static final int BEHEMOTH = 18916;
	private static final int ULTIMATE_BEHEMOTH = 18917;
	private static final int GLAKIAS = 18827;
	private static final int GLAKIAS_DREADFUL = 18838;
	private static final int ZAKEN = 18909;
	private static final int QEEN_ANT = 18910;
	private static final int ORFEN = 18911;
	private static final int CORE = 18912;
	
	// Locations
	private static final Location TO_QUEEN_ANT_LOCATION = new Location(-21612, 183106, -5724);
	private static final Location TO_ORFEN_LOCATION = new Location(45217, 17127, -4398);
	private static final Location TO_CORE_LOCATION = new Location(17721, 110395, -6657);
	private static final Location TO_BAIUM_LOCATION = new Location(115432, 16396, 10077);
	private static final Location TO_BEHEMOTH_LOCATION = new Location(153698, 121570, -3809);
	private static final Location TO_GLAKIAS_LOCATION = new Location(114715, -114123, -11207);
	
	// Zones
	private static final ZoneType ZAKEN_PORTAL_ENTER_ID = ZoneManager.getInstance().getZoneByName("zaken_portal_enter");
	private static final ZoneType QEEN_ANT_PORTAL_ENTER_ID = ZoneManager.getInstance().getZoneByName("queen_ant_portal_enter");
	private static final ZoneType ORFEN_PORTAL_ENTER_ID = ZoneManager.getInstance().getZoneByName("orfen_portal_enter");
	private static final ZoneType CORE_PORTAL_ENTER_ID = ZoneManager.getInstance().getZoneByName("core_portal_enter");
	
	// Misc
	private static final long BOSS_COMBAT_LIMIT = 1 * 600_000; // 10 mins
	private static final int EVENT_PORTALCOLOR = 15987;
	private static final int TEMPLATE_ID = 2000;
	
	private ContinuedRaidBattle()
	{
		super(TEMPLATE_ID);
		
		addInstanceCreatedId(TEMPLATE_ID);
		addInstanceEnterId(TEMPLATE_ID);
		addInstanceLeaveId(TEMPLATE_ID);
		addInstanceDestroyId(TEMPLATE_ID);
		addSpawnId(EVENT_PORTALCOLOR, ZAKEN);
		addCreatureSeeId(EVENT_PORTALCOLOR);
		addKillId(ZAKEN, QEEN_ANT, ORFEN, CORE);
		addKillId(BAIUM, IMMORTAL_BAIUM, BEHEMOTH, GLAKIAS, GLAKIAS_DREADFUL);
		addEnterZoneId(ZAKEN_PORTAL_ENTER_ID.getId());
		addEnterZoneId(QEEN_ANT_PORTAL_ENTER_ID.getId());
		addEnterZoneId(ORFEN_PORTAL_ENTER_ID.getId());
		addEnterZoneId(CORE_PORTAL_ENTER_ID.getId());
		
		addTalkId(BEIRON);
		addFirstTalkId(BEIRON);
		
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		
		switch (event)
		{
			case "ENTER":
			{
				if (player.isInParty())
				{
					final Party party = player.getParty();
					final boolean isInCC = party.isInCommandChannel();
					final List<Player> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
					for (Player member : members)
					{
						if (!member.isInsideRadius3D(npc, 1000))
						{
							player.sendMessage("Player " + member.getName() + " must go closer to Parme.");
						}
						
						enterInstance(member, npc, TEMPLATE_ID);
					}
				}
				else if (player.isGM())
				{
					enterInstance(player, npc, TEMPLATE_ID);
					player.sendMessage("SYS: You have entered as GM/Admin to Continued Raid.");
				}
				else
				{
					if (!player.isInsideRadius3D(npc, 1000))
					{
						player.sendMessage("You must go closer to Parme.");
					}
					
					enterInstance(player, npc, TEMPLATE_ID);
				}
				break;
			}
			case "FINAL_BOSS":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					final int randomChance = Rnd.get(100);
					if (randomChance < 70)
					{
						addSpawn(GLAKIAS, 114713, -114799, -11209, 16145, false, 0, false, world.getId());
						world.getParameters().set("finalLoc", 2);
					}
					else if (randomChance < 60)
					{
						addSpawn(BEHEMOTH, 154363, 121253, -3809, 16145, false, 0, false, world.getId());
						world.getParameters().set("finalLoc", 3);
					}
					else if (randomChance < 20)
					{
						addSpawn(GLAKIAS_DREADFUL, 114713, -114799, -11209, 16145, false, 0, false, world.getId());
						world.getParameters().set("finalLoc", 2);
					}
					else if (randomChance < 15)
					{
						addSpawn(IMMORTAL_BAIUM, 115740, 17146, 10075, 37604, false, 0, false, world.getId());
						world.getParameters().set("finalLoc", 1);
					}
					else if (randomChance < 10)
					{
						addSpawn(ULTIMATE_BEHEMOTH, 154363, 121253, -3809, 16145, false, 0, false, world.getId());
						world.getParameters().set("finalLoc", 3);
					}
					else
					{
						addSpawn(BAIUM, 115740, 17146, 10075, 37604, false, 0, false, world.getId());
						world.getParameters().set("finalLoc", 1);
					}
					
					world.spawnGroup("Stage4_Core_portal");
				}
				break;
			}
			case "TELEPORT":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					world.removePlayer(player);
					player.teleToLocation(TeleportWhereType.TOWN, null);
				}
				break;
			}
			case "REWARD":
			{
				if (!player.getVariables().getBoolean(PlayerVariables.DAILY_CONTINUED_RAID_GET_REWARD, false))
				{
					final Instance world = player.getInstanceWorld();
					if (world != null)
					{
						final int location = world.getParameters().getInt("finalLoc", 0);
						if (location == 1)
						{
							player.addItem(ItemProcessType.REWARD, TRIAL_BAMIUM, 1, player, true);
						}
						else if (location == 2)
						{
							player.addItem(ItemProcessType.REWARD, TRIAL_GLAKIAS, 1, player, true);
						}
						else
						{
							player.addItem(ItemProcessType.REWARD, TRIAL_BEHEMOTH, 1, player, true);
						}
						
						player.getVariables().set(PlayerVariables.DAILY_CONTINUED_RAID_GET_REWARD, true);
					}
				}
				else
				{
					player.sendMessage("You claimed the item.");
					return null;
				}
				break;
			}
			case "CHECK_TIME":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					int currentStage = world.getParameters().getInt("continued_raid_stage", 0);
					int remainingTime = getRemainingTime(world);
					
					if ((remainingTime <= 600) && (currentStage <= 3))
					{
						world.finishInstance();
					}
				}
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onEnterZone(Creature creature, ZoneType zone)
	{
		final Instance world = creature.getInstanceWorld();
		if (creature.isPlayer() && (world != null))
		{
			final int zoneId = zone.getId();
			
			// Zaken Portal -> Takes you to Queen Ant.
			if (zoneId == ZAKEN_PORTAL_ENTER_ID.getId())
			{
				creature.teleToLocation(TO_QUEEN_ANT_LOCATION, world);
			}
			// Queen Ant Portal -> Takes you to Orfen.
			else if (zoneId == QEEN_ANT_PORTAL_ENTER_ID.getId())
			{
				creature.teleToLocation(TO_ORFEN_LOCATION, world);
			}
			// Orfen Portal -> Takes you to the Core.
			else if (zoneId == ORFEN_PORTAL_ENTER_ID.getId())
			{
				creature.teleToLocation(TO_CORE_LOCATION, world);
			}
			// Core Portal -> Takes you to a Random Final Boss.
			else if (zoneId == CORE_PORTAL_ENTER_ID.getId())
			{
				final int location = world.getParameters().getInt("finalLoc", 0);
				if (location == 1)
				{
					creature.teleToLocation(TO_BAIUM_LOCATION, world);
				}
				else if (location == 2)
				{
					creature.teleToLocation(TO_GLAKIAS_LOCATION, world);
				}
				else if (location == 3)
				{
					creature.teleToLocation(TO_BEHEMOTH_LOCATION, world);
				}
			}
			
			sendUIEventWithDelay(creature.asPlayer(), world, 1);
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		final int currentStage = world.getParameters().getInt("continued_raid_stage", 0);
		world.getParameters().set("continued_raid_stage", currentStage + 1);
		
		switch (npc.getId())
		{
			case ZAKEN:
			{
				world.spawnGroup("Stage1_zaken_portal");
				broadcastScreenMessageToPlayers(NpcStringId.A_DIMENSIONAL_DOOR_HAS_BEEN_OPENED_TO_THE_FOLLOWING_LOCATIONS, npc, 0);
				break;
			}
			case QEEN_ANT:
			{
				world.spawnGroup("Stage2_QueenAnt_portal");
				broadcastScreenMessageToPlayers(NpcStringId.A_DIMENSIONAL_DOOR_HAS_BEEN_OPENED_TO_THE_FOLLOWING_LOCATIONS, npc, 0);
				break;
			}
			case ORFEN:
			{
				world.spawnGroup("Stage3_Orfen_portal");
				broadcastScreenMessageToPlayers(NpcStringId.A_DIMENSIONAL_DOOR_HAS_BEEN_OPENED_TO_THE_FOLLOWING_LOCATIONS, npc, 0);
				break;
			}
			case CORE:
			{
				startQuestTimer("FINAL_BOSS", 1000, npc, killer);
				broadcastScreenMessageToPlayers(NpcStringId.A_DIMENSIONAL_DOOR_HAS_BEEN_OPENED_TO_THE_FOLLOWING_LOCATIONS, npc, 0);
				break;
			}
			case BAIUM:
			case IMMORTAL_BAIUM:
			{
				broadcastScreenMessageToPlayers(NpcStringId.YOU_HAVE_DEFEATED_BAIUM_GET_YOUR_REWARD_FROM_BEIRON, npc, 0);
				world.spawnGroup("beiron_baium_reward");
				break;
			}
			case ULTIMATE_BEHEMOTH:
			case BEHEMOTH:
			{
				broadcastScreenMessageToPlayers(NpcStringId.YOU_HAVE_DEFEATED_BEHEMOTH_GET_YOUR_REWARD_FROM_BEIRON, npc, 0);
				world.spawnGroup("beiron_behemoth_reward");
				break;
			}
			case GLAKIAS:
			case GLAKIAS_DREADFUL:
			{
				broadcastScreenMessageToPlayers(NpcStringId.YOU_HAVE_DEFEATED_GLAKIAS_GET_YOUR_REWARD_FROM_BEIRON, npc, 0);
				world.spawnGroup("beiron_glakias_reward");
				break;
			}
		}
		
		if (currentStage == 4)
		{
			world.finishInstance();
		}
		
		killer.sendPacket(new ExSendUIEvent(killer, false, false, 0, 0, NpcStringId.TIME_LEFT));
		
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		if ((npc.getId() == ZAKEN))
		{
			startQuestTimer("CHECK_TIME", BOSS_COMBAT_LIMIT, npc, null);
		}
		
		if ((npc.getId() == EVENT_PORTALCOLOR))
		{
			npc.setDisplayEffect(1);
		}
	}
	
	@Override
	public void onCreatureSee(Npc npc, Creature creature)
	{
		if (((creature == null) || !creature.isPlayer() || (npc == null)))
		{
			return;
		}
		
		final Instance world = creature.getInstanceWorld();
		int currentStage = world.getParameters().getInt("continued_raid_stage", 0);
		if (currentStage >= 1)
		{
			npc.setDisplayEffect(1);
		}
		
	}
	
	private void broadcastScreenMessageToPlayers(NpcStringId message, Npc npc, int type)
	{
		// final Instance world = npc.getInstanceWorld();
		// if (world != null)
		// {
		// showOnScreenMsg(world, message, 2, 9000, true, new String[0]);
		// }
		//
		// if ((world != null) && (type == 2))
		// {
		// showOnScreenMsg(world, message, 2, 9000, false, new String[0]);
		// }
	}
	
	@Override
	public void onInstanceEnter(Player player, Instance world)
	{
		if (world.getRemainingTime() > 0)
		{
			final boolean running = world.getParameters().getBoolean("Continued_Raid_Running", false);
			if (running)
			{
				final int remainingTimeInSeconds = getRemainingTime(world);
				player.sendPacket(new ExSendUIEvent(player, false, false, remainingTimeInSeconds, 0, NpcStringId.TIME_LEFT));
			}
		}
	}
	
	private int getRemainingTime(Instance world)
	{
		final int currentStage = world.getParameters().getInt("continued_raid_stage", 0);
		if (currentStage <= 4)
		{
			return (int) (world.getRemainingTime() / 1000) - 600;
		}
		
		return (int) (world.getRemainingTime() / 1000);
	}
	
	private void sendUIEventWithDelay(Player player, Instance world, int delayInSeconds)
	{
		ThreadPool.schedule(() ->
		{
			if ((player != null) && (player.getInstanceWorld() == world))
			{
				player.sendPacket(new ExSendUIEvent(player, false, false, getRemainingTime(world), 0, NpcStringId.TIME_LEFT));
			}
		}, delayInSeconds * 1000L);
	}
	
	@Override
	public void onInstanceCreated(Instance world, Player player)
	{
		world.getParameters().set("finalLoc", 0);
		world.getParameters().set("continued_raid_stage", 0);
		world.setParameter("Continued_Raid_Running", true);
	}
	
	@Override
	public void onInstanceLeave(Player player, Instance world)
	{
		player.sendPacket(new ExSendUIEvent(player, false, false, 0, 0, NpcStringId.TIME_LEFT));
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (npc.getId() == BEIRON)
		{
			if (npc.isInInstance())
			{
				return "34195-1.htm";
			}
		}
		
		return npc.getId() + ".htm";
	}
	
	public static void main(String[] args)
	{
		new ContinuedRaidBattle();
	}
}
