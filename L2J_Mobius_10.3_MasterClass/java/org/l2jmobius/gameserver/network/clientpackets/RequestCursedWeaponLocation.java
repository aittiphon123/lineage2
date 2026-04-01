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

import java.util.LinkedList;
import java.util.List;

import org.l2jmobius.gameserver.managers.CursedWeaponsManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.CursedWeapon;
import org.l2jmobius.gameserver.network.serverpackets.ExActivatedCursedTreasureBoxLocation;
import org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation;
import org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;

/**
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