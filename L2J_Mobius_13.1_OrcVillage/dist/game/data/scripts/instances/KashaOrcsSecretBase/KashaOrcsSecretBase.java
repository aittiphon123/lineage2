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
package instances.KashaOrcsSecretBase;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.enums.SkillFinishType;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10559_KashaOrcsSecretBase.Q10559_KashaOrcsSecretBase;

/**
 * @author CostyKiller
 */
public class KashaOrcsSecretBase extends InstanceScript
{
	// NPCs
	private static final int KASHU = 31637;
	private static final int CHAOS_STATUE = 18028;
	private static final int DUMMY_NPC = 27621;
	
	// Orc Raids
	private static final int RAID1_LAVIKI = 27630;
	private static final int RAID2_TOGOB = 27631;
	private static final int RAID3_KENUAN = 27632;
	private static final int RAID4_MORHI = 27633;
	
	// Skills
	private static final SkillHolder CHAOS_SIGMA = new SkillHolder(62197, 1);
	private static final SkillHolder KASHA_PUNISHMENT = new SkillHolder(62188, 1);
	private static final SkillHolder AKKAN_PROTECTION = new SkillHolder(62189, 1);
	
	// Location
	private static final Location KASHU_LOCATION = new Location(-24189, -109530, -2993, 54625);
	
	// Template
	private static final int TEMPLATE_ID = 348;
	private static final int INSTANCE_TIME = 7; // Default: 7 minutes
	
	// Instance Status
	private static final int INITIAL_STATUS = 0;
	private static final int FIGHT_RAID1 = 1;
	private static final int FIGHT_RAID2 = 2;
	private static final int FIGHT_RAID3 = 3;
	private static final int FIGHT_RAID4 = 4;
	
	public KashaOrcsSecretBase()
	{
		super(TEMPLATE_ID);
		addInstanceCreatedId(TEMPLATE_ID);
		addInstanceLeaveId(TEMPLATE_ID);
		addStartNpc(KASHU);
		addFirstTalkId(KASHU);
		addAttackId(RAID3_KENUAN);
		addKillId(RAID1_LAVIKI, RAID2_TOGOB, RAID3_KENUAN, RAID4_MORHI);
		addSpawnId(RAID1_LAVIKI, RAID2_TOGOB, RAID3_KENUAN, RAID4_MORHI);
		addAggroRangeEnterId(CHAOS_STATUE);
	}
	
	@Override
	public void onInstanceCreated(Instance activeInstance, Player player)
	{
		activeInstance.setStatus(0);
		activeInstance.getParameters().set("CHAOS_STATUE_SPAWNED", false);
	}
	
	@Override
	public void onInstanceLeave(Player player, Instance instance)
	{
		player.sendPacket(new ExSendUIEvent(player, true, false, 0, 0, NpcStringId.TIME_LEFT));
		// Cancel timers
		cancelQuestTimers("AKKAN_PROTECTION");
		cancelQuestTimers("ANNOUNCE_KASHA_PUNISHMENT");
		cancelQuestTimers("KASHA_PUNISHMENT");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "enterInstance":
			{
				enterInstance(player, npc, TEMPLATE_ID);
				startQuestTimer("START_MESSAGE", 2000, null, player);
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
			case "31637-01.html":
			case "31637-02.html":
			{
				htmltext = event;
				return htmltext;
			}
			case "31637-03.html":
			{
				npc.onDecay();
				startQuestTimer("ACTIVATE_NEXT_RAID", 2000, null, player);
				startQuestTimer("ACTIVATE_INSTANCE_EFFECTS", 2000, null, player);
				htmltext = event;
				return htmltext;
			}
			case "START_MESSAGE":
			{
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					showOnScreenMsg(world, NpcStringId.TO_COMPLETE_THE_MISSION_TALK_TO_KHAVATARI_KASHU, ExShowScreenMessage.TOP_CENTER, 2000, true);
				}
				break;
			}
			case "ACTIVATE_NEXT_RAID":
			{
				final Instance world = player.getInstanceWorld();
				switch (world.getStatus())
				{
					case INITIAL_STATUS:
					case FIGHT_RAID1:
					{
						showOnScreenMsg(world, NpcStringId.THE_KASHA_ORC_HAS_COME_TO_HIS_SENSES_AND_ATTACKS_YOU, ExShowScreenMessage.TOP_CENTER, 5000, true);
						world.getNpc(RAID1_LAVIKI).setInvul(false);
						world.getNpc(RAID1_LAVIKI).setImmobilized(false);
						world.getNpc(RAID1_LAVIKI).getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.FLESH_STONE);
						// Set instance time
						world.setDuration(INSTANCE_TIME);
						ThreadPool.schedule(() ->
						{
							world.setStatus(FIGHT_RAID2);
							world.getNpc(RAID1_LAVIKI).broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_VE_BEEN_SENT_BY_THOSE_WRETCHED_HIGH_ORCS_HUH_I_LL_KILL_YOU);
							world.getNpc(RAID1_LAVIKI).setRunning();
							world.getNpc(RAID1_LAVIKI).getAI().setIntention(Intention.ATTACK, player);
							world.getNpc(RAID1_LAVIKI).asAttackable().addDamageHate(player, 1, 999);
							// Show timer
							player.sendPacket(new ExSendUIEvent(player, false, false, INSTANCE_TIME * 60, 0, NpcStringId.TIME_LEFT));
							if (!world.getNpc(RAID1_LAVIKI).isDead())
							{
								// Announce next raid
								startQuestTimer("ANNOUNCE_NEXT_RAID", 50000, null, player);
								// Activate next raid
								startQuestTimer("ACTIVATE_NEXT_RAID", 60000, null, player);
							}
						}, 2000);
						break;
					}
					case FIGHT_RAID2:
					{
						showOnScreenMsg(world, NpcStringId.ANOTHER_KASHA_ORC_HAS_COME_TO_HIS_SENSES_AND_ATTACKS_YOU, ExShowScreenMessage.TOP_CENTER, 5000, true);
						world.getNpc(RAID2_TOGOB).setInvul(false);
						world.getNpc(RAID2_TOGOB).setImmobilized(false);
						world.getNpc(RAID2_TOGOB).getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.FLESH_STONE);
						ThreadPool.schedule(() ->
						{
							world.setStatus(FIGHT_RAID3);
							world.getNpc(RAID2_TOGOB).broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_RE_THE_HIGH_ORCS_DOG_ON_YOUR_KNEES);
							world.getNpc(RAID2_TOGOB).setRunning();
							world.getNpc(RAID2_TOGOB).getAI().setIntention(Intention.ATTACK, player);
							world.getNpc(RAID2_TOGOB).asAttackable().addDamageHate(player, 1, 999);
							if (!world.getNpc(RAID2_TOGOB).isDead())
							{
								// Announce next raid
								startQuestTimer("ANNOUNCE_NEXT_RAID", 50000, null, player);
								// Activate next raid
								startQuestTimer("ACTIVATE_NEXT_RAID", 60000, null, player);
							}
						}, 2000);
						break;
					}
					case FIGHT_RAID3:
					{
						showOnScreenMsg(world, NpcStringId.ANOTHER_KASHA_ORC_HAS_COME_TO_HIS_SENSES_AND_ATTACKS_YOU, ExShowScreenMessage.TOP_CENTER, 5000, true);
						world.getNpc(RAID3_KENUAN).setInvul(false);
						world.getNpc(RAID3_KENUAN).setImmobilized(false);
						world.getNpc(RAID3_KENUAN).getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.FLESH_STONE);
						ThreadPool.schedule(() ->
						{
							world.setStatus(FIGHT_RAID4);
							world.getNpc(RAID3_KENUAN).broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HOW_DARE_YOU_INTERRUPT_OUR_RITUAL_YOU_LL_PAY_WITH_YOUR_LIFE_FOR_THAT);
							world.getNpc(RAID3_KENUAN).setRunning();
							world.getNpc(RAID3_KENUAN).getAI().setIntention(Intention.ATTACK, player);
							world.getNpc(RAID3_KENUAN).asAttackable().addDamageHate(player, 1, 999);
							if (!world.getNpc(RAID3_KENUAN).isDead())
							{
								// Announce next raid
								startQuestTimer("ANNOUNCE_NEXT_RAID", 50000, null, player);
								// Activate next raid
								startQuestTimer("ACTIVATE_NEXT_RAID", 60000, null, player);
							}
						}, 2000);
						break;
					}
					case FIGHT_RAID4:
					{
						showOnScreenMsg(world, NpcStringId.ANOTHER_KASHA_ORC_HAS_COME_TO_HIS_SENSES_AND_ATTACKS_YOU, ExShowScreenMessage.TOP_CENTER, 5000, true);
						world.getNpc(RAID4_MORHI).setInvul(false);
						world.getNpc(RAID4_MORHI).setImmobilized(false);
						world.getNpc(RAID4_MORHI).getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.FLESH_STONE);
						ThreadPool.schedule(() ->
						{
							world.getNpc(RAID4_MORHI).setRunning();
							world.getNpc(RAID4_MORHI).getAI().setIntention(Intention.ATTACK, player);
							world.getNpc(RAID4_MORHI).asAttackable().addDamageHate(player, 1, 999);
						}, 2000);
						break;
					}
				}
				break;
			}
			case "SPAWN_CHAOS_STATUE":
			{
				final Instance world = npc.getInstanceWorld();
				showOnScreenMsg(world, NpcStringId.KENUAN_IS_USING_DEADLY_SKILLS_DESTROY_THE_CHAOS_STATUE_TO_STOP_HIM, ExShowScreenMessage.TOP_CENTER, 5000, true);
				world.spawnGroup("CHAOS_STATUE");
				world.getParameters().set("CHAOS_STATUE_SPAWNED", true);
				world.getNpc(RAID3_KENUAN).setTarget(player);
				world.getNpc(RAID3_KENUAN).doCast(CHAOS_SIGMA.getSkill());
				break;
			}
			case "ANNOUNCE_NEXT_RAID":
			{
				final Instance world = player.getInstanceWorld();
				showOnScreenMsg(world, NpcStringId.ANOTHER_KASHA_ORC_WILL_COME_TO_HIS_SENSES_IN_10_SEC, ExShowScreenMessage.TOP_CENTER, 5000, true);
				break;
			}
			case "ACTIVATE_INSTANCE_EFFECTS":
			{
				// Akkan Protection
				startQuestTimer("AKKAN_PROTECTION", 20000, null, player, true);
				// Kasha Punishment
				startQuestTimer("ANNOUNCE_KASHA_PUNISHMENT", 20000, null, player, true);
				startQuestTimer("KASHA_PUNISHMENT", 35000, null, player, true);
				break;
			}
			case "AKKAN_PROTECTION":
			{
				final Instance world = player.getInstanceWorld();
				SkillCaster.triggerCast(world.getNpc(DUMMY_NPC), player, AKKAN_PROTECTION.getSkill());
				break;
			}
			case "ANNOUNCE_KASHA_PUNISHMENT":
			{
				final Instance world = player.getInstanceWorld();
				showOnScreenMsg(world, NpcStringId.IN_15_SEC_THE_SECRET_BASE_WILL_BE_UNDER_KASHA_ORCS_PUNISHMENT_MOVE_UNDER_AKKAN_S_PROTECTION_TO_SURVIVE, ExShowScreenMessage.TOP_CENTER, 5000, true);
				break;
			}
			case "KASHA_PUNISHMENT":
			{
				final Instance world = player.getInstanceWorld();
				showOnScreenMsg(world, NpcStringId.KASHA_ORCS_PUNISHMENT_IS_ACTIVE_INSIDE_THE_SECRET_BASE, ExShowScreenMessage.TOP_CENTER, 5000, true);
				SkillCaster.triggerCast(world.getNpc(DUMMY_NPC), player, KASHA_PUNISHMENT.getSkill());
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
			final int hpPer = npc.getCurrentHpPercent();
			{
				if ((hpPer <= 80) && (world.getParameters().getBoolean("CHAOS_STATUE_SPAWNED") == false))
				{
					startQuestTimer("SPAWN_CHAOS_STATUE", 1000, npc, null);
				}
				else if ((hpPer <= 50) && (world.getParameters().getBoolean("CHAOS_STATUE_SPAWNED") == false))
				{
					startQuestTimer("SPAWN_CHAOS_STATUE", 1000, npc, null);
				}
				else if ((hpPer <= 30) && (world.getParameters().getBoolean("CHAOS_STATUE_SPAWNED") == false))
				{
					startQuestTimer("SPAWN_CHAOS_STATUE", 1000, npc, null);
				}
			}
		}
	}
	
	@Override
	public void onAggroRangeEnter(Npc npc, Player player, boolean isSummon)
	{
		SkillCaster.triggerCast(npc, player, AKKAN_PROTECTION.getSkill());
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		
		final QuestState questState = killer.getQuestState(Q10559_KashaOrcsSecretBase.class.getSimpleName());
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case RAID1_LAVIKI:
				{
					world.setStatus(FIGHT_RAID2);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_VE_JUST_POSTPONED_THE_INEVITABLE);
					break;
				}
				case RAID2_TOGOB:
				{
					world.setStatus(FIGHT_RAID3);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.MY_DEATH_WILL_CHANGE_NOTHING);
					break;
				}
				case RAID3_KENUAN:
				{
					world.setStatus(FIGHT_RAID4);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_RE_IN_LUCK_FOR_NOW_BUT_ONE_DAY_IT_WILL_LEAVE_YOU);
					break;
				}
				case RAID4_MORHI:
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_LL_NEVER_BE_SAFE);
					questState.set("RAIDS_KILLED", 1);
					questState.setCount(questState.getCount() + 1);
					questState.setCond(QuestCondType.DONE);
					world.finishInstance(0);
					break;
				}
				case CHAOS_STATUE:
				{
					world.getParameters().set("CHAOS_STATUE_SPAWNED", false);
					// Abort death skill
					if (!killer.isDead())
					{
						killer.stopSkillEffects(SkillFinishType.REMOVED, CHAOS_SIGMA.getSkillId());
					}
					break;
				}
			}
		}
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.setRandomAnimation(false);
		npc.setInvul(true);
		npc.setImmobilized(true);
		npc.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.FLESH_STONE);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final QuestState questState = player.getQuestState(Q10559_KashaOrcsSecretBase.class.getSimpleName());
		final Instance world = npc.getInstanceWorld();
		String htmltext = null;
		if (isInInstance(world))
		{
			if ((questState != null) && !questState.isCompleted())
			{
				if (questState.isCond(QuestCondType.STARTED))
				{
					if (npc.getId() == KASHU)
					{
						if (npc.getLocation().equals(KASHU_LOCATION))
						{
							npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THIS_PLACE_IS_VERY_SUSPICIOUS);
							htmltext = "31637.html";
						}
					}
				}
			}
		}
		
		npc.showChatWindow(player);
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new KashaOrcsSecretBase();
	}
}
