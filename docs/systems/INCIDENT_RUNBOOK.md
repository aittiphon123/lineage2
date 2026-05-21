# Incident Runbook (Addons / Weekly Missions)

## 1) Mission pack validation fails
- Run validator and inspect invalid line:
  - `addons/weekly_missions_ext/tools/validate-mission-pack.sh <pack>`
- Fix format and re-run.

## 2) Wrong mission deployed
- Rollback previous active config:
  - `addons/weekly_missions_ext/tools/rollback-mission.sh`

## 3) Rotation executed unexpectedly
- Check lock/log files:
  - `.state/rotation.lock`
  - `.state/rotation.log`
  - `.state/rotation.jsonl`
- Weekly guard prevents < 6 days re-rotation.

## 4) Deploy-all fails
- Run preflight and manifest validation manually:
  - `addons/shared/tools/preflight.sh`
  - `addons/shared/tools/validate-manifest.sh`

## 5) Recovery checklist
- Validate pack
- Generate mission config
- Switch with backup
- Verify `.weekly` in-game
- Monitor logs for 15 minutes


## 6) Weekly readiness drill (recommended)
- Run once after deployment:
  - validate pack
  - switch mission
  - claim flow smoke test
  - rollback mission
- Keep screenshots/log snippets in ops notes.
