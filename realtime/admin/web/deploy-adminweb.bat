@echo off
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0deploy-adminweb.ps1"
echo.
echo ============================================
echo Script finished. Exit code: %ERRORLEVEL%
echo ============================================
pause
