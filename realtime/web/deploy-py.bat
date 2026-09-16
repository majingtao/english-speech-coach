@echo off
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0deploy-py.ps1"
echo.
echo ============================================
echo Script finished. Exit code: %ERRORLEVEL%
echo ============================================
pause
