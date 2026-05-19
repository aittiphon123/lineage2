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
