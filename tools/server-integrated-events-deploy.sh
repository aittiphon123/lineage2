#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SERVER_DIR="${1:-$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game}"
RESTART_CMD="${2:-}"

log() { echo "[server-deploy] $1"; }

[[ -d "$SERVER_DIR" ]] || { echo "[server-deploy] server dir not found: $SERVER_DIR"; exit 1; }

log "prepare integrated config"
bash "$ROOT_DIR/tools/run-integrated-events-ready.sh"

log "target server dir: $SERVER_DIR"
src_custom="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom"
dst_custom="$SERVER_DIR/config/Custom"
mkdir -p "$dst_custom"
backup_root="$dst_custom/.integrated-backups"
ts="$(date -u +%Y%m%dT%H%M%SZ)"
snapshot_dir="$backup_root/$ts"
mkdir -p "$snapshot_dir"

backup_if_exists() {
  local src="$1"
  local name="$2"
  if [[ -f "$src" ]]; then
    cp -f "$src" "$snapshot_dir/$name"
  else
    : > "$snapshot_dir/$name"
  fi
}

backup_if_exists "$dst_custom/WeeklyMissions.ini" "WeeklyMissions.ini"
backup_if_exists "$dst_custom/AuctionEvent.ini" "AuctionEvent.ini"
backup_if_exists "$dst_custom/IntegratedEvents.ini" "IntegratedEvents.ini"
log "backup snapshot created: $snapshot_dir"

copy_if_needed() {
  local src="$1"
  local dst="$2"
  local src_real dst_real
  src_real=$(readlink -f "$src")
  dst_real=$(readlink -f "$dst" 2>/dev/null || true)
  if [[ -n "$dst_real" && "$src_real" == "$dst_real" ]]; then
    log "skip copy (same file): $(basename "$src")"
    return
  fi
  cp -f "$src" "$dst"
}

copy_if_needed "$src_custom/WeeklyMissions.ini" "$dst_custom/WeeklyMissions.ini"
copy_if_needed "$src_custom/AuctionEvent.ini" "$dst_custom/AuctionEvent.ini"
copy_if_needed "$src_custom/IntegratedEvents.ini" "$dst_custom/IntegratedEvents.ini"

log "config files copied"

if [[ -n "$RESTART_CMD" ]]; then
  log "running restart command"
  bash -lc "$RESTART_CMD"
  log "restart command finished"
else
  log "restart command not provided; restart gameserver manually to apply config"
fi

log "done"
