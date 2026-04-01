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
package handlers.chat.commands.admin;

import java.util.StringTokenizer;

import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.handler.IAdminCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.AccountVariables;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.dye.DyeEffectList;

/**
 * @author CostyKiller
 */
public class AdminDyes implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_dye_menu",
		"admin_dye_level",
		"admin_dye_reset"
	};
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		if ((activeChar.getTarget() == null) || !activeChar.getTarget().isPlayer())
		{
			activeChar.sendMessage("Invalid target!");
			return false;
		}
		Player target = activeChar.getTarget() != null ? activeChar.getTarget().asPlayer() : activeChar.asPlayer();
		
		if (command.startsWith("admin_dye_menu"))
		{
			showDyeMenu(activeChar, target);
		}
		else if (command.startsWith("admin_dye_level "))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (!(st.countTokens() == 2))
			{
				activeChar.sendMessage("Usage: //dye_set_level <slotId (1, 2, 3)> <level>");
				return false;
			}
			else if (st.countTokens() == 2)
			{
				int slotId = Integer.valueOf(st.nextToken());
				int slotLevel = Integer.valueOf(st.nextToken());
				int skillId = 0;
				int skillLevel = slotLevel == 0 ? 1 : slotLevel;
				int challengeCount = 30;
				int hiddenSkillId = 0;
				int hiddenSkillLevel = 0;
				if (slotLevel > 30)
				{
					activeChar.sendMessage("Invalid slot level, max is 30!");
					return false;
				}
				// Determine skill ID based on slot
				switch (slotId)
				{
					case 1:
					{
						skillId = 37073; // Seal - Giant's Power.
						hiddenSkillId = 37066; // Seal Heritage - Giant's Power.
						break;
					}
					case 2:
					{
						skillId = 37074; // Seal - Giant's Wisdom.
						hiddenSkillId = 37067; // Seal Heritage - Giant's Wisdom.
						break;
					}
					case 3:
					{
						skillId = 37075; // Seal - Giant's Might.
						hiddenSkillId = 37068; // Seal Heritage - Giant's Might.
						break;
					}
				}
				
				// Update challenge count.
				challengeCount = challengeCount - slotLevel;
				
				// Determine skill level based on slot level.
				switch (slotLevel)
				{
					case 26:
					case 27:
					{
						hiddenSkillLevel = 1;
						break;
					}
					case 28:
					case 29:
					case 30:
					{
						hiddenSkillLevel = 2;
						break;
					}
				}
				
				target.getAccountVariables().set(AccountVariables.DYE_CHALLENGE_COUNT_FOR_SLOT_ + slotId, challengeCount);
				target.getAccountVariables().set(AccountVariables.DYE_LEVEL_FOR_SLOT_ + slotId, slotLevel);
				
				// Add skill.
				final Skill dyeSkill = SkillData.getInstance().getSkill(skillId, skillLevel);
				target.addSkill(dyeSkill, true);
				
				if (slotLevel > 25)
				{
					target.getAccountVariables().set(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + slotId, hiddenSkillLevel);
					
					// Add skill.
					final Skill dyeHiddenSkill = SkillData.getInstance().getSkill(hiddenSkillId, hiddenSkillLevel);
					target.addSkill(dyeHiddenSkill, true);
					
					// Check hidden skills.
					final int hiddenPowerSkillLevel = target.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + 1, 0);
					final int hiddenWisdomSkillLevel = target.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + 2, 0);
					final int hiddenMightSkillLevel = target.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + 3, 0);
					final int hiddenSealBuffLevel = hiddenPowerSkillLevel + hiddenWisdomSkillLevel + hiddenMightSkillLevel;
					
					// Add buff skill.
					final Skill hiddenSealBuffSkill = SkillData.getInstance().getSkill(37072 /* Giant Seal Buff */, hiddenSealBuffLevel);
					target.addSkill(hiddenSealBuffSkill, true);
				}
			}
			if (!(activeChar == target.asPlayer()))
			{
				target.sendMessage("Admin updated your tattoos.");
			}
			activeChar.sendMessage("You updated " + target.getName() + " tattoos.");
			
			// Refresh the menu with updated values
			showDyeMenu(activeChar, target);
		}
		else if (command.startsWith("admin_dye_reset "))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			int slotId = Integer.valueOf(st.nextToken());
			int skillId = 0;
			int hiddenSkillId = 0;
			
			switch (slotId)
			{
				case 1:
				{
					skillId = 37073; // Seal - Giant's Power
					hiddenSkillId = 37066; // Seal Heritage - Giant's Power
					break;
				}
				case 2:
				{
					skillId = 37074; // Seal - Giant's Wisdom
					hiddenSkillId = 37067; // Seal Heritage - Giant's Wisdom
					break;
				}
				case 3:
				{
					skillId = 37075; // Seal - Giant's Might
					hiddenSkillId = 37068; // Seal Heritage - Giant's Might
					break;
				}
			}
			
			int skillLevel = target.getAccountVariables().getInt(AccountVariables.DYE_LEVEL_FOR_SLOT_ + slotId, 0);
			int hiddenSkillLevel = target.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + slotId, 0);
			
			// Check hidden skills levels here.
			final int hiddenPowerSkillLevel = target.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + 1, 0);
			final int hiddenWisdomSkillLevel = target.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + 2, 0);
			final int hiddenMightSkillLevel = target.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + 3, 0);
			int hiddenSealBuffSkillId = 37072; // Giant Seal Buff
			int hiddenSealBuffSkillLevel = hiddenPowerSkillLevel + hiddenWisdomSkillLevel + hiddenMightSkillLevel;
			if (skillLevel != 0)
			{
				final Skill dyeSkill = SkillData.getInstance().getSkill(skillId, skillLevel);
				target.removeSkill(dyeSkill, true);
			}
			if (hiddenSkillLevel != 0)
			{
				final Skill hiddenDyeSkill = SkillData.getInstance().getSkill(hiddenSkillId, hiddenSkillLevel);
				target.removeSkill(hiddenDyeSkill, true);
				target.getAccountVariables().set(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + slotId, 0);
			}
			
			// Update hidden seal buff skill level based on the slot that is reset
			if (hiddenSealBuffSkillLevel != 0)
			{
				final Skill hiddenSealBuffSkill = SkillData.getInstance().getSkill(hiddenSealBuffSkillId, hiddenSealBuffSkillLevel - target.getAccountVariables().getInt(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + slotId, 0));
				target.addSkill(hiddenSealBuffSkill, true);
			}
			
			target.sendSkillList();
			
			target.getAccountVariables().set(AccountVariables.DYE_CHALLENGE_COUNT_FOR_SLOT_ + slotId, 30);
			target.getAccountVariables().set(AccountVariables.DYE_LEVEL_FOR_SLOT_ + slotId, 0);
			target.getAccountVariables().set(AccountVariables.DYE_HIDDEN_SKILL_LEVEL_FOR_SLOT_ + slotId, 0);
			if (!(activeChar == target.asPlayer()))
			{
				target.sendMessage("Admin updated your tattoos.");
			}
			activeChar.sendMessage("You updated " + target.getName() + " tattoos.");
			
			// Refresh the menu with updated values
			showDyeMenu(activeChar, target);
		}
		
		activeChar.sendPacket(new DyeEffectList(target));
		
		return true;
	}
	
	/**
	 * Helper method to build and send the dye menu HTML
	 * @param activeChar the admin character
	 * @param target the target player
	 */
	private void showDyeMenu(Player activeChar, Player target)
	{
		// Get current values
		final int slot1Level = target.getAccountVariables().getInt(AccountVariables.DYE_LEVEL_FOR_SLOT_ + 1, 0);
		final int slot2Level = target.getAccountVariables().getInt(AccountVariables.DYE_LEVEL_FOR_SLOT_ + 2, 0);
		final int slot3Level = target.getAccountVariables().getInt(AccountVariables.DYE_LEVEL_FOR_SLOT_ + 3, 0);
		
		// Build HTML with current values
		String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/dyes_menu.htm");
		content = content.replace("%targetName%", target.getName());
		
		content = content.replace("%slot1_icon%", "icon.dyeeffect_1");
		content = content.replace("%slot2_icon%", "icon.dyeeffect_2");
		content = content.replace("%slot3_icon%", "icon.dyeeffect_3");
		content = content.replace("%slot1Level%", slot1Level > 0 ? "icon.etc_level_panel_lv" + slot1Level : "icon.dyeeffect_1");
		content = content.replace("%slot2Level%", slot2Level > 0 ? "icon.etc_level_panel_lv" + slot2Level : "icon.dyeeffect_2");
		content = content.replace("%slot3Level%", slot3Level > 0 ? "icon.etc_level_panel_lv" + slot3Level : "icon.dyeeffect_3");
		
		activeChar.sendPacket(new NpcHtmlMessage(0, 1, content));
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
}