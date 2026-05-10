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
package ai.areas.MonasteryOfSilence;

import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;

/**
 * Etis van Etina (Head of the Embryo) AI.
 * @author Altur
 */
public class EtisVanEtina extends Script
{
	// NPCs
	private static final int ETIS_VAN_ETINA = 18949;
	private static final int PHANTOM_REAL = 18950;
	private static final int PHANTOM_FAKE = 18951;
	// Misc
	private static final String THINK_TIMER = "THINK";
	private static final String EVT_PHANTOM_REAL_DEAD = "ETIS_PHANTOM_REAL_DEAD";
	private static final String EVT_BOSS_DEAD = "ETIS_BOSS_DEAD";
	private static final int PHASE_NORMAL = 0;
	private static final int PHASE_TRANSFORM = 1;
	private static final int THINK_INTERVAL = 5000;
	private static final int PHANTOM_BROADCAST_RADIUS = 5000;
	
	private EtisVanEtina()
	{
		addSpawnId(ETIS_VAN_ETINA);
		addKillId(ETIS_VAN_ETINA);
		addEventReceivedId(ETIS_VAN_ETINA);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.getVariables().set("phase", PHASE_NORMAL);
		npc.getVariables().set("phantomsAlive", false);
		startQuestTimer(THINK_TIMER, THINK_INTERVAL, npc, null);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals(THINK_TIMER) && (npc != null) && !npc.isDead())
		{
			final int phase = npc.getVariables().getInt("phase", PHASE_NORMAL);
			if ((phase == PHASE_NORMAL) && (npc.getCurrentHp() < (npc.getMaxRecoverableHp() * 0.5)))
			{
				npc.getVariables().set("phase", PHASE_TRANSFORM);
			}
			if ((npc.getVariables().getInt("phase", PHASE_NORMAL) == PHASE_TRANSFORM) && !npc.getVariables().getBoolean("phantomsAlive", false))
			{
				final Npc dr = addSpawn(PHANTOM_REAL, npc.getLocation(), true, 0, false, npc.getInstanceId());
				final Npc df = addSpawn(PHANTOM_FAKE, npc.getLocation(), true, 0, false, npc.getInstanceId());
				final WorldObject target = npc.getTarget();
				if ((target != null) && target.isPlayer())
				{
					final int targetId = target.getObjectId();
					if (dr != null)
					{
						dr.getVariables().set("etisTargetId", targetId);
					}
					if (df != null)
					{
						df.getVariables().set("etisTargetId", targetId);
					}
				}
				npc.getVariables().set("phantomsAlive", true);
			}
			startQuestTimer(THINK_TIMER, THINK_INTERVAL, npc, null);
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public String onEventReceived(String eventName, Npc sender, Npc receiver, WorldObject reference)
	{
		if (eventName.equals(EVT_PHANTOM_REAL_DEAD) && (receiver != null) && (receiver.getId() == ETIS_VAN_ETINA) && !receiver.isDead())
		{
			receiver.getVariables().set("phantomsAlive", false);
		}
		return super.onEventReceived(eventName, sender, receiver, reference);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		cancelQuestTimer(THINK_TIMER, npc, null);
		npc.broadcastEvent(EVT_BOSS_DEAD, PHANTOM_BROADCAST_RADIUS, null);
	}
	
	public static void main(String[] args)
	{
		new EtisVanEtina();
	}
}
