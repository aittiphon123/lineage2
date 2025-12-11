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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.serverpackets.PartyMatchList;

/**
 * @author Mobius
 */
public class RequestPartyMatchList extends ClientPacket
{
	private int _status;
	
	@Override
	protected void readImpl()
	{
		_status = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_status == 1) // PartyMatch window has opened.
		{
			player.setPartyMatchingActive(true);
			final Set<Player> players = World.getInstance().getPlayers().stream() //
				// .filter(nearby -> nearby != player) // On retail you can see yourself in list.
				.filter(Player::isPartyMatchingActive) //
				.filter(nearby -> Math.abs(nearby.getLevel() - player.getLevel()) <= 15) //
				.sorted((p1, p2) -> Double.compare(player.calculateDistance3D(p1), player.calculateDistance3D(p2))) //
				.limit(40) //
				.collect(Collectors.toCollection(LinkedHashSet::new));
			player.sendPacket(new PartyMatchList(players));
			player.broadcastUserInfo();
		}
		else if (_status == 3) // PartyMatch window has closed.
		{
			player.setPartyMatchingActive(false);
			player.broadcastUserInfo();
		}
	}
}
