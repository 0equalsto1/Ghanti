@echo off
echo ========================================================
echo   Installing Android Studio via Windows Package Manager
echo ========================================================
echo.
winget install --id Google.AndroidStudio -e --accept-package-agreements --accept-source-agreements
echo.
echo ========================================================
echo Installation completed! Open Android Studio, then choose:
echo "Open Project" -> Select the D:\AI\New folder\OmniClock folder.
echo ========================================================
pause
