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
package org.l2jmobius.gameserver.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.l2jmobius.commons.util.ConfigReader;
import org.l2jmobius.commons.util.StringUtil;
import org.l2jmobius.gameserver.model.actor.enums.npc.DropType;
import org.l2jmobius.gameserver.model.actor.holders.npc.DropHolder;

/**
 * This class loads all the rates related configurations.
 * @author Mobius
 */
public class RatesConfig
{
	private static final Logger LOGGER = Logger.getLogger(RatesConfig.class.getName());
	
	// File
	private static final String RATES_CONFIG_FILE = "./config/Rates.ini";
	
	// Constants
	public static float RATE_XP;
	public static float RATE_SP;
	public static float RATE_PARTY_XP;
	public static float RATE_PARTY_SP;
	public static float RATE_INSTANCE_XP;
	public static float RATE_INSTANCE_SP;
	public static float RATE_INSTANCE_PARTY_XP;
	public static float RATE_INSTANCE_PARTY_SP;
	public static float RATE_EXTRACTABLE;
	public static int RATE_DROP_MANOR;
	public static float QUEST_ITEM_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_QUEST_REWARD;
	public static float RATE_QUEST_REWARD_XP;
	public static float RATE_QUEST_REWARD_SP;
	public static float RATE_QUEST_REWARD_FP;
	public static float RATE_QUEST_REWARD_ADENA;
	public static boolean RATE_QUEST_REWARD_USE_MULTIPLIERS;
	public static float RATE_QUEST_REWARD_POTION;
	public static float RATE_QUEST_REWARD_SCROLL;
	public static float RATE_QUEST_REWARD_RECIPE;
	public static float RATE_QUEST_REWARD_MATERIAL;
	public static int MONSTER_EXP_MAX_LEVEL_DIFFERENCE;
	public static float RATE_RAIDBOSS_POINTS;
	public static float RATE_VITALITY_EXP_MULTIPLIER;
	public static float RATE_VITALITY_EXP_PREMIUM_MULTIPLIER;
	public static int VITALITY_MAX_ITEMS_ALLOWED;
	public static float RATE_VITALITY_LOST;
	public static float RATE_VITALITY_GAIN;
	public static float RATE_KARMA_LOST;
	public static float RATE_KARMA_EXP_LOST;
	public static float RATE_SIEGE_GUARDS_PRICE;
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	public static float PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static float SINEATER_XP_RATE;
	public static float LUCKY_CHANCE_MULTIPLIER;
	public static float LIMITED_CHANCE_MULTIPLIER;
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	public static float RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_HERB_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_RAID_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_DEATH_DROP_CHANCE_MULTIPLIER;
	public static float RATE_SPOIL_DROP_CHANCE_MULTIPLIER;
	public static float RATE_HERB_DROP_CHANCE_MULTIPLIER;
	public static float RATE_RAID_DROP_CHANCE_MULTIPLIER;
	public static Map<Integer, Float> RATE_DROP_AMOUNT_BY_ID;
	public static Map<Integer, Float> RATE_DROP_CHANCE_BY_ID;
	public static int DROP_MAX_OCCURRENCES_NORMAL;
	public static int DROP_MAX_OCCURRENCES_RAIDBOSS;
	public static int DROP_ADENA_MIN_LEVEL_DIFFERENCE;
	public static int DROP_ADENA_MAX_LEVEL_DIFFERENCE;
	public static double DROP_ADENA_MIN_LEVEL_GAP_CHANCE;
	public static int DROP_ITEM_MIN_LEVEL_DIFFERENCE;
	public static int DROP_ITEM_MAX_LEVEL_DIFFERENCE;
	public static double DROP_ITEM_MIN_LEVEL_GAP_CHANCE;
	public static int EVENT_ITEM_MAX_LEVEL_DIFFERENCE;
	public static boolean BOSS_DROP_ENABLED;
	public static int BOSS_DROP_MIN_LEVEL;
	public static int BOSS_DROP_MAX_LEVEL;
	public static List<DropHolder> BOSS_DROP_LIST = new ArrayList<>();
	public static int DYE_ENCHANT_NORMAL_SKILL_ITEM_ID;
	public static int DYE_ENCHANT_NORMAL_SKILL_ITEM_COUNT;
	public static double DYE_ENCHANT_NORMAL_SKILL_CHANCE;
	public static int DYE_ENCHANT_HIDDEN_SKILL_ITEM_ID;
	public static int DYE_ENCHANT_HIDDEN_SKILL_ITEM_COUNT;
	public static double DYE_ENCHANT_HIDDEN_SKILL_CHANCE;
	public static int DYE_ENCHANT_RESET_FEE_ITEM_ID;
	public static int DYE_ENCHANT_RESET_FEE_ITEM_COUNT;
	
	public static void load()
	{
		final ConfigReader ratesConfig = new ConfigReader(RATES_CONFIG_FILE);
		RATE_XP = ratesConfig.getFloat("RateXp", 1);
		RATE_SP = ratesConfig.getFloat("RateSp", 1);
		RATE_PARTY_XP = ratesConfig.getFloat("RatePartyXp", 1);
		RATE_PARTY_SP = ratesConfig.getFloat("RatePartySp", 1);
		RATE_INSTANCE_XP = ratesConfig.getFloat("RateInstanceXp", -1);
		if (RATE_INSTANCE_XP < 0)
		{
			RATE_INSTANCE_XP = RATE_XP;
		}
		RATE_INSTANCE_SP = ratesConfig.getFloat("RateInstanceSp", -1);
		if (RATE_INSTANCE_SP < 0)
		{
			RATE_INSTANCE_SP = RATE_SP;
		}
		RATE_INSTANCE_PARTY_XP = ratesConfig.getFloat("RateInstancePartyXp", -1);
		if (RATE_INSTANCE_PARTY_XP < 0)
		{
			RATE_INSTANCE_PARTY_XP = RATE_PARTY_XP;
		}
		RATE_INSTANCE_PARTY_SP = ratesConfig.getFloat("RateInstancePartySp", -1);
		if (RATE_INSTANCE_PARTY_SP < 0)
		{
			RATE_INSTANCE_PARTY_SP = RATE_PARTY_SP;
		}
		RATE_EXTRACTABLE = ratesConfig.getFloat("RateExtractable", 1);
		RATE_DROP_MANOR = ratesConfig.getInt("RateDropManor", 1);
		QUEST_ITEM_DROP_AMOUNT_MULTIPLIER = ratesConfig.getFloat("QuestItemDropAmountMultiplier", 1);
		RATE_QUEST_REWARD = ratesConfig.getFloat("RateQuestReward", 1);
		RATE_QUEST_REWARD_XP = ratesConfig.getFloat("RateQuestRewardXP", 1);
		RATE_QUEST_REWARD_SP = ratesConfig.getFloat("RateQuestRewardSP", 1);
		RATE_QUEST_REWARD_FP = ratesConfig.getFloat("RateQuestRewardFP", 1);
		RATE_QUEST_REWARD_ADENA = ratesConfig.getFloat("RateQuestRewardAdena", 1);
		RATE_QUEST_REWARD_USE_MULTIPLIERS = ratesConfig.getBoolean("UseQuestRewardMultipliers", false);
		RATE_QUEST_REWARD_POTION = ratesConfig.getFloat("RateQuestRewardPotion", 1);
		RATE_QUEST_REWARD_SCROLL = ratesConfig.getFloat("RateQuestRewardScroll", 1);
		RATE_QUEST_REWARD_RECIPE = ratesConfig.getFloat("RateQuestRewardRecipe", 1);
		RATE_QUEST_REWARD_MATERIAL = ratesConfig.getFloat("RateQuestRewardMaterial", 1);
		MONSTER_EXP_MAX_LEVEL_DIFFERENCE = ratesConfig.getInt("MonsterExpMaxLevelDifference", 11);
		RATE_RAIDBOSS_POINTS = ratesConfig.getFloat("RateRaidbossPointsReward", 1);
		RATE_VITALITY_EXP_MULTIPLIER = ratesConfig.getFloat("RateVitalityExpMultiplier", 2);
		RATE_VITALITY_EXP_PREMIUM_MULTIPLIER = ratesConfig.getFloat("RateVitalityExpPremiumMultiplier", 3);
		VITALITY_MAX_ITEMS_ALLOWED = ratesConfig.getInt("VitalityMaxItemsAllowed", 999);
		RATE_VITALITY_LOST = ratesConfig.getFloat("RateVitalityLost", 1);
		RATE_VITALITY_GAIN = ratesConfig.getFloat("RateVitalityGain", 1);
		RATE_KARMA_LOST = ratesConfig.getFloat("RateKarmaLost", -1);
		if (RATE_KARMA_LOST == -1)
		{
			RATE_KARMA_LOST = RATE_XP;
		}
		RATE_KARMA_EXP_LOST = ratesConfig.getFloat("RateKarmaExpLost", 1);
		RATE_SIEGE_GUARDS_PRICE = ratesConfig.getFloat("RateSiegeGuardsPrice", 1);
		PLAYER_DROP_LIMIT = ratesConfig.getInt("PlayerDropLimit", 3);
		PLAYER_RATE_DROP = ratesConfig.getInt("PlayerRateDrop", 5);
		PLAYER_RATE_DROP_ITEM = ratesConfig.getInt("PlayerRateDropItem", 70);
		PLAYER_RATE_DROP_EQUIP = ratesConfig.getInt("PlayerRateDropEquip", 25);
		PLAYER_RATE_DROP_EQUIP_WEAPON = ratesConfig.getInt("PlayerRateDropEquipWeapon", 5);
		PET_XP_RATE = ratesConfig.getFloat("PetXpRate", 1);
		PET_FOOD_RATE = ratesConfig.getInt("PetFoodRate", 1);
		SINEATER_XP_RATE = ratesConfig.getFloat("SinEaterXpRate", 1);
		LUCKY_CHANCE_MULTIPLIER = ratesConfig.getFloat("LuckyChanceMultiplier", 1);
		LIMITED_CHANCE_MULTIPLIER = ratesConfig.getFloat("LimitedChanceMultiplier", 1);
		KARMA_DROP_LIMIT = ratesConfig.getInt("KarmaDropLimit", 10);
		KARMA_RATE_DROP = ratesConfig.getInt("KarmaRateDrop", 70);
		KARMA_RATE_DROP_ITEM = ratesConfig.getInt("KarmaRateDropItem", 50);
		KARMA_RATE_DROP_EQUIP = ratesConfig.getInt("KarmaRateDropEquip", 40);
		KARMA_RATE_DROP_EQUIP_WEAPON = ratesConfig.getInt("KarmaRateDropEquipWeapon", 10);
		RATE_DEATH_DROP_AMOUNT_MULTIPLIER = ratesConfig.getFloat("DeathDropAmountMultiplier", 1);
		RATE_SPOIL_DROP_AMOUNT_MULTIPLIER = ratesConfig.getFloat("SpoilDropAmountMultiplier", 1);
		RATE_HERB_DROP_AMOUNT_MULTIPLIER = ratesConfig.getFloat("HerbDropAmountMultiplier", 1);
		RATE_RAID_DROP_AMOUNT_MULTIPLIER = ratesConfig.getFloat("RaidDropAmountMultiplier", 1);
		RATE_DEATH_DROP_CHANCE_MULTIPLIER = ratesConfig.getFloat("DeathDropChanceMultiplier", 1);
		RATE_SPOIL_DROP_CHANCE_MULTIPLIER = ratesConfig.getFloat("SpoilDropChanceMultiplier", 1);
		RATE_HERB_DROP_CHANCE_MULTIPLIER = ratesConfig.getFloat("HerbDropChanceMultiplier", 1);
		RATE_RAID_DROP_CHANCE_MULTIPLIER = ratesConfig.getFloat("RaidDropChanceMultiplier", 1);
		final String[] dropAmountMultiplier = ratesConfig.getString("DropAmountMultiplierByItemId", "").split(";");
		RATE_DROP_AMOUNT_BY_ID = new HashMap<>(dropAmountMultiplier.length);
		if (!dropAmountMultiplier[0].isEmpty())
		{
			for (String item : dropAmountMultiplier)
			{
				final String[] itemSplit = item.split(",");
				if (itemSplit.length != 2)
				{
					LOGGER.warning(StringUtil.concat("Config.load(): invalid config property -> DropAmountMultiplierByItemId \"", item, "\""));
				}
				else
				{
					try
					{
						RATE_DROP_AMOUNT_BY_ID.put(Integer.parseInt(itemSplit[0]), Float.parseFloat(itemSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!item.isEmpty())
						{
							LOGGER.warning(StringUtil.concat("Config.load(): invalid config property -> DropAmountMultiplierByItemId \"", item, "\""));
						}
					}
				}
			}
		}
		final String[] dropChanceMultiplier = ratesConfig.getString("DropChanceMultiplierByItemId", "").split(";");
		RATE_DROP_CHANCE_BY_ID = new HashMap<>(dropChanceMultiplier.length);
		if (!dropChanceMultiplier[0].isEmpty())
		{
			for (String item : dropChanceMultiplier)
			{
				final String[] itemSplit = item.split(",");
				if (itemSplit.length != 2)
				{
					LOGGER.warning(StringUtil.concat("Config.load(): invalid config property -> DropChanceMultiplierByItemId \"", item, "\""));
				}
				else
				{
					try
					{
						RATE_DROP_CHANCE_BY_ID.put(Integer.parseInt(itemSplit[0]), Float.parseFloat(itemSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!item.isEmpty())
						{
							LOGGER.warning(StringUtil.concat("Config.load(): invalid config property -> DropChanceMultiplierByItemId \"", item, "\""));
						}
					}
				}
			}
		}
		DROP_MAX_OCCURRENCES_NORMAL = ratesConfig.getInt("DropMaxOccurrencesNormal", 2);
		DROP_MAX_OCCURRENCES_RAIDBOSS = ratesConfig.getInt("DropMaxOccurrencesRaidboss", 7);
		DROP_ADENA_MIN_LEVEL_DIFFERENCE = ratesConfig.getInt("DropAdenaMinLevelDifference", 8);
		DROP_ADENA_MAX_LEVEL_DIFFERENCE = ratesConfig.getInt("DropAdenaMaxLevelDifference", 15);
		DROP_ADENA_MIN_LEVEL_GAP_CHANCE = ratesConfig.getDouble("DropAdenaMinLevelGapChance", 10);
		DROP_ITEM_MIN_LEVEL_DIFFERENCE = ratesConfig.getInt("DropItemMinLevelDifference", 5);
		DROP_ITEM_MAX_LEVEL_DIFFERENCE = ratesConfig.getInt("DropItemMaxLevelDifference", 10);
		DROP_ITEM_MIN_LEVEL_GAP_CHANCE = ratesConfig.getDouble("DropItemMinLevelGapChance", 10);
		EVENT_ITEM_MAX_LEVEL_DIFFERENCE = ratesConfig.getInt("EventItemMaxLevelDifference", 9);
		BOSS_DROP_ENABLED = ratesConfig.getBoolean("BossDropEnable", false);
		BOSS_DROP_MIN_LEVEL = ratesConfig.getInt("BossDropMinLevel", 85);
		BOSS_DROP_MAX_LEVEL = ratesConfig.getInt("BossDropMaxLevel", 999);
		BOSS_DROP_LIST.clear();
		for (String s : ratesConfig.getString("BossDropList", "").trim().split(";"))
		{
			if (s.isEmpty())
			{
				continue;
			}
			BOSS_DROP_LIST.add(new DropHolder(DropType.DROP, Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1]), Integer.parseInt(s.split(",")[2]), (Double.parseDouble(s.split(",")[3]))));
		}
		
		DYE_ENCHANT_NORMAL_SKILL_ITEM_ID = ratesConfig.getInt("DyeEnchantNormalSkillItemId", 80762);
		DYE_ENCHANT_NORMAL_SKILL_ITEM_COUNT = ratesConfig.getInt("DyeEnchantNormalSkillItemCount", 50);
		DYE_ENCHANT_NORMAL_SKILL_CHANCE = ratesConfig.getDouble("DyeEnchantNormalSkillChance", 75);
		DYE_ENCHANT_HIDDEN_SKILL_ITEM_ID = ratesConfig.getInt("DyeEnchantHiddenSkillItemId", 48472);
		DYE_ENCHANT_HIDDEN_SKILL_ITEM_COUNT = ratesConfig.getInt("DyeEnchantHiddenSkillItemCount", 15000);
		DYE_ENCHANT_HIDDEN_SKILL_CHANCE = ratesConfig.getDouble("DyeEnchantHiddenSkillChance", 25);
		DYE_ENCHANT_RESET_FEE_ITEM_ID = ratesConfig.getInt("DyeEnchantResetFeeItemId", 48472);
		DYE_ENCHANT_RESET_FEE_ITEM_COUNT = ratesConfig.getInt("DyeEnchantResetFeeItemCount", 1500);
	}
}
