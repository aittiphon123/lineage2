@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
set "BASH_CMD=bash"

where %BASH_CMD% >nul 2>nul
if errorlevel 1 (
  echo [integrated-ready] bash not found in PATH. Install Git Bash or WSL.
  exit /b 1
)

%BASH_CMD% "%SCRIPT_DIR%run-integrated-events-ready.sh"
set EXIT_CODE=%ERRORLEVEL%
if not "%EXIT_CODE%"=="0" (
  echo [integrated-ready] failed with code %EXIT_CODE%
  exit /b %EXIT_CODE%
)

echo [integrated-ready] completed.
exit /b 0
