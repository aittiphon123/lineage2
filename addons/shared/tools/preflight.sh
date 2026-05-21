#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
MANIFEST="$ROOT_DIR/addons/manifest/addons.manifest.ini"
TARGET_DIST="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game"

[[ -f "$MANIFEST" ]] || { echo "[preflight] manifest missing: $MANIFEST"; exit 1; }
[[ -d "$TARGET_DIST" ]] || { echo "[preflight] dist dir missing: $TARGET_DIST"; exit 1; }
[[ -w "$TARGET_DIST" ]] || { echo "[preflight] dist dir not writable: $TARGET_DIST"; exit 1; }

echo "[preflight] ok"
