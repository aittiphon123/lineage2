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
package org.l2jmobius.gameserver.model.skill;

import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;

/**
 * An Enum to hold some important references to commonly used skills
 * @author DrHouse, Mobius
 */
public enum CommonSkill
{
	FAKE_DEATH(60, 1),
	SEAL_OF_RULER(246, 1),
	FIREWORK(2024, 1),
	LARGE_FIREWORK(2025, 1),
	CREATE_DWARVEN(172, 1),
	LUCKY(194, 1),
	EXPERTISE(239, 1),
	CRYSTALLIZE(248, 1);
	
	private final SkillHolder _holder;
	
	CommonSkill(int id, int level)
	{
		_holder = new SkillHolder(id, level);
	}
	
	public int getId()
	{
		return _holder.getSkillId();
	}
	
	public int getLevel()
	{
		return _holder.getSkillLevel();
	}
	
	public Skill getSkill()
	{
		return _holder.getSkill();
	}
}
