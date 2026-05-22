@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
set "POWERSHELL=%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe"

if not exist "%POWERSHELL%" (
  echo [oneclick] PowerShell not found.
  exit /b 1
)

"%POWERSHELL%" -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%install-addons-oneclick.ps1" %*
set EXIT_CODE=%ERRORLEVEL%
if not "%EXIT_CODE%"=="0" (
  echo [oneclick] failed with code %EXIT_CODE%
  exit /b %EXIT_CODE%
)

echo [oneclick] completed.
exit /b 0
