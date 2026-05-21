#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/weekly_missions_ext"
PACK_FILE="${1:-$ADDON_DIR/mods/default_pack/missions.ini}"
MISSION_ID="${2:-}"
OUTPUT_FILE="${3:-$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/WeeklyMissions.ini}"

if [[ -z "$MISSION_ID" ]]; then
  echo "Usage: $0 <pack_file> <mission_id> [output_file]"
  exit 1
fi

"$ADDON_DIR/tools/validate-mission-pack.sh" "$PACK_FILE" >/dev/null

LINE=$(grep -E "^\s*${MISSION_ID}\|" "$PACK_FILE" | head -n 1 || true)
if [[ -z "$LINE" ]]; then
  echo "[generate] mission id not found: $MISSION_ID"
  exit 1
fi

LINE="$(echo "$LINE" | sed 's/^\s*//;s/\s*$//')"
IFS='|' read -r id type target target_ids required_item_id required_item_count exp sp rewards <<< "$LINE"

mkdir -p "$(dirname "$OUTPUT_FILE")"
cat > "$OUTPUT_FILE" <<CFG
# ---------------------------------------------------------------------------
# Weekly Missions Settings (generated from addon pack)
# source: $PACK_FILE
# mission: $MISSION_ID
# ---------------------------------------------------------------------------

EnableWeeklyMissions = True
MissionType = ${type}
MissionTargetValue = ${target}
TargetMonsterIds = ${target_ids}
RequiredItemId = ${required_item_id:-0}
RequiredItemCount = ${required_item_count:-0}

# Legacy compatibility
MonsterKillTarget = ${target}

ExpReward = ${exp}
SpReward = ${sp}
RewardItems = ${rewards}
CFG

echo "[generate] Weekly mission config written: $OUTPUT_FILE"
