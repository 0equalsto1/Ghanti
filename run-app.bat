@echo off
echo ========================================================
echo   OmniClock - World Clock & Event Alarms Server
echo ========================================================
echo.
echo Starting local app server at: http://localhost:3000
echo Open this URL on your PC or phone connected to the same Wi-Fi!
echo.
start http://localhost:3000
python -m http.server 3000 -d web
pause
