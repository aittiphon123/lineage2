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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class QuestList extends ServerPacket
{
	final Player _player;
	
	public QuestList(Player player)
	{
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		final List<Quest> quests = _player.getAllActiveQuests();
		ServerPackets.QUEST_LIST.writeId(this, buffer);
		buffer.writeShort(quests.size());
		for (Quest quest : quests)
		{
			buffer.writeInt(quest.getId());
			final QuestState questState = _player.getQuestState(quest.getName());
			if (questState == null)
			{
				buffer.writeInt(0);
				continue;
			}
			
			final int states = questState.getInt("__compltdStateFlags");
			if (states > 0)
			{
				buffer.writeInt(states);
			}
			else
			{
				buffer.writeInt(questState.getCond());
			}
		}
		
		final List<Item> questItems = _player.getInventory().getQuestItems();
		buffer.writeShort(questItems.size());
		for (Item item : questItems)
		{
			buffer.writeInt(item.getObjectId());
			buffer.writeInt(item.getId());
			buffer.writeInt(item.getCount());
			buffer.writeInt(item.getTemplate().getBodyPart().getMask());
		}
	}
}
