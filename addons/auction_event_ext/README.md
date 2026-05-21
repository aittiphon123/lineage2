# Auction Event External Addon

Custom event addon scaffold for timed auction events without rebuilding core.

## Features (MVP scaffold)
- External event pack format with schedule + enable/disable per auction
- Pack validation tool
- Config generation tool
- Deploy helper

## Quick start
1. Edit pack: `addons/auction_event_ext/mods/default_pack/auctions.ini`
2. Validate pack:
   - `addons/auction_event_ext/tools/validate-auction-pack.sh addons/auction_event_ext/mods/default_pack/auctions.ini`
3. Generate active config:
   - `addons/auction_event_ext/tools/generate-auction-config.sh addons/auction_event_ext/mods/default_pack/auctions.ini starter_auction`
4. Deploy:
   - `addons/auction_event_ext/tools/deploy-addon.sh`


## Ops tools
- Switch active auction with backup:
  - `addons/auction_event_ext/tools/switch-auction.sh addons/auction_event_ext/mods/default_pack/auctions.ini starter_auction`
- Rollback active auction config:
  - `addons/auction_event_ext/tools/rollback-auction.sh`
- Rotate to next auction:
  - `addons/auction_event_ext/tools/rotate-auction.sh addons/auction_event_ext/mods/default_pack/auctions.ini`
- Schedule-safe rotation wrapper:
  - `addons/auction_event_ext/tools/schedule-auction-rotation.sh addons/auction_event_ext/mods/default_pack/auctions.ini`
