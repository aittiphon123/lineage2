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


## Weekly auto-rotation (optional)
- `addons/weekly_missions_ext/tools/rotate-mission.sh addons/weekly_missions_ext/mods/default_pack/missions.ini`
- Run once per week using cron/Task Scheduler to switch to the next mission automatically.


## Production scheduler wrapper
- Use lock/log wrapper for cron or Task Scheduler:
  - `addons/weekly_missions_ext/tools/schedule-weekly-rotation.sh addons/weekly_missions_ext/mods/default_pack/missions.ini`


## Incident handling
- See `docs/systems/INCIDENT_RUNBOOK.md` for recovery and rollback operations.


## Production setup checklist
1. Run preflight + manifest validation
2. Validate mission pack
3. Switch/Generate one active mission
4. Deploy enabled addons
5. Install weekly scheduler
6. Run rollback drill once

### Scheduler install helpers
- Linux cron helper:
  - `addons/shared/tools/install-cron-weekly-rotation.sh`
- Windows Task Scheduler helper (PowerShell):
  - `powershell -ExecutionPolicy Bypass -File addons/shared/tools/install-task-weekly-rotation.ps1 -RepoRoot "C:\lineage2"`


## Health & policy checks
- Addon health summary:
  - `addons/shared/tools/addon-health.sh`
- Policy validation for packs:
  - `addons/shared/tools/validate-policy.sh`
  - Strict schema guardrails: `POLICY_STRICT=1 addons/shared/tools/validate-policy.sh`


## One-command validation
- GitHub Actions CI workflow:
  - `.github/workflows/addons-ops-validate.yml`
- Negative regression fixture checks:
  - `addons/shared/tools/check-invalid-fixtures.sh`
- Run the full operator check pipeline before deploy:
  - `addons/shared/tools/check-all.sh` (includes strict policy mode)
- Optional explicit inputs:
  - `addons/shared/tools/check-all.sh <manifest.ini> <missions.ini> <auctions.ini>`
- JSON summary output for CI/automation:
  - `CHECK_ALL_OUTPUT=json addons/shared/tools/check-all.sh`

