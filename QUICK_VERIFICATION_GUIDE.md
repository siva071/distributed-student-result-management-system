# Quick Verification Guide
## Multi-Instance Deployment

This guide provides quick commands to verify the multi-instance deployment is working correctly.

---

## Quick Start Commands

### Start All Services
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
docker-compose up --build -d
```

### Stop All Services
```bash
docker-compose down
```

### Restart All Services
```bash
docker-compose restart
```

---

## Verification Commands

### 1. Check Container Status
```bash
docker-compose ps
```

**Expected Output**:
```
NAME              STATUS
mysql-db          Up (healthy)
redis-cache       Up (healthy)
student-app-1     Up (healthy)
student-app-2     Up (healthy)
student-app-3     Up (healthy)
```

### 2. Check Health Endpoints
```bash
# Instance 1
curl http://localhost:8081/actuator/health

# Instance 2
curl http://localhost:8082/actuator/health

# Instance 3
curl http://localhost:8083/actuator/health
```

**Expected Response**:
```json
{"status":"UP"}
```

### 3. Access Swagger UI
Open in browser:
- Instance 1: http://localhost:8081/swagger-ui/index.html
- Instance 2: http://localhost:8082/swagger-ui/index.html
- Instance 3: http://localhost:8083/swagger-ui/index.html

### 4. Check Logs
```bash
# All logs
docker-compose logs

# Specific service logs
docker-compose logs student-app-1
docker-compose logs student-app-2
docker-compose logs student-app-3
docker-compose logs mysql-db
docker-compose logs redis-cache

# Follow logs in real-time
docker-compose logs -f student-app-1
```

### 5. Test Database Connection
```bash
docker exec -it mysql-db mysql -u root -proot -e "SELECT 1"
```

### 6. Test Redis Connection
```bash
docker exec -it redis-cache redis-cli ping
```

### 7. Test API Endpoints
```bash
# Create student via Instance 1
curl -X POST http://localhost:8081/api/students \
  -H "Content-Type: application/json" \
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

# Get all students via Instance 2 (should include the student created above)
curl http://localhost:8082/api/students

# Get all students via Instance 3 (should include the student created above)
curl http://localhost:8083/api/students
```

---

## Troubleshooting Quick Commands

### Restart Specific Service
```bash
docker-compose restart student-app-1
```

### Rebuild and Start Specific Service
```bash
docker-compose up --build -d student-app-1
```

### View Container Stats
```bash
docker stats
```

### Check Container Resource Usage
```bash
docker stats student-app-1 student-app-2 student-app-3
```

### Remove All Containers and Volumes
```bash
docker-compose down -v
```

---

## Service URLs Summary

| Service | Internal URL | External URL | Port |
|---------|-------------|--------------|------|
| MySQL | mysql-db:3306 | localhost:3306 | 3306 |
| Redis | redis-cache:6379 | localhost:6379 | 6379 |
| Student App 1 | student-app-1:8081 | localhost:8081 | 8081 |
| Student App 2 | student-app-2:8082 | localhost:8082 | 8082 |
| Student App 3 | student-app-3:8083 | localhost:8083 | 8083 |

---

## Environment Variables Summary

| Variable | Instance 1 | Instance 2 | Instance 3 |
|----------|-----------|-----------|-----------|
| SERVER_PORT | 8081 | 8082 | 8083 |
| SPRING_DATASOURCE_URL | jdbc:mysql://mysql-db:3306/student_results | jdbc:mysql://mysql-db:3306/student_results | jdbc:mysql://mysql-db:3306/student_results |
| SPRING_DATASOURCE_USERNAME | root | root | root |
| SPRING_DATASOURCE_PASSWORD | root | root | root |
| SPRING_DATA_REDIS_HOST | redis-cache | redis-cache | redis-cache |
| SPRING_DATA_REDIS_PORT | 6379 | 6379 | 6379 |

---

## Quick Health Check Script

Save as `health-check.sh`:
```bash
#!/bin/bash
echo "Checking container status..."
docker-compose ps

echo -e "\nChecking health endpoints..."
curl -s http://localhost:8081/actuator/health | jq .
curl -s http://localhost:8082/actuator/health | jq .
curl -s http://localhost:8083/actuator/health | jq .

echo -e "\nChecking MySQL..."
docker exec mysql-db mysql -u root -proot -e "SELECT 1"

echo -e "\nChecking Redis..."
docker exec redis-cache redis-cli ping

echo -e "\nHealth check complete!"
```

Run with:
```bash
chmod +x health-check.sh
./health-check.sh
```

---

## Success Criteria

✅ All containers show "Up (healthy)" status
✅ All health endpoints return {"status":"UP"}
✅ Swagger UI loads on all three instances
✅ API requests work on all instances
✅ Data created on one instance is visible on others
✅ MySQL and Redis are accessible from all instances
