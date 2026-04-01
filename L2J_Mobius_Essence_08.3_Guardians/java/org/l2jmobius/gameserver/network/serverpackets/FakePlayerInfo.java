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
import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.enums.creature.Team;
import org.l2jmobius.gameserver.model.actor.enums.player.Sex;
import org.l2jmobius.gameserver.model.actor.holders.npc.FakePlayerHolder;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class FakePlayerInfo extends ServerPacket
{
	private final Npc _npc;
	private final int _objId;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
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
	private final FakePlayerHolder _fpcHolder;
	private final Set<AbnormalVisualEffect> _abnormalVisualEffects;
	private final Set<AbnormalVisualEffect> _additionalAbnormalVisualEffects = new HashSet<>(3);
	private final Clan _clan;
	private final String _name;
	private final String _title;
	
	public FakePlayerInfo(Npc npc)
	{
		_npc = npc;
		_objId = npc.getObjectId();
		_x = npc.getX();
		_y = npc.getY();
		_z = npc.getZ();
		_heading = npc.getHeading();
		_mAtkSpd = npc.getMAtkSpd();
		_pAtkSpd = npc.getPAtkSpd();
		_attackSpeedMultiplier = (float) npc.getAttackSpeedMultiplier();
		_moveMultiplier = (float) npc.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(npc.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(npc.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(npc.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(npc.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = npc.isFlying() ? _runSpd : 0;
		_flyWalkSpd = npc.isFlying() ? _walkSpd : 0;
		_fpcHolder = npc.getTemplate().getFakePlayerInfo();
		
		_abnormalVisualEffects = npc.getEffectList().getCurrentAbnormalVisualEffects();
		
		if (npc.isInvisible() && !_abnormalVisualEffects.contains(AbnormalVisualEffect.STEALTH))
		{
			_additionalAbnormalVisualEffects.add(AbnormalVisualEffect.STEALTH);
		}
		
		final Team team = npc.getTeam();
		if (team == Team.BLUE)
		{
			if ((GeneralConfig.BLUE_TEAM_ABNORMAL_EFFECT != null) && !_abnormalVisualEffects.contains(GeneralConfig.BLUE_TEAM_ABNORMAL_EFFECT))
			{
				_additionalAbnormalVisualEffects.add(GeneralConfig.BLUE_TEAM_ABNORMAL_EFFECT);
			}
		}
		else if ((team == Team.RED) && (GeneralConfig.RED_TEAM_ABNORMAL_EFFECT != null) && !_abnormalVisualEffects.contains(GeneralConfig.RED_TEAM_ABNORMAL_EFFECT))
		{
			_additionalAbnormalVisualEffects.add(GeneralConfig.RED_TEAM_ABNORMAL_EFFECT);
		}
		
		_clan = ClanTable.getInstance().getClan(_fpcHolder.getClanId());
		_name = npc.getName();
		_title = npc.getTitle();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_CHAR_INFO.writeId(this, buffer);
		
		// CachedParameters block size. ((13 * 2) + (85 * 4) + (25 * 1) + (4 * 4) + ((_title.length() * 2) + 2) + (_player.getCubics().size() * 2) + ((_abnormalVisualEffects.size() + _additionalAbnormalVisualEffects.size()) * 2))
		buffer.writeShort(409 + (_title.length() * 2) + 0 + ((_abnormalVisualEffects.size() + _additionalAbnormalVisualEffects.size()) * 2));
		
		// CachedParameters block data.
		buffer.writeInt(_objId);
		buffer.writeShort(_npc.getRace().ordinal());
		buffer.writeByte(_npc.getTemplate().getSex() == Sex.FEMALE);
		buffer.writeInt(_fpcHolder.getPlayerClass().getRootClass().getId());
		
		// Paperdoll item display id.
		buffer.writeShort(50); // (12 * 4) + 2
		buffer.writeInt(0); // Inventory.PAPERDOLL_UNDER
		buffer.writeInt(_fpcHolder.getEquipHead());
		buffer.writeInt(_fpcHolder.getEquipRHand());
		buffer.writeInt(_fpcHolder.getEquipLHand());
		buffer.writeInt(_fpcHolder.getEquipGloves());
		buffer.writeInt(_fpcHolder.getEquipChest());
		buffer.writeInt(_fpcHolder.getEquipLegs());
		buffer.writeInt(_fpcHolder.getEquipFeet());
		buffer.writeInt(_fpcHolder.getEquipCloak());
		buffer.writeInt(_fpcHolder.getEquipRHand()); // dual hand
		buffer.writeInt(_fpcHolder.getEquipHair());
		buffer.writeInt(_fpcHolder.getEquipHair2());
		
		// Paperdoll item augmentation.
		for (int i = 0; i < 6; i++)
		{
			buffer.writeShort(14); // (3 * 4) + 2
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
		
		buffer.writeByte(_fpcHolder.getArmorEnchantLevel());
		
		// Paperdoll item visual id.
		buffer.writeShort(38); // (9 * 4) + 2
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		
		buffer.writeByte(_npc.getScriptValue()); // getPvpFlag()
		buffer.writeInt(_npc.getReputation());
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
		buffer.writeFloat(_npc.getCollisionRadius());
		buffer.writeFloat(_npc.getCollisionHeight());
		buffer.writeInt(_fpcHolder.getHair()); // nFace ?
		buffer.writeInt(_fpcHolder.getHairColor()); // nHairShape ?
		buffer.writeInt(_fpcHolder.getFace()); // nHairColor ?
		buffer.writeSizedString(_title);
		
		if (_clan != null)
		{
			buffer.writeInt(_clan.getId());
			buffer.writeInt(_clan.getCrestId());
			buffer.writeInt(_clan.getAllyId());
			buffer.writeInt(_clan.getAllyCrestId());
		}
		else
		{
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
		
		buffer.writeByte(!_fpcHolder.isSitting());
		buffer.writeByte(_npc.isRunning());
		buffer.writeByte(_npc.isInCombat());
		buffer.writeByte(0); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		buffer.writeByte(_fpcHolder.getPrivateStoreType());
		
		buffer.writeInt(0); // getCubics().size()
		// getCubics().keySet().forEach(packet::writeShort);
		
		buffer.writeByte(0); // isInMatchingRoom
		buffer.writeByte(_npc.isInsideZone(ZoneId.WATER));
		buffer.writeShort(_fpcHolder.getRecommends());
		buffer.writeInt(0); // getMountNpcId() == 0 ? 0 : getMountNpcId() + 1000000
		buffer.writeInt(_fpcHolder.getPlayerClass().getId());
		buffer.writeInt(0); // nFootEffect
		buffer.writeByte(_fpcHolder.getWeaponEnchantLevel()); // isMounted() ? 0 : _enchantLevel
		buffer.writeByte(0); // cBackEnchant
		buffer.writeByte(0); // cHairEnchant
		buffer.writeByte(0); // cHair2Enchant
		buffer.writeByte(_npc.getTeam().getId());
		buffer.writeInt(_clan != null ? _clan.getCrestLargeId() : 0);
		buffer.writeByte(_fpcHolder.getNobleLevel());
		buffer.writeByte(_fpcHolder.isHero() ? 2 : 0); // 152 - Value for enabled changed to 2
		
		buffer.writeByte(_fpcHolder.isFishing());
		buffer.writeInt(_fpcHolder.getBaitLocationX());
		buffer.writeInt(_fpcHolder.getBaitLocationY());
		buffer.writeInt(_fpcHolder.getBaitLocationZ());
		
		buffer.writeInt(_fpcHolder.getNameColor());
		buffer.writeInt(_heading);
		buffer.writeByte(_fpcHolder.getPledgeStatus());
		buffer.writeShort(0); // getPledgeType()
		buffer.writeInt(_fpcHolder.getTitleColor());
		buffer.writeByte(0); // isCursedWeaponEquipped
		buffer.writeInt(0); // getAppearance().getVisibleClanId() > 0 ? getClan().getReputationScore() : 0
		buffer.writeInt(0); // getTransformationDisplayId()
		buffer.writeInt(_fpcHolder.getAgathionId());
		buffer.writeByte(0); // nPvPRestrainStatus
		buffer.writeInt(0); // getCurrentCp()
		buffer.writeInt((int) Math.round(_npc.getCurrentHp()));
		buffer.writeInt((int) _npc.getMaxHp());
		buffer.writeInt((int) Math.round(_npc.getCurrentMp()));
		buffer.writeInt(_npc.getMaxMp());
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
		
		buffer.writeByte(0); // isTrueHero() ? 100 : 0
		buffer.writeByte((_fpcHolder.getHair() > 0) || (_fpcHolder.getEquipHair2() > 0));
		buffer.writeByte(0); // Used Ability Points
		buffer.writeInt(0); // nCursedWeaponClassId
		buffer.writeInt(0); // AFK animation.
		buffer.writeInt(0); // Rank.
		buffer.writeShort(0); // _player.getFame()
		buffer.writeInt(_fpcHolder.getPlayerClass().getId());
		buffer.writeInt(_fpcHolder.getHairColor() + 1); // 338 - DK color.
		buffer.writeInt(ServerConfig.SERVER_ID);
		
		// RealtimeParameters block size. 2 + (6 * 4) + (4 * 1) + ((_appearance.getVisibleName().length() * 2) + 2)
		buffer.writeShort(32 + (_name.length() * 2));
		
		// RealtimeParameters block data.
		buffer.writeByte(0); // cCreateOrUpdate
		buffer.writeByte(0); // cShowSpawnEvent
		buffer.writeInt(_x);
		buffer.writeInt(_y);
		buffer.writeInt(_z);
		buffer.writeInt(0); // vehicleId
		buffer.writeSizedString(_name);
		buffer.writeByte(_npc.isAlikeDead()); // !_player.isInOlympiadMode() && _player.isAlikeDead()
		buffer.writeByte(_fpcHolder.getPlayerClass().level() + 1); // 362 - Vanguard mount.
		buffer.writeInt(0); // nlastDeadStatus
		buffer.writeInt(0); // nEnemyKillCount
	}
}
