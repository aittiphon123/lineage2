#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/weekly_missions_ext"
PACK_FILE="${1:-$ADDON_DIR/mods/default_pack/missions.ini}"
LOCK_FILE="${2:-$ADDON_DIR/.state/rotation.lock}"
LOG_FILE="${3:-$ADDON_DIR/.state/rotation.log}"

mkdir -p "$(dirname "$LOCK_FILE")"
mkdir -p "$(dirname "$LOG_FILE")"

exec 9>"$LOCK_FILE"
if ! flock -n 9; then
  echo "[schedule] another rotation job is running" | tee -a "$LOG_FILE"
  exit 0
fi

ts="$(date -u +'%Y-%m-%dT%H:%M:%SZ')"
echo "[$ts] rotation start" >> "$LOG_FILE"

if "$ADDON_DIR/tools/rotate-mission.sh" "$PACK_FILE" >> "$LOG_FILE" 2>&1; then
  ts2="$(date -u +'%Y-%m-%dT%H:%M:%SZ')"
  echo "[$ts2] rotation success" >> "$LOG_FILE"
else
  ts2="$(date -u +'%Y-%m-%dT%H:%M:%SZ')"
  echo "[$ts2] rotation failed" >> "$LOG_FILE"
  exit 1
fi
