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
package org.l2jmobius.gameserver.network.clientpackets.ability;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.request.AbilityLearnRequest;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.enums.SkillFinishType;
import org.l2jmobius.gameserver.model.skill.holders.SkillLearn;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.ability.ExAcquireAPSkillList;

/**
 * @author Mobius
 */
public class RequestResetAbilityPoint extends ClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.hasRequest(AbilityLearnRequest.class))
		{
			return;
		}
		
		player.addRequest(new AbilityLearnRequest(player));
		
		if (player.isSubClassActive() && !player.isDualClassActive())
		{
			player.removeRequest(AbilityLearnRequest.class);
			return;
		}
		
		if (player.isInStoreMode() || (player.getActiveRequester() != null))
		{
			player.removeRequest(AbilityLearnRequest.class);
			return;
		}
		else if ((player.getLevel() < 99) || (player.getNobleLevel() == 0))
		{
			player.sendPacket(SystemMessageId.ABILITIES_CAN_BE_USED_BY_NOBLESSE_EXALTED_LV_99_OR_ABOVE);
			player.removeRequest(AbilityLearnRequest.class);
			return;
		}
		else if (player.isInOlympiadMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_OR_RESET_ABILITY_POINTS_WHILE_PARTICIPATING_IN_THE_OLYMPIAD_OR_CEREMONY_OF_CHAOS);
			player.removeRequest(AbilityLearnRequest.class);
			return;
		}
		else if (player.isOnEvent())
		{
			player.sendMessage("You cannot use or reset Ability Points while participating in an event.");
			player.removeRequest(AbilityLearnRequest.class);
			return;
		}
		else if (player.getAbilityPoints() == 0)
		{
			player.sendMessage("You don't have ability points to reset!");
			player.removeRequest(AbilityLearnRequest.class);
			return;
		}
		else if (player.getAbilityPointsUsed() == 0)
		{
			player.sendMessage("You haven't used your ability points yet!");
			player.removeRequest(AbilityLearnRequest.class);
			return;
		}
		else if (player.getAdena() < PlayerConfig.ABILITY_POINTS_RESET_ADENA)
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			player.removeRequest(AbilityLearnRequest.class);
			return;
		}
		
		if (player.reduceAdena(ItemProcessType.FEE, PlayerConfig.ABILITY_POINTS_RESET_ADENA, player, true))
		{
			for (SkillLearn sk : SkillTreeData.getInstance().getAbilitySkillTree().values())
			{
				final Skill skill = player.getKnownSkill(sk.getSkillId());
				if (skill != null)
				{
					player.removeSkill(skill);
					player.getEffectList().stopSkillEffects(SkillFinishType.SILENT, skill); // TODO: Check if retail shows system message.
				}
			}
		}
		
		ThreadPool.schedule(() ->
		{
			player.setAbilityPointsUsed(0);
			player.sendPacket(new ExAcquireAPSkillList(player));
			player.broadcastUserInfo();
			player.removeRequest(AbilityLearnRequest.class);
		}, 300);
	}
}
