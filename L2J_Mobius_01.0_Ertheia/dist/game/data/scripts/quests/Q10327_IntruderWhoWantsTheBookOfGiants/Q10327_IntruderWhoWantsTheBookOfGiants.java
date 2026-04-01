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
package quests.Q10327_IntruderWhoWantsTheBookOfGiants;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.State;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10326_RespectYourElders.Q10326_RespectYourElders;

/**
 * Intruder Who Wants the Book of Giants (10327)
 * @author Gladicek, Trevor The Third
 */
public class Q10327_IntruderWhoWantsTheBookOfGiants extends Quest
{
	// NPCs
	private static final int PANTHEON = 32972;
	
	// Items
	private static final int THE_WAR_OF_GODS_AND_GIANTS = 17575;
	
	// Misc
	private static final int MAX_LEVEL = 20;
	private static final int APPRENTICE_EARRING = 112;
	
	public Q10327_IntruderWhoWantsTheBookOfGiants()
	{
		super(10327);
		addStartNpc(PANTHEON);
		addTalkId(PANTHEON);
		registerQuestItems(THE_WAR_OF_GODS_AND_GIANTS);
		addCondMaxLevel(MAX_LEVEL, "32972-09.html");
		addCondCompletedQuest(Q10326_RespectYourElders.class.getSimpleName(), "32972-09.html");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "32972-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32972-03.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32972-07.html":
			{
				if (qs.isCond(3))
				{
					showOnScreenMsg(player, NpcStringId.ACCESSORIES_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 4500);
					giveAdena(player, 160, true);
					giveItems(player, APPRENTICE_EARRING, 2);
					addExpAndSp(player, 7800, 5);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "32972-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "32972-04.html";
					break;
				}
				else if (qs.isCond(2))
				{
					htmltext = "32972-05.html";
					break;
				}
				else if (qs.isCond(3))
				{
					htmltext = "32972-06.html";
					break;
				}
			}
			case State.COMPLETED:
			{
				htmltext = "32972-08.html";
				break;
			}
		}
		
		return htmltext;
	}
}
