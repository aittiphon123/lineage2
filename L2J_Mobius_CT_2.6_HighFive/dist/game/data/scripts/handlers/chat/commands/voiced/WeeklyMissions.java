/*
 * Copyright (c) 2013 L2jMobius
 */
package handlers.chat.commands.voiced;

import org.l2jmobius.gameserver.config.custom.WeeklyMissionsConfig;
import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;

public class WeeklyMissions implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"weekly"
	};
	
	@Override
	public boolean onCommand(String command, Player player, String params)
	{
		if ((player == null) || !"weekly".equals(command))
		{
			return false;
		}
		
		if (!WeeklyMissionsConfig.ENABLE_WEEKLY_MISSIONS)
		{
			player.sendMessage("Weekly Missions are currently disabled.");
			return true;
		}
		
		player.sendMessage("Weekly Mission (MVP): Kill " + WeeklyMissionsConfig.WEEKLY_MISSION_MONSTER_KILL_TARGET + " monsters.");
		player.sendMessage("Reward: " + WeeklyMissionsConfig.WEEKLY_MISSION_EXP_REWARD + " EXP, " + WeeklyMissionsConfig.WEEKLY_MISSION_SP_REWARD + " SP.");
		player.sendMessage("Progress tracking and rewards claim will be added in the next phase.");
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
