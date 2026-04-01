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
package org.l2jmobius.gameserver.data.holders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.item.type.WeaponType;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;

/**
 * @author Brado
 */
public final class CharacterStyleDataHolder
{
	public final int _styleId;
	public final String _name;
	public final WeaponType _weaponType;
	public final int _shiftWeaponId;
	private final SkillHolder _skillHolder;
	public final List<ItemHolder> _cost;
	
	public CharacterStyleDataHolder(int styleId, String name, List<ItemHolder> cost)
	{
		_styleId = styleId;
		_name = name;
		_shiftWeaponId = 0;
		_skillHolder = null;
		_weaponType = WeaponType.NONE;
		_cost = Collections.unmodifiableList(new ArrayList<>(cost));
	}
	
	public CharacterStyleDataHolder(int styleId, String name, int shiftWeaponId, WeaponType weaponType, List<ItemHolder> cost)
	{
		_styleId = styleId;
		_name = name;
		_shiftWeaponId = shiftWeaponId;
		_skillHolder = null;
		_weaponType = weaponType;
		_cost = Collections.unmodifiableList(new ArrayList<>(cost));
	}
	
	public CharacterStyleDataHolder(int styleId, String name, SkillHolder skillHolder, List<ItemHolder> cost)
	{
		_styleId = styleId;
		_name = name;
		_shiftWeaponId = 0;
		_weaponType = WeaponType.NONE;
		_skillHolder = skillHolder;
		_cost = Collections.unmodifiableList(new ArrayList<>(cost));
	}
	
	public WeaponType getWeaponType()
	{
		return _weaponType;
	}
	
	public SkillHolder getSkillHolder()
	{
		return _skillHolder;
	}
	
	public int getShiftWeaponId()
	{
		return _shiftWeaponId;
	}
	
	public int getStyleId()
	{
		return _styleId;
	}
	
	public List<ItemHolder> getCosts()
	{
		return _cost;
	}
	
	@Override
	public String toString()
	{
		return "Style{id=" + _styleId + ", name='" + _name + '\'' + ", shiftWeaponId=" + _shiftWeaponId + ", cost=" + _cost + '}';
	}
}
