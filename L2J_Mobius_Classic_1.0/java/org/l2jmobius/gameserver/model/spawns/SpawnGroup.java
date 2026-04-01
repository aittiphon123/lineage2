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
package org.l2jmobius.gameserver.model.spawns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.interfaces.IParameterized;
import org.l2jmobius.gameserver.model.interfaces.ITerritorized;
import org.l2jmobius.gameserver.model.zone.type.BannedSpawnTerritory;
import org.l2jmobius.gameserver.model.zone.type.SpawnTerritory;

/**
 * @author UnAfraid, Mobius
 */
public class SpawnGroup implements Cloneable, ITerritorized, IParameterized<StatSet>
{
	private final String _name;
	private final boolean _spawnByDefault;
	private List<SpawnTerritory> _territories;
	private List<BannedSpawnTerritory> _bannedTerritories;
	private final List<NpcSpawnTemplate> _spawns = new ArrayList<>();
	private StatSet _parameters;
	
	public SpawnGroup(StatSet set)
	{
		this(set.getString("name", null), set.getBoolean("spawnByDefault", true));
	}
	
	private SpawnGroup(String name, boolean spawnByDefault)
	{
		_name = name;
		_spawnByDefault = spawnByDefault;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public boolean isSpawningByDefault()
	{
		return _spawnByDefault;
	}
	
	public void addSpawn(NpcSpawnTemplate template)
	{
		_spawns.add(template);
	}
	
	public List<NpcSpawnTemplate> getSpawns()
	{
		return _spawns;
	}
	
	@Override
	public void addTerritory(SpawnTerritory territory)
	{
		if (_territories == null)
		{
			_territories = new ArrayList<>();
		}
		
		_territories.add(territory);
	}
	
	@Override
	public List<SpawnTerritory> getTerritories()
	{
		return _territories != null ? _territories : Collections.emptyList();
	}
	
	@Override
	public void addBannedTerritory(BannedSpawnTerritory territory)
	{
		if (_bannedTerritories == null)
		{
			_bannedTerritories = new ArrayList<>();
		}
		
		_bannedTerritories.add(territory);
	}
	
	@Override
	public List<BannedSpawnTerritory> getBannedTerritories()
	{
		return _bannedTerritories != null ? _bannedTerritories : Collections.emptyList();
	}
	
	@Override
	public StatSet getParameters()
	{
		return _parameters;
	}
	
	@Override
	public void setParameters(StatSet parameters)
	{
		_parameters = parameters;
	}
	
	public List<NpcSpawnTemplate> getSpawnsById(int id)
	{
		List<NpcSpawnTemplate> result = new ArrayList<>();
		for (NpcSpawnTemplate spawn : _spawns)
		{
			if (spawn.getId() == id)
			{
				result.add(spawn);
			}
		}
		
		return result;
	}
	
	public void spawnAll()
	{
		spawnAll(null);
	}
	
	public void spawnAll(Instance instance)
	{
		_spawns.forEach(template -> template.spawn(instance));
	}
	
	public void despawnAll()
	{
		_spawns.forEach(NpcSpawnTemplate::despawn);
	}
	
	@Override
	public SpawnGroup clone()
	{
		final SpawnGroup group = new SpawnGroup(_name, _spawnByDefault);
		
		// Clone banned territories
		for (BannedSpawnTerritory territory : getBannedTerritories())
		{
			group.addBannedTerritory(territory);
		}
		
		// Clone territories
		for (SpawnTerritory territory : getTerritories())
		{
			group.addTerritory(territory);
		}
		
		// Clone spawns
		for (NpcSpawnTemplate spawn : _spawns)
		{
			group.addSpawn(spawn.clone());
		}
		
		return group;
	}
}
