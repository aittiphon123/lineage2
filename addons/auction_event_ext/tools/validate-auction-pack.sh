#!/usr/bin/env bash
set -euo pipefail
PACK_FILE="${1:-addons/auction_event_ext/mods/default_pack/auctions.ini}"

[[ -f "$PACK_FILE" ]] || { echo "[auction-validate] pack missing: $PACK_FILE"; exit 1; }
lines=$(grep -E '^\s*[a-zA-Z0-9_\-]+\|' "$PACK_FILE" || true)
[[ -n "$lines" ]] || { echo "[auction-validate] no auction lines found"; exit 1; }

ok=1
while IFS= read -r line; do
  line="$(echo "$line" | sed 's/^\s*//;s/\s*$//')"
  IFS='|' read -r id enabled dow hhmm item_id item_count start_bid min_increment duration currency announce <<< "$line"
  [[ -n "${id:-}" && -n "${enabled:-}" && -n "${dow:-}" && -n "${hhmm:-}" && -n "${item_id:-}" && -n "${item_count:-}" && -n "${start_bid:-}" && -n "${min_increment:-}" && -n "${duration:-}" && -n "${currency:-}" && -n "${announce:-}" ]] || { echo "[auction-validate] invalid columns: $line"; ok=0; continue; }

  [[ "$enabled" == "true" || "$enabled" == "false" ]] || { echo "[auction-validate] enabled must be true/false in: $line"; ok=0; }
  case "$dow" in MON|TUE|WED|THU|FRI|SAT|SUN|DAILY) ;; *) echo "[auction-validate] invalid dow '$dow' in: $line"; ok=0 ;; esac
  [[ "$hhmm" =~ ^[0-2][0-9][0-5][0-9]$ ]] || { echo "[auction-validate] start_hhmm_utc invalid in: $line"; ok=0; }

  for n in "$item_id" "$item_count" "$start_bid" "$min_increment" "$duration" "$currency"; do
    [[ "$n" =~ ^[0-9]+$ ]] || { echo "[auction-validate] numeric field invalid in: $line"; ok=0; }
  done
  [[ "$announce" == "true" || "$announce" == "false" ]] || { echo "[auction-validate] announce must be true/false in: $line"; ok=0; }
done <<< "$lines"

[[ "$ok" -eq 1 ]] || { echo "[auction-validate] validation failed"; exit 1; }
echo "[auction-validate] pack valid: $PACK_FILE"
