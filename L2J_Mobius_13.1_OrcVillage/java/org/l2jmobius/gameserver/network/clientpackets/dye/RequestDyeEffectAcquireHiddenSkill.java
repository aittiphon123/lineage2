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
import org.l2jmobius.gameserver.network.serverpackets.dye.DyeEffectAcquireHiddenSkill;

/**
 * @author CostyKiller, Mobius
 */
public class RequestDyeEffectAcquireHiddenSkill extends ClientPacket
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
		
		final int slotLevel = player.getAccountVariables().getInt(AccountVariables.DYE_LEVEL_FOR_SLOT_ + _slotId, 1);
		boolean success = false;
		int hiddenSkillId = 0;
		int hiddenSkillLevel = 0;
		
		// Determine skill ID based on slot
		switch (_slotId)
		{
			case 1:
			{
				hiddenSkillId = 37066; // Seal Heritage - Giant's Power.
				break;
			}
			case 2:
			{
				hiddenSkillId = 37067; // Seal Heritage - Giant's Wisdom.
				break;
			}
			case 3:
			{
				hiddenSkillId = 37068; // Seal Heritage - Giant's Might.
				break;
			}
		}
		
		// Determine skill level based on slot level.
		switch (slotLevel)
		{
			case 26:
			case 27:
			{
				hiddenSkillLevel = 1;
				break;
			}
			case 28:
			case 29:
			case 30:
			{
				hiddenSkillLevel = 2;
				break;
			}
		}
		
		// Process skill acquisition.
		if (slotLevel >= 26)
		{
			final Item destoyedItem = player.getInventory().getItemByItemId(RatesConfig.DYE_ENCHANT_NORMAL_SKILL_ITEM_ID);
			if ((destoyedItem != null) && (destoyedItem.getCount() >= RatesConfig.DYE_ENCHANT_HIDDEN_SKILL_ITEM_COUNT))
			{
				if (Rnd.get(100) < RatesConfig.DYE_ENCHANT_HIDDEN_SKILL_CHANCE)
				{
					success = true;
					player.getAccountVariables().set(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + _slotId, hiddenSkillLevel);
					
					// Add skill.
					final Skill dyeHiddenSkill = SkillData.getInstance().getSkill(hiddenSkillId, hiddenSkillLevel);
					player.addSkill(dyeHiddenSkill, true);
					
					// Check hidden skills.
					final int hiddenPowerSkillLevel = player.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + 1, 0);
					final int hiddenWisdomSkillLevel = player.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + 2, 0);
					final int hiddenMightSkillLevel = player.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + 3, 0);
					final int hiddenSealBuffLevel = hiddenPowerSkillLevel + hiddenWisdomSkillLevel + hiddenMightSkillLevel;
					
					// Add buff skill.
					final Skill hiddenSealBuffSkill = SkillData.getInstance().getSkill(37072 /* Giant Seal Buff */, hiddenSealBuffLevel);
					player.addSkill(hiddenSealBuffSkill, true);
				}
				
				player.getInventory().destroyItem(ItemProcessType.FEE, destoyedItem, RatesConfig.DYE_ENCHANT_HIDDEN_SKILL_ITEM_COUNT, player, true);
				
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
		}
		
		player.sendPacket(new DyeEffectAcquireHiddenSkill(_category, _slotId, success, hiddenSkillId, hiddenSkillLevel));
	}
}
