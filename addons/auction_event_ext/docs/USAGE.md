# Auction Event Addon Usage

## What you can configure per auction
- Open/Close: `enabled` (`true|false`)
- Day: `dow` (`MON..SUN` or `DAILY`)
- Time (UTC): `start_hhmm_utc` (example `2000`)
- Auction item: `item_id`, `item_count`
- Bid settings: `start_bid`, `min_increment`
- Duration: `duration_minutes`
- Bid currency item: `currency_item_id`
- Announce: `announce` (`true|false`)

## Flow
1. Edit pack line in `mods/default_pack/auctions.ini`
2. Validate pack
3. Generate active `AuctionEvent.ini`
4. Deploy addon

Commands:
- `addons/auction_event_ext/tools/validate-auction-pack.sh addons/auction_event_ext/mods/default_pack/auctions.ini`
- `addons/auction_event_ext/tools/generate-auction-config.sh addons/auction_event_ext/mods/default_pack/auctions.ini starter_auction`
- `addons/auction_event_ext/tools/deploy-addon.sh`


## Rotation and rollback
- `addons/auction_event_ext/tools/switch-auction.sh addons/auction_event_ext/mods/default_pack/auctions.ini starter_auction`
- `addons/auction_event_ext/tools/rotate-auction.sh addons/auction_event_ext/mods/default_pack/auctions.ini`
- `addons/auction_event_ext/tools/schedule-auction-rotation.sh addons/auction_event_ext/mods/default_pack/auctions.ini`
- `addons/auction_event_ext/tools/rollback-auction.sh`
