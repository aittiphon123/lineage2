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

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.gameserver.config.custom.MultilingualSupportConfig;
import org.l2jmobius.gameserver.model.clientstrings.Builder;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

public class SystemMessageId
{
	private static final Logger LOGGER = Logger.getLogger(SystemMessageId.class.getName());
	
	private static final SMLocalisation[] EMPTY_SML_ARRAY = new SMLocalisation[0];
	private static final Map<Integer, SystemMessageId> VALUES = new HashMap<>();
	
	@ClientString(id = 0, message = "Your connection has been disconnected from the server.")
	public static SystemMessageId YOUR_CONNECTION_HAS_BEEN_DISCONNECTED_FROM_THE_SERVER;
	
	@ClientString(id = 1, message = "The server will be disconnected in $s1 seconds. Please quit the game.")
	public static SystemMessageId THE_SERVER_WILL_BE_DISCONNECTED_IN_S1_SECONDS_PLEASE_QUIT_THE_GAME;
	
	@ClientString(id = 2, message = "$s1 does not exist.")
	public static SystemMessageId S1_DOES_NOT_EXIST;
	
	@ClientString(id = 3, message = "$s1 is not logged in.")
	public static SystemMessageId S1_IS_NOT_LOGGED_IN;
	
	@ClientString(id = 4, message = "You cannot ask yourself to apply to a clan.")
	public static SystemMessageId YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN;
	
	@ClientString(id = 5, message = "$s1 already exists.")
	public static SystemMessageId S1_ALREADY_EXISTS;
	
	@ClientString(id = 6, message = "$s1 does not exist.")
	public static SystemMessageId S1_DOES_NOT_EXIST_2;
	
	@ClientString(id = 7, message = "You already belong to $s1.")
	public static SystemMessageId YOU_ALREADY_BELONG_TO_S1;
	
	@ClientString(id = 8, message = "You are working with another clan.")
	public static SystemMessageId YOU_ARE_WORKING_WITH_ANOTHER_CLAN;
	
	@ClientString(id = 9, message = "$s1 is not a clan leader.")
	public static SystemMessageId S1_IS_NOT_A_CLAN_LEADER;
	
	@ClientString(id = 10, message = "$s1 is working with another clan.")
	public static SystemMessageId S1_IS_WORKING_WITH_ANOTHER_CLAN;
	
	@ClientString(id = 11, message = "There are no applicants for this clan.")
	public static SystemMessageId THERE_ARE_NO_APPLICANTS_FOR_THIS_CLAN;
	
	@ClientString(id = 12, message = "Applicant information is incorrect.")
	public static SystemMessageId APPLICANT_INFORMATION_IS_INCORRECT;
	
	@ClientString(id = 13, message = "Unable to disperse: your clan has requested to participate in a castle siege.")
	public static SystemMessageId UNABLE_TO_DISPERSE_YOUR_CLAN_HAS_REQUESTED_TO_PARTICIPATE_IN_A_CASTLE_SIEGE;
	
	@ClientString(id = 14, message = "Unable to disperse: your clan owns one or more castles or hideouts.")
	public static SystemMessageId UNABLE_TO_DISPERSE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS;
	
	@ClientString(id = 15, message = "You are in siege.")
	public static SystemMessageId YOU_ARE_IN_SIEGE;
	
	@ClientString(id = 16, message = "You are not in siege.")
	public static SystemMessageId YOU_ARE_NOT_IN_SIEGE;
	
	@ClientString(id = 17, message = "Castle siege has begun.")
	public static SystemMessageId CASTLE_SIEGE_HAS_BEGUN;
	
	@ClientString(id = 18, message = "Castle siege is over.")
	public static SystemMessageId CASTLE_SIEGE_IS_OVER;
	
	@ClientString(id = 19, message = "The castellan has been changed!")
	public static SystemMessageId THE_CASTELLAN_HAS_BEEN_CHANGED;
	
	@ClientString(id = 20, message = "The gate is being opened.")
	public static SystemMessageId THE_GATE_IS_BEING_OPENED;
	
	@ClientString(id = 21, message = "The gate is being destroyed.")
	public static SystemMessageId THE_GATE_IS_BEING_DESTROYED;
	
	@ClientString(id = 22, message = "Target is too far.")
	public static SystemMessageId TARGET_IS_TOO_FAR;
	
	@ClientString(id = 23, message = "Not enough HP.")
	public static SystemMessageId NOT_ENOUGH_HP;
	
	@ClientString(id = 24, message = "Not enough MP.")
	public static SystemMessageId NOT_ENOUGH_MP;
	
	@ClientString(id = 25, message = "Rejuvenating HP.")
	public static SystemMessageId REJUVENATING_HP;
	
	@ClientString(id = 26, message = "Rejuvenating MP.")
	public static SystemMessageId REJUVENATING_MP;
	
	@ClientString(id = 27, message = "Casting has been interrupted.")
	public static SystemMessageId CASTING_HAS_BEEN_INTERRUPTED;
	
	@ClientString(id = 28, message = "You picked up $s1 adena.")
	public static SystemMessageId YOU_PICKED_UP_S1_ADENA;
	
	@ClientString(id = 29, message = "You picked up $s2 $s1.")
	public static SystemMessageId YOU_PICKED_UP_S2_S1;
	
	@ClientString(id = 30, message = "You picked up $s1.")
	public static SystemMessageId YOU_PICKED_UP_S1;
	
	@ClientString(id = 31, message = "You cannot move while sitting.")
	public static SystemMessageId YOU_CANNOT_MOVE_WHILE_SITTING;
	
	@ClientString(id = 32, message = "You are not capable of combat. Move to the nearest restart point.")
	public static SystemMessageId YOU_ARE_NOT_CAPABLE_OF_COMBAT_MOVE_TO_THE_NEAREST_RESTART_POINT;
	
	@ClientString(id = 33, message = "You cannot move when using magic.")
	public static SystemMessageId YOU_CANNOT_MOVE_WHEN_USING_MAGIC;
	
	@ClientString(id = 34, message = "Welcome to the World of Lineage II.")
	public static SystemMessageId WELCOME_TO_THE_WORLD_OF_LINEAGE_II;
	
	@ClientString(id = 35, message = "You did $s1 damage.")
	public static SystemMessageId YOU_DID_S1_DAMAGE;
	
	@ClientString(id = 36, message = "$s1 gave you $s2 damage.")
	public static SystemMessageId S1_GAVE_YOU_S2_DAMAGE;
	
	@ClientString(id = 37, message = "$s1 gave you $s2 damage.")
	public static SystemMessageId S1_GAVE_YOU_S2_DAMAGE_2;
	
	@ClientString(id = 38, message = "The TGS2002 event begins!")
	public static SystemMessageId THE_TGS2002_EVENT_BEGINS;
	
	@ClientString(id = 39, message = "The TGS2002 event is over. Thank you very much.")
	public static SystemMessageId THE_TGS2002_EVENT_IS_OVER_THANK_YOU_VERY_MUCH;
	
	@ClientString(id = 40, message = "This is the TGS demo: the character will immediately be restored.")
	public static SystemMessageId THIS_IS_THE_TGS_DEMO_THE_CHARACTER_WILL_IMMEDIATELY_BE_RESTORED;
	
	@ClientString(id = 41, message = "Getting ready to shoot arrows.")
	public static SystemMessageId GETTING_READY_TO_SHOOT_ARROWS;
	
	@ClientString(id = 42, message = "Avoided $s1's attack.")
	public static SystemMessageId AVOIDED_S1_S_ATTACK;
	
	@ClientString(id = 43, message = "Missed target.")
	public static SystemMessageId MISSED_TARGET;
	
	@ClientString(id = 44, message = "Critical hit!")
	public static SystemMessageId CRITICAL_HIT;
	
	@ClientString(id = 45, message = "You have earned $s1 experience.")
	public static SystemMessageId YOU_HAVE_EARNED_S1_EXPERIENCE;
	
	@ClientString(id = 46, message = "Use $s1.")
	public static SystemMessageId USE_S1;
	
	@ClientString(id = 47, message = "Using $s1.")
	public static SystemMessageId USING_S1;
	
	@ClientString(id = 48, message = "$s1 is not available at this time: being prepared for reuse.")
	public static SystemMessageId S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE;
	
	@ClientString(id = 49, message = "You are equipped with $s1.")
	public static SystemMessageId YOU_ARE_EQUIPPED_WITH_S1;
	
	@ClientString(id = 50, message = "Target can not be found.")
	public static SystemMessageId TARGET_CAN_NOT_BE_FOUND;
	
	@ClientString(id = 51, message = "You cannot use this on yourself.")
	public static SystemMessageId YOU_CANNOT_USE_THIS_ON_YOURSELF;
	
	@ClientString(id = 52, message = "Earned $s1 adena.")
	public static SystemMessageId EARNED_S1_ADENA;
	
	@ClientString(id = 53, message = "Earned $s2 $s1(s).")
	public static SystemMessageId EARNED_S2_S1_S;
	
	@ClientString(id = 54, message = "Earned $s1.")
	public static SystemMessageId EARNED_S1;
	
	@ClientString(id = 55, message = "Failed to pick up $s1 adena.")
	public static SystemMessageId FAILED_TO_PICK_UP_S1_ADENA;
	
	@ClientString(id = 56, message = "Failed to pick up $s1.")
	public static SystemMessageId FAILED_TO_PICK_UP_S1;
	
	@ClientString(id = 57, message = "Failed to pick up $s2 $s1(s).")
	public static SystemMessageId FAILED_TO_PICK_UP_S2_S1_S;
	
	@ClientString(id = 58, message = "Failed to earn $s1 adena.")
	public static SystemMessageId FAILED_TO_EARN_S1_ADENA;
	
	@ClientString(id = 59, message = "Failed to earn $s1.")
	public static SystemMessageId FAILED_TO_EARN_S1;
	
	@ClientString(id = 60, message = "Failed to earn $s2 $s1(s).")
	public static SystemMessageId FAILED_TO_EARN_S2_S1_S;
	
	@ClientString(id = 61, message = "Nothing happened.")
	public static SystemMessageId NOTHING_HAPPENED;
	
	@ClientString(id = 62, message = "$s1 has been successfully enchanted.")
	public static SystemMessageId S1_HAS_BEEN_SUCCESSFULLY_ENCHANTED;
	
	@ClientString(id = 63, message = "+$s1$s2 has been successfully enchanted.")
	public static SystemMessageId S1_S2_HAS_BEEN_SUCCESSFULLY_ENCHANTED;
	
	@ClientString(id = 64, message = "Enchantment has failed. $s1 has been evaporated.")
	public static SystemMessageId ENCHANTMENT_HAS_FAILED_S1_HAS_BEEN_EVAPORATED;
	
	@ClientString(id = 65, message = "Enchantment has failed.+$s1$s2 has been evaporated.")
	public static SystemMessageId ENCHANTMENT_HAS_FAILED_S1_S2_HAS_BEEN_EVAPORATED;
	
	@ClientString(id = 66, message = "$s1 has invited you to his/her party. Do you accept the invitation?")
	public static SystemMessageId S1_HAS_INVITED_YOU_TO_HIS_HER_PARTY_DO_YOU_ACCEPT_THE_INVITATION;
	
	@ClientString(id = 67, message = "$s1 has invited you to the $s2 Clan. Do you want to join?")
	public static SystemMessageId S1_HAS_INVITED_YOU_TO_THE_S2_CLAN_DO_YOU_WANT_TO_JOIN;
	
	@ClientString(id = 68, message = "Withdraw from the $s1 Clan. Do you want to continue?")
	public static SystemMessageId WITHDRAW_FROM_THE_S1_CLAN_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 69, message = "Expel $s1 from the clan. Do you want to continue?")
	public static SystemMessageId EXPEL_S1_FROM_THE_CLAN_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 70, message = "Disperse the $s1 Clan. Do you want to continue?")
	public static SystemMessageId DISPERSE_THE_S1_CLAN_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 71, message = "How many $s1(s) do you want to discard?")
	public static SystemMessageId HOW_MANY_S1_S_DO_YOU_WANT_TO_DISCARD;
	
	@ClientString(id = 72, message = "How many $s1(s) do you want to move?")
	public static SystemMessageId HOW_MANY_S1_S_DO_YOU_WANT_TO_MOVE;
	
	@ClientString(id = 73, message = "How many $s1(s) do you want to destroy?")
	public static SystemMessageId HOW_MANY_S1_S_DO_YOU_WANT_TO_DESTROY;
	
	@ClientString(id = 74, message = "Destroy $s1. Do you want to continue?")
	public static SystemMessageId DESTROY_S1_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 75, message = "ID does not exist.")
	public static SystemMessageId ID_DOES_NOT_EXIST;
	
	@ClientString(id = 76, message = "Incorrect password.")
	public static SystemMessageId INCORRECT_PASSWORD;
	
	@ClientString(id = 77, message = "You cannot create another character. Please delete the existing character and try again.")
	public static SystemMessageId YOU_CANNOT_CREATE_ANOTHER_CHARACTER_PLEASE_DELETE_THE_EXISTING_CHARACTER_AND_TRY_AGAIN;
	
	@ClientString(id = 78, message = "Do you want to delete $s1?")
	public static SystemMessageId DO_YOU_WANT_TO_DELETE_S1;
	
	@ClientString(id = 79, message = "Name already exists.")
	public static SystemMessageId NAME_ALREADY_EXISTS;
	
	@ClientString(id = 80, message = "Please insert 1~16 English characters.")
	public static SystemMessageId PLEASE_INSERT_1_16_ENGLISH_CHARACTERS;
	
	@ClientString(id = 81, message = "Please select your race.")
	public static SystemMessageId PLEASE_SELECT_YOUR_RACE;
	
	@ClientString(id = 82, message = "Please select your occupation.")
	public static SystemMessageId PLEASE_SELECT_YOUR_OCCUPATION;
	
	@ClientString(id = 83, message = "Please select your gender.")
	public static SystemMessageId PLEASE_SELECT_YOUR_GENDER;
	
	@ClientString(id = 84, message = "You cannot attack in the peace zone.")
	public static SystemMessageId YOU_CANNOT_ATTACK_IN_THE_PEACE_ZONE;
	
	@ClientString(id = 85, message = "You cannot attack the target in the peace zone.")
	public static SystemMessageId YOU_CANNOT_ATTACK_THE_TARGET_IN_THE_PEACE_ZONE;
	
	@ClientString(id = 86, message = "Please insert your ID.")
	public static SystemMessageId PLEASE_INSERT_YOUR_ID;
	
	@ClientString(id = 87, message = "Please insert your password.")
	public static SystemMessageId PLEASE_INSERT_YOUR_PASSWORD;
	
	@ClientString(id = 88, message = "Protocol version is different. Please quit the program.")
	public static SystemMessageId PROTOCOL_VERSION_IS_DIFFERENT_PLEASE_QUIT_THE_PROGRAM;
	
	@ClientString(id = 89, message = "Protocol version is different. Please continue.")
	public static SystemMessageId PROTOCOL_VERSION_IS_DIFFERENT_PLEASE_CONTINUE;
	
	@ClientString(id = 90, message = "Unable to connect to server.")
	public static SystemMessageId UNABLE_TO_CONNECT_TO_SERVER;
	
	@ClientString(id = 91, message = "Please select your hairstyle.")
	public static SystemMessageId PLEASE_SELECT_YOUR_HAIRSTYLE;
	
	@ClientString(id = 92, message = "The effect of $s1 has disappeared.")
	public static SystemMessageId THE_EFFECT_OF_S1_HAS_DISAPPEARED;
	
	@ClientString(id = 93, message = "Not enough SP.")
	public static SystemMessageId NOT_ENOUGH_SP;
	
	@ClientString(id = 94, message = "Copyright 2002 NCsoft Corporation. All Rights Reserved.")
	public static SystemMessageId COPYRIGHT_2002_NCSOFT_CORPORATION_ALL_RIGHTS_RESERVED;
	
	@ClientString(id = 95, message = "You have earned $s1 experience and $s2 SP.")
	public static SystemMessageId YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP;
	
	@ClientString(id = 96, message = "Your have increased your level!")
	public static SystemMessageId YOUR_HAVE_INCREASED_YOUR_LEVEL;
	
	@ClientString(id = 97, message = "Quest items cannot be moved.")
	public static SystemMessageId QUEST_ITEMS_CANNOT_BE_MOVED;
	
	@ClientString(id = 98, message = "Quest items cannot be discarded or destroyed.")
	public static SystemMessageId QUEST_ITEMS_CANNOT_BE_DISCARDED_OR_DESTROYED;
	
	@ClientString(id = 99, message = "Quest items cannot be traded or sold.")
	public static SystemMessageId QUEST_ITEMS_CANNOT_BE_TRADED_OR_SOLD;
	
	@ClientString(id = 100, message = "$s1 requests a trade. Do you want to trade?")
	public static SystemMessageId S1_REQUESTS_A_TRADE_DO_YOU_WANT_TO_TRADE;
	
	@ClientString(id = 101, message = "You cannot logout while in combat.")
	public static SystemMessageId YOU_CANNOT_LOGOUT_WHILE_IN_COMBAT;
	
	@ClientString(id = 102, message = "You cannot restart while in combat.")
	public static SystemMessageId YOU_CANNOT_RESTART_WHILE_IN_COMBAT;
	
	@ClientString(id = 103, message = "ID is logged in.")
	public static SystemMessageId ID_IS_LOGGED_IN;
	
	@ClientString(id = 104, message = "You cannot use equipment when using other skills or magic.")
	public static SystemMessageId YOU_CANNOT_USE_EQUIPMENT_WHEN_USING_OTHER_SKILLS_OR_MAGIC;
	
	@ClientString(id = 105, message = "You have invited $s1 to your party.")
	public static SystemMessageId YOU_HAVE_INVITED_S1_TO_YOUR_PARTY;
	
	@ClientString(id = 106, message = "You have joined $s1's party.")
	public static SystemMessageId YOU_HAVE_JOINED_S1_S_PARTY;
	
	@ClientString(id = 107, message = "$s1 has joined the party.")
	public static SystemMessageId S1_HAS_JOINED_THE_PARTY;
	
	@ClientString(id = 108, message = "$s1 has left the party.")
	public static SystemMessageId S1_HAS_LEFT_THE_PARTY;
	
	@ClientString(id = 109, message = "Incorrect target.")
	public static SystemMessageId INCORRECT_TARGET;
	
	@ClientString(id = 110, message = "You can feel $s1's effect.")
	public static SystemMessageId YOU_CAN_FEEL_S1_S_EFFECT;
	
	@ClientString(id = 111, message = "Shield defense has succeeded.")
	public static SystemMessageId SHIELD_DEFENSE_HAS_SUCCEEDED;
	
	@ClientString(id = 112, message = "Not enough arrows.")
	public static SystemMessageId NOT_ENOUGH_ARROWS;
	
	@ClientString(id = 113, message = "$s1 cannot be used due to unsuitable terms.")
	public static SystemMessageId S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS;
	
	@ClientString(id = 114, message = "Enter the shadow of the World Tree.")
	public static SystemMessageId ENTER_THE_SHADOW_OF_THE_WORLD_TREE;
	
	@ClientString(id = 115, message = "Exit the shadow of the World Tree.")
	public static SystemMessageId EXIT_THE_SHADOW_OF_THE_WORLD_TREE;
	
	@ClientString(id = 116, message = "Entering the peace zone.")
	public static SystemMessageId ENTERING_THE_PEACE_ZONE;
	
	@ClientString(id = 117, message = "Exiting the peace zone.")
	public static SystemMessageId EXITING_THE_PEACE_ZONE;
	
	@ClientString(id = 118, message = "Request $s1 for trade.")
	public static SystemMessageId REQUEST_S1_FOR_TRADE;
	
	@ClientString(id = 119, message = "$s1 denied your request for trade.")
	public static SystemMessageId S1_DENIED_YOUR_REQUEST_FOR_TRADE;
	
	@ClientString(id = 120, message = "Begin trading with $s1.")
	public static SystemMessageId BEGIN_TRADING_WITH_S1;
	
	@ClientString(id = 121, message = "$s1 confirmed trade.")
	public static SystemMessageId S1_CONFIRMED_TRADE;
	
	@ClientString(id = 122, message = "You cannot move additional items because trade has been confirmed.")
	public static SystemMessageId YOU_CANNOT_MOVE_ADDITIONAL_ITEMS_BECAUSE_TRADE_HAS_BEEN_CONFIRMED;
	
	@ClientString(id = 123, message = "Trade has been successful.")
	public static SystemMessageId TRADE_HAS_BEEN_SUCCESSFUL;
	
	@ClientString(id = 124, message = "$s1 cancelled trade.")
	public static SystemMessageId S1_CANCELLED_TRADE;
	
	@ClientString(id = 125, message = "Quit game. Do you want to continue?")
	public static SystemMessageId QUIT_GAME_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 126, message = "Restart the game. Do you want to continue?")
	public static SystemMessageId RESTART_THE_GAME_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 127, message = "Your connection has been disconnected from the server. Please try again.")
	public static SystemMessageId YOUR_CONNECTION_HAS_BEEN_DISCONNECTED_FROM_THE_SERVER_PLEASE_TRY_AGAIN;
	
	@ClientString(id = 128, message = "You have failed to create a character.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_CREATE_A_CHARACTER;
	
	@ClientString(id = 129, message = "Inventory slot is full.")
	public static SystemMessageId INVENTORY_SLOT_IS_FULL;
	
	@ClientString(id = 130, message = "Warehouse slot is full.")
	public static SystemMessageId WAREHOUSE_SLOT_IS_FULL;
	
	@ClientString(id = 131, message = "$s1 has logged in.")
	public static SystemMessageId S1_HAS_LOGGED_IN;
	
	@ClientString(id = 132, message = "$s1 has been added to your friend list.")
	public static SystemMessageId S1_HAS_BEEN_ADDED_TO_YOUR_FRIEND_LIST;
	
	@ClientString(id = 133, message = "$s1 has been removed from your friend list.")
	public static SystemMessageId S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIEND_LIST;
	
	@ClientString(id = 134, message = "Please check your friend list again.")
	public static SystemMessageId PLEASE_CHECK_YOUR_FRIEND_LIST_AGAIN;
	
	@ClientString(id = 135, message = "$s1 did not reply to your invitation: party invitation has been cancelled.")
	public static SystemMessageId S1_DID_NOT_REPLY_TO_YOUR_INVITATION_PARTY_INVITATION_HAS_BEEN_CANCELLED;
	
	@ClientString(id = 136, message = "You did not reply to $s1's invitation: joining has been cancelled.")
	public static SystemMessageId YOU_DID_NOT_REPLY_TO_S1_S_INVITATION_JOINING_HAS_BEEN_CANCELLED;
	
	@ClientString(id = 137, message = "There are no more items in the shortcut.")
	public static SystemMessageId THERE_ARE_NO_MORE_ITEMS_IN_THE_SHORTCUT;
	
	@ClientString(id = 138, message = "Designate shortcut.")
	public static SystemMessageId DESIGNATE_SHORTCUT;
	
	@ClientString(id = 139, message = "$s1 was unaffected by $s2.")
	public static SystemMessageId S1_WAS_UNAFFECTED_BY_S2;
	
	@ClientString(id = 140, message = "Skill was removed due to lack of MP.")
	public static SystemMessageId SKILL_WAS_REMOVED_DUE_TO_LACK_OF_MP;
	
	@ClientString(id = 141, message = "If trade is confirmed, the item cannot be moved again.")
	public static SystemMessageId IF_TRADE_IS_CONFIRMED_THE_ITEM_CANNOT_BE_MOVED_AGAIN;
	
	@ClientString(id = 142, message = "Already trading.")
	public static SystemMessageId ALREADY_TRADING;
	
	@ClientString(id = 143, message = "$s1 is trading with another person.")
	public static SystemMessageId S1_IS_TRADING_WITH_ANOTHER_PERSON;
	
	@ClientString(id = 144, message = "Target is incorrect.")
	public static SystemMessageId TARGET_IS_INCORRECT;
	
	@ClientString(id = 145, message = "Target is not found in the game.")
	public static SystemMessageId TARGET_IS_NOT_FOUND_IN_THE_GAME;
	
	@ClientString(id = 146, message = "Chatting is permitted.")
	public static SystemMessageId CHATTING_IS_PERMITTED;
	
	@ClientString(id = 147, message = "Chatting is prohibited.")
	public static SystemMessageId CHATTING_IS_PROHIBITED;
	
	@ClientString(id = 148, message = "You cannot use quest items.")
	public static SystemMessageId YOU_CANNOT_USE_QUEST_ITEMS;
	
	@ClientString(id = 149, message = "You cannot use items while trading.")
	public static SystemMessageId YOU_CANNOT_USE_ITEMS_WHILE_TRADING;
	
	@ClientString(id = 150, message = "You cannot discard or destroy items while trading.")
	public static SystemMessageId YOU_CANNOT_DISCARD_OR_DESTROY_ITEMS_WHILE_TRADING;
	
	@ClientString(id = 151, message = "Too far to discard.")
	public static SystemMessageId TOO_FAR_TO_DISCARD;
	
	@ClientString(id = 152, message = "Wrong target has been invited.")
	public static SystemMessageId WRONG_TARGET_HAS_BEEN_INVITED;
	
	@ClientString(id = 153, message = "$s1 is busy. Please try again later.")
	public static SystemMessageId S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER;
	
	@ClientString(id = 154, message = "Only the leader can give out invitations.")
	public static SystemMessageId ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS;
	
	@ClientString(id = 155, message = "Party is full.")
	public static SystemMessageId PARTY_IS_FULL;
	
	@ClientString(id = 156, message = "Drain was only half successful.")
	public static SystemMessageId DRAIN_WAS_ONLY_HALF_SUCCESSFUL;
	
	@ClientString(id = 157, message = "You resisted $s1's drain.")
	public static SystemMessageId YOU_RESISTED_S1_S_DRAIN;
	
	@ClientString(id = 158, message = "Attack failed.")
	public static SystemMessageId ATTACK_FAILED;
	
	@ClientString(id = 159, message = "Resisted against $s1's magic.")
	public static SystemMessageId RESISTED_AGAINST_S1_S_MAGIC;
	
	@ClientString(id = 160, message = "$s1 is a member of another party and cannot be invited.")
	public static SystemMessageId S1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED;
	
	@ClientString(id = 161, message = "The user you invited is not online.")
	public static SystemMessageId THE_USER_YOU_INVITED_IS_NOT_ONLINE;
	
	@ClientString(id = 162, message = "Warehouse is too far.")
	public static SystemMessageId WAREHOUSE_IS_TOO_FAR;
	
	@ClientString(id = 163, message = "You cannot destroy it because the number is incorrect.")
	public static SystemMessageId YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT;
	
	@ClientString(id = 164, message = "Waiting for another reply.")
	public static SystemMessageId WAITING_FOR_ANOTHER_REPLY;
	
	@ClientString(id = 165, message = "You cannot add yourself to your own friend list.")
	public static SystemMessageId YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST;
	
	@ClientString(id = 166, message = "Friend list is not ready yet. Please register again later.")
	public static SystemMessageId FRIEND_LIST_IS_NOT_READY_YET_PLEASE_REGISTER_AGAIN_LATER;
	
	@ClientString(id = 167, message = "$s1 is already on your friend list.")
	public static SystemMessageId S1_IS_ALREADY_ON_YOUR_FRIEND_LIST;
	
	@ClientString(id = 168, message = "$s1 has requested to become friends.")
	public static SystemMessageId S1_HAS_REQUESTED_TO_BECOME_FRIENDS;
	
	@ClientString(id = 169, message = "Accept friendship 0/1 (1 to accept, 0 to deny)")
	public static SystemMessageId ACCEPT_FRIENDSHIP_0_1_1_TO_ACCEPT_0_TO_DENY;
	
	@ClientString(id = 170, message = "The user who requested to become friends is not found in the game.")
	public static SystemMessageId THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME;
	
	@ClientString(id = 171, message = "$s1 is not on your friend list.")
	public static SystemMessageId S1_IS_NOT_ON_YOUR_FRIEND_LIST;
	
	@ClientString(id = 172, message = "You have no money to pay for custody.")
	public static SystemMessageId YOU_HAVE_NO_MONEY_TO_PAY_FOR_CUSTODY;
	
	@ClientString(id = 173, message = "You don't have enough money to pay for custody.")
	public static SystemMessageId YOU_DON_T_HAVE_ENOUGH_MONEY_TO_PAY_FOR_CUSTODY;
	
	@ClientString(id = 174, message = "The person's inventory is full.")
	public static SystemMessageId THE_PERSON_S_INVENTORY_IS_FULL;
	
	@ClientString(id = 175, message = "HP was fully recovered, and skill was removed.")
	public static SystemMessageId HP_WAS_FULLY_RECOVERED_AND_SKILL_WAS_REMOVED;
	
	@ClientString(id = 176, message = "The person is in a message refusal mode.")
	public static SystemMessageId THE_PERSON_IS_IN_A_MESSAGE_REFUSAL_MODE;
	
	@ClientString(id = 177, message = "Message refusal mode.")
	public static SystemMessageId MESSAGE_REFUSAL_MODE;
	
	@ClientString(id = 178, message = "Message acceptance mode.")
	public static SystemMessageId MESSAGE_ACCEPTANCE_MODE;
	
	@ClientString(id = 179, message = "You cannot discard items here.")
	public static SystemMessageId YOU_CANNOT_DISCARD_ITEMS_HERE;
	
	@ClientString(id = 180, message = "You have $s1 day(s) left until deletion. Do you want to cancel deletion?")
	public static SystemMessageId YOU_HAVE_S1_DAY_S_LEFT_UNTIL_DELETION_DO_YOU_WANT_TO_CANCEL_DELETION;
	
	@ClientString(id = 181, message = "Cannot see target.")
	public static SystemMessageId CANNOT_SEE_TARGET;
	
	@ClientString(id = 182, message = "Do you want to quit the current quest?")
	public static SystemMessageId DO_YOU_WANT_TO_QUIT_THE_CURRENT_QUEST;
	
	@ClientString(id = 183, message = "There are too many users on the server. Please try again later.")
	public static SystemMessageId THERE_ARE_TOO_MANY_USERS_ON_THE_SERVER_PLEASE_TRY_AGAIN_LATER;
	
	@ClientString(id = 184, message = "Please try again later.")
	public static SystemMessageId PLEASE_TRY_AGAIN_LATER;
	
	@ClientString(id = 185, message = "Select user to invite to your party.")
	public static SystemMessageId SELECT_USER_TO_INVITE_TO_YOUR_PARTY;
	
	@ClientString(id = 186, message = "Select user to invite to your clan.")
	public static SystemMessageId SELECT_USER_TO_INVITE_TO_YOUR_CLAN;
	
	@ClientString(id = 187, message = "Select user to expel.")
	public static SystemMessageId SELECT_USER_TO_EXPEL;
	
	@ClientString(id = 188, message = "Create clan name.")
	public static SystemMessageId CREATE_CLAN_NAME;
	
	@ClientString(id = 189, message = "Clan has been created.")
	public static SystemMessageId CLAN_HAS_BEEN_CREATED;
	
	@ClientString(id = 190, message = "You have failed to create a clan.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_CREATE_A_CLAN;
	
	@ClientString(id = 191, message = "Clan member $s1 has been expelled.")
	public static SystemMessageId CLAN_MEMBER_S1_HAS_BEEN_EXPELLED;
	
	@ClientString(id = 192, message = "You have failed to expel $s1 from the clan.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_EXPEL_S1_FROM_THE_CLAN;
	
	@ClientString(id = 193, message = "Clan has dispersed.")
	public static SystemMessageId CLAN_HAS_DISPERSED;
	
	@ClientString(id = 194, message = "You have failed to disperse the Clan.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_DISPERSE_THE_CLAN;
	
	@ClientString(id = 195, message = "Entered the clan.")
	public static SystemMessageId ENTERED_THE_CLAN;
	
	@ClientString(id = 196, message = "$s1 refused to join the clan.")
	public static SystemMessageId S1_REFUSED_TO_JOIN_THE_CLAN;
	
	@ClientString(id = 197, message = "Withdrawn from the clan.")
	public static SystemMessageId WITHDRAWN_FROM_THE_CLAN;
	
	@ClientString(id = 198, message = "You have failed to withdraw from the $s1 Clan.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_S1_CLAN;
	
	@ClientString(id = 199, message = "Membership to this clan has been terminated.")
	public static SystemMessageId MEMBERSHIP_TO_THIS_CLAN_HAS_BEEN_TERMINATED;
	
	@ClientString(id = 200, message = "You have withdrawn from the party.")
	public static SystemMessageId YOU_HAVE_WITHDRAWN_FROM_THE_PARTY;
	
	@ClientString(id = 201, message = "$s1 was expelled from the party.")
	public static SystemMessageId S1_WAS_EXPELLED_FROM_THE_PARTY;
	
	@ClientString(id = 202, message = "You have been expelled from the party.")
	public static SystemMessageId YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY;
	
	@ClientString(id = 203, message = "The party has dispersed.")
	public static SystemMessageId THE_PARTY_HAS_DISPERSED;
	
	@ClientString(id = 204, message = "Incorrect name. Please try again.")
	public static SystemMessageId INCORRECT_NAME_PLEASE_TRY_AGAIN;
	
	@ClientString(id = 205, message = "Incorrect character name. Please ask the GM.")
	public static SystemMessageId INCORRECT_CHARACTER_NAME_PLEASE_ASK_THE_GM;
	
	@ClientString(id = 206, message = "Enter name of clan to declare war on.")
	public static SystemMessageId ENTER_NAME_OF_CLAN_TO_DECLARE_WAR_ON;
	
	@ClientString(id = 207, message = "$s2 of the $s1 Clan requests declaration of war. Do you accept?")
	public static SystemMessageId S2_OF_THE_S1_CLAN_REQUESTS_DECLARATION_OF_WAR_DO_YOU_ACCEPT;
	
	@ClientString(id = 208, message = "Please include file type when entering file path.")
	public static SystemMessageId PLEASE_INCLUDE_FILE_TYPE_WHEN_ENTERING_FILE_PATH;
	
	@ClientString(id = 209, message = "The size of the image file is different. Please adjust to 16*12.")
	public static SystemMessageId THE_SIZE_OF_THE_IMAGE_FILE_IS_DIFFERENT_PLEASE_ADJUST_TO_16_12;
	
	@ClientString(id = 210, message = "Cannot find file. Please enter precise path.")
	public static SystemMessageId CANNOT_FIND_FILE_PLEASE_ENTER_PRECISE_PATH;
	
	@ClientString(id = 211, message = "Can only register 16*12 sized bmp files of 256 colors.")
	public static SystemMessageId CAN_ONLY_REGISTER_16_12_SIZED_BMP_FILES_OF_256_COLORS;
	
	@ClientString(id = 212, message = "You are not a clan member.")
	public static SystemMessageId YOU_ARE_NOT_A_CLAN_MEMBER;
	
	@ClientString(id = 213, message = "Not working. Please try again later.")
	public static SystemMessageId NOT_WORKING_PLEASE_TRY_AGAIN_LATER;
	
	@ClientString(id = 214, message = "Title has changed.")
	public static SystemMessageId TITLE_HAS_CHANGED;
	
	@ClientString(id = 215, message = "War with the $s1 Clan has begun.")
	public static SystemMessageId WAR_WITH_THE_S1_CLAN_HAS_BEGUN;
	
	@ClientString(id = 216, message = "War with the $s1 Clan has ended.")
	public static SystemMessageId WAR_WITH_THE_S1_CLAN_HAS_ENDED;
	
	@ClientString(id = 217, message = "You have won the war over the $s1 Clan!")
	public static SystemMessageId YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN;
	
	@ClientString(id = 218, message = "You have surrendered to the $s1 Clan.")
	public static SystemMessageId YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN;
	
	@ClientString(id = 219, message = "Your clan leader has died.You have been defeated by the $s1 Clan.")
	public static SystemMessageId YOUR_CLAN_LEADER_HAS_DIED_YOU_HAVE_BEEN_DEFEATED_BY_THE_S1_CLAN;
	
	@ClientString(id = 220, message = "You have $s1 minutes left until the clan war ends.")
	public static SystemMessageId YOU_HAVE_S1_MINUTES_LEFT_UNTIL_THE_CLAN_WAR_ENDS;
	
	@ClientString(id = 221, message = "The time limit for the clan war is up.War with the $s1 Clan is over.")
	public static SystemMessageId THE_TIME_LIMIT_FOR_THE_CLAN_WAR_IS_UP_WAR_WITH_THE_S1_CLAN_IS_OVER;
	
	@ClientString(id = 222, message = "$s1 has newly joined the clan.")
	public static SystemMessageId S1_HAS_NEWLY_JOINED_THE_CLAN;
	
	@ClientString(id = 223, message = "$s1 has withdrawn from the clan.")
	public static SystemMessageId S1_HAS_WITHDRAWN_FROM_THE_CLAN;
	
	@ClientString(id = 224, message = "$s1 did not respond: Invitation to the clan has been cancelled.")
	public static SystemMessageId S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED;
	
	@ClientString(id = 225, message = "You didn't respond to $s1's invitation: joining has been cancelled.")
	public static SystemMessageId YOU_DIDN_T_RESPOND_TO_S1_S_INVITATION_JOINING_HAS_BEEN_CANCELLED;
	
	@ClientString(id = 226, message = "The $s1 Clan did not respond: war proclamation has been refused.")
	public static SystemMessageId THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED;
	
	@ClientString(id = 227, message = "Clan war has been refused because you did not respond to $s1 Clan's war proclamation.")
	public static SystemMessageId CLAN_WAR_HAS_BEEN_REFUSED_BECAUSE_YOU_DID_NOT_RESPOND_TO_S1_CLAN_S_WAR_PROCLAMATION;
	
	@ClientString(id = 228, message = "Request to end war has been denied.")
	public static SystemMessageId REQUEST_TO_END_WAR_HAS_BEEN_DENIED;
	
	@ClientString(id = 229, message = "You are not qualified to create a clan.")
	public static SystemMessageId YOU_ARE_NOT_QUALIFIED_TO_CREATE_A_CLAN;
	
	@ClientString(id = 230, message = "You cannot create a new clan within 10 days of dispersion.")
	public static SystemMessageId YOU_CANNOT_CREATE_A_NEW_CLAN_WITHIN_10_DAYS_OF_DISPERSION;
	
	@ClientString(id = 231, message = "A new member cannot join within 5 days of a clan member's expulsion.")
	public static SystemMessageId A_NEW_MEMBER_CANNOT_JOIN_WITHIN_5_DAYS_OF_A_CLAN_MEMBER_S_EXPULSION;
	
	@ClientString(id = 232, message = "You cannot join a clan within 5 days of expulsion or withdrawal.")
	public static SystemMessageId YOU_CANNOT_JOIN_A_CLAN_WITHIN_5_DAYS_OF_EXPULSION_OR_WITHDRAWAL;
	
	@ClientString(id = 233, message = "The clan is full and cannot accept new members.")
	public static SystemMessageId THE_CLAN_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS;
	
	@ClientString(id = 234, message = "The target must be a clan member.")
	public static SystemMessageId THE_TARGET_MUST_BE_A_CLAN_MEMBER;
	
	@ClientString(id = 235, message = "You cannot transfer your rights.")
	public static SystemMessageId YOU_CANNOT_TRANSFER_YOUR_RIGHTS;
	
	@ClientString(id = 236, message = "Only the clan leader is enabled.")
	public static SystemMessageId ONLY_THE_CLAN_LEADER_IS_ENABLED;
	
	@ClientString(id = 237, message = "Cannot find clan leader.")
	public static SystemMessageId CANNOT_FIND_CLAN_LEADER;
	
	@ClientString(id = 238, message = "Not joined in any clan.")
	public static SystemMessageId NOT_JOINED_IN_ANY_CLAN;
	
	@ClientString(id = 239, message = "The clan leader cannot withdraw.")
	public static SystemMessageId THE_CLAN_LEADER_CANNOT_WITHDRAW;
	
	@ClientString(id = 240, message = "Currently involved in clan war.")
	public static SystemMessageId CURRENTLY_INVOLVED_IN_CLAN_WAR;
	
	@ClientString(id = 241, message = "Leader of the $s1 Clan is not logged in.")
	public static SystemMessageId LEADER_OF_THE_S1_CLAN_IS_NOT_LOGGED_IN;
	
	@ClientString(id = 242, message = "Select target.")
	public static SystemMessageId SELECT_TARGET;
	
	@ClientString(id = 243, message = "Cannot proclaim war on allied clans.")
	public static SystemMessageId CANNOT_PROCLAIM_WAR_ON_ALLIED_CLANS;
	
	@ClientString(id = 244, message = "Unqualified to request declaration of clan war.")
	public static SystemMessageId UNQUALIFIED_TO_REQUEST_DECLARATION_OF_CLAN_WAR;
	
	@ClientString(id = 245, message = "5 days has not passed since you were refused war. Do you want to continue?")
	public static SystemMessageId FIVE_DAYS_HAS_NOT_PASSED_SINCE_YOU_WERE_REFUSED_WAR_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 246, message = "The other clan is currently at war.")
	public static SystemMessageId THE_OTHER_CLAN_IS_CURRENTLY_AT_WAR;
	
	@ClientString(id = 247, message = "You have already been at war with the $s1 Clan: 5 days must pass before you can proclaim war again.")
	public static SystemMessageId YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_PROCLAIM_WAR_AGAIN;
	
	@ClientString(id = 248, message = "You cannot proclaim war: the $s1 Clan does not have enough members.")
	public static SystemMessageId YOU_CANNOT_PROCLAIM_WAR_THE_S1_CLAN_DOES_NOT_HAVE_ENOUGH_MEMBERS;
	
	@ClientString(id = 249, message = "Do you wish to surrender to the $s1 Clan?")
	public static SystemMessageId DO_YOU_WISH_TO_SURRENDER_TO_THE_S1_CLAN;
	
	@ClientString(id = 250, message = "You have personally surrendered to the $s1 Clan. You are leaving the clan war.")
	public static SystemMessageId YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN_YOU_ARE_LEAVING_THE_CLAN_WAR;
	
	@ClientString(id = 251, message = "You cannot proclaim war: you are at war with another clan.")
	public static SystemMessageId YOU_CANNOT_PROCLAIM_WAR_YOU_ARE_AT_WAR_WITH_ANOTHER_CLAN;
	
	@ClientString(id = 252, message = "Enter the name of clan to surrender to.")
	public static SystemMessageId ENTER_THE_NAME_OF_CLAN_TO_SURRENDER_TO;
	
	@ClientString(id = 253, message = "Enter the name of clan to request end of war.")
	public static SystemMessageId ENTER_THE_NAME_OF_CLAN_TO_REQUEST_END_OF_WAR;
	
	@ClientString(id = 254, message = "Clan leader cannot surrender personally.")
	public static SystemMessageId CLAN_LEADER_CANNOT_SURRENDER_PERSONALLY;
	
	@ClientString(id = 255, message = "The $s1 Clan has requested to end war. Do you agree?")
	public static SystemMessageId THE_S1_CLAN_HAS_REQUESTED_TO_END_WAR_DO_YOU_AGREE;
	
	@ClientString(id = 256, message = "Enter name.")
	public static SystemMessageId ENTER_NAME;
	
	@ClientString(id = 257, message = "Do you propose to the $s1 Clan to end the war?")
	public static SystemMessageId DO_YOU_PROPOSE_TO_THE_S1_CLAN_TO_END_THE_WAR;
	
	@ClientString(id = 258, message = "Not involved in clan war.")
	public static SystemMessageId NOT_INVOLVED_IN_CLAN_WAR;
	
	@ClientString(id = 259, message = "Select clan members from list.")
	public static SystemMessageId SELECT_CLAN_MEMBERS_FROM_LIST;
	
	@ClientString(id = 260, message = "Fame level has decreased: 5 days have not passed since you were refused war.")
	public static SystemMessageId FAME_LEVEL_HAS_DECREASED_5_DAYS_HAVE_NOT_PASSED_SINCE_YOU_WERE_REFUSED_WAR;
	
	@ClientString(id = 261, message = "Clan name is incorrect.")
	public static SystemMessageId CLAN_NAME_IS_INCORRECT;
	
	@ClientString(id = 262, message = "Clan name's length is incorrect.")
	public static SystemMessageId CLAN_NAME_S_LENGTH_IS_INCORRECT;
	
	@ClientString(id = 263, message = "Dispersion has already been requested.")
	public static SystemMessageId DISPERSION_HAS_ALREADY_BEEN_REQUESTED;
	
	@ClientString(id = 264, message = "Cannot disperse clan in the middle of war.")
	public static SystemMessageId CANNOT_DISPERSE_CLAN_IN_THE_MIDDLE_OF_WAR;
	
	@ClientString(id = 265, message = "Cannot disperse clan during siege or while protecting castle.")
	public static SystemMessageId CANNOT_DISPERSE_CLAN_DURING_SIEGE_OR_WHILE_PROTECTING_CASTLE;
	
	@ClientString(id = 266, message = "A clan that owns a hideout or castle cannot disperse.")
	public static SystemMessageId A_CLAN_THAT_OWNS_A_HIDEOUT_OR_CASTLE_CANNOT_DISPERSE;
	
	@ClientString(id = 267, message = "No requests for dispersion.")
	public static SystemMessageId NO_REQUESTS_FOR_DISPERSION;
	
	@ClientString(id = 268, message = "You already belong to a (the) clan.")
	public static SystemMessageId YOU_ALREADY_BELONG_TO_A_THE_CLAN;
	
	@ClientString(id = 269, message = "You cannot expel yourself.")
	public static SystemMessageId YOU_CANNOT_EXPEL_YOURSELF;
	
	@ClientString(id = 270, message = "You have already surrendered.")
	public static SystemMessageId YOU_HAVE_ALREADY_SURRENDERED;
	
	@ClientString(id = 271, message = "Title endowment is only possible when clan's skill levels are above 3.")
	public static SystemMessageId TITLE_ENDOWMENT_IS_ONLY_POSSIBLE_WHEN_CLAN_S_SKILL_LEVELS_ARE_ABOVE_3;
	
	@ClientString(id = 272, message = "Clan crest registration is only possible when clan's skill levels are above 3.")
	public static SystemMessageId CLAN_CREST_REGISTRATION_IS_ONLY_POSSIBLE_WHEN_CLAN_S_SKILL_LEVELS_ARE_ABOVE_3;
	
	@ClientString(id = 273, message = "Proclamation of clan war is only possible when clan's skill levels are above 3.")
	public static SystemMessageId PROCLAMATION_OF_CLAN_WAR_IS_ONLY_POSSIBLE_WHEN_CLAN_S_SKILL_LEVELS_ARE_ABOVE_3;
	
	@ClientString(id = 274, message = "Clan's skill level has increased.")
	public static SystemMessageId CLAN_S_SKILL_LEVEL_HAS_INCREASED;
	
	@ClientString(id = 275, message = "Clan has failed to increase skill level.")
	public static SystemMessageId CLAN_HAS_FAILED_TO_INCREASE_SKILL_LEVEL;
	
	@ClientString(id = 276, message = "You do not have enough items to learn skills.")
	public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_SKILLS;
	
	@ClientString(id = 277, message = "You have earned $s1.")
	public static SystemMessageId YOU_HAVE_EARNED_S1;
	
	@ClientString(id = 278, message = "You do not have enough SP to learn skills.")
	public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_SKILLS;
	
	@ClientString(id = 279, message = "You do not have enough adena.")
	public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_ADENA;
	
	@ClientString(id = 280, message = "You do not have any items to sell.")
	public static SystemMessageId YOU_DO_NOT_HAVE_ANY_ITEMS_TO_SELL;
	
	@ClientString(id = 281, message = "You do not have enough custody fees.")
	public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_CUSTODY_FEES;
	
	@ClientString(id = 282, message = "There's nothing here that you've entrusted.")
	public static SystemMessageId THERE_S_NOTHING_HERE_THAT_YOU_VE_ENTRUSTED;
	
	@ClientString(id = 283, message = "You have entered the battlefield.")
	public static SystemMessageId YOU_HAVE_ENTERED_THE_BATTLEFIELD;
	
	@ClientString(id = 284, message = "You have left the battlefield.")
	public static SystemMessageId YOU_HAVE_LEFT_THE_BATTLEFIELD;
	
	@ClientString(id = 285, message = "Clan $s1 has succeeded in engraving the ruler!")
	public static SystemMessageId CLAN_S1_HAS_SUCCEEDED_IN_ENGRAVING_THE_RULER;
	
	@ClientString(id = 286, message = "Your base is being attacked.")
	public static SystemMessageId YOUR_BASE_IS_BEING_ATTACKED;
	
	@ClientString(id = 287, message = "The opponent clan has begun to engrave the ruler.")
	public static SystemMessageId THE_OPPONENT_CLAN_HAS_BEGUN_TO_ENGRAVE_THE_RULER;
	
	@ClientString(id = 288, message = "The castle gate has been broken down.")
	public static SystemMessageId THE_CASTLE_GATE_HAS_BEEN_BROKEN_DOWN;
	
	@ClientString(id = 289, message = "You can only build one base during combat.")
	public static SystemMessageId YOU_CAN_ONLY_BUILD_ONE_BASE_DURING_COMBAT;
	
	@ClientString(id = 290, message = "You cannot set up a base here.")
	public static SystemMessageId YOU_CANNOT_SET_UP_A_BASE_HERE;
	
	@ClientString(id = 291, message = "Clan $s1 is victorious over $s2's castle siege!")
	public static SystemMessageId CLAN_S1_IS_VICTORIOUS_OVER_S2_S_CASTLE_SIEGE;
	
	@ClientString(id = 292, message = "$s1 has announced the castle siege time.")
	public static SystemMessageId S1_HAS_ANNOUNCED_THE_CASTLE_SIEGE_TIME;
	
	@ClientString(id = 293, message = "The registration term for $s1 has ended.")
	public static SystemMessageId THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED;
	
	@ClientString(id = 294, message = "You cannot summon a base because you are not in battle.")
	public static SystemMessageId YOU_CANNOT_SUMMON_A_BASE_BECAUSE_YOU_ARE_NOT_IN_BATTLE;
	
	@ClientString(id = 295, message = "$s1's siege was canceled because there were no clans that participated.")
	public static SystemMessageId S1_S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED;
	
	@ClientString(id = 296, message = "You received $s1 damage from taking a high fall.")
	public static SystemMessageId YOU_RECEIVED_S1_DAMAGE_FROM_TAKING_A_HIGH_FALL;
	
	@ClientString(id = 297, message = "You received $s1 damage because you were unable to breathe.")
	public static SystemMessageId YOU_RECEIVED_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE;
	
	@ClientString(id = 298, message = "You have dropped $s1.")
	public static SystemMessageId YOU_HAVE_DROPPED_S1;
	
	@ClientString(id = 299, message = "$s1 picked up $s3 $s2.")
	public static SystemMessageId S1_PICKED_UP_S3_S2;
	
	@ClientString(id = 300, message = "$s1 picked up $s2.")
	public static SystemMessageId S1_PICKED_UP_S2;
	
	@ClientString(id = 301, message = "$s2 $s1 has disappeared.")
	public static SystemMessageId S2_S1_HAS_DISAPPEARED;
	
	@ClientString(id = 302, message = "$s1 has disappeared.")
	public static SystemMessageId S1_HAS_DISAPPEARED;
	
	@ClientString(id = 303, message = "Select item to enchant.")
	public static SystemMessageId SELECT_ITEM_TO_ENCHANT;
	
	@ClientString(id = 304, message = "Clan member $s1 has logged into game.")
	public static SystemMessageId CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME;
	
	@ClientString(id = 305, message = "The player declined to join your party.")
	public static SystemMessageId THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY;
	
	@ClientString(id = 306, message = "You have failed to delete the character.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER;
	
	@ClientString(id = 307, message = "You have failed to trade with the warehouse.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_TRADE_WITH_THE_WAREHOUSE;
	
	@ClientString(id = 308, message = "Failed to join the clan")
	public static SystemMessageId FAILED_TO_JOIN_THE_CLAN;
	
	@ClientString(id = 309, message = "Succeeded in expelling a clan member.")
	public static SystemMessageId SUCCEEDED_IN_EXPELLING_A_CLAN_MEMBER;
	
	@ClientString(id = 310, message = "Failed to expel a clan member.")
	public static SystemMessageId FAILED_TO_EXPEL_A_CLAN_MEMBER;
	
	@ClientString(id = 311, message = "Clan war has been accepted.")
	public static SystemMessageId CLAN_WAR_HAS_BEEN_ACCEPTED;
	
	@ClientString(id = 312, message = "Clan war has been refused.")
	public static SystemMessageId CLAN_WAR_HAS_BEEN_REFUSED;
	
	@ClientString(id = 313, message = "The cease war request has been accepted.")
	public static SystemMessageId THE_CEASE_WAR_REQUEST_HAS_BEEN_ACCEPTED;
	
	@ClientString(id = 314, message = "Failed to surrender.")
	public static SystemMessageId FAILED_TO_SURRENDER;
	
	@ClientString(id = 315, message = "Ffailed to personally surrender.")
	public static SystemMessageId FFAILED_TO_PERSONALLY_SURRENDER;
	
	@ClientString(id = 316, message = "Failed to withdraw from the party.")
	public static SystemMessageId FAILED_TO_WITHDRAW_FROM_THE_PARTY;
	
	@ClientString(id = 317, message = "Failed to expel a party member.")
	public static SystemMessageId FAILED_TO_EXPEL_A_PARTY_MEMBER;
	
	@ClientString(id = 318, message = "Failed to disperse the party.")
	public static SystemMessageId FAILED_TO_DISPERSE_THE_PARTY;
	
	@ClientString(id = 319, message = "You are unable to unlock the door.")
	public static SystemMessageId YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR;
	
	@ClientString(id = 320, message = "You have failed to unlock the door.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR;
	
	@ClientString(id = 321, message = "It is not locked.")
	public static SystemMessageId IT_IS_NOT_LOCKED;
	
	@ClientString(id = 322, message = "Please decide on the sales price.")
	public static SystemMessageId PLEASE_DECIDE_ON_THE_SALES_PRICE;
	
	@ClientString(id = 323, message = "Your force has increased to $s1 level.")
	public static SystemMessageId YOUR_FORCE_HAS_INCREASED_TO_S1_LEVEL;
	
	@ClientString(id = 324, message = "You can no longer increase your force.")
	public static SystemMessageId YOU_CAN_NO_LONGER_INCREASE_YOUR_FORCE;
	
	@ClientString(id = 325, message = "The corpse has already disappeared.")
	public static SystemMessageId THE_CORPSE_HAS_ALREADY_DISAPPEARED;
	
	@ClientString(id = 326, message = "Select target from list.")
	public static SystemMessageId SELECT_TARGET_FROM_LIST;
	
	@ClientString(id = 327, message = "You cannot exceed 80 characters.")
	public static SystemMessageId YOU_CANNOT_EXCEED_80_CHARACTERS;
	
	@ClientString(id = 328, message = "Please input title using less than 128 characters.")
	public static SystemMessageId PLEASE_INPUT_TITLE_USING_LESS_THAN_128_CHARACTERS;
	
	@ClientString(id = 329, message = "Please input contents using less than 3000 characters.")
	public static SystemMessageId PLEASE_INPUT_CONTENTS_USING_LESS_THAN_3000_CHARACTERS;
	
	@ClientString(id = 330, message = "A one-line response may not exceed 128 characters.")
	public static SystemMessageId A_ONE_LINE_RESPONSE_MAY_NOT_EXCEED_128_CHARACTERS;
	
	@ClientString(id = 331, message = "You have acquired $s1 SP.")
	public static SystemMessageId YOU_HAVE_ACQUIRED_S1_SP;
	
	@ClientString(id = 332, message = "Do you want to be restored?")
	public static SystemMessageId DO_YOU_WANT_TO_BE_RESTORED;
	
	@ClientString(id = 333, message = "You have received $s1 damage by Core's barrier.")
	public static SystemMessageId YOU_HAVE_RECEIVED_S1_DAMAGE_BY_CORE_S_BARRIER;
	
	@ClientString(id = 334, message = "Please enter store message.")
	public static SystemMessageId PLEASE_ENTER_STORE_MESSAGE;
	
	@ClientString(id = 335, message = "$s1 is aborted.")
	public static SystemMessageId S1_IS_ABORTED;
	
	@ClientString(id = 336, message = "$s1 is crystallized. Do you want to continue?")
	public static SystemMessageId S1_IS_CRYSTALLIZED_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 337, message = "Soulshot does not match weapon grade.")
	public static SystemMessageId SOULSHOT_DOES_NOT_MATCH_WEAPON_GRADE;
	
	@ClientString(id = 338, message = "Not enough soulshots.")
	public static SystemMessageId NOT_ENOUGH_SOULSHOTS;
	
	@ClientString(id = 339, message = "Cannot use soulshots.")
	public static SystemMessageId CANNOT_USE_SOULSHOTS;
	
	@ClientString(id = 340, message = "Private store under way.")
	public static SystemMessageId PRIVATE_STORE_UNDER_WAY;
	
	@ClientString(id = 341, message = "Not enough materials.")
	public static SystemMessageId NOT_ENOUGH_MATERIALS;
	
	@ClientString(id = 342, message = "Power of the spirits enabled.")
	public static SystemMessageId POWER_OF_THE_SPIRITS_ENABLED;
	
	@ClientString(id = 343, message = "Sweeper failed, target not spoiled.")
	public static SystemMessageId SWEEPER_FAILED_TARGET_NOT_SPOILED;
	
	@ClientString(id = 344, message = "Power of the spirits disabled.")
	public static SystemMessageId POWER_OF_THE_SPIRITS_DISABLED;
	
	@ClientString(id = 345, message = "Chat enabled.")
	public static SystemMessageId CHAT_ENABLED;
	
	@ClientString(id = 346, message = "Chat disabled.")
	public static SystemMessageId CHAT_DISABLED;
	
	@ClientString(id = 347, message = "Incorrect item count.")
	public static SystemMessageId INCORRECT_ITEM_COUNT;
	
	@ClientString(id = 348, message = "Incorrect item price.")
	public static SystemMessageId INCORRECT_ITEM_PRICE;
	
	@ClientString(id = 349, message = "Private store already closed.")
	public static SystemMessageId PRIVATE_STORE_ALREADY_CLOSED;
	
	@ClientString(id = 350, message = "Item out of stock.")
	public static SystemMessageId ITEM_OUT_OF_STOCK;
	
	@ClientString(id = 351, message = "Incorrect item count.")
	public static SystemMessageId INCORRECT_ITEM_COUNT_2;
	
	@ClientString(id = 352, message = "Incorrect item.")
	public static SystemMessageId INCORRECT_ITEM;
	
	@ClientString(id = 353, message = "Cannot purchase.")
	public static SystemMessageId CANNOT_PURCHASE;
	
	@ClientString(id = 354, message = "Cancel enchant.")
	public static SystemMessageId CANCEL_ENCHANT;
	
	@ClientString(id = 355, message = "Inappropriate enchant conditions.")
	public static SystemMessageId INAPPROPRIATE_ENCHANT_CONDITIONS;
	
	@ClientString(id = 356, message = "Reject resurrection.")
	public static SystemMessageId REJECT_RESURRECTION;
	
	@ClientString(id = 357, message = "Already spoiled.")
	public static SystemMessageId ALREADY_SPOILED;
	
	@ClientString(id = 358, message = "$s1 hour(s) until castle siege conclusion.")
	public static SystemMessageId S1_HOUR_S_UNTIL_CASTLE_SIEGE_CONCLUSION;
	
	@ClientString(id = 359, message = "$s1 minute(s) until castle siege conclusion.")
	public static SystemMessageId S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION;
	
	@ClientString(id = 360, message = "Castle siege $s1 second(s) left!")
	public static SystemMessageId CASTLE_SIEGE_S1_SECOND_S_LEFT;
	
	@ClientString(id = 361, message = "Over-hit!")
	public static SystemMessageId OVER_HIT;
	
	@ClientString(id = 362, message = "Acquired $s1 bonus experience through over-hit.")
	public static SystemMessageId ACQUIRED_S1_BONUS_EXPERIENCE_THROUGH_OVER_HIT;
	
	@ClientString(id = 363, message = "Chat available time: $s1 minute.")
	public static SystemMessageId CHAT_AVAILABLE_TIME_S1_MINUTE;
	
	@ClientString(id = 364, message = "Enter user's name to search.")
	public static SystemMessageId ENTER_USER_S_NAME_TO_SEARCH;
	
	@ClientString(id = 365, message = "Are you sure?")
	public static SystemMessageId ARE_YOU_SURE;
	
	@ClientString(id = 366, message = "Select hair color.")
	public static SystemMessageId SELECT_HAIR_COLOR;
	
	@ClientString(id = 367, message = "Cannot remove clan character.")
	public static SystemMessageId CANNOT_REMOVE_CLAN_CHARACTER;
	
	@ClientString(id = 368, message = "Equipped +$s1$s2.")
	public static SystemMessageId EQUIPPED_S1_S2;
	
	@ClientString(id = 369, message = "Picked up +$s1$s2.")
	public static SystemMessageId PICKED_UP_S1_S2;
	
	@ClientString(id = 370, message = "Failed to pick up $s1.")
	public static SystemMessageId FAILED_TO_PICK_UP_S1_2;
	
	@ClientString(id = 371, message = "Acquired +$s1$s2.")
	public static SystemMessageId ACQUIRED_S1_S2;
	
	@ClientString(id = 372, message = "Failed to earn $s1.")
	public static SystemMessageId FAILED_TO_EARN_S1_2;
	
	@ClientString(id = 373, message = "Destroy +$s1$s2. Do you wish to continue?")
	public static SystemMessageId DESTROY_S1_S2_DO_YOU_WISH_TO_CONTINUE;
	
	@ClientString(id = 374, message = "Crystallize +$s1$s2. Do you wish to continue?")
	public static SystemMessageId CRYSTALLIZE_S1_S2_DO_YOU_WISH_TO_CONTINUE;
	
	@ClientString(id = 375, message = "Dropped +$s1$s2.")
	public static SystemMessageId DROPPED_S1_S2;
	
	@ClientString(id = 376, message = "$s1 picked up +$s2$s3.")
	public static SystemMessageId S1_PICKED_UP_S2_S3;
	
	@ClientString(id = 377, message = "+$s1$s2 disappeared.")
	public static SystemMessageId S1_S2_DISAPPEARED;
	
	@ClientString(id = 378, message = "$s1 purchased $s2.")
	public static SystemMessageId S1_PURCHASED_S2;
	
	@ClientString(id = 379, message = "$s1 purchased +$s2$s3.")
	public static SystemMessageId S1_PURCHASED_S2_S3;
	
	@ClientString(id = 380, message = "$s1 purchased $s3 $s2(s).")
	public static SystemMessageId S1_PURCHASED_S3_S2_S;
	
	@ClientString(id = 381, message = "Cannot connect to petition server.")
	public static SystemMessageId CANNOT_CONNECT_TO_PETITION_SERVER;
	
	@ClientString(id = 382, message = "Currently there are no users that have checked out a GM ID.")
	public static SystemMessageId CURRENTLY_THERE_ARE_NO_USERS_THAT_HAVE_CHECKED_OUT_A_GM_ID;
	
	@ClientString(id = 383, message = "Request confirmed to end consultation at petition server.")
	public static SystemMessageId REQUEST_CONFIRMED_TO_END_CONSULTATION_AT_PETITION_SERVER;
	
	@ClientString(id = 384, message = "The client is not logged onto the game server.")
	public static SystemMessageId THE_CLIENT_IS_NOT_LOGGED_ONTO_THE_GAME_SERVER;
	
	@ClientString(id = 385, message = "Request confirmed to begin consultation at petition server.")
	public static SystemMessageId REQUEST_CONFIRMED_TO_BEGIN_CONSULTATION_AT_PETITION_SERVER;
	
	@ClientString(id = 386, message = "Petition requests must be over five characters.")
	public static SystemMessageId PETITION_REQUESTS_MUST_BE_OVER_FIVE_CHARACTERS;
	
	@ClientString(id = 387, message = "Ending petition consultation.")
	public static SystemMessageId ENDING_PETITION_CONSULTATION;
	
	@ClientString(id = 388, message = "Not under petition consultation.")
	public static SystemMessageId NOT_UNDER_PETITION_CONSULTATION;
	
	@ClientString(id = 389, message = "Petition application accepted. Receipt No. is $s1.")
	public static SystemMessageId PETITION_APPLICATION_ACCEPTED_RECEIPT_NO_IS_S1;
	
	@ClientString(id = 390, message = "Already applied for petition.")
	public static SystemMessageId ALREADY_APPLIED_FOR_PETITION;
	
	@ClientString(id = 391, message = "Receipt No. $s1, petition cancelled.")
	public static SystemMessageId RECEIPT_NO_S1_PETITION_CANCELLED;
	
	@ClientString(id = 392, message = "Under petition advice.")
	public static SystemMessageId UNDER_PETITION_ADVICE;
	
	@ClientString(id = 393, message = "Failed to cancel petition. Please try again later.")
	public static SystemMessageId FAILED_TO_CANCEL_PETITION_PLEASE_TRY_AGAIN_LATER;
	
	@ClientString(id = 394, message = "Petition consultation with $s1, under way.")
	public static SystemMessageId PETITION_CONSULTATION_WITH_S1_UNDER_WAY;
	
	@ClientString(id = 395, message = "Ending petition consultation with $s1.")
	public static SystemMessageId ENDING_PETITION_CONSULTATION_WITH_S1;
	
	@ClientString(id = 396, message = "Please login after changing your temporary password.")
	public static SystemMessageId PLEASE_LOGIN_AFTER_CHANGING_YOUR_TEMPORARY_PASSWORD;
	
	@ClientString(id = 397, message = "Not a paid account.")
	public static SystemMessageId NOT_A_PAID_ACCOUNT;
	
	@ClientString(id = 398, message = "You have no more time left on your account.")
	public static SystemMessageId YOU_HAVE_NO_MORE_TIME_LEFT_ON_YOUR_ACCOUNT;
	
	@ClientString(id = 399, message = "System error.")
	public static SystemMessageId SYSTEM_ERROR;
	
	@ClientString(id = 400, message = "Discard $s1. Do you wish to continue?")
	public static SystemMessageId DISCARD_S1_DO_YOU_WISH_TO_CONTINUE;
	
	@ClientString(id = 401, message = "Too many quests in progress.")
	public static SystemMessageId TOO_MANY_QUESTS_IN_PROGRESS;
	
	@ClientString(id = 402, message = "You may not get on board without a pass.")
	public static SystemMessageId YOU_MAY_NOT_GET_ON_BOARD_WITHOUT_A_PASS;
	
	@ClientString(id = 403, message = "You have exceeded your pocket money limit.")
	public static SystemMessageId YOU_HAVE_EXCEEDED_YOUR_POCKET_MONEY_LIMIT;
	
	@ClientString(id = 404, message = "Create Item level is low.")
	public static SystemMessageId CREATE_ITEM_LEVEL_IS_LOW;
	
	@ClientString(id = 405, message = "The total price of the product is too high.")
	public static SystemMessageId THE_TOTAL_PRICE_OF_THE_PRODUCT_IS_TOO_HIGH;
	
	@ClientString(id = 406, message = "Petition application accepted.")
	public static SystemMessageId PETITION_APPLICATION_ACCEPTED;
	
	@ClientString(id = 407, message = "Petition under process.")
	public static SystemMessageId PETITION_UNDER_PROCESS;
	
	@ClientString(id = 408, message = "Set Period")
	public static SystemMessageId SET_PERIOD;
	
	@ClientString(id = 409, message = "Set Time-$s1: $s2: $s3")
	public static SystemMessageId SET_TIME_S1_S2_S3;
	
	@ClientString(id = 410, message = "Registration Period")
	public static SystemMessageId REGISTRATION_PERIOD;
	
	@ClientString(id = 411, message = "Registration TIme-$s1: $s2: $s3")
	public static SystemMessageId REGISTRATION_TIME_S1_S2_S3;
	
	@ClientString(id = 412, message = "Battle begins in $s1: $s2: $s4")
	public static SystemMessageId BATTLE_BEGINS_IN_S1_S2_S4;
	
	@ClientString(id = 413, message = "Battle ends in $s1: $s2: $s5")
	public static SystemMessageId BATTLE_ENDS_IN_S1_S2_S5;
	
	@ClientString(id = 414, message = "Standby")
	public static SystemMessageId STANDBY;
	
	@ClientString(id = 415, message = "Under Siege")
	public static SystemMessageId UNDER_SIEGE;
	
	@ClientString(id = 416, message = "Cannot be exchanged.")
	public static SystemMessageId CANNOT_BE_EXCHANGED;
	
	@ClientString(id = 417, message = "$s1 has been disarmed.")
	public static SystemMessageId S1_HAS_BEEN_DISARMED;
	
	@ClientString(id = 418, message = "There is a significant difference between the item's price and its standard price. Please check again.")
	public static SystemMessageId THERE_IS_A_SIGNIFICANT_DIFFERENCE_BETWEEN_THE_ITEM_S_PRICE_AND_ITS_STANDARD_PRICE_PLEASE_CHECK_AGAIN;
	
	@ClientString(id = 419, message = "$s1 minute(s) of designated usage time left.")
	public static SystemMessageId S1_MINUTE_S_OF_DESIGNATED_USAGE_TIME_LEFT;
	
	@ClientString(id = 420, message = "Time expired.")
	public static SystemMessageId TIME_EXPIRED;
	
	@ClientString(id = 421, message = "Another person has logged in with the same account.")
	public static SystemMessageId ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT;
	
	@ClientString(id = 422, message = "You have exceeded the weight limit.")
	public static SystemMessageId YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT;
	
	@ClientString(id = 423, message = "The Scroll of Enchant cannot be cancelled.")
	public static SystemMessageId THE_SCROLL_OF_ENCHANT_CANNOT_BE_CANCELLED;
	
	@ClientString(id = 424, message = "Does not fit strengthening conditions of the scroll.")
	public static SystemMessageId DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL;
	
	@ClientString(id = 425, message = "Your Create Item level is too low.")
	public static SystemMessageId YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW;
	
	@ClientString(id = 426, message = "Your account has been reported for intentionally not paying the cyber café fees.")
	public static SystemMessageId YOUR_ACCOUNT_HAS_BEEN_REPORTED_FOR_INTENTIONALLY_NOT_PAYING_THE_CYBER_CAF_FEES;
	
	@ClientString(id = 427, message = "Please contact us.")
	public static SystemMessageId PLEASE_CONTACT_US;
	
	@ClientString(id = 428, message = "번역불필요")
	public static SystemMessageId EMPTY;
	
	@ClientString(id = 429, message = "번역불필요")
	public static SystemMessageId EMPTY_2;
	
	@ClientString(id = 430, message = "번역불필요")
	public static SystemMessageId EMPTY_3;
	
	@ClientString(id = 431, message = "번역불필요")
	public static SystemMessageId EMPTY_4;
	
	@ClientString(id = 432, message = "번역불필요")
	public static SystemMessageId EMPTY_5;
	
	@ClientString(id = 433, message = "번역불필요")
	public static SystemMessageId EMPTY_6;
	
	@ClientString(id = 434, message = "번역불필요")
	public static SystemMessageId EMPTY_7;
	
	@ClientString(id = 435, message = "번역불필요")
	public static SystemMessageId EMPTY_8;
	
	@ClientString(id = 436, message = "번역불필요")
	public static SystemMessageId EMPTY_9;
	
	@ClientString(id = 437, message = "번역불필요")
	public static SystemMessageId EMPTY_10;
	
	@ClientString(id = 438, message = "번역불필요")
	public static SystemMessageId EMPTY_11;
	
	@ClientString(id = 439, message = "번역불필요")
	public static SystemMessageId EMPTY_12;
	
	@ClientString(id = 440, message = "번역불필요")
	public static SystemMessageId EMPTY_13;
	
	@ClientString(id = 441, message = "번역불필요")
	public static SystemMessageId EMPTY_14;
	
	@ClientString(id = 442, message = "번역불필요")
	public static SystemMessageId EMPTY_15;
	
	@ClientString(id = 443, message = "번역불필요")
	public static SystemMessageId EMPTY_16;
	
	@ClientString(id = 444, message = "The following account may not be accessed due to a withdrawal request that has been submitted.")
	public static SystemMessageId THE_FOLLOWING_ACCOUNT_MAY_NOT_BE_ACCESSED_DUE_TO_A_WITHDRAWAL_REQUEST_THAT_HAS_BEEN_SUBMITTED;
	
	@ClientString(id = 445, message = "Your application number is $s1.")
	public static SystemMessageId YOUR_APPLICATION_NUMBER_IS_S1;
	
	@ClientString(id = 446, message = "For more details, please visit the official Lineage II website (http://www.lineage2.co.kr/).")
	public static SystemMessageId FOR_MORE_DETAILS_PLEASE_VISIT_THE_OFFICIAL_LINEAGE_II_WEBSITE_HTTP_WWW_LINEAGE2_CO_KR;
	
	@ClientString(id = 447, message = ".")
	public static SystemMessageId EMPTY_17;
	
	@ClientString(id = 448, message = "System error, please log in again later.")
	public static SystemMessageId SYSTEM_ERROR_PLEASE_LOG_IN_AGAIN_LATER;
	
	@ClientString(id = 449, message = "Password does not match this acount.")
	public static SystemMessageId PASSWORD_DOES_NOT_MATCH_THIS_ACOUNT;
	
	@ClientString(id = 450, message = "Confirm your account information and log in again later.")
	public static SystemMessageId CONFIRM_YOUR_ACCOUNT_INFORMATION_AND_LOG_IN_AGAIN_LATER;
	
	@ClientString(id = 451, message = "Password does not match this acount.")
	public static SystemMessageId PASSWORD_DOES_NOT_MATCH_THIS_ACOUNT_2;
	
	@ClientString(id = 452, message = "Confirm your account information and try again later.")
	public static SystemMessageId CONFIRM_YOUR_ACCOUNT_INFORMATION_AND_TRY_AGAIN_LATER;
	
	@ClientString(id = 453, message = "Your account information is incorrect.")
	public static SystemMessageId YOUR_ACCOUNT_INFORMATION_IS_INCORRECT;
	
	@ClientString(id = 454, message = "Please contact the Customer Support Center or the 1: 1 inquiry service of the Lineage II official website.")
	public static SystemMessageId PLEASE_CONTACT_THE_CUSTOMER_SUPPORT_CENTER_OR_THE_1_1_INQUIRY_SERVICE_OF_THE_LINEAGE_II_OFFICIAL_WEBSITE;
	
	@ClientString(id = 455, message = "The account is already in use. Access denied.")
	public static SystemMessageId THE_ACCOUNT_IS_ALREADY_IN_USE_ACCESS_DENIED;
	
	@ClientString(id = 456, message = "Lineage II game services may only be used by adults 18 years of age and older.")
	public static SystemMessageId LINEAGE_II_GAME_SERVICES_MAY_ONLY_BE_USED_BY_ADULTS_18_YEARS_OF_AGE_AND_OLDER;
	
	@ClientString(id = 457, message = "Server under maintenance. Please try again later.")
	public static SystemMessageId SERVER_UNDER_MAINTENANCE_PLEASE_TRY_AGAIN_LATER;
	
	@ClientString(id = 458, message = "Your usage term has been expired.")
	public static SystemMessageId YOUR_USAGE_TERM_HAS_BEEN_EXPIRED;
	
	@ClientString(id = 459, message = "Extend your usage time")
	public static SystemMessageId EXTEND_YOUR_USAGE_TIME;
	
	@ClientString(id = 460, message = "at the official Lineage II website (http://www.lineage2.co.kr).")
	public static SystemMessageId AT_THE_OFFICIAL_LINEAGE_II_WEBSITE_HTTP_WWW_LINEAGE2_CO_KR;
	
	@ClientString(id = 461, message = "Access failed.")
	public static SystemMessageId ACCESS_FAILED;
	
	@ClientString(id = 462, message = "Please try again later.")
	public static SystemMessageId PLEASE_TRY_AGAIN_LATER_2;
	
	@ClientString(id = 463, message = ".")
	public static SystemMessageId EMPTY_18;
	
	@ClientString(id = 464, message = "Feature available to alliance leaders only.")
	public static SystemMessageId FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY;
	
	@ClientString(id = 465, message = "No current alliances.")
	public static SystemMessageId NO_CURRENT_ALLIANCES;
	
	@ClientString(id = 466, message = "You have exceeded the limit.")
	public static SystemMessageId YOU_HAVE_EXCEEDED_THE_LIMIT;
	
	@ClientString(id = 467, message = "You may not accept any clan within a day after expelling another clan.")
	public static SystemMessageId YOU_MAY_NOT_ACCEPT_ANY_CLAN_WITHIN_A_DAY_AFTER_EXPELLING_ANOTHER_CLAN;
	
	@ClientString(id = 468, message = "A clan that has withdrawn or been expelled cannot enter into an alliance within one day of withdrawal or expulsion.")
	public static SystemMessageId A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION;
	
	@ClientString(id = 469, message = "You may not ally with a clan you are at battle with.")
	public static SystemMessageId YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_AT_BATTLE_WITH;
	
	@ClientString(id = 470, message = "Only the clan leader may apply for withdrawal from the alliance.")
	public static SystemMessageId ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE;
	
	@ClientString(id = 471, message = "Alliance leaders cannot withdraw.")
	public static SystemMessageId ALLIANCE_LEADERS_CANNOT_WITHDRAW;
	
	@ClientString(id = 472, message = "You cannot expel yourself from the clan.")
	public static SystemMessageId YOU_CANNOT_EXPEL_YOURSELF_FROM_THE_CLAN;
	
	@ClientString(id = 473, message = "Different alliance.")
	public static SystemMessageId DIFFERENT_ALLIANCE;
	
	@ClientString(id = 474, message = "The following clan does not exist.")
	public static SystemMessageId THE_FOLLOWING_CLAN_DOES_NOT_EXIST;
	
	@ClientString(id = 475, message = "Different alliance.")
	public static SystemMessageId DIFFERENT_ALLIANCE_2;
	
	@ClientString(id = 476, message = "Incorrect image size. Please adjust to 8x12.")
	public static SystemMessageId INCORRECT_IMAGE_SIZE_PLEASE_ADJUST_TO_8X12;
	
	@ClientString(id = 477, message = "No response. Invitation to join an alliance has been cancelled.")
	public static SystemMessageId NO_RESPONSE_INVITATION_TO_JOIN_AN_ALLIANCE_HAS_BEEN_CANCELLED;
	
	@ClientString(id = 478, message = "No response. Your entrance to the alliance has been cancelled.")
	public static SystemMessageId NO_RESPONSE_YOUR_ENTRANCE_TO_THE_ALLIANCE_HAS_BEEN_CANCELLED;
	
	@ClientString(id = 479, message = "$s1 has newly joined as a friend.")
	public static SystemMessageId S1_HAS_NEWLY_JOINED_AS_A_FRIEND;
	
	@ClientString(id = 480, message = "Please check your Friends List.")
	public static SystemMessageId PLEASE_CHECK_YOUR_FRIENDS_LIST;
	
	@ClientString(id = 481, message = "$s1 has been deleted from your friends list.")
	public static SystemMessageId S1_HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST;
	
	@ClientString(id = 482, message = "You cannot add yourself to your own friend list.")
	public static SystemMessageId YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST_2;
	
	@ClientString(id = 483, message = "Friend list is not ready yet. Please try again later.")
	public static SystemMessageId FRIEND_LIST_IS_NOT_READY_YET_PLEASE_TRY_AGAIN_LATER;
	
	@ClientString(id = 484, message = "Already registered on the friends list.")
	public static SystemMessageId ALREADY_REGISTERED_ON_THE_FRIENDS_LIST;
	
	@ClientString(id = 485, message = "No new friend invitations from other users.")
	public static SystemMessageId NO_NEW_FRIEND_INVITATIONS_FROM_OTHER_USERS;
	
	@ClientString(id = 486, message = "The following user is not in your friends list.")
	public static SystemMessageId THE_FOLLOWING_USER_IS_NOT_IN_YOUR_FRIENDS_LIST;
	
	@ClientString(id = 487, message = "======<Friends List>======")
	public static SystemMessageId FRIENDS_LIST;
	
	@ClientString(id = 488, message = "$s1 (Currently: Online)")
	public static SystemMessageId S1_CURRENTLY_ONLINE;
	
	@ClientString(id = 489, message = "$s1 (Currently: Offline)")
	public static SystemMessageId S1_CURRENTLY_OFFLINE;
	
	@ClientString(id = 490, message = "========================")
	public static SystemMessageId EMPTY_19;
	
	@ClientString(id = 491, message = "=======<Alliance Information>=======")
	public static SystemMessageId ALLIANCE_INFORMATION;
	
	@ClientString(id = 492, message = "Alliance Name: $s1")
	public static SystemMessageId ALLIANCE_NAME_S1;
	
	@ClientString(id = 493, message = "Connection: $s1 / Total $s2")
	public static SystemMessageId CONNECTION_S1_TOTAL_S2;
	
	@ClientString(id = 494, message = "Alliance Leader: $s2 of $s1")
	public static SystemMessageId ALLIANCE_LEADER_S2_OF_S1;
	
	@ClientString(id = 495, message = "Affiliated clans: Total $s1 clan(s)")
	public static SystemMessageId AFFILIATED_CLANS_TOTAL_S1_CLAN_S;
	
	@ClientString(id = 496, message = "=====<Clan Information>=====")
	public static SystemMessageId CLAN_INFORMATION;
	
	@ClientString(id = 497, message = "Clan Name: $s1")
	public static SystemMessageId CLAN_NAME_S1;
	
	@ClientString(id = 498, message = "Clan Leader: $s1")
	public static SystemMessageId CLAN_LEADER_S1;
	
	@ClientString(id = 499, message = "Clan level: $s1")
	public static SystemMessageId CLAN_LEVEL_S1;
	
	@ClientString(id = 500, message = "------------------------")
	public static SystemMessageId EMPTY_20;
	
	@ClientString(id = 501, message = "========================")
	public static SystemMessageId EMPTY_21;
	
	@ClientString(id = 502, message = "You have already joined an alliance.")
	public static SystemMessageId YOU_HAVE_ALREADY_JOINED_AN_ALLIANCE;
	
	@ClientString(id = 503, message = "$s1 (Friend) has logged in.")
	public static SystemMessageId S1_FRIEND_HAS_LOGGED_IN;
	
	@ClientString(id = 504, message = "Only clan leaders may create alliances.")
	public static SystemMessageId ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES;
	
	@ClientString(id = 505, message = "You cannot create a new alliance within 10 days after dissolution.")
	public static SystemMessageId YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_10_DAYS_AFTER_DISSOLUTION;
	
	@ClientString(id = 506, message = "Incorrect alliance name.")
	public static SystemMessageId INCORRECT_ALLIANCE_NAME;
	
	@ClientString(id = 507, message = "Incorrect length for an alliance name.")
	public static SystemMessageId INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME;
	
	@ClientString(id = 508, message = "The following alliance already exists.")
	public static SystemMessageId THE_FOLLOWING_ALLIANCE_ALREADY_EXISTS;
	
	@ClientString(id = 509, message = "Cannot accept. clan ally is registered as an enemy during siege battle.")
	public static SystemMessageId CANNOT_ACCEPT_CLAN_ALLY_IS_REGISTERED_AS_AN_ENEMY_DURING_SIEGE_BATTLE;
	
	@ClientString(id = 510, message = "You have invited someone to your alliance.")
	public static SystemMessageId YOU_HAVE_INVITED_SOMEONE_TO_YOUR_ALLIANCE;
	
	@ClientString(id = 511, message = "Select user to invite.")
	public static SystemMessageId SELECT_USER_TO_INVITE;
	
	@ClientString(id = 512, message = "Do you really wish to withdraw from the alliance?")
	public static SystemMessageId DO_YOU_REALLY_WISH_TO_WITHDRAW_FROM_THE_ALLIANCE;
	
	@ClientString(id = 513, message = "Enter the name of the clan you wish to expel.")
	public static SystemMessageId ENTER_THE_NAME_OF_THE_CLAN_YOU_WISH_TO_EXPEL;
	
	@ClientString(id = 514, message = "Do you really wish to dissolve the alliance?")
	public static SystemMessageId DO_YOU_REALLY_WISH_TO_DISSOLVE_THE_ALLIANCE;
	
	@ClientString(id = 515, message = "Enter file name for the alliance crest.")
	public static SystemMessageId ENTER_FILE_NAME_FOR_THE_ALLIANCE_CREST;
	
	@ClientString(id = 516, message = "$s1 has invited you as a Friend.")
	public static SystemMessageId S1_HAS_INVITED_YOU_AS_A_FRIEND;
	
	@ClientString(id = 517, message = "You have accepted the alliance.")
	public static SystemMessageId YOU_HAVE_ACCEPTED_THE_ALLIANCE;
	
	@ClientString(id = 518, message = "You have failed to invite a clan into the alliance.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE;
	
	@ClientString(id = 519, message = "You have withdrawn from the alliance.")
	public static SystemMessageId YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE;
	
	@ClientString(id = 520, message = "You have failed to withdraw from the alliance.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE;
	
	@ClientString(id = 521, message = "You have succeeded in expelling a clan.")
	public static SystemMessageId YOU_HAVE_SUCCEEDED_IN_EXPELLING_A_CLAN;
	
	@ClientString(id = 522, message = "You have failed to expel a clan.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_EXPEL_A_CLAN;
	
	@ClientString(id = 523, message = "The alliance has dissolved.")
	public static SystemMessageId THE_ALLIANCE_HAS_DISSOLVED;
	
	@ClientString(id = 524, message = "You have failed to dissolve the alliance.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE;
	
	@ClientString(id = 525, message = "You have succeeded in inviting a Friend.")
	public static SystemMessageId YOU_HAVE_SUCCEEDED_IN_INVITING_A_FRIEND;
	
	@ClientString(id = 526, message = "You have failed to invite a Friend.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_INVITE_A_FRIEND;
	
	@ClientString(id = 527, message = "$s2, the alliance leader of $s1 has requested an alliance.")
	public static SystemMessageId S2_THE_ALLIANCE_LEADER_OF_S1_HAS_REQUESTED_AN_ALLIANCE;
	
	@ClientString(id = 528, message = "File not found.")
	public static SystemMessageId FILE_NOT_FOUND;
	
	@ClientString(id = 529, message = "You may only register 8x12 *.bmp files with 256 colors.")
	public static SystemMessageId YOU_MAY_ONLY_REGISTER_8X12_BMP_FILES_WITH_256_COLORS;
	
	@ClientString(id = 530, message = "Spiritshot does not match weapon grade.")
	public static SystemMessageId SPIRITSHOT_DOES_NOT_MATCH_WEAPON_GRADE;
	
	@ClientString(id = 531, message = "Not enough Spiritshots.")
	public static SystemMessageId NOT_ENOUGH_SPIRITSHOTS;
	
	@ClientString(id = 532, message = "Cannot use Spiritshots.")
	public static SystemMessageId CANNOT_USE_SPIRITSHOTS;
	
	@ClientString(id = 533, message = "Power of Mana enabled.")
	public static SystemMessageId POWER_OF_MANA_ENABLED;
	
	@ClientString(id = 534, message = "Power of Mana disabled.")
	public static SystemMessageId POWER_OF_MANA_DISABLED;
	
	@ClientString(id = 535, message = "Name pet.")
	public static SystemMessageId NAME_PET;
	
	@ClientString(id = 536, message = "How much adena do you wish to transfer to your Inventory?")
	public static SystemMessageId HOW_MUCH_ADENA_DO_YOU_WISH_TO_TRANSFER_TO_YOUR_INVENTORY;
	
	@ClientString(id = 537, message = "How much will you transfer?")
	public static SystemMessageId HOW_MUCH_WILL_YOU_TRANSFER;
	
	@ClientString(id = 538, message = "SP has decreased by $s1.")
	public static SystemMessageId SP_HAS_DECREASED_BY_S1;
	
	@ClientString(id = 539, message = "Experience has decreased by $s1.")
	public static SystemMessageId EXPERIENCE_HAS_DECREASED_BY_S1;
	
	@ClientString(id = 540, message = "Clan leaders cannot be deleted. DD Dissolve the clan and try again.")
	public static SystemMessageId CLAN_LEADERS_CANNOT_BE_DELETED_DD_DISSOLVE_THE_CLAN_AND_TRY_AGAIN;
	
	@ClientString(id = 541, message = "You cannot delete a clan member. Withdraw from the clan and try again.")
	public static SystemMessageId YOU_CANNOT_DELETE_A_CLAN_MEMBER_WITHDRAW_FROM_THE_CLAN_AND_TRY_AGAIN;
	
	@ClientString(id = 542, message = "NPC server not operating. Pets cannot be summoned.")
	public static SystemMessageId NPC_SERVER_NOT_OPERATING_PETS_CANNOT_BE_SUMMONED;
	
	@ClientString(id = 543, message = "You already have a pet.")
	public static SystemMessageId YOU_ALREADY_HAVE_A_PET;
	
	@ClientString(id = 544, message = "Item not available for pets.")
	public static SystemMessageId ITEM_NOT_AVAILABLE_FOR_PETS;
	
	@ClientString(id = 545, message = "Exceeded pet inventory's volume limit.")
	public static SystemMessageId EXCEEDED_PET_INVENTORY_S_VOLUME_LIMIT;
	
	@ClientString(id = 546, message = "Exceeded pet inventory's weight limit.")
	public static SystemMessageId EXCEEDED_PET_INVENTORY_S_WEIGHT_LIMIT;
	
	@ClientString(id = 547, message = "Summon a pet.")
	public static SystemMessageId SUMMON_A_PET;
	
	@ClientString(id = 548, message = "Your pet's name can be up to 8 characters.")
	public static SystemMessageId YOUR_PET_S_NAME_CAN_BE_UP_TO_8_CHARACTERS;
	
	@ClientString(id = 549, message = "To create an alliance, your clan must be Level 5 or higher.")
	public static SystemMessageId TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER;
	
	@ClientString(id = 550, message = "You cannot create an alliance during the term of dissolution postponement.")
	public static SystemMessageId YOU_CANNOT_CREATE_AN_ALLIANCE_DURING_THE_TERM_OF_DISSOLUTION_POSTPONEMENT;
	
	@ClientString(id = 551, message = "You cannot raise your clan level during the term of dispersion postponement.")
	public static SystemMessageId YOU_CANNOT_RAISE_YOUR_CLAN_LEVEL_DURING_THE_TERM_OF_DISPERSION_POSTPONEMENT;
	
	@ClientString(id = 552, message = "You cannot register a clan crest during the term of dispersion postponement.")
	public static SystemMessageId YOU_CANNOT_REGISTER_A_CLAN_CREST_DURING_THE_TERM_OF_DISPERSION_POSTPONEMENT;
	
	@ClientString(id = 553, message = "The opposing clan has applied for dispersion.")
	public static SystemMessageId THE_OPPOSING_CLAN_HAS_APPLIED_FOR_DISPERSION;
	
	@ClientString(id = 554, message = "You cannot disperse the clans in your alliance.")
	public static SystemMessageId YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE;
	
	@ClientString(id = 555, message = "You cannot move. Your item weight is too great.")
	public static SystemMessageId YOU_CANNOT_MOVE_YOUR_ITEM_WEIGHT_IS_TOO_GREAT;
	
	@ClientString(id = 556, message = "You cannot move in this state.")
	public static SystemMessageId YOU_CANNOT_MOVE_IN_THIS_STATE;
	
	@ClientString(id = 557, message = "The pet has been summoned and cannot be deleted.")
	public static SystemMessageId THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_DELETED;
	
	@ClientString(id = 558, message = "The pet has been summoned and cannot be let go.")
	public static SystemMessageId THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_LET_GO;
	
	@ClientString(id = 559, message = "Purchased $s2 from $s1.")
	public static SystemMessageId PURCHASED_S2_FROM_S1;
	
	@ClientString(id = 560, message = "Purchased +$s2$s3 from $s1 .")
	public static SystemMessageId PURCHASED_S2_S3_FROM_S1;
	
	@ClientString(id = 561, message = "Purchased $s3 $s2(s) from $s1 .")
	public static SystemMessageId PURCHASED_S3_S2_S_FROM_S1;
	
	@ClientString(id = 562, message = "Cannot crystallize. Crystallization skill level too low.")
	public static SystemMessageId CANNOT_CRYSTALLIZE_CRYSTALLIZATION_SKILL_LEVEL_TOO_LOW;
	
	@ClientString(id = 563, message = "Failed to disable attack target.")
	public static SystemMessageId FAILED_TO_DISABLE_ATTACK_TARGET;
	
	@ClientString(id = 564, message = "Failed to change attack target.")
	public static SystemMessageId FAILED_TO_CHANGE_ATTACK_TARGET;
	
	@ClientString(id = 565, message = "Not enough luck.")
	public static SystemMessageId NOT_ENOUGH_LUCK;
	
	@ClientString(id = 566, message = "Confusion failed.")
	public static SystemMessageId CONFUSION_FAILED;
	
	@ClientString(id = 567, message = "Fear failed.")
	public static SystemMessageId FEAR_FAILED;
	
	@ClientString(id = 568, message = "Cubic Summoning failed.")
	public static SystemMessageId CUBIC_SUMMONING_FAILED;
	
	@ClientString(id = 569, message = "Caution--the item price greatly differs from the shop's standard price. Do you wish to continue?")
	public static SystemMessageId CAUTION_THE_ITEM_PRICE_GREATLY_DIFFERS_FROM_THE_SHOP_S_STANDARD_PRICE_DO_YOU_WISH_TO_CONTINUE;
	
	@ClientString(id = 570, message = "How many $s1 (s) do you wish to purchase?")
	public static SystemMessageId HOW_MANY_S1_S_DO_YOU_WISH_TO_PURCHASE;
	
	@ClientString(id = 571, message = "How many $s1 (s) do you want to purchase?")
	public static SystemMessageId HOW_MANY_S1_S_DO_YOU_WANT_TO_PURCHASE;
	
	@ClientString(id = 572, message = "Do you wish to consent to $s1's party invitation? (Item distribution: possession of the looter)")
	public static SystemMessageId DO_YOU_WISH_TO_CONSENT_TO_S1_S_PARTY_INVITATION_ITEM_DISTRIBUTION_POSSESSION_OF_THE_LOOTER;
	
	@ClientString(id = 573, message = "Do you wish to consent to $s1's party invitation? (Item distribution: random distribution to party members)")
	public static SystemMessageId DO_YOU_WISH_TO_CONSENT_TO_S1_S_PARTY_INVITATION_ITEM_DISTRIBUTION_RANDOM_DISTRIBUTION_TO_PARTY_MEMBERS;
	
	@ClientString(id = 574, message = "No servitors or pets available.")
	public static SystemMessageId NO_SERVITORS_OR_PETS_AVAILABLE;
	
	@ClientString(id = 575, message = "How much adena do you wish to transfer to your pet?")
	public static SystemMessageId HOW_MUCH_ADENA_DO_YOU_WISH_TO_TRANSFER_TO_YOUR_PET;
	
	@ClientString(id = 576, message = "How much do you wish to transfer?")
	public static SystemMessageId HOW_MUCH_DO_YOU_WISH_TO_TRANSFER;
	
	@ClientString(id = 577, message = "You cannot summon during a trade or while using the private shops.")
	public static SystemMessageId YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS;
	
	@ClientString(id = 578, message = "You cannot summon during combat.")
	public static SystemMessageId YOU_CANNOT_SUMMON_DURING_COMBAT;
	
	@ClientString(id = 579, message = "A pet cannot be sent back during battle.")
	public static SystemMessageId A_PET_CANNOT_BE_SENT_BACK_DURING_BATTLE;
	
	@ClientString(id = 580, message = "You may not use multiple pets or servitors at the same time.")
	public static SystemMessageId YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME;
	
	@ClientString(id = 581, message = "There is a space in the name.")
	public static SystemMessageId THERE_IS_A_SPACE_IN_THE_NAME;
	
	@ClientString(id = 582, message = "Inappropriate character name.")
	public static SystemMessageId INAPPROPRIATE_CHARACTER_NAME;
	
	@ClientString(id = 583, message = "Name includes forbidden words.")
	public static SystemMessageId NAME_INCLUDES_FORBIDDEN_WORDS;
	
	@ClientString(id = 584, message = "Already in use by another pet.")
	public static SystemMessageId ALREADY_IN_USE_BY_ANOTHER_PET;
	
	@ClientString(id = 585, message = "Please decide on the price.")
	public static SystemMessageId PLEASE_DECIDE_ON_THE_PRICE;
	
	@ClientString(id = 586, message = "Pet items cannot be registered as shortcuts.")
	public static SystemMessageId PET_ITEMS_CANNOT_BE_REGISTERED_AS_SHORTCUTS;
	
	@ClientString(id = 587, message = "Irregular system speed.")
	public static SystemMessageId IRREGULAR_SYSTEM_SPEED;
	
	@ClientString(id = 588, message = "Pet inventory is full.")
	public static SystemMessageId PET_INVENTORY_IS_FULL;
	
	@ClientString(id = 589, message = "A dead pet cannot be sent back.")
	public static SystemMessageId A_DEAD_PET_CANNOT_BE_SENT_BACK;
	
	@ClientString(id = 590, message = "Cannot give items to a dead pet.")
	public static SystemMessageId CANNOT_GIVE_ITEMS_TO_A_DEAD_PET;
	
	@ClientString(id = 591, message = "An invalid character is included in the pet's name.")
	public static SystemMessageId AN_INVALID_CHARACTER_IS_INCLUDED_IN_THE_PET_S_NAME;
	
	@ClientString(id = 592, message = "Do you wish to dismiss your pet? Dismissing your pet will cause the pet necklace to disappear.")
	public static SystemMessageId DO_YOU_WISH_TO_DISMISS_YOUR_PET_DISMISSING_YOUR_PET_WILL_CAUSE_THE_PET_NECKLACE_TO_DISAPPEAR;
	
	@ClientString(id = 593, message = "Your pet has left due to unbearable hunger.")
	public static SystemMessageId YOUR_PET_HAS_LEFT_DUE_TO_UNBEARABLE_HUNGER;
	
	@ClientString(id = 594, message = "You cannot restore hungry pets.")
	public static SystemMessageId YOU_CANNOT_RESTORE_HUNGRY_PETS;
	
	@ClientString(id = 595, message = "Your pet is very hungry.")
	public static SystemMessageId YOUR_PET_IS_VERY_HUNGRY;
	
	@ClientString(id = 596, message = "Your pet ate a little, but is still hungry.")
	public static SystemMessageId YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY;
	
	@ClientString(id = 597, message = "Your pet is very hungry. Please be careful.")
	public static SystemMessageId YOUR_PET_IS_VERY_HUNGRY_PLEASE_BE_CAREFUL;
	
	@ClientString(id = 598, message = "You cannot chat while you are invisible.")
	public static SystemMessageId YOU_CANNOT_CHAT_WHILE_YOU_ARE_INVISIBLE;
	
	@ClientString(id = 599, message = "The GM has an important notice. Chat is temporarily aborted.")
	public static SystemMessageId THE_GM_HAS_AN_IMPORTANT_NOTICE_CHAT_IS_TEMPORARILY_ABORTED;
	
	@ClientString(id = 600, message = "You cannot equip a pet item.")
	public static SystemMessageId YOU_CANNOT_EQUIP_A_PET_ITEM;
	
	@ClientString(id = 601, message = "There are $s1 petitions pending.")
	public static SystemMessageId THERE_ARE_S1_PETITIONS_PENDING;
	
	@ClientString(id = 602, message = "The petition system is currently unavailable. Please try again later.")
	public static SystemMessageId THE_PETITION_SYSTEM_IS_CURRENTLY_UNAVAILABLE_PLEASE_TRY_AGAIN_LATER;
	
	@ClientString(id = 603, message = "That item cannot be discarded or exchanged.")
	public static SystemMessageId THAT_ITEM_CANNOT_BE_DISCARDED_OR_EXCHANGED;
	
	@ClientString(id = 604, message = "You may not call forth a pet or summoned creature from this location.")
	public static SystemMessageId YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION;
	
	@ClientString(id = 605, message = "You may register up to 64 people on your list.")
	public static SystemMessageId YOU_MAY_REGISTER_UP_TO_64_PEOPLE_ON_YOUR_LIST;
	
	@ClientString(id = 606, message = "You cannot be registered because the other person has already registered 64 people on his/her list.")
	public static SystemMessageId YOU_CANNOT_BE_REGISTERED_BECAUSE_THE_OTHER_PERSON_HAS_ALREADY_REGISTERED_64_PEOPLE_ON_HIS_HER_LIST;
	
	@ClientString(id = 607, message = "You do not have any further skills to learn. Come back when you have reached Level $s1.")
	public static SystemMessageId YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN_COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1;
	
	@ClientString(id = 608, message = "$s1 obtained $s3 $s2(s) by using Search.")
	public static SystemMessageId S1_OBTAINED_S3_S2_S_BY_USING_SEARCH;
	
	@ClientString(id = 609, message = "$s1 obtained $s2 by using Search.")
	public static SystemMessageId S1_OBTAINED_S2_BY_USING_SEARCH;
	
	@ClientString(id = 610, message = "Your skill has been cancelled due to lack of HP.")
	public static SystemMessageId YOUR_SKILL_HAS_BEEN_CANCELLED_DUE_TO_LACK_OF_HP;
	
	@ClientString(id = 611, message = "You have succeeded in Confusing the enemy.")
	public static SystemMessageId YOU_HAVE_SUCCEEDED_IN_CONFUSING_THE_ENEMY;
	
	@ClientString(id = 612, message = "The Spoil condition has been activated.")
	public static SystemMessageId THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED;
	
	@ClientString(id = 613, message = "======<Ignore List>======")
	public static SystemMessageId IGNORE_LIST;
	
	@ClientString(id = 614, message = "$s1 $s2")
	public static SystemMessageId S1_S2;
	
	@ClientString(id = 615, message = "You have failed to register the user to your Ignore List.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST;
	
	@ClientString(id = 616, message = "You have failed to delete the character.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_2;
	
	@ClientString(id = 617, message = "You have registered $s1 to your Ignore List.")
	public static SystemMessageId YOU_HAVE_REGISTERED_S1_TO_YOUR_IGNORE_LIST;
	
	@ClientString(id = 618, message = "You have removed $s1 from your Ignore List.")
	public static SystemMessageId YOU_HAVE_REMOVED_S1_FROM_YOUR_IGNORE_LIST;
	
	@ClientString(id = 619, message = "$s1 has placed you on his/her Ignore List.")
	public static SystemMessageId S1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST;
	
	@ClientString(id = 620, message = "$s1 has placed you on his/her Ignore List.")
	public static SystemMessageId S1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST_2;
	
	@ClientString(id = 621, message = "This server is reserved for players in Korea. To use Lineage II game services, please connect to the server in your region.")
	public static SystemMessageId THIS_SERVER_IS_RESERVED_FOR_PLAYERS_IN_KOREA_TO_USE_LINEAGE_II_GAME_SERVICES_PLEASE_CONNECT_TO_THE_SERVER_IN_YOUR_REGION;
	
	@ClientString(id = 622, message = "You may not make a declaration of war during an alliance battle.")
	public static SystemMessageId YOU_MAY_NOT_MAKE_A_DECLARATION_OF_WAR_DURING_AN_ALLIANCE_BATTLE;
	
	@ClientString(id = 623, message = "Your opponent has exceeded the number of simultaneous alliance battles allowed.")
	public static SystemMessageId YOUR_OPPONENT_HAS_EXCEEDED_THE_NUMBER_OF_SIMULTANEOUS_ALLIANCE_BATTLES_ALLOWED;
	
	@ClientString(id = 624, message = "$s1 Clan leader is not currently connected to the game server.")
	public static SystemMessageId S1_CLAN_LEADER_IS_NOT_CURRENTLY_CONNECTED_TO_THE_GAME_SERVER;
	
	@ClientString(id = 625, message = "Your request for Alliance Battle truce has been denied.")
	public static SystemMessageId YOUR_REQUEST_FOR_ALLIANCE_BATTLE_TRUCE_HAS_BEEN_DENIED;
	
	@ClientString(id = 626, message = "The $s1 clan did not respond: war proclamation has been refused.")
	public static SystemMessageId THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED_2;
	
	@ClientString(id = 627, message = "Clan battle has been refused because you did not respond to $s1 clan's war proclamation.")
	public static SystemMessageId CLAN_BATTLE_HAS_BEEN_REFUSED_BECAUSE_YOU_DID_NOT_RESPOND_TO_S1_CLAN_S_WAR_PROCLAMATION;
	
	@ClientString(id = 628, message = "You have already been at war with the $s1 clan: 5 days must pass before you can declare war again.")
	public static SystemMessageId YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN;
	
	@ClientString(id = 629, message = "Your opponent has exceeded the number of simultaneous alliance battles allowed.")
	public static SystemMessageId YOUR_OPPONENT_HAS_EXCEEDED_THE_NUMBER_OF_SIMULTANEOUS_ALLIANCE_BATTLES_ALLOWED_2;
	
	@ClientString(id = 630, message = "War with the $s1 clan has begun.")
	public static SystemMessageId WAR_WITH_THE_S1_CLAN_HAS_BEGUN_2;
	
	@ClientString(id = 631, message = "War with the $s1 clan is over.")
	public static SystemMessageId WAR_WITH_THE_S1_CLAN_IS_OVER;
	
	@ClientString(id = 632, message = "You have won the war over the $s1 clan!")
	public static SystemMessageId YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN_2;
	
	@ClientString(id = 633, message = "You have surrendered to the $s1 clan.")
	public static SystemMessageId YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN_2;
	
	@ClientString(id = 634, message = "Your alliance leader has been slain. You have been defeated by the $s1 clan.")
	public static SystemMessageId YOUR_ALLIANCE_LEADER_HAS_BEEN_SLAIN_YOU_HAVE_BEEN_DEFEATED_BY_THE_S1_CLAN;
	
	@ClientString(id = 635, message = "The time limit for the clan war has been exceeded. War with the $s1 clan is over.")
	public static SystemMessageId THE_TIME_LIMIT_FOR_THE_CLAN_WAR_HAS_BEEN_EXCEEDED_WAR_WITH_THE_S1_CLAN_IS_OVER;
	
	@ClientString(id = 636, message = "Not involved in clan war.")
	public static SystemMessageId NOT_INVOLVED_IN_CLAN_WAR_2;
	
	@ClientString(id = 637, message = "A clan ally has registered itself to the opponent.")
	public static SystemMessageId A_CLAN_ALLY_HAS_REGISTERED_ITSELF_TO_THE_OPPONENT;
	
	@ClientString(id = 638, message = "You have already requested a Siege Battle.")
	public static SystemMessageId YOU_HAVE_ALREADY_REQUESTED_A_SIEGE_BATTLE;
	
	@ClientString(id = 639, message = "Your application has been denied because you have already submitted a request for another Siege Battle.")
	public static SystemMessageId YOUR_APPLICATION_HAS_BEEN_DENIED_BECAUSE_YOU_HAVE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_SIEGE_BATTLE;
	
	@ClientString(id = 640, message = "You have failed to refuse castle defense aid.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_REFUSE_CASTLE_DEFENSE_AID;
	
	@ClientString(id = 641, message = "You have failed to approve castle defense aid.")
	public static SystemMessageId YOU_HAVE_FAILED_TO_APPROVE_CASTLE_DEFENSE_AID;
	
	@ClientString(id = 642, message = "You are already registered to the attacker side and must cancel your registration before submitting your request.")
	public static SystemMessageId YOU_ARE_ALREADY_REGISTERED_TO_THE_ATTACKER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST;
	
	@ClientString(id = 643, message = "You have already registered to the defender side and must cancel your registration before submitting your request.")
	public static SystemMessageId YOU_HAVE_ALREADY_REGISTERED_TO_THE_DEFENDER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST;
	
	@ClientString(id = 644, message = "You are not yet registered for the castle siege.")
	public static SystemMessageId YOU_ARE_NOT_YET_REGISTERED_FOR_THE_CASTLE_SIEGE;
	
	@ClientString(id = 645, message = "Only clans with Level 4 and higher may register for a castle siege.")
	public static SystemMessageId ONLY_CLANS_WITH_LEVEL_4_AND_HIGHER_MAY_REGISTER_FOR_A_CASTLE_SIEGE;
	
	@ClientString(id = 646, message = "You do not have the authority to modify the castle defender list.")
	public static SystemMessageId YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST;
	
	@ClientString(id = 647, message = "You do not have the authority to modify the siege time.")
	public static SystemMessageId YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME;
	
	@ClientString(id = 648, message = "No more registrations may be accepted for the attacker side.")
	public static SystemMessageId NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE;
	
	@ClientString(id = 649, message = "No more registrations may be accepted for the defender side.")
	public static SystemMessageId NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE;
	
	@ClientString(id = 650, message = "You may not summon from your current location.")
	public static SystemMessageId YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION;
	
	@ClientString(id = 651, message = "Place $s1 in the current location and direction. Do you wish to continue?")
	public static SystemMessageId PLACE_S1_IN_THE_CURRENT_LOCATION_AND_DIRECTION_DO_YOU_WISH_TO_CONTINUE;
	
	@ClientString(id = 652, message = "The target of the summoned monster is wrong.")
	public static SystemMessageId THE_TARGET_OF_THE_SUMMONED_MONSTER_IS_WRONG;
	
	@ClientString(id = 653, message = "You do not have the authority to position mercenaries.")
	public static SystemMessageId YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES;
	
	@ClientString(id = 654, message = "You do not have the authority to cancel mercenary positioning.")
	public static SystemMessageId YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING;
	
	@ClientString(id = 655, message = "This is not the castle to which it is affiliated and so the mercenary cannot be positioned.")
	public static SystemMessageId THIS_IS_NOT_THE_CASTLE_TO_WHICH_IT_IS_AFFILIATED_AND_SO_THE_MERCENARY_CANNOT_BE_POSITIONED;
	
	@ClientString(id = 656, message = "This mercenary cannot be positioned anymore.")
	public static SystemMessageId THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE;
	
	@ClientString(id = 657, message = "Positioning cannot be done here because the distance between mercenaries is too short.")
	public static SystemMessageId POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT;
	
	@ClientString(id = 658, message = "This is not a mercenary of a castle that you own and so you cannot cancel its positioning.")
	public static SystemMessageId THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_YOU_CANNOT_CANCEL_ITS_POSITIONING;
	
	@ClientString(id = 659, message = "This is not the time for siege registration and so registrations cannot be accepted or rejected.")
	public static SystemMessageId THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPTED_OR_REJECTED;
	
	@ClientString(id = 660, message = "This is not the time for siege registration and so registration and cancellation cannot be done.")
	public static SystemMessageId THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE;
	
	@ClientString(id = 661, message = "It is a character that cannot be spoiled.")
	public static SystemMessageId IT_IS_A_CHARACTER_THAT_CANNOT_BE_SPOILED;
	
	@ClientString(id = 662, message = "The other player is rejecting friend invitations.")
	public static SystemMessageId THE_OTHER_PLAYER_IS_REJECTING_FRIEND_INVITATIONS;
	
	@ClientString(id = 663, message = "The siege time has been declared for $s. It is not possible to change the time after a siege time has been declared. Do you want to continue?")
	public static SystemMessageId THE_SIEGE_TIME_HAS_BEEN_DECLARED_FOR_S_IT_IS_NOT_POSSIBLE_TO_CHANGE_THE_TIME_AFTER_A_SIEGE_TIME_HAS_BEEN_DECLARED_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 664, message = "Please choose a person to receive.")
	public static SystemMessageId PLEASE_CHOOSE_A_PERSON_TO_RECEIVE;
	
	@ClientString(id = 665, message = "$s2% of $s1 alliance is applying for alliance war. Do you want to accept the challenge?")
	public static SystemMessageId S2_OF_S1_ALLIANCE_IS_APPLYING_FOR_ALLIANCE_WAR_DO_YOU_WANT_TO_ACCEPT_THE_CHALLENGE;
	
	@ClientString(id = 666, message = "A request for ceasefire has been received from $s1 alliance. Do you want to agree to the cease-fire?")
	public static SystemMessageId A_REQUEST_FOR_CEASEFIRE_HAS_BEEN_RECEIVED_FROM_S1_ALLIANCE_DO_YOU_WANT_TO_AGREE_TO_THE_CEASE_FIRE;
	
	@ClientString(id = 667, message = "You are registering on the attacking side of the $s1 siege. Do you want to continue?")
	public static SystemMessageId YOU_ARE_REGISTERING_ON_THE_ATTACKING_SIDE_OF_THE_S1_SIEGE_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 668, message = "You are registering on the defending side of the $s1 siege. Do you want to continue?")
	public static SystemMessageId YOU_ARE_REGISTERING_ON_THE_DEFENDING_SIDE_OF_THE_S1_SIEGE_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 669, message = "You are canceling your application to participate in the $s1 siege battle. Do you want to continue?")
	public static SystemMessageId YOU_ARE_CANCELING_YOUR_APPLICATION_TO_PARTICIPATE_IN_THE_S1_SIEGE_BATTLE_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 670, message = "You are refusing the registration of $s1 clan on the defending side. Do you want to continue?")
	public static SystemMessageId YOU_ARE_REFUSING_THE_REGISTRATION_OF_S1_CLAN_ON_THE_DEFENDING_SIDE_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 671, message = "You are agreeing to the registration of $s1 clan on the defending side. Do you want to continue?")
	public static SystemMessageId YOU_ARE_AGREEING_TO_THE_REGISTRATION_OF_S1_CLAN_ON_THE_DEFENDING_SIDE_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 672, message = "$s1 adena disappeared.")
	public static SystemMessageId S1_ADENA_DISAPPEARED;
	
	@ClientString(id = 673, message = "You do not have the authority to participate in an auction.")
	public static SystemMessageId YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_PARTICIPATE_IN_AN_AUCTION;
	
	@ClientString(id = 674, message = "It has not yet been seven days since canceling an auction.")
	public static SystemMessageId IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION;
	
	@ClientString(id = 675, message = "There are no clan halls up for auction.")
	public static SystemMessageId THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION;
	
	@ClientString(id = 676, message = "You have already submitted a bid.")
	public static SystemMessageId YOU_HAVE_ALREADY_SUBMITTED_A_BID;
	
	@ClientString(id = 677, message = "Your bid price must be higher than the minimum price that can be bid.")
	public static SystemMessageId YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_THAT_CAN_BE_BID;
	
	@ClientString(id = 678, message = "You have submitted a bid in the auction of $s1.")
	public static SystemMessageId YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1;
	
	@ClientString(id = 679, message = "You have canceled your bid.")
	public static SystemMessageId YOU_HAVE_CANCELED_YOUR_BID;
	
	@ClientString(id = 680, message = "You cannot participate in an auction.")
	public static SystemMessageId YOU_CANNOT_PARTICIPATE_IN_AN_AUCTION;
	
	@ClientString(id = 681, message = "The clan does not own a clan hall.")
	public static SystemMessageId THE_CLAN_DOES_NOT_OWN_A_CLAN_HALL;
	
	@ClientString(id = 682, message = "You are moving to another village. Do you want to continue?")
	public static SystemMessageId YOU_ARE_MOVING_TO_ANOTHER_VILLAGE_DO_YOU_WANT_TO_CONTINUE;
	
	@ClientString(id = 683, message = "There are no priority rights on a sweeper.")
	public static SystemMessageId THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER;
	
	@ClientString(id = 684, message = "You cannot position mercenaries during a siege.")
	public static SystemMessageId YOU_CANNOT_POSITION_MERCENARIES_DURING_A_SIEGE;
	
	@ClientString(id = 685, message = "You cannot apply for clan war with a clan that belongs to the same alliance.")
	public static SystemMessageId YOU_CANNOT_APPLY_FOR_CLAN_WAR_WITH_A_CLAN_THAT_BELONGS_TO_THE_SAME_ALLIANCE;
	
	@ClientString(id = 686, message = "You have received $s1 damage from the fire of magic.")
	public static SystemMessageId YOU_HAVE_RECEIVED_S1_DAMAGE_FROM_THE_FIRE_OF_MAGIC;
	
	@ClientString(id = 687, message = "You cannot move in a frozen state. Please wait a moment.")
	public static SystemMessageId YOU_CANNOT_MOVE_IN_A_FROZEN_STATE_PLEASE_WAIT_A_MOMENT;
	
	@ClientString(id = 688, message = "The clan that owns the castle is automatically registered on the defending side.")
	public static SystemMessageId THE_CLAN_THAT_OWNS_THE_CASTLE_IS_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE;
	
	@ClientString(id = 689, message = "A clan that owns a castle cannot participate in another siege.")
	public static SystemMessageId A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE;
	
	@ClientString(id = 690, message = "You cannot register on the attacking side because you are part of an alliance with the clan that owns the castle.")
	public static SystemMessageId YOU_CANNOT_REGISTER_ON_THE_ATTACKING_SIDE_BECAUSE_YOU_ARE_PART_OF_AN_ALLIANCE_WITH_THE_CLAN_THAT_OWNS_THE_CASTLE;
	
	@ClientString(id = 691, message = "$s1 clan is already a member of $s2 alliance.")
	public static SystemMessageId S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE;
	
	@ClientString(id = 692, message = "The other party is frozen. Please wait a moment.")
	public static SystemMessageId THE_OTHER_PARTY_IS_FROZEN_PLEASE_WAIT_A_MOMENT;
	
	@ClientString(id = 693, message = "The package that arrived is in another warehouse.")
	public static SystemMessageId THE_PACKAGE_THAT_ARRIVED_IS_IN_ANOTHER_WAREHOUSE;
	
	@ClientString(id = 694, message = "No packages have arrived.")
	public static SystemMessageId NO_PACKAGES_HAVE_ARRIVED;
	
	@ClientString(id = 695, message = "You cannot set the name of the pet.")
	public static SystemMessageId YOU_CANNOT_SET_THE_NAME_OF_THE_PET;
	
	@ClientString(id = 696, message = "Your account is restricted for not paying your PC room usage fees.")
	public static SystemMessageId YOUR_ACCOUNT_IS_RESTRICTED_FOR_NOT_PAYING_YOUR_PC_ROOM_USAGE_FEES;
	
	@ClientString(id = 697, message = "The item enchant value is strange.")
	public static SystemMessageId THE_ITEM_ENCHANT_VALUE_IS_STRANGE;
	
	@ClientString(id = 698, message = "The price is different than the same item on the sales list.")
	public static SystemMessageId THE_PRICE_IS_DIFFERENT_THAN_THE_SAME_ITEM_ON_THE_SALES_LIST;
	
	@ClientString(id = 699, message = "Currently not purchasing.")
	public static SystemMessageId CURRENTLY_NOT_PURCHASING;
	
	@ClientString(id = 700, message = "The purchase is complete.")
	public static SystemMessageId THE_PURCHASE_IS_COMPLETE;
	
	@ClientString(id = 701, message = "You do not have enough required items.")
	public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS;
	
	@ClientString(id = 702, message = "There are not any GMs that are providing customer service currently.")
	public static SystemMessageId THERE_ARE_NOT_ANY_GMS_THAT_ARE_PROVIDING_CUSTOMER_SERVICE_CURRENTLY;
	
	@ClientString(id = 703, message = "======<GM List>======")
	public static SystemMessageId GM_LIST;
	
	@ClientString(id = 704, message = "GM : $s1")
	public static SystemMessageId GM_S1;
	
	@ClientString(id = 705, message = "You cannot exclude yourself.")
	public static SystemMessageId YOU_CANNOT_EXCLUDE_YOURSELF;
	
	@ClientString(id = 706, message = "You can only register up to 64 names on your exclude list.")
	public static SystemMessageId YOU_CAN_ONLY_REGISTER_UP_TO_64_NAMES_ON_YOUR_EXCLUDE_LIST;
	
	@ClientString(id = 707, message = "You cannot teleport to a village that is in a siege.")
	public static SystemMessageId YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE;
	
	@ClientString(id = 708, message = "You do not have the right to use the castle warehouse.")
	public static SystemMessageId YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CASTLE_WAREHOUSE;
	
	@ClientString(id = 709, message = "You do not have the right to use the clan warehouse.")
	public static SystemMessageId YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE;
	
	@ClientString(id = 710, message = "Only clans of clan level 1 or higher can use a clan warehouse.")
	public static SystemMessageId ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE;
	
	@ClientString(id = 711, message = "The siege of $s1 has started.")
	public static SystemMessageId THE_SIEGE_OF_S1_HAS_STARTED;
	
	@ClientString(id = 712, message = "The siege of $s1 has finished.")
	public static SystemMessageId THE_SIEGE_OF_S1_HAS_FINISHED;
	
	@ClientString(id = 713, message = "$s1/$s2/$s3 $s4:$s5")
	public static SystemMessageId S1_S2_S3_S4_S5;
	
	@ClientString(id = 714, message = "A trap device has tripped.")
	public static SystemMessageId A_TRAP_DEVICE_HAS_TRIPPED;
	
	@ClientString(id = 715, message = "The trap device has stopped.")
	public static SystemMessageId THE_TRAP_DEVICE_HAS_STOPPED;
	
	@ClientString(id = 716, message = "The base camp has been destroyed and resurrection is not possible.")
	public static SystemMessageId THE_BASE_CAMP_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE;
	
	@ClientString(id = 717, message = "The guardian tower has been destroyed and resurrection is not possible.")
	public static SystemMessageId THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE;
	
	@ClientString(id = 718, message = "The castle gates cannot be opened and closed during a siege.")
	public static SystemMessageId THE_CASTLE_GATES_CANNOT_BE_OPENED_AND_CLOSED_DURING_A_SIEGE;
	
	@ClientString(id = 719, message = "You failed at item mixing.")
	public static SystemMessageId YOU_FAILED_AT_ITEM_MIXING;
	
	@ClientString(id = 720, message = "The purchase price is higher than the amount of money that you have and so you cannot open a personal store.")
	public static SystemMessageId THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE;
	
	@ClientString(id = 721, message = "You cannot create an alliance while participating in a siege.")
	public static SystemMessageId YOU_CANNOT_CREATE_AN_ALLIANCE_WHILE_PARTICIPATING_IN_A_SIEGE;
	
	@ClientString(id = 722, message = "You cannot dissolve an alliance while an affiliated clan is participating in a siege battle.")
	public static SystemMessageId YOU_CANNOT_DISSOLVE_AN_ALLIANCE_WHILE_AN_AFFILIATED_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE;
	
	@ClientString(id = 723, message = "The opposing clan is participating in a siege battle.")
	public static SystemMessageId THE_OPPOSING_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE;
	
	@ClientString(id = 724, message = "You cannot leave while participating in a siege battle.")
	public static SystemMessageId YOU_CANNOT_LEAVE_WHILE_PARTICIPATING_IN_A_SIEGE_BATTLE;
	
	@ClientString(id = 725, message = "You cannot banish a clan from an alliance while the clan is participating in a siege.")
	public static SystemMessageId YOU_CANNOT_BANISH_A_CLAN_FROM_AN_ALLIANCE_WHILE_THE_CLAN_IS_PARTICIPATING_IN_A_SIEGE;
	
	@ClientString(id = 726, message = "The frozen condition has started. Please wait a moment.")
	public static SystemMessageId THE_FROZEN_CONDITION_HAS_STARTED_PLEASE_WAIT_A_MOMENT;
	
	@ClientString(id = 727, message = "The frozen condition was removed.")
	public static SystemMessageId THE_FROZEN_CONDITION_WAS_REMOVED;
	
	@ClientString(id = 728, message = "You cannot apply for dissolution again within seven days after a previous application for dissolution.")
	public static SystemMessageId YOU_CANNOT_APPLY_FOR_DISSOLUTION_AGAIN_WITHIN_SEVEN_DAYS_AFTER_A_PREVIOUS_APPLICATION_FOR_DISSOLUTION;
	
	@ClientString(id = 729, message = "That item cannot be discarded.")
	public static SystemMessageId THAT_ITEM_CANNOT_BE_DISCARDED;
	
	@ClientString(id = 730, message = "You have submitted $s1 petitions. You may submit $s2 more petitions today.")
	public static SystemMessageId YOU_HAVE_SUBMITTED_S1_PETITIONS_YOU_MAY_SUBMIT_S2_MORE_PETITIONS_TODAY;
	
	@ClientString(id = 731, message = "A petition has been received by the GM on behalf of $s1. It is petition #$s2.")
	public static SystemMessageId A_PETITION_HAS_BEEN_RECEIVED_BY_THE_GM_ON_BEHALF_OF_S1_IT_IS_PETITION_S2;
	
	@ClientString(id = 732, message = "$s1 has received a request for a consultation with the GM.")
	public static SystemMessageId S1_HAS_RECEIVED_A_REQUEST_FOR_A_CONSULTATION_WITH_THE_GM;
	
	@ClientString(id = 733, message = "We have received $s1 petitions from you today and that is the maximum that you can submit in one day. You cannot submit any more petitions.")
	public static SystemMessageId WE_HAVE_RECEIVED_S1_PETITIONS_FROM_YOU_TODAY_AND_THAT_IS_THE_MAXIMUM_THAT_YOU_CAN_SUBMIT_IN_ONE_DAY_YOU_CANNOT_SUBMIT_ANY_MORE_PETITIONS;
	
	@ClientString(id = 734, message = "You failed at submitting a petition on behalf of someone else. $s1 already submitted a petition.")
	public static SystemMessageId YOU_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_SOMEONE_ELSE_S1_ALREADY_SUBMITTED_A_PETITION;
	
	@ClientString(id = 735, message = "You failed at submitting a petition on behalf of $s1. The error is #$s2.")
	public static SystemMessageId YOU_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_S1_THE_ERROR_IS_S2;
	
	@ClientString(id = 736, message = "The petition was canceled. You may submit $s1 more petitions today.")
	public static SystemMessageId THE_PETITION_WAS_CANCELED_YOU_MAY_SUBMIT_S1_MORE_PETITIONS_TODAY;
	
	@ClientString(id = 737, message = "You failed at submitting a petition on behalf of $s1.")
	public static SystemMessageId YOU_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_S1;
	
	@ClientString(id = 738, message = "You have not submitted a petition.")
	public static SystemMessageId YOU_HAVE_NOT_SUBMITTED_A_PETITION;
	
	@ClientString(id = 739, message = "You failed at canceling a petition on behalf of $s1. The error code is $s2.")
	public static SystemMessageId YOU_FAILED_AT_CANCELING_A_PETITION_ON_BEHALF_OF_S1_THE_ERROR_CODE_IS_S2;
	
	@ClientString(id = 740, message = "$s1 participated in a petition chat at the request of the GM.")
	public static SystemMessageId S1_PARTICIPATED_IN_A_PETITION_CHAT_AT_THE_REQUEST_OF_THE_GM;
	
	@ClientString(id = 741, message = "You failed at adding $s1 to the petition chat. A petition has already been submitted.")
	public static SystemMessageId YOU_FAILED_AT_ADDING_S1_TO_THE_PETITION_CHAT_A_PETITION_HAS_ALREADY_BEEN_SUBMITTED;
	
	@ClientString(id = 742, message = "You failed at adding $s1 to the petition chat. The error code is $s2.")
	public static SystemMessageId YOU_FAILED_AT_ADDING_S1_TO_THE_PETITION_CHAT_THE_ERROR_CODE_IS_S2;
	
	@ClientString(id = 743, message = "$s1 left the petition chat.")
	public static SystemMessageId S1_LEFT_THE_PETITION_CHAT;
	
	@ClientString(id = 744, message = "You failed at removing $s1 from the petition chat. The error code is $s2.")
	public static SystemMessageId YOU_FAILED_AT_REMOVING_S1_FROM_THE_PETITION_CHAT_THE_ERROR_CODE_IS_S2;
	
	@ClientString(id = 745, message = "You are currently not in a petition chat.")
	public static SystemMessageId YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT;
	
	@ClientString(id = 746, message = "It is not currently a petition.")
	public static SystemMessageId IT_IS_NOT_CURRENTLY_A_PETITION;
	
	@ClientString(id = 747, message = "If you need help, please use 1:1 Inquiry on the official web site.")
	public static SystemMessageId IF_YOU_NEED_HELP_PLEASE_USE_1_1_INQUIRY_ON_THE_OFFICIAL_WEB_SITE;
	
	@ClientString(id = 748, message = "The distance is too far and so the casting has been stopped.")
	public static SystemMessageId THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED;
	
	@ClientString(id = 749, message = "The effect of $s1 has been removed.")
	public static SystemMessageId THE_EFFECT_OF_S1_HAS_BEEN_REMOVED;
	
	@ClientString(id = 750, message = "There are no other skills to learn.")
	public static SystemMessageId THERE_ARE_NO_OTHER_SKILLS_TO_LEARN;
	
	@ClientString(id = 751, message = "As there is a conflict in the siege relationship with a clan in the alliance, you cannot invite that clan to the alliance.")
	public static SystemMessageId AS_THERE_IS_A_CONFLICT_IN_THE_SIEGE_RELATIONSHIP_WITH_A_CLAN_IN_THE_ALLIANCE_YOU_CANNOT_INVITE_THAT_CLAN_TO_THE_ALLIANCE;
	
	@ClientString(id = 752, message = "That name cannot be used.")
	public static SystemMessageId THAT_NAME_CANNOT_BE_USED;
	
	@ClientString(id = 753, message = "You cannot position mercenaries here.")
	public static SystemMessageId YOU_CANNOT_POSITION_MERCENARIES_HERE;
	
	@ClientString(id = 754, message = "There are $s1 hours and $s2 minutes left in this week's usage time.")
	public static SystemMessageId THERE_ARE_S1_HOURS_AND_S2_MINUTES_LEFT_IN_THIS_WEEK_S_USAGE_TIME;
	
	@ClientString(id = 755, message = "There are $s1 minutes left in this week's usage time.")
	public static SystemMessageId THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEK_S_USAGE_TIME;
	
	@ClientString(id = 756, message = "This week's usage time has finished.")
	public static SystemMessageId THIS_WEEK_S_USAGE_TIME_HAS_FINISHED;
	
	@ClientString(id = 757, message = "There are $s1 hours and $s2 minutes left in the fixed use time.")
	public static SystemMessageId THERE_ARE_S1_HOURS_AND_S2_MINUTES_LEFT_IN_THE_FIXED_USE_TIME;
	
	@ClientString(id = 758, message = "There are $s1 minutes left in this week's play time.")
	public static SystemMessageId THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEK_S_PLAY_TIME;
	
	@ClientString(id = 759, message = "There are $s1 minutes left in this week's play time.")
	public static SystemMessageId THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEK_S_PLAY_TIME_2;
	
	@ClientString(id = 760, message = "$s1% cannot join the clan because five days have not yet passed since he/she left another clan.")
	public static SystemMessageId S1_CANNOT_JOIN_THE_CLAN_BECAUSE_FIVE_DAYS_HAVE_NOT_YET_PASSED_SINCE_HE_SHE_LEFT_ANOTHER_CLAN;
	
	@ClientString(id = 761, message = "$s1% clan cannot join the alliance because one day has not yet passed since it left another alliance.")
	public static SystemMessageId S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_IT_LEFT_ANOTHER_ALLIANCE;
	
	@ClientString(id = 762, message = "$s1% rolled $s2% and $s3's eye came out.")
	public static SystemMessageId S1_ROLLED_S2_AND_S3_S_EYE_CAME_OUT;
	
	@ClientString(id = 763, message = "You failed at sending the package because you are too far from the warehouse.")
	public static SystemMessageId YOU_FAILED_AT_SENDING_THE_PACKAGE_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_WAREHOUSE;
	
	@ClientString(id = 764, message = "You have played for $s1 hours. For your health, take a rest before playing again.")
	public static SystemMessageId YOU_HAVE_PLAYED_FOR_S1_HOURS_FOR_YOUR_HEALTH_TAKE_A_REST_BEFORE_PLAYING_AGAIN;
	
	@ClientString(id = 765, message = "GameGuard is already running. Please try running it again after rebooting.")
	public static SystemMessageId GAMEGUARD_IS_ALREADY_RUNNING_PLEASE_TRY_RUNNING_IT_AGAIN_AFTER_REBOOTING;
	
	@ClientString(id = 766, message = "There is a GameGuard initialization error. Please try running it again after rebooting.")
	public static SystemMessageId THERE_IS_A_GAMEGUARD_INITIALIZATION_ERROR_PLEASE_TRY_RUNNING_IT_AGAIN_AFTER_REBOOTING;
	
	@ClientString(id = 767, message = "The GameGuard file is damaged . Please reinstall GameGuard.")
	public static SystemMessageId THE_GAMEGUARD_FILE_IS_DAMAGED_PLEASE_REINSTALL_GAMEGUARD;
	
	@ClientString(id = 768, message = "A Windows system file is damaged. Please reinstall Internet Explorer.")
	public static SystemMessageId A_WINDOWS_SYSTEM_FILE_IS_DAMAGED_PLEASE_REINSTALL_INTERNET_EXPLORER;
	
	@ClientString(id = 769, message = "A hacking tool has been discovered. Please try playing again after closing unnecessary programs.")
	public static SystemMessageId A_HACKING_TOOL_HAS_BEEN_DISCOVERED_PLEASE_TRY_PLAYING_AGAIN_AFTER_CLOSING_UNNECESSARY_PROGRAMS;
	
	@ClientString(id = 770, message = "The GameGuard update was canceled. Please check your network connection status or firewall.")
	public static SystemMessageId THE_GAMEGUARD_UPDATE_WAS_CANCELED_PLEASE_CHECK_YOUR_NETWORK_CONNECTION_STATUS_OR_FIREWALL;
	
	@ClientString(id = 771, message = "The GameGuard update was canceled. Please try running it again after doing a virus scan or changing the settings in your PC management program.")
	public static SystemMessageId THE_GAMEGUARD_UPDATE_WAS_CANCELED_PLEASE_TRY_RUNNING_IT_AGAIN_AFTER_DOING_A_VIRUS_SCAN_OR_CHANGING_THE_SETTINGS_IN_YOUR_PC_MANAGEMENT_PROGRAM;
	
	@ClientString(id = 772, message = "There was a problem when running GameGuard.")
	public static SystemMessageId THERE_WAS_A_PROBLEM_WHEN_RUNNING_GAMEGUARD;
	
	@ClientString(id = 773, message = "The game or GameGuard files are damaged.")
	public static SystemMessageId THE_GAME_OR_GAMEGUARD_FILES_ARE_DAMAGED;
	
	@ClientString(id = 774, message = "Since this is a peace zone, play time does not get expended here.")
	public static SystemMessageId SINCE_THIS_IS_A_PEACE_ZONE_PLAY_TIME_DOES_NOT_GET_EXPENDED_HERE;
	
	@ClientString(id = 775, message = "From here on, play time will be expended.")
	public static SystemMessageId FROM_HERE_ON_PLAY_TIME_WILL_BE_EXPENDED;
	
	@ClientString(id = 776, message = "The clan hall which was put up for auction has been awarded to $s1 clan.")
	public static SystemMessageId THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN;
	
	@ClientString(id = 777, message = "The clan hall which had been put up for auction was not sold and therefore has been re-listed.")
	public static SystemMessageId THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RE_LISTED;
	
	static
	{
		buildFastLookupTable();
	}
	
	private static void buildFastLookupTable()
	{
		for (Field field : SystemMessageId.class.getDeclaredFields())
		{
			final int mod = field.getModifiers();
			if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && field.getType().equals(SystemMessageId.class) && field.isAnnotationPresent(ClientString.class))
			{
				try
				{
					final ClientString annotation = field.getAnnotationsByType(ClientString.class)[0];
					final SystemMessageId smId = new SystemMessageId(annotation.id());
					smId.setName(annotation.message());
					smId.setParamCount(parseMessageParameters(field.getName()));
					field.set(null, smId);
					VALUES.put(smId.getId(), smId);
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "SystemMessageId: Failed field access for '" + field.getName() + "'", e);
				}
			}
		}
	}
	
	private static int parseMessageParameters(String name)
	{
		int paramCount = 0;
		char c1;
		char c2;
		for (int i = 0; i < (name.length() - 1); i++)
		{
			c1 = name.charAt(i);
			if ((c1 == 'C') || (c1 == 'S'))
			{
				c2 = name.charAt(i + 1);
				if (Character.isDigit(c2))
				{
					paramCount = Math.max(paramCount, Character.getNumericValue(c2));
					i++;
				}
			}
		}
		
		return paramCount;
	}
	
	public static SystemMessageId getSystemMessageId(int id)
	{
		final SystemMessageId smi = getSystemMessageIdInternal(id);
		return smi == null ? new SystemMessageId(id) : smi;
	}
	
	private static SystemMessageId getSystemMessageIdInternal(int id)
	{
		return VALUES.get(id);
	}
	
	public static SystemMessageId getSystemMessageId(String name)
	{
		try
		{
			return (SystemMessageId) SystemMessageId.class.getField(name).get(null);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public static void loadLocalisations()
	{
		for (SystemMessageId smId : VALUES.values())
		{
			if (smId != null)
			{
				smId.removeAllLocalisations();
			}
		}
		
		if (!MultilingualSupportConfig.MULTILANG_ENABLE)
		{
			LOGGER.log(Level.INFO, "SystemMessageId: MultiLanguage disabled.");
			return;
		}
		
		final List<String> languages = MultilingualSupportConfig.MULTILANG_ALLOWED;
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		
		File file;
		Node node;
		Document document;
		NamedNodeMap nnmb;
		SystemMessageId smId;
		String text;
		for (String lang : languages)
		{
			file = new File("data/lang/" + lang + "/SystemMessageLocalisation.xml");
			if (!file.isFile())
			{
				continue;
			}
			
			try
			{
				document = factory.newDocumentBuilder().parse(file);
				for (Node na = document.getFirstChild(); na != null; na = na.getNextSibling())
				{
					if ("list".equals(na.getNodeName()))
					{
						for (Node nb = na.getFirstChild(); nb != null; nb = nb.getNextSibling())
						{
							if ("localisation".equals(nb.getNodeName()))
							{
								nnmb = nb.getAttributes();
								node = nnmb.getNamedItem("id");
								if (node != null)
								{
									smId = getSystemMessageId(Integer.parseInt(node.getNodeValue()));
									if (smId == null)
									{
										LOGGER.log(Level.WARNING, "SystemMessageId: Unknown SMID '" + node.getNodeValue() + "', lang '" + lang + "'.");
										continue;
									}
									
									node = nnmb.getNamedItem("translation");
									if (node == null)
									{
										LOGGER.log(Level.WARNING, "SystemMessageId: No text defined for SMID '" + smId + "', lang '" + lang + "'.");
										continue;
									}
									
									text = node.getNodeValue();
									if (text.isEmpty() || (text.length() > 255))
									{
										LOGGER.log(Level.WARNING, "SystemMessageId: Invalid text defined for SMID '" + smId + "' (to long or empty), lang '" + lang + "'.");
										continue;
									}
									
									smId.attachLocalizedText(lang, text);
								}
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "SystemMessageId: Failed loading '" + file + "'", e);
			}
			
			LOGGER.log(Level.INFO, "SystemMessageId: Loaded localisations for [" + lang + "].");
		}
	}
	
	private final int _id;
	private String _name;
	private byte _params;
	private SMLocalisation[] _localisations;
	private SystemMessage _staticSystemMessage;
	
	private SystemMessageId(int id)
	{
		_id = id;
		_localisations = EMPTY_SML_ARRAY;
	}
	
	public int getId()
	{
		return _id;
	}
	
	private void setName(String name)
	{
		_name = name;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getParamCount()
	{
		return _params;
	}
	
	public void setParamCount(int params)
	{
		if (params < 0)
		{
			throw new IllegalArgumentException("Invalid negative param count: " + params);
		}
		
		if (params > 10)
		{
			throw new IllegalArgumentException("Maximum param count exceeded: " + params);
		}
		
		if (params != 0)
		{
			_staticSystemMessage = null;
		}
		
		_params = (byte) params;
	}
	
	public SMLocalisation getLocalisation(String lang)
	{
		if (_localisations == null)
		{
			return null;
		}
		
		SMLocalisation sml;
		for (int i = _localisations.length; i-- > 0;)
		{
			sml = _localisations[i];
			if (sml.getLanguage().hashCode() == lang.hashCode())
			{
				return sml;
			}
		}
		
		return null;
	}
	
	public void attachLocalizedText(String lang, String text)
	{
		final int length = _localisations.length;
		final SMLocalisation[] localisations = Arrays.copyOf(_localisations, length + 1);
		localisations[length] = new SMLocalisation(lang, text);
		_localisations = localisations;
	}
	
	public void removeAllLocalisations()
	{
		_localisations = EMPTY_SML_ARRAY;
	}
	
	public SystemMessage getStaticSystemMessage()
	{
		return _staticSystemMessage;
	}
	
	public void setStaticSystemMessage(SystemMessage sm)
	{
		_staticSystemMessage = sm;
	}
	
	@Override
	public String toString()
	{
		return "SM[" + getId() + ": " + getName() + "]";
	}
	
	public static class SMLocalisation
	{
		private final String _lang;
		private final Builder _builder;
		
		public SMLocalisation(String lang, String text)
		{
			_lang = lang;
			_builder = Builder.newBuilder(text);
		}
		
		public String getLanguage()
		{
			return _lang;
		}
		
		public String getLocalisation(Object... params)
		{
			return _builder.toString(params);
		}
	}
}
