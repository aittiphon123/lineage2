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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.data.holders.CollectionDataHolder;
import org.l2jmobius.gameserver.data.holders.RelicCollectionDataHolder;
import org.l2jmobius.gameserver.data.holders.RelicDataHolder;
import org.l2jmobius.gameserver.data.xml.CollectionData;
import org.l2jmobius.gameserver.data.xml.OptionData;
import org.l2jmobius.gameserver.data.xml.RelicCollectionData;
import org.l2jmobius.gameserver.handler.IAdminCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.PlayerCollectionData;
import org.l2jmobius.gameserver.model.actor.holders.player.PlayerRelicCollectionData;
import org.l2jmobius.gameserver.model.item.holders.ItemEnchantHolder;
import org.l2jmobius.gameserver.model.options.Options;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.collection.ExCollectionActiveEvent;
import org.l2jmobius.gameserver.network.serverpackets.collection.ExCollectionComplete;
import org.l2jmobius.gameserver.network.serverpackets.collection.ExCollectionInfo;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsCollectionCompleteAnnounce;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsCollectionInfo;

/**
 * Admin Collections Menu
 * @author CostyKiller
 */
public class AdminCollections implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_collections_menu",
		"admin_collection_add",
		"admin_collection_remove",
		"admin_collection_switch",
		"admin_collection_type_switch"
	};
	
	private static final int ITEMS_PER_PAGE = 3;
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		if ((activeChar.getTarget() == null) || !activeChar.getTarget().isPlayer())
		{
			activeChar.sendMessage("Invalid target! Please target a player.");
			return false;
		}
		
		Player target = activeChar.getTarget().asPlayer();
		
		if (command.startsWith("admin_collections_menu"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			int type = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			int category = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;
			
			showCollectionsMenu(activeChar, target, type, category, page);
		}
		else if (command.startsWith("admin_collection_type_switch"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			int newType = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			showCollectionsMenu(activeChar, target, newType, 1, 0);
		}
		else if (command.startsWith("admin_collection_switch"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			int type = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			int category = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			
			showCollectionsMenu(activeChar, target, type, category, 0);
		}
		else if (command.startsWith("admin_collection_add"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (st.countTokens() < 4)
			{
				activeChar.sendMessage("Usage: //collection_add <type> <id> <category> <page>");
				return false;
			}
			
			int type = Integer.parseInt(st.nextToken());
			int id = Integer.parseInt(st.nextToken());
			int category = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			
			if (type == 1) // Collection
			{
				CollectionDataHolder collection = CollectionData.getInstance().getCollection(id);
				if (collection == null)
				{
					activeChar.sendMessage("Invalid collection ID: " + id);
					return false;
				}
				
				// Add all items from the collection with unique index check
				for (ItemEnchantHolder item : collection.getItems())
				{
					// Check if this collection+index combination already exists
					final int itemIndex = item.getIndex();
					boolean indexExists = target.getCollections().stream().anyMatch(col -> (col.getCollectionId() == id) && (col.getIndex() == itemIndex));
					
					// Only add if this specific index doesn't exist for this collection
					if (!indexExists)
					{
						target.getCollections().add(new PlayerCollectionData(id, item.getId(), item.getIndex()));
					}
				}
				target.storeCollections();
				
				// Update client
				if (category == 7)
				{
					target.sendPacket(new ExCollectionActiveEvent());
				}
				else
				{
					target.sendPacket(new ExCollectionInfo(target, category));
				}
				
				// Announce Collection Complete.
				target.sendPacket(new ExCollectionComplete(id));
				target.sendPacket(new SystemMessage(SystemMessageId.S1_COLLECTION_IS_COMPLETE).addString(collection.getName()));
				
				// Apply collection option
				final Options options = OptionData.getInstance().getOptions(collection.getOptionId());
				if (options != null)
				{
					options.apply(target);
				}
				
				activeChar.sendMessage("Added Collection '" + sanitizeText(collection.getName()) + "' to " + target.getName());
			}
			else if (type == 2) // Relic collection
			{
				RelicCollectionDataHolder collection = RelicCollectionData.getInstance().getRelicCollection(id);
				if (collection == null)
				{
					activeChar.sendMessage("Invalid relic collection ID: " + id);
					return false;
				}
				
				// Add all relics from the collection
				int index = 0;
				for (RelicDataHolder relic : collection.getRelics())
				{
					target.getRelicCollections().add(new PlayerRelicCollectionData(id, relic.getRelicId(), relic.getEnchantLevel(), index));
					index++;
				}
				target.storeRelicCollections();
				
				// Update client
				target.sendPacket(new ExRelicsCollectionInfo(target));
				// Announce Collection Complete.
				target.sendPacket(new ExRelicsCollectionCompleteAnnounce(id));
				
				// Apply collection option
				final Options options = OptionData.getInstance().getOptions(collection.getOptionId());
				if (options != null)
				{
					options.apply(target);
				}
				
				activeChar.sendMessage("Added Relic Collection '" + sanitizeText(collection.getName()) + "' to " + target.getName());
			}
			
			if (activeChar != target)
			{
				target.sendMessage("Admin added a collection to you.");
			}
			
			showCollectionsMenu(activeChar, target, type, category, page);
		}
		else if (command.startsWith("admin_collection_remove"))
		{
			// XXX: When you remove a collection ui updates only after relog
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (st.countTokens() < 4)
			{
				activeChar.sendMessage("Usage: //collection_remove <type> <id> <category> <page>");
				return false;
			}
			
			int type = Integer.parseInt(st.nextToken());
			int id = Integer.parseInt(st.nextToken());
			int category = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			
			if (type == 1) // Collection
			{
				// Remove collection option first
				final CollectionDataHolder collection = CollectionData.getInstance().getCollection(id);
				if (collection != null)
				{
					final Options options = OptionData.getInstance().getOptions(collection.getOptionId());
					if (options != null)
					{
						options.remove(target);
					}
				}
				
				// Delete directly from database
				try (Connection con = DatabaseFactory.getConnection();
					PreparedStatement ps = con.prepareStatement("DELETE FROM collections WHERE accountName=? AND collectionId=?"))
				{
					ps.setString(1, target.getAccountName());
					ps.setInt(2, id);
					ps.execute();
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Error removing collection from database: " + e.getMessage());
					return false;
				}
				
				// Update cached collections
				target.getCollections().removeIf(col -> col.getCollectionId() == id);
				
				// Send update packet
				target.sendPacket(new ExCollectionInfo(target, category));
				
				String collectionName = collection != null ? sanitizeText(collection.getName()) : "Collection #" + id;
				if ((collectionName == null) || collectionName.trim().isEmpty())
				{
					collectionName = "Collection #" + id;
				}
				activeChar.sendMessage("Removed Collection '" + collectionName + "' from " + target.getName());
			}
			else if (type == 2) // Relic collection
			{
				// Remove collection option first
				final RelicCollectionDataHolder collection = RelicCollectionData.getInstance().getRelicCollection(id);
				if (collection != null)
				{
					final Options options = OptionData.getInstance().getOptions(collection.getOptionId());
					if (options != null)
					{
						options.remove(target);
					}
				}
				
				// Delete directly from database
				try (Connection con = DatabaseFactory.getConnection();
					PreparedStatement ps = con.prepareStatement("DELETE FROM relic_collections WHERE accountName=? AND relicCollectionId=?"))
				{
					ps.setString(1, target.getAccountName());
					ps.setInt(2, id);
					ps.execute();
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Error removing relic collection from database: " + e.getMessage());
					return false;
				}
				
				// Update cached collections
				target.getRelicCollections().removeIf(rc -> rc.getRelicCollectionId() == id);
				
				// Send update packet
				target.sendPacket(new ExRelicsCollectionInfo(target));
				
				String collectionName = collection != null ? sanitizeText(collection.getName()) : "Relic Collection #" + id;
				if ((collectionName == null) || collectionName.trim().isEmpty())
				{
					collectionName = "Relic Collection #" + id;
				}
				activeChar.sendMessage("Removed Relic Collection '" + collectionName + "' from " + target.getName());
			}
			
			if (activeChar != target)
			{
				target.sendMessage("Admin removed a collection from you.");
			}
			
			showCollectionsMenu(activeChar, target, type, category, page);
		}
		
		return true;
	}
	
	/**
	 * Show collections menu to admin with enhanced UI
	 * @param activeChar
	 * @param target
	 * @param type
	 * @param category
	 * @param page
	 */
	private void showCollectionsMenu(Player activeChar, Player target, int type, int category, int page)
	{
		String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/collections_menu.htm");
		
		content = content.replace("%targetName%", target.getName());
		content = content.replace("%typeButtons%", generateTypeButtons(type, category, page));
		content = content.replace("%categoryButtons%", generateCategoryButtons(type, category, page));
		content = content.replace("%collections%", generateCollectionsSection(target, type, category, page));
		content = content.replace("%statistics%", generateStatistics(target, type));
		
		activeChar.sendPacket(new NpcHtmlMessage(0, 1, content));
	}
	
	/**
	 * Generate type switch buttons (Common/Relic)
	 * @param currentType
	 * @param category
	 * @param page
	 * @return
	 */
	private String generateTypeButtons(int currentType, int category, int page)
	{
		StringBuilder html = new StringBuilder();
		html.append("<table width=270><tr>");
		
		// Common Collections button
		String commonStyle = currentType == 1 ? "L2UI_CT1.Button_DF_Calculator_Down" : "L2UI_CT1.Button_DF_Down";
		html.append("<td width=135 align=center>");
		html.append("<button value=\"Collections\" action=\"bypass -h admin_collection_type_switch 1\" ");
		html.append("width=130 height=22 back=\"").append(commonStyle).append("\" fore=\"L2UI_CT1.Button_DF\">");
		html.append("</td>");
		
		// Relic Collections button
		String relicStyle = currentType == 2 ? "L2UI_CT1.Button_DF_Calculator_Down" : "L2UI_CT1.Button_DF_Down";
		html.append("<td width=135 align=center>");
		html.append("<button value=\"Relic Collections\" action=\"bypass -h admin_collection_type_switch 2\" ");
		html.append("width=130 height=22 back=\"").append(relicStyle).append("\" fore=\"L2UI_CT1.Button_DF\">");
		html.append("</td>");
		
		html.append("</tr></table>");
		return html.toString();
	}
	
	/**
	 * Generate category filter buttons
	 * @param type
	 * @param currentCategory
	 * @param page
	 * @return
	 */
	private String generateCategoryButtons(int type, int currentCategory, int page)
	{
		StringBuilder html = new StringBuilder();
		int maxCategory = type == 1 ? 7 : 4;
		
		html.append("<table width=270><tr>");
		for (int i = 1; i <= maxCategory; i++)
		{
			String buttonStyle = (i == currentCategory) ? "L2UI_CT1.Button_DF_Calculator_Down" : "L2UI_CT1.Button_DF_Down";
			int width = (270 / maxCategory) - 2;
			
			html.append("<td align=center>");
			html.append("<button value=\"Cat ").append(i).append("\" ");
			html.append("action=\"bypass -h admin_collection_switch ").append(type).append(" ").append(i).append("\" ");
			html.append("width=").append(width).append(" height=20 ");
			html.append("back=\"").append(buttonStyle).append("\" fore=\"L2UI_CT1.Button_DF\">");
			html.append("</td>");
		}
		html.append("</tr></table>");
		
		return html.toString();
	}
	
	/**
	 * Generate collections list with pagination
	 * @param target
	 * @param type
	 * @param category
	 * @param page
	 * @return
	 */
	private String generateCollectionsSection(Player target, int type, int category, int page)
	{
		StringBuilder html = new StringBuilder();
		
		List<?> collections;
		String typeName;
		
		if (type == 1)
		{
			collections = CollectionData.getInstance().getCollectionsByTabId(category);
			typeName = "Collection";
		}
		else
		{
			collections = RelicCollectionData.getInstance().getRelicCollectionsByCategory(category);
			typeName = "Relic Collection";
		}
		
		if (collections.isEmpty())
		{
			html.append("<table width=270 bgcolor=282828 cellpadding=5>");
			html.append("<tr><td align=center><font color=\"FF6B6B\">No collections found in this category</font></td></tr>");
			html.append("</table>");
			return html.toString();
		}
		
		final int totalCollections = collections.size();
		final int totalPages = (int) Math.ceil((double) totalCollections / ITEMS_PER_PAGE);
		final int startIndex = page * ITEMS_PER_PAGE;
		final int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalCollections);
		
		html.append("<table width=270 bgcolor=282828 cellpadding=2 cellspacing=1>");
		
		// Header
		html.append("<tr>");
		html.append("<td align=center>");
		html.append("<center><font color=\"LEVEL\">").append(typeName).append(" (Category ").append(category).append(")</font></center>");
		html.append("</td>");
		html.append("</tr>");
		
		// Collections list
		for (int i = startIndex; i < endIndex; i++)
		{
			int collectionId;
			String collectionName;
			boolean hasCollection;
			
			if (type == 1)
			{
				CollectionDataHolder collection = (CollectionDataHolder) collections.get(i);
				collectionId = collection.getCollectionId();
				collectionName = sanitizeHtmlText(collection.getName());
				hasCollection = target.isCompleteCollection(collectionId);
			}
			else
			{
				RelicCollectionDataHolder collection = (RelicCollectionDataHolder) collections.get(i);
				collectionId = collection.getCollectionId();
				collectionName = sanitizeHtmlText(collection.getName());
				hasCollection = target.isCompleteRelicCollection(collectionId);
			}
			
			// Fallback to ID if name is empty after sanitization
			if ((collectionName == null) || collectionName.trim().isEmpty())
			{
				collectionName = typeName + " #" + collectionId;
			}
			
			String bgColor = ((i - startIndex) % 2) == 0 ? "171612" : "1f1c18";
			String statusColor = hasCollection ? "4EE44E" : "FF6B6B";
			String statusText = hasCollection ? "✓ Complete" : "✗ Missing";
			String action = hasCollection ? "remove" : "add";
			String buttonValue = hasCollection ? "Remove" : "Add";
			String buttonBack = hasCollection ? "L2UI_CT1.Button_DF_Down" : "L2UI_CT1.Button_DF_Down";
			
			html.append("<tr bgcolor=").append(bgColor).append(">");
			html.append("<td width=110 align=left> ").append(collectionName).append("</td>");
			html.append("<td width=60 align=center><font color=\"").append(statusColor).append("\">").append(statusText).append("</font></td>");
			html.append("<td width=45 align=center>");
			html.append("<button value=\"").append(buttonValue).append("\" ");
			html.append("action=\"bypass -h admin_collection_").append(action).append(" ");
			html.append(type).append(" ").append(collectionId).append(" ").append(category).append(" ").append(page).append("\" ");
			html.append("width=45 height=20 back=\"").append(buttonBack).append("\" fore=\"L2UI_CT1.Button_DF\">");
			html.append("</td>");
			html.append("</tr>");
		}
		
		// Pagination
		if (totalPages > 1)
		{
			html.append("</table><tr>");
			html.append("<td colspan=3 align=center bgcolor=1a1a1a>");
			html.append("<table width=268><tr>");
			
			// Previous button
			html.append("<td width=70 align=center>");
			if (page > 0)
			{
				html.append("<button value=\"◄ Prev\" action=\"bypass -h admin_collections_menu ");
				html.append(type).append(" ").append(category).append(" ").append(page - 1);
				html.append("\" width=65 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
			}
			else
			{
				html.append("<button value=\"◄ Prev\" action=\"\" width=65 height=20 ");
				html.append("back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF_Grayed\">");
			}
			html.append("</td>");
			
			// Page info
			html.append("<td width=128 align=center>");
			html.append("<font color=\"LEVEL\">Page ").append(page + 1).append(" / ").append(totalPages).append("</font>");
			html.append("</td>");
			
			// Next button
			html.append("<td width=70 align=center>");
			if (page < (totalPages - 1))
			{
				html.append("<button value=\"Next ►\" action=\"bypass -h admin_collections_menu ");
				html.append(type).append(" ").append(category).append(" ").append(page + 1);
				html.append("\" width=65 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
			}
			else
			{
				html.append("<button value=\"Next ►\" action=\"\" width=65 height=20 ");
				html.append("back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF_Grayed\">");
			}
			html.append("</td>");
			
			html.append("</tr></table>");
			html.append("</td>");
			html.append("</tr>");
		}
		
		html.append("</table>");
		return html.toString();
	}
	
	/**
	 * Generate statistics section
	 * @param target
	 * @param type
	 * @return
	 */
	private String generateStatistics(Player target, int type)
	{
		StringBuilder html = new StringBuilder();
		
		int total = 0;
		int completed = 0;
		
		if (type == 1)
		{
			Collection<CollectionDataHolder> allCollections = CollectionData.getInstance().getCollections();
			total = allCollections.size();
			for (CollectionDataHolder collection : allCollections)
			{
				if (target.isCompleteCollection(collection.getCollectionId()))
				{
					completed++;
				}
			}
		}
		else
		{
			Collection<RelicCollectionDataHolder> allCollections = RelicCollectionData.getInstance().getRelicCollections();
			total = allCollections.size();
			for (RelicCollectionDataHolder collection : allCollections)
			{
				if (target.isCompleteRelicCollection(collection.getCollectionId()))
				{
					completed++;
				}
			}
		}
		
		int percentage = total > 0 ? (completed * 100) / total : 0;
		String color = percentage >= 75 ? "4EE44E" : percentage >= 50 ? "FFD700" : percentage >= 25 ? "FFA500" : "FF6B6B";
		
		html.append("<table width=270 bgcolor=282828 cellpadding=3>");
		html.append("<tr>");
		html.append("<td width=135 align=center>Completed: <font color=\"").append(color).append("\">").append(completed).append("/").append(total).append("</font></td>");
		html.append("<td width=135 align=center>Progress: <font color=\"").append(color).append("\">").append(percentage).append("%</font></td>");
		html.append("</tr>");
		html.append("</table>");
		
		return html.toString();
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	/**
	 * Sanitize text for HTML display by removing problematic characters
	 * @param text
	 * @return
	 */
	private String sanitizeHtmlText(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return "";
		}
		
		// Replace HTML special characters
		text = text.replace("&", "&amp;");
		text = text.replace("<", "&lt;");
		text = text.replace(">", "&gt;");
		text = text.replace("\"", "&quot;");
		
		// Remove control characters and other problematic characters
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
		
		// Remove zero-width spaces and other invisible characters
		text = text.replaceAll("[\u200B-\u200D\uFEFF]", "");
		
		return text.trim();
	}
	
	/**
	 * Sanitize text for console messages (no HTML encoding needed)
	 * @param text
	 * @return
	 */
	private String sanitizeText(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return "";
		}
		
		// Remove control characters and other problematic characters
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
		
		// Remove zero-width spaces and other invisible characters
		text = text.replaceAll("[\u200B-\u200D\uFEFF]", "");
		
		return text.trim();
	}
}