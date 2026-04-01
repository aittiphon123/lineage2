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
package handlers.chat.channels;

import org.l2jmobius.gameserver.config.GeneralConfig;
import org.l2jmobius.gameserver.handler.IChatHandler;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.CreatureSay;

/**
 * Battlefield Chat Handler.
 * @author Gigiikun
 */
public class ChatBattlefield implements IChatHandler
{
	private static final ChatType[] CHAT_TYPES =
	{
		ChatType.BATTLEFIELD,
	};
	
	@Override
	public void onChat(ChatType type, Player activeChar, String target, String text)
	{
		if (activeChar.getSiegeSide() > 0)
		{
			if (activeChar.isChatBanned() && GeneralConfig.BAN_CHAT_CHANNELS.contains(type))
			{
				activeChar.sendMessage("Chatting is currently prohibited. If you try to chat before the prohibition is removed, the prohibition time will become even longer.");
				return;
			}
			
			final CreatureSay cs = new CreatureSay(activeChar, type, activeChar.getName(), text);
			for (Player player : World.getInstance().getPlayers())
			{
				if (player.getSiegeSide() == activeChar.getSiegeSide())
				{
					player.sendPacket(cs);
				}
			}
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}
