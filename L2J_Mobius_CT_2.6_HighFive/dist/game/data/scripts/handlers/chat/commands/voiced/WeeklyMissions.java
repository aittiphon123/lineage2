/*
 * Copyright (c) 2013 L2jMobius
 */
package handlers.chat.commands.voiced;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.l2jmobius.gameserver.config.custom.WeeklyMissionsConfig;
import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;

public class WeeklyMissions implements IVoicedCommandHandler
{
	private static final String PROGRESS_VAR = "WEEKLY_MISSION_KILL_PROGRESS";
	private static final String PROGRESS_WEEK_VAR = "WEEKLY_MISSION_PROGRESS_WEEK";
	private static final String CLAIMED_WEEK_VAR = "WEEKLY_MISSION_CLAIMED_WEEK";

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
		final String[] paramArray = (params == null) ? new String[0] : params.trim().toLowerCase().split("\\s+");

		if (!WeeklyMissionsConfig.ENABLE_WEEKLY_MISSIONS)
		{
			player.sendMessage("Weekly Missions are currently disabled.");
			return true;
		}

		final String weekKey = getCurrentWeekKey();
		ensureWeeklyState(player, weekKey);
		int progress = player.getVariables().getInt(PROGRESS_VAR, 0);
		if (progress < 0)
		{
			progress = 0;
		}
		final int target = WeeklyMissionsConfig.WEEKLY_MISSION_MONSTER_KILL_TARGET;
		progress = Math.min(progress, target);

		if ((paramArray.length > 0) && "claim".equals(paramArray[0]))
		{
			if (weekKey.equals(player.getVariables().getString(CLAIMED_WEEK_VAR, "")))
			{
				player.sendMessage("Weekly Mission reward already claimed for week " + weekKey + ".");
				return true;
			}
			if (progress < target)
			{
				player.sendMessage("Weekly Mission is not complete yet. Progress: " + progress + "/" + target + ".");
				return true;
			}
			player.addExpAndSp(WeeklyMissionsConfig.WEEKLY_MISSION_EXP_REWARD, WeeklyMissionsConfig.WEEKLY_MISSION_SP_REWARD);
			player.getVariables().set(CLAIMED_WEEK_VAR, weekKey);
			player.sendMessage("Weekly Mission reward claimed: +" + WeeklyMissionsConfig.WEEKLY_MISSION_EXP_REWARD + " EXP, +" + WeeklyMissionsConfig.WEEKLY_MISSION_SP_REWARD + " SP.");
			return true;
		}
		else if ((paramArray.length > 1) && "setprogress".equals(paramArray[0]) && player.isGM())
		{
			try
			{
				progress = Math.min(target, Math.max(0, Integer.parseInt(paramArray[1])));
				player.getVariables().set(PROGRESS_VAR, progress);
				player.sendMessage("Weekly Mission progress set to " + progress + " (GM command).");
			}
			catch (NumberFormatException e)
			{
				player.sendMessage("Usage: .weekly setprogress <number>");
			}
			return true;
		}
		else if ((paramArray.length > 1) && "addkill".equals(paramArray[0]) && player.isGM())
		{
			try
			{
				final int add = Math.max(0, Integer.parseInt(paramArray[1]));
				progress = Math.min(target, progress + add);
				player.getVariables().set(PROGRESS_VAR, progress);
				player.sendMessage("Weekly Mission progress increased to " + progress + "/" + target + " (GM command).");
			}
			catch (NumberFormatException e)
			{
				player.sendMessage("Usage: .weekly addkill <number>");
			}
			return true;
		}
		else if ((paramArray.length > 0) && "reset".equals(paramArray[0]) && player.isGM())
		{
			player.getVariables().set(PROGRESS_VAR, 0);
			player.getVariables().remove(CLAIMED_WEEK_VAR);
			player.sendMessage("Weekly Mission state reset for this character (GM command).");
			return true;
		}

		player.sendMessage("Weekly Mission: Kill " + target + " monsters.");
		player.sendMessage("Progress: " + progress + "/" + target + " | Week: " + weekKey);
		player.sendMessage("Reward: " + WeeklyMissionsConfig.WEEKLY_MISSION_EXP_REWARD + " EXP, " + WeeklyMissionsConfig.WEEKLY_MISSION_SP_REWARD + " SP.");
		player.sendMessage("Commands: .weekly claim");
		if (player.isGM())
		{
			player.sendMessage("GM Commands: .weekly setprogress <count> | .weekly addkill <count> | .weekly reset");
		}
		return true;
	}

	private String getCurrentWeekKey()
	{
		final LocalDate now = LocalDate.now();
		final WeekFields weekFields = WeekFields.of(Locale.getDefault());
		return now.get(weekFields.weekBasedYear()) + "-" + now.get(weekFields.weekOfWeekBasedYear());
	}

	private void ensureWeeklyState(Player player, String weekKey)
	{
		final String progressWeek = player.getVariables().getString(PROGRESS_WEEK_VAR, "");
		if (!weekKey.equals(progressWeek))
		{
			player.getVariables().set(PROGRESS_WEEK_VAR, weekKey);
			player.getVariables().set(PROGRESS_VAR, 0);
		}
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
