# NGINX Troubleshooting Guide
## Student Result Management System

This guide provides solutions to common issues when using NGINX as a reverse proxy and load balancer.

---

## Common Issues and Solutions

### Issue 1: NGINX Container Won't Start

**Symptoms**:
- Container exits immediately
- `docker ps` doesn't show nginx-lb
- Error: "port is already allocated"

**Solutions**:

```bash
# Check what's using port 80
netstat -ano | findstr :80

# Kill the process using port 80
taskkill /PID <PID> /F

# Or change NGINX port in nginx.conf
# Change: listen 80;
# To: listen 8080;

# Check NGINX logs
docker logs nginx-lb

# Test configuration
docker exec nginx-lb nginx -t
```

### Issue 2: 502 Bad Gateway Error

**Symptoms**:
- NGINX returns 502 Bad Gateway
- Browser shows "502 Bad Gateway"
- API calls fail through NGINX

**Solutions**:

```bash
# Check if Spring Boot instances are running
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# Check NGINX upstream configuration
docker exec nginx-lb nginx -T | grep -A 5 "upstream student_backend"

# Check NGINX error logs
docker logs nginx-lb 2>&1 | Select-String "error"

# Verify network connectivity
docker network inspect student-result-network

# Restart NGINX
docker-compose -f nginx-docker-compose.yml restart nginx
```

### Issue 3: Requests Not Distributed (All Go to One Instance)

**Symptoms**:
- All requests go to the same Spring Boot instance
- Load balancing not working
- Round Robin not functioning

**Solutions**:

```bash
# Check upstream configuration
docker exec nginx-lb nginx -T | grep -A 5 "upstream student_backend"

# Verify all instances are healthy
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# Reload NGINX configuration
docker exec nginx-lb nginx -s reload

# Restart NGINX
docker-compose -f nginx-docker-compose.yml restart nginx

# Check if instances are marked as down
docker logs nginx-lb | Select-String "max_fails"
```

### Issue 4: Connection Timeout

**Symptoms**:
- Requests timeout through NGINX
- "504 Gateway Timeout" errors
- Slow response times

**Solutions**:

```bash
# Increase proxy timeouts in nginx.conf
# Add or modify:
proxy_connect_timeout 120s;
proxy_send_timeout 120s;
proxy_read_timeout 120s;

# Reload NGINX
docker exec nginx-lb nginx -s reload

# Check Spring Boot instance response times
curl -UseBasicParsing http://localhost:8081/api/students
curl -UseBasicParsing http://localhost:8082/api/students
curl -UseBasicParsing http://localhost:8083/api/students

# Check NGINX logs for slow requests
docker logs nginx-lb | Select-String "upstream_response_time"
```

### Issue 5: Configuration Syntax Error

**Symptoms**:
- NGINX won't start
- "configuration file test failed"
- Syntax errors in nginx.conf

**Solutions**:

```bash
# Test configuration
docker exec nginx-lb nginx -t

# Check error logs
docker logs nginx-lb 2>&1 | Select-String "error"

# Validate nginx.conf syntax
# Check for:
# - Missing semicolons
# - Unmatched braces
# - Typos in directives

# Restore backup configuration
# If you have a backup, restore it
```

### Issue 6: SSL/TLS Certificate Issues

**Symptoms**:
- HTTPS not working
- Certificate errors
- Mixed content warnings

**Solutions**:

```bash
# Check certificate files exist
docker exec nginx-lb ls -la /etc/nginx/ssl/

# Verify certificate permissions
docker exec nginx-lb ls -la /etc/nginx/ssl/cert.pem
docker exec nginx-lb ls -la /etc/nginx/ssl/key.pem

# Test SSL configuration
docker exec nginx-lb nginx -t

# Check certificate validity
openssl x509 -in cert.pem -text -noout
```

### Issue 7: High Memory Usage

**Symptoms**:
- NGINX container using excessive memory
- System slowdown
- Out of memory errors

**Solutions**:

```bash
# Check container resource usage
docker stats nginx-lb

# Reduce worker connections in nginx.conf
# Modify: worker_connections 512;

# Reduce buffer sizes
proxy_buffer_size 2k;
proxy_buffers 4 2k;

# Restart NGINX
docker-compose -f nginx-docker-compose.yml restart nginx
```

### Issue 8: Logs Not Writing

**Symptoms**:
- No access logs
- No error logs
- Empty log files

**Solutions**:

```bash
# Check log directory permissions
docker exec nginx-lb ls -la /var/log/nginx/

# Check if log directory exists
docker exec nginx-lb mkdir -p /var/log/nginx

# Check NGINX user permissions
docker exec nginx-lb chown -R nginx:nginx /var/log/nginx

# Restart NGINX
docker-compose -f nginx-docker-compose.yml restart nginx
```

### Issue 9: WebSocket Connections Failing

**Symptoms**:
- WebSocket connections drop
- Real-time features not working
- Connection reset errors

**Solutions**:

```bash
# Add WebSocket support to nginx.conf
# Add to location block:
proxy_http_version 1.1;
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";

# Reload NGINX
docker exec nginx-lb nginx -s reload
```

### Issue 10: CORS Errors

**Symptoms**:
- CORS errors in browser
- API calls blocked by browser
- "Access-Control-Allow-Origin" errors

**Solutions**:

```bash
# Add CORS headers to nginx.conf
# Add to location block:
add_header Access-Control-Allow-Origin *;
add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
add_header Access-Control-Allow-Headers "Origin, Content-Type, Accept, Authorization";

# Handle OPTIONS requests
if ($request_method = 'OPTIONS') {
    return 204;
}

# Reload NGINX
docker exec nginx-lb nginx -s reload
```

---

## Diagnostic Commands

### General Diagnostics

```bash
# Check NGINX status
docker ps | findstr nginx

# Check container health
docker inspect nginx-lb | Select-String "Health"

# View container details
docker inspect nginx-lb

# Check resource usage
docker stats nginx-lb
```

### Configuration Diagnostics

```bash
# Test configuration
docker exec nginx-lb nginx -t

# View full configuration
docker exec nginx-lb nginx -T

# Check upstream configuration
docker exec nginx-lb nginx -T | grep -A 10 "upstream"

# Check server configuration
docker exec nginx-lb nginx -T | grep -A 20 "server {"
```

### Network Diagnostics

```bash
# Check Docker network
docker network ls
docker network inspect student-result-network

# Test connectivity to Spring Boot instances
docker exec nginx-lb wget -O- http://localhost:8081/actuator/health
docker exec nginx-lb wget -O- http://localhost:8082/actuator/health
docker exec nginx-lb wget -O- http://localhost:8083/actuator/health

# Check DNS resolution
docker exec nginx-lb nslookup localhost
```

### Log Diagnostics

```bash
# View recent logs
docker logs --tail 100 nginx-lb

# View error logs only
docker logs nginx-lb 2>&1 | Select-String "error"

# View upstream logs
docker logs nginx-lb | Select-String "upstream"

# View access logs with timestamps
docker logs nginx-lb | Select-String "GET\|POST\|PUT\|DELETE"
```

---

## Performance Issues

### Slow Response Times

**Symptoms**: NGINX adds latency to requests

**Solutions**:

```bash
# Enable caching in nginx.conf
proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=my_cache:10m max_size=1g inactive=60m;

proxy_cache my_cache;
proxy_cache_valid 200 60m;
proxy_cache_bypass $http_upgrade;

# Enable connection pooling
upstream student_backend {
    keepalive 32;
    keepalive_requests 100;
    keepalive_timeout 60s;
}

# Enable gzip compression
gzip on;
gzip_types application/json application/xml text/plain;
```

### High CPU Usage

**Symptoms**: NGINX using high CPU

**Solutions**:

```bash
# Reduce worker processes
worker_processes 2;

# Reduce worker connections
worker_connections 512;

# Disable unnecessary logging
access_log off;

# Enable caching
proxy_cache on;
```

---

## Security Issues

### Unauthorized Access

**Symptoms**: Unauthorized access to sensitive endpoints

**Solutions**:

```bash
# Restrict actuator endpoints
location /actuator/ {
    allow 127.0.0.1;
    deny all;
    proxy_pass http://student_backend;
}

# Add authentication
# Use basic auth or token-based auth
```

### DDoS Attacks

**Symptoms**: Excessive requests, service degradation

**Solutions**:

```bash
# Add rate limiting
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;

limit_req zone=api_limit burst=20 nodelay;

# Add connection limiting
limit_conn_zone $binary_remote_addr zone=conn_limit:10m;
limit_conn conn_limit 10;
```

---

## Recovery Procedures

### Restore from Backup

```bash
# Stop NGINX
docker-compose -f nginx-docker-compose.yml down nginx

# Restore nginx.conf from backup
copy nginx.conf.backup nginx.conf

# Restart NGINX
docker-compose -f nginx-docker-compose.yml up -d nginx
```

### Emergency Restart

```bash
# Force stop NGINX
docker stop nginx-lb
docker rm nginx-lb

# Start NGINX fresh
docker-compose -f nginx-docker-compose.yml up -d nginx
```

### Configuration Reset

```bash
# Reset to default configuration
docker exec nginx-lb cp /etc/nginx/nginx.conf.default /etc/nginx/nginx.conf

# Reload NGINX
docker exec nginx-lb nginx -s reload
```

---

## Prevention and Maintenance

### Regular Maintenance Tasks

```bash
# Weekly: Check logs for errors
docker logs nginx-lb --since 7d | Select-String "error"

# Weekly: Check configuration
docker exec nginx-lb nginx -t

# Monthly: Review and update configuration
# Review nginx.conf for optimizations
# Update security headers if needed

# Monthly: Check disk space
docker system df
```

### Monitoring Setup

```bash
# Enable status module in nginx.conf
# Add to http block:
server {
    listen 8080;
    location /nginx_status {
        stub_status on;
        allow 127.0.0.1;
        deny all;
    }
}

# Monitor status
curl http://localhost:8080/nginx_status
```

---

## Contact and Support

If issues persist after trying these solutions:

1. Check NGINX documentation: http://nginx.org/en/docs/
2. Check Docker documentation: https://docs.docker.com/
3. Review application logs for errors
4. Verify all prerequisites are met
5. Consider reaching out to system administrator

---

## Quick Reference

### Common Commands

```bash
# Start NGINX
.\start-nginx.bat

# Stop NGINX
.\stop-nginx.bat

# Restart NGINX
docker-compose -f nginx-docker-compose.yml restart nginx

# View logs
docker logs -f nginx-lb

# Test configuration
docker exec nginx-lb nginx -t

# Reload configuration
docker exec nginx-lb nginx -s reload
```

### Log Locations

- **Access Logs**: `docker logs nginx-lb`
- **Error Logs**: `docker logs nginx-lb 2>&1`
- **Configuration**: `nginx.conf`
- **Docker Compose**: `nginx-docker-compose.yml`

### Important Files

- `nginx.conf` - Main NGINX configuration
- `nginx-docker-compose.yml` - Docker Compose configuration
- `start-nginx.bat` - Start script
- `stop-nginx.bat` - Stop script
- `NGINX_SETUP_GUIDE.md` - Setup guide
- `NGINX_VERIFICATION_GUIDE.md` - Verification guide
