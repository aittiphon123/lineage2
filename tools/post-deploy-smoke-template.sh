#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="${1:-.state}"
mkdir -p "$OUT_DIR"
OUT_FILE="$OUT_DIR/post-deploy-smoke-$(date -u +%Y%m%dT%H%M%SZ).md"

cat > "$OUT_FILE" <<EOT
# Post-deploy smoke result

- [ ] Weekly mission command opens and mission id matches expected
- [ ] Weekly claim flow succeeds for eligible character
- [ ] Auction schedule/announcement matches configured AuctionId
- [ ] AutoPlay PvE starts in PvE zone
- [ ] AutoPlay blocked in PvP/Siege when configured
- [ ] AutoPlay stop-on-zone works
- [ ] .play stats telemetry increments on blocked attempts

Operator:
Server:
Build/Commit:
Notes:
EOT

echo "[smoke-template] created $OUT_FILE"
