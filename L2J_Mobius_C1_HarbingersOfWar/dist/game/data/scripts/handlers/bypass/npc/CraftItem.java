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
package handlers.bypass.npc;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.holders.RecipeHolder;
import org.l2jmobius.gameserver.data.holders.RecipeStatHolder;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.data.xml.RecipeData;
import org.l2jmobius.gameserver.handler.IBypassHandler;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.recipe.RecipeList;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @author Mobius
 */
public class CraftItem implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"CraftItem"
	};
	
	@Override
	public boolean onCommand(String command, Player player, Creature target)
	{
		if (!PlayerConfig.IS_CRAFTING_ENABLED)
		{
			player.sendMessage("Crafting is disabled, you cannot register this recipe.");
			return false;
		}
		
		if (!player.getClient().getFloodProtectors().canManufacture())
		{
			player.sendMessage("You may not alter your recipe book while engaged in manufacturing.");
			return false;
		}
		
		final int recipeItemId = Integer.parseInt(command.replace("CraftItem ", ""));
		final RecipeList recipe = RecipeData.getInstance().getRecipeByItemId(recipeItemId);
		if (recipe == null)
		{
			return false;
		}
		
		if (!player.hasDwarvenCraft())
		{
			player.sendMessage("You do not have the ability to create items.");
			return false;
		}
		
		if (recipe.getLevel() > player.getDwarvenCraft())
		{
			player.sendPacket(SystemMessageId.CREATE_ITEM_LEVEL_IS_LOW);
			return false;
		}
		
		final RecipeStatHolder stat = recipe.getStatUse()[0];
		final int mpConsume = stat != null ? stat.getValue() : 0;
		if (player.getCurrentMp() < mpConsume)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
			return false;
		}
		
		final RecipeHolder[] materials = recipe.getRecipes();
		for (RecipeHolder holder : materials)
		{
			if (player.getInventory().getInventoryItemCount(holder.getItemId(), -1) < holder.getQuantity())
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return false;
			}
		}
		
		for (RecipeHolder holder : materials)
		{
			final int itemId = holder.getItemId();
			if (itemId == recipeItemId)
			{
				continue; // Recipe is not consumed in C1.
			}
			
			if (!player.destroyItemByItemId(ItemProcessType.CRAFT, itemId, holder.getQuantity(), player, true))
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return false;
			}
		}
		
		player.reduceCurrentMp(mpConsume);
		
		if (Rnd.get(100) < recipe.getSuccessRate())
		{
			player.addItem(ItemProcessType.QUEST, recipe.getItemId(), recipe.getCount(), player, true);
		}
		else
		{
			player.sendMessage("The attempt to create " + ItemData.getInstance().getTemplate(recipe.getItemId()).getName() + " has failed.");
		}
		return true;
	}
	
	@Override
	public String[] getCommandList()
	{
		return COMMANDS;
	}
}
