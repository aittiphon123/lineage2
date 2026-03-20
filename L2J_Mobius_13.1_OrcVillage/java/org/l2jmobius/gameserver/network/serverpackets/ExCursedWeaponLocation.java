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
				
				// Calculates how much FARM (Excess) there is
				// If the total reward is 5,000,027,000, the farm is 27,000.
				long currentFarm = w.reward - 5_000_000_000L;
				if (currentFarm < 0)
				{
					currentFarm = 0;
				}
				
				// Sending order to the client
				// The client fills: [Guaranteed] then [Bonus]
				
				// A) First Long: Goes to "Guaranteed Reward"
				buffer.writeLong(5_000_000_000L);
				// B) Second Long: Goes to "Bonus Reward"
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
		public long reward; // Variable to store Adena
		
		// Constructor to receive Adena
		public CursedWeaponInfo(Location p, int cwId, int status, long r)
		{
			pos = p;
			id = cwId;
			activated = status;
			reward = r;
		}
	}
}