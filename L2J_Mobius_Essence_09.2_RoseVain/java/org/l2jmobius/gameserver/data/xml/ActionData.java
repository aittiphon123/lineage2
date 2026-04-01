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
package org.l2jmobius.gameserver.data.xml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.data.holders.ActionDataHolder;

/**
 * Loads and manages player action data from XML.<br>
 * Maps action IDs to handlers and maintains relationships between skills and actions.
 * @author UnAfraid, Mobius
 */
public class ActionData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ActionData.class.getName());
	
	// Data storage.
	private final Map<Integer, ActionDataHolder> _actionData = new HashMap<>();
	private final Map<Integer, Integer> _actionSkillData = new HashMap<>();
	private int[] _actionIds;
	
	protected ActionData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_actionData.clear();
		_actionSkillData.clear();
		parseDatapackFile("data/ActionData.xml");
		
		// Cache action IDs.
		_actionIds = _actionData.keySet().stream().mapToInt(Number::intValue).toArray();
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _actionData.size() + " player actions.");
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, "list", listNode -> forEach(listNode, "action", actionNode ->
		{
			final NamedNodeMap attrs = actionNode.getAttributes();
			final int id = parseInteger(attrs, "id", 0);
			final String handler = parseString(attrs, "handler", "");
			final int optionId = parseInteger(attrs, "option", 0);
			
			if (handler.equals("PetSkillUse") || handler.equals("ServitorSkillUse"))
			{
				_actionSkillData.put(optionId, id);
			}
			
			_actionData.put(id, new ActionDataHolder(id, handler, optionId));
		}));
	}
	
	/**
	 * Retrieves the action data associated with the specified action ID.
	 * @param id the unique identifier of the action
	 * @return the {@link ActionDataHolder} associated with the given ID, or {@code null} if not found
	 */
	public ActionDataHolder getActionData(int id)
	{
		return _actionData.get(id);
	}
	
	/**
	 * Retrieves the action ID associated with a specific skill ID.
	 * @param skillId the unique identifier of the skill
	 * @return the action ID associated with the specified skill ID, or {@code -1} if not found
	 */
	public int getSkillActionId(int skillId)
	{
		return _actionSkillData.getOrDefault(skillId, -1);
	}
	
	/**
	 * Retrieves a list of all action IDs from the cached array.
	 * @return an array of all action IDs available in the action data
	 */
	public int[] getActionIdList()
	{
		return _actionIds;
	}
	
	public static ActionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ActionData INSTANCE = new ActionData();
	}
}
