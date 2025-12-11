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
import org.l2jmobius.gameserver.model.multisell.Entry;
import org.l2jmobius.gameserver.model.multisell.Ingredient;
import org.l2jmobius.gameserver.model.multisell.ListContainer;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class MultiSellList extends ServerPacket
{
	private final int _listId;
	private final List<Entry> _entries;
	private final int _size;
	
	public MultiSellList(ListContainer list)
	{
		_listId = list.getListId();
		_entries = list.getEntries();
		_size = _entries.size();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.MULTI_SELL_LIST.writeId(this, buffer);
		buffer.writeInt(_listId);
		buffer.writeInt(_size);
		for (int i = 0; i < _size; i++)
		{
			final Entry entry = _entries.get(i);
			buffer.writeInt(entry.getEntryId());
			final Ingredient prod = entry.getProducts().get(0); // C1 has only one product.
			if (prod.getTemplate() != null)
			{
				buffer.writeInt(prod.getTemplate().getDisplayId());
				buffer.writeInt(prod.getTemplate().getBodyPart().getMask());
				buffer.writeInt(prod.getTemplate().getType2());
			}
			else
			{
				buffer.writeInt(prod.getItemId());
				buffer.writeInt(0);
				buffer.writeInt(65535);
			}
			buffer.writeInt(prod.getItemCount());
			
			buffer.writeInt(entry.getIngredients().size());
			for (Ingredient ing : entry.getIngredients())
			{
				buffer.writeInt(ing.getTemplate() != null ? ing.getTemplate().getDisplayId() : ing.getItemId());
				buffer.writeInt(ing.getTemplate() != null ? ing.getTemplate().getType2() : 65535);
				buffer.writeInt(ing.getItemCount());
			}
		}
	}
}
