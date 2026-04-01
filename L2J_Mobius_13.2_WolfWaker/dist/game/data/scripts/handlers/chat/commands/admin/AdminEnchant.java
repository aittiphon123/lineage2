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
package handlers.chat.commands.admin;

import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.xml.EnchantItemGroupsData;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.handler.IAdminCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author CostyKiller
 */
public class AdminEnchant implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_seteh", // 6
		"admin_setec", // 10
		"admin_seteg", // 9
		"admin_setel", // 11
		"admin_seteb", // 12
		"admin_setew", // 7
		"admin_setes", // 8
		"admin_setle", // 1
		"admin_setre", // 2
		"admin_setlf", // 4
		"admin_setrf", // 5
		"admin_seten", // 3
		"admin_setun", // 0
		"admin_setba", // 13
		"admin_setbe", // belt
		"admin_seth1", // L Hair
		"admin_seth2", // R Hair
		"admin_setbr", // Brooch
		"admin_setbt", // L Bracelet
		"admin_setsb", // Seed (R) Bracelet
		"admin_setab", // Artifact Book
		"admin_seta1", // Agathion SLOT1
		"admin_seta2", // Agathion SLOT2
		"admin_seta3", // Agathion SLOT3
		"admin_seta4", // Agathion SLOT4
		"admin_seta5", // Agathion SLOT5
		"admin_set01", // Artifact (balance)
		"admin_set02", // Artifact (balance)
		"admin_set03", // Artifact (balance)
		"admin_set04", // Artifact (balance)
		"admin_set05", // Artifact (balance)
		"admin_set06", // Artifact (balance)
		"admin_set07", // Artifact (balance)
		"admin_set08", // Artifact (balance)
		"admin_set09", // Artifact (balance)
		"admin_set10", // Artifact (balance)
		"admin_set11", // Artifact (balance)
		"admin_set12", // Artifact (balance)
		"admin_set13", // Artifact (Attack)
		"admin_set14", // Artifact (Attack)
		"admin_set15", // Artifact (Attack)
		"admin_set16", // Artifact (Protection)
		"admin_set17", // Artifact (Protection)
		"admin_set18", // Artifact (Protection)
		"admin_set19", // Artifact (Support)
		"admin_set20", // Artifact (Support)
		"admin_set21", // Artifact (Support)
		"admin_sett1", // Talisman SLOT1
		"admin_sett2", // Talisman SLOT2
		"admin_sett3", // Talisman SLOT3
		"admin_sett4", // Talisman SLOT4
		"admin_sett5", // Talisman SLOT5
		"admin_sett6", // Talisman SLOT6
		"admin_artifact",
		"admin_agathion",
		"admin_talisman",
		"admin_enchant"
	};
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		int currentPage = 0;
		if (command.equals("admin_enchant"))
		{
			currentPage = 1;
			showMainPage(activeChar, currentPage);
		}
		else if (command.equals("admin_artifact"))
		{
			currentPage = 2;
			showMainPage(activeChar, currentPage);
		}
		else if (command.equals("admin_agathion"))
		{
			currentPage = 3;
			showMainPage(activeChar, currentPage);
		}
		else if (command.equals("admin_talisman"))
		{
			currentPage = 4;
			showMainPage(activeChar, currentPage);
		}
		else
		{
			int slot = -1;
			int pageToShow = 1; // default to main enchant page
			
			if (command.startsWith("admin_seteh"))
			{
				slot = Inventory.PAPERDOLL_HEAD;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setec"))
			{
				slot = Inventory.PAPERDOLL_CHEST;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_seteg"))
			{
				slot = Inventory.PAPERDOLL_GLOVES;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_seteb"))
			{
				slot = Inventory.PAPERDOLL_FEET;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setel"))
			{
				slot = Inventory.PAPERDOLL_LEGS;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setew"))
			{
				slot = Inventory.PAPERDOLL_RHAND;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setes"))
			{
				slot = Inventory.PAPERDOLL_LHAND;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setle"))
			{
				slot = Inventory.PAPERDOLL_LEAR;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setre"))
			{
				slot = Inventory.PAPERDOLL_REAR;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setlf"))
			{
				slot = Inventory.PAPERDOLL_LFINGER;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setrf"))
			{
				slot = Inventory.PAPERDOLL_RFINGER;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_seten"))
			{
				slot = Inventory.PAPERDOLL_NECK;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setun"))
			{
				slot = Inventory.PAPERDOLL_UNDER;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setba"))
			{
				slot = Inventory.PAPERDOLL_CLOAK;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setbe"))
			{
				slot = Inventory.PAPERDOLL_BELT;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_seth1"))
			{
				slot = Inventory.PAPERDOLL_HAIR;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_seth2"))
			{
				slot = Inventory.PAPERDOLL_HAIR2;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setbr"))
			{
				slot = Inventory.PAPERDOLL_BROOCH;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setbt")) // bracelet
			{
				slot = Inventory.PAPERDOLL_RBRACELET;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setsb")) // seed bracelet
			{
				slot = Inventory.PAPERDOLL_LBRACELET;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_setab"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT_BOOK;
				pageToShow = 1;
			}
			else if (command.startsWith("admin_seta1"))
			{
				slot = Inventory.PAPERDOLL_AGATHION1;
				pageToShow = 3;
			}
			else if (command.startsWith("admin_seta2"))
			{
				slot = Inventory.PAPERDOLL_AGATHION2;
				pageToShow = 3;
			}
			else if (command.startsWith("admin_seta3"))
			{
				slot = Inventory.PAPERDOLL_AGATHION3;
				pageToShow = 3;
			}
			else if (command.startsWith("admin_seta4"))
			{
				slot = Inventory.PAPERDOLL_AGATHION4;
				pageToShow = 3;
			}
			else if (command.startsWith("admin_seta5"))
			{
				slot = Inventory.PAPERDOLL_AGATHION5;
				pageToShow = 3;
			}
			else if (command.startsWith("admin_set01"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT1;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set02"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT2;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set03"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT3;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set04"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT4;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set05"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT5;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set06"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT6;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set07"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT7;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set08"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT8;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set09"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT9;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set10"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT10;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set11"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT11;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set12"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT12;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set13"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT13;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set14"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT14;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set15"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT15;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set16"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT16;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set17"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT17;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set18"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT18;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set19"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT19;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set20"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT20;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_set21"))
			{
				slot = Inventory.PAPERDOLL_ARTIFACT21;
				pageToShow = 2;
			}
			else if (command.startsWith("admin_sett1"))
			{
				slot = Inventory.PAPERDOLL_DECO1;
				pageToShow = 4;
			}
			else if (command.startsWith("admin_sett2"))
			{
				slot = Inventory.PAPERDOLL_DECO2;
				pageToShow = 4;
			}
			else if (command.startsWith("admin_sett3"))
			{
				slot = Inventory.PAPERDOLL_DECO3;
				pageToShow = 4;
			}
			else if (command.startsWith("admin_sett4"))
			{
				slot = Inventory.PAPERDOLL_DECO4;
				pageToShow = 4;
			}
			else if (command.startsWith("admin_sett5"))
			{
				slot = Inventory.PAPERDOLL_DECO5;
				pageToShow = 4;
			}
			else if (command.startsWith("admin_sett6"))
			{
				slot = Inventory.PAPERDOLL_DECO6;
				pageToShow = 4;
			}
			
			if (slot != -1)
			{
				try
				{
					final int enchIn = Integer.parseInt(command.substring(12));
					int ench = enchIn;
					
					// check value
					if ((ench < 0) || (ench > 127))
					{
						activeChar.sendSysMessage("New enchant value can only be 0 - 127.");
					}
					else
					{
						setEnchant(activeChar, ench, slot);
					}
				}
				catch (StringIndexOutOfBoundsException e)
				{
					// Quality of life change. If no input - set enchant value to 0.
					activeChar.sendSysMessage("Auto-Set Enchant value to 0.");
					setEnchant(activeChar, 0, slot);
				}
				catch (NumberFormatException e)
				{
					activeChar.sendSysMessage("Please specify a valid new enchant value.");
				}
			}
			
			// show the enchant menu after an action
			showMainPage(activeChar, pageToShow);
		}
		
		return true;
	}
	
	private void setEnchant(Player activeChar, int ench, int slot)
	{
		// Get the target.
		final Player player = activeChar.getTarget() != null ? activeChar.getTarget().asPlayer() : activeChar;
		if (player == null)
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		// Now we need to find the equipped weapon of the targeted character...
		Item itemInstance = null;
		
		// Only attempt to enchant if there is a weapon equipped.
		final Item paperdollInstance = player.getInventory().getPaperdollItem(slot);
		if ((paperdollInstance != null) && (paperdollInstance.getLocationSlot() == slot))
		{
			itemInstance = paperdollInstance;
		}
		
		if (itemInstance != null)
		{
			final int curEnchant = itemInstance.getEnchantLevel();
			
			// Set enchant value.
			int enchant = ench;
			if (PlayerConfig.OVER_ENCHANT_PROTECTION && !player.isGM())
			{
				if (itemInstance.isWeapon())
				{
					if (enchant > EnchantItemGroupsData.getInstance().getMaxWeaponEnchant())
					{
						activeChar.sendSysMessage("Maximum enchantment for weapon items is " + EnchantItemGroupsData.getInstance().getMaxWeaponEnchant() + ".");
						enchant = EnchantItemGroupsData.getInstance().getMaxWeaponEnchant();
					}
				}
				else if (itemInstance.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
				{
					if (enchant > EnchantItemGroupsData.getInstance().getMaxAccessoryEnchant())
					{
						activeChar.sendSysMessage("Maximum enchantment for accessory items is " + EnchantItemGroupsData.getInstance().getMaxAccessoryEnchant() + ".");
						enchant = EnchantItemGroupsData.getInstance().getMaxAccessoryEnchant();
					}
				}
				else if (enchant > EnchantItemGroupsData.getInstance().getMaxArmorEnchant())
				{
					activeChar.sendSysMessage("Maximum enchantment for armor items is " + EnchantItemGroupsData.getInstance().getMaxArmorEnchant() + ".");
					enchant = EnchantItemGroupsData.getInstance().getMaxArmorEnchant();
				}
			}
			
			itemInstance.setEnchantLevel(enchant);
			
			// Send packets.
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(itemInstance);
			player.sendInventoryUpdate(iu);
			player.broadcastUserInfo();
			
			// Information.
			activeChar.sendSysMessage("Changed enchantment of " + player.getName() + "'s " + itemInstance.getTemplate().getName() + " from " + curEnchant + " to " + enchant + ".");
			player.sendMessage("Admin has changed the enchantment of your " + itemInstance.getTemplate().getName() + " from " + curEnchant + " to " + enchant + ".");
		}
	}
	
	private void showMainPage(Player activeChar, int currentPage)
	{
		final Player player = activeChar.getTarget() != null ? activeChar.getTarget().asPlayer() : activeChar;
		if (player == null)
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		if (currentPage == 1)
		{
			AdminHtml.showAdminHtml(activeChar, "enchant.htm");
		}
		else if (currentPage == 2)
		{
			String getVars = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/enchantArtifact.htm");
			for (int i = 0; i < 21; i++)
			{
				int currentEnch = 0;
				final ItemTemplate item = ItemData.getInstance().getTemplate(player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_ARTIFACT1 + i));
				Item findItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_ARTIFACT1 + i);
				
				if (findItem != null)
				{
					currentEnch = findItem.getEnchantLevel();
				}
				
				// If no artifact in slot - returns blank square icon
				if ((item == null) || (findItem == null))
				{
					getVars = getVars.replace("%ar" + i + "_icon%", "L2UI_CT1.Windows.Windows_DF_TooltipBG");
					getVars = getVars.replace("%ar" + i + "_ench%", " ");
				}
				else
				{
					getVars = getVars.replace("%ar" + i + "_icon%", item.getIcon() == null ? "icon.etc_question_mark_i00" : item.getIcon());
					
					// if enchant value is 0 - show "blank instead of 0
					if (currentEnch != 0)
					{
						getVars = getVars.replace("%ar" + i + "_ench%", Integer.toString(currentEnch));
					}
					else
					{
						getVars = getVars.replace("%ar" + i + "_ench%", " ");
					}
				}
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
			html.setHtml(getVars);
			activeChar.sendPacket(html);
		}
		else if (currentPage == 3)
		{
			String getVars = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/enchantAgathion.htm");
			for (int i = 0; i < 5; i++)
			{
				int currentEnch = 0;
				final ItemTemplate item = ItemData.getInstance().getTemplate(player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_AGATHION1 + i));
				Item findItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_AGATHION1 + i);
				
				if (findItem != null)
				{
					currentEnch = findItem.getEnchantLevel();
				}
				
				// If no agathion in slot - returns blank square icon
				if ((item == null) || (findItem == null))
				{
					getVars = getVars.replace("%ag" + i + "_icon%", "L2UI_CT1.Windows.Windows_DF_TooltipBG");
					getVars = getVars.replace("%ag" + i + "_ench%", " ");
				}
				else
				{
					getVars = getVars.replace("%ag" + i + "_icon%", item.getIcon() == null ? "icon.etc_question_mark_i00" : item.getIcon());
					
					// if enchant value is 0 - show "blank instead of 0
					if (currentEnch != 0)
					{
						getVars = getVars.replace("%ag" + i + "_ench%", Integer.toString(currentEnch));
					}
					else
					{
						getVars = getVars.replace("%ag" + i + "_ench%", " ");
					}
				}
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
			html.setHtml(getVars);
			activeChar.sendPacket(html);
		}
		else if (currentPage == 4)
		{
			String getVars = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/enchantTalisman.htm");
			for (int i = 0; i < 6; i++)
			{
				int currentEnch = 0;
				final ItemTemplate item = ItemData.getInstance().getTemplate(player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_DECO1 + i));
				Item findItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_DECO1 + i);
				
				if (findItem != null)
				{
					currentEnch = findItem.getEnchantLevel();
				}
				
				// If no talisman in slot - returns blank square icon
				if ((item == null) || (findItem == null))
				{
					getVars = getVars.replace("%t" + i + "_icon%", "L2UI_CT1.Windows.Windows_DF_TooltipBG");
					getVars = getVars.replace("%t" + i + "_ench%", " ");
				}
				else
				{
					getVars = getVars.replace("%t" + i + "_icon%", item.getIcon() == null ? "icon.etc_question_mark_i00" : item.getIcon());
					
					// if enchant value is 0 - show "blank instead of 0
					if (currentEnch != 0)
					{
						getVars = getVars.replace("%t" + i + "_ench%", Integer.toString(currentEnch));
					}
					else
					{
						getVars = getVars.replace("%t" + i + "_ench%", " ");
					}
				}
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
			html.setHtml(getVars);
			activeChar.sendPacket(html);
		}
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
}