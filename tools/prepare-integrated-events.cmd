@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
set "BASH_CMD=bash"

where %BASH_CMD% >nul 2>nul
if errorlevel 1 (
  echo [integrated-prepare] bash not found in PATH. Install Git Bash or WSL.
  exit /b 1
)

%BASH_CMD% "%SCRIPT_DIR%prepare-integrated-events.sh"
set EXIT_CODE=%ERRORLEVEL%
if not "%EXIT_CODE%"=="0" (
  echo [integrated-prepare] failed with code %EXIT_CODE%
  exit /b %EXIT_CODE%
)

echo [integrated-prepare] completed.
exit /b 0
