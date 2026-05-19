/*
 * Copyright (c) 2013 L2jMobius
 */
package org.l2jmobius.gameserver.config.custom;

import org.l2jmobius.commons.util.ConfigReader;

public class WeeklyMissionsConfig
{
	private static final java.lang.String WEEKLY_MISSIONS_CONFIG_FILE = "./config/Custom/WeeklyMissions.ini";
	
	public static boolean ENABLE_WEEKLY_MISSIONS;
	public static int WEEKLY_MISSION_MONSTER_KILL_TARGET;
	public static long WEEKLY_MISSION_EXP_REWARD;
	public static int WEEKLY_MISSION_SP_REWARD;
	
	public static void load()
	{
		final ConfigReader config = new ConfigReader(WEEKLY_MISSIONS_CONFIG_FILE);
		ENABLE_WEEKLY_MISSIONS = config.getBoolean("EnableWeeklyMissions", false);
		WEEKLY_MISSION_MONSTER_KILL_TARGET = Math.max(1, config.getInt("MonsterKillTarget", 500));
		WEEKLY_MISSION_EXP_REWARD = Math.max(0, config.getLong("ExpReward", 5000000));
		WEEKLY_MISSION_SP_REWARD = Math.max(0, config.getInt("SpReward", 500000));
	}
}
