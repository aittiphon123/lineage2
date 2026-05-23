# Addon Platform Usage Guide

> Status: Deprecated for this server migration path.

This document remains for historical reference only.

## Migration target
Use built-in integrated config mode documented in:
- `docs/systems/INTEGRATED_EVENTS_USAGE.md`

## Deprecated components
The following external pack flow is no longer the recommended deployment path for this server:
- `addons/manifest/addons.manifest.ini`
- `addons/shared/tools/deploy-all-addons.sh`
- `addons/*/tools/deploy-addon.sh`

## Why deprecated
- Built-in mode removes external pack deployment complexity.
- One source of truth (`IntegratedEvents.ini`) is easier for operators to maintain.
- Runtime output is deterministic via `tools/apply-integrated-events.sh`.
