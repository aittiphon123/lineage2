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

import java.util.StringTokenizer;

import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.handler.IAdminCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.herobook.HeroBookInfoHolder;
import org.l2jmobius.gameserver.model.herobook.HeroBookManager;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.herobook.ExHeroBookInfo;

/**
 * Admin Tales of Hero Menu
 * @author CostyKiller
 */
public class AdminTales implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_tales_menu",
		"admin_tales_modify",
		"admin_tales_set"
	};
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		if ((activeChar.getTarget() == null) || !activeChar.getTarget().isPlayer())
		{
			activeChar.sendMessage("Invalid target!");
			return false;
		}
		Player target = activeChar.getTarget().asPlayer();
		
		if (command.startsWith("admin_tales_menu"))
		{
			showTalesMenu(activeChar, target);
		}
		else if (command.startsWith("admin_tales_set "))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (st.countTokens() < 2)
			{
				activeChar.sendMessage("Usage: //tales_set <category> <level>");
				return false;
			}
			
			int category = Integer.parseInt(st.nextToken()); // 1 = Common, 2 = Wanderings
			int level = Integer.parseInt(st.nextToken());
			
			// Validate level
			if ((category == 1) && ((level < 0) || (level > 300)))
			{
				activeChar.sendMessage("Invalid level for Tales of Hero! Valid range: 0-300");
				return false;
			}
			else if ((category == 2) && ((level < 0) || (level > 100)))
			{
				activeChar.sendMessage("Invalid level for Tales of Hero - Wanderings! Valid range: 0-100");
				return false;
			}
			
			// Get or create holder
			HeroBookInfoHolder holder = target.getHeroBookProgress((byte) category);
			
			// Set the level
			holder.setCurrentLevel(level);
			holder.setCurrentExp(0); // Reset exp when setting level manually
			
			// Save to database
			HeroBookManager.saveCurrentPlayerProgress(target, holder, category);
			
			// Update client
			target.sendPacket(new ExHeroBookInfo());
			
			String typeName = category == 1 ? "Tales of Hero" : "Tales of Hero - Wanderings";
			activeChar.sendMessage("Set " + typeName + " to level " + level + " for " + target.getName());
			if (activeChar != target)
			{
				target.sendMessage("Admin set your " + typeName + " to level " + level);
			}
			
			showTalesMenu(activeChar, target);
		}
		else if (command.startsWith("admin_tales_modify "))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (st.countTokens() < 2)
			{
				activeChar.sendMessage("Usage: //tales_modify <category> <increase|decrease>");
				return false;
			}
			
			int category = Integer.parseInt(st.nextToken()); // 1 = Common, 2 = Wanderings
			String action = st.nextToken(); // "increase" or "decrease"
			
			// Get current level
			HeroBookInfoHolder holder = target.getHeroBookProgress((byte) category);
			int currentLevel = holder.getCurrentLevel();
			
			// Modify level
			if ("increase".equals(action))
			{
				if ((category == 1) && (currentLevel < 300))
				{
					currentLevel++;
				}
				else if ((category == 2) && (currentLevel < 100))
				{
					currentLevel++;
				}
				else
				{
					activeChar.sendMessage("Already at maximum level!");
					return false;
				}
			}
			else if ("decrease".equals(action))
			{
				if (currentLevel > 0)
				{
					currentLevel--;
				}
				else
				{
					activeChar.sendMessage("Already at minimum level!");
					return false;
				}
			}
			
			// Set the new level
			holder.setCurrentLevel(currentLevel);
			holder.setCurrentExp(0); // Reset exp
			
			// Save to database
			HeroBookManager.saveCurrentPlayerProgress(target, holder, category);
			
			// Update client
			target.sendPacket(new ExHeroBookInfo());
			
			String typeName = category == 1 ? "Tales of Hero" : "Tales of Hero - Wanderings";
			activeChar.sendMessage("Set " + typeName + " to level " + currentLevel + " for " + target.getName());
			if (activeChar != target)
			{
				target.sendMessage("Admin set your " + typeName + " to level " + currentLevel);
			}
			
			showTalesMenu(activeChar, target);
		}
		
		return true;
	}
	
	/**
	 * Generate HTML section for a tale type
	 * @param target
	 * @param category
	 * @return
	 */
	private String generateTaleSection(Player target, int category)
	{
		StringBuilder html = new StringBuilder();
		
		// Get current level
		HeroBookInfoHolder holder = target.getHeroBookProgress((byte) category);
		int currentLevel = holder.getCurrentLevel();
		int maxLevel = category == 1 ? 300 : 100;
		
		String typeName = category == 1 ? "Tales of Hero" : "Tales of Hero - Wanderings";
		
		html.append("<table width=270 bgcolor=282828 cellpadding=3 cellspacing=2>");
		
		// Title table
		html.append("<tr><table><tr><td width=45></td>");
		html.append("<td width=180 align=center>");
		html.append("<font color=\"LEVEL\">").append(typeName).append("</font>");
		html.append("</td>");
		html.append("<td width=45></td></tr></table></tr>");
		
		// Control table
		html.append("<tr><table><tr>");
		html.append("<td width=90 align=center>");
		html.append("<button value=\"Decrease\" action=\"bypass -h admin_tales_modify ").append(category).append(" decrease\" ");
		html.append("width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		html.append("</td>");
		html.append("<td width=90 align=center>");
		html.append("<font color=\"LEVEL\">Level: ").append(currentLevel).append(" / ").append(maxLevel).append("</font>");
		html.append("</td>");
		html.append("<td width=90 align=center>");
		html.append("<button value=\"Increase\" action=\"bypass -h admin_tales_modify ").append(category).append(" increase\" ");
		html.append("width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		html.append("</td>");
		html.append("</tr></table></tr>");
		
		// Set level table
		html.append("<tr><table><tr><td width=90></td>");
		html.append("<td width=90 align=center>");
		html.append("Set Level: <edit var=\"talesLevel").append(category).append("\" width=50 height=15 type=\"number\"> ");
		html.append("<button value=\"Set\" action=\"bypass -h admin_tales_set ").append(category).append(" $talesLevel").append(category).append("\" ");
		html.append("width=45 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		html.append("</td>");
		html.append("<td width=90></td></tr></table></tr>");
		
		html.append("</table>");
		
		return html.toString();
	}
	
	/**
	 * Show tales menu to admin
	 * @param activeChar
	 * @param target
	 */
	private void showTalesMenu(Player activeChar, Player target)
	{
		String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/tales_menu.htm");
		
		content = content.replace("%targetName%", target.getName());
		content = content.replace("%talesOfHeroCommon%", generateTaleSection(target, 1));
		content = content.replace("%talesOfHeroWanderings%", generateTaleSection(target, 2));
		
		activeChar.sendPacket(new NpcHtmlMessage(0, 1, content));
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
}