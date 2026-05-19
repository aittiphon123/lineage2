#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/weekly_missions_ext"
TARGET_FILE="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/WeeklyMissions.ini"
BACKUP_FILE="$ADDON_DIR/.backup/WeeklyMissions.ini.bak"

if [[ ! -f "$BACKUP_FILE" ]]; then
  echo "[rollback] backup not found: $BACKUP_FILE"
  exit 1
fi

cp -f "$BACKUP_FILE" "$TARGET_FILE"
echo "[rollback] restored WeeklyMissions.ini from backup"
