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
package handlers.items;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.holders.RecipeHolder;
import org.l2jmobius.gameserver.data.holders.RecipeStatHolder;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.data.xml.RecipeData;
import org.l2jmobius.gameserver.handler.IItemHandler;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.item.recipe.RecipeList;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Mobius
 */
public class Recipes implements IItemHandler
{
	@Override
	public boolean onItemUse(Playable playable, Item item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.ITEM_NOT_AVAILABLE_FOR_PETS);
			return false;
		}
		
		if (!PlayerConfig.IS_CRAFTING_ENABLED)
		{
			playable.sendMessage("Crafting is disabled, you cannot register this recipe.");
			return false;
		}
		
		final RecipeList recipe = RecipeData.getInstance().getRecipeByItemId(item.getId());
		if (recipe == null)
		{
			return false;
		}
		
		final Player player = playable.asPlayer();
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
		
		final int recipeItemId = recipe.getRecipeId();
		final StringBuilder sb = new StringBuilder();
		for (RecipeHolder holder : recipe.getRecipes())
		{
			final int itemId = holder.getItemId();
			if (itemId == recipeItemId)
			{
				continue; // Recipe is not consumed in C1.
			}
			
			sb.append(holder.getQuantity());
			sb.append(" ");
			sb.append(ItemData.getInstance().getTemplate(itemId).getName());
			sb.append("<br1>");
		}
		
		final RecipeStatHolder stat = recipe.getStatUse()[0];
		final int mpConsume = stat != null ? stat.getValue() : 0;
		
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(player, "data/html/recipe.htm");
		html.replace("%item_name%", ItemData.getInstance().getTemplate(recipe.getItemId()).getName());
		html.replace("%material_list%", sb.toString());
		html.replace("%count%", String.valueOf(recipe.getCount()));
		html.replace("%mp%", String.valueOf(mpConsume));
		html.replace("%rate%", String.valueOf(recipe.getSuccessRate()));
		html.replace("%recipeId%", String.valueOf(recipeItemId));
		player.sendPacket(html);
		
		return true;
	}
}
