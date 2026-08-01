@echo off
echo ========================================
echo Starting All Spring Boot Instances
echo ========================================
echo.

cd "c:\Users\sivas\Desktop\distubted server equally"

echo [1/3] Killing existing processes on ports 8081, 8082, 8083...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8082') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8083') do taskkill /PID %%a /F >nul 2>&1
echo Existing processes killed.
echo.

echo [2/3] Starting Instance 1 on port 8081...
start "Instance-8081" cmd /k "cd /d c:\Users\sivas\Desktop\distubted server equally && start-instance-1.bat"
timeout /t 5 /nobreak >nul

echo [3/3] Starting Instance 2 on port 8082...
start "Instance-8082" cmd /k "cd /d c:\Users\sivas\Desktop\distubted server equally && start-instance-2.bat"
timeout /t 5 /nobreak >nul

echo [4/3] Starting Instance 3 on port 8083...
start "Instance-8083" cmd /k "cd /d c:\Users\sivas\Desktop\distubted server equally && start-instance-3.bat"

echo.
echo ========================================
echo All instances starting in new windows...
echo ========================================
echo.
echo Please wait 2-3 minutes for all instances to fully start.
echo You will see "Started StudentResultManagementApplication" in each window.
echo.
echo After all instances start, refresh your dashboard at:
echo http://localhost:8080
echo.
pause
