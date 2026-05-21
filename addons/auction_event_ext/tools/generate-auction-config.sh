#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/auction_event_ext"
PACK_FILE="${1:-$ADDON_DIR/mods/default_pack/auctions.ini}"
AUCTION_ID="${2:-}"
OUT_FILE="${3:-$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/AuctionEvent.ini}"

[[ -n "$AUCTION_ID" ]] || { echo "Usage: $0 <pack_file> <auction_id> [out_file]"; exit 1; }
"$ADDON_DIR/tools/validate-auction-pack.sh" "$PACK_FILE" >/dev/null
line=$(grep -E "^\s*${AUCTION_ID}\|" "$PACK_FILE" | head -n1 || true)
[[ -n "$line" ]] || { echo "[auction-generate] auction id not found: $AUCTION_ID"; exit 1; }
line="$(echo "$line" | sed 's/^\s*//;s/\s*$//')"
IFS='|' read -r id enabled dow hhmm item_id item_count start_bid min_increment duration currency announce <<< "$line"
mkdir -p "$(dirname "$OUT_FILE")"
cat > "$OUT_FILE" <<CFG
# Generated Auction Event config
EnableAuctionEvent = ${enabled}
AuctionId = ${id}
AuctionDayOfWeek = ${dow}
AuctionStartTimeUtc = ${hhmm}
AuctionItemId = ${item_id}
AuctionItemCount = ${item_count}
AuctionStartBid = ${start_bid}
AuctionMinIncrement = ${min_increment}
AuctionDurationMinutes = ${duration}
AuctionCurrencyItemId = ${currency}
AuctionAnnounce = ${announce}
CFG
echo "[auction-generate] config written: $OUT_FILE"
