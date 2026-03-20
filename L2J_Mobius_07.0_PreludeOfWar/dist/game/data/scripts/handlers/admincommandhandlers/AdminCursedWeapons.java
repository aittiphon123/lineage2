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
package handlers.admincommandhandlers;

import java.util.Collection;
import java.util.StringTokenizer;

import org.l2jmobius.gameserver.handler.IAdminCommandHandler;
import org.l2jmobius.gameserver.managers.CursedWeaponsManager;
import org.l2jmobius.gameserver.managers.ScriptManager;
import org.l2jmobius.gameserver.model.CursedWeapon;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This class handles the following admin commands: <br>
 * - cw_info = displays cursed weapon status <br>
 * - cw_remove = removes a cursed weapon from the world (item id or name must be provided) <br>
 * - cw_add = adds a cursed weapon into the world (item id or name must be provided; target will be the wielder) <br>
 * - cw_goto = teleports GM to the specified cursed weapon <br>
 * - cw_reload = reloads the cursed weapons manager <br>
 * - cw_info_menu = opens the HTML control panel for cursed weapons <br>
 * --- Control Panel Extensions --- <br>
 * - cw_test_toggle = toggles test mode for cursed weapon defense <br>
 * - cw_phase_1 = starts the normal event phase (18h–22h29) <br>
 * - cw_phase_2 = starts the special event phase (22h30–23h58) <br>
 * - cw_stop = stops the cursed weapon event <br>
 * @version $Revision: 1.1.6.5 $ $Date: 2026/02/16 16:40:00 $
 */
public class AdminCursedWeapons implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_cw_info",
		"admin_cw_remove",
		"admin_cw_goto",
		"admin_cw_reload",
		"admin_cw_add",
		"admin_cw_info_menu",
		// Command Panel Control
		"admin_cw_test_toggle",
		"admin_cw_phase_1",
		"admin_cw_phase_2",
		"admin_cw_stop"
	};
	
	private static int _activePhase = 0;
	private static boolean _isTestMode = false;
	
	@Override
	public boolean onCommand(String command, Player activeChar)
	{
		final CursedWeaponsManager cwm = CursedWeaponsManager.getInstance();
		if (command.startsWith("admin_cw_stop"))
		{
			cwm.endEventForced();
			org.l2jmobius.gameserver.model.script.Quest defenseScript = org.l2jmobius.gameserver.managers.ScriptManager.getInstance().getScript("CursedWeaponDefense");
			
			if (_isTestMode && (defenseScript != null))
			{
				defenseScript.notifyEvent("admin_cmd_test_toggle", null, activeChar);
			}
			
			_activePhase = 0;
			_isTestMode = false;
			
			if (defenseScript != null)
			{
				defenseScript.notifyEvent("admin_cmd_stop", null, activeChar);
			}
			
			activeChar.sendMessage("The event and NPCs have ended (23:00 Simulation)!");
			onCommand("admin_cw_info menu", activeChar);
			return true;
		}
		// Control Panel
		if (command.startsWith("admin_cw_phase") || command.startsWith("admin_cw_test"))
		{
			Quest defenseScript = ScriptManager.getInstance().getScript("CursedWeaponDefense");
			
			if (defenseScript != null)
			{
				
				if (command.startsWith("admin_cw_phase") && !_isTestMode)
				{
					activeChar.sendMessage("Error: You must enable 'Test Mode' before starting the phases!");
					onCommand("admin_cw_info menu", activeChar);
					return true;
				}
				
				String eventName = command.replace("admin_cw_", "admin_cmd_");
				defenseScript.notifyEvent(eventName, null, activeChar);
				
				if (command.contains("test_toggle"))
				{
					_isTestMode = !_isTestMode; // Turns Test Mode on or off (Toggle)
					if (!_isTestMode)
					{
						_activePhase = 0; // If the GM disabled Test Mode, phases are immediately cleared
					}
				}
				else if (command.contains("phase_1"))
				{
					_activePhase = 1; // Switches visually to Phase 1
				}
				else if (command.contains("phase_2"))
				{
					_activePhase = 2; // Switches visually to Phase 2
				}
				if (command.contains("test") || command.contains("phase"))
				{
					onCommand("admin_cw_info menu", activeChar);
				}
			}
			else
			{
				activeChar.sendMessage("Defense Script 'CursedWeaponDefense' not loaded.");
			}
			return true;
		}
		
		int id = 0;
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (command.startsWith("admin_cw_info"))
		{
			if (!command.contains("menu"))
			{
				activeChar.sendSysMessage("====== Cursed Weapons: ======");
				for (CursedWeapon cw : cwm.getCursedWeapons())
				{
					activeChar.sendSysMessage("> " + cw.getName() + " (" + cw.getItemId() + ")");
					if (cw.isActivated())
					{
						final Player pl = cw.getPlayer();
						activeChar.sendSysMessage("  Player holding: " + (pl == null ? "null" : pl.getName()));
						activeChar.sendSysMessage("    Player Reputation: " + cw.getPlayerReputation());
						activeChar.sendSysMessage("    Time Remaining: " + (cw.getTimeLeft() / 60000) + " min.");
						activeChar.sendSysMessage("    Kills : " + cw.getNbKills());
					}
					else if (cw.isDropped())
					{
						activeChar.sendSysMessage("  Lying on the ground.");
						activeChar.sendSysMessage("    Time Remaining: " + (cw.getTimeLeft() / 60000) + " min.");
						activeChar.sendSysMessage("    Kills : " + cw.getNbKills());
					}
					else
					{
						activeChar.sendSysMessage("  Don't exist in the world.");
					}
					
					activeChar.sendPacket(SystemMessageId.EMPTY_3);
				}
			}
			else
			{
				final Collection<CursedWeapon> cws = cwm.getCursedWeapons();
				final StringBuilder replyMSG = new StringBuilder(cws.size() * 300);
				// final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
				final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 25000);
				adminReply.setFile(activeChar, "data/html/admin/cwinfo.htm");
				
				// --- Cursed Weapons Defense - Control Panel ---
				if (cwm.isEventActive() && (_activePhase == 0))
				{
					java.util.Calendar now = java.util.Calendar.getInstance();
					int h = now.get(java.util.Calendar.HOUR_OF_DAY);
					int m = now.get(java.util.Calendar.MINUTE);
					_activePhase = (((h == 22) && (m >= 30)) || (h == 23)) ? 2 : 1;
				}
				
				// --- Dynamic Texts and Buttons System ---
				String statusText = "OFF";
				String btnP1 = "Normal 18h/22h29";
				String btnP2 = "Special 22h30/23h58";
				String btnTest = _isTestMode ? "[ON] Test Mode" : "Test Mode";
				
				if (_activePhase == 1)
				{
					statusText = "NORMAL (ON)";
					btnP1 = "[ ON ] Normal 18h";
				}
				else if (_activePhase == 2)
				{
					statusText = "SPECIAL (ON)";
					btnP2 = "[ ON ] Special 22h30";
				}
				else if ((_activePhase == 0) && _isTestMode)
				{
					statusText = "TEST MODE (ON)"; // Status when Test Mode is enabled without selecting a phase
				}
				
				// --- Cursed Weapons Defense - Control Panel ---
				StringBuilder controlPanel = new StringBuilder();
				controlPanel.append("<center><table width=270 border=0 bgcolor=222222>");
				controlPanel.append("<tr><td align=center>Control Panel - ").append(statusText).append("</td></tr>");
				controlPanel.append("</table>");
				controlPanel.append("<table width=270>");
				controlPanel.append("<tr>");
				
				controlPanel.append("<td><button value=\"").append(btnTest).append("\" action=\"bypass -h admin_cw_test_toggle\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
				controlPanel.append("<td><button value=\"Stop Event\" action=\"bypass -h admin_cw_stop\" width=75 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
				controlPanel.append("<td><button value=\"Help\" action=\"bypass -h admin_html help/cwinfo.htm\" width=75 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
				controlPanel.append("</tr></table>");
				
				controlPanel.append("<table width=270><tr>");
				controlPanel.append("<td><button value=\"").append(btnP1).append("\" action=\"bypass -h admin_cw_phase_1\" width=125 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
				controlPanel.append("<td><button value=\"").append(btnP2).append("\" action=\"bypass -h admin_cw_phase_2\" width=125 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
				controlPanel.append("</tr></table>");
				
				for (CursedWeapon cw : cwm.getCursedWeapons())
				{
					final int itemId = cw.getItemId();
					replyMSG.append("<table width=270><tr><td>Name:</td><td>");
					replyMSG.append(cw.getName());
					replyMSG.append("</td></tr>");
					
					if (cw.isActivated())
					{
						final Player pl = cw.getPlayer();
						replyMSG.append("<tr><td>Weilder:</td><td>");
						replyMSG.append(pl == null ? "null" : pl.getName());
						replyMSG.append("</td></tr>");
						replyMSG.append("<tr><td>Karma:</td><td>");
						replyMSG.append(cw.getPlayerReputation());
						replyMSG.append("</td></tr>");
						replyMSG.append("<tr><td>Kills:</td><td>");
						replyMSG.append(cw.getPlayerPkKills());
						replyMSG.append("/");
						replyMSG.append(cw.getNbKills());
						replyMSG.append("</td></tr><tr><td>Time remaining:</td><td>");
						replyMSG.append(cw.getTimeLeft() / 60000);
						replyMSG.append(" min.</td></tr>");
						replyMSG.append("<tr><td><button value=\"Remove\" action=\"bypass -h admin_cw_remove ");
						replyMSG.append(itemId);
						replyMSG.append("\" width=73 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
						replyMSG.append("<td><button value=\"Go\" action=\"bypass -h admin_cw_goto ");
						replyMSG.append(itemId);
						replyMSG.append("\" width=73 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
					}
					else if (cw.isDropped())
					{
						replyMSG.append("<tr><td>Position:</td><td>Lying on the ground</td></tr><tr><td>Time remaining:</td><td>");
						replyMSG.append(cw.getTimeLeft() / 60000);
						replyMSG.append(" min.</td></tr><tr><td>Kills:</td><td>");
						replyMSG.append(cw.getNbKills());
						replyMSG.append("</td></tr><tr><td><button value=\"Remove\" action=\"bypass -h admin_cw_remove ");
						replyMSG.append(itemId);
						replyMSG.append("\" width=73 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
						replyMSG.append("<td><button value=\"Go\" action=\"bypass -h admin_cw_goto ");
						replyMSG.append(itemId);
						replyMSG.append("\" width=73 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
					}
					else
					{
						replyMSG.append("<tr><td>Position:</td><td>Doesn't exist.</td></tr><tr><td><button value=\"Give to Target\" action=\"bypass -h admin_cw_add ");
						replyMSG.append(itemId);
						replyMSG.append("\" width=130 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td></td></tr>");
					}
					
					replyMSG.append("</table><br>");
				}
				
				adminReply.replace("%cwinfo%", controlPanel.toString() + replyMSG.toString());
				activeChar.sendPacket(adminReply);
			}
		}
		else if (command.startsWith("admin_cw_reload"))
		{
			cwm.load();
			// Reload admin HTML info
			onCommand("admin_cw_info menu", activeChar);
		}
		else
		{
			CursedWeapon cw = null;
			try
			{
				String parameter = st.nextToken();
				if (parameter.matches("[0-9]*"))
				{
					id = Integer.parseInt(parameter);
				}
				else
				{
					parameter = parameter.replace('_', ' ');
					for (CursedWeapon cwp : cwm.getCursedWeapons())
					{
						if (cwp.getName().toLowerCase().contains(parameter.toLowerCase()))
						{
							id = cwp.getItemId();
							break;
						}
					}
				}
				
				cw = cwm.getCursedWeapon(id);
			}
			catch (Exception e)
			{
				activeChar.sendSysMessage("Usage: //cw_remove|//cw_goto|//cw_add <itemid|name>");
			}
			
			if (cw == null)
			{
				activeChar.sendSysMessage("Unknown cursed weapon ID.");
				return false;
			}
			
			if (command.startsWith("admin_cw_remove "))
			{
				cw.endOfLife();
				cw.purgeGhostMapIcons();
				// Reload admin HTML info
				onCommand("admin_cw_info menu", activeChar);
			}
			else if (command.startsWith("admin_cw_goto "))
			{
				cw.goTo(activeChar);
			}
			else if (command.startsWith("admin_cw_add"))
			{
				if (cw.isActive())
				{
					activeChar.sendSysMessage("This cursed weapon is already active.");
				}
				else
				{
					// 1. Determine the receiver of the weapon (target or the GM himself)
					final WorldObject target = activeChar.getTarget();
					final Player targetPlayer = ((target != null) && target.isPlayer()) ? target.asPlayer() : activeChar;
					
					// 2. Apply temporary GM authorization
					// This flag tells CursedWeapon.java: "Ignore the schedule, this is a GM command."
					targetPlayer.getVariables().set("CW_GM_AUTH", true);
					
					// 3. Grant the weapon (core checks the variable and allows activation)
					targetPlayer.addItem(ItemProcessType.QUEST, id, 1, activeChar, true);
					
					// 4. Reset weapon duration and reactivate (core reads the variable in reActivate())
					cw.setEndTime(System.currentTimeMillis() + (cw.getDuration() * 60000));
					cw.reActivate();
					
					// 5. Remove GM authorization immediately after reActivate (cleanup)
					targetPlayer.getVariables().remove("CW_GM_AUTH");
					
					// Reload admin HTML info
					onCommand("admin_cw_info menu", activeChar);
				}
			}
			else
			{
				activeChar.sendSysMessage("Unknown command.");
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getCommandList()
	{
		return ADMIN_COMMANDS;
	}
}