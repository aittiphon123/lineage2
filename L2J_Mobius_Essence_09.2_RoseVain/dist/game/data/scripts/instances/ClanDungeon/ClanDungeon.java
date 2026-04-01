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
package instances.ClanDungeon;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.managers.events.ClanDungeonRankingManager;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.TeleportWhereType;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @URL https://l2wiki.com/essence/articles/1988.html
 * @author Serenitty
 */
public class ClanDungeon extends InstanceScript
{
	// NPCs
	private static final int LANGDRE = 34219;
	private static final int ANDISH = 34423;
	private static final int BANDIT = 26078;
	
	private static final int CLAN_DUNGEON_RANKING_REWARD = 99886;
	
	private static final Set<Integer> BOSSES = new HashSet<>();
	static
	{
		BOSSES.add(18558); // NIURKA
		BOSSES.add(26001); // RADIN
	}
	
	private static final Set<Integer> BONUS_BOSSES = new HashSet<>();
	static
	{
		BONUS_BOSSES.add(29230);
		BONUS_BOSSES.add(29231);
		BONUS_BOSSES.add(29235);
		BONUS_BOSSES.add(29172);
	}
	
	private static final String SELECTED_ZONE = "SELECTED_ZONE_VAR";
	private static final String BANDIT_KILL_COUNT = "BANDIT_KILL_COUNT";
	
	private static final long BOSS_SPAWN_DELAY = 1 * 600_000;
	
	private static final int[] REGULAR_BOSSES =
	{
		29230, // QUEEN_ANT
		29231 // ORFEN
	};
	
	private static final int[] SPECIAL_BOSSES =
	{
		29235, // CH ORFEN
		29172 // CHAOTIC_QUEEN_ANT
	};
	
	private static final int TEMPLATE_ID = 235;
	
	private ClanDungeon()
	{
		super(TEMPLATE_ID);
		addStartNpc(LANGDRE, ANDISH);
		addFirstTalkId(LANGDRE, ANDISH);
		addTalkId(LANGDRE, ANDISH);
		addKillId(BOSSES);
		addKillId(BANDIT);
		addKillId(BONUS_BOSSES);
		addInstanceLeaveId(TEMPLATE_ID);
		addInstanceDestroyId(TEMPLATE_ID);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "ENTER":
			{
				if ((player.getClan() != null) && (player.getClan().getLeader().getObjectId() == player.getObjectId()) && (player.getCommandChannel() != null))
				{
					for (Player member : player.getCommandChannel().getMembers())
					{
						final Instance world = member.getInstanceWorld();
						if ((world != null) && (world.getTemplateId() == TEMPLATE_ID) && (world.getPlayersCount() < 40) && (player.getClanId() == member.getClanId()))
						{
							if ((world.getStatus() > 0))
							{
								player.sendPacket(new ExSendUIEvent(player, false, false, (int) (world.getRemainingTime() / 1000), 0, NpcStringId.TIME_LEFT));
							}
							
							return null;
						}
					}
				}
				
				// Clan checks.
				if (!player.isGM())
				{
					if ((player.getClan() == null) || (player.getClan().getLeaderId() != player.getObjectId()) || (player.getCommandChannel() == null))
					{
						return "no-cc.htm";
					}
					
					if (player.getClan().getLevel() < 3)
					{
						player.sendMessage("Your clan must be at least level 3.");
						return "no-cc.htm";
					}
					
					if (player.getClan().getLeader().getObjectId() != player.getObjectId())
					{
						player.sendMessage("You must be the clan leader to enter this instance.");
						return "no-leader.htm";
					}
					
					for (Player member : player.getCommandChannel().getMembers())
					{
						if ((member.getClan() == null) || (member.getClanId() != player.getClanId()))
						{
							player.sendMessage("Your command channel must be consisted only by clan members.");
							return "no-cc.htm";
						}
					}
				}
				
				enterInstance(player, npc, TEMPLATE_ID);
				break;
			}
			case "LANGDRE_TALK":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.GET_READY_THE_MONSTERS_WILL_APPEAR_SHORTLY);
				}
				break;
			}
			case "START_COUNTDOWN":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					world.getParameters().set("clan_dungeon_stage", 1);
					
					for (Player plr : world.getPlayers())
					{
						plr.sendPacket(new ExSendUIEvent(plr, false, false, 1200, 0, NpcStringId.TIME_LEFT));
					}
					
					startQuestTimer("SPAWN", 4000, npc, player);
					startQuestTimer("FINALBOSS", BOSS_SPAWN_DELAY, npc, player, false);
					HandleRankingPoints(world);
				}
				break;
			}
			case "SPAWN":
			{
				final Instance world = player.getInstanceWorld();
				{
					final int ZONE = world.getParameters().getInt(SELECTED_ZONE);
					world.spawnGroup("DUNGEON_" + ZONE);
				}
				
				for (Npc mobNpc : world.getAliveNpcs())
				{
					if ((player.getInstanceWorld() == mobNpc.getInstanceWorld()) && mobNpc.isAttackable())
					{
						final Attackable mob = (Attackable) mobNpc;
						mob.setTarget(player);
						mob.setRunning();
						mob.addDamageHate(player, 1, 999);
						mob.getAI().setIntention(Intention.ATTACK, player);
					}
				}
				break;
			}
			case "FINALBOSS":
			{
				final Instance world = player.getInstanceWorld();
				final int ZONE = world.getParameters().getInt(SELECTED_ZONE);
				world.despawnGroup("DUNGEON_" + ZONE);
				world.spawnGroup("DUNGEON_" + ZONE + "_BOSS");
				if (ZONE == 1)
				{
					player.sendPacket(new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 8000, false, null, NpcStringId.ONLY_THE_BRAVEST_CAN_GET_INTO_THE_BANDIT_STRONGHOLD, null));
				}
				break;
			}
			case "BONUS":
			{
				return "34423-2.htm";
			}
			case "BONUS_FINALBOSS_REGULAR":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					final boolean bossSpawned = world.getParameters().getBoolean("bonus_boss_spawned", false);
					if (!bossSpawned)
					{
						boolean spawnBoss = false;
						
						for (int bossId : REGULAR_BOSSES)
						{
							if (world.getAliveNpcs(bossId).isEmpty())
							{
								spawnBoss = true;
								break;
							}
						}
						
						if (spawnBoss)
						{
							if (player.destroyItemByItemId(ItemProcessType.FEE, CLAN_DUNGEON_RANKING_REWARD, 5, player, true))
							{
								final int randomBossId = REGULAR_BOSSES[Rnd.get(REGULAR_BOSSES.length)];
								final int ZONE = world.getParameters().getInt(SELECTED_ZONE);
								
								int x;
								int y;
								int z;
								int heading;
								if (ZONE == 1)
								{
									x = 82925;
									y = -15859;
									z = -1897;
									heading = 32767;
								}
								else
								{
									x = -50599;
									y = 83216;
									z = -5133;
									heading = 25443;
								}
								
								addSpawn(randomBossId, x, y, z, heading, false, 0, true, world.getId());
								
								world.getParameters().set("bonus_boss_spawned", true);
							}
							else
							{
								player.sendMessage("Insufficient Items");
							}
						}
					}
					else
					{
						player.sendMessage("Boss has already been spawned.");
					}
					
					return "normal.htm";
				}
				break;
			}
			case "BONUS_FINALBOSS_SPECIAL":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					final boolean bossSpawned = world.getParameters().getBoolean("bonus_boss_spawned", false);
					if (!bossSpawned)
					{
						boolean spawnBoss = false;
						
						for (int bossId : SPECIAL_BOSSES)
						{
							if (world.getAliveNpcs(bossId).isEmpty())
							{
								spawnBoss = true;
								break;
							}
						}
						
						if (spawnBoss)
						{
							if (player.destroyItemByItemId(org.l2jmobius.gameserver.model.item.enums.ItemProcessType.FEE, CLAN_DUNGEON_RANKING_REWARD, 10, player, true))
							{
								final int randomBossId = SPECIAL_BOSSES[Rnd.get(SPECIAL_BOSSES.length)];
								final int ZONE = world.getParameters().getInt(SELECTED_ZONE);
								
								int x;
								int y;
								int z;
								int heading;
								if (ZONE == 1)
								{
									x = 82925;
									y = -15859;
									z = -1897;
									heading = 32767;
								}
								else
								{
									x = -50599;
									y = 83216;
									z = -5133;
									heading = 25443;
								}
								
								addSpawn(randomBossId, x, y, z, heading, false, 0, true, world.getId());
								
								world.getParameters().set("bonus_boss_spawned", true);
							}
							else
							{
								player.sendMessage("Insufficient Items");
							}
						}
					}
					else
					{
						player.sendMessage("Special boss has already been spawned.");
					}
					
					return "hard.htm";
				}
				break;
			}
			case "TELEPORT":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					if ((player.getClan() == null) && (player.getClan().getLeaderId() == player.getObjectId()) && (player.getCommandChannel() != null))
					{
						for (Player players : world.getPlayers())
						{
							world.ejectPlayer(players);
							world.finishInstance();
						}
					}
					else
					{
						player.teleToLocation(TeleportWhereType.TOWN, null);
					}
					
				}
				break;
			}
		}
		
		return null;
	}
	
	@Override
	public void onInstanceLeave(Player player, Instance instance)
	{
		player.sendPacket(new ExSendUIEvent(player, false, false, 0, 0, NpcStringId.TIME_LEFT));
	}
	
	@Override
	public void onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if ((world == null) || (player == null))
		{
			return;
		}
		
		final int npcId = npc.getId();
		if (npcId == BANDIT)
		{
			final int banditKillCount = world.getParameters().getInt(BANDIT_KILL_COUNT, 0) + 1;
			if (banditKillCount >= 30)
			{
				final int zone = world.getParameters().getInt(SELECTED_ZONE, 1);
				world.spawnGroup("DUNGEON_" + zone);
				if (Rnd.get(100) < 70)
				{
					world.spawnGroup("TREASURE_" + zone);
				}
				
				world.getParameters().set(BANDIT_KILL_COUNT, 0);
				
				// Notifies only the Attackables in the instance to attack the player.
				for (Attackable mob : world.getAliveNpcs(Attackable.class))
				{
					if (mob.isAttackable())
					{
						mob.setRunning();
						mob.addDamageHate(player, 1, 999);
						mob.getAI().setIntention(org.l2jmobius.gameserver.ai.Intention.ATTACK, player);
					}
				}
			}
			else
			{
				world.getParameters().set(BANDIT_KILL_COUNT, banditKillCount);
			}
		}
		else if (BONUS_BOSSES.contains(npcId))
		{
			world.finishInstance();
		}
		else if (BOSSES.contains(npcId))
		{
			player.sendPacket(new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 8000, false, null, NpcStringId.I_WON_T_FORGET_I_LL_BE_BACK, null));
			
			final Player leader = player.getCommandChannel() != null ? player.getCommandChannel().getLeader() : player.getParty() != null ? player.getParty().getLeader() : player.isGM() ? player : null;
			if ((leader != null) && (leader.getInventory().getItemByItemId(CLAN_DUNGEON_RANKING_REWARD) != null))
			{
				world.getParameters().set("clan_dungeon_stage", 4);
				world.despawnGroup("LANGDRE");
				
				final int zone = world.getParameters().getInt(SELECTED_ZONE, 1);
				world.spawnGroup(zone == 1 ? "ANDISH" : "ANDISH_" + zone);
			}
			else
			{
				world.getParameters().set("clan_dungeon_stage", 3);
				final Npc langdre = world.getNpc(LANGDRE);
				if (langdre != null)
				{
					langdre.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.OH_NOT_BAD);
				}
				
				player.sendPacket(new ExSendUIEvent(player, false, false, 0, 0, NpcStringId.TIME_LEFT));
				world.finishInstance();
			}
		}
	}
	
	private void HandleRankingPoints(Instance instance)
	{
		final ScheduledFuture<?> spawnTask = ThreadPool.scheduleAtFixedRate(() ->
		{
			Player leader = null;
			
			for (Player player : instance.getPlayers())
			{
				if ((player.getCommandChannel() != null))
				{
					leader = player.getCommandChannel().getLeader();
					break;
				}
				else if (player.getParty() != null)
				{
					leader = player.getParty().getLeader();
					break;
				}
				else if (player.isGM())
				{
					leader = player;
					break;
				}
			}
			
			if (leader != null)
			{
				ClanDungeonRankingManager.getInstance().addPointsForPlayer(leader, 60);
			}
			
		}, 1 * 60 * 1000, 1 * 60 * 1000);
		
		instance.setParameter("RankingPointTask", spawnTask);
	}
	
	@Override
	public void onInstanceDestroy(Instance instance)
	{
		final ScheduledFuture<?> task = instance.getParameters().getObject("RankingPointTask", ScheduledFuture.class);
		if ((task != null) && !task.isDone())
		{
			task.cancel(true);
		}
		
		instance.setParameter("RankingPointTask", null);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final Instance world = player.getInstanceWorld();
		final int dungeonStage = world.getParameters().getInt("clan_dungeon_stage", 0);
		
		if (dungeonStage == 1) // Running.
		{
			return "34219.htm";
		}
		
		if ((dungeonStage == 3) || !(world.getRemainingTime() > 0)) // Running.
		{
			return "34219-1.htm";
		}
		
		if (dungeonStage == 4) // Anbish bonus.
		{
			return "34423-1.htm";
		}
		
		startQuestTimer("LANGDRE_TALK", 1000, npc, null);
		startQuestTimer("START_COUNTDOWN", 2000, npc, player);
		
		if (ZoneManager.getInstance().getZoneByName("fg_authority_thz").getPlayersInside().contains(player))
		{
			world.getParameters().set(SELECTED_ZONE, 2);
		}
		else
		{
			world.getParameters().set(SELECTED_ZONE, 1);
		}
		
		return super.onFirstTalk(npc, player);
	}
	
	public static void main(String[] args)
	{
		new ClanDungeon();
	}
}
