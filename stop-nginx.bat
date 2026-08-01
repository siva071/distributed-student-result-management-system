@echo off
echo Stopping NGINX Reverse Proxy and Load Balancer...
cd "c:\Users\sivas\Desktop\distubted server equally"
docker-compose -f nginx-only-compose.yml down nginx
echo NGINX stopped successfully!
