@echo off
echo =========================================
echo Enterprise Deployment Startup Script
echo Student Result Management System
echo =========================================
echo.
echo This script starts the complete enterprise stack:
echo - HAProxy Load Balancer (Port 80)
echo - Prometheus Monitoring (Port 9090)
echo - Grafana Visualization (Port 3000)
echo - Redis Cache (Port 6379)
echo.
echo Prerequisites:
echo 1. Three Spring Boot instances must be running on ports 8081, 8082, 8083
echo 2. Docker Desktop must be running
echo.
echo Starting Docker Compose Enterprise Stack...
echo.
cd "c:\Users\sivas\Desktop\distubted server equally"
docker-compose -f docker-compose-enterprise.yml up -d
echo.
echo =========================================
echo Enterprise Stack Started Successfully!
echo =========================================
echo.
echo Access Points:
echo   HAProxy Load Balancer: http://localhost
echo   HAProxy Stats: http://localhost:8404/stats (admin/admin123)
echo   Prometheus: http://localhost:9090
echo   Grafana: http://localhost:3000 (admin/admin123)
echo   Redis: localhost:6379
echo.
echo API Endpoints (via HAProxy):
echo   Students API: http://localhost/api/students
echo   Subjects API: http://localhost/api/subjects
echo   Results API: http://localhost/api/results
echo   Swagger UI: http://localhost/swagger-ui/index.html
echo   Health Check: http://localhost/actuator/health
echo   Prometheus Metrics: http://localhost/actuator/prometheus
echo.
echo =========================================
echo Monitoring Setup Instructions
echo =========================================
echo.
echo 1. Open Grafana: http://localhost:3000
echo 2. Login with admin/admin123
echo 3. Add Prometheus datasource (already configured)
echo 4. Import dashboards for Spring Boot monitoring
echo.
pause
