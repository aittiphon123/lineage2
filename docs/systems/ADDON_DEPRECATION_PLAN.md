# Addon Deprecation Plan (CT 2.6 HighFive)

## Decision
For this server line, integrated events mode is the active path.
External addon pack deployment remains legacy/reference only.

## Legacy scope
- `addons/auction_event_ext/*`
- `addons/weekly_missions_ext/*`
- `addons/shared/tools/*`

## Policy
- Do not use addon deploy scripts in production for this server line.
- Keep legacy scripts only for migration fallback/history.
- All production operations must use:
  - `tools/run-integrated-events-ready.sh`
  - `tools/server-integrated-events-deploy.sh`
  - `tools/server-integrated-events-rollback.sh`

## Exit criteria for full removal (future)
1. 2 release cycles with integrated mode only.
2. No rollback to addon-pack path in incident history.
3. Java sanity and smoke checks stable across those releases.
