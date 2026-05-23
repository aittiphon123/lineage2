param(
    [string]$RepoRoot = "",
    [switch]$SkipValidation,
    [switch]$Overlay,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"

function Resolve-RepoRoot {
    param([string]$InputRoot)
    if ($InputRoot -and $InputRoot.Trim().Length -gt 0) {
        return (Resolve-Path $InputRoot).Path
    }

    $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
    return (Resolve-Path (Join-Path $scriptDir "..\..\..")).Path
}

$root = Resolve-RepoRoot -InputRoot $RepoRoot
$bashExe = Get-Command bash -ErrorAction SilentlyContinue
if (-not $bashExe) {
    throw "bash not found in PATH. Install Git Bash (or WSL) and retry."
}

Write-Host "[oneclick] repo root: $root"

if ($Overlay) {
    Write-Host "[oneclick] overlay mode enabled: deploy will overwrite target config files in-place."
}

if ($DryRun) {
    Write-Host "[oneclick] dry-run mode enabled: no deployment changes will be applied."
}

if (-not $SkipValidation) {
    Write-Host "[oneclick] running validation pipeline..."
    & $bashExe.Source -lc "cd '$root' && bash addons/shared/tools/check-all.sh"
    if ($LASTEXITCODE -ne 0) {
        throw "Validation failed. Deployment aborted."
    }
}

if ($DryRun) {
    Write-Host "[oneclick] would run: bash addons/shared/tools/deploy-all-addons.sh"
    Write-Host "[oneclick] dry-run finished successfully."
    exit 0
}

Write-Host "[oneclick] deploying enabled addons from manifest..."
& $bashExe.Source -lc "cd '$root' && bash addons/shared/tools/deploy-all-addons.sh"
if ($LASTEXITCODE -ne 0) {
    throw "Deployment failed."
}

Write-Host "[oneclick] done. Enabled addons were deployed successfully."
