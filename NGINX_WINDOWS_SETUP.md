# NGINX Windows Setup Guide
## Student Result Management System

Due to Docker networking issues, use native Windows NGINX instead of Docker.

---

## Quick Setup

### Step 1: Download NGINX for Windows

Download from: http://nginx.org/en/download.html

Select: `nginx/Windows-x.x.x.zip`

### Step 2: Extract NGINX

Extract to: `C:\nginx`

### Step 3: Copy Configuration File

```bash
copy "c:\Users\sivas\Desktop\distubted server equally\nginx-windows.conf" "C:\nginx\conf\nginx.conf"
```

### Step 4: Start NGINX

```bash
cd C:\nginx
start nginx
```

### Step 5: Verify NGINX is Running

```bash
tasklist | findstr nginx
```

### Step 6: Test Access

```bash
curl http://localhost
```

---

## Start/Stop Commands

### Start NGINX
```bash
cd C:\nginx
start nginx
```

### Stop NGINX
```bash
cd C:\nginx
nginx -s stop
```

### Reload Configuration
```bash
cd C:\nginx
nginx -s reload
```

### Test Configuration
```bash
cd C:\nginx
nginx -t
```

---

## Verification

### Check NGINX Status
```bash
tasklist | findstr nginx
```

### Test Load Balancing
```bash
for ($i=1; $i -le 10; $i++) {
    Write-Host "Request $i"
    curl http://localhost/api/students
    Start-Sleep -Seconds 1
}
```

### View Logs
```bash
type C:\nginx\logs\access.log
type C:\nginx\logs\error.log
```

---

## Access Points

- **Main Access**: http://localhost
- **API Endpoints**: http://localhost/api/students
- **Swagger UI**: http://localhost/swagger-ui/index.html
- **Health Check**: http://localhost/actuator/health

---

## Troubleshooting

### Port 80 Already in Use
```bash
netstat -ano | findstr :80
taskkill /PID <PID> /F
```

### Configuration Error
```bash
cd C:\nginx
nginx -t
```

### Won't Start
```bash
cd C:\nginx
nginx -s stop
start nginx
```

---

## Summary

This native Windows NGINX approach avoids Docker networking issues and provides direct connectivity to your Spring Boot instances running on localhost:8081, 8082, 8083.
