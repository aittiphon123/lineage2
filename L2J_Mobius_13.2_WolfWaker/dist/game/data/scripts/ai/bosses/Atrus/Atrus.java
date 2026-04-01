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
package ai.bosses.Atrus;

import java.util.Calendar;

import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;

/**
 * @author Tanatos
 */
public class Atrus extends Script
{
	private static final int ATRUS = 29433;
	private static final SkillHolder CHAOS_POWER = new SkillHolder(62051, 1);
	private static final Location ATRUS_SPAWN_LOC = new Location(-24586, -109012, -3000, 56219);
	private static final int[] ATRUS_TIME =
	{
		21,
		00
	};
	private static final long DESPAWN_DELAY = 1_200_000;
	private Npc activeAtrus = null;
	
	private Atrus()
	{
		addSpawnId(ATRUS);
		addKillId(ATRUS);
		scheduleAtrus();
		LOGGER.info("Atrus will spawn every Tuesday and Thursday at " + ATRUS_TIME[0] + ":" + ATRUS_TIME[1] + ".");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "spawnAtrus":
			{
				activeAtrus = addSpawn(ATRUS, ATRUS_SPAWN_LOC.getX(), ATRUS_SPAWN_LOC.getY(), ATRUS_SPAWN_LOC.getZ(), ATRUS_SPAWN_LOC.getHeading(), false, 0, false);
				startQuestTimer("despawnAtrus", DESPAWN_DELAY, activeAtrus, null);
				scheduleAtrus();
				break;
			}
			case "despawnAtrus":
			{
				if ((npc != null) && !npc.isDead())
				{
					World.getInstance().getPlayers().forEach(p -> showOnScreenMsg(p, NpcStringId.KASHA_S_AVATAR_ATRUS_HAS_DISAPPEARED_ALONG_WITH_THE_CHAOS_VORTEX, 2, 10000, true));
					npc.deleteMe();
					activeAtrus = null;
				}
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		CHAOS_POWER.getSkill().applyEffects(npc, npc);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isPet)
	{
		World.getInstance().getPlayers().forEach(p -> showOnScreenMsg(p, NpcStringId.YOU_HAVE_DEFEATED_KASHA_S_AVATAR_ATRUS_AND_QUELLED_THE_CHAOS_POWER, 2, 10000, true));
		activeAtrus = null;
	}
	
	private void scheduleAtrus()
	{
		final Calendar now = Calendar.getInstance();
		final int day = now.get(Calendar.DAY_OF_WEEK);
		final int hour = now.get(Calendar.HOUR_OF_DAY);
		final int minute = now.get(Calendar.MINUTE);
		
		final int daysToAdd;
		switch (day)
		{
			case Calendar.TUESDAY:
			{
				daysToAdd = ((hour < ATRUS_TIME[0]) || ((hour == ATRUS_TIME[0]) && (minute < ATRUS_TIME[1]))) ? 0 : 2;
				break;
			}
			case Calendar.THURSDAY:
			{
				daysToAdd = ((hour < ATRUS_TIME[0]) || ((hour == ATRUS_TIME[0]) && (minute < ATRUS_TIME[1]))) ? 0 : 5;
				break;
			}
			case Calendar.WEDNESDAY:
			{
				daysToAdd = 1;
				break;
			}
			case Calendar.FRIDAY:
			{
				daysToAdd = 4;
				break;
			}
			case Calendar.SATURDAY:
			{
				daysToAdd = 3;
				break;
			}
			case Calendar.SUNDAY:
			{
				daysToAdd = 2;
				break;
			}
			default: // Monday or fallback
			{
				daysToAdd = 1;
				break;
			}
		}
		
		now.add(Calendar.DAY_OF_YEAR, daysToAdd);
		now.set(Calendar.HOUR_OF_DAY, ATRUS_TIME[0]);
		now.set(Calendar.MINUTE, ATRUS_TIME[1]);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		startQuestTimer("spawnAtrus", now.getTimeInMillis() - System.currentTimeMillis(), null, null);
	}
	
	public static void main(String[] args)
	{
		new Atrus();
	}
}
