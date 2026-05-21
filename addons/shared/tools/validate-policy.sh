#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
WEEKLY_PACK="${1:-$ROOT_DIR/addons/weekly_missions_ext/mods/default_pack/missions.ini}"
AUCTION_PACK="${2:-$ROOT_DIR/addons/auction_event_ext/mods/default_pack/auctions.ini}"

ok=1

# Weekly policy caps
while IFS= read -r line; do
  line="$(echo "$line" | sed 's/^\s*//;s/\s*$//')"
  IFS='|' read -r id type target target_ids req_item req_count exp sp rewards <<< "$line"
  [[ -z "$id" ]] && continue
  if (( exp > 10000000 )); then echo "[policy] weekly $id exp too high: $exp"; ok=0; fi
  if (( sp > 2000000 )); then echo "[policy] weekly $id sp too high: $sp"; ok=0; fi
  if (( target < 1 || target > 100000 )); then echo "[policy] weekly $id target out of range: $target"; ok=0; fi
done < <(grep -E '^\s*[a-zA-Z0-9_\-]+\|' "$WEEKLY_PACK")

# Auction policy caps
while IFS= read -r line; do
  line="$(echo "$line" | sed 's/^\s*//;s/\s*$//')"
  IFS='|' read -r id enabled dow hhmm item_id item_count start_bid min_inc dur currency ann <<< "$line"
  [[ -z "$id" ]] && continue
  if (( start_bid < min_inc )); then echo "[policy] auction $id start_bid < min_increment"; ok=0; fi
  if (( dur < 5 || dur > 1440 )); then echo "[policy] auction $id duration out of range: $dur"; ok=0; fi
done < <(grep -E '^\s*[a-zA-Z0-9_\-]+\|' "$AUCTION_PACK")

[[ "$ok" -eq 1 ]] || { echo "[policy] validation failed"; exit 1; }
echo "[policy] validation ok"
