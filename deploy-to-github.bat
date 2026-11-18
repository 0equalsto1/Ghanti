@echo off
echo ==============================================
echo   OmniClock - GitHub Pages Deployment Helper
echo ==============================================
echo.
set /p REPO_URL="Enter your GitHub Repository URL (e.g. https://github.com/username/omniclock.git): "
if "%REPO_URL%"=="" (
    echo No URL provided. Aborting.
    pause
    exit /b 1
)
git remote remove origin 2>nul
git remote add origin %REPO_URL%
git branch -M main
echo.
echo Pushing to GitHub...
git push -u origin main
echo.
echo ==============================================
echo Done! Next steps:
echo 1. Open your repository on GitHub -^> Settings -^> Pages
echo 2. Set 'Source' to 'GitHub Actions'
echo Your site will be live shortly!
echo ==============================================
pause
