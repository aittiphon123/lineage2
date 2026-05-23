#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
MANIFEST="${1:-$ROOT_DIR/addons/manifest/addons.manifest.ini}"

[[ -f "$MANIFEST" ]] || { echo "[manifest] file missing: $MANIFEST"; exit 1; }
line=$(grep -E '^EnabledAddons\s*=' "$MANIFEST" | head -n1 || true)
[[ -n "$line" ]] || { echo "[manifest] EnabledAddons key missing"; exit 1; }
csv="${line#*=}"
csv="$(echo "$csv" | tr -d '[:space:]')"
IFS=',' read -ra arr <<< "$csv"

declare -A seen
ok=1
for addon in "${arr[@]}"; do
  [[ -z "$addon" ]] && continue
  if [[ -n "${seen[$addon]:-}" ]]; then
    echo "[manifest] duplicate addon: $addon"; ok=0
  fi
  seen[$addon]=1
  [[ -d "$ROOT_DIR/addons/$addon" ]] || { echo "[manifest] missing addon directory: $addon"; ok=0; }
done

[[ "$ok" -eq 1 ]] || { echo "[manifest] validation failed"; exit 1; }
echo "[manifest] ok"
