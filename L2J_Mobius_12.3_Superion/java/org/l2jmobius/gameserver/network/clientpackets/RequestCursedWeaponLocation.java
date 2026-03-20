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

import java.util.LinkedList;
import java.util.List;

import org.l2jmobius.gameserver.managers.CursedWeaponsManager;
import org.l2jmobius.gameserver.model.CursedWeapon;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.serverpackets.ExActivatedCursedTreasureBoxLocation;
import org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation;
import org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;

/**
 * Updated for Prelude of War version <br>
 * Format: (ch)
 * @author -Wooden-, Notorion
 */
public class RequestCursedWeaponLocation extends ClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.isInsideZone(org.l2jmobius.gameserver.model.zone.ZoneId.CONQUEST))
		{
			CursedWeaponsManager.getInstance().clearSinglePlayerScreen(player);
			return;
		}
		
		final List<CursedWeaponInfo> list = new LinkedList<>();
		for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			if (!cw.isActive())
			{
				continue;
			}
			
			final Location pos = cw.getWorldPosition();
			if (pos != null)
			{
				// Added cw.getCurrentReward() at the end
				list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0, cw.getCurrentReward()));
			}
		}
		
		// Send the ExCursedWeaponLocation
		if (!list.isEmpty())
		{
			player.sendPacket(new ExCursedWeaponLocation(list));
		}
		
		// Send ID
		// Check Zariche Box
		if (!CursedWeaponsManager.getInstance().getZaricheBoxLocs().isEmpty())
		{
			// Use ID 24370 (Chest)
			player.sendPacket(new ExActivatedCursedTreasureBoxLocation(24370, CursedWeaponsManager.getInstance().getZaricheBoxLocs()));
		}
		
		// Check Akamanah Box
		if (!CursedWeaponsManager.getInstance().getAkamanahBoxLocs().isEmpty())
		{
			// Use ID 24371 (Chest)
			player.sendPacket(new ExActivatedCursedTreasureBoxLocation(24371, CursedWeaponsManager.getInstance().getAkamanahBoxLocs()));
		}
	}
}