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
package org.l2jmobius.gameserver.network.serverpackets.clandungeon;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.sql.CharInfoTable;
import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.managers.events.ClanDungeonRankingManager;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Serenitty
 */
public class ExDungeonRankingList extends ServerPacket
{
	private final Map<Integer, Integer> _rankingData;
	private final int _currentSeason;
	private final int _rankingscope;
	private final int _rankingId;
	
	public ExDungeonRankingList(int season, int scope, int rankingid)
	{
		_currentSeason = season;
		_rankingscope = scope;
		_rankingId = rankingid;
		_rankingData = new ConcurrentHashMap<>(ClanDungeonRankingManager.getInstance().getTopPlayers(100));
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_INZONE_RANKING_LIST.writeId(this, buffer);
		buffer.writeInt(_rankingId);
		buffer.writeByte(_rankingscope);
		buffer.writeByte(_currentSeason);
		buffer.writeInt(_rankingData.size());
		
		int rank = 1;
		for (Entry<Integer, Integer> entry : _rankingData.entrySet())
		{
			final int playerObjectId = entry.getKey();
			final int score = entry.getValue();
			final String playerName = CharInfoTable.getInstance().getNameById(playerObjectId);
			final int playerClass = CharInfoTable.getInstance().getClassIdById(playerObjectId);
			final int level = CharInfoTable.getInstance().getLevelById(playerObjectId);
			final int race = CharInfoTable.getInstance().getRacelById(playerObjectId);
			final int pledgeClan = CharInfoTable.getInstance().getClanIdById(playerObjectId);
			final Clan clan = ClanTable.getInstance().getClan(pledgeClan);
			
			buffer.writeSizedString(playerName);
			buffer.writeInt(playerClass);
			buffer.writeInt(race);
			buffer.writeInt(level);
			
			if (clan != null)
			{
				buffer.writeSizedString(clan.getName());
				buffer.writeInt(clan.getLevel());
			}
			else
			{
				buffer.writeSizedString("");
				buffer.writeInt(0);
			}
			
			buffer.writeLong(score);
			buffer.writeInt(rank);
			buffer.writeInt(0);
			
			rank++;
		}
	}
}