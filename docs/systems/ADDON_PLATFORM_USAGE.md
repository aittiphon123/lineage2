# Addon Platform Usage Guide

## Goal
Add/operate new event systems without rebuilding core source.

## Main Paths
- `addons/manifest/addons.manifest.ini`
- `addons/shared/tools/deploy-all-addons.sh`
- `addons/<addon_name>/tools/deploy-addon.sh`

## Weekly Missions Addon Flow
1. Edit mission pack: `addons/weekly_missions_ext/mods/default_pack/missions.ini`
2. Validate:
   - `addons/weekly_missions_ext/tools/validate-mission-pack.sh addons/weekly_missions_ext/mods/default_pack/missions.ini`
3. Generate active server config:
   - `addons/weekly_missions_ext/tools/generate-weekly-config.sh addons/weekly_missions_ext/mods/default_pack/missions.ini <mission_id>`
4. Deploy:
   - `addons/weekly_missions_ext/tools/deploy-addon.sh`
5. Switch active mission with backup:
   - `addons/weekly_missions_ext/tools/switch-mission.sh addons/weekly_missions_ext/mods/default_pack/missions.ini <mission_id>`
6. Rollback if needed:
   - `addons/weekly_missions_ext/tools/rollback-mission.sh`

## New Event Template
Copy `addons/event_template` to create new addon packs quickly.
