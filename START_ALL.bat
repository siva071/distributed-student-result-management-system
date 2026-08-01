@echo off
echo ========================================
echo Starting Complete Distributed System
echo ========================================
echo.
echo Step 1: Starting Redis Container...
docker start redis-cache
if %errorlevel% neq 0 (
    echo Redis already running or error occurred
)
echo Redis started successfully!
echo.
echo Step 2: Starting NGINX Load Balancer...
cd "c:\Users\sivas\Desktop\distubted server equally"
docker-compose -f nginx-only-compose.yml up -d
echo NGINX started successfully!
echo.
echo ========================================
echo IMPORTANT: Manual Steps Required
echo ========================================
echo.
echo You need to manually start the three Spring Boot instances
echo in THREE SEPARATE terminal windows:
echo.
echo Terminal 1 - Instance 1 (Port 8081):
echo   cd "c:\Users\sivas\Desktop\distubted server equally"
echo   .\start-instance-1.bat
echo.
echo Terminal 2 - Instance 2 (Port 8082):
echo   cd "c:\Users\sivas\Desktop\distubted server equally"
echo   .\start-instance-2.bat
echo.
echo Terminal 3 - Instance 3 (Port 8083):
echo   cd "c:\Users\sivas\Desktop\distubted server equally"
echo   .\start-instance-3.bat
echo.
echo ========================================
echo After starting all instances, access:
echo   Main: http://localhost
echo   API: http://localhost/api/students
echo   Swagger: http://localhost/swagger-ui/index.html
echo ========================================
echo.
pause
