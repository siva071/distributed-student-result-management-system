# Docker Deployment Guide

## Overview
This guide provides comprehensive instructions for deploying the Distributed Student Result Management System using Docker Compose with multiple Spring Boot instances.

## Prerequisites

### Required Software
- Docker Desktop (Windows/Mac) or Docker Engine (Linux)
- Docker Compose (included with Docker Desktop)
- Git (optional, for cloning the repository)

### Verify Docker Installation
```bash
docker --version
docker-compose --version
```

## Project Structure

```
distubted server equally/
├── Dockerfile                          # Multi-stage Docker build
├── docker-compose.yml                  # Single instance deployment
├── docker-compose.prod.yml             # Multi-instance deployment (3 apps)
├── .dockerignore                       # Docker build exclusions
├── pom.xml                            # Maven dependencies
├── src/
│   └── main/
│       ├── java/
│       │   └── com/studentresult/
│       │       ├── config/
│       │       │   └── RedisConfig.java
│       │       ├── entity/
│       │       ├── service/
│       │       ├── controller/
│       │       └── StudentResultManagementApplication.java
│       └── resources/
│           └── application.properties  # Environment variable support
```

## Deployment Options

### Option 1: Single Instance Deployment (Development)
Uses `docker-compose.yml` with one Spring Boot instance on port 8080.

### Option 2: Multi-Instance Deployment (Production)
Uses `docker-compose.prod.yml` with three Spring Boot instances on ports 8081, 8082, and 8083.

---

## Option 1: Single Instance Deployment

### Step 1: Build and Start Services
```bash
# Navigate to project directory
cd "c:\Users\sivas\Desktop\distubted server equally"

# Build and start all services
docker-compose up --build -d
```

### Step 2: Verify Services are Running
```bash
# Check all containers
docker-compose ps

# Expected output:
# NAME                STATUS
# mysql-db            Up (healthy)
# redis-cache         Up (healthy)
# student-result-app  Up (healthy)
```

### Step 3: View Logs
```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f student-result-app
docker-compose logs -f mysql-db
docker-compose logs -f redis-cache
```

### Step 4: Verify Application
```bash
# Check health endpoint
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

### Step 5: Access Swagger UI
Open browser: `http://localhost:8080/swagger-ui/index.html`

### Step 6: Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

## Option 2: Multi-Instance Deployment (Production)

### Step 1: Build and Start Services
```bash
# Navigate to project directory
cd "c:\Users\sivas\Desktop\distubted server equally"

# Build and start all services with production configuration
docker-compose -f docker-compose.prod.yml up --build -d
```

### Step 2: Verify Services are Running
```bash
# Check all containers
docker-compose -f docker-compose.prod.yml ps

# Expected output:
# NAME                STATUS
# mysql-db            Up (healthy)
# redis-cache         Up (healthy)
# student-app-1       Up (healthy)
# student-app-2       Up (healthy)
# student-app-3       Up (healthy)
```

### Step 3: View Logs
```bash
# View all logs
docker-compose -f docker-compose.prod.yml logs -f

# View specific instance logs
docker-compose -f docker-compose.prod.yml logs -f student-app-1
docker-compose -f docker-compose.prod.yml logs -f student-app-2
docker-compose -f docker-compose.prod.yml logs -f student-app-3
```

### Step 4: Verify All Instances
```bash
# Check health endpoints for all instances
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# Expected response for all:
# {"status":"UP"}
```

### Step 5: Access Swagger UI for Each Instance
- Instance 1: `http://localhost:8081/swagger-ui/index.html`
- Instance 2: `http://localhost:8082/swagger-ui/index.html`
- Instance 3: `http://localhost:8083/swagger-ui/index.html`

### Step 6: Stop Services
```bash
# Stop all services
docker-compose -f docker-compose.prod.yml down

# Stop and remove volumes
docker-compose -f docker-compose.prod.yml down -v
```

---

## Service Architecture

### Docker Network
All services communicate via the `student-result-network` bridge network.

### Service Communication
- Spring Boot instances connect to MySQL using hostname: `mysql-db`
- Spring Boot instances connect to Redis using hostname: `redis-cache`
- No localhost usage within Docker containers

### Shared Resources
- **MySQL Database**: All three instances share the same `student_results` database
- **Redis Cache**: All three instances share the same Redis cache
- **Docker Image**: All three instances use the same Docker image

### Environment Variables

#### MySQL
- `MYSQL_ROOT_PASSWORD`: root
- `MYSQL_DATABASE`: student_results
- `MYSQL_USER`: root
- `MYSQL_PASSWORD`: root

#### Redis
- Port: 6379
- No authentication (default)

#### Spring Boot Instances
- `SERVER_PORT`: 8081, 8082, or 8083
- `SPRING_DATASOURCE_URL`: jdbc:mysql://mysql-db:3306/student_results
- `SPRING_DATASOURCE_USERNAME`: root
- `SPRING_DATASOURCE_PASSWORD`: root
- `SPRING_DATA_REDIS_HOST`: redis-cache
- `SPRING_DATA_REDIS_PORT`: 6379

---

## Health Checks

### MySQL Health Check
```bash
# Check MySQL health
docker exec mysql-db mysqladmin ping -h localhost -u root -proot

# Expected: mysqld is alive
```

### Redis Health Check
```bash
# Check Redis health
docker exec redis-cache redis-cli ping

# Expected: PONG
```

### Spring Boot Health Check
```bash
# Check Spring Boot health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# Expected: {"status":"UP"}
```

### Docker Health Status
```bash
# View health status of all containers
docker ps --format "table {{.Names}}\t{{.Status}}"
```

---

## Persistent Volumes

### MySQL Data Volume
- Volume name: `mysql-data`
- Mount point: `/var/lib/mysql`
- Purpose: Persist database data across container restarts

### Redis Data Volume
- Volume name: `redis-data`
- Mount point: `/data`
- Purpose: Persist cache data across container restarts

### View Volumes
```bash
# List all volumes
docker volume ls

# Inspect specific volume
docker volume inspect mysql-data
docker volume inspect redis-data
```

---

## Testing the Deployment

### Test 1: Create Student via Instance 1
```bash
curl -X POST http://localhost:8081/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "hallTicketNo": "2024CS001",
    "fullName": "John Doe",
    "gender": "Male",
    "dateOfBirth": "2000-05-15",
    "email": "john.doe@example.com",
    "phone": "9876543210",
    "department": "Computer Science",
    "yearOfStudy": 3,
    "semester": 5,
    "section": "A"
  }'
```

### Test 2: Retrieve Student via Instance 2
```bash
curl http://localhost:8082/api/students/1
```

### Test 3: Retrieve Student via Instance 3
```bash
curl http://localhost:8083/api/students/1
```

### Expected Result
All three instances should return the same student data, confirming they share the same MySQL database.

### Test 4: Verify Redis Caching
```bash
# Clear Redis cache
docker exec redis-cache redis-cli FLUSHALL

# Get student from instance 1 (Cache Miss → MySQL)
curl http://localhost:8081/api/students/1

# Get student from instance 2 (Cache Hit → Redis)
curl http://localhost:8082/api/students/1

# Get student from instance 3 (Cache Hit → Redis)
curl http://localhost:8083/api/students/1
```

---

## Monitoring

### View Container Resource Usage
```bash
# View real-time resource usage
docker stats

# View specific container stats
docker stats student-app-1 student-app-2 student-app-3
```

### View Logs
```bash
# Follow logs for all instances
docker-compose -f docker-compose.prod.yml logs -f

# View last 100 lines
docker-compose -f docker-compose.prod.yml logs --tail=100

# View logs since specific time
docker-compose -f docker-compose.prod.yml logs --since=10m
```

### Inspect Container
```bash
# Inspect container details
docker inspect student-app-1

# View container environment variables
docker exec student-app-1 env
```

---

## Scaling

### Scale Spring Boot Instances
```bash
# Scale to 5 instances (requires load balancer)
docker-compose -f docker-compose.prod.yml up --scale student-app-1=5 -d
```

Note: Scaling requires additional port mapping configuration and a load balancer (e.g., Nginx) for production use.

---

## Restart Policies

### Current Configuration
- `restart: unless-stopped` - Containers restart automatically unless explicitly stopped

### Manual Restart
```bash
# Restart specific service
docker-compose -f docker-compose.prod.yml restart student-app-1

# Restart all services
docker-compose -f docker-compose.prod.yml restart
```

---

## Backup and Restore

### Backup MySQL Database
```bash
# Backup database
docker exec mysql-db mysqldump -u root -proot student_results > backup.sql

# Restore database
docker exec -i mysql-db mysql -u root -proot student_results < backup.sql
```

### Backup Redis Data
```bash
# Backup Redis data
docker exec redis-cache redis-cli SAVE
docker cp redis-cache:/data/dump.rdb ./redis-backup.rdb

# Restore Redis data
docker cp ./redis-backup.rdb redis-cache:/data/dump.rdb
docker exec redis-cache redis-cli SHUTDOWN
docker-compose -f docker-compose.prod.yml restart redis-cache
```

---

## Security Considerations

### Production Recommendations
1. Change default MySQL password
2. Enable Redis authentication
3. Use environment variable files (.env) for sensitive data
4. Use Docker secrets for production
5. Enable SSL/TLS for database connections
6. Implement network policies
7. Regular security updates

### Environment Variables File
Create `.env` file:
```env
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_PASSWORD=your_secure_password
SPRING_DATASOURCE_PASSWORD=your_secure_password
```

Update docker-compose files to use environment variables:
```yaml
environment:
  MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
  MYSQL_PASSWORD: ${MYSQL_PASSWORD}
```

---

## Performance Optimization

### MySQL Optimization
- Increase `innodb_buffer_pool_size`
- Enable query cache
- Optimize indexes

### Redis Optimization
- Increase `maxmemory`
- Configure eviction policy
- Enable persistence

### Spring Boot Optimization
- Increase JVM heap size
- Configure connection pools
- Enable compression

---

## Troubleshooting

See `DOCKER_TROUBLESHOOTING_GUIDE.md` for detailed troubleshooting steps.

---

## Summary

### Single Instance Deployment
- **Command**: `docker-compose up --build -d`
- **Ports**: 8080 (Spring Boot), 3306 (MySQL), 6379 (Redis)
- **Use Case**: Development and testing

### Multi-Instance Deployment
- **Command**: `docker-compose -f docker-compose.prod.yml up --build -d`
- **Ports**: 8081, 8082, 8083 (Spring Boot), 3306 (MySQL), 6379 (Redis)
- **Use Case**: Production with load balancing

### Key Features
- Automatic health checks
- Persistent volumes
- Shared database and cache
- Graceful degradation
- Automatic restart on failure
- Environment variable configuration
- Docker networking for service communication

### Verification URLs
- Swagger UI (Instance 1): `http://localhost:8081/swagger-ui/index.html`
- Swagger UI (Instance 2): `http://localhost:8082/swagger-ui/index.html`
- Swagger UI (Instance 3): `http://localhost:8083/swagger-ui/index.html`
- Health Check (Instance 1): `http://localhost:8081/actuator/health`
- Health Check (Instance 2): `http://localhost:8082/actuator/health`
- Health Check (Instance 3): `http://localhost:8083/actuator/health`
