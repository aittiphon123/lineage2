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
package instances.AbandonedCaveOfTrials;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10543_RescueTheCaptives.Q10543_RescueTheCaptives;

/**
 * @author CostyKiller
 */
public class AbandonedCaveOfTrials extends InstanceScript
{
	private static final boolean DEBUG = false; // Set it true to display related messages into server console.
	
	// Location
	private static final Location ENTER_LOCATION = new Location(21405, -107798, -3048, 45796);
	
	// NPCs
	private static final int DOKARA_QUEST = 32670;
	private static final int DOKARA_SOLO = 33700;
	private static final int HESTUI_WARRIOR = 32671;
	private static final int PRISON_DOOR = 19459;
	
	// Cave of Trials Prisoners
	private static final int[] PRISONERS =
	{
		33701, // Kronin
		33702, // Ufken
		33703, // Dakis
		33704, // Harbo
	};
	
	// Monsters Solo
	private static final int KASHA_ORC_WARDER_DAGGER = 26546;
	private static final int KASHA_ORC_WARDER_POLE = 26547;
	private static final int KASHA_ORC_WARDER_ASSISTANT = 26548;
	private static final int KASHA_ORC_WARDEN_HALKIRK = 26549;
	private static final int[] MONSTERS =
	{
		KASHA_ORC_WARDER_DAGGER,
		KASHA_ORC_WARDER_POLE,
		KASHA_ORC_WARDER_ASSISTANT
	};
	
	// Monsters Quest
	private static final int KASHA_ORC_WARDER_DAGGER_QUEST = 27622;
	private static final int KASHA_ORC_WARDER_POLE_QUEST = 27623;
	private static final int KASHA_ORC_WARDER_ASSISTANT_QUEST = 27644;
	private static final int KASHA_ORC_WARDEN_HALKIRK_QUEST = 27625;
	private static final int[] MONSTERS_QUEST =
	{
		KASHA_ORC_WARDER_DAGGER_QUEST,
		KASHA_ORC_WARDER_POLE_QUEST,
		KASHA_ORC_WARDER_ASSISTANT_QUEST
	};
	
	// Prison Doors
	private static final int DOOR1 = 20140000;
	private static final int DOOR2 = 20140002;
	private static final int DOOR3 = 20140004;
	private static final int DOOR4 = 20140006;
	private static final int DOOR5 = 20140008;
	private static final int DOOR6 = 20140010;
	private static final int DOOR7 = 20140012;
	private static final int DOOR8 = 20140014;
	
	// Rooms Doors
	private static final int DOOR9 = 20140200;
	private static final int DOOR10 = 20140202;
	private static final int DOOR11 = 20140204;
	private static final int DOOR12 = 20140206;
	private static final int DOOR13 = 20140208;
	private static final int DOOR14 = 20140210;
	private static final int DOOR15 = 20140212;
	
	// TODO: Add Room Door Effects
	
	// Template
	private static final int TEMPLATE_ID_QUEST = 346; // quest instance
	private static final int TEMPLATE_ID_SOLO = 351; // solo instance from aden
	private static final int INSTANCE_TIME = 7; // Default: 7 minutes
	
	// Instance Status
	private static final int INITIAL_STAGE = 0;
	private static final int STAGE1 = 1;
	private static final int STAGE2 = 2;
	private static final int STAGE3 = 3;
	private static final int STAGE4 = 4;
	private static final int STAGE5 = 5;
	
	// Raid time
	long raidStartTime = 0;
	long raidFinishTime = 0;
	long instanceFinishTime = 0;
	int completionIndex = -1;
	
	// Reward times in milliseconds
	private static final long[] TIME_LIMITS =
	{
		270000, // S Rank: less than 4m30s
		300000, // A Rank: less than 5m
		330000, // B Rank: less than 5m30s
		360000, // C Rank: less than 6m
		420000 // D Rank: less than 7m
	};
	
	// Reward buffs
	private static final SkillHolder[] TENACITY_REWARDS =
	{
		new SkillHolder(62240, 5), // S Rank
		new SkillHolder(62240, 4), // A Rank
		new SkillHolder(62240, 3), // B Rank
		new SkillHolder(62240, 2), // C Rank
		new SkillHolder(62240, 1) // D Rank
	};
	
	// Reward items
	private static final ItemHolder[] SANTIAGO_SOUPS =
	{
		new ItemHolder(80670, 100), // S Rank
		new ItemHolder(80670, 80), // A Rank
		new ItemHolder(80670, 60), // B Rank
		new ItemHolder(80670, 40), // C Rank
		new ItemHolder(80670, 20) // D Rank
	};
	
	// Rank labels (NpcStringId)
	private static final NpcStringId[] RANK_LABELS =
	{
		NpcStringId.FONT_COLOR_FF6666_S_GRADE_FONT, // S Rank
		NpcStringId.FONT_COLOR_FF9999_A_GRADE_FONT, // A Rank
		NpcStringId.FONT_COLOR_FED7A0_B_GRADE_FONT, // B Rank
		NpcStringId.FONT_COLOR_BBAA88_C_GRADE_FONT, // C Rank
		NpcStringId.FONT_COLOR_5599FF_D_GRADE_FONT // D Rank
	};
	
	// Tenacity Rewards (NpcStringId)
	private static final NpcStringId[] TENACITY_LABELS =
	{
		NpcStringId.TENACITY_REWARD_LV_5,
		NpcStringId.TENACITY_REWARD_LV_4,
		NpcStringId.TENACITY_REWARD_LV_3,
		NpcStringId.TENACITY_REWARD_LV_2,
		NpcStringId.TENACITY_REWARD_LV_1
	};
	
	// Santiago’s Soup (NpcStringId)
	private static final NpcStringId[] SOUP_LABELS =
	{
		NpcStringId.SANTIAGO_S_ROYAL_SOUP_100_PCS,
		NpcStringId.SANTIAGO_S_ROYAL_SOUP_80_PCS,
		NpcStringId.SANTIAGO_S_ROYAL_SOUP_60_PCS,
		NpcStringId.SANTIAGO_S_ROYAL_SOUP_40_PCS,
		NpcStringId.SANTIAGO_S_ROYAL_SOUP_20_PCS
	};
	
	public AbandonedCaveOfTrials()
	{
		super(TEMPLATE_ID_QUEST, TEMPLATE_ID_SOLO);
		addInstanceCreatedId(TEMPLATE_ID_QUEST, TEMPLATE_ID_SOLO);
		addInstanceLeaveId(TEMPLATE_ID_QUEST, TEMPLATE_ID_SOLO);
		addFirstTalkId(PRISON_DOOR, DOKARA_QUEST, DOKARA_SOLO, HESTUI_WARRIOR);
		addFirstTalkId(PRISONERS);
		addSpawnId(HESTUI_WARRIOR);
		// Solo
		addSpawnId(KASHA_ORC_WARDER_DAGGER, KASHA_ORC_WARDER_POLE, KASHA_ORC_WARDER_ASSISTANT, KASHA_ORC_WARDEN_HALKIRK);
		addAttackId(KASHA_ORC_WARDER_DAGGER, KASHA_ORC_WARDER_POLE);
		addKillId(KASHA_ORC_WARDER_DAGGER, KASHA_ORC_WARDER_POLE, KASHA_ORC_WARDER_ASSISTANT, KASHA_ORC_WARDEN_HALKIRK);
		// Quest
		addSpawnId(KASHA_ORC_WARDER_DAGGER_QUEST, KASHA_ORC_WARDER_POLE_QUEST, KASHA_ORC_WARDER_ASSISTANT_QUEST, KASHA_ORC_WARDEN_HALKIRK_QUEST);
		addAttackId(KASHA_ORC_WARDER_DAGGER_QUEST, KASHA_ORC_WARDER_POLE_QUEST);
		addKillId(KASHA_ORC_WARDER_DAGGER_QUEST, KASHA_ORC_WARDER_POLE_QUEST, KASHA_ORC_WARDER_ASSISTANT_QUEST, KASHA_ORC_WARDEN_HALKIRK_QUEST);
	}
	
	@Override
	public void onInstanceCreated(Instance activeInstance, Player player)
	{
		activeInstance.setParameter("STAGE1_WARDERS_ASSISTANTS_SPAWNED", false);
		activeInstance.setParameter("STAGE2_WARDERS_ASSISTANTS_SPAWNED", false);
		activeInstance.setParameter("STAGE3_WARDERS_ASSISTANTS_SPAWNED", false);
		activeInstance.setParameter("STAGE4_WARDERS_ASSISTANTS_SPAWNED", false);
	}
	
	@Override
	public void onInstanceLeave(Player player, Instance instance)
	{
		player.sendPacket(new ExSendUIEvent(player, true, false, 0, 0, NpcStringId.TIME_LEFT));
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "32670-01.html":
			case "32670-02.html":
			case "32671-01.html":
			case "32671-01b.html":
			case "32671-02.html":
			case "33700.html":
			case "33700-01.html":
			{
				htmltext = event;
				return htmltext;
			}
			case "32671-01c.html":
			{
				npc.onDecay();
				htmltext = event;
				return htmltext;
			}
			case "enterInstance":
			{
				if (!player.getQuestState(Q10543_RescueTheCaptives.class.getSimpleName()).isCompleted())
				{
					htmltext = "notAvailable.html";
					return htmltext;
				}
				enterInstance(player, npc, TEMPLATE_ID_SOLO);
				break;
			}
			case "enterInstanceQuest":
			{
				if (player.getQuestState(Q10543_RescueTheCaptives.class.getSimpleName()).isStarted())
				{
					enterInstance(player, npc, TEMPLATE_ID_QUEST);
					startQuestTimer("ENTRY_ANNOUNCE", 2000, null, player);
				}
				break;
			}
			case "reenterInstance":
			{
				final Instance activeInstance = getPlayerInstance(player);
				if (isInInstance(activeInstance))
				{
					enterInstance(player, npc, activeInstance.getTemplateId());
				}
				break;
			}
			case "startMission":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					world.setStatus(STAGE1);
					player.teleToLocation(ENTER_LOCATION);
					startQuestTimer("START_MESSAGE", 2000, null, player);
					startQuestTimer("DOORS_CLOSED_EFFECTS", 100, null, player);
				}
				break;
			}
			case "openDoors":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					if (world.getAliveNpcCount(MONSTERS) == 0)
					{
						switch (world.getStatus())
						{
							case STAGE1:
							{
								world.getDoor(DOOR1).openMe();
								world.getDoor(DOOR2).openMe();
								break;
							}
							case STAGE2:
							{
								world.getDoor(DOOR3).openMe();
								world.getDoor(DOOR4).openMe();
								break;
							}
							case STAGE3:
							{
								world.getDoor(DOOR5).openMe();
								world.getDoor(DOOR6).openMe();
								break;
							}
							case STAGE4:
							{
								world.getDoor(DOOR7).openMe();
								world.getDoor(DOOR8).openMe();
								break;
							}
						}
						htmltext = "prisonDoorUnlocked.html";
						npc.onDecay();
						return htmltext;
					}
				}
			}
			case "freePrisoner":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					if (world.getAliveNpcCount(MONSTERS) == 0)
					{
						switch (world.getStatus())
						{
							case STAGE1:
							{
								world.setStatus(STAGE2);
								npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_LL_NEVER_FORGET_YOUR_HELP);
								// Open doors and remove effect
								world.getDoor(DOOR9).openMe();
								world.getDoor(DOOR10).openMe();
								// world.broadcastPacket(new OnEventTrigger(DOOR9_EFFECT, false));
								// world.broadcastPacket(new OnEventTrigger(DOOR10_EFFECT, false));
								// Spawn second group
								world.spawnGroup("STAGE2_WARDERS");
								break;
							}
							case STAGE2:
							{
								world.setStatus(STAGE3);
								npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_M_FREE_THANK_YOU);
								// Open doors and remove effect
								world.getDoor(DOOR11).openMe();
								world.getDoor(DOOR12).openMe();
								// world.broadcastPacket(new OnEventTrigger(DOOR11_EFFECT, false));
								// world.broadcastPacket(new OnEventTrigger(DOOR12_EFFECT, false));
								// Close the door to the previous room and add effect
								world.getDoor(DOOR10).closeMe();
								startQuestTimer("DOORS_CLOSED_EFFECTS", 100, null, player);
								// Spawn third group
								world.spawnGroup("STAGE3_WARDERS");
								break;
							}
							case STAGE3:
							{
								world.setStatus(STAGE4);
								npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.MAY_THE_GLORY_OF_PA_AGRIO_BE_WITH_YOU);
								// Open doors and remove effect
								world.getDoor(DOOR13).openMe();
								world.getDoor(DOOR14).openMe();
								// world.broadcastPacket(new OnEventTrigger(DOOR13_EFFECT, false));
								// world.broadcastPacket(new OnEventTrigger(DOOR14_EFFECT, false));
								// Close the door to the previous room and add effect
								world.getDoor(DOOR12).closeMe();
								startQuestTimer("DOORS_CLOSED_EFFECTS", 100, null, player);
								
								// Spawn forth group
								world.spawnGroup("STAGE4_WARDERS");
								break;
							}
							case STAGE4:
							{
								world.setStatus(STAGE5);
								npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHY_DON_T_YOU_KILL_THEIR_LEADER);
								// Open door and remove effect
								world.getDoor(DOOR15).openMe();
								// world.broadcastPacket(new OnEventTrigger(DOOR15_EFFECT, false));
								if (world.getTemplateId() == TEMPLATE_ID_QUEST)
								{
									startQuestTimer("ANNOUNCE_STAGE5_QUEST_MSG", 5000, npc, player);
								}
								world.spawnGroup("STAGE5_MONSTERS");
								break;
							}
						}
						showOnScreenMsg(world, NpcStringId.YOU_HAVE_RESCUED_THE_PRISONER_AND_CAN_GO_FORWARD_NOW, ExShowScreenMessage.TOP_CENTER, 5000, true);
						htmltext = npc.getId() + "-01.html";
						return htmltext;
					}
					showOnScreenMsg(world, NpcStringId.THE_NEXT_AREA_WILL_BECOME_AVAILABLE_AFTER_YOU_KILL_ALL_THE_MONSTERS_AND_SAVE_THE_PRISONER, ExShowScreenMessage.TOP_CENTER, 5000, true);
				}
			}
			case "getReward":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					if (npc.getId() == DOKARA_SOLO)
					{
						instanceFinishTime = raidFinishTime - raidStartTime;
						
						for (int i = 0; i < TIME_LIMITS.length; i++)
						{
							if (instanceFinishTime <= TIME_LIMITS[i])
							{
								completionIndex = i;
								
								// Give Tenacity Reward buff
								SkillCaster.triggerCast(npc, player, TENACITY_REWARDS[i].getSkill());
								
								// Give Santiago’s Royal Soup
								player.addItem(ItemProcessType.REWARD, SANTIAGO_SOUPS[i].getId(), SANTIAGO_SOUPS[i].getCount(), npc, true);
								break;
							}
						}
					}
					// Set reenter only if you get the reward
					LocalDateTime now = LocalDateTime.now();
					LocalDateTime nextWednesday = now.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)).withHour(6).withMinute(30).withSecond(0).withNano(0);
					
					world.setReenterTime(nextWednesday.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
					world.finishInstance(0);
					htmltext = "rewardsChosen.html";
					return htmltext;
				}
				break;
			}
			case "noReward":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					world.finishInstance(0);
					htmltext = "rewardsNotChosen.html";
					return htmltext;
				}
				break;
			}
			case "goBack":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					world.finishInstance(0);
				}
				break;
			}
			case "DOORS_CLOSED_EFFECTS":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					switch (world.getStatus())
					{
						case STAGE1:
						{
							// world.broadcastPacket(new OnEventTrigger(DOOR9_EFFECT, true));
							// world.broadcastPacket(new OnEventTrigger(DOOR10_EFFECT, true));
							break;
						}
						case STAGE2:
						{
							// world.broadcastPacket(new OnEventTrigger(DOOR10_EFFECT, true));
							// world.broadcastPacket(new OnEventTrigger(DOOR11_EFFECT, true));
							// world.broadcastPacket(new OnEventTrigger(DOOR12_EFFECT, true));
							break;
						}
						case STAGE3:
						{
							// world.broadcastPacket(new OnEventTrigger(DOOR12_EFFECT, true));
							// world.broadcastPacket(new OnEventTrigger(DOOR13_EFFECT, true));
							// world.broadcastPacket(new OnEventTrigger(DOOR14_EFFECT, true));
							break;
						}
						case STAGE4:
						{
							// world.broadcastPacket(new OnEventTrigger(DOOR14_EFFECT, true));
							// world.broadcastPacket(new OnEventTrigger(DOOR15_EFFECT, true));
							break;
						}
					}
					break;
				}
			}
			case "ENTRY_ANNOUNCE":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.TO_COMPLETE_THE_MISSION_TALK_TO_HESTUI_S_UNKNOWN_WARRIOR, ExShowScreenMessage.TOP_CENTER, 5000, true);
				}
				break;
			}
			case "START_MESSAGE":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					// Store start time
					raidStartTime = System.currentTimeMillis();
					if (DEBUG)
					{
						LOGGER.info("Abandoned Cave of Trials instance time for player: -> " + player.getName() + " -> Raid start (ms): " + raidStartTime);
					}
					showOnScreenMsg(world, NpcStringId.TO_MOVE_TO_THE_NEXT_AREA_GO_FORWARD, ExShowScreenMessage.TOP_CENTER, 2000, true);
					// Set instance time
					world.setDuration(INSTANCE_TIME);
					// Show timer
					player.sendPacket(new ExSendUIEvent(player, false, false, INSTANCE_TIME * 60, 0, NpcStringId.TIME_LEFT));
					// Spawn first group
					world.spawnGroup("STAGE1_WARDERS");
				}
				break;
			}
			case "ANNOUNCE_ASSISTANTS_SPAWN":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					if (world.getParameters().getBoolean("STAGE" + world.getStatus() + "_WARDERS_ASSISTANTS_SPAWNED") == false)
					{
						world.spawnGroup("STAGE" + world.getStatus() + "_WARDERS_ASSISTANTS");
						world.setParameter("STAGE" + world.getStatus() + "_WARDERS_ASSISTANTS_SPAWNED", true);
					}
					showOnScreenMsg(world, NpcStringId.KASHA_ORC_WARDER_S_ASSISTANT_HAS_APPEARED_KILL_HIM_BEFORE_HE_USES_HIS_ABILITIES, ExShowScreenMessage.TOP_CENTER, 5000, true);
				}
				break;
			}
			case "ANNOUNCE_ORC_WARDERS_KILL":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.YOU_HAVE_KILLED_ALL_KASHA_ORC_WARDERS_NOW_YOU_CAN_RESCUE_THE_PRISONER, ExShowScreenMessage.TOP_CENTER, 5000, true);
				}
				break;
			}
			case "ANNOUNCE_STAGE5_QUEST_MSG":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.HELP_HESTUI_S_UNKNOWN_WARRIOR_DEFEAT_KASHA_ORC_WARDEN_THE_AUTOHUNTING_MODE_IS_AVAILABLE_IF_THE_RESPECTFUL_HUNTING_IS_TURNED_OFF, ExShowScreenMessage.TOP_CENTER, 5000, true);
				}
				break;
			}
		}
		return super.onEvent(event, npc, player);
		
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case KASHA_ORC_WARDER_DAGGER:
				case KASHA_ORC_WARDER_POLE:
				case KASHA_ORC_WARDER_DAGGER_QUEST:
				case KASHA_ORC_WARDER_POLE_QUEST:
				{
					if (world.getParameters().getBoolean("STAGE" + world.getStatus() + "_WARDERS_ASSISTANTS_SPAWNED") == false)
					{
						startQuestTimer("ANNOUNCE_ASSISTANTS_SPAWN", 1000, npc, attacker);
					}
					npc.asAttackable().setRunning();
					npc.asAttackable().setTarget(attacker);
					npc.asAttackable().addDamageHate(attacker, 0, 99999);
					npc.asAttackable().getAI().setIntention(Intention.ATTACK, attacker);
					break;
				}
				case KASHA_ORC_WARDEN_HALKIRK:
				case KASHA_ORC_WARDEN_HALKIRK_QUEST:
				{
					// TODO: Manage self buffs
					// showOnScreenMsg(world, NpcStringId.THE_KASHA_ORC_WARDEN_S_ATTACK_SPEED_HAS_INCREASED, ExShowScreenMessage.TOP_CENTER, 5000, true);
					// showOnScreenMsg(world, NpcStringId.THE_KASHA_ORC_WARDEN_S_ATTACK_SPEED_HAS_INCREASED_EVEN_MORE, ExShowScreenMessage.TOP_CENTER, 5000, true);
					// showOnScreenMsg(world, NpcStringId.THE_KASHA_ORC_WARDEN_S_ATTACK_SPEED_HAS_REACHED_ITS_MAXIMUM, ExShowScreenMessage.TOP_CENTER, 5000, true);
					break;
				}
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		
		final QuestState questState = killer.getQuestState(Q10543_RescueTheCaptives.class.getSimpleName());
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				// Solo and quest
				case KASHA_ORC_WARDER_DAGGER:
				case KASHA_ORC_WARDER_POLE:
				case KASHA_ORC_WARDER_ASSISTANT:
				case KASHA_ORC_WARDER_DAGGER_QUEST:
				case KASHA_ORC_WARDER_POLE_QUEST:
				case KASHA_ORC_WARDER_ASSISTANT_QUEST:
				{
					switch (world.getTemplateId())
					{
						case TEMPLATE_ID_QUEST:
						{
							if (world.getAliveNpcCount(MONSTERS_QUEST) == 0)
							{
								startQuestTimer("ANNOUNCE_ORC_WARDERS_KILL", 1000, npc, killer);
							}
							break;
						}
						case TEMPLATE_ID_SOLO:
						{
							if (world.getAliveNpcCount(MONSTERS) == 0)
							{
								startQuestTimer("ANNOUNCE_ORC_WARDERS_KILL", 1000, npc, killer);
							}
							break;
						}
					}
					break;
				}
				case KASHA_ORC_WARDEN_HALKIRK:
				case KASHA_ORC_WARDEN_HALKIRK_QUEST:
				{
					switch (world.getTemplateId())
					{
						case TEMPLATE_ID_QUEST:
						{
							// Finish quest
							if ((questState != null) && !questState.isCompleted())
							{
								questState.set("RAID_KILLED", 1);
								questState.setCount(questState.getCount() + 1);
								questState.setCond(QuestCondType.DONE);
							}
							break;
						}
						case TEMPLATE_ID_SOLO:
						{
							// Store finish time
							raidFinishTime = System.currentTimeMillis();
							if (DEBUG)
							{
								LOGGER.info("Abandoned Cave of Trials instance time for player: -> " + killer.getName() + " -> Raid finish (ms): " + raidFinishTime);
							}
							showOnScreenMsg(world, NpcStringId.GOOD_JOB_TALK_TO_DOKARA_TO_GET_YOUR_REWARD, ExShowScreenMessage.TOP_CENTER, 5000, true);
							// Spawn Dokara
							world.spawnGroup("DOKARA");
							break;
						}
					}
					// Hide timer
					killer.sendPacket(new ExSendUIEvent(killer, true, false, 0, 0, NpcStringId.TIME_LEFT));
					world.finishInstance(5);
					break;
				}
			}
		}
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			npc.setRandomWalking(false);
			switch (npc.getId())
			{
				case HESTUI_WARRIOR:
				{
					if (world.isStatus(STAGE5))
					{
						npc.setInvul(true);
						final Npc halkirk = world.getNpc(KASHA_ORC_WARDEN_HALKIRK_QUEST);
						addAttackDesire(npc, halkirk.asCreature());
					}
					break;
				}
				case KASHA_ORC_WARDER_ASSISTANT:
				case KASHA_ORC_WARDER_ASSISTANT_QUEST:
				{
					Player player = world.getFirstPlayer();
					if (player != null)
					{
						npc.asAttackable().setRunning();
						npc.asAttackable().setTarget(player);
						addAttackDesire(npc, player);
					}
					break;
				}
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final Instance world = npc.getInstanceWorld();
		String htmltext = null;
		if (isInInstance(world))
		{
			switch (world.getTemplateId())
			{
				case TEMPLATE_ID_QUEST:
				{
					switch (npc.getId())
					{
						case PRISON_DOOR:
						{
							if (world.getAliveNpcCount(MONSTERS_QUEST) == 0)
							{
								htmltext = "prisonDoorLocked.html";
							}
							else
							{
								htmltext = "prisonDoorNotReady.html";
								showOnScreenMsg(world, NpcStringId.THE_NEXT_AREA_WILL_BECOME_AVAILABLE_AFTER_YOU_KILL_ALL_THE_MONSTERS_AND_SAVE_THE_PRISONER, ExShowScreenMessage.TOP_CENTER, 5000, true);
							}
							break;
						}
						case HESTUI_WARRIOR:
						{
							if (world.isStatus(INITIAL_STAGE))
							{
								htmltext = "32671-01a.html";
							}
							else if (world.getNpc(HESTUI_WARRIOR).getAI().getIntention() == Intention.ATTACK)
							{
								htmltext = "cannotTalk.html";
							}
							else
							{
								htmltext = npc.getId() + ".html";
							}
							break;
						}
						case DOKARA_QUEST:
						{
							if (world.getNpc(HESTUI_WARRIOR) != null)
							{
								htmltext = npc.getId() + "-00.html";
							}
							else
							{
								htmltext = npc.getId() + ".html";
							}
							break;
						}
						default:
						{
							htmltext = npc.getId() + ".html";
							break;
						}
					}
					break;
				}
				case TEMPLATE_ID_SOLO:
				{
					switch (npc.getId())
					{
						case PRISON_DOOR:
						{
							if (world.getAliveNpcCount(MONSTERS) == 0)
							{
								htmltext = "prisonDoorLocked.html";
							}
							else
							{
								htmltext = "prisonDoorNotReady.html";
								showOnScreenMsg(world, NpcStringId.THE_NEXT_AREA_WILL_BECOME_AVAILABLE_AFTER_YOU_KILL_ALL_THE_MONSTERS_AND_SAVE_THE_PRISONER, ExShowScreenMessage.TOP_CENTER, 5000, true);
							}
							break;
						}
						case DOKARA_SOLO:
						{
							if (world.isStatus(INITIAL_STAGE))
							{
								htmltext = npc.getId() + ".html";
								break;
							}
							else if (world.isStatus(STAGE5))
							{
								npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_VE_COMPLETED_THE_TASK);
								instanceFinishTime = raidFinishTime - raidStartTime;
								
								long totalSeconds = instanceFinishTime / 1000;
								long minutes = totalSeconds / 60;
								long seconds = totalSeconds % 60;
								if (DEBUG)
								{
									LOGGER.info("Abandoned Cave of Trials instance time for player: -> " + player.getName() + " -> Instance time: " + minutes + "m " + seconds + "s");
								}
								
								for (int i = 0; i < TIME_LIMITS.length; i++)
								{
									if (instanceFinishTime <= TIME_LIMITS[i])
									{
										completionIndex = i;
										break;
									}
								}
								htmltext = getHtm(player, "rewards.html");
								
								if (completionIndex != -1)
								{
									htmltext = htmltext.replace("%completionLevel%", "<fstring>" + RANK_LABELS[completionIndex].getId() + "</fstring>");
									htmltext = htmltext.replace("%rewards%", "<fstring>" + TENACITY_LABELS[completionIndex].getId() + "</fstring><br1>" + "<fstring>" + SOUP_LABELS[completionIndex].getId() + "</fstring>");
								}
								else
								{
									htmltext = htmltext.replace("%completionLevel%", "No Rank");
									htmltext = htmltext.replace("%rewards%", "No rewards (over 7 minutes).");
								}
							}
							break;
						}
						default:
						{
							htmltext = npc.getId() + ".html";
							break;
						}
					}
					break;
				}
			}
		}
		
		npc.showChatWindow(player);
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new AbandonedCaveOfTrials();
	}
}
