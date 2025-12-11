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

import java.util.concurrent.TimeUnit;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanAccess;
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestOustPledgeMember extends ClientPacket
{
	private String _target;
	
	@Override
	protected void readImpl()
	{
		_target = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.getClan() == null)
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
			return;
		}
		
		if (!player.hasAccess(ClanAccess.REMOVE_MEMBER))
		{
			player.sendMessage("You are not authorized to do that.");
			return;
		}
		
		if (player.getName().equalsIgnoreCase(_target))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_EXPEL_YOURSELF);
			return;
		}
		
		final Clan clan = player.getClan();
		final ClanMember member = clan.getClanMember(_target);
		if (member == null)
		{
			PacketLogger.warning("Target (" + _target + ") is not member of the clan");
			return;
		}
		
		if (member.isOnline() && member.getPlayer().isInCombat())
		{
			player.sendMessage("A clan member may not be dismissed during combat.");
			return;
		}
		
		// this also updates the database
		clan.removeClanMember(member.getObjectId(), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(PlayerConfig.ALT_CLAN_JOIN_DAYS));
		clan.setCharPenaltyExpiryTime(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(PlayerConfig.ALT_CLAN_JOIN_DAYS));
		clan.updateClanInDB();
		
		final SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_MEMBER_S1_HAS_BEEN_EXPELLED);
		sm.addString(member.getName());
		clan.broadcastToOnlineMembers(sm);
		player.sendPacket(SystemMessageId.SUCCEEDED_IN_EXPELLING_A_CLAN_MEMBER);
		player.sendPacket(SystemMessageId.A_NEW_MEMBER_CANNOT_JOIN_WITHIN_5_DAYS_OF_A_CLAN_MEMBER_S_EXPULSION);
		
		// Remove the Player From the Member list
		clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(_target));
		if (member.isOnline())
		{
			final Player target = member.getPlayer();
			target.sendPacket(SystemMessageId.MEMBERSHIP_TO_THIS_CLAN_HAS_BEEN_TERMINATED);
		}
	}
}
