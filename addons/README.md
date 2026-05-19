# External Addons Platform

This folder is the long-term extension platform for custom events/features **without rebuilding core source**.

## Goals
- Build-once core, iterate via addon packs.
- Enable/disable addons by manifest.
- Standardized deploy/validate scripts.

## Layout
- `manifest/` global enable list
- `shared/tools/` platform-level scripts
- `<addon_name>/` each addon pack (config/scripts/sql/docs/tools)

## Standard Addon Contract
Each addon should contain:
- `config/`
- `scripts/`
- `sql/`
- `docs/`
- `tools/deploy-addon.sh`

## Deploy all enabled addons
```bash
addons/shared/tools/deploy-all-addons.sh
```

## Future integration
- Optional runtime loader can read `manifest/addons.manifest.ini` and apply hot-reload per addon.


## User Guides
- `docs/systems/AUTOPLAY_USAGE.md`
- `docs/systems/CATCHUP_EXP_USAGE.md`
- `docs/systems/WEEKLY_MISSIONS_USAGE.md`
- `docs/systems/ADDON_PLATFORM_USAGE.md`
