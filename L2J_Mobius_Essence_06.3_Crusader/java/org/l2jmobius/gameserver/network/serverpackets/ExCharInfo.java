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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.GeneralConfig;
import org.l2jmobius.gameserver.config.ServerConfig;
import org.l2jmobius.gameserver.managers.CastleManager;
import org.l2jmobius.gameserver.managers.CursedWeaponsManager;
import org.l2jmobius.gameserver.managers.RankManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2jmobius.gameserver.model.actor.enums.creature.Team;
import org.l2jmobius.gameserver.model.actor.instance.Decoy;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.interfaces.ILocational;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.options.VariationInstance;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Lonely, Mobius
 */
public class ExCharInfo extends ServerPacket
{
	private static final int[] PAPERDOLL_ITEM_SLOTS =
	{
		Inventory.PAPERDOLL_UNDER,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_CLOAK,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_HAIR,
		Inventory.PAPERDOLL_HAIR2
	};
	
	private static final int[] PAPERDOLL_VARIATION_SLOTS =
	{
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_RHAND
	};
	
	private static final int[] PAPERDOLL_VISUAL_SLOTS =
	{
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_HAIR,
		Inventory.PAPERDOLL_HAIR2,
	};
	
	private final Player _player;
	private final Clan _clan;
	private int _objId;
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	private final int _mAtkSpd;
	private final int _pAtkSpd;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final float _moveMultiplier;
	private final float _attackSpeedMultiplier;
	private int _enchantLevel = 0;
	private int _armorEnchant = 0;
	private int _vehicleId = 0;
	private final PlayerAppearance _appearance;
	private final Inventory _inventory;
	private final ILocational _baitLocation;
	private final Set<AbnormalVisualEffect> _abnormalVisualEffects;
	private final Set<AbnormalVisualEffect> _additionalAbnormalVisualEffects = new HashSet<>(3);
	private final Team _team;
	private final int _afkAnimation;
	private final int _rank;
	private final String _name;
	private final String _title;
	
	public ExCharInfo(Player player, boolean gmSeeInvis)
	{
		_player = player;
		_objId = player.getObjectId();
		_clan = player.getClan();
		
		if ((player.getVehicle() != null) && (player.getInVehiclePosition() != null))
		{
			_x = player.getInVehiclePosition().getX();
			_y = player.getInVehiclePosition().getY();
			_z = player.getInVehiclePosition().getZ();
			_vehicleId = player.getVehicle().getObjectId();
		}
		else
		{
			_x = player.getX();
			_y = player.getY();
			_z = player.getZ();
		}
		
		_heading = player.getHeading();
		_mAtkSpd = player.getMAtkSpd();
		_pAtkSpd = player.getPAtkSpd();
		_attackSpeedMultiplier = (float) player.getAttackSpeedMultiplier();
		_moveMultiplier = (float) player.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = player.isFlying() ? _runSpd : 0;
		_flyWalkSpd = player.isFlying() ? _walkSpd : 0;
		_appearance = player.getAppearance();
		_inventory = player.getInventory();
		_enchantLevel = _inventory.getWeaponEnchant();
		_armorEnchant = _inventory.getArmorSetEnchant();
		_baitLocation = player.getFishing().getBaitLocation();
		
		_name = player.isMercenary() ? player.getMercenaryName() : _appearance.getVisibleName();
		_title = gmSeeInvis ? "Invisible" : player.isMercenary() ? "" : _appearance.getVisibleTitle();
		
		_team = (GeneralConfig.BLUE_TEAM_ABNORMAL_EFFECT != null) && (GeneralConfig.RED_TEAM_ABNORMAL_EFFECT != null) ? player.getTeam() : Team.NONE;
		_abnormalVisualEffects = player.getEffectList().getCurrentAbnormalVisualEffects();
		
		if (gmSeeInvis && !_abnormalVisualEffects.contains(AbnormalVisualEffect.STEALTH))
		{
			_additionalAbnormalVisualEffects.add(AbnormalVisualEffect.STEALTH);
		}
		
		if (_team == Team.BLUE)
		{
			if ((GeneralConfig.BLUE_TEAM_ABNORMAL_EFFECT != null) && !_abnormalVisualEffects.contains(GeneralConfig.BLUE_TEAM_ABNORMAL_EFFECT))
			{
				_additionalAbnormalVisualEffects.add(GeneralConfig.BLUE_TEAM_ABNORMAL_EFFECT);
			}
		}
		else if ((_team == Team.RED) && (GeneralConfig.RED_TEAM_ABNORMAL_EFFECT != null) && !_abnormalVisualEffects.contains(GeneralConfig.RED_TEAM_ABNORMAL_EFFECT))
		{
			_additionalAbnormalVisualEffects.add(GeneralConfig.RED_TEAM_ABNORMAL_EFFECT);
		}
		
		_afkAnimation = ((_clan != null) && (CastleManager.getInstance().getCastleByOwner(_clan) != null)) ? (player.isClanLeader() ? 100 : 101) : 0;
		_rank = RankManager.getInstance().getPlayerGlobalRank(player) == 1 ? 1 : RankManager.getInstance().getPlayerRaceRank(player) == 1 ? 2 : 0;
	}
	
	public ExCharInfo(Decoy decoy, boolean gmSeeInvis)
	{
		this(decoy.asPlayer(), gmSeeInvis);
		
		_objId = decoy.getObjectId();
		_x = decoy.getX();
		_y = decoy.getY();
		_z = decoy.getZ();
		_heading = decoy.getHeading();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_CHAR_INFO.writeId(this, buffer);
		
		// CachedParameters block size. ((8 * 2) + (73 * 4) + (23 * 1) + (4 * 4) + ((_title.length() * 2) + 2) + (_player.getCubics().size() * 2) + ((_abnormalVisualEffects.size() + _additionalAbnormalVisualEffects.size()) * 2))
		buffer.writeShort(349 + (_title.length() * 2) + (_player.getCubics().size() * 2) + ((_abnormalVisualEffects.size() + _additionalAbnormalVisualEffects.size()) * 2));
		
		// CachedParameters block data.
		buffer.writeInt(_objId);
		buffer.writeShort(_player.getRace().ordinal());
		buffer.writeByte(_appearance.isFemale());
		buffer.writeInt(_player.getBaseTemplate().getPlayerClass().getRootClass().getId());
		
		// Paperdoll item display id.
		buffer.writeShort(50); // (12 * 4) + 2
		for (int slot : PAPERDOLL_ITEM_SLOTS)
		{
			buffer.writeInt(_inventory.getPaperdollItemDisplayId(slot));
		}
		
		// Paperdoll item augmentation.
		buffer.writeShort(26); // (8 * 3) + 2
		for (int slot : PAPERDOLL_VARIATION_SLOTS)
		{
			final VariationInstance variation = _inventory.getPaperdollAugmentation(slot);
			if (variation != null)
			{
				buffer.writeInt(variation.getOption1Id());
				buffer.writeInt(variation.getOption2Id());
			}
			else
			{
				buffer.writeInt(0);
				buffer.writeInt(0);
			}
		}
		
		buffer.writeByte(_armorEnchant);
		
		// Paperdoll item visual id.
		buffer.writeShort(38); // (9 * 4) + 2
		for (int slot : PAPERDOLL_VISUAL_SLOTS)
		{
			buffer.writeInt(_inventory.getPaperdollItemVisualId(slot));
		}
		
		buffer.writeByte(_player.getPvpFlag());
		buffer.writeInt(_player.getReputation());
		buffer.writeInt(_mAtkSpd);
		buffer.writeInt(_pAtkSpd);
		buffer.writeInt(_runSpd);
		buffer.writeInt(_walkSpd);
		buffer.writeInt(_swimRunSpd);
		buffer.writeInt(_swimWalkSpd);
		buffer.writeInt(_flyRunSpd);
		buffer.writeInt(_flyWalkSpd);
		buffer.writeInt(_flyRunSpd);
		buffer.writeInt(_flyWalkSpd);
		buffer.writeFloat(_moveMultiplier);
		buffer.writeFloat(_attackSpeedMultiplier);
		buffer.writeFloat(_player.getCollisionRadius());
		buffer.writeFloat(_player.getCollisionHeight());
		buffer.writeInt(_player.getVisualHair()); // nFace ?
		buffer.writeInt(_player.getVisualHairColor()); // nHairShape ?
		buffer.writeInt(_player.getVisualFace()); // nHairColor ?
		buffer.writeSizedString(_title);
		buffer.writeInt(_appearance.getVisibleClanId());
		buffer.writeInt(_appearance.getVisibleClanCrestId());
		buffer.writeInt(_appearance.getVisibleAllyId());
		buffer.writeInt(_appearance.getVisibleAllyCrestId());
		buffer.writeByte(!_player.isSitting());
		buffer.writeByte(_player.isRunning());
		buffer.writeByte(_player.isInCombat());
		buffer.writeByte(_player.isMounted() ? 0 : _player.getMountType().ordinal());
		buffer.writeByte(_player.getPrivateStoreType().getId());
		
		buffer.writeInt(_player.getCubics().size());
		_player.getCubics().keySet().forEach(buffer::writeShort);
		
		buffer.writeByte(_player.isInMatchingRoom());
		buffer.writeByte(_player.isInsideZone(ZoneId.WATER) ? 1 : _player.isFlyingMounted() ? 2 : 0);
		buffer.writeShort(_player.getRecomHave());
		buffer.writeInt(_player.getMountNpcId() == 0 ? 0 : _player.getMountNpcId() + 1000000);
		buffer.writeInt(_player.getPlayerClass().getId());
		buffer.writeInt(0); // nFootEffect
		buffer.writeByte(_player.isMounted() ? 0 : _enchantLevel);
		buffer.writeByte(0); // cBackEnchant
		buffer.writeByte(_team.getId()); // cEventMatchTeamID
		buffer.writeInt(_player.getClanCrestLargeId());
		buffer.writeByte(_player.isNoble());
		buffer.writeByte(_player.isHero() || (_player.isGM() && GeneralConfig.GM_HERO_AURA) ? 2 : 0); // 152 - Value for enabled changed to 2?
		
		buffer.writeByte(_player.isFishing());
		if (_baitLocation != null)
		{
			buffer.writeInt(_baitLocation.getX());
			buffer.writeInt(_baitLocation.getY());
			buffer.writeInt(_baitLocation.getZ());
		}
		else
		{
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
		
		buffer.writeInt(_appearance.getNameColor());
		buffer.writeInt(_heading);
		buffer.writeByte(_player.getPledgeClass());
		buffer.writeShort(_player.getPledgeType());
		buffer.writeInt(_appearance.getTitleColor());
		buffer.writeByte(_player.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_player.getCursedWeaponEquippedId()) : 0);
		buffer.writeInt(_clan != null ? _clan.getReputationScore() : 0);
		buffer.writeInt(_player.getTransformationDisplayId());
		buffer.writeInt(_player.getAgathionId());
		buffer.writeByte(0); // nPvPRestrainStatus
		buffer.writeInt((int) Math.round(_player.getCurrentCp()));
		buffer.writeInt((int) Math.round(_player.getCurrentHp()));
		buffer.writeInt(_player.getMaxHp());
		buffer.writeInt((int) Math.round(_player.getCurrentMp()));
		buffer.writeInt(_player.getMaxMp());
		buffer.writeByte(0); // cBRLectureMark (0-3)
		
		buffer.writeInt(_abnormalVisualEffects.size() + _additionalAbnormalVisualEffects.size());
		for (AbnormalVisualEffect abnormalVisualEffect : _abnormalVisualEffects)
		{
			buffer.writeShort(abnormalVisualEffect.getClientId());
		}
		for (AbnormalVisualEffect abnormalVisualEffect : _additionalAbnormalVisualEffects)
		{
			buffer.writeShort(abnormalVisualEffect.getClientId());
		}
		
		buffer.writeByte(_player.isTrueHero() ? 100 : 0);
		buffer.writeByte(_player.isHairAccessoryEnabled());
		buffer.writeByte(_player.getAbilityPointsUsed());
		buffer.writeInt(0); // nCursedWeaponClassId
		buffer.writeInt(_afkAnimation);
		buffer.writeInt(_rank);
		buffer.writeShort(0); // _player.getFame()
		buffer.writeInt(_player.getPlayerClass().getId());
		buffer.writeInt(_player.getVisualHairColor() + 1); // 338 - DK color.
		buffer.writeInt(ServerConfig.SERVER_ID);
		
		// RealtimeParameters block size. 2 + (4 * 4) + (4 * 1) + ((_appearance.getVisibleName().length() * 2) + 2)
		buffer.writeShort(24 + (_name.length() * 2));
		
		// RealtimeParameters block data.
		buffer.writeByte(0); // cCreateOrUpdate
		buffer.writeByte(0); // cShowSpawnEvent
		buffer.writeInt(_x);
		buffer.writeInt(_y);
		buffer.writeInt(_z);
		buffer.writeInt(_vehicleId);
		buffer.writeSizedString(_name);
		buffer.writeByte(!_player.isInOlympiadMode() && _player.isAlikeDead());
		buffer.writeByte(_player.getPlayerClass().level() + 1); // 362 - Vanguard mount.
	}
}
