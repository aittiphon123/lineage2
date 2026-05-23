#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
ADDON_DIR="$ROOT_DIR/addons/event_template"
TARGET_DIST="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game"

mkdir -p "$TARGET_DIST/config/Custom"
cp -f "$ADDON_DIR/config/EventTemplate.ini" "$TARGET_DIST/config/Custom/EventTemplate.ini"

echo "[addon:event_template] deployed config"
