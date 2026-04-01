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
package org.l2jmobius.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.l2jmobius.commons.util.StringUtil;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.config.custom.SchemeBufferConfig;
import org.l2jmobius.gameserver.data.SchemeBufferTable;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.holders.npc.BuffSkillHolder;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.util.HtmlUtil;

/**
 * Scheme Buffer NPC handler supporting scheme management and manual buff casting.<br>
 * Provides extra pages (Buffs/Dances/Songs/etc.) and auto-buff presets without exposing internal groups in the edit UI.
 * <ul>
 * <li>Creates, edits, deletes and casts player schemes.</li>
 * <li>Manually casts skills by category with pagination and target selection.</li>
 * <li>Applies class-based auto-buffs using MAGE_GROUP / FIGHTER_GROUP.</li>
 * </ul>
 * @Author Mobius, BazookaRpm
 */
public class SchemeBuffer extends Npc
{
	// Constants.
	private static final int PAGE_LIMIT = 6;
	private static final int SCHEME_NAME_MAX_LENGTH = 14;
	private static final int TYPES_PER_ROW = 4;
	
	/**
	 * Creates a SchemeBuffer instance.
	 * @param template
	 */
	public SchemeBuffer(NpcTemplate template)
	{
		super(template);
	}
	
	/**
	 * Handles Scheme Buffer bypass commands.
	 * @param player
	 * @param commandValue
	 */
	@Override
	public void onBypassFeedback(Player player, String commandValue)
	{
		if ((player == null) || (commandValue == null) || commandValue.isEmpty())
		{
			return;
		}
		
		final StringTokenizer st = new StringTokenizer(commandValue.replace("createscheme ", "createscheme;"), ";");
		if (!st.hasMoreTokens())
		{
			return;
		}
		
		final String currentCommand = st.nextToken();
		
		if (currentCommand.startsWith("menu"))
		{
			showMainMenu(player);
			return;
		}
		else if (currentCommand.startsWith("cleanup"))
		{
			player.stopAllEffects();
			
			final Summon summon = player.getSummon();
			if (summon != null)
			{
				summon.stopAllEffects();
			}
			
			showMainMenu(player);
			return;
		}
		else if (currentCommand.startsWith("heal"))
		{
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
			
			final Summon summon = player.getSummon();
			if (summon != null)
			{
				summon.setCurrentHpMp(summon.getMaxHp(), summon.getMaxMp());
			}
			
			showMainMenu(player);
			return;
		}
		else if (currentCommand.startsWith("support"))
		{
			showGiveBuffsWindow(player);
			return;
		}
		else if (currentCommand.startsWith("givebuffs"))
		{
			if (st.countTokens() < 2)
			{
				return;
			}
			
			final String schemeName = st.nextToken();
			
			final int cost;
			try
			{
				cost = Integer.parseInt(st.nextToken());
			}
			catch (NumberFormatException e)
			{
				return;
			}
			
			Creature target = player;
			if (st.hasMoreTokens() && "pet".equalsIgnoreCase(st.nextToken()))
			{
				target = player.getSummon();
			}
			
			if (target == null)
			{
				player.sendMessage("You don't have a pet.");
				return;
			}
			
			if ((cost == 0) || ((SchemeBufferConfig.BUFFER_ITEM_ID == 57) && player.reduceAdena(ItemProcessType.FEE, cost, this, true)) || ((SchemeBufferConfig.BUFFER_ITEM_ID != 57) && player.destroyItemByItemId(ItemProcessType.FEE, SchemeBufferConfig.BUFFER_ITEM_ID, cost, player, true)))
			{
				for (int skillId : SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName))
				{
					final BuffSkillHolder holder = SchemeBufferTable.getInstance().getAvailableBuff(skillId);
					if (holder == null)
					{
						continue;
					}
					
					final Skill skill = SkillData.getInstance().getSkill(skillId, holder.getLevel());
					if (skill != null)
					{
						skill.applyEffects(this, target);
					}
				}
			}
			return;
		}
		else if (currentCommand.startsWith("editschemes"))
		{
			if (st.countTokens() < 3)
			{
				return;
			}
			
			final String groupType = st.nextToken();
			final String schemeName = st.nextToken();
			
			final int page;
			try
			{
				page = Integer.parseInt(st.nextToken());
			}
			catch (NumberFormatException e)
			{
				return;
			}
			
			showEditSchemeWindow(player, groupType, schemeName, page);
			return;
		}
		else if (currentCommand.startsWith("skill"))
		{
			if (st.countTokens() < 4)
			{
				return;
			}
			
			final String groupType = st.nextToken();
			final String schemeName = st.nextToken();
			
			final int skillId;
			final int page;
			try
			{
				skillId = Integer.parseInt(st.nextToken());
				page = Integer.parseInt(st.nextToken());
			}
			catch (NumberFormatException e)
			{
				return;
			}
			
			final Map<String, List<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
			if ((schemes == null) || !schemes.containsKey(schemeName))
			{
				player.sendMessage("This scheme name is invalid.");
				showGiveBuffsWindow(player);
				return;
			}
			
			final List<Integer> skills = schemes.get(schemeName);
			
			if (currentCommand.startsWith("skillselect") && !schemeName.equalsIgnoreCase("none"))
			{
				final Skill skill = SkillData.getInstance().getSkill(skillId, 1);
				if (skill == null)
				{
					showEditSchemeWindow(player, groupType, schemeName, page);
					return;
				}
				
				final int totalBuffs = skills.size();
				final int currentDanceSongCount = getCountOf(skills, true);
				final boolean isDanceOrSong = skill.isDance();
				final int maxCount = player.getStat().getMaxBuffCount();
				
				if (totalBuffs >= maxCount)
				{
					player.sendMessage("This scheme has reached the maximum amount of buffs.");
				}
				else if (isDanceOrSong && (currentDanceSongCount >= PlayerConfig.DANCES_MAX_AMOUNT))
				{
					player.sendMessage("You cannot add more than " + PlayerConfig.DANCES_MAX_AMOUNT + " songs/dances to this scheme.");
				}
				else if (!skills.contains(skillId))
				{
					skills.add(skillId);
				}
			}
			else if (currentCommand.startsWith("skillunselect"))
			{
				skills.remove(Integer.valueOf(skillId));
			}
			
			showEditSchemeWindow(player, groupType, schemeName, page);
			return;
		}
		else if (currentCommand.startsWith("createscheme"))
		{
			try
			{
				if (!st.hasMoreTokens())
				{
					player.sendMessage("Scheme's name must contain up to " + SCHEME_NAME_MAX_LENGTH + " chars.");
					return;
				}
				
				final String schemeName = st.nextToken().trim();
				if (schemeName.length() > SCHEME_NAME_MAX_LENGTH)
				{
					player.sendMessage("Scheme's name must contain up to " + SCHEME_NAME_MAX_LENGTH + " chars.");
					return;
				}
				
				final String normalized = schemeName.replace(" ", "").replace(".", "").replace(",", "").replace("-", "").replace("+", "").replace("!", "").replace("?", "");
				if (!StringUtil.isAlphaNumeric(normalized))
				{
					player.sendMessage("Please use plain alphanumeric characters.");
					return;
				}
				
				final Map<String, List<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
				if (schemes != null)
				{
					if (schemes.size() == SchemeBufferConfig.BUFFER_MAX_SCHEMES)
					{
						player.sendMessage("Maximum schemes amount is already reached.");
						return;
					}
					if (schemes.containsKey(schemeName))
					{
						player.sendMessage("The scheme name already exists.");
						return;
					}
				}
				
				SchemeBufferTable.getInstance().setScheme(player.getObjectId(), schemeName, new ArrayList<>());
				showGiveBuffsWindow(player);
			}
			catch (Exception e)
			{
				player.sendMessage("Scheme's name must contain up to " + SCHEME_NAME_MAX_LENGTH + " chars.");
			}
			return;
		}
		else if (currentCommand.startsWith("deletescheme"))
		{
			try
			{
				if (!st.hasMoreTokens())
				{
					player.sendMessage("This scheme name is invalid.");
					showGiveBuffsWindow(player);
					return;
				}
				
				final String schemeName = st.nextToken();
				final Map<String, List<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
				if ((schemes != null) && schemes.containsKey(schemeName))
				{
					schemes.remove(schemeName);
				}
			}
			catch (Exception e)
			{
				player.sendMessage("This scheme name is invalid.");
			}
			
			showGiveBuffsWindow(player);
			return;
		}
		else if (currentCommand.startsWith("manual"))
		{
			final String category = st.hasMoreTokens() ? st.nextToken() : "Buffs";
			
			final int page;
			try
			{
				page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			}
			catch (NumberFormatException e)
			{
				showMainMenu(player);
				return;
			}
			
			showManualWindow(player, category, page, st.hasMoreTokens() ? st.nextToken() : "me");
			return;
		}
		else if (currentCommand.startsWith("castbuff"))
		{
			if (st.countTokens() < 4)
			{
				return;
			}
			
			try
			{
				final int skillId = Integer.parseInt(st.nextToken());
				final String category = st.nextToken();
				final int page = Integer.parseInt(st.nextToken());
				final String targetType = st.nextToken();
				
				final Creature target = "pet".equalsIgnoreCase(targetType) ? player.getSummon() : player;
				if (target == null)
				{
					player.sendMessage("You don't have a pet.");
					showManualWindow(player, category, page, targetType);
					return;
				}
				
				final BuffSkillHolder holder = SchemeBufferTable.getInstance().getAvailableBuff(category, skillId);
				if (holder != null)
				{
					final Skill skill = SkillData.getInstance().getSkill(skillId, holder.getLevel());
					if (skill != null)
					{
						skill.applyEffects(this, target);
					}
				}
				
				showManualWindow(player, category, page, targetType);
			}
			catch (NumberFormatException e)
			{
				return;
			}
			return;
		}
		else if (currentCommand.startsWith("autobuff"))
		{
			applyAutoBuff(player, st.hasMoreTokens() ? st.nextToken() : "me");
			return;
		}
		
		super.onBypassFeedback(player, commandValue);
	}
	
	/**
	 * Returns the HTML path used by this NPC.
	 * @param npcId
	 * @param value
	 * @return the HTML file path.
	 */
	@Override
	public String getHtmlPath(int npcId, int value)
	{
		return "data/html/mods/SchemeBuffer/" + ((value == 0) ? Integer.toString(npcId) : (npcId + "-" + value)) + ".htm";
	}
	
	/**
	 * Shows the main menu window.
	 * @param player
	 */
	private void showMainMenu(Player player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player, getHtmlPath(getId(), 0));
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * Shows the scheme list and actions window.
	 * @param player
	 */
	private void showGiveBuffsWindow(Player player)
	{
		// UI colors (scheme actions).
		final String COLOR_USE_ME = "66FF66"; // light green
		final String COLOR_USE_PET = "FFA500"; // orange
		final String COLOR_EDIT = "FFFF00"; // yellow
		final String COLOR_DELETE = "FF0000"; // red
		
		final StringBuilder sb = new StringBuilder(1024);
		final Map<String, List<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
		
		if ((schemes == null) || schemes.isEmpty())
		{
			sb.append("<center><font color=\"LEVEL\">You haven't defined any scheme.</font></center>");
		}
		else
		{
			for (Entry<String, List<Integer>> scheme : schemes.entrySet())
			{
				final String schemeName = scheme.getKey();
				final int buffCount = scheme.getValue().size();
				final int cost = getFee(scheme.getValue());
				
				sb.append("<table width=270 cellpadding=1 cellspacing=0>").append("<tr>").append("<td width=70 align=left><font color=\"LEVEL\">").append(schemeName).append("</font>").append(" <font color=\"AAAAAA\">[").append(buffCount).append("]</font></td>")
					
					.append("<td width=50 align=center><a action=\"bypass -h npc_%objectId%_givebuffs;").append(schemeName).append(";").append(cost).append("\"><font color=\"").append(COLOR_USE_ME).append("\">Use Me</font></a></td>")
					
					.append("<td width=50 align=center><a action=\"bypass -h npc_%objectId%_givebuffs;").append(schemeName).append(";").append(cost).append(";pet\"><font color=\"").append(COLOR_USE_PET).append("\">Use Pet</font></a></td>")
					
					.append("<td width=50 align=center><a action=\"bypass -h npc_%objectId%_editschemes;Buffs;").append(schemeName).append(";1\"><font color=\"").append(COLOR_EDIT).append("\">Edit</font></a></td>")
					
					.append("<td width=50 align=center><a action=\"bypass -h npc_%objectId%_deletescheme;").append(schemeName).append("\"><font color=\"").append(COLOR_DELETE).append("\">Delete</font></a></td>").append("</tr>").append("</table>").append("<img src=\"L2UI.SquareGray\" width=270 height=1><br1>");
			}
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player, getHtmlPath(getId(), 1));
		html.replace("%schemes%", sb.toString());
		html.replace("%max_schemes%", SchemeBufferConfig.BUFFER_MAX_SCHEMES);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * Shows the scheme edit window for adding and removing skills.
	 * @param player
	 * @param groupType
	 * @param schemeName
	 * @param page
	 */
	private void showEditSchemeWindow(Player player, String groupType, String schemeName, int page)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		final List<Integer> schemeSkills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
		
		html.setFile(player, getHtmlPath(getId(), 2));
		html.replace("%schemename%", schemeName);
		html.replace("%count%", (getCountOf(schemeSkills, false) + getCountOf(schemeSkills, true)) + " / " + player.getStat().getMaxBuffCount() + " buffs");
		html.replace("%typesframe%", getTypesFrame(groupType, schemeName));
		html.replace("%skilllistframe%", getGroupSkillList(player, groupType, schemeName, page));
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * Shows manual casting window by category with pagination and target selection.
	 * @param player
	 * @param category
	 * @param pageValue
	 * @param targetType
	 */
	private void showManualWindow(Player player, String category, int pageValue, String targetType)
	{
		List<Integer> skills = SchemeBufferTable.getInstance().getSkillsIdsByType(category);
		if (skills.isEmpty())
		{
			player.sendMessage("That category doesn't contain any skills.");
			return;
		}
		
		final int max = HtmlUtil.countPageNumber(skills.size(), PAGE_LIMIT);
		int page = pageValue;
		if (page > max)
		{
			page = max;
		}
		if (page < 1)
		{
			page = 1;
		}
		
		skills = skills.subList((page - 1) * PAGE_LIMIT, Math.min(page * PAGE_LIMIT, skills.size()));
		final StringBuilder sb = new StringBuilder(skills.size() * 220);
		
		// Category tabs removed (main menu already provides category navigation)
		// Target selector
		sb.append("<table width=270 cellpadding=1 cellspacing=0><tr>");
		sb.append("<td width=135 align=center>");
		if ("me".equalsIgnoreCase(targetType))
		{
			sb.append("<font color=\"LEVEL\">Me</font>");
		}
		else
		{
			sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_manual;").append(category).append(";").append(page).append(";me\">Me</a>");
		}
		sb.append("</td>");
		sb.append("<td width=135 align=center>");
		if ("pet".equalsIgnoreCase(targetType))
		{
			sb.append("<font color=\"LEVEL\">Pet</font>");
		}
		else
		{
			sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_manual;").append(category).append(";").append(page).append(";pet\">Pet</a>");
		}
		sb.append("</td>");
		sb.append("</tr></table><br1>");
		
		// Skills list as boxed slots + Cast button
		for (int skillId : skills)
		{
			final BuffSkillHolder holder = SchemeBufferTable.getInstance().getAvailableBuff(category, skillId);
			if (holder == null)
			{
				continue;
			}
			
			final Skill skill = SkillData.getInstance().getSkill(skillId, holder.getLevel());
			if (skill == null)
			{
				continue;
			}
			
			// top border
			sb.append("<img src=\"L2UI.SquareGray\" width=270 height=1>");
			
			sb.append("<table width=270 cellpadding=2 cellspacing=0><tr>");
			sb.append("<td width=1><img src=\"L2UI.SquareGray\" width=1 height=40></td>");
			sb.append("<td width=36 height=40 align=center><img src=\"").append(skill.getIcon()).append("\" width=32 height=32></td>");
			sb.append("<td width=176 align=left>").append(skill.getName()).append("<br1><font color=\"B09878\">").append(holder.getDescription()).append("</font></td>");
			sb.append("<td width=50 align=center>").append("<button value=\"Cast\" action=\"bypass -h npc_").append(getObjectId()).append("_castbuff;").append(skillId).append(";").append(category).append(";").append(page).append(";").append(targetType).append("\" width=48 height=20 back=\"L2UI.pledgeButten2\" fore=\"L2UI.pledgeButten1\">").append("</td>");
			sb.append("<td width=1><img src=\"L2UI.SquareGray\" width=1 height=40></td>");
			sb.append("</tr></table>");
			
			// bottom border + spacing
			sb.append("<img src=\"L2UI.SquareGray\" width=270 height=1><br1>");
		}
		
		// Pagination
		sb.append("<img src=\"L2UI.SquareGray\" width=270 height=1>");
		sb.append("<table width=270 cellpadding=2 cellspacing=0><tr>");
		if (page > 1)
		{
			sb.append("<td align=left width=70><a action=\"bypass -h npc_").append(getObjectId()).append("_manual;").append(category).append(";").append(page - 1).append(";").append(targetType).append("\">Previous</a></td>");
		}
		else
		{
			sb.append("<td align=left width=70><font color=\"AAAAAA\">Previous</font></td>");
		}
		
		sb.append("<td align=center width=130>Page ").append(page).append(" / ").append(max).append("</td>");
		
		if (page < max)
		{
			sb.append("<td align=right width=70><a action=\"bypass -h npc_").append(getObjectId()).append("_manual;").append(category).append(";").append(page + 1).append(";").append(targetType).append("\">Next</a></td>");
		}
		else
		{
			sb.append("<td align=right width=70><font color=\"AAAAAA\">Next</font></td>");
		}
		sb.append("</tr></table>");
		sb.append("<img src=\"L2UI.SquareGray\" width=270 height=1><br1>");
		sb.append("<center><a action=\"bypass -h npc_").append(getObjectId()).append("_menu\">Back</a></center>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player, getHtmlPath(getId(), 3));
		html.replace("%category%", category);
		html.replace("%skills%", sb.toString());
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * Applies auto-buff preset based on player class to player or pet.
	 * @param player
	 * @param targetType
	 */
	private void applyAutoBuff(Player player, String targetType)
	{
		final Creature target = "pet".equalsIgnoreCase(targetType) ? player.getSummon() : player;
		if (target == null)
		{
			player.sendMessage("You don't have a pet.");
			showMainMenu(player);
			return;
		}
		
		final String category = player.isMageClass() ? "MAGE_GROUP" : "FIGHTER_GROUP";
		final List<Integer> skills = SchemeBufferTable.getInstance().getSkillsIdsByType(category);
		
		if (skills.isEmpty())
		{
			player.sendMessage("Auto buff configuration is not available.");
			showMainMenu(player);
			return;
		}
		
		for (int skillId : skills)
		{
			final BuffSkillHolder holder = SchemeBufferTable.getInstance().getAvailableBuff(category, skillId);
			if (holder == null)
			{
				continue;
			}
			
			final Skill skill = SkillData.getInstance().getSkill(skillId, holder.getLevel());
			if (skill != null)
			{
				skill.applyEffects(this, target);
			}
		}
		
		player.sendMessage("Auto buff applied successfully!");
		showMainMenu(player);
	}
	
	/**
	 * Builds paginated skill list for scheme edit window.
	 * @param player
	 * @param groupType
	 * @param schemeName
	 * @param pageValue
	 * @return the HTML fragment.
	 */
	private String getGroupSkillList(Player player, String groupType, String schemeName, int pageValue)
	{
		List<Integer> skills = SchemeBufferTable.getInstance().getSkillsIdsByType(groupType);
		if (skills.isEmpty())
		{
			return "<center><font color=\"LEVEL\">That group doesn't contain any skills.</font></center>";
		}
		
		final int max = HtmlUtil.countPageNumber(skills.size(), PAGE_LIMIT);
		int page = pageValue;
		if (page > max)
		{
			page = max;
		}
		if (page < 1)
		{
			page = 1;
		}
		
		skills = skills.subList((page - 1) * PAGE_LIMIT, Math.min(page * PAGE_LIMIT, skills.size()));
		final List<Integer> schemeSkills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
		
		final StringBuilder sb = new StringBuilder(skills.size() * 220);
		
		for (int skillId : skills)
		{
			final BuffSkillHolder holder = SchemeBufferTable.getInstance().getAvailableBuff(groupType, skillId);
			if (holder == null)
			{
				continue;
			}
			
			final Skill skill = SkillData.getInstance().getSkill(skillId, 1);
			if (skill == null)
			{
				continue;
			}
			
			final boolean selected = schemeSkills.contains(skillId);
			
			// Slot row (boxed)
			sb.append("<table width=270 cellpadding=3 cellspacing=0><tr>");
			sb.append("<td width=36 align=center><img src=\"").append(skill.getIcon()).append("\" width=32 height=32></td>");
			sb.append("<td width=184 align=left>").append(skill.getName()).append("<br1><font color=\"B09878\">").append(holder.getDescription()).append("</font></td>");
			
			if (selected)
			{
				sb.append("<td width=50 align=center><button value=\"Delete\" action=\"bypass -h npc_%objectId%_skillunselect;").append(groupType).append(";").append(schemeName).append(";").append(skillId).append(";").append(page).append("\" width=48 height=20 back=\"L2UI.pledgeButten2\" fore=\"L2UI.pledgeButten1\"></td>");
			}
			else
			{
				sb.append("<td width=50 align=center><button value=\"Add\" action=\"bypass -h npc_%objectId%_skillselect;").append(groupType).append(";").append(schemeName).append(";").append(skillId).append(";").append(page).append("\" width=48 height=20 back=\"L2UI.pledgeButten2\" fore=\"L2UI.pledgeButten1\"></td>");
			}
			
			sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=270 height=1><br1>");
		}
		
		// Pager
		sb.append("<table width=270 cellpadding=2 cellspacing=0><tr>");
		if (page > 1)
		{
			sb.append("<td align=left width=70><a action=\"bypass -h npc_%objectId%_editschemes;").append(groupType).append(";").append(schemeName).append(";").append(page - 1).append("\">Previous</a></td>");
		}
		else
		{
			sb.append("<td align=left width=70><font color=\"AAAAAA\">Previous</font></td>");
		}
		
		sb.append("<td align=center width=130>Page ").append(page).append(" / ").append(max).append("</td>");
		
		if (page < max)
		{
			sb.append("<td align=right width=70><a action=\"bypass -h npc_%objectId%_editschemes;").append(groupType).append(";").append(schemeName).append(";").append(page + 1).append("\">Next</a></td>");
		}
		else
		{
			sb.append("<td align=right width=70><font color=\"AAAAAA\">Next</font></td>");
		}
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=270 height=1>");
		
		return sb.toString();
	}
	
	/**
	 * Builds the category selector frame for scheme editing.
	 * @param groupType
	 * @param schemeName
	 * @return the HTML fragment.
	 */
	private static String getTypesFrame(String groupType, String schemeName)
	{
		final StringBuilder sb = new StringBuilder(500);
		sb.append("<table>");
		
		int count = 0;
		for (String type : SchemeBufferTable.getInstance().getSkillTypes())
		{
			if (count == 0)
			{
				sb.append("<tr>");
			}
			
			if (groupType.equalsIgnoreCase(type))
			{
				sb.append("<td width=65>").append(type).append("</td>");
			}
			else
			{
				sb.append("<td width=65><a action=\"bypass -h npc_%objectId%_editschemes;").append(type).append(";").append(schemeName).append(";1\">").append(type).append("</a></td>");
			}
			
			count++;
			if (count == TYPES_PER_ROW)
			{
				sb.append("</tr>");
				count = 0;
			}
		}
		
		if (count != 0)
		{
			sb.append("</tr>");
		}
		
		sb.append("</table>");
		return sb.toString();
	}
	
	/**
	 * Computes the total fee for a skill list.
	 * @param list
	 * @return the fee.
	 */
	private static int getFee(List<Integer> list)
	{
		if (SchemeBufferConfig.BUFFER_STATIC_BUFF_COST > 0)
		{
			return list.size() * SchemeBufferConfig.BUFFER_STATIC_BUFF_COST;
		}
		
		int fee = 0;
		for (int skillId : list)
		{
			final BuffSkillHolder holder = SchemeBufferTable.getInstance().getAvailableBuff(skillId);
			if (holder != null)
			{
				fee += holder.getPrice();
			}
		}
		return fee;
	}
	
	/**
	 * Counts skills in a list as dances/songs or non-dances.
	 * @param skills
	 * @param dances
	 * @return the count.
	 */
	private static int getCountOf(List<Integer> skills, boolean dances)
	{
		int count = 0;
		for (int skillId : skills)
		{
			final Skill skill = SkillData.getInstance().getSkill(skillId, 1);
			if ((skill != null) && (skill.isDance() == dances))
			{
				count++;
			}
		}
		return count;
	}
}