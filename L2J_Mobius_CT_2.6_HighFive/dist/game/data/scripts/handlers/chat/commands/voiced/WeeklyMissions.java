/*
 * Copyright (c) 2013 L2jMobius
 */
package handlers.chat.commands.voiced;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.l2jmobius.gameserver.config.custom.WeeklyMissionsConfig;
import org.l2jmobius.gameserver.enums.ItemProcessType;
import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;

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
		final int effectiveTarget = Math.max(1, WeeklyMissionsConfig.WEEKLY_MISSION_TARGET_VALUE);
		progress = Math.min(progress, effectiveTarget);

		if ((paramArray.length > 0) && "claim".equals(paramArray[0]))
		{
			if (weekKey.equals(player.getVariables().getString(CLAIMED_WEEK_VAR, "")))
			{
				player.sendMessage("Weekly Mission reward already claimed for week " + weekKey + ".");
				return true;
			}
			if (progress < effectiveTarget)
			{
				player.sendMessage("Weekly Mission is not complete yet. Progress: " + progress + "/" + effectiveTarget + ".");
				return true;
			}
			if ((WeeklyMissionsConfig.WEEKLY_MISSION_TYPE == WeeklyMissionsConfig.MissionType.ITEM_COLLECT) && (WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_ID > 0) && (WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_COUNT > 0))
			{
				final Item requiredItem = player.getInventory().getItemByItemId(WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_ID);
				final long currentCount = (requiredItem == null) ? 0 : requiredItem.getCount();
				if (currentCount < WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_COUNT)
				{
					player.sendMessage("Required items are not enough for claim (" + currentCount + "/" + WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_COUNT + ").");
					return true;
				}
				player.destroyItemByItemId(ItemProcessType.REWARD, WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_ID, WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_COUNT, player, true);
			}
			player.addExpAndSp(WeeklyMissionsConfig.WEEKLY_MISSION_EXP_REWARD, WeeklyMissionsConfig.WEEKLY_MISSION_SP_REWARD);
			for (WeeklyMissionsConfig.RewardItem rewardItem : WeeklyMissionsConfig.WEEKLY_MISSION_ITEM_REWARDS)
			{
				player.addItem(ItemProcessType.REWARD, rewardItem.getId(), rewardItem.getCount(), player, true);
			}
			player.getVariables().set(CLAIMED_WEEK_VAR, weekKey);
			player.sendMessage("Weekly Mission reward claimed: +" + WeeklyMissionsConfig.WEEKLY_MISSION_EXP_REWARD + " EXP, +" + WeeklyMissionsConfig.WEEKLY_MISSION_SP_REWARD + " SP.");
			if (WeeklyMissionsConfig.WEEKLY_MISSION_ITEM_REWARDS.length > 0)
			{
				player.sendMessage("Weekly Mission item rewards have been sent to your inventory.");
			}
			return true;
		}
		else if ((paramArray.length > 1) && "setprogress".equals(paramArray[0]) && player.isGM())
		{
			try
			{
				progress = Math.min(effectiveTarget, Math.max(0, Integer.parseInt(paramArray[1])));
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
				progress = Math.min(effectiveTarget, progress + add);
				player.getVariables().set(PROGRESS_VAR, progress);
				player.sendMessage("Weekly Mission progress increased to " + progress + "/" + effectiveTarget + " (GM command).");
			}
			catch (NumberFormatException e)
			{
				player.sendMessage("Usage: .weekly addkill <number>");
			}
			return true;
		}
		else if ((paramArray.length > 2) && "addkillmonster".equals(paramArray[0]) && player.isGM())
		{
			try
			{
				final int monsterId = Integer.parseInt(paramArray[1]);
				final int add = Math.max(0, Integer.parseInt(paramArray[2]));
				if ((WeeklyMissionsConfig.WEEKLY_MISSION_TARGET_MONSTER_IDS.length > 0) && !isTargetMonster(monsterId))
				{
					player.sendMessage("Monster id " + monsterId + " is not in TargetMonsterIds list.");
					return true;
				}
				progress = Math.min(effectiveTarget, progress + add);
				player.getVariables().set(PROGRESS_VAR, progress);
				player.sendMessage("Weekly Mission progress increased to " + progress + "/" + effectiveTarget + " for monster id " + monsterId + " (GM command).");
			}
			catch (NumberFormatException e)
			{
				player.sendMessage("Usage: .weekly addkillmonster <monsterId> <count>");
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

		player.sendMessage("Weekly Mission Type: " + getMissionTypeDescription() + ".");
		player.sendMessage("Weekly Mission Target: " + effectiveTarget + ".");
		player.sendMessage("Progress: " + progress + "/" + effectiveTarget + " | Week: " + weekKey);
		player.sendMessage("Reward: " + WeeklyMissionsConfig.WEEKLY_MISSION_EXP_REWARD + " EXP, " + WeeklyMissionsConfig.WEEKLY_MISSION_SP_REWARD + " SP.");
		if (WeeklyMissionsConfig.WEEKLY_MISSION_ITEM_REWARDS.length > 0)
		{
			player.sendMessage("Reward Items: " + getItemRewardDescription());
		}
		player.sendMessage("Commands: .weekly claim");
		if (WeeklyMissionsConfig.WEEKLY_MISSION_TARGET_MONSTER_IDS.length > 0)
		{
			player.sendMessage("Target Monster IDs: " + getTargetMonsterIdsDescription());
		}
		if ((WeeklyMissionsConfig.WEEKLY_MISSION_TYPE == WeeklyMissionsConfig.MissionType.ITEM_COLLECT) && (WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_ID > 0))
		{
			player.sendMessage("Required Item: " + WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_ID + " x" + WeeklyMissionsConfig.WEEKLY_MISSION_REQUIRED_ITEM_COUNT + " (consumed on claim).");
		}
		if (player.isGM())
		{
			player.sendMessage("GM Commands: .weekly setprogress <count> | .weekly addkill <count> | .weekly addkillmonster <monsterId> <count> | .weekly reset");
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

	private String getMissionTypeDescription()
	{
		switch (WeeklyMissionsConfig.WEEKLY_MISSION_TYPE)
		{
			case PVP_KILL:
				return "PvP Kills";
			case ITEM_COLLECT:
				return "Item Collection";
			case ONLINE_TIME:
				return "Online Time";
			case MONSTER_KILL:
			default:
				return "Monster Kills";
		}
	}

	private String getItemRewardDescription()
	{
		final StringBuilder sb = new StringBuilder();
		for (WeeklyMissionsConfig.RewardItem rewardItem : WeeklyMissionsConfig.WEEKLY_MISSION_ITEM_REWARDS)
		{
			if (sb.length() > 0)
			{
				sb.append(" | ");
			}
			sb.append(rewardItem.getId()).append(" x").append(rewardItem.getCount());
		}
		return sb.toString();
	}

	private boolean isTargetMonster(int monsterId)
	{
		for (int id : WeeklyMissionsConfig.WEEKLY_MISSION_TARGET_MONSTER_IDS)
		{
			if (id == monsterId)
			{
				return true;
			}
		}
		return false;
	}

	private String getTargetMonsterIdsDescription()
	{
		final StringBuilder sb = new StringBuilder();
		for (int id : WeeklyMissionsConfig.WEEKLY_MISSION_TARGET_MONSTER_IDS)
		{
			if (sb.length() > 0)
			{
				sb.append(",");
			}
			sb.append(id);
		}
		return sb.toString();
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
