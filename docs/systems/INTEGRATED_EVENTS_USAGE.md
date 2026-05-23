# Integrated Events Usage (Built-in Server Mode)

## Goal
Use built-in server configuration for Weekly Missions and Auction Event without external addon pack deployment.

## Source of truth
- `L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/IntegratedEvents.ini`

## Apply flow
1. Edit `IntegratedEvents.ini` values (`WeeklyMissionLine`, `AuctionLine`).
2. Run full ready pipeline:
   - `bash tools/run-integrated-events-ready.sh`
   - Windows one-click: `tools/run-integrated-events-ready.cmd`
3. Deploy to server config directory:
   - `bash tools/server-integrated-events-deploy.sh`
   - Windows one-click: `tools/server-integrated-events-deploy.cmd`
   - Optional custom target and restart command:
     - `bash tools/server-integrated-events-deploy.sh /path/to/dist/game "systemctl restart l2j-game"`
4. Rollback latest snapshot (if needed):
   - `bash tools/server-integrated-events-rollback.sh`
   - Windows one-click: `tools/server-integrated-events-rollback.cmd`
   - Optional custom target and restart command:
     - `bash tools/server-integrated-events-rollback.sh /path/to/dist/game "systemctl restart l2j-game"`
5. Prepare runtime files only (optional):
   - `bash tools/prepare-integrated-events.sh`
   - Windows one-click: `tools/prepare-integrated-events.cmd`
6. (Optional) Generate only:
   - `bash tools/apply-integrated-events.sh`
   - Windows one-click: `tools/apply-integrated-events.cmd`
7. Restart server (or reload config path used by your deployment process).

## Output files
- `L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/WeeklyMissions.ini`
- `L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/AuctionEvent.ini`

## Quick validation
- `bash tools/run-integrated-events-ready.sh`
- `bash tools/validate-integrated-events.sh`
- `bash tools/prepare-integrated-events.sh`
- `grep -E '^MissionId\s*=\s*' L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/WeeklyMissions.ini`
- `grep -E '^AuctionId\s*=\s*' L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/AuctionEvent.ini`

## Production run recommendation
- Use `run-integrated-events-ready` immediately before restart/deploy window.
- Prefer `server-integrated-events-deploy.sh` in operations so copy + readiness checks are always done together.
- `server-integrated-events-deploy.sh` creates automatic snapshots in `config/Custom/.integrated-backups` for rollback.
- Keep `IntegratedEvents.ini` in version control and review diffs before applying.
