#!/usr/bin/env bash
set -euo pipefail

profile="${1:-}"

case "$profile" in
  linux-systemd)
    echo "systemctl restart l2j-game"
    ;;
  windows-service)
    echo "net stop L2JGame && net start L2JGame"
    ;;
  script-local)
    echo "/opt/l2/scripts/restart-game.sh"
    ;;
  *)
    echo "usage: $0 <linux-systemd|windows-service|script-local>" >&2
    exit 1
    ;;
esac
