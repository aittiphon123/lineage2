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
package org.l2jmobius.gameserver.network.clientpackets.characterstyle;

import org.l2jmobius.gameserver.data.enums.CharacterStyleCategoryType;
import org.l2jmobius.gameserver.data.holders.CharacterStyleDataHolder;
import org.l2jmobius.gameserver.data.xml.CharacterStylesData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.characterstyle.ExCharacterStyleRegister;

/**
 * @author Brado
 */
public class ExRequestCharacterStyleRegister extends ClientPacket
{
	private int _styleType;
	private int _styleId;
	
	@Override
	protected void readImpl()
	{
		_styleType = readInt();
		_styleId = readInt();
		readInt(); // _costItemId
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final CharacterStyleCategoryType category = CharacterStyleCategoryType.getByClientId(_styleType);
		final CharacterStyleDataHolder style = CharacterStylesData.getInstance().getSpecificStyleByCategoryAndId(category, _styleId);
		if ((category == null) || (style == null))
		{
			player.sendPacket(ExCharacterStyleRegister.STATIC_PACKET_FAIL);
			return;
		}
		
		for (ItemHolder price : style._cost) // Inventory check for all items before actual item deduction.
		{
			// If there is implementation of selection of item to pay for style, remove the for and apply only selected item check.
			// if (price.getId() != _costItemId)
			// {
			// player.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS));
			// return;
			// }
			
			if (player.getInventory().getInventoryItemCount(price.getId(), -1) < price.getCount())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS));
				player.sendPacket(ExCharacterStyleRegister.STATIC_PACKET_FAIL);
				return;
			}
		}
		
		for (ItemHolder price : style._cost) // Actual item deduction
		{
			// If there is implementation of selection of item to pay for style, remove the for and apply only selected item check.
			// if (price.getId() != _costItemId)
			// {
			// player.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS));
			// return;
			// }
			
			if (player.getInventory().destroyItemByItemId(ItemProcessType.DESTROY, price.getId(), price.getCount(), player, null) == null)
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS));
				player.sendPacket(ExCharacterStyleRegister.STATIC_PACKET_FAIL);
				return;
			}
		}
		
		player.modifyCharacterStyle(category, _styleId, false, true);
		player.sendPacket(ExCharacterStyleRegister.STATIC_PACKET_SUCCESS);
	}
}
