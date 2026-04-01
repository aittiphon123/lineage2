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
package handlers.bypass.npc;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.enums.CategoryType;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.handler.IBypassHandler;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;

/**
 * @author Mobius
 */
public class SupportMagic implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"supportmagic"
	};
	
	// Levels
	private static final int LOWEST_LEVEL = 8;
	private static final int HIGHEST_LEVEL = 24;
	
	@Override
	public boolean onCommand(String command, Player player, Creature target)
	{
		if (!target.isNpc())
		{
			return false;
		}
		
		if (command.equalsIgnoreCase(COMMANDS[0]))
		{
			final int level = player.getLevel();
			final Npc npc = target.asNpc();
			if (!PlayerConfig.ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE && !player.isNewbie())
			{
				npc.showChatWindow(player, "data/html/default/SupportMagicNovice.htm");
				return false;
			}
			else if (level > HIGHEST_LEVEL)
			{
				npc.showChatWindow(player, "data/html/default/SupportMagicHighLevel.htm");
				return false;
			}
			else if (level < LOWEST_LEVEL)
			{
				npc.showChatWindow(player, "data/html/default/SupportMagicLowLevel.htm");
				return false;
			}
			else if (player.getPlayerClass().level() == 3)
			{
				player.sendMessage("Only adventurers who have not completed their 3rd class transfer may receive these buffs."); // Custom message
				return false;
			}
			
			if ((player.getLevel() >= 8) && (player.getLevel() <= 24))
			{
				SkillData.getInstance().getSkill(1204, 1).applyEffects(npc, player); // WindWalk
				player.sendPacket(new MagicSkillUse(npc, player, 1204, 1, 0, 0));
			}
			
			if ((player.getLevel() >= 11) && (player.getLevel() <= 24))
			{
				SkillData.getInstance().getSkill(1040, 1).applyEffects(npc, player); // Shield
				player.sendPacket(new MagicSkillUse(npc, player, 1040, 1, 0, 0));
			}
			
			if (player.isInCategory(CategoryType.BEGINNER_MAGE))
			{
				if ((player.getLevel() >= 12) && (player.getLevel() <= 23))
				{
					SkillData.getInstance().getSkill(1048, 1).applyEffects(npc, player); // Bless the Soul
					player.sendPacket(new MagicSkillUse(npc, player, 1048, 1, 0, 0));
				}
				
				if ((player.getLevel() >= 13) && (player.getLevel() <= 22))
				{
					SkillData.getInstance().getSkill(1085, 1).applyEffects(npc, player); // Acumen
					player.sendPacket(new MagicSkillUse(npc, player, 1085, 1, 0, 0));
				}
				
				if ((player.getLevel() >= 14) && (player.getLevel() <= 21))
				{
					SkillData.getInstance().getSkill(1078, 1).applyEffects(npc, player); // Concentration
					player.sendPacket(new MagicSkillUse(npc, player, 1078, 1, 0, 0));
				}
				
				if ((player.getLevel() >= 15) && (player.getLevel() <= 20))
				{
					SkillData.getInstance().getSkill(1059, 1).applyEffects(npc, player); // Empower
					player.sendPacket(new MagicSkillUse(npc, player, 1059, 1, 0, 0));
				}
			}
			else
			{
				if ((player.getLevel() >= 12) && (player.getLevel() <= 23))
				{
					SkillData.getInstance().getSkill(1045, 1).applyEffects(npc, player); // Bless the Body
					player.sendPacket(new MagicSkillUse(npc, player, 1045, 1, 0, 0));
				}
				
				if ((player.getLevel() >= 13) && (player.getLevel() <= 22))
				{
					SkillData.getInstance().getSkill(1068, 1).applyEffects(npc, player); // Might
					player.sendPacket(new MagicSkillUse(npc, player, 1068, 1, 0, 0));
				}
				
				if ((player.getLevel() >= 14) && (player.getLevel() <= 21))
				{
					SkillData.getInstance().getSkill(1044, 1).applyEffects(npc, player); // Regeneration
					player.sendPacket(new MagicSkillUse(npc, player, 1044, 1, 0, 0));
				}
				
				if ((player.getLevel() >= 15) && (player.getLevel() <= 20))
				{
					SkillData.getInstance().getSkill(1086, 1).applyEffects(npc, player); // Haste
					player.sendPacket(new MagicSkillUse(npc, player, 1086, 1, 0, 0));
				}
			}
			
			if ((player.getLevel() >= 16) && (player.getLevel() <= 19))
			{
				SkillData.getInstance().getSkill(67, 1).applyEffects(npc, player); // Life Cubic
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getCommandList()
	{
		return COMMANDS;
	}
}
