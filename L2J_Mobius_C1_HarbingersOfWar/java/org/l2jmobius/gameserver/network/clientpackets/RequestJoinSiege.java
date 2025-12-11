/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.gameserver.managers.CHSiegeManager;
import org.l2jmobius.gameserver.managers.CastleManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanAccess;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.model.siege.clanhalls.SiegableHall;
import org.l2jmobius.gameserver.network.serverpackets.SiegeInfo;

/**
 * @author KenM
 */
public class RequestJoinSiege extends ClientPacket
{
	private int _castleId;
	private int _isAttacker;
	private int _isJoining;
	
	@Override
	protected void readImpl()
	{
		_castleId = readInt();
		_isAttacker = readInt();
		_isJoining = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!player.hasAccess(ClanAccess.CASTLE_SIEGE))
		{
			player.sendMessage("You are not authorized to do that.");
			return;
		}
		
		final Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}
		
		final Castle castle = CastleManager.getInstance().getCastleById(_castleId);
		if (castle != null)
		{
			if (_isJoining == 1)
			{
				if (System.currentTimeMillis() < clan.getDissolvingExpiryTime())
				{
					player.sendMessage("Your clan may not register to participate in a siege while under a grace period of the clan's dissolution.");
					return;
				}
				
				if (_isAttacker == 1)
				{
					castle.getSiege().registerAttacker(player);
				}
				else
				{
					castle.getSiege().registerDefender(player);
				}
			}
			else
			{
				castle.getSiege().removeSiegeClan(player);
			}
			
			castle.getSiege().listRegisterClan(player);
		}
		
		final SiegableHall hall = CHSiegeManager.getInstance().getSiegableHall(_castleId);
		if (hall != null)
		{
			if (_isJoining == 1)
			{
				if (System.currentTimeMillis() < clan.getDissolvingExpiryTime())
				{
					player.sendMessage("Your clan may not register to participate in a siege while under a grace period of the clan's dissolution.");
					return;
				}
				
				CHSiegeManager.getInstance().registerClan(clan, hall, player);
			}
			else
			{
				CHSiegeManager.getInstance().unRegisterClan(clan, hall);
			}
			
			player.sendPacket(new SiegeInfo(hall, player));
		}
	}
}
