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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * Format: (ch) d[ddddd] <br>
 * Updated to the latest version of the Cursed Sword - Prelude of War 2019
 * @author -Wooden-, Notorion
 */
public class ExCursedWeaponLocation extends ServerPacket
{
	private final List<CursedWeaponInfo> _cursedWeaponInfo;
	
	public ExCursedWeaponLocation(List<CursedWeaponInfo> cursedWeaponInfo)
	{
		_cursedWeaponInfo = cursedWeaponInfo;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_EXISTING_CURSED_WEAPON_LOCATION.writeId(this, buffer);
		if (!_cursedWeaponInfo.isEmpty())
		{
			buffer.writeInt(_cursedWeaponInfo.size());
			for (CursedWeaponInfo w : _cursedWeaponInfo)
			{
				buffer.writeInt(w.id);
				buffer.writeInt(w.activated);
				buffer.writeInt(w.pos.getX());
				buffer.writeInt(w.pos.getY());
				buffer.writeInt(w.pos.getZ());
				
				// Calculates how much FARM (Excess) there is.
				// If the total reward is 5,000,027,000, the farm is 27,000.
				final long currentFarm = Math.max(0, w.reward - 5_000_000_000L);
				
				// Sending order to the client.
				// The client fills: [Guaranteed] then [Bonus].
				
				// A) First Long: Goes to "Guaranteed Reward".
				buffer.writeLong(5_000_000_000L);
				
				// B) Second Long: Goes to "Bonus Reward".
				buffer.writeLong(currentFarm);
			}
		}
		else
		{
			buffer.writeInt(0);
		}
	}
	
	public static class CursedWeaponInfo
	{
		public Location pos;
		public int id;
		public int activated;
		public long reward; // Variable to store Adena.
		
		// Constructor to receive Adena.
		public CursedWeaponInfo(Location p, int cwId, int status, long r)
		{
			pos = p;
			id = cwId;
			activated = status;
			reward = r;
		}
	}
}
