#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SERVER_DIR="${1:-$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game}"
BACKUP_DIR="$SERVER_DIR/config/Custom/.integrated-backups"
RESTART_CMD="${2:-}"

log() { echo "[server-rollback] $1"; }

[[ -d "$SERVER_DIR" ]] || { echo "[server-rollback] server dir not found: $SERVER_DIR"; exit 1; }
[[ -d "$BACKUP_DIR" ]] || { echo "[server-rollback] backup dir missing: $BACKUP_DIR"; exit 1; }

latest=$(ls -1dt "$BACKUP_DIR"/* 2>/dev/null | head -n1 || true)
[[ -n "$latest" ]] || { echo "[server-rollback] no backup snapshot found"; exit 1; }

log "restoring snapshot: $(basename "$latest")"
cp -f "$latest/WeeklyMissions.ini" "$SERVER_DIR/config/Custom/WeeklyMissions.ini"
cp -f "$latest/AuctionEvent.ini" "$SERVER_DIR/config/Custom/AuctionEvent.ini"
cp -f "$latest/IntegratedEvents.ini" "$SERVER_DIR/config/Custom/IntegratedEvents.ini"

if [[ -n "$RESTART_CMD" ]]; then
  log "running restart command"
  bash -lc "$RESTART_CMD"
  log "restart command finished"
else
  log "restart command not provided; restart gameserver manually"
fi

log "rollback done"
