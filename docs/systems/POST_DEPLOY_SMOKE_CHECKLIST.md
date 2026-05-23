# Post-deploy Smoke Checklist (Integrated Events)

## Preconditions
- Integrated pipeline completed successfully (`run-integrated-events-ready` and deploy script).
- Server restarted with the updated `config/Custom` files.

## In-game checks (5-10 minutes)
1. Weekly mission command opens and shows active mission id.
2. Weekly claim path works for eligible player and reward inventory changes as expected.
3. Auction event schedule shows correct `AuctionId` and start time from config.
4. AutoPlay PvE mode can start in PvE zone.
5. AutoPlay start is blocked in PvP/Siege zone when configured.
6. AutoPlay stop-on-zone rule triggers when entering restricted zone.
7. `.play stats` returns telemetry counters and values increase on blocked attempts.

## Failure handling
- If config mismatch or bad runtime behavior appears, rollback immediately:
  - `bash tools/server-integrated-events-rollback.sh <server_dist_game_path> "<restart_command>"`
- Re-run integrated readiness and deploy after fixing `IntegratedEvents.ini`.
