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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class CatchUpExpConfig
{
	private static final java.lang.String CATCH_UP_EXP_CONFIG_FILE = "./config/Custom/CatchUpExp.ini";

	public static boolean ENABLE_CATCH_UP_EXP;
	public static int CATCH_UP_MAX_LEVEL;
	public static double CATCH_UP_EXP_MULTIPLIER;
	public static double CATCH_UP_SP_MULTIPLIER;
	public static int CATCH_UP_LOW_MAX_LEVEL;
	public static double CATCH_UP_EXP_MULTIPLIER_LOW;
	public static double CATCH_UP_SP_MULTIPLIER_LOW;
	public static int CATCH_UP_MID_MAX_LEVEL;
	public static double CATCH_UP_EXP_MULTIPLIER_MID;
	public static double CATCH_UP_SP_MULTIPLIER_MID;

	public static boolean ENABLE_RESTED_BONUS;
	public static int RESTED_MIN_OFFLINE_HOURS;
	public static double RESTED_EXP_MULTIPLIER;
	public static double RESTED_SP_MULTIPLIER;

	public static void load()
	{
		final Properties properties = new Properties();
		try (InputStream input = new FileInputStream(CATCH_UP_EXP_CONFIG_FILE))
		{
			properties.load(input);
		}
		catch (Exception e)
		{
			// Keep defaults if file is missing or malformed.
		}

		ENABLE_CATCH_UP_EXP = getBoolean(properties, "EnableCatchUpExp", false);
		CATCH_UP_MAX_LEVEL = getInt(properties, "CatchUpMaxLevel", 76);
		CATCH_UP_EXP_MULTIPLIER = getDouble(properties, "CatchUpExpMultiplier", 1.20);
		CATCH_UP_SP_MULTIPLIER = getDouble(properties, "CatchUpSpMultiplier", 1.10);
		CATCH_UP_LOW_MAX_LEVEL = getInt(properties, "CatchUpLowMaxLevel", 40);
		CATCH_UP_EXP_MULTIPLIER_LOW = getDouble(properties, "CatchUpExpMultiplierLow", 1.40);
		CATCH_UP_SP_MULTIPLIER_LOW = getDouble(properties, "CatchUpSpMultiplierLow", 1.25);
		CATCH_UP_MID_MAX_LEVEL = getInt(properties, "CatchUpMidMaxLevel", 61);
		CATCH_UP_EXP_MULTIPLIER_MID = getDouble(properties, "CatchUpExpMultiplierMid", 1.25);
		CATCH_UP_SP_MULTIPLIER_MID = getDouble(properties, "CatchUpSpMultiplierMid", 1.15);

		ENABLE_RESTED_BONUS = getBoolean(properties, "EnableRestedBonus", false);
		RESTED_MIN_OFFLINE_HOURS = getInt(properties, "RestedMinOfflineHours", 8);
		RESTED_EXP_MULTIPLIER = getDouble(properties, "RestedExpMultiplier", 1.10);
		RESTED_SP_MULTIPLIER = getDouble(properties, "RestedSpMultiplier", 1.05);

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

	private static boolean getBoolean(Properties properties, java.lang.String key, boolean defaultValue)
	{
		final java.lang.String value = properties.getProperty(key);
		return (value == null) ? defaultValue : "true".equalsIgnoreCase(value.trim());
	}

	private static int getInt(Properties properties, java.lang.String key, int defaultValue)
	{
		final java.lang.String value = properties.getProperty(key);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Integer.parseInt(value.trim());
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	private static double getDouble(Properties properties, java.lang.String key, double defaultValue)
	{
		final java.lang.String value = properties.getProperty(key);
		if (value == null)
		{
			return defaultValue;
		}
		try
		{
			return Double.parseDouble(value.trim());
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	public static double getExpMultiplierForLevel(int level)
	{
		if (!ENABLE_CATCH_UP_EXP || (level < 1) || (level > CATCH_UP_MAX_LEVEL))
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
		if (!ENABLE_CATCH_UP_EXP || (level < 1) || (level > CATCH_UP_MAX_LEVEL))
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

	public static boolean isRestedBonusActive(long lastAccess)
	{
		return isRested(lastAccess);
	}

	public static long getOfflineSeconds(long lastAccess)
	{
		if (lastAccess <= 0)
		{
			return 0;
		}
		return Math.max(0, (System.currentTimeMillis() / 1000) - lastAccess);
	}

	private static boolean isRested(long lastAccess)
	{
		if (!ENABLE_RESTED_BONUS || (lastAccess <= 0))
		{
			return false;
		}
		return getOfflineSeconds(lastAccess) >= (RESTED_MIN_OFFLINE_HOURS * 3600L);
	}

	public static java.lang.String getBracketForLevel(int level)
	{
		if (!ENABLE_CATCH_UP_EXP || (level < 1) || (level > CATCH_UP_MAX_LEVEL))
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
