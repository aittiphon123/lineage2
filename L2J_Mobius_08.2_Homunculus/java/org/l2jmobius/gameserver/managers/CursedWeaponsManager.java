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
package org.l2jmobius.gameserver.managers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.config.GeneralConfig;
import org.l2jmobius.gameserver.data.xml.MapRegionData;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.CursedWeapon;
import org.l2jmobius.gameserver.model.actor.instance.Defender;
import org.l2jmobius.gameserver.model.actor.instance.FeedableBeast;
import org.l2jmobius.gameserver.model.actor.instance.FortCommander;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.actor.instance.Guard;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ExActivatedCursedTreasureBoxLocation;
import org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponList;
import org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.util.Broadcast;

/**
 * CursedWeaponsManager AI - Version Prelude of War 2019
 * @author Micht, Notorion
 */
public class CursedWeaponsManager implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(CursedWeaponsManager.class.getName());
	private final Map<Integer, CursedWeapon> _cursedWeapons = new HashMap<>();
	
	// NPCs.
	public static final int ZARICHE_BOX_NPC_ID = 24370;
	public static final int AKAMANAH_BOX_NPC_ID = 24371;
	
	public static final int ZARICHE_WEAPON_ID = 1;
	public static final int AKAMANAH_WEAPON_ID = 2;
	
	private final List<Location> _zaricheBoxLocs = new CopyOnWriteArrayList<>();
	private final List<Location> _akamanahBoxLocs = new CopyOnWriteArrayList<>();
	
	// Time Lock.
	private boolean _isEventActive = false;
	
	protected CursedWeaponsManager()
	{
		load();
		
		// Checks the time every 1 minute (60000ms).
		ThreadPool.scheduleAtFixedRate(this::checkEventStatus, 1000, 60000);
	}
	
	public boolean isEventActive()
	{
		return _isEventActive;
	}
	
	public void checkEventStatus()
	{
		final Calendar now = Calendar.getInstance();
		final int day = now.get(Calendar.DAY_OF_WEEK);
		final int hour = now.get(Calendar.HOUR_OF_DAY);
		final int minute = now.get(Calendar.MINUTE);
		
		// 1. Week day verification: Monday and Thursday.
		if ((day != Calendar.MONDAY) && (day != Calendar.THURSDAY))
		{
			if (_isEventActive)
			{
				endAllWeapons();
				_isEventActive = false;
				clearEventVisuals();
			}
			return;
		}
		
		// 2. Event time.
		if ((hour >= 18) && (hour < 23))
		{
			if (!_isEventActive)
			{
				_isEventActive = true;
				LOGGER.info("Cursed Weapons Defense Event Started.");
			}
		}
		// 3. Deactivate and clean outside 18h/23h schedule.
		else
		{
			if (_isEventActive)
			{
				endAllWeapons();
				_isEventActive = false;
				clearEventVisuals();
				LOGGER.info("Cursed Weapons Event Ended (Time Schedule).");
			}
			// Emergency redundancy (23:01).
			else if ((hour == 23) && (minute == 1))
			{
				endAllWeapons();
				clearEventVisuals();
			}
		}
	}
	
	// Clean cursed sword icons and treasure chest icons.
	public void clearEventVisuals()
	{
		final ArrayList<ExCursedWeaponLocation.CursedWeaponInfo> splitList = new ArrayList<>();
		final Location locZ = new Location(100000, 100000, 0);
		final Location locA = new Location(-100000, -100000, 0);
		splitList.add(new ExCursedWeaponLocation.CursedWeaponInfo(locZ, 8190, 1, 0L));
		splitList.add(new ExCursedWeaponLocation.CursedWeaponInfo(locA, 8689, 1, 0L));
		broadcastToWorld(new ExCursedWeaponLocation(splitList));
		
		_zaricheBoxLocs.clear();
		_akamanahBoxLocs.clear();
		broadcastToWorld(new ExActivatedCursedTreasureBoxLocation(8190, _zaricheBoxLocs));
		broadcastToWorld(new ExActivatedCursedTreasureBoxLocation(8689, _akamanahBoxLocs));
		broadcastToWorld(new ExCursedWeaponLocation(Collections.emptyList()));
	}
	
	private void endAllWeapons()
	{
		for (CursedWeapon cw : _cursedWeapons.values())
		{
			// End active Sword Weapons so they don't remain in the world.
			if (cw.isActive())
			{
				cw.endOfLife();
			}
		}
	}
	
	/**
	 * Adds the location of a Treasure Chest and notifies all players.
	 * @param npcId The NPC ID of the Box (24370 or 24371)
	 * @param loc The location X, Y, Z
	 */
	public void addBoxLocation(int npcId, Location loc)
	{
		if (!_isEventActive)
		{
			return;
		}
		
		// Zariche Treasure Chests (24370).
		if (npcId == ZARICHE_BOX_NPC_ID)
		{
			if (!_zaricheBoxLocs.contains(loc))
			{
				_zaricheBoxLocs.add(loc);
				broadcastToWorld(new ExActivatedCursedTreasureBoxLocation(npcId, _zaricheBoxLocs));
			}
		}
		// Akamanah Treasure Chests (24371).
		else if (npcId == AKAMANAH_BOX_NPC_ID)
		{
			if (!_akamanahBoxLocs.contains(loc))
			{
				_akamanahBoxLocs.add(loc);
				broadcastToWorld(new ExActivatedCursedTreasureBoxLocation(npcId, _akamanahBoxLocs));
			}
		}
	}
	
	public void removeBoxLocation(int npcId, Location loc)
	{
		// Zariche Treasure Chests (24370).
		if (npcId == ZARICHE_BOX_NPC_ID)
		{
			_zaricheBoxLocs.remove(loc);
			broadcastToWorld(new ExActivatedCursedTreasureBoxLocation(npcId, _zaricheBoxLocs));
		}
		// Akamanah Treasure Chests (24371).
		else if (npcId == AKAMANAH_BOX_NPC_ID)
		{
			_akamanahBoxLocs.remove(loc);
			broadcastToWorld(new ExActivatedCursedTreasureBoxLocation(npcId, _akamanahBoxLocs));
		}
	}
	
	public List<Location> getZaricheBoxLocs()
	{
		if (!_isEventActive)
		{
			return java.util.Collections.emptyList();
		}
		
		return _zaricheBoxLocs;
	}
	
	public List<Location> getAkamanahBoxLocs()
	{
		if (!_isEventActive)
		{
			return java.util.Collections.emptyList();
		}
		
		return _akamanahBoxLocs;
	}
	
	@Override
	public void load()
	{
		if (!GeneralConfig.ALLOW_CURSED_WEAPONS)
		{
			return;
		}
		
		parseDatapackFile("data/CursedWeapons.xml");
		restore();
		controlPlayers();
		// LOGGER.info(getClass().getSimpleName() + ": Loaded " + _cursedWeapons.size() + " cursed weapons.");
	}
	
	public void increaseKills(int itemId, Player victim)
	{
		final CursedWeapon cw = _cursedWeapons.get(itemId);
		if (cw != null)
		{
			cw.increaseKills(victim);
		}
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("item".equalsIgnoreCase(d.getNodeName()))
					{
						NamedNodeMap attrs = d.getAttributes();
						final int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
						final int skillId = Integer.parseInt(attrs.getNamedItem("skillId").getNodeValue());
						final String name = attrs.getNamedItem("name").getNodeValue();
						final CursedWeapon cw = new CursedWeapon(id, skillId, name);
						int val;
						for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
						{
							// No longer by chance rate, during the event it is 100% configured in cursedweapon.java.
							if ("dropRate".equalsIgnoreCase(cd.getNodeName()))
							{
								attrs = cd.getAttributes();
								val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								cw.setDropRate(val);
							}
							else if ("duration".equalsIgnoreCase(cd.getNodeName()))
							{
								attrs = cd.getAttributes();
								val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								cw.setDuration(val);
							}
							else if ("durationLost".equalsIgnoreCase(cd.getNodeName()))
							{
								attrs = cd.getAttributes();
								val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								cw.setDurationLost(val);
							}
							else if ("disapearChance".equalsIgnoreCase(cd.getNodeName()))
							{
								attrs = cd.getAttributes();
								val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								cw.setDisapearChance(val);
							}
							else if ("stageKills".equalsIgnoreCase(cd.getNodeName()))
							{
								attrs = cd.getAttributes();
								val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								cw.setStageKills(val);
							}
						}
						
						_cursedWeapons.put(id, cw);
					}
				}
			}
		}
	}
	
	private void restore()
	{
		try (Connection con = DatabaseFactory.getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("SELECT itemId, charId, playerReputation, playerPkKills, nbKills, endTime FROM cursed_weapons"))
		{
			// LOGGER.info("[CW-DEBUG] private void restore in manage");
			// Restore data for each cursed weapon from the database.
			CursedWeapon cw;
			while (rs.next())
			{
				cw = _cursedWeapons.get(rs.getInt("itemId"));
				cw.setPlayerId(rs.getInt("charId"));
				cw.setPlayerReputation(rs.getInt("playerReputation"));
				cw.setPlayerPkKills(rs.getInt("playerPkKills"));
				cw.setNbKills(rs.getInt("nbKills"));
				cw.setEndTime(rs.getLong("endTime"));
				cw.reActivate();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not restore CursedWeapons data: ", e);
		}
	}
	
	private void controlPlayers()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT owner_id FROM items WHERE item_id=?"))
		{
			// Remove the weapon from those who shouldn't have it.
			for (CursedWeapon cw : _cursedWeapons.values())
			{
				if (cw.isActivated())
				{
					continue;
				}
				
				final int itemId = cw.getItemId();
				ps.setInt(1, itemId);
				try (ResultSet rset = ps.executeQuery())
				{
					if (rset.next())
					{
						final int playerId = rset.getInt("owner_id");
						LOGGER.info("PROBLEM : Player " + playerId + " owns the cursed weapon " + itemId + " but he shouldn't.");
						
						// Deletes the item from inventory.
						try (PreparedStatement delete = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?"))
						{
							delete.setInt(1, playerId);
							delete.setInt(2, itemId);
							delete.executeUpdate();
						}
						catch (Exception e)
						{
							LOGGER.warning("Error while deleting cursed weapon " + itemId + " from userId " + playerId);
						}
						
						// Restore Karma and PK.
						try (PreparedStatement update = con.prepareStatement("UPDATE characters SET reputation=?, pkkills=? WHERE charId=?"))
						{
							update.setInt(1, cw.getPlayerReputation());
							update.setInt(2, cw.getPlayerPkKills());
							update.setInt(3, playerId);
							update.executeUpdate();
						}
						
						// Remove skills from this specific player (in case the weapon still existed).
						try (PreparedStatement delSkills = con.prepareStatement("DELETE FROM character_skills WHERE charId=? AND skill_id IN (35398, 35400, 35399, 35401)"))
						{
							delSkills.setInt(1, playerId);
							delSkills.executeUpdate();
						}
						
						removeFromDb(itemId);
					}
				}
			}
			
			try (PreparedStatement psClean = con.prepareStatement("DELETE FROM character_skills WHERE skill_id IN (35398, 35399, 35400, 35401) " + "AND charId NOT IN (SELECT owner_id FROM items WHERE item_id IN (8190, 8689))"))
			{
				final int cleaned = psClean.executeUpdate();
				if (cleaned > 0)
				{
					// LOGGER.info("CursedWeaponsManager: Cleaned orphaned skills from " + cleaned + " players.");
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not check CursedWeapons data: ", e);
		}
	}
	
	public synchronized void checkDrop(Attackable attackable, Player player)
	{
		// Original block list.
		if ((attackable instanceof Defender) || (attackable instanceof Guard) || (attackable instanceof GrandBoss) || (attackable instanceof FeedableBeast) || (attackable instanceof FortCommander))
		{
			return;
		}
		
		// Block after 23h.
		final Calendar now = Calendar.getInstance();
		final int h = now.get(Calendar.HOUR_OF_DAY);
		final int m = now.get(Calendar.MINUTE);
		final int s = now.get(Calendar.SECOND);
		
		// Logic for absolute block for possession of cursed swords.
		// 1. (h >= 23) -> Blocks 23:00, 23:01, 00:00...
		// 2. (h < 18) -> Blocks morning/afternoon.
		// 3. (h == 22 && m == 59) -> Blocks entire 22:59.
		// 4. (h == 22 && m == 58 && s >= 50) -> Blocks the final 10s.
		if ((h >= 23) || (h < 18) || ((h == 22) && ((m == 59) || ((m == 58) && (s >= 50)))))
		{
			return; // Cancels drop.
		}
		
		// 1. Time Lock (Controlled by checkEventStatus).
		if (!_isEventActive)
		{
			return;
		}
		
		// 3. Blocks and rules (Level, Instance, Zone).
		if (attackable.getLevel() < 95)
		{
			return;
		}
		
		if (attackable.getInstanceId() > 0)
		{
			return;
		}
		
		// 3. Random drop between Cursed Swords.
		final List<CursedWeapon> shuffledWeapons = new ArrayList<>(_cursedWeapons.values());
		java.util.Collections.shuffle(shuffledWeapons);
		
		// Current region of the monster.
		final int currentRegionId = MapRegionData.getInstance().getMapRegionLocId(attackable.getX(), attackable.getY());
		for (CursedWeapon cw : shuffledWeapons)
		{
			if (cw.isActive())
			{
				continue;
			}
			
			// Region block.
			boolean regionOccupied = false;
			for (CursedWeapon activeCw : _cursedWeapons.values())
			{
				// Checks active Cursed Swords.
				if (activeCw.isActive() && (activeCw.getWorldPosition() != null))
				{
					// Region of the active Sword.
					final int activeRegionId = MapRegionData.getInstance().getMapRegionLocId(activeCw.getWorldPosition().getX(), activeCw.getWorldPosition().getY());
					
					// If the region is the same, prohibits the drop.
					if (currentRegionId == activeRegionId)
					{
						regionOccupied = true;
						break;
					}
				}
			}
			
			if (regionOccupied)
			{
				continue; // Region occupied, tries the next sword.
			}
			
			// If the region is free, tries the real drop (Chance %).
			if (cw.checkDrop(attackable, player))
			{
				break;
			}
		}
	}
	
	public void activate(Player player, Item item)
	{
		final CursedWeapon cw = _cursedWeapons.get(item.getId());
		if (player.isCursedWeaponEquipped())
		{
			final CursedWeapon cw2 = _cursedWeapons.get(player.getCursedWeaponEquippedId());
			
			// Update the kill count based on the stage kills.
			cw2.setNbKills(cw2.getStageKills() - 1);
			cw2.increaseKills();
			cw.setPlayer(player); // Necessary to identify the inventory location.
			cw.endOfLife(); // Expire the weapon and clean up.
		}
		else
		{
			cw.activate(player, item);
		}
	}
	
	public void drop(int itemId, Creature killer)
	{
		final CursedWeapon cw = _cursedWeapons.get(itemId);
		cw.dropIt(killer);
	}
	
	public void increaseKills(int itemId)
	{
		final CursedWeapon cw = _cursedWeapons.get(itemId);
		cw.increaseKills();
	}
	
	public int getLevel(int itemId)
	{
		final CursedWeapon cw = _cursedWeapons.get(itemId);
		return cw.getLevel();
	}
	
	public static void announce(SystemMessage sm)
	{
		Broadcast.toAllOnlinePlayers(sm);
	}
	
	public void checkPlayer(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		if (!_isEventActive)
		{
			// Check if player has a registered cursed sword and clear without drop.
			for (CursedWeapon cw : _cursedWeapons.values())
			{
				if (cw.isActivated() && (player.getObjectId() == cw.getPlayerId()))
				{
					// Bind player so forceClearWithoutDrop() works properly.
					cw.setPlayer(player);
					cw.setItem(player.getInventory().getItemByItemId(cw.getItemId()));
					
					// Direct cleanup: no drop, no endOfLife(), no risk of re-drop.
					cw.forceClearWithoutDrop();
					
					clearEventVisuals();
					return;
				}
			}
			
			// No sword: clear only visual packets.
			clearSinglePlayerScreen(player);
			return;
		}
		
		// Event ACTIVE: logic.
		for (CursedWeapon cw : _cursedWeapons.values())
		{
			if (cw.isActivated() && (player.getObjectId() == cw.getPlayerId()))
			{
				cw.setPlayer(player);
				cw.setItem(player.getInventory().getItemByItemId(cw.getItemId()));
				cw.giveSkill();
				player.setCursedWeaponEquippedId(cw.getItemId());
				
				// Use S1 to send the full string if the specific SystemMessageId is missing in this version.
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1);
				sm.addString(cw.getName() + "'s remaining time is " + ((cw.getEndTime() - System.currentTimeMillis()) / 60000) + " min. Type /cursedsword to check other information.");
				player.sendPacket(sm);
			}
		}
	}
	
	public int checkOwnsWeaponId(int ownerId)
	{
		for (CursedWeapon cw : _cursedWeapons.values())
		{
			if (cw.isActivated() && (ownerId == cw.getPlayerId()))
			{
				return cw.getItemId();
			}
		}
		
		return -1;
	}
	
	public static void removeFromDb(int itemId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM cursed_weapons WHERE itemId = ?"))
		{
			// LOGGER.info("[CW-DEBUG] public static void removeFromDb ");
			ps.setInt(1, itemId);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Failed to remove data: " + e.getMessage(), e);
		}
	}
	
	public void saveData()
	{
		for (CursedWeapon cw : _cursedWeapons.values())
		{
			// LOGGER.info("[CW-DEBUG] savedata manage");
			cw.saveData();
		}
	}
	
	public boolean isCursed(int itemId)
	{
		return _cursedWeapons.containsKey(itemId);
	}
	
	public Collection<CursedWeapon> getCursedWeapons()
	{
		return _cursedWeapons.values();
	}
	
	public Set<Integer> getCursedWeaponsIds()
	{
		return _cursedWeapons.keySet();
	}
	
	public CursedWeapon getCursedWeapon(int itemId)
	{
		return _cursedWeapons.get(itemId);
	}
	
	public void givePassive(int itemId)
	{
		try
		{
			_cursedWeapons.get(itemId).giveSkill();
		}
		catch (Exception e)
		{
		}
	}
	
	public static CursedWeaponsManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CursedWeaponsManager INSTANCE = new CursedWeaponsManager();
	}
	
	public void broadcastToWorld(ServerPacket packet)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if ((player != null) && player.isOnline())
			{
				player.sendPacket(packet);
			}
		}
	}
	
	public void endEventForced()
	{
		endAllWeapons();
		_isEventActive = false;
		clearEventVisuals();
	}
	
	public void clearSinglePlayerScreen(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		final Set<Integer> emptyActiveList = new HashSet<>();
		player.sendPacket(new ExCursedWeaponList(emptyActiveList));
		
		final ArrayList<ExCursedWeaponLocation.CursedWeaponInfo> splitList = new ArrayList<>();
		final Location locZ = new Location(100000, 100000, 0);
		final Location locA = new Location(-100000, -100000, 0);
		splitList.add(new ExCursedWeaponLocation.CursedWeaponInfo(locZ, 8190, 1, 0L));
		splitList.add(new ExCursedWeaponLocation.CursedWeaponInfo(locA, 8689, 1, 0L));
		player.sendPacket(new ExCursedWeaponLocation(splitList));
		
		player.sendPacket(new ExCursedWeaponLocation(Collections.emptyList()));
		player.sendPacket(new ExActivatedCursedTreasureBoxLocation(8190, Collections.emptyList()));
		player.sendPacket(new ExActivatedCursedTreasureBoxLocation(8689, Collections.emptyList()));
	}
}
