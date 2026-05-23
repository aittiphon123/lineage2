#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
CUSTOM_DIR="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom"

log() {
  echo "[integrated-prepare] $1"
}

log "start"

bash "$ROOT_DIR/tools/validate-integrated-events.sh"
bash "$ROOT_DIR/tools/apply-integrated-events.sh"

weekly_cfg="$CUSTOM_DIR/WeeklyMissions.ini"
auction_cfg="$CUSTOM_DIR/AuctionEvent.ini"

[[ -f "$weekly_cfg" ]] || { echo "[integrated-prepare] missing $weekly_cfg"; exit 1; }
[[ -f "$auction_cfg" ]] || { echo "[integrated-prepare] missing $auction_cfg"; exit 1; }

grep -E '^MissionId\s*=\s*' "$weekly_cfg" >/dev/null
grep -E '^AuctionId\s*=\s*' "$auction_cfg" >/dev/null

log "runtime config ready"
log "next: restart gameserver to apply updated configs"
