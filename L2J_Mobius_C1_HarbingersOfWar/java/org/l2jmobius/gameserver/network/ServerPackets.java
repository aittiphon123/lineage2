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
package org.l2jmobius.gameserver.network;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.DevelopmentConfig;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public enum ServerPackets
{
	KEY_PACKET(0x00),
	CHAR_MOVE_TO_LOCATION(0x01),
	NPC_SAY(0x02),
	CHAR_INFO(0x03),
	USER_INFO(0x04),
	// 05 Dummy_05
	ATTACK(0x06),
	DIE(0x0B),
	REVIVE(0x0C),
	// 0D AttackOutofRange
	// 0E AttackinCoolTime
	// 0F AttackDeadTarget
	// 10 LeaveWorld
	// 11 AuthLoginSuccess
	// 12 AuthLoginFail
	// 13 Dummy_13
	// 14 Dummy_14
	SPAWN_ITEM(0x15),
	DROP_ITEM(0x16),
	GET_ITEM(0x17),
	// 18 EquipItem
	// 19 UnequipItem
	STATUS_UPDATE(0x1A),
	NPC_HTML_MESSAGE(0x1B),
	SELL_LIST(0x1C),
	BUY_LIST(0x1D),
	DELETE_OBJECT(0x1E),
	CHAR_SELECT_INFO(0x1F),
	LOGIN_FAIL(0x20),
	CHAR_SELECTED(0x21),
	NPC_INFO(0x22),
	CHAR_TEMPLATES(0x23), // NewCharacterSuccess
	// NewCharacterFail
	CHAR_CREATE_OK(0x25),
	CHAR_CREATE_FAIL(0x26),
	ITEM_LIST(0x27),
	SUNRISE(0x28),
	SUNSET(0x29),
	// 2A EquipItemSuccess
	// 2B EquipItemFail
	// 2C UnEquipItemSuccess
	// 2D UnEquipItemFail
	TRADE_START(0x2E),
	TRADE_START_OK(0x2F),
	TRADE_OWN_ADD(0x30),
	TRADE_OTHER_ADD(0x31),
	TRADE_DONE(0x32),
	CHAR_DELETE_OK(0x33),
	CHAR_DELETE_FAIL(0x34),
	ACTION_FAIL(0x35),
	SERVER_CLOSE(0x36),
	INVENTORY_UPDATE(0x37),
	TELEPORT_TO_LOCATION(0x38),
	TARGET_SELECTED(0x39),
	TARGET_UNSELECTED(0x3A),
	AUTO_ATTACK_START(0x3B),
	AUTO_ATTACK_STOP(0x3C),
	SOCIAL_ACTION(0x3D),
	CHANGE_MOVE_TYPE(0x3E),
	CHANGE_WAIT_TYPE(0x3F),
	// 40 NetworkFail
	// 41 Dummy_41
	// 42 Dummy_42
	MANAGE_PLEDGE_POWER(0x43),
	ASK_JOIN_PLEDGE(0x44),
	JOIN_PLEDGE(0x45),
	// 46 WithdrawalPledge
	// 47 OustPledgeMember
	// 48 SetOustPledgeMember
	// 49 DismissPledge
	// 4A SetDismissPledge
	ASK_JOIN_PARTY(0x4B),
	JOIN_PARTY(0x3C),
	// 4D WithdrawalParty
	// 4E OustPartyMember
	// 4F SetOustPartyMember
	// 50 DismissParty
	// 51 SetDismissParty
	// 52 MagicAndSkillList
	WAREHOUSE_DEPOSIT_LIST(0x53),
	WAREHOUSE_WITHDRAW_LIST(0x54),
	// WAREHOUSE_DONE(0x55),
	SHORT_CUT_REGISTER(0x56),
	SHORT_CUT_INIT(0x57),
	SHORT_CUT_DELETE(0x58),
	STOP_MOVE(0x59),
	MAGIC_SKILL_USE(0x5A),
	MAGIC_SKILL_CANCELD(0x5B),
	// 5C
	CREATURE_SAY(0x5D),
	EQUIP_UPDATE(0x5E),
	// 5F StopMoveWithLocation
	DOOR_INFO(0x60),
	DOOR_STATUS_UPDATE(0x61),
	// 62 Dummy_62
	PARTY_SMALL_WINDOW_ALL(0x63),
	PARTY_SMALL_WINDOW_ADD(0x64),
	PARTY_SMALL_WINDOW_DELETE_ALL(0x65),
	PARTY_SMALL_WINDOW_DELETE(0x66),
	PARTY_SMALL_WINDOW_UPDATE(0x67),
	PLEDGE_SHOW_MEMBER_LIST_ALL(0x68),
	PLEDGE_SHOW_MEMBER_LIST_UPDATE(0x69),
	PLEDGE_SHOW_MEMBER_LIST_ADD(0x6A),
	PLEDGE_SHOW_MEMBER_LIST_DELETE(0x6B),
	// 6C MagicList
	SKILL_LIST(0x6D),
	VEHICLE_INFO(0x6E),
	VEHICLE_DEPARTURE(0x6F),
	VEHICLE_CHECK_LOCATION(0x70),
	GET_ON_VEHICLE(0x71),
	GET_OFF_VEHICLE(0x72),
	SEND_TRADE_REQUEST(0x73),
	RESTART_RESPONSE(0x74),
	MOVE_TO_PAWN(0x75),
	VALIDATE_LOCATION(0x76),
	BEGIN_ROTATION(0x77),
	STOP_ROTATION(0x78),
	SYSTEM_MESSAGE(0x7A),
	// 7B Dummy
	// 7C Dummy
	START_PLEDGE_WAR(0x7D),
	// 7E ReplyStartPledgeWar
	STOP_PLEDGE_WAR(0x7F),
	// 80 ReplyStopPledgeWar
	SURRENDER_PLEDGE_WAR(0x81),
	// 82 ReplySurrenderPledgeWar
	// 83 SetPledgeCrest
	PLEDGE_CREST(0x84),
	SETUP_GAUGE(0x85),
	SHOW_BOARD(0x86),
	CHOOSE_INVENTORY_ITEM(0x87),
	// 88 Dummy
	MOVE_TO_LOCATION_IN_VEHICLE(0x89),
	STOP_MOVE_IN_VEHICLE(0x8A),
	VALIDATE_LOCATION_IN_VEHICLE(0x8B),
	TRADE_UPDATE(0x8C),
	TRADE_PRESS_OWN_OK(0x8D),
	MAGIC_SKILL_LAUNCHED(0x8E),
	// 8F FriendAddRequestResult
	// 90 FriendAdd
	// 91 FriendRemove
	// 92 FriendList
	// 93 FriendStatus
	TRADE_PRESS_OTHER_OK(0x94),
	ASK_JOIN_FRIEND(0x95),
	LEAVE_WORLD(0x96),
	ABNORMAL_STATUS_UPDATE(0x97),
	QUEST_LIST(0x98),
	ENCHANT_RESULT(0x99),
	// 9A AuthServerList
	PLEDGE_SHOW_MEMBER_LIST_DELETE_ALL(0x9B),
	PLEDGE_INFO(0x9C),
	RIDE(0x9F),
	// A0 GiveNickNameDone
	PLEDGE_SHOW_INFO_UPDATE(0xA1),
	// 0xA2 ClientAction,
	ACQUIRE_SKILL_LIST(0xA3),
	ACQUIRE_SKILL_INFO(0xA4),
	SERVER_OBJECT_INFO(0xA5),
	// A6 GMHide
	ACQUIRE_SKILL_DONE(0xA7),
	GM_VIEW_CHARACTER_INFO(0xA8),
	GM_VIEW_PLEDGE_INFO(0xA9),
	GM_VIEW_SKILL_INFO(0xAA),
	// AB GMViewMagicInfo
	GM_VIEW_QUEST_LIST(0xAC),
	GM_VIEW_ITEM_LIST(0xAD),
	GM_VIEW_WAREHOUSE_WITHDRAW_LIST(0xAE),
	PARTY_MATCH_LIST(0xAF),
	PARTY_MATCH_DETAIL(0xB0),
	PLAY_SOUND(0xB1),
	STATIC_OBJECT(0xB2),
	PRIVATE_STORE_MANAGE_LIST_SELL(0xB3),
	PRIVATE_STORE_LIST_SELL(0xB4),
	PRIVATE_STORE_MSG_SELL(0xB5),
	SHOW_MINI_MAP(0xB6),
	// B7 ReviveRequest
	// B8 AbnormalVisualEffect
	TUTORIAL_SHOW_HTML(0xB9),
	TUTORIAL_SHOW_QUESTION_MARK(0xBA),
	TUTORIAL_ENABLE_CLIENT_EVENT(0xBB),
	TUTORIAL_CLOSE_HTML(0xBC),
	// BD ShowRadar
	// BE DeleteRadar
	MY_TARGET_SELECTED(0xBF),
	PARTY_MEMBER_POSITION(0xC0),
	ASK_JOIN_ALLIANCE(0xC1),
	// C2 JoinAlliance
	// C3 WithdrawAlliance
	// C4 OustAllianceMemberPledge
	// C5 DismissAlliance
	// C6 SetAllianceCrest
	ALLIANCE_CREST(0xC7),
	// C8 ServerCloseSocket
	PET_STATUS_SHOW(0xC9),
	PET_INFO(0xCA),
	PET_ITEM_LIST(0xCB),
	PET_INVENTORY_UPDATE(0xCC),
	ALLIANCE_INFO(0xCD),
	PET_STATUS_UPDATE(0xCE),
	// CD AllianceInfo
	// CE PetStatusUpdate
	PET_DELETE(0xCF),
	PRIVATE_STORE_BUY_MANAGE_LIST(0xD0),
	PRIVATE_STORE_BUY_LIST(0xD1),
	PRIVATE_STORE_BUY_MSG(0xD2),
	VEHICLE_START(0xD3),
	// D4 RequestTimeCheck
	// D5 StartAllianceWar
	// D6 ReplyStartAllianceWar
	// D7 StopAllianceWar
	// D8 ReplyStopAllianceWar
	// D9 SurrenderAllianceWar
	SKILL_COOL_TIME(0xDA),
	PACKAGE_TO_LIST(0xDB),
	PACKAGE_SENDABLE_LIST(0xDC),
	EARTHQUAKE(0xDD),
	// DE FlyToLocation
	// DF BlockList
	SPECIAL_CAMERA(0xE0),
	NORMAL_CAMERA(0xE1),
	SIEGE_INFO(0xE2),
	SIEGE_ATTACKER_LIST(0xE3),
	SIEGE_DEFENDER_LIST(0xE4),
	NICK_NAME_CHANGED(0xE5),
	PLEDGE_STATUS_CHANGED(0xE6),
	RELATION_CHANGED(0xE7),
	EVENT_TRIGGER(0xE8),
	MULTI_SELL_LIST(0xE9),
	SET_SUMMON_REMAIN_TIME(0xEA),
	// EB SkillRemainSec
	NET_PING(0xEC),
	// ED Dummy ED
	// TODO: Delete bellow?
	CONFIRM_DLG(0xED);
	
	private final int _id1;
	private final int _id2;
	
	ServerPackets(int id1)
	{
		this(id1, -1);
	}
	
	ServerPackets(int id1, int id2)
	{
		_id1 = id1;
		_id2 = id2;
	}
	
	public void writeId(ServerPacket packet, WritableBuffer buffer)
	{
		if (DevelopmentConfig.DEBUG_SERVER_PACKETS)
		{
			final String name = packet.getClass().getSimpleName();
			if (!DevelopmentConfig.EXCLUDED_DEBUG_PACKETS.contains(name))
			{
				PacketLogger.info((_id2 > 0 ? "[S EX] " : "[S] ") + name);
			}
		}
		
		buffer.writeByte(_id1);
		if (_id2 > 0)
		{
			buffer.writeShort(_id2);
		}
	}
}
