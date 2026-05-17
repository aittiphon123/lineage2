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
package org.l2jmobius.gameserver.config.custom;

import org.l2jmobius.commons.util.ConfigReader;

public class CatchUpExpConfig
{
	private static final String CATCH_UP_EXP_CONFIG_FILE = "./config/Custom/CatchUpExp.ini";
	
	public static boolean ENABLE_CATCH_UP_EXP;
	public static int CATCH_UP_MAX_LEVEL;
	public static double CATCH_UP_EXP_MULTIPLIER;
	public static double CATCH_UP_SP_MULTIPLIER;
	public static double CATCH_UP_EXP_MULTIPLIER_LOW;
	public static double CATCH_UP_SP_MULTIPLIER_LOW;
	public static int CATCH_UP_LOW_MAX_LEVEL;
	public static double CATCH_UP_EXP_MULTIPLIER_MID;
	public static double CATCH_UP_SP_MULTIPLIER_MID;
	public static boolean ENABLE_RESTED_BONUS;
	public static int RESTED_MIN_OFFLINE_HOURS;
	public static double RESTED_EXP_MULTIPLIER;
	public static double RESTED_SP_MULTIPLIER;
	public static int CATCH_UP_MID_MAX_LEVEL;
	
	public static void load()
	{
		final ConfigReader config = new ConfigReader(CATCH_UP_EXP_CONFIG_FILE);
		ENABLE_CATCH_UP_EXP = config.getBoolean("EnableCatchUpExp", false);
		CATCH_UP_MAX_LEVEL = config.getInt("CatchUpMaxLevel", 76);
		CATCH_UP_EXP_MULTIPLIER = config.getDouble("CatchUpExpMultiplier", 1.20);
		CATCH_UP_SP_MULTIPLIER = config.getDouble("CatchUpSpMultiplier", 1.10);
		CATCH_UP_LOW_MAX_LEVEL = config.getInt("CatchUpLowMaxLevel", 40);
		CATCH_UP_EXP_MULTIPLIER_LOW = config.getDouble("CatchUpExpMultiplierLow", 1.40);
		CATCH_UP_SP_MULTIPLIER_LOW = config.getDouble("CatchUpSpMultiplierLow", 1.25);
		CATCH_UP_MID_MAX_LEVEL = config.getInt("CatchUpMidMaxLevel", 61);
		CATCH_UP_EXP_MULTIPLIER_MID = config.getDouble("CatchUpExpMultiplierMid", 1.25);
		CATCH_UP_SP_MULTIPLIER_MID = config.getDouble("CatchUpSpMultiplierMid", 1.15);
		ENABLE_RESTED_BONUS = config.getBoolean("EnableRestedBonus", false);
		RESTED_MIN_OFFLINE_HOURS = config.getInt("RestedMinOfflineHours", 8);
		RESTED_EXP_MULTIPLIER = config.getDouble("RestedExpMultiplier", 1.10);
		RESTED_SP_MULTIPLIER = config.getDouble("RestedSpMultiplier", 1.05);

		// Sanity guards.
		CATCH_UP_MAX_LEVEL = Math.max(1, CATCH_UP_MAX_LEVEL);
		CATCH_UP_LOW_MAX_LEVEL = Math.max(1, Math.min(CATCH_UP_LOW_MAX_LEVEL, CATCH_UP_MAX_LEVEL));
		CATCH_UP_MID_MAX_LEVEL = Math.max(CATCH_UP_LOW_MAX_LEVEL, Math.min(CATCH_UP_MID_MAX_LEVEL, CATCH_UP_MAX_LEVEL));
		CATCH_UP_EXP_MULTIPLIER = Math.max(0, CATCH_UP_EXP_MULTIPLIER);
		CATCH_UP_SP_MULTIPLIER = Math.max(0, CATCH_UP_SP_MULTIPLIER);
		CATCH_UP_EXP_MULTIPLIER_LOW = Math.max(0, CATCH_UP_EXP_MULTIPLIER_LOW);
		CATCH_UP_SP_MULTIPLIER_LOW = Math.max(0, CATCH_UP_SP_MULTIPLIER_LOW);
		CATCH_UP_EXP_MULTIPLIER_MID = Math.max(0, CATCH_UP_EXP_MULTIPLIER_MID);
		CATCH_UP_SP_MULTIPLIER_MID = Math.max(0, CATCH_UP_SP_MULTIPLIER_MID);
		RESTED_MIN_OFFLINE_HOURS = Math.max(1, RESTED_MIN_OFFLINE_HOURS);
		RESTED_EXP_MULTIPLIER = Math.max(0, RESTED_EXP_MULTIPLIER);
		RESTED_SP_MULTIPLIER = Math.max(0, RESTED_SP_MULTIPLIER);
	}

	public static double getExpMultiplierForLevel(int level)
	{
		if (!ENABLE_CATCH_UP_EXP || (level > CATCH_UP_MAX_LEVEL))
		{
			return 1.0;
		}

		if (level <= CATCH_UP_LOW_MAX_LEVEL)
		{
			return CATCH_UP_EXP_MULTIPLIER_LOW;
		}
		if (level <= CATCH_UP_MID_MAX_LEVEL)
		{
			return CATCH_UP_EXP_MULTIPLIER_MID;
		}
		return CATCH_UP_EXP_MULTIPLIER;
	}

	public static double getSpMultiplierForLevel(int level)
	{
		if (!ENABLE_CATCH_UP_EXP || (level > CATCH_UP_MAX_LEVEL))
		{
			return 1.0;
		}

		if (level <= CATCH_UP_LOW_MAX_LEVEL)
		{
			return CATCH_UP_SP_MULTIPLIER_LOW;
		}
		if (level <= CATCH_UP_MID_MAX_LEVEL)
		{
			return CATCH_UP_SP_MULTIPLIER_MID;
		}
		return CATCH_UP_SP_MULTIPLIER;
	}

	public static double getRestedExpMultiplier(long lastAccess)
	{
		return isRested(lastAccess) ? RESTED_EXP_MULTIPLIER : 1.0;
	}

	public static double getRestedSpMultiplier(long lastAccess)
	{
		return isRested(lastAccess) ? RESTED_SP_MULTIPLIER : 1.0;
	}

	private static boolean isRested(long lastAccess)
	{
		if (!ENABLE_RESTED_BONUS || (lastAccess <= 0))
		{
			return false;
		}
		final long offlineSeconds = Math.max(0, (System.currentTimeMillis() / 1000) - lastAccess);
		return offlineSeconds >= (RESTED_MIN_OFFLINE_HOURS * 3600L);
	}

	public static String getBracketForLevel(int level)
	{
		if (!ENABLE_CATCH_UP_EXP || (level > CATCH_UP_MAX_LEVEL))
		{
			return "none";
		}
		if (level <= CATCH_UP_LOW_MAX_LEVEL)
		{
			return "low";
		}
		if (level <= CATCH_UP_MID_MAX_LEVEL)
		{
			return "mid";
		}
		return "high";
	}
}
