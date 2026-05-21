#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
MANIFEST="$ROOT_DIR/addons/manifest/addons.manifest.ini"

"$ROOT_DIR/addons/shared/tools/preflight.sh" >/dev/null
"$ROOT_DIR/addons/shared/tools/validate-manifest.sh" "$MANIFEST" >/dev/null

enabled_line=$(grep -E '^EnabledAddons\s*=' "$MANIFEST" | head -n1)
enabled_csv="${enabled_line#*=}"
enabled_csv="$(echo "$enabled_csv" | tr -d '[:space:]')"
IFS=',' read -ra addons <<< "$enabled_csv"

echo "[health] addons manifest: ok"
for addon in "${addons[@]}"; do
  [[ -z "$addon" ]] && continue
  base="$ROOT_DIR/addons/$addon"
  status="ok"
  [[ -d "$base/config" ]] || status="missing-config"
  [[ -d "$base/tools" ]] || status="missing-tools"
  [[ -x "$base/tools/deploy-addon.sh" ]] || status="missing-deploy-script"
  echo "[health] $addon: $status"
done
