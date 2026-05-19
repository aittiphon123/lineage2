# Weekly Missions MOD Guide

This guide defines a simple external "mission pack" format so staff can add events without touching core.

## Mission line format
`id|type|target|target_ids|required_item_id|required_item_count|exp|sp|rewards`

- `id`: unique key
- `type`: `MONSTER_KILL | PVP_KILL | ITEM_COLLECT | ONLINE_TIME`
- `target`: required progress amount
- `target_ids`: comma-separated NPC IDs for monster-specific missions (optional)
- `required_item_id`, `required_item_count`: for item submit/collect missions
- `exp`, `sp`: reward values
- `rewards`: `itemId,count;itemId,count`

## Example workflow
1. Clone default pack from `mods/default_pack/missions.ini`
2. Create your own pack (e.g. `mods/weekend_event/missions.ini`)
3. Deploy config using `tools/deploy-addon.sh`
4. Reload scripts or restart game process.

## Recommendation
- Start with 3-5 missions/week.
- Keep one easy, two medium, one hard objective.
- Mix mission types to reduce repetitive farming.
