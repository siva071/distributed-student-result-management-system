@echo off
echo Starting NGINX Reverse Proxy and Load Balancer...
cd "c:\Users\sivas\Desktop\distubted server equally"
docker-compose -f nginx-only-compose.yml up -d nginx
echo NGINX started successfully!
echo Access the application at: http://localhost
echo.
echo To view NGINX logs: docker logs -f nginx-lb
echo To stop NGINX: stop-nginx.bat
