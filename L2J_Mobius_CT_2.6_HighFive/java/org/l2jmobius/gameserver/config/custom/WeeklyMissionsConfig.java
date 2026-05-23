/*
 * Copyright (c) 2013 L2jMobius
 */
package org.l2jmobius.gameserver.config.custom;

import org.l2jmobius.commons.util.ConfigReader;

public class WeeklyMissionsConfig
{
	public enum MissionType
	{
		MONSTER_KILL,
		PVP_KILL,
		ITEM_COLLECT,
		ONLINE_TIME
	}

	public static class RewardItem
	{
		private final int _id;
		private final long _count;

		public RewardItem(int id, long count)
		{
			_id = id;
			_count = count;
		}

		public int getId()
		{
			return _id;
		}

		public long getCount()
		{
			return _count;
		}
	}

	private static final java.lang.String WEEKLY_MISSIONS_CONFIG_FILE = "./config/Custom/WeeklyMissions.ini";

	public static boolean ENABLE_WEEKLY_MISSIONS;
	public static MissionType WEEKLY_MISSION_TYPE;
	public static int WEEKLY_MISSION_MONSTER_KILL_TARGET;
	public static int WEEKLY_MISSION_TARGET_VALUE;
	public static int[] WEEKLY_MISSION_TARGET_MONSTER_IDS;
	public static int WEEKLY_MISSION_REQUIRED_ITEM_ID;
	public static long WEEKLY_MISSION_REQUIRED_ITEM_COUNT;
	public static long WEEKLY_MISSION_EXP_REWARD;
	public static int WEEKLY_MISSION_SP_REWARD;
	public static RewardItem[] WEEKLY_MISSION_ITEM_REWARDS;

	public static void load()
	{
		final ConfigReader config = new ConfigReader(WEEKLY_MISSIONS_CONFIG_FILE);
		ENABLE_WEEKLY_MISSIONS = config.getBoolean("EnableWeeklyMissions", false);
		WEEKLY_MISSION_TYPE = parseMissionType(config.getString("MissionType", "MONSTER_KILL"));
		WEEKLY_MISSION_MONSTER_KILL_TARGET = Math.max(1, config.getInt("MonsterKillTarget", 500));
		WEEKLY_MISSION_TARGET_VALUE = Math.max(1, config.getInt("MissionTargetValue", WEEKLY_MISSION_MONSTER_KILL_TARGET));
		WEEKLY_MISSION_TARGET_MONSTER_IDS = parseIntegerList(config.getString("TargetMonsterIds", ""));
		WEEKLY_MISSION_REQUIRED_ITEM_ID = Math.max(0, config.getInt("RequiredItemId", 0));
		WEEKLY_MISSION_REQUIRED_ITEM_COUNT = Math.max(0, config.getLong("RequiredItemCount", 0));
		WEEKLY_MISSION_EXP_REWARD = Math.max(0, config.getLong("ExpReward", 5000000));
		WEEKLY_MISSION_SP_REWARD = Math.max(0, config.getInt("SpReward", 500000));
		WEEKLY_MISSION_ITEM_REWARDS = parseRewardItems(config.getString("RewardItems", ""));
	}

	private static MissionType parseMissionType(String value)
	{
		try
		{
			return MissionType.valueOf(value.trim().toUpperCase());
		}
		catch (Exception e)
		{
			return MissionType.MONSTER_KILL;
		}
	}

	private static RewardItem[] parseRewardItems(String value)
	{
		if ((value == null) || value.trim().isEmpty())
		{
			return new RewardItem[0];
		}
		return java.util.Arrays.stream(value.split(";"))
			.map(String::trim)
			.filter(v -> !v.isEmpty())
			.map(WeeklyMissionsConfig::parseRewardItem)
			.filter(java.util.Objects::nonNull)
			.toArray(RewardItem[]::new);
	}

	private static RewardItem parseRewardItem(String token)
	{
		final String[] values = token.split(",");
		if (values.length != 2)
		{
			return null;
		}
		try
		{
			final int id = Integer.parseInt(values[0].trim());
			final long count = Long.parseLong(values[1].trim());
			if ((id < 1) || (count < 1))
			{
				return null;
			}
			return new RewardItem(id, count);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	private static int[] parseIntegerList(String value)
	{
		if ((value == null) || value.trim().isEmpty())
		{
			return new int[0];
		}
		return java.util.Arrays.stream(value.split(","))
			.map(String::trim)
			.filter(v -> !v.isEmpty())
			.mapToInt(v ->
			{
				try
				{
					return Integer.parseInt(v);
				}
				catch (NumberFormatException e)
				{
					return -1;
				}
			})
			.filter(id -> id > 0)
			.toArray();
	}
}
