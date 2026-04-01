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
package org.l2jmobius.gameserver.data;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.config.custom.SchemeBufferConfig;
import org.l2jmobius.gameserver.model.actor.holders.npc.BuffSkillHolder;

/**
 * Loads Scheme Buffer categories and skills from XML and persists player schemes into memory and database.<br>
 * Maintains category-aware skill lists to support duplicated skill IDs across different categories.
 * <ul>
 * <li>Loads available skills from {@value #SKILLS_XML_PATH}.</li>
 * <li>Loads and saves player schemes from/to database.</li>
 * <li>Provides category-aware lookups to avoid conflicts for manual pages (e.g. Resist).</li>
 * </ul>
 * @author Mobius, BazookaRpm
 */
public class SchemeBufferTable
{
	private static final Logger LOGGER = Logger.getLogger(SchemeBufferTable.class.getName());
	
	// Constants.
	private static final String SKILLS_XML_PATH = "./data/SchemeBufferSkills.xml";
	private static final String LOAD_SCHEMES = "SELECT * FROM buffer_schemes";
	private static final String DELETE_SCHEMES = "TRUNCATE TABLE buffer_schemes";
	private static final String INSERT_SCHEME = "INSERT INTO buffer_schemes (object_id, scheme_name, skills) VALUES (?,?,?)";
	private static final String TYPE_MAGE_GROUP = "MAGE_GROUP";
	private static final String TYPE_FIGHTER_GROUP = "FIGHTER_GROUP";
	
	// Player Schemes Storage.
	private final Map<Integer, Map<String, List<Integer>>> _schemesTable = new ConcurrentHashMap<>();
	
	// Skills Registry.
	private final Map<Integer, BuffSkillHolder> _availableBuffs = new LinkedHashMap<>();
	private final Map<String, List<Integer>> _skillIdsByType = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private final Map<String, Map<Integer, BuffSkillHolder>> _availableBuffsByType = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private final List<String> _skillTypesOrder = new ArrayList<>();
	
	/**
	 * Initializes the table by loading XML skills and database player schemes.
	 */
	public SchemeBufferTable()
	{
		loadAvailableBuffs();
		loadPlayerSchemes();
		LOGGER.info("SchemeBufferTable: Loaded " + _schemesTable.size() + " players and " + _availableBuffs.size() + " available skills.");
	}
	
	/**
	 * Saves all loaded schemes to database by truncating and inserting current in-memory data.
	 */
	public void saveSchemes()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement st = con.prepareStatement(DELETE_SCHEMES))
			{
				st.execute();
			}
			
			try (PreparedStatement st = con.prepareStatement(INSERT_SCHEME))
			{
				for (Entry<Integer, Map<String, List<Integer>>> playerEntry : _schemesTable.entrySet())
				{
					for (Entry<String, List<Integer>> schemeEntry : playerEntry.getValue().entrySet())
					{
						final StringBuilder sb = new StringBuilder();
						for (int skillId : schemeEntry.getValue())
						{
							sb.append(skillId).append(',');
						}
						
						if (sb.length() > 0)
						{
							sb.setLength(sb.length() - 1);
						}
						
						st.setInt(1, playerEntry.getKey());
						st.setString(2, schemeEntry.getKey());
						st.setString(3, sb.toString());
						st.addBatch();
					}
				}
				st.executeBatch();
			}
		}
		catch (Exception e)
		{
			LOGGER.warning("SchemeBufferTable: Error while saving schemes. " + e);
		}
	}
	
	/**
	 * Registers or replaces a scheme for the given player.<br>
	 * If the player does not exist yet, it is created, otherwise max schemes limit is enforced.
	 * @param playerId
	 * @param schemeName
	 * @param list
	 */
	public void setScheme(int playerId, String schemeName, List<Integer> list)
	{
		if (!_schemesTable.containsKey(playerId))
		{
			_schemesTable.put(playerId, new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
		}
		else if (_schemesTable.get(playerId).size() >= SchemeBufferConfig.BUFFER_MAX_SCHEMES)
		{
			return;
		}
		
		_schemesTable.get(playerId).put(schemeName, list);
	}
	
	/**
	 * Returns the scheme map for a player.
	 * @param playerId
	 * @return the schemes map or null.
	 */
	public Map<String, List<Integer>> getPlayerSchemes(int playerId)
	{
		return _schemesTable.get(playerId);
	}
	
	/**
	 * Returns the skill ID list for a specific player scheme.
	 * @param playerId
	 * @param schemeName
	 * @return the scheme skill list or an empty list.
	 */
	public List<Integer> getScheme(int playerId, String schemeName)
	{
		if ((_schemesTable.get(playerId) == null) || (_schemesTable.get(playerId).get(schemeName) == null))
		{
			return Collections.emptyList();
		}
		return _schemesTable.get(playerId).get(schemeName);
	}
	
	/**
	 * Checks whether a scheme contains a specific skill ID.
	 * @param playerId
	 * @param schemeName
	 * @param skillId
	 * @return true if the scheme contains the skill ID.
	 */
	public boolean getSchemeContainsSkill(int playerId, String schemeName, int skillId)
	{
		final List<Integer> skills = getScheme(playerId, schemeName);
		if (skills.isEmpty())
		{
			return false;
		}
		
		for (int id : skills)
		{
			if (id == skillId)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a copy of skill IDs belonging to a category/type.
	 * @param groupType
	 * @return the category skill IDs.
	 */
	public List<Integer> getSkillsIdsByType(String groupType)
	{
		final List<Integer> list = _skillIdsByType.get(groupType);
		return (list != null) ? new ArrayList<>(list) : Collections.emptyList();
	}
	
	/**
	 * Returns available category/type names for scheme editing.<br>
	 * Internal groups (MAGE_GROUP / FIGHTER_GROUP) are hidden from the edit UI.
	 * @return the list of visible category names.
	 */
	public List<String> getSkillTypes()
	{
		final List<String> skillTypes = new ArrayList<>(_skillTypesOrder.size());
		for (String type : _skillTypesOrder)
		{
			if (type.equalsIgnoreCase(TYPE_MAGE_GROUP) || type.equalsIgnoreCase(TYPE_FIGHTER_GROUP))
			{
				continue;
			}
			skillTypes.add(type);
		}
		return skillTypes;
	}
	
	/**
	 * Returns the global holder for a skill ID (compatibility path).<br>
	 * If duplicated IDs exist, "last loaded wins" applies for this global map.
	 * @param skillId
	 * @return the global holder or null.
	 */
	public BuffSkillHolder getAvailableBuff(int skillId)
	{
		return _availableBuffs.get(skillId);
	}
	
	/**
	 * Returns the category-aware holder for a skill ID.<br>
	 * If not found under the category, falls back to the global holder map.
	 * @param groupType
	 * @param skillId
	 * @return the resolved holder or null.
	 */
	public BuffSkillHolder getAvailableBuff(String groupType, int skillId)
	{
		final Map<Integer, BuffSkillHolder> map = _availableBuffsByType.get(groupType);
		final BuffSkillHolder holder = (map != null) ? map.get(skillId) : null;
		return (holder != null) ? holder : _availableBuffs.get(skillId);
	}
	
	/**
	 * Returns the singleton instance.
	 * @return the instance.
	 */
	public static SchemeBufferTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private void loadAvailableBuffs()
	{
		int categories = 0;
		int skills = 0;
		
		try
		{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(new File(SKILLS_XML_PATH));
			final Node root = document.getFirstChild();
			
			for (Node categoryNode = root.getFirstChild(); categoryNode != null; categoryNode = categoryNode.getNextSibling())
			{
				if (!"category".equalsIgnoreCase(categoryNode.getNodeName()))
				{
					continue;
				}
				
				final Node typeNode = categoryNode.getAttributes().getNamedItem("type");
				if (typeNode == null)
				{
					continue;
				}
				
				final String category = typeNode.getNodeValue();
				if (!_skillIdsByType.containsKey(category))
				{
					_skillIdsByType.put(category, new ArrayList<>());
					_availableBuffsByType.put(category, new LinkedHashMap<>());
					_skillTypesOrder.add(category);
					categories++;
				}
				
				for (Node buffNode = categoryNode.getFirstChild(); buffNode != null; buffNode = buffNode.getNextSibling())
				{
					if (!"buff".equalsIgnoreCase(buffNode.getNodeName()))
					{
						continue;
					}
					
					final NamedNodeMap attrs = buffNode.getAttributes();
					if (attrs == null)
					{
						continue;
					}
					
					final Node idNode = attrs.getNamedItem("id");
					final Node levelNode = attrs.getNamedItem("level");
					final Node priceNode = attrs.getNamedItem("price");
					if ((idNode == null) || (levelNode == null) || (priceNode == null))
					{
						continue;
					}
					
					final int skillId = Integer.parseInt(idNode.getNodeValue());
					final int level = Integer.parseInt(levelNode.getNodeValue());
					final int price = Integer.parseInt(priceNode.getNodeValue());
					final Node descNode = attrs.getNamedItem("desc");
					final String desc = (descNode != null) ? descNode.getNodeValue() : "";
					
					final List<Integer> ids = _skillIdsByType.get(category);
					if (!ids.contains(skillId))
					{
						ids.add(skillId);
					}
					
					final BuffSkillHolder holder = new BuffSkillHolder(skillId, level, price, category, desc);
					_availableBuffsByType.get(category).put(skillId, holder);
					_availableBuffs.put(skillId, holder);
					skills++;
				}
			}
			
			LOGGER.info("SchemeBufferTable: Loaded " + categories + " categories and " + skills + " entries from " + SKILLS_XML_PATH + ".");
		}
		catch (Exception e)
		{
			LOGGER.warning("SchemeBufferTable: Failed to load " + SKILLS_XML_PATH + ". " + e);
		}
	}
	
	private void loadPlayerSchemes()
	{
		int entryCount = 0;
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement st = con.prepareStatement(LOAD_SCHEMES);
			ResultSet rs = st.executeQuery())
		{
			while (rs.next())
			{
				final int objectId = rs.getInt("object_id");
				final String schemeName = rs.getString("scheme_name");
				final String skills = rs.getString("skills");
				if ((schemeName == null) || (skills == null))
				{
					continue;
				}
				
				final String[] split = skills.split(",");
				final List<Integer> schemeList = new ArrayList<>(split.length);
				
				for (String token : split)
				{
					if (token.isEmpty())
					{
						break;
					}
					
					final int skillId = Integer.parseInt(token);
					if (_availableBuffs.containsKey(skillId))
					{
						schemeList.add(skillId);
					}
				}
				
				setScheme(objectId, schemeName, schemeList);
				entryCount++;
			}
			
			LOGGER.info("SchemeBufferTable: Loaded " + entryCount + " scheme entries from database.");
		}
		catch (Exception e)
		{
			LOGGER.warning("SchemeBufferTable: Failed to load buff schemes from database. " + e);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final SchemeBufferTable INSTANCE = new SchemeBufferTable();
	}
}
