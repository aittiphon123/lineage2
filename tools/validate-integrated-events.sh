#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
CFG="$ROOT_DIR/L2J_Mobius_CT_2.6_HighFive/dist/game/config/Custom/IntegratedEvents.ini"

[[ -f "$CFG" ]] || { echo "[integrated-validate] missing config: $CFG"; exit 1; }

weekly_line=$(grep -E '^WeeklyMissionLine\s*=' "$CFG" | head -n1 || true)
auction_line=$(grep -E '^AuctionLine\s*=' "$CFG" | head -n1 || true)

[[ -n "$weekly_line" ]] || { echo "[integrated-validate] WeeklyMissionLine missing"; exit 1; }
[[ -n "$auction_line" ]] || { echo "[integrated-validate] AuctionLine missing"; exit 1; }

weekly="${weekly_line#*=}"; weekly="$(echo "$weekly" | sed 's/^\s*//;s/\s*$//')"
auction="${auction_line#*=}"; auction="$(echo "$auction" | sed 's/^\s*//;s/\s*$//')"

IFS='|' read -r mid mtype mtarget mtargetids mreqid mreqcount mexp msp mrewards <<< "$weekly"
IFS='|' read -r aid aenabled adow ahhmm aitemid aitemcount astart amininc adur acurrency aann <<< "$auction"

ok=1
[[ -n "$mid" && -n "$mtype" && -n "$mtarget" ]] || { echo "[integrated-validate] invalid WeeklyMissionLine"; ok=0; }
[[ "$mtype" =~ ^(MONSTER_KILL|PVP_KILL|ITEM_COLLECT|ONLINE_TIME)$ ]] || { echo "[integrated-validate] invalid mission type: $mtype"; ok=0; }
[[ "$mtarget" =~ ^[0-9]+$ ]] || { echo "[integrated-validate] mission target must be integer"; ok=0; }
[[ "$mexp" =~ ^[0-9]+$ && "$msp" =~ ^[0-9]+$ ]] || { echo "[integrated-validate] mission exp/sp must be integers"; ok=0; }

[[ -n "$aid" ]] || { echo "[integrated-validate] invalid AuctionLine id"; ok=0; }
[[ "$aenabled" == "true" || "$aenabled" == "false" ]] || { echo "[integrated-validate] auction enabled must be true/false"; ok=0; }
[[ "$adow" =~ ^(MON|TUE|WED|THU|FRI|SAT|SUN|DAILY)$ ]] || { echo "[integrated-validate] invalid auction day: $adow"; ok=0; }
[[ "$ahhmm" =~ ^[0-2][0-9][0-5][0-9]$ ]] || { echo "[integrated-validate] invalid auction hhmm: $ahhmm"; ok=0; }
for n in "$aitemid" "$aitemcount" "$astart" "$amininc" "$adur" "$acurrency"; do
  [[ "$n" =~ ^[0-9]+$ ]] || { echo "[integrated-validate] auction numeric field invalid"; ok=0; }
done
[[ "$aann" == "true" || "$aann" == "false" ]] || { echo "[integrated-validate] auction announce must be true/false"; ok=0; }

[[ "$ok" -eq 1 ]] || { echo "[integrated-validate] validation failed"; exit 1; }
echo "[integrated-validate] ok"
