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
package org.l2jmobius.gameserver.managers.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.managers.MailManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.itemcontainer.Mail;
import org.l2jmobius.gameserver.network.holders.MailMessage;

/**
 * @URL https://l2wiki.com/essence/articles/1988.html
 * @author Serenitty
 */
public class ClanDungeonRankingManager
{
	private static final Logger LOGGER = Logger.getLogger(ClanDungeonRankingManager.class.getName());
	
	private static final String INSERT_DUNGEON_RANKING = "REPLACE INTO clan_dungeon_ranking (charId, points) VALUES (?, ?)";
	private static final String SELECT_DUNGEON_RANKING = "SELECT charId, points FROM clan_dungeon_ranking";
	private static final String DELETE_DUNGEON_RANKING = "DELETE FROM clan_dungeon_ranking WHERE charId=?";
	private static final String DELETE_ALL_DUNGEON_RANKING = "DELETE FROM clan_dungeon_ranking";
	
	private static final int CLAN_DUNGEON_RANKING_REWARD = 99886;
	
	private final ConcurrentHashMap<Integer, AtomicInteger> _playerPoints = new ConcurrentHashMap<>();
	
	private final List<Integer> _rewardedPlayers = new ArrayList<>();
	
	public ClanDungeonRankingManager()
	{
		restoreDungeonRankingFromDatabase();
		scheduleMondayReset();
	}
	
	private void scheduleMondayReset()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			calendar.add(Calendar.WEEK_OF_MONTH, 1);
		}
		
		ThreadPool.schedule(new MondayResetTask(), calendar.getTimeInMillis() - System.currentTimeMillis());
	}
	
	public void addPointsForPlayer(Player player, int val)
	{
		_playerPoints.computeIfAbsent(player.getObjectId(), k -> new AtomicInteger()).addAndGet(val);
		saveDungeonRankingToDatabase();
	}
	
	public long getTotalPoints()
	{
		return _playerPoints.values().stream().mapToLong(AtomicInteger::get).sum();
	}
	
	public Map<Integer, Integer> getTopPlayers(int count)
	{
		return _playerPoints.entrySet().stream().sorted(Entry.comparingByValue(Comparator.comparingInt(AtomicInteger::get).reversed())).limit(count).collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().get()));
	}
	
	public int getPlayerRank(Player player)
	{
		final int playerPoints = _playerPoints.getOrDefault(player.getObjectId(), new AtomicInteger(0)).get();
		return (int) _playerPoints.values().stream().filter(points -> points.get() > playerPoints).count() + 1;
	}
	
	public int getPlayerPoints(Player player)
	{
		return _playerPoints.getOrDefault(player.getObjectId(), new AtomicInteger(0)).get();
	}
	
	public void rewardTopPlayersOnMonday()
	{
		final Map<Integer, Integer> topPlayers = getTopPlayers(10);
		int rank = 1;
		for (Entry<Integer, Integer> entry : topPlayers.entrySet())
		{
			final int playerId = entry.getKey();
			int rewards = 3;
			if (rank == 1)
			{
				rewards = 10;
			}
			else if ((rank == 2) || (rank == 3))
			{
				rewards = 7;
			}
			else if ((rank == 4) || (rank == 5))
			{
				rewards = 5;
			}
			
			giveRewardToPlayer(playerId, rewards);
			rank++;
		}
	}
	
	private void giveRewardToPlayer(int playerId, int rewards)
	{
		final MailMessage message = new MailMessage(-1, playerId, false, "Weekly Clan Dungeon Reward", "Congratulations! Here are your rewards.", 0);
		final Mail attachments = message.createAttachments();
		if (attachments != null)
		{
			// 1. We use the Enum ItemProcessType.REWARD (or FEE/CONSUME if you prefer)
			// 2. We cast rewards to (long)
			// 3. We pass null for Player and Object (the compiler accepts null if the type is compatible)
			attachments.addItem(ItemProcessType.REWARD, CLAN_DUNGEON_RANKING_REWARD, rewards, null, null);
			
			MailManager.getInstance().sendMessage(message);
		}
	}
	
	public void clear()
	{
		_playerPoints.clear();
		_rewardedPlayers.clear();
	}
	
	public void saveDungeonRankingToDatabase()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_DUNGEON_RANKING))
		{
			_playerPoints.forEach((playerId, points) ->
			{
				try
				{
					ps.setInt(1, playerId);
					ps.setInt(2, points.get());
					ps.addBatch(); // Add to batch instead of executing immediately
				}
				catch (SQLException e)
				{
					LOGGER.severe("Error adding batch for player " + playerId + ": " + e.getMessage());
				}
			});
			
			ps.executeBatch();
			// No need to call con.commit() as HikariCP autocommit is true.
		}
		catch (SQLException e)
		{
			LOGGER.severe("Error saving dungeon ranking data: " + e.getMessage());
		}
	}
	
	public void restoreDungeonRankingFromDatabase()
	{
		_playerPoints.clear();
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_DUNGEON_RANKING);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final int playerId = rs.getInt("charId");
				final int points = rs.getInt("points");
				_playerPoints.put(playerId, new AtomicInteger(points));
			}
		}
		catch (SQLException e)
		{
			LOGGER.severe("Error restoring data from database: " + e.getMessage());
		}
	}
	
	public void deletePlayerFromDungeonRanking(int playerId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_DUNGEON_RANKING))
		{
			ps.setInt(1, playerId);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.severe("Error deleting player from database: " + e.getMessage());
		}
		
		_playerPoints.remove(playerId);
	}
	
	public void deleteAllDungeonRankingData()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_ALL_DUNGEON_RANKING))
		{
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.severe("Error deleting all dungeon ranking data from database: " + e.getMessage());
		}
		
		clear();
	}
	
	protected class MondayResetTask implements Runnable
	{
		public MondayResetTask()
		{
		}
		
		@Override
		public void run()
		{
			ClanDungeonRankingManager.getInstance().rewardTopPlayersOnMonday();
			ClanDungeonRankingManager.getInstance().deleteAllDungeonRankingData();
			scheduleMondayReset();
			LOGGER.info(getClass().getSimpleName() + ": Clan Dungeon reset.");
		}
	}
	
	public static ClanDungeonRankingManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanDungeonRankingManager INSTANCE = new ClanDungeonRankingManager();
	}
}
