#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/auction_event_ext"
TARGET_FILE="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/AuctionEvent.ini"
BACKUP_FILE="$ADDON_DIR/.backup/AuctionEvent.ini.bak"

if [[ ! -f "$BACKUP_FILE" ]]; then
  echo "[auction-rollback] backup not found: $BACKUP_FILE"
  exit 1
fi

cp -f "$BACKUP_FILE" "$TARGET_FILE"
echo "[auction-rollback] restored AuctionEvent.ini from backup"
