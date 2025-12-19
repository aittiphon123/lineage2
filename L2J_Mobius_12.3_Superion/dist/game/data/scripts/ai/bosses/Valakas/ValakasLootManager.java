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
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.NpcData;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.npc.DropGroupHolder;
import org.l2jmobius.gameserver.model.actor.holders.npc.DropHolder;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.groups.CommandChannel;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Valakas Loot Manager AI System: Auto-Loot Rule: Command Channel (CC)
 * @author Notorion
 */
public class ValakasLootManager extends Script
{
	private static final Logger LOGGER = Logger.getLogger(ValakasLootManager.class.getName());
	
	private static final int VALAKAS_ID = 29415;
	
	// CONFIGURATION: Minimum number of players in the CC required to be eligible for the drop, 30 is a safety margin.
	private static final int MIN_CC_MEMBERS = 30;
	
	private static final List<DropGroupHolder> _storedDrops = new ArrayList<>();
	private static boolean _dropsLoaded = false;
	
	private final Map<Integer, Long> _damageMap = new ConcurrentHashMap<>();
	
	public ValakasLootManager()
	{
		addAttackId(VALAKAS_ID);
		addKillId(VALAKAS_ID);
		addSpawnId(VALAKAS_ID);
		
		loadAndHijackDrops();
		scanForExistingValakas();
		LOGGER.info("ValakasLootManager: Loaded. Target: " + VALAKAS_ID);
	}
	
	private void scanForExistingValakas()
	{
		for (WorldObject obj : World.getInstance().getVisibleObjects())
		{
			if ((obj != null) && obj.isNpc() && (obj.getId() == VALAKAS_ID) && !((Npc) obj).isDead())
			{
				loadAndHijackDrops();
				return;
			}
		}
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		loadAndHijackDrops();
		_damageMap.clear();
	}
	
	private void loadAndHijackDrops()
	{
		if (_dropsLoaded && !_storedDrops.isEmpty())
		{
			return;
		}
		
		final NpcTemplate template = NpcData.getInstance().getTemplate(VALAKAS_ID);
		if (template == null)
		{
			LOGGER.warning("ValakasLootManager: Template " + VALAKAS_ID + " not found.");
			return;
		}
		
		final List<DropGroupHolder> originalDrops = template.getDropGroups();
		if ((originalDrops != null) && !originalDrops.isEmpty())
		{
			synchronized (_storedDrops)
			{
				_storedDrops.clear();
				_storedDrops.addAll(originalDrops);
				_dropsLoaded = true;
			}
			
			// Drop removed from XML so the server does not drop it on the ground.
			template.removeDropGroups();
			LOGGER.info("ValakasLootManager: Drops successfully captured.");
		}
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (attacker == null)
		{
			return;
		}
		
		_damageMap.merge(attacker.getObjectId(), (long) damage, Long::sum);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (_damageMap.isEmpty())
		{
			return;
		}
		
		// 1. Get Damage Ranking (Top -> Bottom).
		final List<Player> ranking = calculateRanking();
		
		Player validWinner = null;
		
		// 2. Command Channel (CC) Filter.
		for (Player candidate : ranking)
		{
			Party party = candidate.getParty();
			
			// Check if there is a Party and if there is a Command Channel.
			if ((party != null) && (party.getCommandChannel() != null))
			{
				final CommandChannel cc = party.getCommandChannel();
				
				// Check Minimum Size of the CC.
				if (cc.getMemberCount() >= MIN_CC_MEMBERS)
				{
					validWinner = candidate; // Valid Winner.
					break;
				}
				
				// LOGGER.info("Leader's CC " + candidate.getName() + " ignored. Members: " + cc.getMemberCount() + " (Minimum: " + MIN_CC_MEMBERS + ")");
			}
		}
		
		if (validWinner != null)
		{
			final CommandChannel cc = validWinner.getParty().getCommandChannel();
			final Player ccLeader = cc.getLeader(); // The drop always goes to the CC Leader.
			
			LOGGER.info("Valakas auto-loot: " + ccLeader.getName() + " (Membros: " + cc.getMemberCount() + ")");
			
			// 3. Official Message on Screen.
			sendScreenMessage(npc, ccLeader.getName());
			
			// 4. Assign items directly to the Command Channel (CC) Leader's inventory.
			giveAutoLootDrops(npc, ccLeader);
		}
		else
		{
			// LOGGER.info("No valid Command Channel (CC) with " + MIN_CC_MEMBERS + "+ members inflicted sufficient damage.");
		}
		
		_damageMap.clear();
	}
	
	private void sendScreenMessage(Npc npc, String winnerName)
	{
		// Message: "# $s1's Command Channel has looting rights.".
		ExShowScreenMessage screenMsg = new ExShowScreenMessage(NpcStringId.S1_S_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS, ExShowScreenMessage.TOP_CENTER, 10000, true, winnerName);
		
		// Broadcast message to all players who inflicted damage.
		for (Integer playerId : _damageMap.keySet())
		{
			final Player p = World.getInstance().getPlayer(playerId);
			if ((p != null) && p.isOnline())
			{
				p.sendPacket(screenMsg);
			}
		}
		
		// Broadcast to nearby spectators.
		if (npc != null)
		{
			World.getInstance().forEachVisibleObject(npc, Player.class, p ->
			{
				if (!_damageMap.containsKey(p.getObjectId()))
				{
					p.sendPacket(screenMsg);
				}
			});
		}
	}
	
	private void giveAutoLootDrops(Npc npc, Player owner)
	{
		if (_storedDrops.isEmpty())
		{
			return;
		}
		
		int totalGiven = 0;
		for (DropGroupHolder group : _storedDrops)
		{
			// Probability of drop assigned to the group.
			if ((group.getChance() > 0) && (Rnd.get(100.0) > group.getChance()))
			{
				continue;
			}
			
			for (DropHolder drop : group.getDropList())
			{
				// Probability of item drop.
				if (Rnd.get(100.0) < drop.getChance())
				{
					long count = Rnd.get(drop.getMin(), drop.getMax());
					
					// Assign directly to player’s inventory.
					owner.addItem(ItemProcessType.LOOT, drop.getItemId(), count, npc, true);
					totalGiven++;
				}
			}
		}
		
		LOGGER.info("Items successfully assigned to Command Channel (CC) Leader (" + owner.getName() + "): " + totalGiven);
	}
	
	private List<Player> calculateRanking()
	{
		final Map<Integer, Long> groupDamage = new ConcurrentHashMap<>();
		final Map<Integer, Player> leaderObjects = new ConcurrentHashMap<>();
		
		// Aggregate damage per unit, defined by Party Leader.
		for (Entry<Integer, Long> entry : _damageMap.entrySet())
		{
			final Player player = World.getInstance().getPlayer(entry.getKey());
			if ((player == null) || !player.isOnline())
			{
				continue;
			}
			
			final Player leader = getUltimateLeader(player);
			groupDamage.merge(leader.getObjectId(), entry.getValue(), Long::sum);
			leaderObjects.putIfAbsent(leader.getObjectId(), leader);
		}
		
		if (groupDamage.isEmpty())
		{
			return new ArrayList<>();
		}
		
		final List<Entry<Integer, Long>> sortedGroups = new ArrayList<>(groupDamage.entrySet());
		sortedGroups.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
		
		final List<Player> rankedLeaders = new ArrayList<>();
		for (Entry<Integer, Long> entry : sortedGroups)
		{
			rankedLeaders.add(leaderObjects.get(entry.getKey()));
		}
		
		return rankedLeaders;
	}
	
	private Player getUltimateLeader(Player player)
	{
		// Retrieve Party Leader to aggregate damage.
		final Party party = player.getParty();
		if (party != null)
		{
			return party.getLeader();
		}
		
		return player;
	}
	
	public static void main(String[] args)
	{
		new ValakasLootManager();
	}
}
