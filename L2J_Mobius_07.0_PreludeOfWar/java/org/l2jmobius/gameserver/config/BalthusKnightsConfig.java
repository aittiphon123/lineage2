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
package org.l2jmobius.gameserver.config;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.util.ConfigReader;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;

/**
 * This class loads all the Balthus knights related configurations.
 * @author Mobius
 */
public class BalthusKnightsConfig
{
	// File
	private static final String BALTHUS_KNIGHTS_CONFIG_FILE = "./config/BalthusKnights.ini";
	
	// Constants
	public static boolean BALTHUS_KNIGHTS_ENABLED;
	public static int BALTHUS_KNIGHTS_LEVEL;
	public static boolean BALTHUS_KNIGHTS_PREMIUM;
	public static Location BALTHUS_KNIGHTS_LOCATION;
	public static List<ItemHolder> BALTHUS_KNIGHTS_REWARDS = new ArrayList<>();
	public static boolean BALTHUS_KNIGHTS_REWARD_SKILLS;
	
	public static void load()
	{
		final ConfigReader config = new ConfigReader(BALTHUS_KNIGHTS_CONFIG_FILE);
		BALTHUS_KNIGHTS_ENABLED = config.getBoolean("BalthusKnightsEnabled", true);
		BALTHUS_KNIGHTS_LEVEL = config.getInt("BalthusKnightsLevel", 85);
		BALTHUS_KNIGHTS_PREMIUM = config.getBoolean("BalthusKnightsPremium", true);
		
		final String[] balthusKnightsLocation = config.getString("BalthusKnightsLocation", "-114371,256483,-1286").split(",");
		BALTHUS_KNIGHTS_LOCATION = new Location(Integer.parseInt(balthusKnightsLocation[0]), Integer.parseInt(balthusKnightsLocation[1]), Integer.parseInt(balthusKnightsLocation[2]));
		
		BALTHUS_KNIGHTS_REWARDS.clear();
		for (String s : config.getString("BalthusKnightsRewards", "46919;1").split(","))
		{
			if (s.isEmpty())
			{
				continue;
			}
			
			BALTHUS_KNIGHTS_REWARDS.add(new ItemHolder(Integer.parseInt(s.split(";")[0]), Integer.parseInt(s.split(";")[1])));
		}
		
		BALTHUS_KNIGHTS_REWARD_SKILLS = config.getBoolean("BalthusKnightsRewardSkills", true);
	}
}
