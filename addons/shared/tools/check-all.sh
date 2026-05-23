#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
MANIFEST="${1:-$ROOT_DIR/addons/manifest/addons.manifest.ini}"
WEEKLY_PACK="${2:-$ROOT_DIR/addons/weekly_missions_ext/mods/default_pack/missions.ini}"
AUCTION_PACK="${3:-$ROOT_DIR/addons/auction_event_ext/mods/default_pack/auctions.ini}"
OUTPUT_MODE="${CHECK_ALL_OUTPUT:-text}"

declare -a PASSED_CHECKS=()

run_check() {
  local label="$1"
  shift
  if [[ "$OUTPUT_MODE" != "json" ]]; then
    echo "[check-all] running: $label"
    "$@"
    PASSED_CHECKS+=("$label")
    return
  fi

  local tmp
  tmp=$(mktemp)
  if "$@" >"$tmp" 2>&1; then
    PASSED_CHECKS+=("$label")
    rm -f "$tmp"
    return
  fi

  local output
  output=$(tr '\n' ' ' <"$tmp" | sed 's/"/\\"/g')
  rm -f "$tmp"
  printf '{"status":"failed","failed_check":"%s","output":"%s"}\n' "$label" "$output"
  exit 1

}

print_json_summary() {
  local checks_json=""
  local i
  for i in "${!PASSED_CHECKS[@]}"; do
    [[ "$i" -gt 0 ]] && checks_json+=","
    checks_json+="\"${PASSED_CHECKS[$i]}\""
  done
  printf '{"status":"ok","checks":[%s]}\n' "$checks_json"
}

run_check "preflight" "$ROOT_DIR/addons/shared/tools/preflight.sh"
run_check "manifest" "$ROOT_DIR/addons/shared/tools/validate-manifest.sh" "$MANIFEST"
run_check "health" "$ROOT_DIR/addons/shared/tools/addon-health.sh"
run_check "weekly-pack" "$ROOT_DIR/addons/weekly_missions_ext/tools/validate-mission-pack.sh" "$WEEKLY_PACK"
run_check "auction-pack" "$ROOT_DIR/addons/auction_event_ext/tools/validate-auction-pack.sh" "$AUCTION_PACK"
run_check "policy(strict)" env POLICY_STRICT=1 "$ROOT_DIR/addons/shared/tools/validate-policy.sh" "$WEEKLY_PACK" "$AUCTION_PACK"

if [[ "$OUTPUT_MODE" == "json" ]]; then
  print_json_summary
else
  echo "[check-all] all checks passed"
fi
