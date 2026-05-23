#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
SCHED_SCRIPT="$ROOT_DIR/addons/weekly_missions_ext/tools/schedule-weekly-rotation.sh"
PACK_FILE="$ROOT_DIR/addons/weekly_missions_ext/mods/default_pack/missions.ini"

[[ -x "$SCHED_SCRIPT" ]] || { echo "[cron-install] scheduler script missing: $SCHED_SCRIPT"; exit 1; }

CRON_CMD="0 0 * * 1 $SCHED_SCRIPT $PACK_FILE"
( crontab -l 2>/dev/null | grep -v "$SCHED_SCRIPT"; echo "$CRON_CMD" ) | crontab -

echo "[cron-install] installed weekly rotation cron: $CRON_CMD"
