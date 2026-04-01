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
package org.l2jmobius.gameserver.model.script;

import java.util.function.Predicate;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;

/**
 * Represents a conditional rule used by the quest scripting system.<br>
 * Binds a boolean condition evaluated against a player instance with an HTML dialog response associated with that condition.
 * @author Galagard
 */
public class QuestCondition
{
	private final Predicate<Player> _condition;
	private final String _html;
	
	/**
	 * Creates a QuestCondition with an HTML dialog that applies regardless of which NPC triggered the condition.
	 * @param condition a predicate that evaluates against a player instance
	 * @param html a single HTML dialog string
	 */
	public QuestCondition(Predicate<Player> condition, String html)
	{
		_condition = condition;
		_html = html;
	}
	
	/**
	 * Evaluates the condition against the given player.
	 * @param player a player instance
	 * @return true if the player satisfies the condition, false otherwise
	 */
	public boolean test(Player player)
	{
		return _condition.test(player);
	}
	
	/**
	 * Retrieves the HTML dialog associated with the given NPC.
	 * @param npc an NPC instance
	 * @return HTML dialog string or null
	 */
	public String getHtml(Npc npc)
	{
		return _html;
	}
}
