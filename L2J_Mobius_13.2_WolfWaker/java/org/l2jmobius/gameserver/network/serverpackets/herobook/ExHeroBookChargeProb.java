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
package org.l2jmobius.gameserver.network.serverpackets.herobook;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.herobook.HeroBookInfoHolder;
import org.l2jmobius.gameserver.model.herobook.HeroBookManager;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Atronic
 */
public class ExHeroBookChargeProb extends ServerPacket
{
	private final int _chance;
	private final int _category;
	
	public ExHeroBookChargeProb(int chance, int category)
	{
		_chance = chance;
		_category = category;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final HeroBookInfoHolder holder = player.getHeroBookProgress((byte) _category);
		final int expForNextLevel = HeroBookManager.getExpForNextLevel(Math.min(holder.getCurrentLevel(), 300));
		final double successChance = ((double) (holder.getCurrentExp() + _chance) / expForNextLevel) * 10000;
		
		ServerPackets.EX_HERO_BOOK_CHARGE_PROB.writeId(this, buffer);
		buffer.writeInt((int) successChance);
	}
}
