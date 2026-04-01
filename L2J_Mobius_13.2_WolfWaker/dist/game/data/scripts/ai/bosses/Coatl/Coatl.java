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
package ai.bosses.Coatl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.SpawnTable;
import org.l2jmobius.gameserver.data.xml.NpcData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.managers.DatabaseSpawnManager;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.model.spawns.Spawn;
import org.l2jmobius.gameserver.model.zone.type.ArenaZone;

/**
 * Coatl AI
 * @author Notorion
 */
public class Coatl extends Script
{
	// NPCs
	private static final int MAIN_BOSS_ID = 29408; // Coatl RaidBoss
	private static final int INVISIBLE_COATL_ID = 29413; // Coatl invisible
	private static final int FLAME_TOTEM_ID = 29409;
	private static final int KASHA_TOTEM_ID = 29412;
	private static final int EARTH_TOTEM_ID = 29411;
	private static final int WATER_TOTEM_ID = 29410;
	private static final int[] TOTEM_IDS =
	{
		WATER_TOTEM_ID,
		KASHA_TOTEM_ID,
		EARTH_TOTEM_ID,
		FLAME_TOTEM_ID
	};
	
	// Locations
	private static final Location FLAME_TOTEM_LOCATION = new Location(-83613, 209856, -5248, 371);
	private static final Location KASHA_TOTEM_LOCATION = new Location(-84417, 210668, -5254, 16503);
	private static final Location EARTH_TOTEM_LOCATION = new Location(-85226, 209857, -5258, 32689);
	private static final Location WATER_TOTEM_LOCATION = new Location(-84415, 209044, -5258, 49298);
	private static final Location INVISIBLE_COATL_LOCATION = new Location(-84416, 209860, -5254);
	private static final Location SPAWN_LOCATION = new Location(-84416, 209860, -5254);
	private static final Location GLUDIO_EXIT = new Location(-14575, 121425, -3011); // Kick player loc
	
	// Skills
	private static final SkillHolder WATER_TOTEM_SKILL = new SkillHolder(34807, 1); // Totem Protection - Water
	private static final SkillHolder EARTH_TOTEM_SKILL = new SkillHolder(34808, 1); // Totem Protection - Ice
	private static final SkillHolder KASHA_TOTEM_SKILL = new SkillHolder(34809, 1); // Totem Protection - Kasha
	private static final int FLAME_TOTEM_SKILL_ID = 34806; // Totem Protection - Fire
	private static final SkillHolder FLAME_EXPLOSION_SKILL = new SkillHolder(34805, 1); // Coatl's Rage - Fire
	private static final SkillHolder EARTH_EXPLOSION_SKILL = new SkillHolder(34811, 1); // Coatl's Rage - Ice
	private static final SkillHolder KASHA_EXPLOSION_SKILL = new SkillHolder(34812, 1); // Coatl's Rage - Kasha
	private static final SkillHolder WATER_EXPLOSION_SKILL = new SkillHolder(34813, 1); // Coatl's Rage - Water
	private static final SkillHolder SKILL_WATER = new SkillHolder(34807, 1);
	private static final SkillHolder SKILL_KASHA = new SkillHolder(34809, 1);
	private static final SkillHolder SKILL_EARTH = new SkillHolder(34808, 1);
	private static final SkillHolder SKILL_FLAME = new SkillHolder(34806, 1);
	private static final SkillHolder SKILL_1 = new SkillHolder(34789, 1); // Coatl Skill - Coatl's Fireball
	private static final SkillHolder SKILL_2 = new SkillHolder(34790, 1); // Coatl Skill - Coatl's Stone Throw
	private static final SkillHolder SKILL_3 = new SkillHolder(34791, 1); // Coatl Skill - Coatl's Kasha Wave
	private static final SkillHolder SKILL_4 = new SkillHolder(34792, 1); // Coatl Skill - Coatl's Frozen Field
	
	private static final SkillHolder BARRIER_INITIAL = new SkillHolder(29518, 1); // Barrier 1
	private static final SkillHolder BARRIER_TIMED = new SkillHolder(29515, 1); // Barrier 2
	
	private static final int HITS_TO_BREAK = 2000; // 2000 hits
	private static final long TIME_RESET_INACTIVITY = 10 * 60000; // 10 Minutes reset HP
	
	// Misc
	private static final ArenaZone ARENA_ZONE = ZoneManager.getInstance().getZoneByName("Coatls_Lair", ArenaZone.class);
	
	private final Set<Integer> _involvedPlayers = new HashSet<>();
	private final Map<Creature, Integer> _aggroList = new ConcurrentHashMap<>();
	private final Map<String, Object[]> _timerParameters = new HashMap<>();
	private final Queue<Runnable> _specialMechanicsQueue = new LinkedList<>();
	
	private boolean _isInitialBarrier = false;
	private boolean _isTimedBarrier = false;
	private final Map<Npc, Integer> _hitCounter = new ConcurrentHashMap<>();
	private long _lastAction;
	
	private boolean _combatLoopStarted = false;
	private boolean _specialMechanicsActive = false;
	private boolean _hp76Triggered = false;
	private boolean _hp70Triggered = false;
	private boolean _hp50Triggered = false;
	private boolean _hp40Triggered = false;
	private boolean _hp30Triggered = false;
	private boolean _hp10Triggered = false;
	private static Npc _spawnedMainBoss;
	private static Npc _invisibleCoatl;
	private static Npc _flameTotem;
	private static Npc _kashaTotem;
	private static Npc _earthTotem;
	private static Npc _waterTotem;
	
	private Coatl()
	{
		addAttackId(MAIN_BOSS_ID);
		addKillId(MAIN_BOSS_ID);
		
		final long currentTime = System.currentTimeMillis();
		final Calendar calendar = Calendar.getInstance();
		
		// Spawn time to 21:00 Monday.
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 21);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		if (GlobalVariablesManager.getInstance().getBoolean("COATL_ALIVE", true))
		{
			spawnCoatl();
		}
		
		if (calendar.getTimeInMillis() < currentTime)
		{
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		}
		
		ThreadPool.scheduleAtFixedRate(this::spawnCoatl, calendar.getTimeInMillis() - currentTime, 604800000);
		startQuestTimer("check_arena", 10000, null, null, true);
	}
	
	private void spawnCoatl()
	{
		final Spawn oldSpawn = SpawnTable.getInstance().getAnySpawn(MAIN_BOSS_ID);
		if (oldSpawn != null)
		{
			if (oldSpawn.getLastSpawn() != null)
			{
				oldSpawn.getLastSpawn().deleteMe();
			}
			
			SpawnTable.getInstance().removeSpawn(oldSpawn);
		}
		
		try
		{
			final NpcTemplate template = NpcData.getInstance().getTemplate(MAIN_BOSS_ID);
			final Spawn spawn = new Spawn(template);
			spawn.setXYZ(SPAWN_LOCATION);
			spawn.setHeading(0);
			spawn.setRespawnDelay(0);
			_spawnedMainBoss = DatabaseSpawnManager.getInstance().addNewSpawn(spawn, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		GlobalVariablesManager.getInstance().set("COATL_ALIVE", true);
		// System.out.println("Coatl Manager: Coatl (29408) spawned successfully.");
		
		// Spawn.
		_invisibleCoatl = addSpawn(INVISIBLE_COATL_ID, INVISIBLE_COATL_LOCATION);
		_invisibleCoatl.setImmobilized(true);
		_flameTotem = addSpawn(FLAME_TOTEM_ID, FLAME_TOTEM_LOCATION);
		_flameTotem.setImmobilized(true);
		_kashaTotem = addSpawn(KASHA_TOTEM_ID, KASHA_TOTEM_LOCATION);
		_kashaTotem.setImmobilized(true);
		_earthTotem = addSpawn(EARTH_TOTEM_ID, EARTH_TOTEM_LOCATION);
		_earthTotem.setImmobilized(true);
		_waterTotem = addSpawn(WATER_TOTEM_ID, WATER_TOTEM_LOCATION);
		_waterTotem.setImmobilized(true);
		
		applyInitialBarrier(_spawnedMainBoss);
		
		_lastAction = System.currentTimeMillis();
		startQuestTimer("check_activity_task", 60000, null, null);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "check_arena":
			{
				if (_spawnedMainBoss != null)
				{
					checkPlayersInArena();
					checkBossHP();
				}
				break;
			}
			case "check_activity_task":
			{
				if ((_lastAction + TIME_RESET_INACTIVITY) < System.currentTimeMillis())
				{
					if ((_spawnedMainBoss != null) && !_spawnedMainBoss.isDead())
					{
						if (_spawnedMainBoss.getCurrentHp() < _spawnedMainBoss.getMaxHp())
						{
							resetCoatl(_spawnedMainBoss);
						}
					}
				}
				else
				{
					startQuestTimer("check_activity_task", 60000, null, null);
				}
				break;
			}
			case "SKILL_LOOP":
			{
				if ((_spawnedMainBoss != null) && !_spawnedMainBoss.isDead())
				{
					if (!_specialMechanicsActive && !_spawnedMainBoss.isCastingNow())
					{
						manageSkills(_spawnedMainBoss);
					}
					
					boolean playersInZone = false;
					if ((ARENA_ZONE != null) && !ARENA_ZONE.getCharactersInside().isEmpty())
					{
						for (Creature c : ARENA_ZONE.getCharactersInside())
						{
							if (c.isPlayer() && !c.isDead())
							{
								playersInZone = true;
								break;
							}
						}
					}
					
					if (playersInZone)
					{
						startQuestTimer("SKILL_LOOP", 250, _spawnedMainBoss, null);
					}
					else
					{
						_combatLoopStarted = false;
					}
				}
				else
				{
					_combatLoopStarted = false;
				}
				break;
			}
			case "END_VULNERABILITY":
			{
				if ((npc != null) && !npc.isDead())
				{
					applyTimedBarrier(npc);
				}
				break;
			}
			case "END_TIMED_BARRIER":
			{
				if ((npc != null) && !npc.isDead() && _isTimedBarrier)
				{
					applyTimedBarrier(npc);
				}
				break;
			}
			case "castWaterTotemSkill":
			{
				if (npc != null)
				{
					npc.setTarget(npc);
					npc.doCast(WATER_TOTEM_SKILL.getSkill());
				}
				break;
			}
			case "castKashaTotemSkill":
			{
				if (npc != null)
				{
					npc.setTarget(npc);
					npc.doCast(KASHA_TOTEM_SKILL.getSkill());
				}
				break;
			}
			case "castEarthTotemSkill":
			{
				if (npc != null)
				{
					npc.setTarget(npc);
					npc.doCast(EARTH_TOTEM_SKILL.getSkill());
				}
				break;
			}
			case "castFlameTotemSkill":
			{
				if (npc != null)
				{
					npc.setTarget(npc);
					final Skill flameTotemSkill = SkillData.getInstance().getSkill(FLAME_TOTEM_SKILL_ID, 1);
					if (flameTotemSkill != null)
					{
						npc.doCast(flameTotemSkill);
					}
				}
				break;
			}
			case "dispel_boss_buffs":
			{
				dispelBossBuffs(_spawnedMainBoss);
				break;
			}
			case "waterTotemMechanicsEnd":
			{
				final Object[] parameters = _timerParameters.get("waterTotemMechanicsEnd");
				if (parameters != null)
				{
					final int waterBuffId = (int) parameters[0];
					final int effectSkillId = (int) parameters[1];
					cancelQuestTimer("coatl_common_skills", _spawnedMainBoss, null);
					killPlayersInArena(waterBuffId);
					castSkillOnTotems(SkillData.getInstance().getSkill(effectSkillId, 1));
					cancelTotemTimers();
				}
				break;
			}
			case "kashaTotemMechanicsEnd":
			{
				final Object[] parameters = _timerParameters.get("kashaTotemMechanicsEnd");
				if (parameters != null)
				{
					final int kashaBuffId = (int) parameters[0];
					final int effectSkillId = (int) parameters[1];
					cancelQuestTimer("coatl_common_skills", _spawnedMainBoss, null);
					killPlayersInArena(kashaBuffId);
					castSkillOnTotems(SkillData.getInstance().getSkill(effectSkillId, 1));
					cancelTotemTimers();
				}
				break;
			}
			case "earthTotemMechanicsEnd":
			{
				final Object[] parameters = _timerParameters.get("earthTotemMechanicsEnd");
				if (parameters != null)
				{
					final int earthBuffId = (int) parameters[0];
					final int effectSkillId = (int) parameters[1];
					cancelQuestTimer("coatl_common_skills", _spawnedMainBoss, null);
					killPlayersInArena(earthBuffId);
					castSkillOnTotems(SkillData.getInstance().getSkill(effectSkillId, 1));
					cancelTotemTimers();
				}
				break;
			}
			case "flameTotemMechanicsEnd":
			{
				final Object[] parameters = _timerParameters.get("flameTotemMechanicsEnd");
				if (parameters != null)
				{
					final int flameBuffId = (int) parameters[0];
					final int effectSkillId = (int) parameters[1];
					cancelQuestTimer("coatl_common_skills", _spawnedMainBoss, null);
					killPlayersInArena(flameBuffId);
					castSkillOnTotems(SkillData.getInstance().getSkill(effectSkillId, 1));
					cancelTotemTimers();
				}
				break;
			}
			case "castWaterExplosionSkill":
			{
				if (npc != null)
				{
					npc.setTarget(npc);
					npc.doCast(WATER_EXPLOSION_SKILL.getSkill());
				}
				break;
			}
			case "castKashaExplosionSkill":
			{
				if (npc != null)
				{
					npc.setTarget(npc);
					npc.doCast(KASHA_EXPLOSION_SKILL.getSkill());
				}
				break;
			}
			case "castEarthExplosionSkill":
			{
				if (npc != null)
				{
					npc.setTarget(npc);
					npc.doCast(EARTH_EXPLOSION_SKILL.getSkill());
				}
				break;
			}
			case "castFlameExplosionSkill":
			{
				if (npc != null)
				{
					npc.setTarget(npc);
					npc.doCast(FLAME_EXPLOSION_SKILL.getSkill());
				}
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	private void addAggro(Creature attacker, int damage)
	{
		if ((attacker == null) || attacker.isDead())
		{
			return;
		}
		
		final int newAggroVal = damage + getRandom(3000);
		final int aggroVal = _aggroList.getOrDefault(attacker, 0) + 1000;
		if (aggroVal < newAggroVal)
		{
			_aggroList.put(attacker, newAggroVal);
		}
	}
	
	private void manageSkills(Npc npc)
	{
		if (npc.isCastingNow() || npc.isCoreAIDisabled())
		{
			return;
		}
		
		if (_specialMechanicsActive)
		{
			return;
		}
		
		if (npc.isAttackingNow())
		{
			npc.abortAttack();
		}
		
		_aggroList.forEach((attacker, aggro) ->
		{
			if ((attacker == null) || attacker.isDead() || (npc.calculateDistance3D(attacker) > 3000))
			{
				_aggroList.remove(attacker);
			}
		});
		
		final Creature target = getTargetLogic(npc);
		if ((target == null) || target.isDead())
		{
			return;
		}
		
		npc.setTarget(target);
		
		SkillHolder skillToCast = null;
		final int randomSkill = getRandom(100);
		
		if (randomSkill < 20)
		{
			skillToCast = SKILL_4; // 20%
		}
		else if (randomSkill < 40)
		{
			skillToCast = SKILL_3; // 20%
		}
		else if (randomSkill < 70)
		{
			skillToCast = SKILL_2; // 30%
		}
		else
		{
			skillToCast = SKILL_1; // 30%
		}
		
		if (SkillCaster.checkUseConditions(npc, skillToCast.getSkill()))
		{
			npc.doCast(skillToCast.getSkill());
		}
		else if (SkillCaster.checkUseConditions(npc, SKILL_2.getSkill()))
		{
			npc.doCast(SKILL_2.getSkill());
		}
		else if (SkillCaster.checkUseConditions(npc, SKILL_1.getSkill()))
		{
			npc.doCast(SKILL_1.getSkill());
		}
	}
	
	private void cancelTotemTimers()
	{
		cancelQuestTimer("castWaterTotemSkill", _waterTotem, null);
		cancelQuestTimer("castKashaTotemSkill", _kashaTotem, null);
		cancelQuestTimer("castEarthTotemSkill", _earthTotem, null);
		cancelQuestTimer("castFlameTotemSkill", _flameTotem, null);
	}
	
	private void killPlayersInArena(int buffId)
	{
		for (Creature creature : ARENA_ZONE.getCharactersInside())
		{
			if (!creature.isPlayer() && !creature.isSummon())
			{
				continue;
			}
			
			boolean hasBuff = false;
			for (BuffInfo effect : creature.getEffectList().getEffects())
			{
				if (effect.getSkill().getId() == buffId)
				{
					hasBuff = true;
					break;
				}
			}
			
			if (!hasBuff)
			{
				creature.doDie(_spawnedMainBoss);
			}
		}
	}
	
	public void castSkillOnTotems(Skill skill)
	{
		if (_waterTotem != null)
		{
			_waterTotem.setTarget(_waterTotem);
			_waterTotem.doCast(skill);
		}
		
		if (_kashaTotem != null)
		{
			_kashaTotem.setTarget(_kashaTotem);
			_kashaTotem.doCast(skill);
		}
		
		if (_earthTotem != null)
		{
			_earthTotem.setTarget(_earthTotem);
			_earthTotem.doCast(skill);
		}
		
		if (_flameTotem != null)
		{
			_flameTotem.setTarget(_flameTotem);
			_flameTotem.doCast(skill);
		}
	}
	
	private void checkPlayersInArena()
	{
		if ((_spawnedMainBoss != null) && !ARENA_ZONE.isInsideZone(_spawnedMainBoss))
		{
			_spawnedMainBoss.stopMove(null);
			_spawnedMainBoss.setXYZ(SPAWN_LOCATION.getX(), SPAWN_LOCATION.getY(), SPAWN_LOCATION.getZ());
			_spawnedMainBoss.setCurrentHp(_spawnedMainBoss.getMaxHp());
			_spawnedMainBoss.broadcastPacket(new org.l2jmobius.gameserver.network.serverpackets.ValidateLocation(_spawnedMainBoss));
		}
		
		if (ARENA_ZONE != null)
		{
			for (Creature creature : ARENA_ZONE.getCharactersInside())
			{
				if (!creature.isPlayer())
				{
					continue;
				}
				if ((creature.getTarget() == _spawnedMainBoss) && !ARENA_ZONE.isInsideZone(creature))
				{
					creature.teleToLocation(GLUDIO_EXIT, true);
					continue;
				}
				if (ARENA_ZONE.isInsideZone(creature))
				{
					if (!_involvedPlayers.contains(creature.getObjectId()))
					{
						_involvedPlayers.add(creature.getObjectId());
					}
				}
			}
		}
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		_lastAction = System.currentTimeMillis();
		
		if ((attacker != null) && (ARENA_ZONE != null) && !ARENA_ZONE.isInsideZone(attacker))
		{
			if (isSummon)
			{
				for (org.l2jmobius.gameserver.model.actor.Summon summon : attacker.getServitors().values())
				{
					if (summon != null)
					{
						summon.unSummon(attacker);
					}
				}
			}
			attacker.teleToLocation(GLUDIO_EXIT, true);
			return;
		}
		
		if (npc.getId() == MAIN_BOSS_ID)
		{
			if (!_combatLoopStarted)
			{
				_combatLoopStarted = true;
				startQuestTimer("SKILL_LOOP", 1000, npc, null);
			}
			
			if (_isInitialBarrier || _isTimedBarrier)
			{
				int hits = _hitCounter.merge(npc, 1, Integer::sum);
				if (hits >= HITS_TO_BREAK)
				{
					enterVulnerableState(npc);
				}
			}
		}
		
		checkBossHP();
		addAggro(attacker, damage);
	}
	
	private void checkBossHP()
	{
		if (_spawnedMainBoss != null)
		{
			final double currentHP = _spawnedMainBoss.getCurrentHp();
			final long maxHP = _spawnedMainBoss.getMaxHp();
			final int currentHPPercentage = (int) ((currentHP / maxHP) * 100);
			if (currentHPPercentage <= 0)
			{
				onNpcDeath(_spawnedMainBoss);
				return;
			}
			
			if ((currentHPPercentage <= 76) && !_hp76Triggered && !_spawnedMainBoss.isCastingNow())
			{
				_hp76Triggered = true;
				_specialMechanicsQueue.offer(this::startRandomTotemMechanics);
				processSpecialMechanicsQueue();
			}
			else if ((currentHPPercentage <= 70) && !_hp70Triggered && !_spawnedMainBoss.isCastingNow())
			{
				_hp70Triggered = true;
				_specialMechanicsQueue.offer(this::startRandomTotemMechanics);
				processSpecialMechanicsQueue();
			}
			else if ((currentHPPercentage <= 50) && !_hp50Triggered && !_spawnedMainBoss.isCastingNow())
			{
				_hp50Triggered = true;
				_specialMechanicsQueue.offer(this::startRandomTotemMechanics);
				processSpecialMechanicsQueue();
			}
			else if ((currentHPPercentage <= 40) && !_hp40Triggered && !_spawnedMainBoss.isCastingNow())
			{
				_hp40Triggered = true;
				_specialMechanicsQueue.offer(this::startRandomTotemMechanics);
				processSpecialMechanicsQueue();
			}
			else if ((currentHPPercentage <= 30) && !_hp30Triggered && !_spawnedMainBoss.isCastingNow())
			{
				_hp30Triggered = true;
				_specialMechanicsQueue.offer(this::startRandomTotemMechanics);
				processSpecialMechanicsQueue();
			}
			else if ((currentHPPercentage <= 10) && !_hp10Triggered && !_spawnedMainBoss.isCastingNow())
			{
				_hp10Triggered = true;
				_specialMechanicsQueue.offer(this::startRandomTotemMechanics);
				processSpecialMechanicsQueue();
			}
		}
	}
	
	private void processSpecialMechanicsQueue()
	{
		if (!_specialMechanicsActive && !_specialMechanicsQueue.isEmpty())
		{
			_specialMechanicsActive = true;
			final Runnable mechanic = _specialMechanicsQueue.poll();
			mechanic.run();
		}
	}
	
	private void dispelBossBuffs(Npc boss)
	{
		if ((boss != null) && !boss.isDead())
		{
			boss.stopSkillEffects(SKILL_WATER.getSkill());
			boss.stopSkillEffects(SKILL_KASHA.getSkill());
			boss.stopSkillEffects(SKILL_EARTH.getSkill());
			boss.stopSkillEffects(SKILL_FLAME.getSkill());
			boss.stopSkillEffects(null, 46797);
		}
	}
	
	private void start78PercentMechanics()
	{
		if (_waterTotem != null)
		{
			_waterTotem.setTarget(_waterTotem);
			_waterTotem.doCast(WATER_TOTEM_SKILL.getSkill());
			startQuestTimer("castWaterTotemSkill", 3100, _waterTotem, null, true);
		}
		
		if (_kashaTotem != null)
		{
			_kashaTotem.setTarget(_kashaTotem);
			_kashaTotem.doCast(KASHA_TOTEM_SKILL.getSkill());
			startQuestTimer("castKashaTotemSkill", 3100, _kashaTotem, null, true);
		}
		
		if (_earthTotem != null)
		{
			_earthTotem.setTarget(_earthTotem);
			_earthTotem.doCast(EARTH_TOTEM_SKILL.getSkill());
			startQuestTimer("castEarthTotemSkill", 3100, _earthTotem, null, true);
		}
		
		if (_flameTotem != null)
		{
			_flameTotem.setTarget(_flameTotem);
			
			final Skill flameTotemSkill = SkillData.getInstance().getSkill(FLAME_TOTEM_SKILL_ID, 1);
			if (flameTotemSkill != null)
			{
				_flameTotem.doCast(flameTotemSkill);
				startQuestTimer("castFlameTotemSkill", 3100, _flameTotem, null, true);
			}
		}
	}
	
	private void startRandomTotemMechanics()
	{
		final int totemIndex = Rnd.get(TOTEM_IDS.length);
		switch (totemIndex)
		{
			case 0: // Water
			{
				startWaterTotemMechanics();
				break;
			}
			case 1: // Kasha
			{
				startKashaTotemMechanics();
				break;
			}
			case 2: // Earth
			{
				startEarthTotemMechanics();
				break;
			}
			case 3: // Flame
			{
				startFlameTotemMechanics();
				break;
			}
		}
		
		_specialMechanicsActive = false;
		processSpecialMechanicsQueue();
		
		startQuestTimer("dispel_boss_buffs", 250, null, null, true);
	}
	
	private void startWaterTotemMechanics()
	{
		final int bossMainCastSkillId = 34800;
		final int coatlInvisibleSkillId = 34796;
		final int waterBuffId = 34807;
		final int effectSkillId = 34810;
		final Skill kashaProtection = SkillData.getInstance().getSkill(34816, 1);
		if (kashaProtection != null)
		{
			kashaProtection.applyEffects(_spawnedMainBoss, _spawnedMainBoss);
		}
		
		Skill skill = SkillData.getInstance().getSkill(bossMainCastSkillId, 1);
		if (!_spawnedMainBoss.isSkillDisabled(skill))
		{
			_spawnedMainBoss.setTarget(_spawnedMainBoss);
			_spawnedMainBoss.doCast(skill);
		}
		
		if (_invisibleCoatl != null)
		{
			skill = SkillData.getInstance().getSkill(coatlInvisibleSkillId, 1);
			if (!_invisibleCoatl.isSkillDisabled(skill))
			{
				_invisibleCoatl.setTarget(_invisibleCoatl);
				_invisibleCoatl.doCast(skill);
			}
		}
		
		start78PercentMechanics();
		_timerParameters.put("waterTotemMechanicsEnd", new Object[]
		{
			waterBuffId,
			effectSkillId
		});
		
		startQuestTimer("waterTotemMechanicsEnd", 9000L, _spawnedMainBoss, null);
		startQuestTimer("castWaterExplosionSkill", 9000L, _invisibleCoatl, null);
	}
	
	private void startKashaTotemMechanics()
	{
		final int bossMainCastSkillId = 34799;
		final int coatlInvisibleSkillId = 34795;
		final int kashaBuffId = 34809;
		final int effectSkillId = 34810;
		final Skill kashaProtection = SkillData.getInstance().getSkill(34816, 1);
		if (kashaProtection != null)
		{
			kashaProtection.applyEffects(_spawnedMainBoss, _spawnedMainBoss);
		}
		
		Skill skill = SkillData.getInstance().getSkill(bossMainCastSkillId, 1);
		if (!_spawnedMainBoss.isSkillDisabled(skill))
		{
			_spawnedMainBoss.setTarget(_spawnedMainBoss);
			_spawnedMainBoss.doCast(skill);
		}
		
		if (_invisibleCoatl != null)
		{
			skill = SkillData.getInstance().getSkill(coatlInvisibleSkillId, 1);
			if (!_invisibleCoatl.isSkillDisabled(skill))
			{
				_invisibleCoatl.setTarget(_invisibleCoatl);
				_invisibleCoatl.doCast(skill);
			}
		}
		
		start78PercentMechanics();
		_timerParameters.put("kashaTotemMechanicsEnd", new Object[]
		{
			kashaBuffId,
			effectSkillId
		});
		
		startQuestTimer("kashaTotemMechanicsEnd", 9000L, _spawnedMainBoss, null);
		startQuestTimer("castKashaExplosionSkill", 9000L, _invisibleCoatl, null);
	}
	
	private void startEarthTotemMechanics()
	{
		final int bossMainCastSkillId = 34798;
		final int coatlInvisibleSkillId = 34794;
		final int earthBuffId = 34808;
		final int effectSkillId = 34810;
		final Skill kashaProtection = SkillData.getInstance().getSkill(34816, 1);
		if (kashaProtection != null)
		{
			kashaProtection.applyEffects(_spawnedMainBoss, _spawnedMainBoss);
		}
		
		Skill skill = SkillData.getInstance().getSkill(bossMainCastSkillId, 1);
		if (!_spawnedMainBoss.isSkillDisabled(skill))
		{
			_spawnedMainBoss.setTarget(_spawnedMainBoss);
			_spawnedMainBoss.doCast(skill);
		}
		
		if (_invisibleCoatl != null)
		{
			skill = SkillData.getInstance().getSkill(coatlInvisibleSkillId, 1);
			if (!_invisibleCoatl.isSkillDisabled(skill))
			{
				_invisibleCoatl.setTarget(_invisibleCoatl);
				_invisibleCoatl.doCast(skill);
			}
		}
		
		start78PercentMechanics();
		_timerParameters.put("earthTotemMechanicsEnd", new Object[]
		{
			earthBuffId,
			effectSkillId
		});
		
		startQuestTimer("earthTotemMechanicsEnd", 9000L, _spawnedMainBoss, null);
		startQuestTimer("castEarthExplosionSkill", 9000L, _invisibleCoatl, null);
	}
	
	private void startFlameTotemMechanics()
	{
		final int bossMainCastSkillId = 34797;
		final int coatlInvisibleSkillId = 34793;
		final int flameBuffId = 34806;
		final int effectSkillId = 34810;
		final Skill kashaProtection = SkillData.getInstance().getSkill(34816, 1);
		if (kashaProtection != null)
		{
			kashaProtection.applyEffects(_spawnedMainBoss, _spawnedMainBoss);
		}
		
		Skill skill = SkillData.getInstance().getSkill(bossMainCastSkillId, 1);
		if (!_spawnedMainBoss.isSkillDisabled(skill))
		{
			_spawnedMainBoss.setTarget(_spawnedMainBoss);
			_spawnedMainBoss.doCast(skill);
		}
		
		if (_invisibleCoatl != null)
		{
			skill = SkillData.getInstance().getSkill(coatlInvisibleSkillId, 1);
			if (!_invisibleCoatl.isSkillDisabled(skill))
			{
				_invisibleCoatl.setTarget(_invisibleCoatl);
				_invisibleCoatl.doCast(skill);
			}
		}
		
		start78PercentMechanics();
		_timerParameters.put("flameTotemMechanicsEnd", new Object[]
		{
			flameBuffId,
			effectSkillId
		});
		
		startQuestTimer("flameTotemMechanicsEnd", 9000L, _spawnedMainBoss, null);
		startQuestTimer("castFlameExplosionSkill", 9000L, _invisibleCoatl, null);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		cancelQuestTimers("check_arena");
		cancelQuestTimer("remove_barrier", npc, null);
		cancelQuestTimer("check_activity_task", npc, null);
		cancelQuestTimer("END_VULNERABILITY", npc, null);
		cancelQuestTimer("END_TIMED_BARRIER", npc, null);
		
		_combatLoopStarted = false;
		_spawnedMainBoss = null;
		GlobalVariablesManager.getInstance().set("COATL_ALIVE", false);
		// System.out.println("RaidBoss: Coatl(29408) status is 2 (Dead)");
		_invisibleCoatl.deleteMe();
		_flameTotem.deleteMe();
		_kashaTotem.deleteMe();
		_earthTotem.deleteMe();
		_waterTotem.deleteMe();
		
		_invisibleCoatl = null;
		_flameTotem = null;
		_kashaTotem = null;
		_earthTotem = null;
		_waterTotem = null;
		
		cancelQuestTimers("dispel_boss_buffs");
	}
	
	public void onNpcDeath(Npc npc)
	{
		if (npc == _spawnedMainBoss)
		{
			onKill(npc, null, false);
			
			_involvedPlayers.clear();
			if (_invisibleCoatl != null)
			{
				_invisibleCoatl.setInvul(true);
			}
		}
	}
	
	private void applyInitialBarrier(Npc npc)
	{
		cleanBarriers(npc);
		_isInitialBarrier = true;
		_isTimedBarrier = false;
		_hitCounter.put(npc, 0);
		
		npc.setInvul(true);
		if (BARRIER_INITIAL.getSkill() != null)
		{
			BARRIER_INITIAL.getSkill().applyEffects(npc, npc);
		}
		
		cancelQuestTimer("END_VULNERABILITY", npc, null);
		cancelQuestTimer("END_TIMED_BARRIER", npc, null);
	}
	
	private void applyTimedBarrier(Npc npc)
	{
		cleanBarriers(npc);
		_isInitialBarrier = false;
		_isTimedBarrier = true;
		_hitCounter.put(npc, 0);
		
		npc.setInvul(true);
		if (BARRIER_TIMED.getSkill() != null)
		{
			BARRIER_TIMED.getSkill().applyEffects(npc, npc);
		}
		
		startQuestTimer("END_TIMED_BARRIER", 600000, npc, null);
	}
	
	private void enterVulnerableState(Npc npc)
	{
		cleanBarriers(npc);
		_isInitialBarrier = false;
		_isTimedBarrier = false;
		_hitCounter.put(npc, 0);
		
		npc.setInvul(false);
		
		final long randomTime = Rnd.get(20000, 60000);
		startQuestTimer("END_VULNERABILITY", randomTime, npc, null);
	}
	
	private void cleanBarriers(Npc npc)
	{
		if (BARRIER_INITIAL.getSkill() != null)
		{
			npc.stopSkillEffects(BARRIER_INITIAL.getSkill());
		}
		
		if (BARRIER_TIMED.getSkill() != null)
		{
			npc.stopSkillEffects(BARRIER_TIMED.getSkill());
		}
		
		npc.setInvul(false);
	}
	
	private void resetCoatl(Npc npc)
	{
		npc.setCurrentHp(npc.getMaxHp());
		npc.setCurrentMp(npc.getMaxMp());
		npc.teleToLocation(SPAWN_LOCATION, true);
		npc.stopAllEffects();
		cancelQuestTimer("SKILL_LOOP", npc, null);
		_combatLoopStarted = false;
		
		if (npc.isAttackable())
		{
			npc.asAttackable().clearAggroList();
		}
		
		applyInitialBarrier(npc);
		_lastAction = System.currentTimeMillis();
		startQuestTimer("check_activity_task", 60000, null, null);
	}
	
	private Creature getTargetLogic(Npc npc)
	{
		Creature target = _aggroList.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
		
		if (Rnd.get(100) < 30)
		{
			final List<Player> validTargets = new ArrayList<>();
			if (ARENA_ZONE != null)
			{
				for (Player p : ARENA_ZONE.getPlayersInside())
				{
					if ((p != null) && !p.isDead() && !p.isInvul() && p.isSpawned() && (npc.calculateDistance3D(p) <= 1000))
					{
						validTargets.add(p);
					}
				}
			}
			
			if (!validTargets.isEmpty())
			{
				target = validTargets.get(Rnd.get(validTargets.size()));
			}
		}
		
		if ((target == null) || target.isDead())
		{
			target = (Creature) npc.getTarget();
		}
		
		if ((target != null) && target.isSummon())
		{
			Player owner = target.asSummon().getOwner();
			if ((owner != null) && !owner.isDead() && (ARENA_ZONE != null) && ARENA_ZONE.isInsideZone(owner))
			{
				target = owner;
			}
		}
		
		return target;
	}
	
	public boolean isCoatlActive()
	{
		return _spawnedMainBoss != null;
	}
	
	public static void main(String[] args)
	{
		new Coatl();
	}
}
