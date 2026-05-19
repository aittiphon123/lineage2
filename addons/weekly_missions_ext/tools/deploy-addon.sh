#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/weekly_missions_ext"
TARGET_DIST="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game"

echo "[addon] Deploying Weekly Missions external addon scaffold..."
"$ADDON_DIR/tools/validate-mission-pack.sh" "$ADDON_DIR/mods/default_pack/missions.ini"

mkdir -p "$TARGET_DIST/config/Custom"
cp -f "$ADDON_DIR/config/WeeklyMissionsAddon.ini" "$TARGET_DIST/config/Custom/WeeklyMissionsAddon.ini"

echo "[addon] Config deployed to: $TARGET_DIST/config/Custom/WeeklyMissionsAddon.ini"
mkdir -p "$TARGET_DIST/config/Custom/WeeklyMissionsMod"
cp -f "$ADDON_DIR/mods/default_pack/missions.ini" "$TARGET_DIST/config/Custom/WeeklyMissionsMod/missions.ini"

echo "[addon] Mission pack deployed to: $TARGET_DIST/config/Custom/WeeklyMissionsMod/missions.ini"
echo "[addon] Next: wire script loader/reload command as needed."
