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
package ai.bosses.MightyChaosGolem;

import java.util.Calendar;

import org.l2jmobius.commons.time.TimeUtil;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;

/**
 * @author Tanatos
 */
public class MightyChaosGolem extends Script
{
	private static final int MIGHTY_CHAOS_GOLEM = 29436;
	private static final Location[] MIGHTY_CHAOS_GOLEM_SPAWN_LOC =
	{
		new Location(116942, -149931, -2328),
		new Location(119666, -162360, -1336),
		new Location(103981, -155303, -1800),
		new Location(101943, -148981, -3152),
		new Location(107592, -159375, -1800)
	};
	private static final long DESPAWN_DELAY = 1_200_000;
	
	private static final SpawnEvent[] SPAWN_EVENTS =
	{
		new SpawnEvent("spawnFirstGolem", 13, 0),
		new SpawnEvent("spawnSecondGolem", 16, 0),
		new SpawnEvent("spawnThirdGolem", 20, 0)
	};
	
	private int _location;
	
	private MightyChaosGolem()
	{
		for (SpawnEvent event : SPAWN_EVENTS)
		{
			scheduleSpawn(event);
		}
		
		LOGGER.info("Mighty Chaos Golem will spawn at 13:00, 16:00 and 20:00.");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		for (SpawnEvent spawnEvent : SPAWN_EVENTS)
		{
			if (spawnEvent.name.equals(event))
			{
				_location = getRandom(4);
				addSpawn(MIGHTY_CHAOS_GOLEM, MIGHTY_CHAOS_GOLEM_SPAWN_LOC[_location].getX(), MIGHTY_CHAOS_GOLEM_SPAWN_LOC[_location].getY(), MIGHTY_CHAOS_GOLEM_SPAWN_LOC[_location].getZ(), 0, false, DESPAWN_DELAY, false);
				
				scheduleSpawn(spawnEvent);
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	private void scheduleSpawn(SpawnEvent spawnEvent)
	{
		final Calendar time = TimeUtil.getNextTime(spawnEvent.hour, spawnEvent.minute);
		startQuestTimer(spawnEvent.name, time.getTimeInMillis() - System.currentTimeMillis(), null, null);
	}
	
	private static class SpawnEvent
	{
		String name;
		int hour;
		int minute;
		
		SpawnEvent(String name, int hour, int minute)
		{
			this.name = name;
			this.hour = hour;
			this.minute = minute;
		}
	}
	
	public static void main(String[] args)
	{
		new MightyChaosGolem();
	}
}
