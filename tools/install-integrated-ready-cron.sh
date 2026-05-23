#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SCHEDULE="${1:-30 3 * * 1}"

line="$SCHEDULE cd $ROOT_DIR && bash tools/run-integrated-events-ready.sh >> $ROOT_DIR/.state/integrated-ready.cron.log 2>&1"

( crontab -l 2>/dev/null | grep -v 'run-integrated-events-ready.sh' || true; echo "$line" ) | crontab -

echo "[integrated-cron] installed: $line"
