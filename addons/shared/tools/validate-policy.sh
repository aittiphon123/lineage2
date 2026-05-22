#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
WEEKLY_PACK="${1:-$ROOT_DIR/addons/weekly_missions_ext/mods/default_pack/missions.ini}"
AUCTION_PACK="${2:-$ROOT_DIR/addons/auction_event_ext/mods/default_pack/auctions.ini}"
STRICT_MODE="${POLICY_STRICT:-0}"

ok=1

require_uint() {
  local label="$1"
  local value="$2"
  local line="$3"
  if ! [[ "$value" =~ ^[0-9]+$ ]]; then
    echo "[policy] $label must be an unsigned integer: $value (line: $line)"
    ok=0
    return 1
  fi
  return 0
}

# Weekly policy caps
while IFS= read -r line; do
  line="$(echo "$line" | sed 's/^\s*//;s/\s*$//')"
  IFS='|' read -r id type target target_ids req_item req_count exp sp rewards <<< "$line"
  [[ -z "$id" ]] && continue
  require_uint "weekly:$id target" "$target" "$line" || continue
  require_uint "weekly:$id exp" "$exp" "$line" || continue
  require_uint "weekly:$id sp" "$sp" "$line" || continue
  if [[ "$STRICT_MODE" == "1" ]]; then
    case "$type" in
      MONSTER_KILL|PVP_KILL|ITEM_COLLECT|ONLINE_TIME) ;;
      *) echo "[policy] weekly $id invalid mission type: $type"; ok=0 ;;
    esac
    require_uint "weekly:$id required_item_id" "${req_item:-0}" "$line" || true
    require_uint "weekly:$id required_item_count" "${req_count:-0}" "$line" || true
  fi
  if (( exp > 10000000 )); then echo "[policy] weekly $id exp too high: $exp"; ok=0; fi
  if (( sp > 2000000 )); then echo "[policy] weekly $id sp too high: $sp"; ok=0; fi
  if (( target < 1 || target > 100000 )); then echo "[policy] weekly $id target out of range: $target"; ok=0; fi
done < <(grep -E '^\s*[a-zA-Z0-9_\-]+\|' "$WEEKLY_PACK")

# Auction policy caps
while IFS= read -r line; do
  line="$(echo "$line" | sed 's/^\s*//;s/\s*$//')"
  IFS='|' read -r id enabled dow hhmm item_id item_count start_bid min_inc dur currency ann <<< "$line"
  [[ -z "$id" ]] && continue
  require_uint "auction:$id start_bid" "$start_bid" "$line" || continue
  require_uint "auction:$id min_increment" "$min_inc" "$line" || continue
  require_uint "auction:$id duration" "$dur" "$line" || continue
  if [[ "$STRICT_MODE" == "1" ]]; then
    [[ "$enabled" == "true" || "$enabled" == "false" ]] || { echo "[policy] auction $id enabled must be true/false"; ok=0; }
    [[ "$ann" == "true" || "$ann" == "false" ]] || { echo "[policy] auction $id announce must be true/false"; ok=0; }
    case "$dow" in MON|TUE|WED|THU|FRI|SAT|SUN|DAILY) ;; *) echo "[policy] auction $id invalid day-of-week: $dow"; ok=0 ;; esac
    [[ "$hhmm" =~ ^[0-2][0-9][0-5][0-9]$ ]] || { echo "[policy] auction $id invalid start_hhmm_utc: $hhmm"; ok=0; }
  fi
  if (( start_bid < min_inc )); then echo "[policy] auction $id start_bid < min_increment"; ok=0; fi
  if (( dur < 5 || dur > 1440 )); then echo "[policy] auction $id duration out of range: $dur"; ok=0; fi
done < <(grep -E '^\s*[a-zA-Z0-9_\-]+\|' "$AUCTION_PACK")

[[ "$ok" -eq 1 ]] || { echo "[policy] validation failed"; exit 1; }
echo "[policy] validation ok"
