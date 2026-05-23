#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
MANIFEST="$ROOT_DIR/addons/manifest/addons.manifest.ini"

"$ROOT_DIR/addons/shared/tools/preflight.sh"
"$ROOT_DIR/addons/shared/tools/validate-manifest.sh" "$MANIFEST"

if [[ ! -f "$MANIFEST" ]]; then
  echo "[addons] manifest not found: $MANIFEST"
  exit 1
fi

enabled_line=$(grep -E '^EnabledAddons\s*=' "$MANIFEST" | head -n1 || true)
if [[ -z "$enabled_line" ]]; then
  echo "[addons] EnabledAddons key missing in manifest"
  exit 1
fi

enabled_csv="${enabled_line#*=}"
enabled_csv="$(echo "$enabled_csv" | tr -d '[:space:]')"
IFS=',' read -ra addons <<< "$enabled_csv"

for addon in "${addons[@]}"; do
  [[ -z "$addon" ]] && continue
  script="$ROOT_DIR/addons/$addon/tools/deploy-addon.sh"
  if [[ -x "$script" ]]; then
    echo "[addons] deploying: $addon"
    "$script"
  else
    echo "[addons] skipped (no deploy script): $addon"
  fi
done

echo "[addons] deployment complete"
