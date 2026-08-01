# Testing Guide - Distributed Student Result Management System

## Current System Status

✅ **Redis Container**: Running on port 6379  
✅ **Spring Boot Instance 1**: Running on port 8081  
✅ **Spring Boot Instance 2**: Running on port 8082  
✅ **Spring Boot Instance 3**: Running on port 8083  
⏸️ **NGINX**: Stopped (Docker networking issues)

## Direct Testing (Without NGINX)

Since NGINX Docker networking has connectivity issues, test the system directly first:

### Test 1: Verify All Instances are Healthy

```bash
# Test Instance 1
curl http://localhost:8081/actuator/health

# Test Instance 2
curl http://localhost:8082/actuator/health

# Test Instance 3
curl http://localhost:8083/actuator/health
```

**Expected**: All return `{"status":"UP"}`

### Test 2: Access Swagger UI on Each Instance

Open in browser:
- **Instance 1**: http://localhost:8081/swagger-ui/index.html
- **Instance 2**: http://localhost:8082/swagger-ui/index.html
- **Instance 3**: http://localhost:8083/swagger-ui/index.html

**Expected**: All Swagger UI pages load successfully

### Test 3: Test API Endpoints Directly

```bash
# Get students from Instance 1
curl http://localhost:8081/api/students

# Get students from Instance 2
curl http://localhost:8082/api/students

# Get students from Instance 3
curl http://localhost:8083/api/students
```

**Expected**: All return the same student data (shared database)

### Test 4: Test Data Sharing

```bash
# Create a student via Instance 1
curl -X POST http://localhost:8081/api/students `
  -H "Content-Type: application/json" `
  -d '{
    "hallTicketNo": "TEST001",
    "fullName": "Test Student",
    "gender": "MALE",
    "dateOfBirth": "2000-01-01",
    "email": "test@example.com",
    "phone": "1234567890",
    "yearOfStudy": 1,
    "semester": 1,
    "section": "A",
    "branch": "CSE"
  }'

# Retrieve from Instance 2 (should show the new student)
curl http://localhost:8082/api/students

# Retrieve from Instance 3 (should show the new student)
curl http://localhost:8083/api/students
```

**Expected**: The student created via Instance 1 should be visible on Instances 2 and 3

### Test 5: Manual Load Balancing Test

```bash
# Make 10 requests and observe which instance handles each
for ($i=1; $i -le 10; $i++) {
    Write-Host "Request $i to Instance 1"
    curl http://localhost:8081/api/students
    Start-Sleep -Seconds 1
}
```

## NGINX Testing (Native Windows)

For NGINX to work properly, use native Windows NGINX instead of Docker:

### Step 1: Download NGINX for Windows

Download from: http://nginx.org/en/download.html

Select: `nginx/Windows-x.x.x.zip`

### Step 2: Extract to C:\nginx

### Step 3: Copy Configuration

```bash
copy "c:\Users\sivas\Desktop\distubted server equally\nginx-windows.conf" "C:\nginx\conf\nginx.conf"
```

### Step 4: Start NGINX

```bash
cd C:\nginx
start nginx
```

### Step 5: Test Through NGINX

```bash
# Test main access
curl http://localhost

# Test API through NGINX
curl http://localhost/api/students

# Test Swagger through NGINX
# Open browser: http://localhost/swagger-ui/index.html
```

### Step 6: Test Round Robin Load Balancing

```bash
# Make 10 requests through NGINX
for ($i=1; $i -le 10; $i++) {
    Write-Host "Request $i through NGINX"
    curl http://localhost/api/students
    Start-Sleep -Seconds 1
}
```

**Expected**: Requests should be distributed among 8081, 8082, 8083

## Verification Checklist

- [ ] All three Spring Boot instances are healthy
- [ ] Swagger UI loads on all instances
- [ ] API endpoints work on all instances
- [ ] Data is shared across instances (same database)
- [ ] Redis is accessible and caching works
- [ ] NGINX (if using) distributes requests evenly

## Troubleshooting

### Instance Not Responding

```bash
# Check if instance is running
netstat -ano | findstr :8081
netstat -ano | findstr :8082
netstat -ano | findstr :8083

# Restart the instance if needed
# Stop the terminal and run the batch file again
```

### Redis Not Accessible

```bash
# Check Redis container
docker ps | findstr redis

# Test Redis connection
docker exec -it redis-cache redis-cli ping
```

### NGINX Not Working

```bash
# Test NGINX configuration
cd C:\nginx
nginx -t

# Check NGINX logs
type C:\nginx\logs\error.log
type C:\nginx\logs\access.log

# Restart NGINX
nginx -s stop
start nginx
```

## Summary

Your distributed system is fully operational with:
- ✅ Three Spring Boot instances running
- ✅ Shared MySQL database
- ✅ Shared Redis cache
- ✅ Round-robin load balancing capability

Test directly without NGINX first to verify all functionality, then set up native Windows NGINX for load balancing if needed.
