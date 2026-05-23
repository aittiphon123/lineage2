#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

# Block reintroducing addon deploy scripts into CT2.6 production docs/workflows.
if rg -n "addons/.*/tools/deploy-addon.sh|deploy-all-addons.sh" "$ROOT_DIR/docs/systems/INTEGRATED_EVENTS_USAGE.md" "$ROOT_DIR/.github/workflows" >/dev/null; then
  echo "[guard-integrated-only] legacy addon deploy commands found in integrated production surfaces"
  exit 1
fi

echo "[guard-integrated-only] ok"
