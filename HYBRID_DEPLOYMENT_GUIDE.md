# Hybrid Multi-Instance Deployment Guide
## Distributed Student Result Management System

This guide provides instructions for deploying the Student Result Management System as a distributed system using a hybrid approach: **Local MySQL + Docker Redis + Maven Spring Boot**.

This approach is used when Docker Hub connectivity issues prevent pulling Docker images.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Local System                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ student-app-1│  │ student-app-2│  │ student-app-3│     │
│  │   Port 8081  │  │   Port 8082  │  │   Port 8083  │     │
│  │  (Maven Run) │  │  (Maven Run) │  │  (Maven Run) │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                  │                  │             │
│         └──────────────────┼──────────────────┘             │
│                            │                                │
│         ┌──────────────────┼──────────────────┐             │
│         │                  │                  │             │
│  ┌──────▼──────┐    ┌──────▼──────┐    ┌──────▼──────┐    │
│  │ Local MySQL │    │ Docker Redis│    │  (Shared)   │    │
│  │  Port 3306  │    │  Port 6379  │    │   Data      │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Key Features

- **Local MySQL**: Uses your installed MySQL Server 8.0
- **Docker Redis**: Uses Redis running in Docker container
- **Maven Spring Boot**: Three independent instances running via Maven
- **Shared Database**: All instances connect to the same local MySQL
- **Shared Cache**: All instances connect to the same Docker Redis
- **Independent Scaling**: Each instance runs independently

---

## Prerequisites

### Software Requirements

- **Java**: 21 (already installed)
- **Maven**: 3.9+ (already installed)
- **MySQL Server**: 8.0 (already installed and running)
- **Docker Desktop**: Running (for Redis container)
- **Git**: For version control (optional)

### System Requirements

- **RAM**: Minimum 4GB (8GB recommended)
- **Disk Space**: Minimum 5GB free space
- **Network**: Local network connectivity

### Verify Prerequisites

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check MySQL is running
# Open MySQL Workbench or command line
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
.\mysql.exe -u root -p
# Enter password: Shankii9900#

# Check Docker is running
docker ps
```

---

## Quick Start

### Step 1: Start Redis Container

```bash
docker run -d --name redis-cache -p 6379:6379 redis:latest
```

### Step 2: Verify Redis is Running

```bash
docker exec -it redis-cache redis-cli ping
# Expected output: PONG
```

### Step 3: Start Spring Boot Instances

Open **three separate terminal windows** and run each instance:

**Terminal 1 - Instance 1:**
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
start-instance-1.bat
```

**Terminal 2 - Instance 2:**
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
start-instance-2.bat
```

**Terminal 3 - Instance 3:**
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
start-instance-3.bat
```

### Step 4: Verify Instances are Running

Open browser and test each instance:
- **Instance 1**: http://localhost:8081/swagger-ui/index.html
- **Instance 2**: http://localhost:8082/swagger-ui/index.html
- **Instance 3**: http://localhost:8083/swagger-ui/index.html

---

## Manual Startup (Alternative to Batch Files)

If you prefer manual startup without batch files:

### Instance 1
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
set SERVER_PORT=8081
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/student_results
set SPRING_DATASOURCE_USERNAME=root
set SPRING_DATASOURCE_PASSWORD=Shankii9900#
set SPRING_DATA_REDIS_HOST=localhost
set SPRING_DATA_REDIS_PORT=6379
mvn spring-boot:run
```

### Instance 2
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
set SERVER_PORT=8082
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/student_results
set SPRING_DATASOURCE_USERNAME=root
set SPRING_DATASOURCE_PASSWORD=Shankii9900#
set SPRING_DATA_REDIS_HOST=localhost
set SPRING_DATA_REDIS_PORT=6379
mvn spring-boot:run
```

### Instance 3
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
set SERVER_PORT=8083
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/student_results
set SPRING_DATASOURCE_USERNAME=root
set SPRING_DATASOURCE_PASSWORD=Shankii9900#
set SPRING_DATA_REDIS_HOST=localhost
set SPRING_DATA_REDIS_PORT=6379
mvn spring-boot:run
```

---

## Environment Variables Configuration

### Instance 1 (Port 8081)
| Variable | Value |
|----------|-------|
| `SERVER_PORT` | 8081 |
| `SPRING_DATASOURCE_URL` | jdbc:mysql://localhost:3306/student_results |
| `SPRING_DATASOURCE_USERNAME` | root |
| `SPRING_DATASOURCE_PASSWORD` | Shankii9900# |
| `SPRING_DATA_REDIS_HOST` | localhost |
| `SPRING_DATA_REDIS_PORT` | 6379 |

### Instance 2 (Port 8082)
| Variable | Value |
|----------|-------|
| `SERVER_PORT` | 8082 |
| `SPRING_DATASOURCE_URL` | jdbc:mysql://localhost:3306/student_results |
| `SPRING_DATASOURCE_USERNAME` | root |
| `SPRING_DATASOURCE_PASSWORD` | Shankii9900# |
| `SPRING_DATA_REDIS_HOST` | localhost |
| `SPRING_DATA_REDIS_PORT` | 6379 |

### Instance 3 (Port 8083)
| Variable | Value |
|----------|-------|
| `SERVER_PORT` | 8083 |
| `SPRING_DATASOURCE_URL` jdbc:mysql://localhost:3306/student_results |
| `SPRING_DATASOURCE_USERNAME` | root |
| `SPRING_DATASOURCE_PASSWORD` | Shankii9900# |
| `SPRING_DATA_REDIS_HOST` | localhost |
| `SPRING_DATA_REDIS_PORT` | 6379 |

---

## Verification Steps

### Step 1: Check Redis Container

```bash
docker ps
# Expected: redis-cache container running
```

### Step 2: Check Redis Connection

```bash
docker exec -it redis-cache redis-cli ping
# Expected: PONG
```

### Step 3: Check MySQL Connection

```bash
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
.\mysql.exe -u root -p
# Enter password: Shankii9900#
# In MySQL prompt:
USE student_results;
SHOW TABLES;
EXIT;
```

### Step 4: Check Health Endpoints

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

### Step 5: Access Swagger UI

Open in browser:
- **Instance 1**: http://localhost:8081/swagger-ui/index.html
- **Instance 2**: http://localhost:8082/swagger-ui/index.html
- **Instance 3**: http://localhost:8083/swagger-ui/index.html

### Step 6: Test API Endpoints

```bash
# Create a student via Instance 1
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

# Retrieve the student via Instance 2 (should work due to shared database)
curl http://localhost:8082/api/students

# Retrieve the student via Instance 3 (should work due to shared database)
curl http://localhost:8083/api/students
```

### Step 7: Verify Redis Cache

```bash
# Connect to Redis
docker exec -it redis-cache redis-cli

# Check cache keys
KEYS student_result_*

# Exit Redis
EXIT
```

---

## Stopping the System

### Stop Spring Boot Instances

Press `Ctrl+C` in each terminal window where the instances are running.

### Stop Redis Container

```bash
docker stop redis-cache
docker rm redis-cache
```

### Stop MySQL (if needed)

```bash
# Stop MySQL service
net stop MySQL80

# Or use MySQL Workbench to stop the server
```

---

## Troubleshooting

### Issue 1: Port Already in Use

**Symptoms**: Error "Port 8081 is already in use"

**Solutions**:
```bash
# Find what's using the port
netstat -ano | findstr :8081

# Kill the process
taskkill /PID <PID> /F

# Or use a different port in the batch file
```

### Issue 2: MySQL Connection Refused

**Symptoms**: Spring Boot app can't connect to MySQL

**Solutions**:
```bash
# Verify MySQL is running
net start MySQL80

# Check MySQL service status
sc query MySQL80

# Test MySQL connection
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
.\mysql.exe -u root -pShankii9900# -e "SELECT 1"
```

### Issue 3: Redis Connection Failed

**Symptoms**: Spring Boot app can't connect to Redis

**Solutions**:
```bash
# Check Redis container is running
docker ps

# Restart Redis container
docker restart redis-cache

# Test Redis connection
docker exec -it redis-cache redis-cli ping
```

### Issue 4: Maven Build Fails

**Symptoms**: Maven compilation errors

**Solutions**:
```bash
# Clean and rebuild
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Check Java version
java -version
# Should be Java 21
```

### Issue 5: Instance Won't Start

**Symptoms**: Application fails to start

**Solutions**:
```bash
# Check logs in the terminal window
# Look for specific error messages

# Verify environment variables are set
echo %SERVER_PORT%
echo %SPRING_DATASOURCE_URL%

# Check application.properties
cat src/main/resources/application.properties
```

---

## Monitoring

### Check Instance Logs

Each instance logs to its terminal window. Monitor for:
- Application startup messages
- Database connection status
- Redis connection status
- API request logs
- Error messages

### Check Redis Logs

```bash
docker logs redis-cache
```

### Check MySQL Logs

MySQL logs are typically located at:
- Windows: `C:\ProgramData\MySQL\MySQL Server 8.0\Data\`
- Or check MySQL Workbench → Server → Logs

---

## Performance Considerations

### Resource Usage

Each Spring Boot instance typically uses:
- **Memory**: 500MB - 1GB
- **CPU**: 10-20% during startup, 5-10% during idle
- **Disk**: Minimal (logs only)

**Total for 3 instances**:
- **Memory**: 1.5GB - 3GB
- **CPU**: 15-30% during startup, 15-30% during idle

### Optimization Tips

1. **Reduce Logging**: Change logging level from DEBUG to INFO in application.properties
2. **Increase JVM Memory**: Add `-Xmx512m` to batch files
3. **Use Connection Pooling**: Already configured via HikariCP
4. **Enable Caching**: Already configured via Redis

---

## Scaling

### Adding More Instances

To add more instances (e.g., Instance 4 on port 8084):

1. Create `start-instance-4.bat`:
```batch
@echo off
cd "c:\Users\sivas\Desktop\distubted server equally"
set SERVER_PORT=8084
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/student_results
set SPRING_DATASOURCE_USERNAME=root
set SPRING_DATASOURCE_PASSWORD=Shankii9900#
set SPRING_DATA_REDIS_HOST=localhost
set SPRING_DATA_REDIS_PORT=6379
echo Starting Student App Instance 4 on port 8084...
mvn spring-boot:run
```

2. Open new terminal and run:
```bash
start-instance-4.bat
```

### Load Balancing

For production use, consider adding a load balancer:
- **Nginx**: Configure reverse proxy to distribute requests
- **HAProxy**: Configure round-robin load balancing
- **DNS Round Robin**: Configure DNS to return multiple IPs

---

## Comparison with Docker Compose Approach

| Feature | Docker Compose | Hybrid Approach |
|---------|----------------|-----------------|
| **MySQL** | Docker container | Local installation |
| **Redis** | Docker container | Docker container |
| **Spring Boot** | Docker containers | Maven processes |
| **Docker Hub Required** | Yes | No (for Redis only) |
| **Resource Efficiency** | High | Medium |
| **Startup Time** | Medium | Slow (Maven build) |
| **Portability** | High | Medium |
| **Ease of Use** | High | Medium |

---

## Migration to Docker Compose

When Docker Hub connectivity is restored, you can migrate to the full Docker Compose approach:

1. **Stop all Maven instances**: Press Ctrl+C in each terminal
2. **Stop Redis**: `docker stop redis-cache && docker rm redis-cache`
3. **Use docker-compose.yml**: Run `docker-compose up --build -d`
4. **Verify**: Check all instances are running correctly

---

## Summary

This hybrid deployment approach provides a working multi-instance solution when Docker Hub connectivity issues prevent using the full Docker Compose approach. It uses:

- ✅ Local MySQL Server 8.0
- ✅ Docker Redis container
- ✅ Three Maven Spring Boot instances
- ✅ Shared database and cache
- ✅ Independent instance management
- ✅ Same functionality as Docker Compose

### Key Benefits

- **No Docker Hub Required**: Only Redis image needs to be pulled (already available)
- **Local MySQL**: Uses your existing MySQL installation
- **Easy to Debug**: Direct access to application logs
- **Flexible**: Can easily add/remove instances
- **Production Ready**: Same functionality as Docker approach

### Next Steps

1. Start Redis container
2. Run the three batch files in separate terminals
3. Verify all instances are working
4. Test API endpoints
5. Configure load balancer for production
6. Monitor system performance

For Docker Compose deployment, resolve Docker Hub connectivity issues first, then use the docker-compose.yml file.
