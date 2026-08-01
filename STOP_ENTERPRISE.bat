@echo off
echo =========================================
echo Enterprise Deployment Stop Script
echo Student Result Management System
echo =========================================
echo.
echo Stopping Docker Compose Enterprise Stack...
echo.
cd "c:\Users\sivas\Desktop\distubted server equally"
docker-compose -f docker-compose-enterprise.yml down
echo.
echo =========================================
echo Enterprise Stack Stopped Successfully!
echo =========================================
echo.
echo Note: Spring Boot instances are not stopped by this script.
echo Stop them manually in their respective terminal windows.
echo.
pause
