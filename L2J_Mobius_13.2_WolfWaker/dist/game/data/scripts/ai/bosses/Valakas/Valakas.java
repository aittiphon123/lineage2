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
package ai.bosses.Valakas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.model.zone.type.NoRestartZone;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;
import org.l2jmobius.gameserver.network.serverpackets.SpecialCamera;
import org.l2jmobius.gameserver.util.LocationUtil;

/**
 * Valakas AI
 * @author Notorion
 */
public class Valakas extends Script
{
	// NPC IDs
	private static final int VALAKAS = 29415; // Valakas Lv.130
	private static final int INVISIBLE_NPC = 18919;
	
	// Attack Skills - Valakas
	private static final SkillHolder VALAKAS_TRAMPLE_1 = new SkillHolder(34863, 1);
	private static final SkillHolder VALAKAS_TRAMPLE_2 = new SkillHolder(34864, 1);
	private static final SkillHolder VALAKAS_TAIL_RASH = new SkillHolder(34865, 1);
	private static final SkillHolder VALAKAS_TAIL_STRIKE = new SkillHolder(34867, 1);
	private static final SkillHolder VALAKAS_DRAGON_BREATH = new SkillHolder(34866, 1);
	private static final SkillHolder VALAKAS_INHALE = new SkillHolder(34877, 1);
	
	// Meteor Mechanic to Npc Invisible
	private static final SkillHolder VALAKAS_METEOR_STORM = new SkillHolder(34872, 1);
	
	// Mechanic Cycle Skills
	private static final SkillHolder VALAKAS_BARRIER = new SkillHolder(34875, 1);
	private static final SkillHolder VALAKAS_EARTHQUAKE_CAST = new SkillHolder(34870, 1);
	private static final SkillHolder VALAKAS_FIRE_EARTHQUAKER_DMG = new SkillHolder(34871, 1);
	private static final SkillHolder VALAKAS_BARRIER_BREAK = new SkillHolder(34876, 1);
	private static final SkillHolder VALAKAS_WEAKNESS = new SkillHolder(34874, 1);
	private static final SkillHolder VALAKAS_FURY = new SkillHolder(34869, 1);
	private static final SkillHolder VALAKAS_BERSERK = new SkillHolder(34873, 1);
	
	// Messages
	private static final NpcStringId VALAKAS_CHALLENGE_MSG = NpcStringId.getNpcStringId(1000519);
	
	// Locations
	private static final Location[] TELEPORT_CUBE_LOCATIONS =
	{
		new Location(214880, -116144, -1644),
		new Location(213696, -116592, -1644),
		new Location(212112, -116688, -1644),
		new Location(211184, -115472, -1664),
		new Location(210336, -114592, -1644),
		new Location(211360, -113904, -1644),
		new Location(213152, -112352, -1644),
		new Location(214032, -113232, -1644),
		new Location(214752, -114592, -1644),
		new Location(209824, -115568, -1421),
		new Location(210528, -112192, -1403),
		new Location(213120, -111136, -1408),
		new Location(215184, -111504, -1392),
		new Location(215456, -117328, -1392),
		new Location(213200, -118160, -1424)
	};
	
	// Location
	private static final Location VALAKAS_HIDDEN_LOC = new Location(220963, -104895, -1620);
	private static final Location ATTACKER_REMOVE = new Location(150037, -57255, -2976);
	private static final Location VALAKAS_LAIR = new Location(212852, -114842, -1632, 63000);
	private static final Location VALAKAS_REGENERATION_LOC = new Location(-105200, -253104, -15264);
	
	// Status
	private static final byte DORMANT = 0;
	private static final byte WAITING = 1;
	private static final byte FIGHTING = 2;
	private static final byte DEAD = 3;
	
	private static final int VALAKAS_WAIT_TIME = 20; // 20 Minutes until Valakas appears (Lair)
	
	// Respawn Cycle: 11 ~ 17 Days - info wiki KR
	private static final int VALAKAS_SPAWN_INTERVAL = 264; // Hours
	private static final int VALAKAS_SPAWN_RANDOM = 144; // Hours
	
	// Configs
	// Hit Calculation Logic (Estimated based on mechanics/No official data):
	// 1. Min CC: 49 Players (~28 Active DDs).
	// 2. Time Window: 10 Seconds.
	// 3. Avg Speed: 2 hits/sec (1000 AtkSpd).
	// 4. Math: 28 DDs * 20 hits = 560 Total Hits (Theoretical Max).
	// 5. Threshold set to 320 (Large safety margin for lag/reaction).
	// ! CRITICAL: If hits < BARRIER_HIT_REQUIREMENT, Valakas casts a WIPE skill killing ALL players.
	private static final int BARRIER_HIT_REQUIREMENT = 320;
	
	private static final int FURY_CHANCE = 80;
	
	// Zones
	private static final ZoneType BOSS_ZONE = ZoneManager.getInstance().getZoneById(12010);
	private static final NoRestartZone GROUND_ZONE = ZoneManager.getInstance().getZoneById(13010, NoRestartZone.class);
	
	// State Variables
	private GrandBoss _valakas = null;
	private long _timeTracker = 0;
	
	// Atomic State Machine
	private final AtomicInteger _mechanicStage = new AtomicInteger(0);
	private final AtomicInteger _barrierHits = new AtomicInteger(0);
	private boolean _isBarrierHitPhase = false;
	private boolean _barrierBroken = false;
	private boolean _furyUsed = false;
	
	// Flags
	private boolean _isMechanicActive = false;
	private boolean _isPreparingMechanic = false;
	private boolean _cycleTriggered = false;
	
	// Inhale Logic
	private long _inhaleNextTime = 0;
	private int _inhaleBurstCount = 0;
	
	/**
	 * Official Korean Wiki - Valakas Raid Revamp<br>
	 * Respawn Cycle: 11 ~ 17 Days<br>
	 * Level Limit: 120 or higher<br>
	 * Participant Limit: 49 minimum / 200 maximum in Lair<br>
	 * Mechanics (2023): You can access the entry NPC "Heart of Volcano" via the NPC "Boromir" located in Goddard Castle Town.<br>
	 * Spawn Timing: Valakas will appear in the nest exactly 20 minutes after the first Command Channel members request entry.
	 */
	private Valakas()
	{
		addAttackId(VALAKAS);
		addKillId(VALAKAS);
		addSpawnId(VALAKAS);
		
		final StatSet info = GrandBossManager.getInstance().getStatSet(VALAKAS);
		final int status = GrandBossManager.getInstance().getStatus(VALAKAS);
		if (status == DEAD)
		{
			final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
			if (temp > 0)
			{
				startQuestTimer("valakas_unlock", temp, null, null);
			}
			else
			{
				spawnValakas(true);
			}
		}
		else
		{
			final int loc_x = info.getInt("loc_x");
			final int loc_y = info.getInt("loc_y");
			final int loc_z = info.getInt("loc_z");
			final int heading = info.getInt("heading");
			final double hp = info.getDouble("currentHP");
			final double mp = info.getDouble("currentMP");
			
			_valakas = (GrandBoss) addSpawn(VALAKAS, loc_x, loc_y, loc_z, heading, false, 0);
			GrandBossManager.getInstance().addBoss(_valakas);
			_valakas.setCurrentHpMp(hp, mp);
			_valakas.setRunning();
			
			if (status == FIGHTING)
			{
				_timeTracker = System.currentTimeMillis();
				startQuestTimer("check_inactivity", 60000, _valakas, null, true);
				startQuestTimer("manage_skills", 700, _valakas, null, true);
				
				// Independent Timers
				startQuestTimer("TASK_METEOR_UPDATED", 45000, _valakas, null);
				startQuestTimer("TASK_FURY_INDIVIDUAL", 105000, _valakas, null);
			}
			else
			{
				_valakas.teleToLocation(VALAKAS_HIDDEN_LOC);
				_valakas.setInvul(true);
				_valakas.getAI().setIntention(Intention.IDLE);
				if (status == WAITING)
				{
					startQuestTimer("beginning", VALAKAS_WAIT_TIME * 60000, _valakas, null);
				}
			}
		}
	}
	
	private void spawnValakas(boolean isDormant)
	{
		_valakas = (GrandBoss) addSpawn(VALAKAS, VALAKAS_REGENERATION_LOC, false, 0);
		_valakas.teleToLocation(VALAKAS_HIDDEN_LOC);
		GrandBossManager.getInstance().addBoss(_valakas);
		
		if (isDormant)
		{
			GrandBossManager.getInstance().setStatus(VALAKAS, DORMANT);
			_valakas.setInvul(true);
			_valakas.setRunning();
			_valakas.getAI().setIntention(Intention.IDLE);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equalsIgnoreCase("valakas_unlock"))
		{
			spawnValakas(true);
			return super.onEvent(event, npc, player);
		}
		
		if (event.equalsIgnoreCase("remove_players"))
		{
			BOSS_ZONE.oustAllPlayers();
			return super.onEvent(event, npc, player);
		}
		
		if (npc == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "beginning":
			{
				_timeTracker = System.currentTimeMillis();
				npc.teleToLocation(VALAKAS_LAIR);
				startQuestTimer("broadcast_spawn", 100, npc, null);
				
				startQuestTimer("spawn_1", 1700, npc, null);
				startQuestTimer("spawn_2", 3200, npc, null);
				startQuestTimer("spawn_3", 6500, npc, null);
				startQuestTimer("spawn_4", 9400, npc, null);
				startQuestTimer("spawn_5", 12100, npc, null);
				startQuestTimer("spawn_6", 12430, npc, null);
				startQuestTimer("spawn_7", 15430, npc, null);
				startQuestTimer("spawn_8", 16830, npc, null);
				startQuestTimer("spawn_9", 23530, npc, null);
				startQuestTimer("spawn_10", 26000, npc, null);
				startQuestTimer("challenge_message", 28500, npc, null);
				break;
			}
			case "broadcast_spawn":
			{
				npc.setInvul(true);
				npc.disableAllSkills();
				npc.setTargetable(false);
				npc.setImmobilized(true);
				for (Player plyr : BOSS_ZONE.getPlayersInside())
				{
					plyr.sendPacket(new SpecialCamera(npc, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0));
					plyr.sendPacket(new PlaySound(1, "BS03_A", 0, 0, 0, 0, 0));
					plyr.sendPacket(new SocialAction(npc.getObjectId(), 3));
				}
				break;
			}
			case "spawn_1":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1800, 180, -1, 1500, 15000, 10000, 0, 0, 1, 0, 0));
				break;
			}
			case "spawn_2":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1300, 180, -5, 3000, 15000, 10000, 0, -5, 1, 0, 0));
				break;
			}
			case "spawn_3":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 500, 180, -8, 600, 15000, 10000, 0, 60, 1, 0, 0));
				break;
			}
			case "spawn_4":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 800, 180, -8, 2700, 15000, 10000, 0, 30, 1, 0, 0));
				break;
			}
			case "spawn_5":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 200, 250, 70, 0, 15000, 10000, 30, 80, 1, 0, 0));
				break;
			}
			case "spawn_6":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1100, 250, 70, 2500, 15000, 10000, 30, 80, 1, 0, 0));
				break;
			}
			case "spawn_7":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 700, 150, 30, 0, 15000, 10000, -10, 60, 1, 0, 0));
				break;
			}
			case "spawn_8":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1200, 150, 20, 2900, 15000, 10000, -10, 30, 1, 0, 0));
				break;
			}
			case "spawn_9":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 750, 170, -10, 3400, 15000, 4000, 10, -15, 1, 0, 0));
				break;
			}
			case "spawn_10":
			{
				GrandBossManager.getInstance().setStatus(VALAKAS, FIGHTING);
				npc.setInvul(false);
				npc.setImmobilized(false);
				npc.enableAllSkills();
				npc.setTargetable(true);
				npc.getAI().setIntention(Intention.ACTIVE);
				npc.broadcastInfo();
				
				resetMechanicState();
				_cycleTriggered = false;
				
				// Set Initial Inhale Timer (1m 40s).
				_inhaleNextTime = System.currentTimeMillis() + 100000;
				_inhaleBurstCount = 0;
				
				startQuestTimer("start_ai_tasks", 2000, npc, null);
				break;
			}
			case "start_ai_tasks":
			{
				if (GrandBossManager.getInstance().getStatus(VALAKAS) == FIGHTING)
				{
					startQuestTimer("check_inactivity", 60000, npc, null, true);
					startQuestTimer("manage_skills", 1000, npc, null, true);
					
					// Independent Timers.
					// Meteor: 45s start, 20s interval.
					startQuestTimer("TASK_METEOR_UPDATED", 45000, npc, null);
					// Fury Individual: 1m 45s start.
					startQuestTimer("TASK_FURY_INDIVIDUAL", 105000, npc, null);
				}
				break;
			}
			case "manage_skills":
			{
				manageSkills(npc);
				break;
			}
			case "TASK_METEOR_UPDATED":
			{
				if (GrandBossManager.getInstance().getStatus(VALAKAS) != FIGHTING)
				{
					return null;
				}
				if (!_isMechanicActive)
				{
					spawnMeteors(npc); // 8 Random
					spawnLateralMeteors(npc); // 2 Lateral
				}
				startQuestTimer("TASK_METEOR_UPDATED", 20000, npc, null); // 20s interval
				break;
			}
			case "TASK_FURY_INDIVIDUAL":
			{
				if (GrandBossManager.getInstance().getStatus(VALAKAS) != FIGHTING)
				{
					return null;
				}
				
				if (!_isMechanicActive && !_isPreparingMechanic)
				{
					startQuestTimer("PRE_FURY_INDIVIDUAL", 1000, npc, null);
				}
				else
				{
					startQuestTimer("TASK_FURY_INDIVIDUAL", 5000, npc, null);
				}
				break;
			}
			case "PRE_FURY_INDIVIDUAL":
			{
				_isPreparingMechanic = true;
				
				npc.abortCast();
				npc.abortAttack();
				npc.stopMove(null);
				npc.getAI().setIntention(Intention.IDLE);
				
				npc.setTarget(npc);
				npc.doCast(VALAKAS_TRAMPLE_1.getSkill());
				
				startQuestTimer("MSG_FURY_INDIVIDUAL", 5000, npc, null);
				break;
			}
			case "MSG_FURY_INDIVIDUAL":
			{
				BOSS_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.IT_S_BEEN_A_WHILE_SINCE_ANYONE_MADE_ME_THIS_ANGRY_FEEL_MY_FLAMING_WRATH_AND_RUN_AWAY_IN_FEAR, ExShowScreenMessage.TOP_CENTER, 7000, true));
				
				startQuestTimer("CAST_FURY_INDIVIDUAL", 2000, npc, null);
				break;
			}
			case "CAST_FURY_INDIVIDUAL":
			{
				npc.setTarget(npc);
				npc.getAI().setIntention(Intention.ACTIVE);
				npc.doCast(VALAKAS_FURY.getSkill());
				ThreadPool.schedule(() ->
				{
					_isPreparingMechanic = false;
					manageSkills(npc);
					startQuestTimer("TASK_FURY_INDIVIDUAL", 105000, npc, null);
				}, 3000);
				break;
			}
			case "CLEANSE_TASK":
			{
				if ((!_isMechanicActive && !_isPreparingMechanic) || npc.isDead())
				{
					return null;
				}
				cleanDebuffs(npc);
				startQuestTimer("CLEANSE_TASK", 500, npc, null);
				break;
			}
			case "PRE_BARRIER_PHASE":
			{
				// STOP Fury Timer (It restarts after cycle)
				cancelQuestTimer("TASK_FURY_INDIVIDUAL", npc, null);
				_isPreparingMechanic = true;
				// Cleanse starts 3s before Mechanic
				startQuestTimer("PRE_MECHANIC_CLEANSE", 7000, npc, null);
				// Mechanic starts 10s later
				startQuestTimer("START_BARRIER_SEQUENCE", 10000, npc, null);
				break;
			}
			case "PRE_MECHANIC_CLEANSE":
			{
				startQuestTimer("CLEANSE_TASK", 100, npc, null);
				break;
			}
			case "START_BARRIER_SEQUENCE":
			{
				if (GrandBossManager.getInstance().getStatus(VALAKAS) != FIGHTING)
				{
					return null;
				}
				
				_mechanicStage.set(1);
				_isBarrierHitPhase = true;
				_barrierHits.set(0);
				_barrierBroken = false;
				
				_isMechanicActive = true;
				_isPreparingMechanic = false;
				
				npc.disableCoreAI(true);
				
				BOSS_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.HOW_DARE_THESE_PUNY_CREATURES_ATTACK_ME_THEY_ARE_NO_MATCH_BOTH_FOR_MY_FLAMES_AND_MY_WRATH, ExShowScreenMessage.TOP_CENTER, 5000, true));
				
				toggleAttackSkills(npc, false);
				
				npc.stopMove(null);
				npc.getAI().setIntention(Intention.IDLE);
				npc.abortCast();
				npc.abortAttack();
				npc.setTarget(npc);
				
				// Apply Barrier.
				ThreadPool.schedule(() ->
				{
					if (npc.isDead())
					{
						return;
					}
					SkillCaster.triggerCast(npc, npc, VALAKAS_BARRIER.getSkill());
				}, 500);
				
				// Earthquake.
				ThreadPool.schedule(() ->
				{
					if (npc.isDead())
					{
						return;
					}
					npc.setTarget(npc);
					npc.doCast(VALAKAS_EARTHQUAKE_CAST.getSkill());
					_mechanicStage.set(2);
				}, 2000);
				
				// Wait 14s (2s + 10s anim + 2s margin).
				startQuestTimer("BARRIER_RESULT_CHECK", 14000, npc, null);
				break;
			}
			case "BARRIER_RESULT_CHECK":
			{
				_isBarrierHitPhase = false;
				
				if (_barrierBroken)
				{
					SkillCaster.triggerCast(npc, npc, VALAKAS_BARRIER_BREAK.getSkill());
					BOSS_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.VALAKAS_PROTECTIVE_BARRIER_IS_SHATTERED_AND_HIS_FLAMES_ARE_EXTINGUISHED_THE_FIRE_POWER_LEAVES_VALAKAS_WEAKENING_HIM, ExShowScreenMessage.TOP_CENTER, 5000, true));
					startQuestTimer("APPLY_WEAKNESS", 1000, npc, null);
				}
				else // FAILURE = WIPE + HEAL + MESSAGE.
				{
					// 1. Message (7s).
					BOSS_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_FIRE_BARRIER_IS_PROTECTING_VALAKAS_RECOVERING_HIS_HP_THE_FIRE_WRATH_FALLS_UPON_THE_WHOLE_AREA, ExShowScreenMessage.TOP_CENTER, 7000, true));
					
					// 2. Explosion.
					SkillCaster.triggerCast(npc, npc, VALAKAS_FIRE_EARTHQUAKER_DMG.getSkill());
					
					// 3. Wipe Players (Instant Kill in Zone).
					for (Creature creature : BOSS_ZONE.getCharactersInside())
					{
						if ((creature != null) && creature.isPlayer() && !creature.isDead())
						{
							creature.doDie(npc);
						}
					}
					
					// 4. Full Heal.
					npc.setCurrentHp(npc.getMaxHp());
					
					// 5. End Mechanic.
					startQuestTimer("RESTART_CYCLE_FAILED", 3000, npc, null);
				}
				break;
			}
			case "RESTART_CYCLE_FAILED":
			{
				// Unlock AI after Wipe.
				_isMechanicActive = false;
				cancelQuestTimer("CLEANSE_TASK", npc, null);
				npc.disableCoreAI(false);
				toggleAttackSkills(npc, true);
				
				resetMechanicState();
				
				_cycleTriggered = false;
				
				// Restart Fury Timer (1m 45s from now).
				startQuestTimer("TASK_FURY_INDIVIDUAL", 105000, npc, null);
				break;
			}
			case "APPLY_WEAKNESS":
			{
				_mechanicStage.set(3);
				cancelQuestTimer("CLEANSE_TASK", npc, null);
				
				Skill weakness = VALAKAS_WEAKNESS.getSkill();
				if (weakness != null)
				{
					weakness.applyEffects(npc, npc);
				}
				
				ThreadPool.schedule(() ->
				{
					if (npc.isDead())
					{
						return;
					}
					_isMechanicActive = false;
					npc.disableCoreAI(false);
					toggleAttackSkills(npc, true);
					manageSkills(npc);
				}, 2000);
				
				startQuestTimer("WEAKNESS_ENDED", 10000, npc, null);
				break;
			}
			case "WEAKNESS_ENDED":
			{
				_mechanicStage.set(4);
				_furyUsed = false;
				
				if (_isMechanicActive)
				{
					_isMechanicActive = false;
					npc.disableCoreAI(false);
					toggleAttackSkills(npc, true);
				}
				
				startQuestTimer("END_FURY_WINDOW", 15000, npc, null);
				break;
			}
			case "END_FURY_WINDOW":
			{
				_mechanicStage.set(5);
				
				// BERSERK CHANCE 50%.
				if (Rnd.get(100) < 50)
				{
					startQuestTimer("PRE_BERSERK_PHASE", 10000, npc, null);
				}
				else
				{
					startQuestTimer("RESTART_CYCLE", 1000, npc, null);
				}
				break;
			}
			case "PRE_BERSERK_PHASE":
			{
				_isPreparingMechanic = true;
				startQuestTimer("PRE_MECHANIC_CLEANSE", 7000, npc, null);
				startQuestTimer("PREPARE_BERSERK", 10000, npc, null);
				break;
			}
			case "PREPARE_BERSERK":
			{
				_mechanicStage.set(6);
				_isMechanicActive = true;
				_isPreparingMechanic = false;
				npc.disableCoreAI(true);
				toggleAttackSkills(npc, false);
				npc.stopMove(null);
				npc.getAI().setIntention(Intention.IDLE);
				npc.abortCast();
				npc.abortAttack();
				npc.setTarget(npc);
				
				ThreadPool.schedule(() ->
				{
					if (npc.isDead())
					{
						return;
					}
					npc.setTarget(npc);
					npc.doCast(VALAKAS_BERSERK.getSkill());
					ThreadPool.schedule(() ->
					{
						BOSS_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.HOW_DARE_THESE_PUNY_CREATURES_ATTACK_ME_THEY_ARE_NO_MATCH_BOTH_FOR_MY_FLAMES_AND_MY_WRATH, ExShowScreenMessage.TOP_CENTER, 5000, true));
					}, 1000);
				}, 2000);
				
				ThreadPool.schedule(() ->
				{
					if (npc.isDead())
					{
						return;
					}
					_isMechanicActive = false;
					npc.disableCoreAI(false);
					toggleAttackSkills(npc, true);
					_mechanicStage.set(7);
					manageSkills(npc);
					startQuestTimer("BERSERK_COMBAT_PHASE", 3000, npc, null);
				}, 5000);
				
				break;
			}
			case "BERSERK_COMBAT_PHASE":
			{
				_isMechanicActive = false;
				startQuestTimer("RESTART_CYCLE", 60000, npc, null);
				break;
			}
			case "RESTART_CYCLE":
			{
				resetMechanicState();
				
				// RESTART FURY TIMER (1m 45s after mechanic ends).
				startQuestTimer("TASK_FURY_INDIVIDUAL", 105000, npc, null);
				
				// WAIT FOR NEXT CYCLE (5 Min).
				startQuestTimer("PRE_BARRIER_PHASE", 300000, npc, null);
				break;
			}
			case "challenge_message":
			{
				if (GrandBossManager.getInstance().getStatus(VALAKAS) == FIGHTING)
				{
					final Playable target = getRandomTarget(npc);
					if (target != null)
					{
						BOSS_ZONE.broadcastPacket(new ExShowScreenMessage(VALAKAS_CHALLENGE_MSG, ExShowScreenMessage.TOP_CENTER, 10000, true, target.getName()));
					}
				}
				break;
			}
			case "check_inactivity":
			{
				// Inactivity 1 hour (60 minutes).
				if ((GrandBossManager.getInstance().getStatus(VALAKAS) == FIGHTING) && ((_timeTracker + 3600000) < System.currentTimeMillis()))
				{
					npc.getAI().setIntention(Intention.IDLE);
					npc.teleToLocation(VALAKAS_REGENERATION_LOC);
					GrandBossManager.getInstance().setStatus(VALAKAS, DORMANT);
					npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
					BOSS_ZONE.oustAllPlayers();
					
					cancelQuestTimer("check_inactivity", npc, null);
					cancelQuestTimer("manage_skills", npc, null);
					cancelQuestTimer("METEOR_TASK", npc, null);
					cancelQuestTimer("TASK_METEOR_UPDATED", npc, null);
					cancelQuestTimer("TASK_FURY_INDIVIDUAL", npc, null);
					cancelQuestTimer("CAST_FURY_INDIVIDUAL", npc, null);
					cancelQuestTimer("PRE_FURY_INDIVIDUAL", npc, null);
					cancelQuestTimer("MSG_FURY_INDIVIDUAL", npc, null);
					
					cancelQuestTimer("PRE_BARRIER_PHASE", npc, null);
					cancelQuestTimer("PRE_MECHANIC_CLEANSE", npc, null);
					cancelQuestTimer("CLEANSE_TASK", npc, null);
					
					cancelQuestTimer("START_BARRIER_SEQUENCE", npc, null);
					cancelQuestTimer("CAST_EARTHQUAKE_CHANNEL", npc, null);
					cancelQuestTimer("BARRIER_RESULT_CHECK", npc, null);
					cancelQuestTimer("APPLY_WEAKNESS", npc, null);
					cancelQuestTimer("WEAKNESS_ENDED", npc, null);
					cancelQuestTimer("END_FURY_WINDOW", npc, null);
					cancelQuestTimer("PRE_BERSERK_PHASE", npc, null);
					cancelQuestTimer("PREPARE_BERSERK", npc, null);
					cancelQuestTimer("EXECUTE_BERSERK", npc, null);
					cancelQuestTimer("BERSERK_COMBAT_PHASE", npc, null);
					cancelQuestTimer("RESTART_CYCLE", npc, null);
					cancelQuestTimer("RESTART_CYCLE_FAILED", npc, null);
					
					resetMechanicState();
					_cycleTriggered = false;
				}
				break;
			}
			case "die_1":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 2000, 130, -1, 0, 15000, 10000, 0, 0, 1, 1, 0));
				break;
			}
			case "die_2":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1100, 210, -5, 3000, 15000, 10000, -13, 0, 1, 1, 0));
				break;
			}
			case "die_3":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1300, 200, -8, 3000, 15000, 10000, 0, 15, 1, 1, 0));
				break;
			}
			case "die_4":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1000, 190, 0, 500, 15000, 10000, 0, 10, 1, 1, 0));
				break;
			}
			case "die_5":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 120, 0, 2500, 15000, 10000, 12, 40, 1, 1, 0));
				break;
			}
			case "die_6":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 20, 0, 700, 15000, 10000, 10, 10, 1, 1, 0));
				break;
			}
			case "die_7":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 10, 0, 1000, 15000, 10000, 20, 70, 1, 1, 0));
				break;
			}
			case "die_8":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 10, 0, 300, 15000, 250, 20, -20, 1, 1, 0));
				for (Location loc : TELEPORT_CUBE_LOCATIONS)
				{
					addSpawn(31759, loc, false, 60000);
				}
				startQuestTimer("remove_players", 60000, null, null);
				break;
			}
			case "valakas_unlock":
			{
				spawnValakas(true);
				break;
			}
			case "remove_players":
			{
				BOSS_ZONE.oustAllPlayers();
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.asAttackable().setCanReturnToSpawnPoint(false);
		npc.setRandomWalking(false);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (!BOSS_ZONE.isInsideZone(attacker))
		{
			attacker.doDie(attacker);
			return;
		}
		
		if (GrandBossManager.getInstance().getStatus(VALAKAS) != FIGHTING)
		{
			attacker.teleToLocation(ATTACKER_REMOVE);
			return;
		}
		
		_timeTracker = System.currentTimeMillis();
		
		// CYCLE AT 75% HP.
		if ((npc.getCurrentHpPercent() < 75) && !_cycleTriggered && !_isMechanicActive && !_isPreparingMechanic)
		{
			_cycleTriggered = true;
			startQuestTimer("PRE_BARRIER_PHASE", 1000, npc, null);
		}
		
		int stage = _mechanicStage.get();
		if (((stage == 1) || (stage == 2)) && _isBarrierHitPhase && !_barrierBroken)
		{
			if (_barrierHits.incrementAndGet() >= BARRIER_HIT_REQUIREMENT)
			{
				_barrierBroken = true;
			}
		}
		
		if (_isMechanicActive)
		{
			return;
		}
		
		manageSkills(npc);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		cancelQuestTimer("check_inactivity", npc, null);
		cancelQuestTimer("manage_skills", npc, null);
		cancelQuestTimer("METEOR_TASK", npc, null);
		cancelQuestTimer("TASK_METEOR_UPDATED", npc, null);
		cancelQuestTimer("TASK_FURY_INDIVIDUAL", npc, null);
		cancelQuestTimer("CAST_FURY_INDIVIDUAL", npc, null);
		cancelQuestTimer("PRE_FURY_INDIVIDUAL", npc, null);
		cancelQuestTimer("MSG_FURY_INDIVIDUAL", npc, null);
		
		cancelQuestTimer("PRE_BARRIER_PHASE", npc, null);
		cancelQuestTimer("PRE_MECHANIC_CLEANSE", npc, null);
		cancelQuestTimer("CLEANSE_TASK", npc, null);
		
		cancelQuestTimer("START_BARRIER_SEQUENCE", npc, null);
		cancelQuestTimer("CAST_EARTHQUAKE_CHANNEL", npc, null);
		cancelQuestTimer("BARRIER_RESULT_CHECK", npc, null);
		cancelQuestTimer("APPLY_WEAKNESS", npc, null);
		cancelQuestTimer("WEAKNESS_ENDED", npc, null);
		cancelQuestTimer("END_FURY_WINDOW", npc, null);
		cancelQuestTimer("ACTIVATE_BERSERK", npc, null);
		cancelQuestTimer("PREPARE_BERSERK", npc, null);
		cancelQuestTimer("EXECUTE_BERSERK", npc, null);
		cancelQuestTimer("BERSERK_COMBAT_PHASE", npc, null);
		cancelQuestTimer("RESTART_CYCLE", npc, null);
		cancelQuestTimer("RESTART_CYCLE_FAILED", npc, null);
		
		BOSS_ZONE.broadcastPacket(new PlaySound(1, "B03_D", 0, 0, 0, 0, 0));
		BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0));
		startQuestTimer("die_1", 300, npc, null);
		startQuestTimer("die_2", 600, npc, null);
		startQuestTimer("die_3", 3800, npc, null);
		startQuestTimer("die_4", 8200, npc, null);
		startQuestTimer("die_5", 8700, npc, null);
		startQuestTimer("die_6", 13300, npc, null);
		startQuestTimer("die_7", 14000, npc, null);
		startQuestTimer("die_8", 16500, npc, null);
		
		GrandBossManager.getInstance().setStatus(VALAKAS, DEAD);
		
		final long baseIntervalMillis = (long) VALAKAS_SPAWN_INTERVAL * 3600000;
		final long randomRangeMillis = (long) VALAKAS_SPAWN_RANDOM * 3600000;
		final long respawnTime = baseIntervalMillis + Rnd.get(-randomRangeMillis, randomRangeMillis);
		startQuestTimer("valakas_unlock", respawnTime, null, null);
		
		final StatSet info = GrandBossManager.getInstance().getStatSet(VALAKAS);
		info.set("respawn_time", System.currentTimeMillis() + respawnTime);
		GrandBossManager.getInstance().setStatSet(VALAKAS, info);
	}
	
	private void cleanDebuffs(Npc npc)
	{
		if ((npc == null) || npc.isDead())
		{
			return;
		}
		
		npc.getEffectList().stopEffects(info -> info.getSkill().isDebuff(), true, true);
	}
	
	private void toggleAttackSkills(Npc npc, boolean enable)
	{
		if (enable)
		{
			npc.enableSkill(VALAKAS_TRAMPLE_1.getSkill());
			npc.enableSkill(VALAKAS_TRAMPLE_2.getSkill());
			npc.enableSkill(VALAKAS_TAIL_RASH.getSkill());
			npc.enableSkill(VALAKAS_TAIL_STRIKE.getSkill());
			npc.enableSkill(VALAKAS_DRAGON_BREATH.getSkill());
		}
		else
		{
			npc.disableSkill(VALAKAS_TRAMPLE_1.getSkill(), 15000);
			npc.disableSkill(VALAKAS_TRAMPLE_2.getSkill(), 15000);
			npc.disableSkill(VALAKAS_TAIL_RASH.getSkill(), 15000);
			npc.disableSkill(VALAKAS_TAIL_STRIKE.getSkill(), 15000);
			npc.disableSkill(VALAKAS_DRAGON_BREATH.getSkill(), 15000);
		}
	}
	
	private void manageSkills(Npc npc)
	{
		if (npc.isCastingNow() || npc.isCoreAIDisabled() || npc.isInvul() || npc.isDead())
		{
			return;
		}
		
		if (_isMechanicActive || _isPreparingMechanic)
		{
			return;
		}
		
		if (!GROUND_ZONE.isCharacterInZone(npc))
		{
			npc.teleToLocation(VALAKAS_LAIR);
			return;
		}
		
		final Playable target = getRandomTarget(npc);
		if (target == null)
		{
			return;
		}
		
		Skill skillToCast = null;
		
		if (_isPreparingMechanic)
		{
			if (Rnd.get(100) < 50)
			{
				skillToCast = VALAKAS_TRAMPLE_1.getSkill();
			}
			else
			{
				skillToCast = VALAKAS_TRAMPLE_2.getSkill();
			}
			
			if ((skillToCast != null) && !npc.isSkillDisabled(skillToCast))
			{
				if (LocationUtil.checkIfInRange(skillToCast.getCastRange(), npc, target, true))
				{
					npc.setTarget(target);
					npc.doCast(skillToCast);
				}
				else
				{
					npc.getAI().setIntention(Intention.FOLLOW, target, null);
				}
			}
			return;
		}
		
		// Inhale Logic.
		long currentTime = System.currentTimeMillis();
		if (currentTime > _inhaleNextTime)
		{
			if (_mechanicStage.get() == 3)
			{
				return;
			}
			
			npc.setTarget(npc);
			npc.doCast(VALAKAS_INHALE.getSkill());
			_inhaleBurstCount++;
			if (_inhaleBurstCount >= 3)
			{
				_inhaleNextTime = currentTime + 180000;
				_inhaleBurstCount = 0;
			}
			else
			{
				_inhaleNextTime = currentTime + 10000;
			}
			return;
		}
		
		// Fury Logic.
		if ((_mechanicStage.get() == 4) && !_furyUsed)
		{
			if (Rnd.get(100) < FURY_CHANCE)
			{
				_furyUsed = true;
				npc.setTarget(npc);
				npc.doCast(VALAKAS_FURY.getSkill());
				return;
			}
		}
		
		int chance = Rnd.get(100);
		if (chance < 20)
		{
			skillToCast = VALAKAS_DRAGON_BREATH.getSkill();
		}
		else if (chance < 40)
		{
			skillToCast = VALAKAS_TAIL_STRIKE.getSkill();
		}
		else if (chance < 60)
		{
			skillToCast = VALAKAS_TRAMPLE_2.getSkill();
		}
		else if (chance < 80)
		{
			skillToCast = VALAKAS_TRAMPLE_1.getSkill();
		}
		else
		{
			skillToCast = VALAKAS_TAIL_RASH.getSkill();
		}
		
		if (skillToCast != null)
		{
			if (npc.isSkillDisabled(skillToCast))
			{
				return;
			}
			
			if (LocationUtil.checkIfInRange((skillToCast.getCastRange() < 600) ? 600 : skillToCast.getCastRange(), npc, target, true))
			{
				npc.setTarget(target);
				npc.doCast(skillToCast);
			}
			else
			{
				npc.getAI().setIntention(Intention.FOLLOW, target, null);
			}
		}
	}
	
	private Playable getRandomTarget(Npc npc)
	{
		final List<Playable> result = new ArrayList<>();
		World.getInstance().forEachVisibleObject(npc, Playable.class, obj ->
		{
			if ((obj != null) && !obj.isDead() && !obj.isInvisible() && !obj.isPet() && obj.isPlayable())
			{
				result.add(obj);
			}
		});
		
		return result.isEmpty() ? null : getRandomEntry(result);
	}
	
	// Meteor lateral Logic.
	private void spawnLateralMeteors(Npc npc)
	{
		final int z = npc.getZ();
		final double angle = LocationUtil.convertHeadingToDegree(npc.getHeading());
		final double angleLeft = Math.toRadians(angle + 90);
		final double angleRight = Math.toRadians(angle - 90);
		
		for (int i = 0; i < 4; i++)
		{
			
			double targetAngle = (i < 2) ? angleLeft : angleRight;
			
			targetAngle += Math.toRadians(Rnd.get(-40, 40));
			
			final int dist = ((i % 2) == 0) ? 800 : 1600;
			final int x = (int) (npc.getX() + (dist * Math.cos(targetAngle)));
			final int y = (int) (npc.getY() + (dist * Math.sin(targetAngle)));
			
			Location spawnLoc = new Location(x, y, z);
			if (!BOSS_ZONE.isInsideZone(spawnLoc))
			{
				continue;
			}
			
			final Npc meteorTrap = addSpawn(INVISIBLE_NPC, spawnLoc, false, 5000);
			if (meteorTrap != null)
			{
				meteorTrap.setTarget(meteorTrap);
				meteorTrap.doCast(VALAKAS_METEOR_STORM.getSkill());
			}
		}
	}
	
	// Meteor Logic.
	private void spawnMeteors(Npc npc)
	{
		final int radius = 2500;
		final int z = npc.getZ();
		
		for (int i = 0; i < 8; i++)
		{
			final int x = npc.getX() + Rnd.get(-radius, radius);
			final int y = npc.getY() + Rnd.get(-radius, radius);
			final Location spawnLoc = new Location(x, y, z);
			if (!BOSS_ZONE.isInsideZone(spawnLoc))
			{
				continue;
			}
			
			final Npc meteorTrap = addSpawn(INVISIBLE_NPC, spawnLoc, false, 5000);
			if (meteorTrap != null)
			{
				meteorTrap.setTarget(meteorTrap);
				meteorTrap.doCast(VALAKAS_METEOR_STORM.getSkill());
			}
		}
	}
	
	private void resetMechanicState()
	{
		_mechanicStage.set(0);
		_isBarrierHitPhase = false;
		_barrierHits.set(0);
		_barrierBroken = false;
		_furyUsed = false;
		_isMechanicActive = false;
		_isPreparingMechanic = false;
	}
	
	public static void main(String[] args)
	{
		new Valakas();
	}
}
