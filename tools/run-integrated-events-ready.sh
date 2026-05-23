#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
CUSTOM_DIR="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom"

log() { echo "[integrated-ready] $1"; }

log "starting full readiness pipeline"

bash "$ROOT_DIR/tools/validate-integrated-events.sh"
bash "$ROOT_DIR/tools/prepare-integrated-events.sh"

weekly_cfg="$CUSTOM_DIR/WeeklyMissions.ini"
auction_cfg="$CUSTOM_DIR/AuctionEvent.ini"

mission_id=$(grep -E '^MissionId\s*=\s*' "$weekly_cfg" | head -n1 | cut -d'=' -f2- | xargs)
auction_id=$(grep -E '^AuctionId\s*=\s*' "$auction_cfg" | head -n1 | cut -d'=' -f2- | xargs)

[[ -n "$mission_id" ]] || { echo "[integrated-ready] MissionId empty"; exit 1; }
[[ -n "$auction_id" ]] || { echo "[integrated-ready] AuctionId empty"; exit 1; }

log "ready"
log "MissionId=$mission_id"
log "AuctionId=$auction_id"
log "action required: restart gameserver process to load updated config files"
