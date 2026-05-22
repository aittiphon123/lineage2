#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/auction_event_ext"
PACK_FILE="${1:-$ADDON_DIR/mods/default_pack/auctions.ini}"
AUCTION_ID="${2:-}"
TARGET_FILE="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/AuctionEvent.ini"
BACKUP_DIR="$ADDON_DIR/.backup"

if [[ -z "$AUCTION_ID" ]]; then
  echo "Usage: $0 [pack_file] <auction_id>"
  exit 1
fi

mkdir -p "$BACKUP_DIR"
if [[ -f "$TARGET_FILE" ]]; then
  cp -f "$TARGET_FILE" "$BACKUP_DIR/AuctionEvent.ini.bak"
fi

"$ADDON_DIR/tools/generate-auction-config.sh" "$PACK_FILE" "$AUCTION_ID" "$TARGET_FILE"

echo "[auction-switch] active auction switched to '$AUCTION_ID'"
echo "[auction-switch] backup file: $BACKUP_DIR/AuctionEvent.ini.bak"
