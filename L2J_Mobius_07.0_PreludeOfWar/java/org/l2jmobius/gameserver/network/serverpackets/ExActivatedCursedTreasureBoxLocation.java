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

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * Version Prelude of War 2019. <br>
 * Package responsible for displaying the icon of Treasure Chest NPCs on the Map (M). <br>
 * Related to the Cursed Weapons Defense event (Zariche/Akamanah).
 * @author Notorion
 */
public class ExActivatedCursedTreasureBoxLocation extends ServerPacket
{
	private final int _npcId;
	private final List<Location> _locs;
	
	/**
	 * Constructor to display the location of a single Treasure Chest.
	 * @param npcId The NPC ID representing the chest (e.g., 24370 or 24371).
	 * @param loc The physical location (X, Y, Z) in the world.
	 */
	public ExActivatedCursedTreasureBoxLocation(int npcId, Location loc)
	{
		_npcId = npcId;
		_locs = new ArrayList<>();
		if (loc != null)
		{
			_locs.add(loc);
		}
	}
	
	/**
	 * Constructor to display a LIST simultaneously. Used to update the map of all players with all active NPCs.
	 * @param npcId The NPC ID.
	 * @param locs List of locations (Location).
	 */
	public ExActivatedCursedTreasureBoxLocation(int npcId, List<Location> locs)
	{
		_npcId = npcId;
		_locs = locs;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		// Writes the packet ID so the client can identify that these are the locations
		ServerPackets.EX_ACTIVATED_CURSED_TREASURE.writeId(this, buffer);
		
		if ((_locs != null) && !_locs.isEmpty())
		{
			// Sends the amount to be displayed on the map
			buffer.writeInt(_locs.size());
			for (Location loc : _locs)
			{
				// For each Treasure Chest, sends the NPC ID and coordinates
				buffer.writeInt(_npcId);
				buffer.writeInt(loc.getX());
				buffer.writeInt(loc.getY());
				buffer.writeInt(loc.getZ());
			}
		}
		else
		{
			// If the list is empty, sends 0 to clear the icons from the client’s map
			buffer.writeInt(0);
		}
	}
}