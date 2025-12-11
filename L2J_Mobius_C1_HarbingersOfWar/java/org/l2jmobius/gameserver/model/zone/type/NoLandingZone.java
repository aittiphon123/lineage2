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
package org.l2jmobius.gameserver.model.zone.type;

import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.MountType;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.ZoneType;

/**
 * A no landing zone
 * @author durgus
 */
public class NoLandingZone extends ZoneType
{
	private int dismountDelay = 5;
	
	public NoLandingZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("dismountDelay"))
		{
			dismountDelay = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (creature.isPlayer())
		{
			creature.setInsideZone(ZoneId.NO_LANDING, true);
			
			final Player player = creature.asPlayer();
			
			if (player.isGM())
			{
				player.sendMessage("You have entered a no-landing zone. Dismount restrictions are ignored for GMs.");
				return;
			}
			
			if (player.getMountType() == MountType.WYVERN)
			{
				player.sendMessage("You cannot enter this area while riding on a wyvern. If you continue to remain in this area while mounted on a wyvern, your riding status will be cancelled by force.");
				player.enteredNoLanding(dismountDelay);
			}
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (creature.isPlayer())
		{
			creature.setInsideZone(ZoneId.NO_LANDING, false);
			
			final Player player = creature.asPlayer();
			if (player.getMountType() == MountType.WYVERN)
			{
				player.exitedNoLanding();
			}
		}
	}
}
