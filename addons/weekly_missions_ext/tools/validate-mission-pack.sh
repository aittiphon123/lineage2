#!/usr/bin/env bash
set -euo pipefail

PACK_FILE="${1:-addons/weekly_missions_ext/mods/default_pack/missions.ini}"

if [[ ! -f "$PACK_FILE" ]]; then
  echo "[validate] mission pack not found: $PACK_FILE"
  exit 1
fi

MISSION_LINES=$(grep -E '^\s*[a-zA-Z0-9_\-]+\|' "$PACK_FILE" | sed '/^\s*$/d')
if [[ -z "$MISSION_LINES" ]]; then
  echo "[validate] no mission lines found in: $PACK_FILE"
  exit 1
fi

ok=1
while IFS= read -r line; do
  line="$(echo "$line" | sed 's/^\s*//;s/\s*$//')"
  IFS='|' read -r id type target target_ids required_item_id required_item_count exp sp rewards <<< "$line"

  if [[ -z "${id:-}" || -z "${type:-}" || -z "${target:-}" || -z "${exp:-}" || -z "${sp:-}" ]]; then
    echo "[validate] invalid mission line (missing columns): $line"
    ok=0
    continue
  fi

  case "$type" in
    MONSTER_KILL|PVP_KILL|ITEM_COLLECT|ONLINE_TIME) ;;
    *) echo "[validate] invalid mission type '$type' in line: $line"; ok=0 ;;
  esac

  if ! [[ "$target" =~ ^[0-9]+$ ]]; then
    echo "[validate] target must be integer in line: $line"
    ok=0
  fi

  if ! [[ "${required_item_id:-0}" =~ ^[0-9]+$ ]] || ! [[ "${required_item_count:-0}" =~ ^[0-9]+$ ]]; then
    echo "[validate] required_item fields must be integers in line: $line"
    ok=0
  fi

  if ! [[ "$exp" =~ ^[0-9]+$ ]] || ! [[ "$sp" =~ ^[0-9]+$ ]]; then
    echo "[validate] exp/sp must be integers in line: $line"
    ok=0
  fi

  if [[ -n "${rewards:-}" ]]; then
    IFS=';' read -ra reward_parts <<< "$rewards"
    for reward in "${reward_parts[@]}"; do
      reward="$(echo "$reward" | sed 's/^\s*//;s/\s*$//')"
      [[ -z "$reward" ]] && continue
      if ! [[ "$reward" =~ ^[0-9]+,[0-9]+$ ]]; then
        echo "[validate] invalid reward token '$reward' in line: $line"
        ok=0
      fi
    done
  fi

done <<< "$MISSION_LINES"

if [[ "$ok" -ne 1 ]]; then
  echo "[validate] mission pack validation failed"
  exit 1
fi

echo "[validate] mission pack valid: $PACK_FILE"
