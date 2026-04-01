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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.gameserver.config.GeneralConfig;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.BlockList;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.groups.PartyDistributionType;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.AskJoinParty;
import org.l2jmobius.gameserver.network.serverpackets.JoinParty;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Mobius
 */
public class RequestJoinParty extends ClientPacket
{
	private int _objectId;
	private int _partyDistributionTypeId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		_partyDistributionTypeId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player requestor = getPlayer();
		if (requestor == null)
		{
			return;
		}
		
		final Player target = World.getInstance().getPlayer(_objectId);
		if (target == null)
		{
			requestor.sendPacket(SystemMessageId.SELECT_USER_TO_INVITE_TO_YOUR_PARTY);
			return;
		}
		
		if ((target.getClient() == null) || target.getClient().isDetached())
		{
			requestor.sendMessage("Player is in offline mode.");
			return;
		}
		
		if (requestor.isPartyBanned())
		{
			requestor.sendMessage("You have been reported as an illegal program user, so participating in a party is not allowed.");
			requestor.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (target.isPartyBanned())
		{
			requestor.sendMessage(target.getName() + " has been reported as an illegal program user and cannot join a party.");
			return;
		}
		
		if (!target.isVisibleFor(requestor))
		{
			requestor.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			return;
		}
		
		if (requestor.isRegisteredOnEvent() || target.isRegisteredOnEvent())
		{
			if (GeneralConfig.ALLOW_PARTY_IN_SAME_EVENT)
			{
				if (!((requestor.getInstanceId() == target.getInstanceId()) && requestor.isRegisteredOnEvent() && target.isRegisteredOnEvent()))
				{
					requestor.sendMessage("Event paticipants cannot be invited to parties.");
					return;
				}
				
				if (!requestor.getTeam().equals(target.getTeam()))
				{
					requestor.sendMessage("You cannot be invited to a party of another team.");
					return;
				}
			}
			else
			{
				requestor.sendMessage("Event paticipants cannot be invited to parties.");
				return;
			}
		}
		
		SystemMessage sm;
		if (target.isInParty())
		{
			sm = new SystemMessage(SystemMessageId.S1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED);
			sm.addString(target.getName());
			requestor.sendPacket(sm);
			return;
		}
		
		if (BlockList.isBlocked(target, requestor))
		{
			sm = new SystemMessage(SystemMessageId.S1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST);
			sm.addString(target.getName());
			requestor.sendPacket(sm);
			return;
		}
		
		if (target == requestor)
		{
			requestor.sendPacket(SystemMessageId.WRONG_TARGET_HAS_BEEN_INVITED);
			return;
		}
		
		if (target.isJailed() || requestor.isJailed())
		{
			requestor.sendMessage("You cannot invite a player while is in Jail.");
			return;
		}
		
		if (requestor.isProcessingRequest())
		{
			requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			return;
		}
		
		if (target.isProcessingRequest())
		{
			sm = new SystemMessage(SystemMessageId.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER);
			sm.addString(target.getName());
			requestor.sendPacket(sm);
			return;
		}
		
		final Party party = requestor.getParty();
		if ((party != null) && !party.isLeader(requestor))
		{
			requestor.sendPacket(SystemMessageId.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
			return;
		}
		
		sm = new SystemMessage(SystemMessageId.YOU_HAVE_INVITED_S1_TO_YOUR_PARTY);
		sm.addString(target.getName());
		requestor.sendPacket(sm);
		
		if (!requestor.isInParty())
		{
			createNewParty(target, requestor);
		}
		else
		{
			addTargetToParty(target, requestor);
		}
	}
	
	private void addTargetToParty(Player target, Player requestor)
	{
		final Party party = requestor.getParty();
		
		// summary of ppl already in party and ppl that get invitation
		if (!party.isLeader(requestor))
		{
			requestor.sendPacket(SystemMessageId.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
			return;
		}
		
		if (party.getMemberCount() >= 9)
		{
			requestor.sendPacket(SystemMessageId.PARTY_IS_FULL);
			return;
		}
		
		if (party.getPendingInvitation() && !party.isInvitationRequestExpired())
		{
			requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			return;
		}
		
		if (!target.isProcessingRequest())
		{
			requestor.onTransactionRequest(target);
			party.setPendingInvitation(true);
			managePartyJoin(target, requestor);
		}
		else
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER);
			sm.addString(target.getName());
			requestor.sendPacket(sm);
		}
	}
	
	private void createNewParty(Player target, Player requestor)
	{
		final PartyDistributionType partyDistributionType = PartyDistributionType.findById(_partyDistributionTypeId);
		if (partyDistributionType == null)
		{
			return;
		}
		
		if (!target.isProcessingRequest())
		{
			target.setActiveRequester(requestor);
			requestor.onTransactionRequest(target);
			requestor.setPartyDistributionType(partyDistributionType);
			managePartyJoin(target, requestor);
		}
		else
		{
			requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
		}
	}
	
	private void managePartyJoin(Player target, Player requestor)
	{
		// Party Matching automatic accept.
		if (target.isPartyMatchingActive() && target.isPartyMatchingAutomaticRegistration())
		{
			if (requestor.isInParty())
			{
				if (requestor.getParty().getMemberCount() >= 9)
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.PARTY_IS_FULL);
					target.sendPacket(sm);
					requestor.sendPacket(sm);
					return;
				}
				
				requestor.sendPacket(new JoinParty(1 /* Party accept */));
				target.joinParty(requestor.getParty());
			}
			else
			{
				requestor.sendPacket(new JoinParty(1 /* Party accept */));
				requestor.setParty(new Party(requestor, requestor.getPartyDistributionType()));
				target.joinParty(requestor.getParty());
			}
		}
		else // Request join party.
		{
			target.sendPacket(new AskJoinParty(requestor.getObjectId(), requestor.getPartyDistributionType()));
		}
	}
}
