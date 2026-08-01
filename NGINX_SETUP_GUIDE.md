# NGINX Reverse Proxy and Load Balancer Setup Guide
## Student Result Management System

This guide provides complete instructions for setting up NGINX as a reverse proxy and load balancer for the distributed Spring Boot application.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Windows Installation](#windows-installation)
3. [Configuration Setup](#configuration-setup)
4. [Starting NGINX](#starting-nginx)
5. [Stopping NGINX](#stopping-nginx)
6. [Verification](#verification)
7. [Testing Round Robin](#testing-round-robin)
8. [Logging](#logging)
9. [Troubleshooting](#troubleshooting)
10. [Production Best Practices](#production-best-practices)

---

## Architecture Overview

```
                    Users
                      |
                      |
                  NGINX (Port 80)
                      |
           ----------------------------
           |            |            |
           |            |            |
      localhost:8081  localhost:8082  localhost:8083
      (Instance 1)    (Instance 2)    (Instance 3)
           |            |            |
           ----------------------------
                      |
            ------------------------
            |                      |
      Local MySQL (3306)    Docker Redis (6379)
```

### Load Balancing Strategy

- **Algorithm**: Round Robin (default NGINX behavior)
- **Distribution**: Equal weight for all three instances
- **Health Checks**: Passive (max_fails=3, fail_timeout=30s)
- **Session Persistence**: Disabled (no sticky sessions)

---

## Windows Installation

### Option 1: Using Docker (Recommended)

Since you already have Docker Desktop running, this is the easiest method.

#### Step 1: Create nginx-docker-compose.yml

Already created at: `nginx-docker-compose.yml`

#### Step 2: Start NGINX with Docker

```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
docker-compose -f nginx-docker-compose.yml up -d nginx
```

#### Step 3: Verify NGINX is Running

```bash
docker ps
# Should show nginx-lb container
```

### Option 2: Native Windows Installation

#### Step 1: Download NGINX for Windows

Download from: http://nginx.org/en/download.html

Select the stable version for Windows (nginx/Windows-x.x.x.zip)

#### Step 2: Extract NGINX

Extract to: `C:\nginx`

#### Step 3: Copy Configuration File

```bash
copy "c:\Users\sivas\Desktop\distubted server equally\nginx.conf" "C:\nginx\conf\nginx.conf"
```

#### Step 4: Start NGINX

```bash
cd C:\nginx
start nginx
```

#### Step 5: Verify NGINX is Running

```bash
tasklist | findstr nginx
```

---

## Configuration Setup

### NGINX Configuration File

The `nginx.conf` file includes:

#### Upstream Block
```nginx
upstream student_backend {
    server localhost:8081 weight=1 max_fails=3 fail_timeout=30s;
    server localhost:8082 weight=1 max_fails=3 fail_timeout=30s;
    server localhost:8083 weight=1 max_fails=3 fail_timeout=30s;
    keepalive 32;
    keepalive_requests 100;
    keepalive_timeout 60s;
}
```

#### Proxy Configuration
```nginx
location / {
    proxy_pass http://student_backend;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header Connection "";
}
```

#### Key Features

- **Round Robin**: Default load balancing algorithm
- **Health Checks**: Passive health checking with max_fails and fail_timeout
- **Connection Pooling**: Keepalive connections for better performance
- **Security Headers**: X-Frame-Options, X-Content-Type-Options, X-XSS-Protection
- **Gzip Compression**: Enabled for text-based content
- **Logging**: Detailed access and error logging with upstream information

---

## Starting NGINX

### Docker Method

```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
docker-compose -f nginx-docker-compose.yml up -d nginx
```

### Native Windows Method

```bash
cd C:\nginx
start nginx
```

### Verify NGINX Started

```bash
# Docker method
docker ps | findstr nginx

# Native method
tasklist | findstr nginx
```

---

## Stopping NGINX

### Docker Method

```bash
docker-compose -f nginx-docker-compose.yml down nginx
```

### Native Windows Method

```bash
cd C:\nginx
nginx -s stop
```

### Force Stop (if needed)

```bash
# Docker method
docker stop nginx-lb

# Native method
taskkill /F /IM nginx.exe
```

---

## Verification

### Step 1: Check NGINX Status

```bash
# Docker method
docker ps | findstr nginx

# Native method
tasklist | findstr nginx
```

### Step 2: Test NGINX Configuration

```bash
# Docker method
docker exec nginx-lb nginx -t

# Native method
cd C:\nginx
nginx -t
```

**Expected Output**:
```
nginx: configuration file C:\nginx/conf/nginx.conf syntax is ok
nginx: configuration file C:\nginx/conf/nginx.conf test is successful
```

### Step 3: Check NGINX Access

```bash
curl http://localhost
```

Should return the same response as accessing the Spring Boot instances directly.

### Step 4: Check Upstream Configuration

```bash
# Docker method
docker exec nginx-lb nginx -T | grep -A 5 "upstream student_backend"

# Native method
nginx -T | grep -A 5 "upstream student_backend"
```

### Step 5: Check NGINX Logs

```bash
# Docker method
docker logs nginx-lb

# Native method
type C:\nginx\logs\access.log
type C:\nginx\logs\error.log
```

---

## Testing Round Robin

### Step 1: Make Multiple Requests

```bash
# Make 10 requests to see distribution
for i in {1..10}; do curl http://localhost/api/students; echo ""; done
```

### Step 2: Check Spring Boot Logs

Look at the terminal windows where your Spring Boot instances are running. You should see requests distributed across all three instances.

Expected pattern:
- Request 1 → Instance 1 (8081)
- Request 2 → Instance 2 (8082)
- Request 3 → Instance 3 (8083)
- Request 4 → Instance 1 (8081)
- Request 5 → Instance 2 (8082)
- And so on...

### Step 3: Verify Load Distribution

```bash
# Check NGINX access logs
# Docker method
docker logs nginx-lb | grep "upstream"

# Native method
type C:\nginx\logs\access.log | findstr "upstream"
```

You should see requests distributed among:
- `localhost:8081`
- `localhost:8082`
- `localhost:8083`

---

## Logging

### NGINX Access Logs

NGINX logs include:
- Remote address
- Request details
- Response status
- Upstream address (which instance handled the request)
- Response time

### Spring Boot Instance Logs

Each Spring Boot instance logs:
- Timestamp
- Request URI
- Instance identifier (based on port)

To enhance logging, you can add a custom filter in Spring Boot to log the instance handling each request.

### View Real-time Logs

```bash
# Docker method
docker logs -f nginx-lb

# Native method
# Use a tail utility or open the log file in a text editor
```

---

## Troubleshooting

### Issue 1: NGINX Won't Start

**Symptoms**: NGINX fails to start

**Solutions**:
```bash
# Check configuration syntax
nginx -t

# Check for port conflicts
netstat -ano | findstr :80

# Check error logs
type C:\nginx\logs\error.log
```

### Issue 2: 502 Bad Gateway

**Symptoms**: NGINX returns 502 errors

**Solutions**:
```bash
# Check if Spring Boot instances are running
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# Check NGINX upstream configuration
nginx -T | grep -A 5 "upstream"

# Check NGINX error logs
type C:\nginx\logs\error.log
```

### Issue 3: Requests Not Distributed

**Symptoms**: All requests go to one instance

**Solutions**:
```bash
# Check upstream configuration
nginx -T | grep -A 5 "upstream student_backend"

# Verify all instances are healthy
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# Reload NGINX configuration
nginx -s reload
```

### Issue 4: Port 80 Already in Use

**Symptoms**: NGINX can't bind to port 80

**Solutions**:
```bash
# Find what's using port 80
netstat -ano | findstr :80

# Kill the process
taskkill /PID <PID> /F

# Or change NGINX port in nginx.conf
listen 8080;  # Change from 80 to 8080
```

### Issue 5: Connection Timeout

**Symptoms**: Requests timeout through NGINX

**Solutions**:
```bash
# Increase proxy timeouts in nginx.conf
proxy_connect_timeout 120s;
proxy_send_timeout 120s;
proxy_read_timeout 120s;

# Reload NGINX
nginx -s reload
```

---

## Production Best Practices

### Security

1. **HTTPS**: Enable SSL/TLS for production
2. **Security Headers**: Already configured in nginx.conf
3. **Rate Limiting**: Add rate limiting to prevent abuse
4. **IP Whitelisting**: Restrict access to actuator endpoints
5. **Firewall**: Configure firewall rules

### Performance

1. **Worker Processes**: Set to auto (already configured)
2. **Worker Connections**: Increase based on expected load
3. **Keepalive**: Already configured for connection pooling
4. **Gzip Compression**: Already enabled
5. **Caching**: Add caching for static content
6. **Buffer Sizes**: Tune based on your application needs

### High Availability

1. **Multiple NGINX Instances**: Use multiple NGINX instances with keepalived
2. **Health Checks**: Implement active health checks
3. **Load Balancing Algorithms**: Consider least_conn or ip_hash for specific use cases
4. **Session Affinity**: Enable if your application requires it
5. **Monitoring**: Use monitoring tools (Prometheus, Grafana)

### Monitoring

1. **NGINX Status Module**: Enable stub_status for monitoring
2. **Log Analysis**: Use ELK stack or similar for log analysis
3. **Metrics**: Export metrics to Prometheus
4. **Alerting**: Set up alerts for failures and high latency
5. **Dashboards**: Create monitoring dashboards

### Backup and Recovery

1. **Configuration Backup**: Regular backup of nginx.conf
2. **Log Rotation**: Implement log rotation to prevent disk space issues
3. **Disaster Recovery**: Have a plan for NGINX failures
4. **Testing**: Regular testing of failover scenarios

---

## Quick Reference Commands

### Docker Method

```bash
# Start NGINX
docker-compose -f nginx-docker-compose.yml up -d nginx

# Stop NGINX
docker-compose -f nginx-docker-compose.yml down nginx

# Restart NGINX
docker-compose -f nginx-docker-compose.yml restart nginx

# View logs
docker logs -f nginx-lb

# Test configuration
docker exec nginx-lb nginx -t

# Reload configuration
docker exec nginx-lb nginx -s reload

# View upstream configuration
docker exec nginx-lb nginx -T | grep -A 5 "upstream"
```

### Native Windows Method

```bash
# Start NGINX
cd C:\nginx
start nginx

# Stop NGINX
nginx -s stop

# Restart NGINX
nginx -s reload

# Test configuration
nginx -t

# View logs
type C:\nginx\logs\access.log
type C:\nginx\logs\error.log

# View upstream configuration
nginx -T | grep -A 5 "upstream"
```

---

## Summary

This guide provides a complete NGINX reverse proxy and load balancer setup for your distributed Spring Boot application. The configuration includes:

- ✅ Round Robin load balancing
- ✅ Health checks
- ✅ Security headers
- ✅ Gzip compression
- ✅ Detailed logging
- ✅ Connection pooling
- ✅ Production-ready configuration

### Next Steps

1. Choose installation method (Docker or Native Windows)
2. Follow the installation steps
3. Start NGINX
4. Verify load balancing is working
5. Monitor logs and performance
6. Implement additional security measures for production

For production deployment, consider implementing HTTPS, monitoring, and high availability features.
