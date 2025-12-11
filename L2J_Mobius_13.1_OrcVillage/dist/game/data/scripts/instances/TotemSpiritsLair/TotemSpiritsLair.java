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
package instances.TotemSpiritsLair;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10551_TotemSpiritsTrial.Q10551_TotemSpiritsTrial;

/**
 * @author CostyKiller
 */
public class TotemSpiritsLair extends InstanceScript
{
	// NPCs
	private static final int KARUKIA = 30570;
	private static final int HESTUI_TOTEM_SPIRIT = 32678;
	private static final int GANDI_TOTEM_SPIRIT = 32679;
	private static final int DUDA_MARA_TOTEM_SPIRIT = 32680;
	private static final int[] SPIRITS =
	{
		HESTUI_TOTEM_SPIRIT,
		GANDI_TOTEM_SPIRIT,
		DUDA_MARA_TOTEM_SPIRIT
	};
	
	// Monsters
	private static final int HESTUI_TOTEM_AVATAR = 27627;
	private static final int GANDI_TOTEM_AVATAR = 27628;
	private static final int DUDA_MARA_TOTEM_AVATAR = 27629;
	private static final int[] MONSTERS =
	{
		HESTUI_TOTEM_AVATAR,
		GANDI_TOTEM_AVATAR,
		DUDA_MARA_TOTEM_AVATAR
	};
	
	// Skills
	private static final SkillHolder HESTUI_PAIN = new SkillHolder(62182, 1);
	private static final SkillHolder HESTUI_DESPAIR = new SkillHolder(62185, 1);
	private static final SkillHolder HESTUI_RAGE = new SkillHolder(62220, 1);
	private static final SkillHolder GANDI_PAIN = new SkillHolder(62183, 1);
	private static final SkillHolder GANDI_DESPAIR = new SkillHolder(62186, 1);
	private static final SkillHolder GANDI_RAGE = new SkillHolder(62221, 1);
	private static final SkillHolder DUDA_MARA_PAIN = new SkillHolder(62184, 1);
	private static final SkillHolder DUDA_MARA_DESPAIR = new SkillHolder(62187, 1);
	private static final SkillHolder DUDA_MARA_RAGE = new SkillHolder(62222, 1);
	
	// Locations
	private static final Location GANDI_TOTEM_AVATAR_LOCATION = new Location(-40749, -101462, -2728, 7101);
	private static final Location DUDA_MARA_TOTEM_AVATAR_LOCATION = new Location(-18909, -137617, -1504, 24758);
	
	// Misc
	private static final int TEMPLATE_ID = 347;
	
	public TotemSpiritsLair()
	{
		super(TEMPLATE_ID);
		addInstanceCreatedId(TEMPLATE_ID);
		addStartNpc(KARUKIA);
		addTalkId(KARUKIA);
		addFirstTalkId(SPIRITS);
		addTalkId(SPIRITS);
		addAttackId(MONSTERS);
		addKillId(MONSTERS);
	}
	
	@Override
	public void onInstanceCreated(Instance activeInstance, Player player)
	{
		activeInstance.setStatus(0);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final Instance world = player.getInstanceWorld();
		switch (event)
		{
			case "enterInstance":
			{
				enterInstance(player, npc, TEMPLATE_ID);
				startQuestTimer("SPAWN_HESTUI", 3000, null, player);
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
			case "32678.html":
			case "32678-01.html":
			case "32678-02.html":
			case "32678-03.html":
			case "32678-04.html":
			case "32679.html":
			case "32679-01.html":
			case "32679-02.html":
			case "32679-03.html":
			case "32679-04.html":
			case "32680.html":
			case "32680-01.html":
			case "32680-02.html":
			case "32680-03.html":
			{
				htmltext = event;
				return htmltext;
			}
			case "finishInstance":
			{
				if (isInInstance(world))
				{
					world.finishInstance(3);
				}
				break;
			}
			case "SPAWN_HESTUI":
			{
				if (isInInstance(world))
				{
					world.spawnGroup("HESTUI_TOTEM_AVATAR");
					showOnScreenMsg(player, NpcStringId.TOTEM_SPIRITS_FIRST_TRIAL_DEFEAT_HESTUI_TOTEM_SPIRIT_S_AVATAR, ExShowScreenMessage.TOP_CENTER, 5000, true);
				}
				break;
			}
			case "teleportToGandi":
			{
				if (isInInstance(world))
				{
					ThreadPool.schedule(() ->
					{
						player.teleToLocation(GANDI_TOTEM_AVATAR_LOCATION);
						world.spawnGroup("GANDI_TOTEM_AVATAR");
						showOnScreenMsg(player, NpcStringId.TOTEM_SPIRITS_SECOND_TRIAL_DEFEAT_GANDI_TOTEM_SPIRIT_S_AVATAR, ExShowScreenMessage.TOP_CENTER, 5000, true);
						
					}, 5000);
				}
				break;
			}
			case "teleportToDudaMara":
			{
				if (isInInstance(world))
				{
					ThreadPool.schedule(() ->
					{
						player.teleToLocation(DUDA_MARA_TOTEM_AVATAR_LOCATION);
						world.spawnGroup("DUDA_MARA_TOTEM_AVATAR");
						showOnScreenMsg(player, NpcStringId.TOTEM_SPIRITS_THIRD_TRIAL_DEFEAT_DUDA_MARA_TOTEM_SPIRIT_S_AVATAR, ExShowScreenMessage.TOP_CENTER, 5000, true);
						
					}, 5000);
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
		final int hpPer = npc.getCurrentHpPercent();
		if (!isInInstance(world))
		{
			return;
		}
		
		switch (npc.getId())
		{
			case HESTUI_TOTEM_AVATAR:
			{
				if (getRandom(100) <= 80)
				{
					if (SkillCaster.checkUseConditions(npc, HESTUI_PAIN.getSkill()))
					{
						npc.doCast(HESTUI_PAIN.getSkill());
					}
				}
				
				if ((hpPer <= 90) && world.isStatus(0))
				{
					showOnScreenMsg(world, NpcStringId.I_LL_MAKE_YOU_FEAR_ME, ExShowScreenMessage.TOP_CENTER, 5000, true);
					world.setStatus(1);
				}
				else if ((hpPer <= 60) && SkillCaster.checkUseConditions(npc, HESTUI_RAGE.getSkill()) && world.isStatus(1))
				{
					showOnScreenMsg(world, NpcStringId.TOTEM_SPIRIT_S_AVATAR_IS_ENRAGED_IT_IS_USING_A_POWERFUL_ATTACK_AND_RECOVERING_HP, ExShowScreenMessage.TOP_CENTER, 5000, true);
					npc.doCast(HESTUI_RAGE.getSkill());
					world.setStatus(2);
				}
				else if ((hpPer <= 30) && SkillCaster.checkUseConditions(npc, HESTUI_DESPAIR.getSkill()) && world.isStatus(2))
				{
					showOnScreenMsg(world, NpcStringId.NOW_YOU_WILL_SEE_MY_TRUE_POWER, ExShowScreenMessage.TOP_CENTER, 5000, true);
					npc.doCast(HESTUI_DESPAIR.getSkill());
					world.setStatus(3);
				}
				break;
			}
			case GANDI_TOTEM_AVATAR:
			{
				if (getRandom(100) <= 80)
				{
					if (SkillCaster.checkUseConditions(npc, GANDI_PAIN.getSkill()))
					{
						npc.doCast(GANDI_PAIN.getSkill());
					}
				}
				
				if ((hpPer <= 90) && world.isStatus(3))
				{
					showOnScreenMsg(world, NpcStringId.ARE_YOU_SO_TIRED_OF_LIVING_STRANGER, ExShowScreenMessage.TOP_CENTER, 5000, true);
					world.setStatus(4);
				}
				else if ((hpPer <= 60) && SkillCaster.checkUseConditions(npc, GANDI_RAGE.getSkill()) && world.isStatus(4))
				{
					showOnScreenMsg(world, NpcStringId.TOTEM_SPIRIT_S_AVATAR_IS_ENRAGED_IT_IS_USING_A_POWERFUL_ATTACK_AND_RECOVERING_HP_2, ExShowScreenMessage.TOP_CENTER, 5000, true);
					npc.doCast(GANDI_RAGE.getSkill());
					world.setStatus(5);
				}
				else if ((hpPer <= 30) && SkillCaster.checkUseConditions(npc, GANDI_DESPAIR.getSkill()) && world.isStatus(5))
				{
					showOnScreenMsg(world, NpcStringId.YOU_VE_BEEN_AMUSING_AT_FIRST_BUT_NOW_YOUR_WEAKNESS_ANGERS_ME_TO_NO_END, ExShowScreenMessage.TOP_CENTER, 5000, true);
					npc.doCast(GANDI_DESPAIR.getSkill());
					world.setStatus(6);
				}
				break;
			}
			
			case DUDA_MARA_TOTEM_AVATAR:
			{
				if (getRandom(100) <= 80)
				{
					if (SkillCaster.checkUseConditions(npc, DUDA_MARA_PAIN.getSkill()))
					{
						npc.doCast(DUDA_MARA_PAIN.getSkill());
					}
				}
				
				if ((hpPer <= 90) && world.isStatus(6))
				{
					showOnScreenMsg(world, NpcStringId.WHO_DARES_DISTURB_MY_REST_DIE, ExShowScreenMessage.TOP_CENTER, 5000, true);
					world.setStatus(7);
				}
				else if ((hpPer <= 60) && SkillCaster.checkUseConditions(npc, DUDA_MARA_RAGE.getSkill()) && world.isStatus(7))
				{
					showOnScreenMsg(world, NpcStringId.TOTEM_SPIRIT_S_AVATAR_IS_ENRAGED_IT_IS_USING_A_POWERFUL_ATTACK_AND_RECOVERING_HP_3, ExShowScreenMessage.TOP_CENTER, 5000, true);
					npc.doCast(DUDA_MARA_RAGE.getSkill());
					world.setStatus(8);
				}
				else if ((hpPer <= 30) && SkillCaster.checkUseConditions(npc, DUDA_MARA_DESPAIR.getSkill()) && world.isStatus(8))
				{
					showOnScreenMsg(world, NpcStringId.IS_THAT_ALL_YOU_VE_GOT_PATHETIC, ExShowScreenMessage.TOP_CENTER, 5000, true);
					npc.doCast(DUDA_MARA_DESPAIR.getSkill());
				}
				break;
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			final QuestState questState = killer.getQuestState(Q10551_TotemSpiritsTrial.class.getSimpleName());
			if ((questState != null) && !questState.isCompleted())
			{
				switch (npc.getId())
				{
					case HESTUI_TOTEM_AVATAR:
					{
						npc.onDecay();
						addSpawn(HESTUI_TOTEM_SPIRIT, npc, false, 0, true, world.getId());
						showOnScreenMsg(killer, NpcStringId.THE_TOTEM_SPIRIT_ASSUMES_ITS_TRUE_APPEARANCE_TALK_TO_HESTUI_TOTEM_SPIRIT, ExShowScreenMessage.TOP_CENTER, 5000, true);
						break;
					}
					case GANDI_TOTEM_AVATAR:
					{
						npc.onDecay();
						addSpawn(GANDI_TOTEM_SPIRIT, npc, false, 0, true, world.getId());
						showOnScreenMsg(killer, NpcStringId.THE_TOTEM_SPIRIT_ASSUMES_ITS_TRUE_APPEARANCE_TALK_TO_GANDI_TOTEM_SPIRIT, ExShowScreenMessage.TOP_CENTER, 5000, true);
						break;
					}
					case DUDA_MARA_TOTEM_AVATAR:
					{
						npc.onDecay();
						addSpawn(DUDA_MARA_TOTEM_SPIRIT, npc, false, 0, true, world.getId());
						showOnScreenMsg(killer, NpcStringId.THE_TOTEM_SPIRIT_ASSUMES_ITS_TRUE_APPEARANCE_TALK_TO_DUDA_MARA_TOTEM_SPIRIT, ExShowScreenMessage.TOP_CENTER, 5000, true);
						questState.set("TRIAL_PASSED", 1);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case HESTUI_TOTEM_SPIRIT:
				{
					return "32678.html";
				}
				case GANDI_TOTEM_SPIRIT:
				{
					return "32679.html";
				}
				case DUDA_MARA_TOTEM_SPIRIT:
				{
					return "32680.html";
				}
			}
		}
		
		npc.showChatWindow(player);
		return null;
	}
	
	public static void main(String[] args)
	{
		new TotemSpiritsLair();
	}
}
