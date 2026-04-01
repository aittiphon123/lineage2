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
package events.ContinuedRaid;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.OnDailyReset;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.LongTimeEvent;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;

/**
 * @URL https://l2wiki.com/essence/events_and_promos/2040.html
 * @author Serenitty
 */
public class ContinuedRaid extends LongTimeEvent
{
	// NPC
	private static final int MAKHI = 34194;
	
	// Skills
	private static final SkillHolder BUFF_BAMIUM = new SkillHolder(48003, 1);
	private static final SkillHolder BUFF_BEHEMOTH = new SkillHolder(48004, 1);
	private static final SkillHolder BUFF_GLAKIAS = new SkillHolder(48005, 1);
	
	// Teleport
	private static final Location EVENT_LOCATION = new Location(43560, 206532, -3761);
	
	private static final int TRIAL_BAMIUM = 97784;
	private static final int TRIAL_BEHEMOTH = 97785;
	private static final int TRIAL_GLAKIAS = 97786;
	
	private ContinuedRaid()
	{
		addStartNpc(MAKHI);
		addTalkId(MAKHI);
		addFirstTalkId(MAKHI);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34194.htm":
			case "34194-1.htm":
			{
				htmltext = event;
				break;
			}
			case "TELE_TO_EVENT":
			{
				if ((npc != null) && (npc.getId() == MAKHI))
				{
					player.teleToLocation(EVENT_LOCATION);
				}
				break;
			}
			case "BUFF":
			{
				if ((npc != null) && (npc.getId() == MAKHI))
				{
					if (player.getInventory().getItemByItemId(TRIAL_BAMIUM) != null)
					{
						if (player.destroyItemByItemId(ItemProcessType.FEE, TRIAL_BAMIUM, 1, player, true))
						{
							SkillCaster.triggerCast(player, player, BUFF_BAMIUM.getSkill());
							
							World.getInstance().forEachVisibleObject(player, Player.class, eachPlayer ->
							{
								if (player.isInsideRadius3D(eachPlayer, 500))
								{
									SkillCaster.triggerCast(eachPlayer, eachPlayer, BUFF_BAMIUM.getSkill());
								}
							});
						}
					}
					else if (player.getInventory().getItemByItemId(TRIAL_BEHEMOTH) != null)
					{
						if (player.destroyItemByItemId(ItemProcessType.FEE, TRIAL_BEHEMOTH, 1, player, true))
						{
							SkillCaster.triggerCast(player, player, BUFF_BEHEMOTH.getSkill());
							
							World.getInstance().forEachVisibleObject(player, Player.class, eachPlayer ->
							{
								if (player.isInsideRadius3D(eachPlayer, 500))
								{
									SkillCaster.triggerCast(eachPlayer, eachPlayer, BUFF_BEHEMOTH.getSkill());
								}
							});
						}
					}
					else if (player.getInventory().getItemByItemId(TRIAL_GLAKIAS) != null)
					{
						if (player.destroyItemByItemId(ItemProcessType.FEE, TRIAL_GLAKIAS, 1, player, true))
						{
							SkillCaster.triggerCast(player, player, BUFF_GLAKIAS.getSkill());
							
							World.getInstance().forEachVisibleObject(player, Player.class, eachPlayer ->
							{
								if (player.isInsideRadius3D(eachPlayer, 500))
								{
									SkillCaster.triggerCast(eachPlayer, eachPlayer, BUFF_GLAKIAS.getSkill());
								}
							});
						}
					}
					else
					{
						player.sendMessage("You do not have the required item to receive a buff.");
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".htm";
	}
	
	@RegisterEvent(EventType.ON_DAILY_RESET)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void onDailyReset(OnDailyReset event)
	{
		if (!isEventPeriod())
		{
			return;
		}
		
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ? AND charId IN (SELECT charId FROM characters WHERE online = 0)"))
			{
				ps.setString(1, PlayerVariables.DAILY_CONTINUED_RAID_GET_REWARD);
				ps.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Could not reset variables: " + e.getMessage());
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getVariables().remove(PlayerVariables.DAILY_CONTINUED_RAID_GET_REWARD);
		}
		
		LOGGER.info(getClass().getSimpleName() + " has been reset.");
	}
	
	public static void main(String[] args)
	{
		new ContinuedRaid();
	}
}
