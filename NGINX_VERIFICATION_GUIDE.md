# NGINX Verification and Testing Guide
## Student Result Management System

This guide provides commands to verify NGINX is working correctly and test the round-robin load balancing.

---

## Quick Verification Commands

### 1. Check NGINX Status

```bash
docker ps | findstr nginx
```

**Expected Output**: Should show `nginx-lb` container running

### 2. Test NGINX Configuration

```bash
docker exec nginx-lb nginx -t
```

**Expected Output**:
```
nginx: configuration file /etc/nginx/nginx.conf syntax is ok
nginx: configuration file /etc/nginx/nginx.conf test is successful
```

### 3. Check Upstream Configuration

```bash
docker exec nginx-lb nginx -T | grep -A 5 "upstream student_backend"
```

**Expected Output**:
```
upstream student_backend {
    server localhost:8081 weight=1 max_fails=3 fail_timeout=30s;
    server localhost:8082 weight=1 max_fails=3 fail_timeout=30s;
    server localhost:8083 weight=1 max_fails=3 fail_timeout=30s;
```

### 4. Test Basic Access

```bash
curl -UseBasicParsing http://localhost
```

**Expected Output**: Should return the same response as accessing Spring Boot instances directly

### 5. Test API Endpoint Through NGINX

```bash
curl -UseBasicParsing http://localhost/api/students
```

**Expected Output**: Should return list of students (same as direct access)

### 6. Test Health Endpoint

```bash
curl -UseBasicParsing http://localhost/actuator/health
```

**Expected Output**: `{"status":"UP"}`

---

## Round Robin Testing

### Test 1: Multiple Sequential Requests

```bash
for ($i=1; $i -le 10; $i++) {
    Write-Host "Request $i"
    curl -UseBasicParsing http://localhost/api/students
    Write-Host ""
    Start-Sleep -Seconds 1
}
```

**Expected Behavior**: Requests should be distributed across all three instances

### Test 2: Check NGINX Access Logs

```bash
docker logs nginx-lb | Select-String "upstream"
```

**Expected Output**: Should show requests distributed among:
- `localhost:8081`
- `localhost:8082`
- `localhost:8083`

### Test 3: Monitor Spring Boot Instance Logs

Look at the terminal windows where Spring Boot instances are running. You should see requests distributed.

**Expected Pattern**:
- Request 1 → Instance 1 logs the request
- Request 2 → Instance 2 logs the request
- Request 3 → Instance 3 logs the request
- Request 4 → Instance 1 logs the request
- And so on...

### Test 4: Verify Load Distribution

```bash
# Count requests to each instance
docker logs nginx-lb | Select-String "localhost:8081" | Measure-Object
docker logs nginx-lb | Select-String "localhost:8082" | Measure-Object
docker logs nginx-lb | Select-String "localhost:8083" | Measure-Object
```

**Expected Result**: Similar counts for all three instances (roughly equal distribution)

---

## Health Check Verification

### Check All Spring Boot Instances are Healthy

```bash
curl -UseBasicParsing http://localhost:8081/actuator/health
curl -UseBasicParsing http://localhost:8082/actuator/health
curl -UseBasicParsing http://localhost:8083/actuator/health
```

**Expected Output**: All should return `{"status":"UP"}`

### Check NGINX Health Check

```bash
curl -UseBasicParsing http://localhost/health
```

**Expected Output**: `{"status":"UP"}` (proxied from one of the instances)

---

## Performance Testing

### Test Response Times

```bash
Measure-Command {
    curl -UseBasicParsing http://localhost/api/students
}
```

Run multiple times and compare with direct access to instances.

### Test Concurrent Requests

```bash
# Make 10 concurrent requests
1..10 | ForEach-Object {
    Start-Job -ScriptBlock {
        curl -UseBasicParsing http://localhost/api/students
    }
}
Get-Job | Receive-Job -Wait
```

---

## Configuration Verification

### Verify All Locations are Configured

```bash
docker exec nginx-lb nginx -T | Select-String "location"
```

**Expected Locations**:
- `/` → Root
- `/api/` → API endpoints
- `/swagger-ui/` → Swagger UI
- `/actuator/` → Actuator endpoints
- `/health` → Health check

### Verify Proxy Headers

```bash
docker exec nginx-lb nginx -T | Select-String "proxy_set_header"
```

**Expected Headers**:
- Host
- X-Real-IP
- X-Forwarded-For
- X-Forwarded-Proto
- X-Forwarded-Host
- X-Forwarded-Port
- Connection

### Verify Security Headers

```bash
curl -I http://localhost
```

**Expected Headers**:
- X-Frame-Options
- X-Content-Type-Options
- X-XSS-Protection
- Referrer-Policy

---

## Logging Verification

### View NGINX Access Logs

```bash
docker logs nginx-lb
```

### View NGINX Error Logs

```bash
docker logs nginx-lb 2>&1 | Select-String "error"
```

### View Real-time Logs

```bash
docker logs -f nginx-lb
```

### Check Log Format

```bash
docker logs nginx-lb | Select-Object -Last 5
```

**Expected Format**: Should include upstream address and response time

---

## Troubleshooting Verification

### Check if Port 80 is Available

```bash
netstat -ano | findstr :80
```

**Expected**: Should show NGINX listening on port 80

### Check if Spring Boot Instances are Accessible

```bash
curl -UseBasicParsing http://localhost:8081/api/students
curl -UseBasicParsing http://localhost:8082/api/students
curl -UseBasicParsing http://localhost:8083/api/students
```

**Expected**: All should return valid responses

### Check Docker Network

```bash
docker network ls
docker network inspect student-result-network
```

**Expected**: Network should exist and include NGINX container

---

## Success Criteria

✅ NGINX container is running
✅ NGINX configuration test passes
✅ Upstream configuration shows all three instances
✅ Basic access through NGINX works
✅ API endpoints work through NGINX
✅ Health endpoint works through NGINX
✅ Requests are distributed across all instances
✅ NGINX logs show upstream information
✅ Security headers are present
✅ Response times are acceptable

---

## Quick Test Script

Save as `test-nginx.ps1`:

```powershell
Write-Host "Testing NGINX Reverse Proxy and Load Balancer..." -ForegroundColor Green

# Test 1: NGINX Status
Write-Host "`nTest 1: NGINX Status" -ForegroundColor Yellow
docker ps | findstr nginx

# Test 2: Configuration Test
Write-Host "`nTest 2: Configuration Test" -ForegroundColor Yellow
docker exec nginx-lb nginx -t

# Test 3: Basic Access
Write-Host "`nTest 3: Basic Access" -ForegroundColor Yellow
curl -UseBasicParsing http://localhost

# Test 4: API Access
Write-Host "`nTest 4: API Access" -ForegroundColor Yellow
curl -UseBasicParsing http://localhost/api/students

# Test 5: Health Check
Write-Host "`nTest 5: Health Check" -ForegroundColor Yellow
curl -UseBasicParsing http://localhost/actuator/health

# Test 6: Round Robin Test
Write-Host "`nTest 6: Round Robin Test (10 requests)" -ForegroundColor Yellow
for ($i=1; $i -le 10; $i++) {
    Write-Host "Request $i"
    curl -UseBasicParsing http://localhost/api/students | Out-Null
    Start-Sleep -Seconds 1
}

# Test 7: Load Distribution
Write-Host "`nTest 7: Load Distribution" -ForegroundColor Yellow
Write-Host "Requests to 8081:"
docker logs nginx-lb | Select-String "localhost:8081" | Measure-Object
Write-Host "Requests to 8082:"
docker logs nginx-lb | Select-String "localhost:8082" | Measure-Object
Write-Host "Requests to 8083:"
docker logs nginx-lb | Select-String "localhost:8083" | Measure-Object

Write-Host "`nAll tests completed!" -ForegroundColor Green
```

Run with:
```powershell
.\test-nginx.ps1
```
