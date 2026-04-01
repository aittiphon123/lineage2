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
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.dye.DyeEffectList;

/**
 * @author CostyKiller
 */
public class AdminConquest implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_conquest_menu",
		"admin_conquest_ability_set",
		"admin_conquest_ability_reset"
	};
	
	private static final int PRIMORDIAL_FIRE_SOURCE_SKILL_ID = 34495;
	private static final int FIRE_SOURCE_SKILL_ID = 34498;
	private static final int LIFE_SOURCE_SKILL_ID = 34499;
	private static final int BATTLE_SOUL_SKILL_ID = 34500;
	private static final int FIRE_TOTEM_SKILL_ID = 34501;
	private static final int FLAME_SPARK_SKILL_ID = 34502;
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		if ((activeChar.getTarget() == null) || !activeChar.getTarget().isPlayer())
		{
			activeChar.sendMessage("Invalid target!");
			return false;
		}
		Player target = activeChar.getTarget() != null ? activeChar.getTarget().asPlayer() : activeChar.asPlayer();
		
		if (command.startsWith("admin_conquest_menu"))
		{
			showConquestMenu(activeChar, target);
		}
		else if (command.startsWith("admin_conquest_ability_set "))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (!(st.countTokens() == 2))
			{
				activeChar.sendMessage("Usage: //conquest_ability_set <id (0, 1, 2, 3, 4)> <level>");
				return false;
			}
			else if (st.countTokens() == 2)
			{
				int type = Integer.valueOf(st.nextToken());
				int abilityLevel = Integer.valueOf(st.nextToken());
				if (abilityLevel > 10)
				{
					activeChar.sendMessage("Invalid ability level, max is 10!");
					return false;
				}
				if (type == 0) // Fire Source
				{
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, abilityLevel);
					
					// Add ability skill
					target.addSkill(SkillData.getInstance().getSkill(FIRE_SOURCE_SKILL_ID, abilityLevel), true);
					
					// Check ability set levels to add set skill
					if (checkAbilitySetLevels(target) != 0)
					{
						target.addSkill(SkillData.getInstance().getSkill(PRIMORDIAL_FIRE_SOURCE_SKILL_ID, checkAbilitySetLevels(target)), true);
					}
				}
				else if (type == 1) // Life Source
				{
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, abilityLevel);
					
					// Add ability skill
					target.addSkill(SkillData.getInstance().getSkill(LIFE_SOURCE_SKILL_ID, abilityLevel), true);
					
					// Check ability set levels to add set skill
					if (checkAbilitySetLevels(target) != 0)
					{
						target.addSkill(SkillData.getInstance().getSkill(PRIMORDIAL_FIRE_SOURCE_SKILL_ID, checkAbilitySetLevels(target)), true);
					}
				}
				else if (type == 2) // Flame Spark
				{
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, abilityLevel);
					
					// Add ability skill
					target.addSkill(SkillData.getInstance().getSkill(FLAME_SPARK_SKILL_ID, abilityLevel), true);
					
					// Check ability set levels to add set skill
					if (checkAbilitySetLevels(target) != 0)
					{
						target.addSkill(SkillData.getInstance().getSkill(PRIMORDIAL_FIRE_SOURCE_SKILL_ID, checkAbilitySetLevels(target)), true);
					}
				}
				else if (type == 3) // Fire Totem
				{
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, abilityLevel);
					
					// Add ability skill
					target.addSkill(SkillData.getInstance().getSkill(FIRE_TOTEM_SKILL_ID, abilityLevel), true);
					
					// Check ability set levels to add set skill
					if (checkAbilitySetLevels(target) != 0)
					{
						target.addSkill(SkillData.getInstance().getSkill(PRIMORDIAL_FIRE_SOURCE_SKILL_ID, checkAbilitySetLevels(target)), true);
					}
				}
				else if (type == 4) // Battle Soul
				{
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, abilityLevel);
					
					// Add ability skill
					target.addSkill(SkillData.getInstance().getSkill(BATTLE_SOUL_SKILL_ID, abilityLevel), true);
					
					// Check ability set levels to add set skill
					if (checkAbilitySetLevels(target) != 0)
					{
						target.addSkill(SkillData.getInstance().getSkill(PRIMORDIAL_FIRE_SOURCE_SKILL_ID, checkAbilitySetLevels(target)), true);
					}
				}
			}
			target.sendSkillList();
			if (!(activeChar == target.asPlayer()))
			{
				target.sendMessage("Admin updated your conquest abilities.");
			}
			activeChar.sendMessage("You updated " + target.getName() + " conquest abilities.");
			
			// Refresh the menu with updated values
			showConquestMenu(activeChar, target);
		}
		else if (command.startsWith("admin_conquest_ability_reset "))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			int slotId = Integer.valueOf(st.nextToken());
			int skillId = 0;
			
			// Get current values
			final PlayerVariables variables = target.getVariables();
			final int _fireSourceLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0);
			final int _lifeSourceLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0);
			final int _flameSparkLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0);
			final int _fireTotemLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0);
			final int _battleSoulLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0);
			int skillLevel = 0;
			
			switch (slotId)
			{
				case 0: // Fire Source
				{
					skillId = FIRE_SOURCE_SKILL_ID;
					skillLevel = _fireSourceLevel;
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0);
					if (skillLevel != 0)
					{
						final Skill abilitySkill = SkillData.getInstance().getSkill(skillId, skillLevel);
						target.removeSkill(abilitySkill, true);
					}
					break;
				}
				case 1: // Life Source
				{
					skillId = LIFE_SOURCE_SKILL_ID;
					skillLevel = _lifeSourceLevel;
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0);
					if (skillLevel != 0)
					{
						final Skill abilitySkill = SkillData.getInstance().getSkill(skillId, skillLevel);
						target.removeSkill(abilitySkill, true);
					}
					break;
				}
				case 2: // Flame Spark
				{
					skillId = FLAME_SPARK_SKILL_ID;
					skillLevel = _flameSparkLevel;
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0);
					if (skillLevel != 0)
					{
						final Skill abilitySkill = SkillData.getInstance().getSkill(skillId, skillLevel);
						target.removeSkill(abilitySkill, true);
					}
					break;
				}
				case 3: // Fire Totem
				{
					skillId = FIRE_TOTEM_SKILL_ID;
					skillLevel = _fireTotemLevel;
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0);
					if (skillLevel != 0)
					{
						final Skill abilitySkill = SkillData.getInstance().getSkill(skillId, skillLevel);
						target.removeSkill(abilitySkill, true);
					}
					break;
				}
				case 4: // Battle Soul
				{
					skillId = BATTLE_SOUL_SKILL_ID;
					skillLevel = _battleSoulLevel;
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_EXP, 0);
					target.getVariables().set(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0);
					if (skillLevel != 0)
					{
						final Skill abilitySkill = SkillData.getInstance().getSkill(skillId, skillLevel);
						target.removeSkill(abilitySkill, true);
					}
					break;
				}
			}
			
			// Update set skill after reset
			if (checkAbilitySetLevels(target) > 0)
			{
				target.addSkill(SkillData.getInstance().getSkill(PRIMORDIAL_FIRE_SOURCE_SKILL_ID, checkAbilitySetLevels(target)), true);
			}
			else
			{
				// Remove all levels of set skill if no abilities are at required levels
				for (int i = 1; i <= 10; i++)
				{
					final Skill setSkill = target.getSkills().get(PRIMORDIAL_FIRE_SOURCE_SKILL_ID);
					if (setSkill != null)
					{
						target.removeSkill(setSkill, true);
						break;
					}
				}
			}
			
			target.sendSkillList();
			if (!(activeChar == target.asPlayer()))
			{
				target.sendMessage("Admin updated your conquest abilities.");
			}
			activeChar.sendMessage("You updated " + target.getName() + " conquest abilities.");
			
			// Refresh the menu with updated values
			showConquestMenu(activeChar, target);
		}
		
		activeChar.sendPacket(new DyeEffectList(target));
		
		return true;
		
	}
	
	/**
	 * Helper method to build and send the conquest menu HTML
	 * @param activeChar the admin character
	 * @param target the target player
	 */
	private void showConquestMenu(Player activeChar, Player target)
	{
		// Get current values
		final PlayerVariables variables = target.getVariables();
		final int fireSourceLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0);
		final int lifeSourceLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0);
		final int flameSparkLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0);
		final int fireTotemLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0);
		final int battleSoulLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0);
		
		// Build HTML with current values
		String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/conquest_menu.htm");
		content = content.replace("%targetName%", target.getName());
		
		content = content.replace("%slot1_icon%", "icon.s_dethrone_fire_source_1");
		content = content.replace("%slot2_icon%", "icon.s_dethrone_fire_combat_1");
		content = content.replace("%slot3_icon%", "icon.s_dethrone_fire_life_1");
		content = content.replace("%slot4_icon%", "icon.s_dethrone_fire_totem_1");
		content = content.replace("%slot5_icon%", "icon.s_dethrone_fire_piece_1");
		content = content.replace("%slot1Level%", fireSourceLevel > 0 ? "icon.etc_level_panel_lv" + fireSourceLevel : "icon.s_dethrone_fire_source_1");
		content = content.replace("%slot2Level%", lifeSourceLevel > 0 ? "icon.etc_level_panel_lv" + lifeSourceLevel : "icon.s_dethrone_fire_combat_1");
		content = content.replace("%slot3Level%", flameSparkLevel > 0 ? "icon.etc_level_panel_lv" + flameSparkLevel : "icon.s_dethrone_fire_life_1");
		content = content.replace("%slot4Level%", fireTotemLevel > 0 ? "icon.etc_level_panel_lv" + fireTotemLevel : "icon.s_dethrone_fire_totem_1");
		content = content.replace("%slot5Level%", battleSoulLevel > 0 ? "icon.etc_level_panel_lv" + battleSoulLevel : "icon.s_dethrone_fire_piece_1");
		
		activeChar.sendPacket(new NpcHtmlMessage(0, 1, content));
	}
	
	public int checkAbilitySetLevels(Player target)
	{
		if ((target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0) == 10) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0) == 10) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0) == 10) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0) == 10) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0) == 10))
		{
			return 3;
		}
		else if ((target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0) >= 6) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0) >= 6) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0) >= 6) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0) >= 6) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0) >= 6))
		{
			return 2;
		}
		else if ((target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0) >= 3) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0) >= 3) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0) >= 3) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0) >= 3) //
			&& (target.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0) >= 3))
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
}