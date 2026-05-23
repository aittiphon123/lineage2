@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
set "BASH_CMD=bash"

where %BASH_CMD% >nul 2>nul
if errorlevel 1 (
  echo [server-rollback] bash not found in PATH. Install Git Bash or WSL.
  exit /b 1
)

set "SERVER_DIR=%~1"
set "RESTART_CMD=%~2"

if "%SERVER_DIR%"=="" (
  %BASH_CMD% "%SCRIPT_DIR%server-integrated-events-rollback.sh"
) else (
  if "%RESTART_CMD%"=="" (
    %BASH_CMD% "%SCRIPT_DIR%server-integrated-events-rollback.sh" "%SERVER_DIR%"
  ) else (
    %BASH_CMD% "%SCRIPT_DIR%server-integrated-events-rollback.sh" "%SERVER_DIR%" "%RESTART_CMD%"
  )
)

set EXIT_CODE=%ERRORLEVEL%
if not "%EXIT_CODE%"=="0" (
  echo [server-rollback] failed with code %EXIT_CODE%
  exit /b %EXIT_CODE%
)

echo [server-rollback] completed.
exit /b 0
