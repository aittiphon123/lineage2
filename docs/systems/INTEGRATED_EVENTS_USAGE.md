# Integrated Events Usage (Built-in Server Mode)

## Goal
Use built-in server configuration for Weekly Missions and Auction Event without external addon pack deployment.

## Source of truth
- `L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/IntegratedEvents.ini`

## Apply flow
1. Edit `IntegratedEvents.ini` values (`WeeklyMissionLine`, `AuctionLine`).
2. Generate runtime files:
   - `bash tools/apply-integrated-events.sh`
3. Restart server (or reload config path used by your deployment process).

## Output files
- `L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/WeeklyMissions.ini`
- `L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/AuctionEvent.ini`

## Quick validation
- `bash tools/apply-integrated-events.sh`
- `grep -E '^MissionId\s*=\s*' L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/WeeklyMissions.ini`
- `grep -E '^AuctionId\s*=\s*' L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/AuctionEvent.ini`
