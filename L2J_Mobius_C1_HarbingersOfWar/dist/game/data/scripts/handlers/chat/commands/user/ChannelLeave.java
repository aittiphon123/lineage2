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
package handlers.chat.commands.user;

import org.l2jmobius.gameserver.handler.IUserCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.CommandChannel;
import org.l2jmobius.gameserver.model.groups.Party;

/**
 * Channel Leave user command.
 * @author Chris, Zoey76
 */
public class ChannelLeave implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		96
	};
	
	@Override
	public boolean onCommand(int id, Player player)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		if (!player.isInParty() || !player.getParty().isLeader(player))
		{
			player.sendMessage("Only a party leader can choose the option to leave a channel.");
			return false;
		}
		
		if (player.getParty().isInCommandChannel())
		{
			final CommandChannel channel = player.getParty().getCommandChannel();
			final Party party = player.getParty();
			channel.removeParty(party);
			party.getLeader().sendMessage("You have left the command channel.");
			channel.broadcastString(party.getLeader().getName() + " Party has left the command channel.");
			return true;
		}
		
		return false;
	}
	
	@Override
	public int[] getCommandList()
	{
		return COMMAND_IDS;
	}
}
