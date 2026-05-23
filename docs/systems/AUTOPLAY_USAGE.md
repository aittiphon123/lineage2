# AutoPlay System Usage Guide

## Overview
AutoPlay provides assisted combat automation with optional **PvE-only safety mode**.

## Key Config (`config/Custom/AutoPlay.ini`)
- `EnableAutoPlay`
- `AutoPlayPveOnly`
- `AutoPlayBlockStartInPvpSiege`
- `AutoPlayStopInPvpSiege`
- `AutoPlayNotifyOnZoneRestriction`

## Player Commands
- `.play` : open main AutoPlay UI
- `.play start` / `.play stop`
- `.play mode1` : monster-only target mode
- `.play profile melee|mage|support|auto`

## GM Monitoring
- `.play stats`
- `.play stats reset`

## Recommended Safe Setup
1. Enable `AutoPlayPveOnly = True`
2. Enable start/stop protection in PvP/Siege
3. Enable notifications for blocked actions
