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
package org.l2jmobius.gameserver.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.managers.CursedWeaponsManager;
import org.l2jmobius.gameserver.managers.MailManager;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.PartyMessageType;
import org.l2jmobius.gameserver.model.item.enums.BodyPart;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.itemcontainer.Mail;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.enums.SkillFinishType;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.Earthquake;
import org.l2jmobius.gameserver.network.serverpackets.ExRedSky;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * CursedWeapons AI - Version Prelude of War 2019
 * @author Notorion
 */
public class CursedWeapon
{
	private static final Logger LOGGER = Logger.getLogger(CursedWeapon.class.getName());
	// _name is the name of the cursed weapon associated with its ID.
	private final String _name;
	// _itemId is the Item ID of the cursed weapon.
	private final int _itemId;
	// _skillId is the skills ID.
	private final int _skillId;
	// The drop rate is defined directly by cursedweapon.java at 100% for the CW model version 2019
	// private int _dropRate;
	private int _duration;
	private int _durationLost;
	private int _disapearChance;
	private int _stageKills;
	// this should be false unless if the cursed weapon is dropped, in that case it would be true.
	private boolean _isDropped = false;
	// this sets the cursed weapon status to true only if a player has the cursed weapon, otherwise this should be false.
	private boolean _isActivated = false;
	private boolean _rewardSent = false;
	private ScheduledFuture<?> _removeTask;
	private int _nbKills = 0;
	long _endTime = 0;
	private int _playerId = 0;
	protected Player _player = null;
	private Item _item = null;
	private int _playerReputation = 0;
	private int _playerPkKills = 0;
	protected int transformationId = 0;
	private long _lastKillTime = 0;
	
	public CursedWeapon(int itemId, int skillId, String name)
	{
		_name = name;
		_itemId = itemId;
		_skillId = skillId;
	}
	
	public void endOfLife()
	{
		if (_isActivated)
		{
			// Allows the Defense script to end and the reward to be sent to the player
			if (System.currentTimeMillis() >= (_endTime - 60000))
			{
				sendEventReward();
			}
			if ((_player != null) && _player.isOnline())
			{
				java.util.Calendar now = java.util.Calendar.getInstance();
				int h = now.get(java.util.Calendar.HOUR_OF_DAY);
				
				// 1. Clear combat status and restore the player's original points
				_player.abortAttack();
				_player.setReputation(_playerReputation);
				_player.setPkKills(_playerPkKills);
				_player.setCursedWeaponEquippedId(0);
				
				// 2. Remove all skills (This will automatically remove the Abnormal Visual Effect)
				removeSkill();
				
				// 3. Unequip the weapon from the hand, save the player and destroy the item in the inventory
				_player.getInventory().unEquipItemInBodySlot(BodyPart.LR_HAND);
				_player.storeMe();
				_player.getInventory().destroyItemByItemId(ItemProcessType.NONE, _itemId, 1, _player, null);
				
				// 4. Send the update packet to the client
				_player.sendItemList();
				_player.broadcastUserInfo();
				
				// 5. Check the event time and apply death only AFTER the player is 100% normal
				if (h >= 23)
				{
					if (!_player.isDead())
					{
						_player.doDie(null);
					}
				}
			}
			else
			{
				// Remove from OFFLINE player (Via Database)
				try (Connection con = DatabaseFactory.getConnection())
				{
					// 1. Delete the Item
					try (PreparedStatement del = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?"))
					{
						del.setInt(1, _playerId);
						del.setInt(2, _itemId);
						if (del.executeUpdate() != 1)
						{
							LOGGER.warning("Error while deleting itemId " + _itemId + " from userId " + _playerId);
						}
					}
					// 2. Restore Reputation and PK
					try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET reputation=?, pkkills=? WHERE charId=?"))
					{
						ps.setInt(1, _playerReputation);
						ps.setInt(2, _playerPkKills);
						ps.setInt(3, _playerId);
						if (ps.executeUpdate() != 1)
						{
							LOGGER.warning("Error while updating karma & pkkills for userId " + _playerId);
						}
					}
					// 3. Clear Offline Skills
					try (PreparedStatement delSkills = con.prepareStatement("DELETE FROM character_skills WHERE charId=? AND skill_id IN (35398, 35400, 35399, 35401)"))
					{
						delSkills.setInt(1, _playerId);
						delSkills.executeUpdate();
					}
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "Could not delete : " + e.getMessage(), e);
				}
			}
		}
		else
		{
			// either this cursed weapon is in the inventory of someone who has another cursed weapon equipped,
			// OR this cursed weapon is on the ground.
			if ((_player != null) && (_player.getInventory().getItemByItemId(_itemId) != null))
			{
				// Destroy
				_player.getInventory().destroyItemByItemId(ItemProcessType.NONE, _itemId, 1, _player, null);
				_player.sendItemList();
				_player.broadcastUserInfo();
			}
			// is dropped on the ground
			else if (_item != null)
			{
				_item.decayMe();
				// LOGGER.info(_name + " item has been removed from World.");
			}
		}
		// Delete infos from table if any
		CursedWeaponsManager.removeFromDb(_itemId);
		// Message to indicate the disappearance of the Cursed Sword
		SystemMessage sm = new SystemMessage(1818);
		sm.addItemName(_itemId);
		broadcastToWorld(sm);
		updateMapIcon();
		// Reset state
		cancelTask();
		_isActivated = false;
		_isDropped = false;
		_endTime = 0;
		_player = null;
		_playerId = 0;
		_playerReputation = 0;
		_playerPkKills = 0;
		_item = null;
		_nbKills = 0;
		// Reset the timer so the next holder starts at 0
		_lastKillTime = 0;
	}
	
	private void cancelTask()
	{
		if (_removeTask != null)
		{
			_removeTask.cancel(true);
			_removeTask = null;
		}
	}
	
	private class RemoveTask implements Runnable
	{
		protected RemoveTask()
		{
		}
		
		@Override
		public void run()
		{
			if (System.currentTimeMillis() >= _endTime)
			{
				endOfLife();
			}
		}
	}
	
	private void dropIt(Attackable attackable, Player player)
	{
		dropIt(attackable, player, null, true);
	}
	
	// Function to clear cursed sword holder outside event time (23h/18h)
	public void forceClearWithoutDrop()
	{
		cancelTask();
		
		if (_player != null)
		{
			if (_player.getInventory().getItemByItemId(_itemId) != null)
			{
				_player.getInventory().unEquipItemInBodySlot(org.l2jmobius.gameserver.model.item.enums.BodyPart.LR_HAND);
				_player.getInventory().destroyItemByItemId(org.l2jmobius.gameserver.model.item.enums.ItemProcessType.NONE, _itemId, 1, _player, null);
			}
			_player.setReputation(_playerReputation);
			_player.setPkKills(_playerPkKills);
			_player.setCursedWeaponEquippedId(0);
			removeSkill();
			_player.getAppearance().setVisibleName(null);
			_player.abortAttack();
			_player.sendItemList();
			_player.broadcastUserInfo();
			_player.sendPacket(new org.l2jmobius.gameserver.network.serverpackets.UserInfo(_player));
		}
		else if (_playerId > 0)
		{
			try (java.sql.Connection con = org.l2jmobius.commons.database.DatabaseFactory.getConnection())
			{
				try (java.sql.PreparedStatement ps = con.prepareStatement("UPDATE characters SET reputation=?, pkkills=? WHERE charId=?"))
				{
					ps.setInt(1, _playerReputation);
					ps.setInt(2, _playerPkKills);
					ps.setInt(3, _playerId);
					ps.executeUpdate();
				}
				try (java.sql.PreparedStatement delSkills = con.prepareStatement("DELETE FROM character_skills WHERE charId=? AND skill_id IN (35398,35400,35399,35401)"))
				{
					delSkills.setInt(1, _playerId);
					delSkills.executeUpdate();
				}
				try (java.sql.PreparedStatement del = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?"))
				{
					del.setInt(1, _playerId);
					del.setInt(2, _itemId);
					del.executeUpdate();
				}
			}
			catch (Exception e)
			{
				LOGGER.warning("forceClearWithoutDrop: DB cleanup error: " + e.getMessage());
			}
		}
		
		CursedWeaponsManager.removeFromDb(_itemId);
		
		_isActivated = false;
		_isDropped = false;
		_endTime = 0;
		_player = null;
		_playerId = 0;
		_playerReputation = 0;
		_playerPkKills = 0;
		_item = null;
		_nbKills = 0;
		_rewardSent = false;
	}
	
	private void dropIt(Attackable attackable, Player player, Creature killer, boolean fromMonster)
	{
		// Locks physical drop (Cursed Sword)
		java.util.Calendar now = java.util.Calendar.getInstance();
		int h = now.get(java.util.Calendar.HOUR_OF_DAY);
		// 1. After 23h (h >= 23)
		// 2. Before 18h (h < 18) - To prevent dropping in the morning/afternoon
		if ((h >= 23) || (h < 18))
		{
			forceClearWithoutDrop();
			return;
		}
		if (fromMonster)
		{
			_isActivated = false;
			_item = attackable.dropItem(player, _itemId, 1);
			_item.setDropTime(0);
			// Only execute effects if the item REALLY dropped
			if (_item != null)
			{
				// Visual Effects
				broadcastToWorld(new ExRedSky(10));
				broadcastToWorld(new Earthquake(_item.getX(), _item.getY(), _item.getZ(), 14, 3));
			}
		}
		else
		{
			// Player drop
			if (_player == null)
			{
				return;
			}
			// Visual death effect (souls) of the player carrying the cursed sword (Always executed on death/drop)
			final int soulSkillId = (_itemId == 8190) ? 35402 : 35403;
			final org.l2jmobius.gameserver.model.actor.templates.NpcTemplate template = org.l2jmobius.gameserver.data.xml.NpcData.getInstance().getTemplate(18919);
			if (template != null)
			{
				final org.l2jmobius.gameserver.model.actor.Npc soulsDummy = new org.l2jmobius.gameserver.model.actor.Npc(template);
				// Define location based on who died (Player)
				int x = fromMonster ? attackable.getX() : _player.getX();
				int y = fromMonster ? attackable.getY() : _player.getY();
				int z = fromMonster ? attackable.getZ() : _player.getZ();
				soulsDummy.setInstance(fromMonster ? attackable.getInstanceWorld() : _player.getInstanceWorld());
				soulsDummy.spawnMe(x, y, z);
				org.l2jmobius.commons.threads.ThreadPool.schedule(() ->
				{
					if (soulsDummy.isSpawned())
					{
						soulsDummy.broadcastPacket(new org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse(soulsDummy, soulsDummy, soulSkillId, 1, 1000, 0));
					}
				}, 300);
				org.l2jmobius.commons.threads.ThreadPool.schedule(soulsDummy::deleteMe, 5000);
			}
			_isActivated = false;
			_item = _player.getInventory().getItemByItemId(_itemId);
			_player.dropItem(ItemProcessType.DEATH, _item, killer, true);
			_player.setReputation(_playerReputation);
			_player.setPkKills(_playerPkKills);
			_player.setCursedWeaponEquippedId(0);
			// 1. Remove CursedWeapon Skills
			removeSkill();
			// 2. Removal of buffs
			if (_itemId == 8190)
			{
				_player.removeSkill(35398); // Soul of Sword - Zariche - Status
				_player.removeSkill(35400); // Demonic Sword Zariche - Level
			}
			else if (_itemId == 8689)
			{
				_player.removeSkill(35399); // Soul of Sword - Akamanah - Status
				_player.removeSkill(35401); // Blood Sword Akamanah - Level
			}
			_player.stopAllEffectsExceptThoseThatLastThroughDeath();
			_player.abortAttack();
			_player.sendPacket(new org.l2jmobius.gameserver.network.serverpackets.UserInfo(_player));
			_player.broadcastUserInfo();
			if (_item != null)
			{
				broadcastToWorld(new ExRedSky(10));
				broadcastToWorld(new Earthquake(_item.getX(), _item.getY(), _item.getZ(), 14, 3));
			}
		}
		_isDropped = true;
		_nbKills = 0;
		updateMapIcon();
		if (_item != null)
		{
			NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);
			long currentTotal = getCurrentReward();
			long guaranteed = 5_000_000_000L;
			long changed = currentTotal - guaranteed;
			if (changed < 0)
			{
				changed = 0;
			}
			// Message id 1815
			// $s2 has appeared in $s1. The Treasure Chest contains $s2 adena. Fixed reward: $s3, additional reward: $s4. The adena will be given to the last owner at 23:59.
			SystemMessage sm = new SystemMessage(1815);
			sm.addZoneName(_item.getX(), _item.getY(), _item.getZ()); // $s1
			sm.addItemName(_itemId); // $s2
			// Reward
			// Bonus ($s3) - Guaranteed ($s4)
			sm.addString(nf.format(changed)); // $s3 (Bonus)
			sm.addString(nf.format(guaranteed)); // $s4 (Guaranteed)
			broadcastToWorld(sm);
		}
		if (_removeTask != null)
		{
			_removeTask.cancel(true);
			_removeTask = null;
		}
		// Schedule to disappear in 10 minutes (600,000 ms)
		_removeTask = ThreadPool.schedule(this::endOfLife, 600000);
		
	}
	
	public void cursedOnLogin()
	{
		// Re-applies skills to the player
		int skillIdToApply = 0;
		if (_itemId == 8190)
		{
			skillIdToApply = 35398;
		}
		else if (_itemId == 8689)
		{
			skillIdToApply = 35399;
		}
		if (skillIdToApply > 0)
		{
			int level = getLevel(); // Uses getLevel() method to ensure the correct level
			int maxLvl = SkillData.getInstance().getMaxLevel(skillIdToApply);
			if (level > maxLvl)
			{
				level = maxLvl;
			}
			Skill skill = SkillData.getInstance().getSkill(skillIdToApply, level);
			if (skill != null)
			{
				_player.addSkill(skill, false);
				skill.applyEffects(_player, _player);
				_player.sendSkillList();
			}
		}
		NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);
		long currentTotal = getCurrentReward();
		long guaranteed = 5_000_000_000L;
		long changed = currentTotal - guaranteed;
		if (changed < 0)
		{
			changed = 0;
		}
		// Login message id 1817
		// The $s2's owner is in $s1. The Treasure Chest contains $s2 adena. Fixed reward: $s3, additional reward: $s4. The adena will be given to the last owner at 23:59.
		SystemMessage sm = new SystemMessage(1817);
		sm.addZoneName(_player.getX(), _player.getY(), _player.getZ()); // $s1
		sm.addItemName(_player.getCursedWeaponEquippedId()); // $s2
		// Reward Visual
		sm.addString(nf.format(changed)); // $s3 (Bonus)
		sm.addString(nf.format(guaranteed)); // $s4 (Guaranteed)
		broadcastToWorld(sm);
		_player.getAppearance().setVisibleName(_name);
		broadcastToWorld(new ExRedSky(10));
		broadcastToWorld(new Earthquake(_player.getX(), _player.getY(), _player.getZ(), 14, 3));
		// Time message
		final CursedWeapon cw = CursedWeaponsManager.getInstance().getCursedWeapon(_player.getCursedWeaponEquippedId());
		if (cw != null)
		{
			final int timeLeft = (int) (cw.getTimeLeft() / 60000);
			
			final SystemMessage msg = new SystemMessage(SystemMessageId.S1);
			msg.addString(cw.getName() + "'s remaining time is " + timeLeft + " min. Type /cursedsword to check other information.");
			_player.sendPacket(msg);
		}
		// Update visual to ensure
		_player.broadcastUserInfo();
	}
	
	// Rebinds all CursedWeapon skills for the player.
	// Note: The visual transformation is handled automatically by the Status Skill effects (35398/35399).
	public void giveSkill()
	{
		// Zariche: 35398 (Status/Visual) + 35400 (Level)
		// Akamanah: 35399 (Status/Visual) + 35401 (Level)
		int statSkillId = 0;
		int levelSkillId = 0;
		if (_itemId == 8190) // Zariche
		{
			statSkillId = 35398;
			levelSkillId = 35400;
		}
		else if (_itemId == 8689) // Akamanah
		{
			statSkillId = 35399;
			levelSkillId = 35401;
		}
		if (statSkillId == 0)
		{
			return;
		}
		// Remove any CW skill the player has, avoiding duplication
		_player.removeSkill(35398);
		_player.removeSkill(35399);
		_player.removeSkill(35400);
		_player.removeSkill(35401);
		for (int id : new int[]
		{
			35398,
			35399,
			35400,
			35401
		})
		{
			_player.stopSkillEffects(SkillFinishType.REMOVED, id);
		}
		// Applies the skill status
		Skill statSkill = SkillData.getInstance().getSkill(statSkillId, 1);
		if (statSkill != null)
		{
			_player.addSkill(statSkill, true); // true = save on character
			// Forces immediate effect application (including visual transformation)
			statSkill.applyEffects(_player, _player);
		}
		// Applies the level skill (Based on Kills)
		int currentLevel = getLevel(); // Gets the current kills calculation
		// Ensures it does not exceed the maximum level
		int maxLvl = SkillData.getInstance().getMaxLevel(levelSkillId);
		if (currentLevel > maxLvl)
		{
			currentLevel = maxLvl;
		}
		Skill levelSkill = SkillData.getInstance().getSkill(levelSkillId, currentLevel);
		if (levelSkill != null)
		{
			_player.addSkill(levelSkill, true);
			levelSkill.applyEffects(_player, _player);
		}
		_player.addTransformSkill(CommonSkill.VOID_BURST.getSkill());
		_player.addTransformSkill(CommonSkill.VOID_FLOW.getSkill());
		_player.sendSkillList();
		_player.broadcastUserInfo();
	}
	
	// List of all Cursed Weapon skills (Zariche and Akamanah)
	// 35398: Zariche Stat
	// 35400: Zariche Level
	// 35399: Akamanah Stat
	// 35401: Akamanah Level
	public void removeSkill()
	{
		int[] allCursedSkills =
		{
			35398,
			35400,
			35399,
			35401
		};
		for (int skillId : allCursedSkills)
		{
			_player.removeSkill(skillId);
			Skill skill = SkillData.getInstance().getSkill(skillId, 1);
			if (skill != null)
			{
				_player.stopSkillEffects(skill);
			}
		}
		if (_skillId > 0)
		{
			_player.removeSkill(_skillId);
			Skill skill = SkillData.getInstance().getSkill(_skillId, 1);
			if (skill != null)
			{
				_player.stopSkillEffects(skill);
			}
		}
		_player.getAppearance().setVisibleName(null);
		_player.sendSkillList();
		_player.broadcastUserInfo();
		_player.sendPacket(new org.l2jmobius.gameserver.network.serverpackets.UserInfo(_player));
	}
	
	public void reActivate()
	{
		_isActivated = true;
		if ((_endTime - System.currentTimeMillis()) <= 0)
		{
			endOfLife();
			return;
		}
		
		// Outside event time: schedule cleanup without drop with minimum delay
		// (player may still be offline at the moment of restore)
		java.util.Calendar now = java.util.Calendar.getInstance();
		int h = now.get(java.util.Calendar.HOUR_OF_DAY);
		
		// Same release logic already present in activate()
		if ((h >= 23) || (h < 18))
		{
			// If player is GM or has the panel flag, skip forced removal after 2s
			if ((_player != null) && (_player.isGM() || _player.getVariables().getBoolean("CW_GM_AUTH", false)))
			{
				// GM or tester is authorized outside event time.
			}
			else
			{
				// Schedule with 2s delay to ensure player object is loaded
				ThreadPool.schedule(() ->
				{
					if (_player != null)
					{
						forceClearWithoutDrop();
					}
					else
					{
						// Player offline: cleanup only via database
						CursedWeaponsManager.removeFromDb(_itemId);
						_isActivated = false;
						_isDropped = false;
						_endTime = 0;
						_playerId = 0;
						_playerReputation = 0;
						_playerPkKills = 0;
						_item = null;
						_nbKills = 0;
						_rewardSent = false;
					}
				}, 2000);
				return;
			}
		}
		
		// Event time: original logic
		_removeTask = ThreadPool.scheduleAtFixedRate(new RemoveTask(), _durationLost * 12000, _durationLost * 12000);
	}
	
	public boolean checkDrop(Attackable attackable, Player player)
	{
		// Prevents Priests (24366/24368) or Treasure Chests (24370/24371) from dropping the Sword
		int mobId = attackable.getId();
		if ((mobId == 24366) || (mobId == 24367) || (mobId == 24368) || (mobId == 24369) || (mobId == 24370) || (mobId == 24371))
		{
			return false;
		}
		if (CursedWeaponsManager.getInstance().isEventActive())
		{
			dropIt(attackable, player);
			if (_removeTask != null)
			{
				_removeTask.cancel(true);
			}
			// Schedule to disappear in 10 minutes (600,000 ms)
			_removeTask = ThreadPool.schedule(this::endOfLife, 600000);
			_endTime = System.currentTimeMillis() + (_duration * 60000);
			return true;
		}
		return false;
	}
	
	public void activate(Player player, Item item)
	{
		// Authorization for GM to apply the Cursed Sword on player or self outside event hours or during 18h/23h59
		// If it does NOT have the authorization variable "CW_GM_AUTH" (which admin_cw_add placed)
		// Executes the time lock.
		if (!player.isGM() && !player.getVariables().getBoolean("CW_GM_AUTH", false))
		{
			java.util.Calendar now = java.util.Calendar.getInstance();
			int h = now.get(java.util.Calendar.HOUR_OF_DAY);
			int m = now.get(java.util.Calendar.MINUTE);
			int s = now.get(java.util.Calendar.SECOND);
			if ((h >= 23) || (h < 18) || ((h == 22) && ((m == 59) || ((m == 58) && (s >= 50)))))
			{
				// Destroys the item
				if (item != null)
				{
					try
					{
						player.getInventory().destroyItemByItemId(org.l2jmobius.gameserver.model.item.enums.ItemProcessType.NONE, item.getId(), 1, player, null);
					}
					catch (Exception e)
					{
					}
				}
				_isActivated = false;
				return;
			}
		}
		_rewardSent = false;
		_rewardSent = false;
		cancelTask();
		// If mounted, dismounts
		if (player.isMounted() && !player.dismount())
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
			player.dropItem(ItemProcessType.DROP, item, null, true);
			return;
		}
		_isActivated = true;
		
		// If the sword was forced by GM, it skips drop and starts with time 0.
		if (_endTime == 0)
		{
			_endTime = System.currentTimeMillis() + (_duration * 60000);
		}
		// Start inactivity timer at the exact moment the cursed sword is equipped
		_lastKillTime = System.currentTimeMillis();
		
		_player = player;
		_playerId = _player.getObjectId();
		_playerReputation = _player.getReputation();
		_playerPkKills = _player.getPkKills();
		saveData();
		_player.setCursedWeaponEquippedId(_itemId);
		_player.setReputation(-9999999);
		_player.setPkKills(0);
		if (_player.isInParty())
		{
			_player.getParty().removePartyMember(_player, PartyMessageType.EXPELLED);
		}
		_item = item;
		// Auto-Equip
		// Clears hands to ensure no occupied slot error occurs
		_player.getInventory().unEquipItemInBodySlot(BodyPart.LR_HAND);
		_player.getInventory().unEquipItemInBodySlot(BodyPart.L_HAND);
		_player.getInventory().unEquipItemInBodySlot(BodyPart.R_HAND);
		_player.getInventory().equipItem(_item);
		int skillIdToApply = 0;
		if (_itemId == 8190)
		{
			skillIdToApply = 35398;
		}
		else if (_itemId == 8689)
		{
			skillIdToApply = 35399;
		}
		int level = 1 + (_nbKills / _stageKills);
		int maxLvl = SkillData.getInstance().getMaxLevel(skillIdToApply);
		if (level > maxLvl)
		{
			level = maxLvl;
		}
		// apply Effects
		if (skillIdToApply > 0)
		{
			Skill skill = SkillData.getInstance().getSkill(skillIdToApply, level);
			if (skill != null)
			{
				_player.addSkill(skill, false);
				skill.applyEffects(_player, _player);
				_player.sendSkillList();
			}
		}
		_player.getAppearance().setVisibleName(_name);
		// Since _nbKills starts at 0, getLevel() will return 1.
		// The player starts with Level 1 Buff applied.
		giveSkill();
		final SystemMessage sm = new SystemMessage(SystemMessageId.S1);
		sm.addString(_name + " equipped.");
		player.sendPacket(sm);
		NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);
		long currentTotal = getCurrentReward();
		long guaranteed = 5_000_000_000L;
		long changed = currentTotal - guaranteed;
		if (changed < 0)
		{
			changed = 0;
		}
		// Sends Message 1816 ($s1=Zone, $s2=Item, $s3=Guaranteed, $s4=Changed)
		// The $s2's owner has appeared in $s1. The Treasure Chest contains $s2 adena. Fixed reward: $s3, additional reward: $s4. The adena will be given to the last owner at 23:59.
		SystemMessage msg = new SystemMessage(1816);
		msg.addZoneName(_player.getX(), _player.getY(), _player.getZ());
		msg.addItemName(_item);
		msg.addString(nf.format(changed)); // $s3 (Bonus)
		msg.addString(nf.format(guaranteed)); // $s4 (Guaranteed)
		CursedWeaponsManager.getInstance().broadcastToWorld(msg);
		_player.sendItemList();
		_player.broadcastUserInfo();
		// Possession visual upon acquiring the Cursed Sword (Temporary Abnormal)
		final AbnormalVisualEffect visualEffect = AbnormalVisualEffect.INIT_TRANSFORM;
		_player.getEffectList().startAbnormalVisualEffect(visualEffect);
		// 3 second effect (3000ms)
		ThreadPool.schedule(() ->
		{
			if ((_player != null) && _player.isOnline())
			{
				_player.getEffectList().stopAbnormalVisualEffect(visualEffect);
			}
		}, 3000);
		
		// Purge ghost icons from the client's map cache.
		purgeGhostMapIcons();
		// Recovery HP CP MP
		ThreadPool.schedule(() ->
		{
			if ((_player != null) && _player.isOnline() && !_player.isDead())
			{
				_player.setCurrentHpMp(_player.getMaxHp(), _player.getMaxMp());
				_player.setCurrentCp(_player.getMaxCp());
				_player.broadcastStatusUpdate();
			}
		}, 500);
	}
	
	// Purge ghost icons from the client's map cache.
	// Sends the swords to opposite poles to break Icon
	// and then passes an empty list to finalize cleanup.
	public void purgeGhostMapIcons()
	{
		// 1. Send icons to opposite poles to tear Icon
		java.util.List<org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo> splitList = new java.util.ArrayList<>();
		org.l2jmobius.gameserver.model.Location locZ = new org.l2jmobius.gameserver.model.Location(100000, 100000, 0);
		org.l2jmobius.gameserver.model.Location locA = new org.l2jmobius.gameserver.model.Location(-100000, -100000, 0);
		splitList.add(new org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo(locZ, 8190, 1, 0L));
		splitList.add(new org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo(locA, 8689, 1, 0L));
		broadcastToWorld(new org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation(splitList));
		// 2. Pass an empty list to clear the dirty map history
		broadcastToWorld(new org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation(java.util.Collections.emptyList()));
		// 3. Immediately update the radar with the real coordinates
		updateMapIcon();
	}
	
	public void saveData()
	{
		// LOGGER.info("[CW-DEBUG] Saving Cursed Weapon data " + _name + " (ID: " + _itemId + ")...");
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement del = con.prepareStatement("DELETE FROM cursed_weapons WHERE itemId = ?");
			PreparedStatement ps = con.prepareStatement("INSERT INTO cursed_weapons (itemId, charId, playerReputation, playerPkKills, nbKills, endTime) VALUES (?, ?, ?, ?, ?, ?)"))
		{
			// Delete previous datas
			del.setInt(1, _itemId);
			del.executeUpdate();
			if (_isActivated)
			{
				ps.setInt(1, _itemId);
				ps.setInt(2, _playerId);
				ps.setInt(3, _playerReputation);
				ps.setInt(4, _playerPkKills);
				ps.setInt(5, _nbKills);
				ps.setLong(6, _endTime);
				ps.executeUpdate();
				// LOGGER.info("[CW-DEBUG] SAVE SUCCESS! Weapon saved for PlayerOID: " + _playerId + " with endTime: " + _endTime);
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "CursedWeapon: Failed to save data.", e);
		}
	}
	
	public void dropIt(Creature killer)
	{
		if (Rnd.get(100) <= _disapearChance)
		{
			endOfLife();
		}
		else
		{
			// At the end of the event, clear player reference.
			dropIt(null, null, killer, false);
			
			if (_player != null)
			{
				_player.setReputation(_playerReputation);
				_player.setPkKills(_playerPkKills);
				_player.setCursedWeaponEquippedId(0);
				removeSkill();
				_player.abortAttack();
				_player.broadcastUserInfo();
			}
		}
	}
	
	// Inactivity timer (2 hours without a kill)
	public long getLastKillTime()
	{
		return _lastKillTime;
	}
	
	public void setLastKillTime(long time)
	{
		_lastKillTime = time;
	}
	
	// Compatibility method (kept in case some old script calls without target)
	public void increaseKills()
	{
		increaseKills(null);
	}
	
	public void increaseKills(Player victim)
	{
		// Starts at 0. Thus, if killing level 1, it remains 0.
		int points = 0;
		if (victim != null)
		{
			// If both have clan and it's the same ID, cancel point gain.
			if ((_player.getClanId() > 0) && (victim.getClanId() > 0) && (_player.getClanId() == victim.getClanId()))
			{
				_player.sendMessage("Cursed Weapon: Killing clan members does not grant power.");
				return;
			}
			int lvl = victim.getLevel();
			// Points Table - Prelude of War 2019
			if (lvl >= 111)
			{
				points = 700;
			}
			else if (lvl >= 106)
			{
				points = 500;
			}
			else if (lvl >= 100)
			{
				points = 100;
			}
			else if (lvl >= 85)
			{
				points = 10;
			}
			else if (lvl >= 21)
			{
				points = 1;
				// If lower than 21, points remain 0
			}
			if (victim.isCursedWeaponEquipped())
			{
				points = 1000;
			}
		}
		// Only executes if gained any points.
		if (points > 0)
		{
			// Timer to remove Sword Cursed - Player must make a kill during the event to extend the time
			_lastKillTime = System.currentTimeMillis();
			// Sums internal CW points
			_nbKills += points;
			if ((_player != null) && _player.isOnline())
			{
				// Sums +1 to the player's Visual PK
				_player.setPkKills(_player.getPkKills() + 1);
				// Checks if leveled up
				giveSkill();
				// updateMapIcon();
			}
			saveData();
		}
	}
	
	public void setDisapearChance(int disapearChance)
	{
		_disapearChance = disapearChance;
	}
	
	// No longer by chance rate, during the event it is 100%
	public void setDropRate(int dropRate)
	{
		// _dropRate = dropRate;
	}
	
	public void setDuration(int duration)
	{
		_duration = duration;
	}
	
	public void setDurationLost(int durationLost)
	{
		_durationLost = durationLost;
	}
	
	public void setStageKills(int stageKills)
	{
		_stageKills = stageKills;
	}
	
	public void setNbKills(int nbKills)
	{
		_nbKills = nbKills;
	}
	
	public void setPlayerId(int playerId)
	{
		_playerId = playerId;
	}
	
	public void setPlayerReputation(int playerReputation)
	{
		_playerReputation = playerReputation;
	}
	
	public void setPlayerPkKills(int playerPkKills)
	{
		_playerPkKills = playerPkKills;
	}
	
	public void setActivated(boolean isActivated)
	{
		_isActivated = isActivated;
	}
	
	public void setDropped(boolean isDropped)
	{
		_isDropped = isDropped;
	}
	
	public void setEndTime(long endTime)
	{
		_endTime = endTime;
	}
	
	public void setPlayer(Player player)
	{
		_player = player;
	}
	
	public void setItem(Item item)
	{
		_item = item;
	}
	
	public boolean isActivated()
	{
		return _isActivated;
	}
	
	public boolean isDropped()
	{
		return _isDropped;
	}
	
	public long getEndTime()
	{
		return _endTime;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public int getPlayerId()
	{
		return _playerId;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public int getPlayerReputation()
	{
		return _playerReputation;
	}
	
	public int getPlayerPkKills()
	{
		return _playerPkKills;
	}
	
	public int getNbKills()
	{
		return _nbKills;
	}
	
	public int getStageKills()
	{
		return _stageKills;
	}
	
	public boolean isActive()
	{
		return _isActivated || _isDropped;
	}
	
	// Adena Table:
	// Level 1: 5,000,000,000 (5 bi base)
	// Level 2: 5,200,000,000 (5 bi + 200 mi)
	// Level 3: 5,500,000,000 (5 bi + 500 mi)
	// Level 4: 7,000,000,000 (5 bi + 2 bi)
	// Level 5: 10,000,000,000 (5 bi + 5 bi)
	public int getLevel()
	{
		// Calculates based on current prize (adena)
		long currentReward = getCurrentReward();
		// Thresholds based on Adena (not kills!)
		if (currentReward >= 10_000_000_000L) // 10 bi
		{
			return 5;
		}
		if (currentReward >= 7_000_000_000L) // 7 bi
		{
			return 4;
		}
		if (currentReward >= 5_500_000_000L) // 5.5 bi
		{
			return 3;
		}
		if (currentReward >= 5_200_000_000L) // 5.2 bi
		{
			return 2;
		}
		return 1; // 5 bi base
	}
	
	public long getTimeLeft()
	{
		return _endTime - System.currentTimeMillis();
	}
	
	public void goTo(Player player)
	{
		if (player == null)
		{
			return;
		}
		if (_isActivated && (_player != null))
		{
			// Go to player holding the weapon
			player.teleToLocation(_player.getLocation(), true);
		}
		else if (_isDropped && (_item != null))
		{
			// Go to item on the ground
			player.teleToLocation(_item.getLocation(), true);
		}
		else
		{
			player.sendMessage(_name + " isn't in the World.");
		}
	}
	
	public Location getWorldPosition()
	{
		if (_isActivated && (_player != null))
		{
			return _player.getLocation();
		}
		if (_isDropped && (_item != null))
		{
			return _item.getLocation();
		}
		return null;
	}
	
	// Sends map update packet to all players. Used when Adena/Reward changes.
	private void updateMapIcon()
	{
		if (org.l2jmobius.gameserver.managers.CursedWeaponsManager.getInstance() == null)
		{
			return;
		}
		// Verification; Event Mode Inactive
		if (!CursedWeaponsManager.getInstance().isEventActive())
		{
			// Sends to ALL players (without distinction)
			for (org.l2jmobius.gameserver.model.actor.Player player : org.l2jmobius.gameserver.model.World.getInstance().getPlayers())
			{
				if ((player != null) && player.isOnline())
				{
					clearSinglePlayerScreen(player);
				}
			}
			return;
		}
		// Mode: Event ACTIVE (18:00 - 23:00)
		// Builds list of active CWs in the world
		java.util.List<org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo> listToSend = new java.util.ArrayList<>();
		for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			// Only adds CWs that are:
			// Activated (in someone's hand) OR
			// Dropped (on the ground)
			if (cw.isActive() && ((cw.getPlayerId() > 0) || cw.isDropped()))
			{
				org.l2jmobius.gameserver.model.Location loc = cw.getWorldPosition();
				if (loc != null)
				{
					listToSend.add(new org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo(loc, // Location
						cw.getItemId(), cw.isActivated() ? 1 : 0, // 1 = equipped, 0 = on ground
						cw.getStageReward()));
				}
			}
		}
		// Cursed Weapons location for the world
		org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation realPacket = new org.l2jmobius.gameserver.network.serverpackets.ExCursedWeaponLocation(listToSend);
		
		for (org.l2jmobius.gameserver.model.actor.Player player : org.l2jmobius.gameserver.model.World.getInstance().getPlayers())
		{
			if ((player != null) && player.isOnline())
			{
				if (!listToSend.isEmpty())
				{
					player.sendPacket(realPacket);
				}
				else
				{
					// If the list is empty, clear icons from the player screen
					clearSinglePlayerScreen(player);
				}
			}
		}
	}
	
	public long getStageReward()
	{
		int level = getLevel();
		switch (level)
		{
			case 5:
				return 10_000_000_000L;
			case 4:
				return 7_000_000_000L;
			case 3:
				return 5_500_000_000L;
			case 2:
				return 5_200_000_000L;
			default:
				return 5_000_000_000L;
		}
	}
	
	/**
	 * Method to calculate reward
	 * @return The total Adena value to be rewarded.
	 */
	public long getCurrentReward()
	{
		long baseReward = 5_000_000_000L; // 5 Billion Base
		// 180,000 points (max) * 27,777 = ~5 Billion bonus
		long adenaPerPoint = 27_777L;
		long currentBonus = _nbKills * adenaPerPoint;
		long maxBonus = 5_000_000_000L;
		if (currentBonus > maxBonus)
		{
			currentBonus = maxBonus;
		}
		return baseReward + currentBonus;
	}
	
	public long getDuration()
	{
		return _duration;
	}
	
	// Final reward function
	public void sendEventReward()
	{
		if (_rewardSent)
		{
			return; // If already sent, aborts to avoid duplication.
		}
		if (_playerId == 0)
		{
			return;
		}
		_rewardSent = true; // Marks as reward sent
		long finalReward = 5_000_000_000L; // Base 5bi
		int level = getLevel();
		// Adds the exact bonus according to the final level
		switch (level)
		{
			case 2:
				finalReward += 200_000_000L;
				break;
			case 3:
				finalReward += 500_000_000L;
				break;
			case 4:
				finalReward += 2_000_000_000L;
				break;
			case 5:
				finalReward += 5_000_000_000L;
				break;
			default:
				break; // Level 1 = Only the base
		}
		int rewardBoxId = (_itemId == 8190) ? 80903 : 80904;
		// Unofficial text
		String mailSubject = "Cursed Weapon Event Reward";
		String mailBody = "Congratulations! You are the final possessor of " + _name + ". Here is your reward.";
		// optional message 2
		// String mailBody = "Congratulations! You are the final possessor of " + _name +
		// ". Level: " + level + " | Kills: " + _nbKills;
		Message msg = new Message(-1, _playerId, false, mailSubject, mailBody, 0);
		Mail attachments = msg.createAttachments();
		// Delivery: 'finalReward' (Fixed Table), not the map visual value.
		attachments.addItem(ItemProcessType.REWARD, 57, finalReward, null, null);
		attachments.addItem(ItemProcessType.REWARD, rewardBoxId, 1, null, null);
		MailManager.getInstance().sendMessage(msg);
		NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);
		SystemMessage sm = new SystemMessage(SystemMessageId.S1);
		sm.addString(nf.format(finalReward) + " Adena has been given to the owner of " + _name);
		broadcastToWorld(sm);
	}
	
	public void broadcastToWorld(org.l2jmobius.gameserver.network.serverpackets.ServerPacket packet)
	{
		for (org.l2jmobius.gameserver.model.actor.Player player : org.l2jmobius.gameserver.model.World.getInstance().getPlayers())
		{
			if ((player != null) && player.isOnline())
			{
				player.sendPacket(packet);
			}
		}
	}
	
	public void clearSinglePlayerScreen(org.l2jmobius.gameserver.model.actor.Player player)
	{
		org.l2jmobius.gameserver.managers.CursedWeaponsManager.getInstance().clearSinglePlayerScreen(player);
	}
}