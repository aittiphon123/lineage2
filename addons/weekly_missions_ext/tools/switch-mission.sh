#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/weekly_missions_ext"
PACK_FILE="${1:-$ADDON_DIR/mods/default_pack/missions.ini}"
MISSION_ID="${2:-}"
TARGET_FILE="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/WeeklyMissions.ini"
BACKUP_DIR="$ADDON_DIR/.backup"

if [[ -z "$MISSION_ID" ]]; then
  echo "Usage: $0 [pack_file] <mission_id>"
  exit 1
fi

mkdir -p "$BACKUP_DIR"
if [[ -f "$TARGET_FILE" ]]; then
  cp -f "$TARGET_FILE" "$BACKUP_DIR/WeeklyMissions.ini.bak"
fi

"$ADDON_DIR/tools/generate-weekly-config.sh" "$PACK_FILE" "$MISSION_ID" "$TARGET_FILE"

echo "[switch] active mission switched to '$MISSION_ID'"
echo "[switch] backup file: $BACKUP_DIR/WeeklyMissions.ini.bak"
