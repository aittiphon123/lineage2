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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanAccess;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

public class RequestStartPledgeWar extends ClientPacket
{
	private String _pledgeName;
	
	@Override
	protected void readImpl()
	{
		_pledgeName = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Clan playerClan = player.getClan();
		if (playerClan == null)
		{
			return;
		}
		
		if ((playerClan.getLevel() < 3) || (playerClan.getMembersCount() < PlayerConfig.ALT_CLAN_MEMBERS_FOR_WAR))
		{
			player.sendMessage("A Clan War can be declared only if the clan is level three or above, and the number of clan members is fifteen or greater.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if (!player.hasAccess(ClanAccess.WAR_DECLARATION))
		{
			player.sendMessage("You are not authorized to do that.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);
		if (clan == null)
		{
			player.sendMessage("A Clan War cannot be declared because that clan does not exist!");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if ((playerClan.getAllyId() == clan.getAllyId()) && (playerClan.getAllyId() != 0))
		{
			player.sendMessage("A declaration of Clan War against an allied clan can't be made.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if ((clan.getLevel() < 3) || (clan.getMembersCount() < PlayerConfig.ALT_CLAN_MEMBERS_FOR_WAR))
		{
			player.sendMessage("A Clan War can be declared only if the clan is level three or above, and the number of clan members is fifteen or greater.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if (playerClan.isAtWarWith(clan.getId()))
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN);
			sm.addString(clan.getName());
			player.sendPacket(sm);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		ClanTable.getInstance().storeClanWars(player.getClanId(), clan.getId());
		for (Player member : playerClan.getOnlineMembers(0))
		{
			member.broadcastUserInfo();
		}
		
		for (Player member : clan.getOnlineMembers(0))
		{
			member.broadcastUserInfo();
		}
	}
}
