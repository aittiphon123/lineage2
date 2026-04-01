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

import java.util.Collection;
import java.util.Collections;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Lonely
 */
public class ExLevelAmbienceChangedInfo extends ServerPacket
{
	private final int _packetType;
	private final Collection<Integer> _changedLevelAmbienceList;
	
	public ExLevelAmbienceChangedInfo(int packetType, int changedLevelAmbienceId)
	{
		_packetType = packetType;
		_changedLevelAmbienceList = Collections.singleton(changedLevelAmbienceId);
	}
	
	public ExLevelAmbienceChangedInfo(int packetType, Collection<Integer> changedLevelAmbienceList)
	{
		_packetType = packetType;
		_changedLevelAmbienceList = changedLevelAmbienceList;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_LEVELAMBIENCE_CHANGEDINFO.writeId(this, buffer);
		buffer.writeInt(_packetType);
		buffer.writeInt(_changedLevelAmbienceList.size());
		for (int levelAmbienceId : _changedLevelAmbienceList)
		{
			buffer.writeInt(levelAmbienceId); // ID from LevelAmbience.dat in the client.
		}
	}
}
