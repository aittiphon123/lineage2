/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class ActionUserHandler implements IHandler<IActionUserHandler, String>
{
	private final Map<String, IActionUserHandler> _actions = new HashMap<>();
	
	protected ActionUserHandler()
	{
	}
	
	@Override
	public void registerHandler(IActionUserHandler handler)
	{
		_actions.put(handler.getClass().getSimpleName(), handler);
	}
	
	@Override
	public synchronized void removeHandler(IActionUserHandler handler)
	{
		_actions.remove(handler.getClass().getSimpleName());
	}
	
	@Override
	public IActionUserHandler getHandler(String name)
	{
		return _actions.get(name);
	}
	
	@Override
	public int size()
	{
		return _actions.size();
	}
	
	public static ActionUserHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ActionUserHandler INSTANCE = new ActionUserHandler();
	}
}
