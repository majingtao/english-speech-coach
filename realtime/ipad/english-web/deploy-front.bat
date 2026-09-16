@echo off
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0deploy-front.ps1"
echo.
echo ============================================
echo Script finished. Exit code: %ERRORLEVEL%
echo See deploy-log-*.txt in this folder.
echo ============================================
pause
