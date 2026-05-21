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


## 7) Server startup fails at script execution (core/scripts mismatch)
Symptoms:
- `Failed to execute script list` during GameServer startup.
- Many compile errors such as missing classes/methods in script sources (for example `Config`, `Quest`, `addTalkId`, `addAttackId`, `startQuestTimer`).

Root cause (common):
- Mixed artifact set: `dist/game/data/scripts` copied from a different Mobius chronicle/branch than the running core jars.
- Partial overwrite after update (old files remain from previous datapack build).

Immediate recovery steps:
1. Stop server process completely.
2. Backup current `dist/game/data/scripts` directory.
3. Replace script directory with a clean set from the exact same build/chronicle as the server core.
4. Ensure runtime jars and script sources come from the same package snapshot.
5. Start server again and verify there are no script compilation errors.

Verification commands:
- `addons/shared/tools/check-all.sh`
- `addons/shared/tools/check-invalid-fixtures.sh`
- `CHECK_ALL_OUTPUT=json addons/shared/tools/check-all.sh`

Notes:
- Warnings about `source value 8 is obsolete` are non-fatal compiler warnings.
- The blocking issue is API mismatch between script code and core classes, not addon policy/pack validators.
