#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
CFG="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/IntegratedEvents.ini"
CUSTOM_DIR="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom"
WEEKLY_OUT="$CUSTOM_DIR/WeeklyMissions.ini"
AUCTION_OUT="$CUSTOM_DIR/AuctionEvent.ini"

[[ -f "$CFG" ]] || { echo "[integrated] missing config: $CFG"; exit 1; }

weekly_line=$(grep -E '^WeeklyMissionLine\s*=' "$CFG" | head -n1 || true)
auction_line=$(grep -E '^AuctionLine\s*=' "$CFG" | head -n1 || true)

[[ -n "$weekly_line" ]] || { echo "[integrated] WeeklyMissionLine missing"; exit 1; }
[[ -n "$auction_line" ]] || { echo "[integrated] AuctionLine missing"; exit 1; }

weekly="${weekly_line#*=}"
auction="${auction_line#*=}"
weekly="$(echo "$weekly" | sed 's/^\s*//;s/\s*$//')"
auction="$(echo "$auction" | sed 's/^\s*//;s/\s*$//')"

IFS='|' read -r mid mtype mtarget mtargetids mreqid mreqcount mexp msp mrewards <<< "$weekly"
IFS='|' read -r aid aenabled adow ahhmm aitemid aitemcount astart amininc adur acurrency aann <<< "$auction"

mkdir -p "$CUSTOM_DIR"
cat > "$WEEKLY_OUT" <<EOW
# Generated from IntegratedEvents.ini
EnableWeeklyMissions = true
MissionId = $mid
MissionType = $mtype
MissionTargetCount = $mtarget
MissionTargetIds = $mtargetids
RequiredItemId = $mreqid
RequiredItemCount = $mreqcount
MissionRewardExp = $mexp
MissionRewardSp = $msp
MissionRewards = $mrewards
EOW

cat > "$AUCTION_OUT" <<EOA
# Generated from IntegratedEvents.ini
EnableAuctionEvent = $aenabled
AuctionId = $aid
AuctionDayOfWeek = $adow
AuctionStartTimeUtc = $ahhmm
AuctionItemId = $aitemid
AuctionItemCount = $aitemcount
AuctionStartBid = $astart
AuctionMinIncrement = $amininc
AuctionDurationMinutes = $adur
AuctionCurrencyItemId = $acurrency
AuctionAnnounce = $aann
EOA

echo "[integrated] generated: $WEEKLY_OUT"
echo "[integrated] generated: $AUCTION_OUT"
