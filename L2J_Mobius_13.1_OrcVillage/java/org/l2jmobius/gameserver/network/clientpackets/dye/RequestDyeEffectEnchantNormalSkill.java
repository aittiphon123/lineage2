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
package org.l2jmobius.gameserver.network.clientpackets.dye;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.config.RatesConfig;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.AccountVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.dye.DyeEffectEnchantNormalSkill;

/**
 * @author CostyKiller, Mobius
 */
public class RequestDyeEffectEnchantNormalSkill extends ClientPacket
{
	private int _category;
	private int _slotId;
	
	@Override
	protected void readImpl()
	{
		_category = readInt();
		_slotId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		int slotLevel = player.getAccountVariables().getInt(AccountVariables.DYE_LEVEL_FOR_SLOT_ + _slotId, 0);
		int challengeCount = player.getAccountVariables().getInt(AccountVariables.DYE_CHALLENGE_COUNT_FOR_SLOT_ + _slotId, 30);
		boolean success = false;
		int skillId = 0;
		int skillLevel = 0;
		
		// Determine skill ID based on slot.
		switch (_slotId)
		{
			case 1:
			{
				skillId = 37073; // Seal - Giant's Power.
				break;
			}
			case 2:
			{
				skillId = 37074; // Seal - Giant's Wisdom.
				break;
			}
			case 3:
			{
				skillId = 37075; // Seal - Giant's Might.
				break;
			}
		}
		
		skillLevel = slotLevel == 0 ? 1 : slotLevel + 1;
		
		// Process skill enchant.
		final Item destoyedItem = player.getInventory().getItemByItemId(RatesConfig.DYE_ENCHANT_NORMAL_SKILL_ITEM_ID);
		if ((destoyedItem != null) && (destoyedItem.getCount() >= RatesConfig.DYE_ENCHANT_NORMAL_SKILL_ITEM_COUNT))
		{
			if (Rnd.get(100) < RatesConfig.DYE_ENCHANT_NORMAL_SKILL_CHANCE)
			{
				success = true;
				slotLevel = slotLevel + 1;
				player.getAccountVariables().set(AccountVariables.DYE_LEVEL_FOR_SLOT_ + _slotId, slotLevel);
				
				// Add skill.
				final Skill dyeSkill = SkillData.getInstance().getSkill(skillId, skillLevel);
				player.addSkill(dyeSkill, true);
			}
			
			player.getInventory().destroyItem(ItemProcessType.FEE, destoyedItem, RatesConfig.DYE_ENCHANT_NORMAL_SKILL_ITEM_COUNT, player, true);
			
			// Send inventory update packet.
			final InventoryUpdate playerIU = new InventoryUpdate();
			if (destoyedItem.isStackable() && (destoyedItem.getCount() > 0))
			{
				playerIU.addModifiedItem(destoyedItem);
			}
			else
			{
				playerIU.addRemovedItem(destoyedItem);
			}
			player.sendPacket(playerIU);
			
			// Send message to client.
			if (RatesConfig.DYE_ENCHANT_HIDDEN_SKILL_ITEM_COUNT > 1)
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_X_S2_DISAPPEARED);
				sm.addItemName(destoyedItem);
				sm.addLong(RatesConfig.DYE_ENCHANT_HIDDEN_SKILL_ITEM_COUNT);
				player.sendPacket(sm);
			}
			else
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
				sm.addItemName(destoyedItem);
				player.sendPacket(sm);
			}
		}
		
		// Update challenge count.
		challengeCount = challengeCount - 1;
		player.getAccountVariables().set(AccountVariables.DYE_CHALLENGE_COUNT_FOR_SLOT_ + _slotId, challengeCount);
		
		player.sendPacket(new DyeEffectEnchantNormalSkill(_category, _slotId, success, slotLevel, skillId, skillLevel, challengeCount));
	}
}
