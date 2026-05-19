# Weekly Missions External Addon

This addon scaffold allows you to iterate weekly mission features without rebuilding the full core.

## Structure
- `scripts/` addon runtime scripts/classes
- `config/` addon-specific config files
- `sql/` optional schema/data migrations
- `tools/` deployment/reload helper scripts
- `docs/` design and changelog

## Deploy flow
1. Build only addon artifacts (if needed).
2. Copy addon scripts/config into server runtime directories.
3. Reload scripts (or restart game server process only).

## Notes
- Keep core (`java/org/l2jmobius/...`) unchanged whenever possible.
- Prefer voiced handlers / script listeners for rapid updates.


## Validation
- Validate mission pack syntax before deploy:
  - `addons/weekly_missions_ext/tools/validate-mission-pack.sh addons/weekly_missions_ext/mods/default_pack/missions.ini`

## Build active mission from pack

- Generate active server WeeklyMissions config from pack:
  - `addons/weekly_missions_ext/tools/generate-weekly-config.sh addons/weekly_missions_ext/mods/default_pack/missions.ini hunt_wolfs`

## Switch active mission (with backup)
- `addons/weekly_missions_ext/tools/switch-mission.sh addons/weekly_missions_ext/mods/default_pack/missions.ini hunt_wolfs`
- rollback: `addons/weekly_missions_ext/tools/rollback-mission.sh`

## Auto rotation
- Rotate to next mission in pack (with persisted state):
  - `addons/weekly_missions_ext/tools/rotate-mission.sh addons/weekly_missions_ext/mods/default_pack/missions.ini`
- Suggested: run weekly via cron/task scheduler.

## Scheduled rotation wrapper
- Safe cron/task wrapper (lock + log):
  - `addons/weekly_missions_ext/tools/schedule-weekly-rotation.sh addons/weekly_missions_ext/mods/default_pack/missions.ini`
