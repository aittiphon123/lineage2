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
package org.l2jmobius.gameserver.network.enums;

/**
 * @author St3eT
 */
public enum ChatType
{
	GENERAL(0),
	SHOUT(1),
	WHISPER(2),
	PARTY(3),
	CLAN(4),
	GM(5),
	PETITION_PLAYER(6),
	PETITION_GM(7),
	TRADE(8),
	ALLIANCE(9), // GM_MESSAGE?
	ANNOUNCEMENT(10),
	BOAT(1), // C1 Adjustment
	FRIEND(2), // C1 Adjustment
	MSNCHAT(13), // Exists in C1?
	PARTYMATCH_ROOM(3), // Exists in C1?
	PARTYROOM_COMMANDER(15), // Exists in C1?
	PARTYROOM_ALL(16), // Exists in C1?
	HERO_VOICE(17), // Exists in C1?
	CRITICAL_ANNOUNCE(18), // Exists in C1?
	SCREEN_ANNOUNCE(19), // Exists in C1?
	BATTLEFIELD(20), // Exists in C1?
	MPCC_ROOM(21), // Exists in C1?
	NPC_GENERAL(0), // Epilogue adjustment
	NPC_SHOUT(1); // Epilogue adjustment
	
	private final int _clientId;
	
	private ChatType(int clientId)
	{
		_clientId = clientId;
	}
	
	/**
	 * @return the client id.
	 */
	public int getClientId()
	{
		return _clientId;
	}
	
	/**
	 * Finds the {@code ChatType} by its clientId
	 * @param clientId the clientId
	 * @return the {@code ChatType} if it is found, {@code null} otherwise.
	 */
	public static ChatType findByClientId(int clientId)
	{
		for (ChatType ChatType : values())
		{
			if (ChatType.getClientId() == clientId)
			{
				return ChatType;
			}
		}
		
		return null;
	}
}
