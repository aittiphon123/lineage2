#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/auction_event_ext"
PACK_FILE="${1:-$ADDON_DIR/mods/default_pack/auctions.ini}"
STATE_FILE="${2:-$ADDON_DIR/.state/rotation.state}"
TARGET_FILE="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/AuctionEvent.ini"

mkdir -p "$(dirname "$STATE_FILE")"

"$ADDON_DIR/tools/validate-auction-pack.sh" "$PACK_FILE" >/dev/null

mapfile -t ids < <(grep -E '^\s*[a-zA-Z0-9_\-]+\|' "$PACK_FILE" | sed 's/^\s*//' | cut -d'|' -f1)
if [[ ${#ids[@]} -eq 0 ]]; then
  echo "[auction-rotate] no auction ids found"
  exit 1
fi

current_index=-1
if [[ -f "$STATE_FILE" ]]; then
  current_id=$(cat "$STATE_FILE" | tr -d '[:space:]')
  for i in "${!ids[@]}"; do
    if [[ "${ids[$i]}" == "$current_id" ]]; then
      current_index=$i
      break
    fi
  done
fi

next_index=$((current_index + 1))
if (( next_index >= ${#ids[@]} )); then
  next_index=0
fi
next_id="${ids[$next_index]}"

echo "$next_id" > "$STATE_FILE"
"$ADDON_DIR/tools/generate-auction-config.sh" "$PACK_FILE" "$next_id" "$TARGET_FILE"

echo "[auction-rotate] activated auction: $next_id"
