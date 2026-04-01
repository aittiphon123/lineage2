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
package org.l2jmobius.gameserver.network.serverpackets.dye;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller, Mobius
 */
public class DyeEffectAcquireHiddenSkill extends ServerPacket
{
	private final int _category;
	private final int _slotId;
	private final boolean _success;
	private final int _hiddenSkillId;
	private final int _hiddenSkillLevel;
	
	public DyeEffectAcquireHiddenSkill(int category, int slotId, boolean success, int hiddenSkillId, int hiddenSkillLevel)
	{
		_category = category;
		_slotId = slotId;
		_success = success;
		_hiddenSkillId = hiddenSkillId;
		_hiddenSkillLevel = hiddenSkillLevel;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_DYEEFFECT_ACQUIRE_HIDDENSKILL.writeId(this, buffer);
		buffer.writeInt(_success);
		buffer.writeInt(_category);
		buffer.writeInt(_slotId);
		buffer.writeInt(_hiddenSkillId);
		buffer.writeInt(_hiddenSkillLevel);
	}
}
