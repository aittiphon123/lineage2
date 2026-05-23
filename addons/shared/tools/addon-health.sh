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
  issues=()
  [[ -d "$base/config" ]] || issues+=("missing-config")
  [[ -d "$base/tools" ]] || issues+=("missing-tools")
  [[ -x "$base/tools/deploy-addon.sh" ]] || issues+=("missing-deploy-script")

  if [[ ${#issues[@]} -eq 0 ]]; then
    echo "[health] $addon: ok"
  else
    issue_csv=$(IFS=','; echo "${issues[*]}")
    echo "[health] $addon: $issue_csv"
  fi
done
