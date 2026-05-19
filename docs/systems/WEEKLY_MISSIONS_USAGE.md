# Weekly Missions Usage Guide

## Overview
Weekly Missions support multiple objective types and configurable rewards.

## Key Config (`config/Custom/WeeklyMissions.ini`)
- `EnableWeeklyMissions`
- `MissionType = MONSTER_KILL|PVP_KILL|ITEM_COLLECT|ONLINE_TIME`
- `MissionTargetValue`
- `TargetMonsterIds`
- `RequiredItemId`, `RequiredItemCount`
- `ExpReward`, `SpReward`, `RewardItems`

## Player Command
- `.weekly` : show mission status
- `.weekly claim` : claim reward when complete

## GM Commands
- `.weekly setprogress <count>`
- `.weekly addkill <count>`
- `.weekly addkillmonster <monsterId> <count>`
- `.weekly addtime <minutes>`
- `.weekly reset`

## Example RewardItems
`RewardItems = 57,100000;1463,5000`
