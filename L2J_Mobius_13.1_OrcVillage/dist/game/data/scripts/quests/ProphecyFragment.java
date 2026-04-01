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
package quests;

import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.script.newquestdata.QuestCondType;

import quests.Q10021_EssenceOfTheProphecy.Q10021_EssenceOfTheProphecy;
import quests.Q10031_ProphecyMachineRestoration.Q10031_ProphecyMachineRestoration;
import quests.Q10032_ToGereth.Q10032_ToGereth;
import quests.Q10033_ProphecyInterpretation.Q10033_ProphecyInterpretation;
import quests.Q10121_EssenceOfTheProphecy.Q10121_EssenceOfTheProphecy;
import quests.Q10131_ProphecyMachineRestoration.Q10131_ProphecyMachineRestoration;
import quests.Q10132_ToGereth.Q10132_ToGereth;
import quests.Q10133_ProphecyInterpretation.Q10133_ProphecyInterpretation;
import quests.Q10221_EssenceOfTheProphecy.Q10221_EssenceOfTheProphecy;
import quests.Q10231_ProphecyMachineRestoration.Q10231_ProphecyMachineRestoration;
import quests.Q10232_ToGereth.Q10232_ToGereth;
import quests.Q10233_ProphecyInterpretation.Q10233_ProphecyInterpretation;
import quests.Q10321_EssenceOfTheProphecy.Q10321_EssenceOfTheProphecy;
import quests.Q10331_ProphecyMachineRestoration.Q10331_ProphecyMachineRestoration;
import quests.Q10332_ToGereth.Q10332_ToGereth;
import quests.Q10333_ProphecyInterpretation.Q10333_ProphecyInterpretation;

/**
 * @author Mobius
 */
public class ProphecyFragment extends Script
{
	// NPCs
	private static final int TARTI = 34505;
	private static final int RAYMOND = 30289;
	private static final int GERETH = 33932;
	
	private ProphecyFragment()
	{
		addItemTalkId(39537); // Prophecy Fragment
		addItemTalkId(39538); // Prophecy Fragment
		addItemTalkId(39539); // Prophecy Fragment
		addItemTalkId(39540); // Prophecy Fragment
	}
	
	@Override
	public String onItemTalk(Item item, Player player)
	{
		QuestState questState = player.getQuestState(Q10021_EssenceOfTheProphecy.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc tarti = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == TARTI).stream().findFirst().orElse(null);
			if ((tarti != null) && tarti.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(tarti);
				tarti.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10031_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc raymond = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == RAYMOND).stream().findFirst().orElse(null);
			if ((raymond != null) && raymond.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(raymond);
				raymond.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10032_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc gereth = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == GERETH).stream().findFirst().orElse(null);
			if ((gereth != null) && gereth.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(gereth);
				gereth.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10033_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc gereth = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == GERETH).stream().findFirst().orElse(null);
			if ((gereth != null) && gereth.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(gereth);
				gereth.onAction(player);
			}
			return null;
		}
		
		// Ertheia
		
		questState = player.getQuestState(Q10121_EssenceOfTheProphecy.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc tarti = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == TARTI).stream().findFirst().orElse(null);
			if ((tarti != null) && tarti.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(tarti);
				tarti.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10131_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc raymond = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == RAYMOND).stream().findFirst().orElse(null);
			if ((raymond != null) && raymond.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(raymond);
				raymond.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10132_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc gereth = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == GERETH).stream().findFirst().orElse(null);
			if ((gereth != null) && gereth.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(gereth);
				gereth.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10133_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc gereth = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == GERETH).stream().findFirst().orElse(null);
			if ((gereth != null) && gereth.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(gereth);
				gereth.onAction(player);
			}
			return null;
		}
		
		// Death Knight
		
		questState = player.getQuestState(Q10221_EssenceOfTheProphecy.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc tarti = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == TARTI).stream().findFirst().orElse(null);
			if ((tarti != null) && tarti.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(tarti);
				tarti.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10231_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc raymond = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == RAYMOND).stream().findFirst().orElse(null);
			if ((raymond != null) && raymond.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(raymond);
				raymond.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10232_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc gereth = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == GERETH).stream().findFirst().orElse(null);
			if ((gereth != null) && gereth.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(gereth);
				gereth.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10233_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc gereth = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == GERETH).stream().findFirst().orElse(null);
			if ((gereth != null) && gereth.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(gereth);
				gereth.onAction(player);
			}
			return null;
		}
		
		// Shine Maker
		
		questState = player.getQuestState(Q10321_EssenceOfTheProphecy.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc tarti = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == TARTI).stream().findFirst().orElse(null);
			if ((tarti != null) && tarti.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(tarti);
				tarti.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10331_ProphecyMachineRestoration.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc raymond = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == RAYMOND).stream().findFirst().orElse(null);
			if ((raymond != null) && raymond.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(raymond);
				raymond.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10332_ToGereth.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc gereth = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == GERETH).stream().findFirst().orElse(null);
			if ((gereth != null) && gereth.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(gereth);
				gereth.onAction(player);
			}
			return null;
		}
		
		questState = player.getQuestState(Q10333_ProphecyInterpretation.class.getSimpleName());
		if ((questState != null) && questState.isStarted())
		{
			final Npc gereth = World.getInstance().getVisibleObjects(player, Npc.class, n -> n.getId() == GERETH).stream().findFirst().orElse(null);
			if ((gereth != null) && gereth.isInsideRadius2D(player, Npc.INTERACTION_DISTANCE))
			{
				questState.setCond(QuestCondType.DONE);
				sendEndDialog(player);
				
				player.setTarget(gereth);
				gereth.onAction(player);
			}
			return null;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new ProphecyFragment();
	}
}
