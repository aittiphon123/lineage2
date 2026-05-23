#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../../.." && pwd)"
BAD_WEEKLY="$ROOT_DIR/addons/shared/test-fixtures/missions.invalid.ini"
BAD_AUCTION="$ROOT_DIR/addons/shared/test-fixtures/auctions.invalid.ini"

expect_fail() {
  local label="$1"
  shift
  if "$@" >/tmp/${label}.log 2>&1; then
    echo "[invalid-fixtures] expected failure but passed: $label"
    cat /tmp/${label}.log
    return 1
  fi
  echo "[invalid-fixtures] expected failure observed: $label"
}

expect_fail weekly-pack "$ROOT_DIR/addons/weekly_missions_ext/tools/validate-mission-pack.sh" "$BAD_WEEKLY"
expect_fail auction-pack "$ROOT_DIR/addons/auction_event_ext/tools/validate-auction-pack.sh" "$BAD_AUCTION"
expect_fail policy-strict env POLICY_STRICT=1 "$ROOT_DIR/addons/shared/tools/validate-policy.sh" "$BAD_WEEKLY" "$BAD_AUCTION"

echo "[invalid-fixtures] all negative checks passed"
