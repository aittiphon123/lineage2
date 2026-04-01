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
public class ExHeroBookInfo extends ServerPacket
{
	
	public ExHeroBookInfo()
	{
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_HERO_BOOK_INFO.writeId(this, buffer);
		
		final Player player = client.getPlayer();
		final HeroBookInfoHolder cat1 = (player != null) ? player.getHeroBookProgress((byte) 1) : new HeroBookInfoHolder();
		final HeroBookInfoHolder cat2 = (player != null) ? player.getHeroBookProgress((byte) 2) : new HeroBookInfoHolder();
		
		HeroBookManager.applyLevelEffects(player);
		
		buffer.writeInt(2); // categories array size
		
		// --- Category 1 ---
		final int lvl1 = cat1.getCurrentLevel();
		final int exp1 = cat1.getCurrentExp();
		final int max1 = HeroBookManager.getExpForNextLevel(Math.min(lvl1, 300), 1);
		final int chance1 = (max1 > 0) ? (int) (((double) exp1 / max1) * 10000) : 0;
		
		buffer.writeByte(1);
		buffer.writeInt(exp1);
		buffer.writeInt(lvl1);
		buffer.writeInt(chance1);
		buffer.writeInt(max1);
		
		// --- Category 2 ---
		final int lvl2 = cat2.getCurrentLevel();
		final int exp2 = cat2.getCurrentExp();
		final int max2 = HeroBookManager.getExpForNextLevel(Math.min(lvl2, 100), 2);
		final int chance2 = (max2 > 0) ? (int) (((double) exp2 / max2) * 10000) : 0;
		
		buffer.writeByte(2);
		buffer.writeInt(exp2);
		buffer.writeInt(lvl2);
		buffer.writeInt(chance2);
		buffer.writeInt(max2);
	}
}
