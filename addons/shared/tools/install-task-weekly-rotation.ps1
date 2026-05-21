param(
  [string]$TaskName = "L2J_WeeklyMissionRotation",
  [string]$RepoRoot = "C:\lineage2"
)

$script = Join-Path $RepoRoot "addons\weekly_missions_ext\tools\schedule-weekly-rotation.sh"
$pack   = Join-Path $RepoRoot "addons\weekly_missions_ext\mods\default_pack\missions.ini"

$action = New-ScheduledTaskAction -Execute "bash.exe" -Argument "-lc `"$script $pack`""
$trigger = New-ScheduledTaskTrigger -Weekly -DaysOfWeek Monday -At 00:00

Register-ScheduledTask -TaskName $TaskName -Action $action -Trigger $trigger -Force | Out-Null
Write-Host "[task-install] installed task $TaskName"
