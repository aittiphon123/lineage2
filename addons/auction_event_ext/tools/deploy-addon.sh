#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/auction_event_ext"
TARGET_DIST="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game"

"$ROOT_DIR/addons/shared/tools/preflight.sh"
mkdir -p "$TARGET_DIST/config/Custom/AuctionEventMod"
cp -f "$ADDON_DIR/config/AuctionEventAddon.ini" "$TARGET_DIST/config/Custom/AuctionEventAddon.ini"
cp -f "$ADDON_DIR/mods/default_pack/auctions.ini" "$TARGET_DIST/config/Custom/AuctionEventMod/auctions.ini"
echo "[auction-addon] deployed config and pack"
