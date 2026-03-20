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

import org.l2jmobius.gameserver.managers.CursedWeaponsManager;
// import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.serverpackets.ExActivatedCursedTreasureBoxLocation;

/**
 * Updated for Prelude of War version 2019
 * @author Notorion
 */
public class RequestExActivatedCursedTreasureBoxLocation extends ClientPacket
{
	@Override
	protected void readImpl()
	{
		// Empty packet, used only as a trigger (Request)
	}
	
	@Override
	protected void runImpl()
	{
		// PacketLogger.info("Client requested Treasure Box Location (0x174)");
		
		// Zariche
		if (!CursedWeaponsManager.getInstance().getZaricheBoxLocs().isEmpty())
		{
			// ID Npc Treasure Chest (24370)
			int npcId = CursedWeaponsManager.ZARICHE_BOX_NPC_ID;
			getPlayer().sendPacket(new ExActivatedCursedTreasureBoxLocation(npcId, CursedWeaponsManager.getInstance().getZaricheBoxLocs()));
		}
		
		// Akamanah
		if (!CursedWeaponsManager.getInstance().getAkamanahBoxLocs().isEmpty())
		{
			// ID Npc Treasure Chest (24371)
			int npcId = CursedWeaponsManager.AKAMANAH_BOX_NPC_ID;
			getPlayer().sendPacket(new ExActivatedCursedTreasureBoxLocation(npcId, CursedWeaponsManager.getInstance().getAkamanahBoxLocs()));
		}
	}
}