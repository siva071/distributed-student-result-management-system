# Multi-Instance Deployment Guide
## Distributed Student Result Management System

This guide provides comprehensive instructions for deploying the Student Result Management System as a distributed system with three independent Spring Boot instances sharing the same MySQL database and Redis cache.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Quick Start](#quick-start)
4. [Service Configuration](#service-configuration)
5. [Environment Variables](#environment-variables)
6. [Docker Networking](#docker-networking)
7. [Health Checks](#health-checks)
8. [Verification Steps](#verification-steps)
9. [Scaling and Load Balancing](#scaling-and-load-balancing)
10. [Troubleshooting](#troubleshooting)
11. [Monitoring and Maintenance](#monitoring-and-maintenance)

---

## Architecture Overview

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Network                           │
│              student-result-network                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ student-app-1│  │ student-app-2│  │ student-app-3│     │
│  │   Port 8081  │  │   Port 8082  │  │   Port 8083  │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                  │                  │             │
│         └──────────────────┼──────────────────┘             │
│                            │                                │
│         ┌──────────────────┼──────────────────┐             │
│         │                  │                  │             │
│  ┌──────▼──────┐    ┌──────▼──────┐    ┌──────▼──────┐    │
│  │  mysql-db   │    │ redis-cache │    │  (Shared)   │    │
│  │  Port 3306  │    │  Port 6379  │    │   Data      │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Key Features

- **Shared Database**: All three instances connect to the same MySQL database
- **Shared Cache**: All three instances share the same Redis cache
- **Independent Scaling**: Each instance can be scaled independently
- **Load Distribution**: Requests can be distributed across instances
- **High Availability**: If one instance fails, others continue serving
- **Health Monitoring**: All services have health checks configured

---

## Prerequisites

### Software Requirements

- **Docker Desktop**: Version 20.10+ (Windows/Mac/Linux)
- **Docker Compose**: Version 2.0+
- **Java**: 21 (for local development)
- **Maven**: 3.9+ (for local development)
- **Git**: For cloning the repository

### System Requirements

- **RAM**: Minimum 4GB (8GB recommended)
- **Disk Space**: Minimum 10GB free space
- **Network**: Internet connection for pulling Docker images

### Verify Docker Installation

```bash
# Check Docker version
docker --version

# Check Docker Compose version
docker-compose --version

# Check Docker is running
docker info
```

---

## Quick Start

### Step 1: Navigate to Project Directory

```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
```

### Step 2: Build and Start All Services

```bash
docker-compose up --build -d
```

This command:
- Builds the Docker image for the Spring Boot application
- Starts MySQL database
- Starts Redis cache
- Starts three Spring Boot instances (student-app-1, student-app-2, student-app-3)
- Runs all containers in detached mode (background)

### Step 3: Verify Services are Running

```bash
# Check all containers
docker-compose ps

# Expected output:
# NAME              STATUS
# mysql-db          Up (healthy)
# redis-cache       Up (healthy)
# student-app-1     Up (healthy)
# student-app-2     Up (healthy)
# student-app-3     Up (healthy)
```

### Step 4: Access the Applications

- **Instance 1**: http://localhost:8081/swagger-ui/index.html
- **Instance 2**: http://localhost:8082/swagger-ui/index.html
- **Instance 3**: http://localhost:8083/swagger-ui/index.html

---

## Service Configuration

### MySQL Database Service

```yaml
mysql-db:
  image: mysql:8.0
  container_name: mysql-db
  ports:
    - "3306:3306"
  environment:
    MYSQL_ROOT_PASSWORD: root
    MYSQL_DATABASE: student_results
    MYSQL_USER: root
    MYSQL_PASSWORD: root
  volumes:
    - mysql-data:/var/lib/mysql
  networks:
    - student-result-network
  healthcheck:
    test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-proot"]
    interval: 10s
    timeout: 5s
    retries: 5
    start_period: 30s
  restart: unless-stopped
```

**Configuration Details**:
- **Image**: MySQL 8.0
- **Port**: 3306 (host) → 3306 (container)
- **Database**: student_results
- **Credentials**: root/root
- **Volume**: mysql-data (persistent storage)
- **Health Check**: MySQL ping every 10 seconds

### Redis Cache Service

```yaml
redis-cache:
  image: redis:latest
  container_name: redis-cache
  ports:
    - "6379:6379"
  volumes:
    - redis-data:/data
  networks:
    - student-result-network
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5
    start_period: 10s
  restart: unless-stopped
```

**Configuration Details**:
- **Image**: Redis latest
- **Port**: 6379 (host) → 6379 (container)
- **Volume**: redis-data (persistent storage)
- **Health Check**: Redis ping every 10 seconds

### Spring Boot Application Instances

All three instances use the same configuration with different ports:

```yaml
student-app-1:
  build:
    context: .
    dockerfile: Dockerfile
  container_name: student-app-1
  ports:
    - "8081:8081"
  environment:
    SERVER_PORT: 8081
    SPRING_DATASOURCE_URL: jdbc:mysql://mysql-db:3306/student_results
    SPRING_DATASOURCE_USERNAME: root
    SPRING_DATASOURCE_PASSWORD: root
    SPRING_DATA_REDIS_HOST: redis-cache
    SPRING_DATA_REDIS_PORT: 6379
  depends_on:
    mysql-db:
      condition: service_healthy
    redis-cache:
      condition: service_healthy
  networks:
    - student-result-network
  restart: unless-stopped
  healthcheck:
    test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8081/actuator/health"]
    interval: 30s
    timeout: 10s
    retries: 3
    start_period: 60s
```

**Configuration Details**:
- **Build**: Multi-stage Dockerfile
- **Ports**: 8081, 8082, 8083
- **Database**: mysql-db (Docker service name)
- **Cache**: redis-cache (Docker service name)
- **Health Check**: Actuator endpoint every 30 seconds
- **Depends On**: MySQL and Redis must be healthy first

---

## Environment Variables

### Spring Boot Application Variables

| Variable | Description | Default Value | Docker Value |
|----------|-------------|---------------|--------------|
| `SERVER_PORT` | Application port | 8080 | 8081, 8082, 8083 |
| `SPRING_DATASOURCE_URL` | JDBC connection string | jdbc:mysql://localhost:3306/student_results | jdbc:mysql://mysql-db:3306/student_results |
| `SPRING_DATASOURCE_USERNAME` | Database username | root | root |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Shankii9900# | root |
| `SPRING_DATA_REDIS_HOST` | Redis host | localhost | redis-cache |
| `SPRING_DATA_REDIS_PORT` | Redis port | 6379 | 6379 |

### MySQL Environment Variables

| Variable | Description | Value |
|----------|-------------|-------|
| `MYSQL_ROOT_PASSWORD` | Root password | root |
| `MYSQL_DATABASE` | Database name | student_results |
| `MYSQL_USER` | Database user | root |
| `MYSQL_PASSWORD` | Database password | root |

---

## Docker Networking

### Network Configuration

```yaml
networks:
  student-result-network:
    driver: bridge
```

### Network Details

- **Network Name**: student-result-network
- **Driver**: bridge (default Docker network driver)
- **Scope**: Local (single host)
- **Services**: mysql-db, redis-cache, student-app-1, student-app-2, student-app-3

### Service Communication

All services communicate using Docker service names instead of localhost:

- **MySQL**: `mysql-db:3306`
- **Redis**: `redis-cache:6379`
- **Spring Boot Apps**: `student-app-1:8081`, `student-app-2:8082`, `student-app-3:8083`

### Network Benefits

- **Service Discovery**: Services can find each other by name
- **Isolation**: Network is isolated from other Docker networks
- **Security**: Only containers in the network can communicate
- **Simplicity**: No need to manage IP addresses

---

## Health Checks

### MySQL Health Check

```yaml
healthcheck:
  test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-proot"]
  interval: 10s
  timeout: 5s
  retries: 5
  start_period: 30s
```

**Behavior**:
- Checks MySQL availability every 10 seconds
- Allows 30 seconds for MySQL to start
- Retries 5 times before marking as unhealthy
- Timeout after 5 seconds per check

### Redis Health Check

```yaml
healthcheck:
  test: ["CMD", "redis-cli", "ping"]
  interval: 10s
  timeout: 5s
  retries: 5
  start_period: 10s
```

**Behavior**:
- Checks Redis availability every 10 seconds
- Allows 10 seconds for Redis to start
- Retries 5 times before marking as unhealthy
- Timeout after 5 seconds per check

### Spring Boot Health Check

```yaml
healthcheck:
  test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8081/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

**Behavior**:
- Checks Actuator health endpoint every 30 seconds
- Allows 60 seconds for application to start
- Retries 3 times before marking as unhealthy
- Timeout after 10 seconds per check

### Health Check Endpoints

- **MySQL**: Internal (mysqladmin ping)
- **Redis**: Internal (redis-cli ping)
- **Spring Boot**: http://localhost:PORT/actuator/health

---

## Verification Steps

### Step 1: Check Container Status

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

### Step 2: Check Container Logs

```bash
# Check all logs
docker-compose logs

# Check specific service logs
docker-compose logs student-app-1
docker-compose logs student-app-2
docker-compose logs student-app-3
docker-compose logs mysql-db
docker-compose logs redis-cache

# Follow logs in real-time
docker-compose logs -f student-app-1
```

### Step 3: Verify Health Endpoints

```bash
# Check Instance 1 health
curl http://localhost:8081/actuator/health

# Check Instance 2 health
curl http://localhost:8082/actuator/health

# Check Instance 3 health
curl http://localhost:8083/actuator/health
```

**Expected Response**:
```json
{
  "status": "UP"
}
```

### Step 4: Verify Swagger UI

Open the following URLs in your browser:

- **Instance 1**: http://localhost:8081/swagger-ui/index.html
- **Instance 2**: http://localhost:8082/swagger-ui/index.html
- **Instance 3**: http://localhost:8083/swagger-ui/index.html

All three should display the Swagger UI interface.

### Step 5: Test API Endpoints

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

### Step 6: Verify Database Connection

```bash
# Connect to MySQL container
docker exec -it mysql-db mysql -u root -proot

# In MySQL prompt
USE student_results;
SHOW TABLES;
SELECT * FROM students LIMIT 5;
EXIT;
```

### Step 7: Verify Redis Connection

```bash
# Connect to Redis container
docker exec -it redis-cache redis-cli

# In Redis prompt
PING
KEYS student_result_*
GET student_result_student_1
EXIT;
```

### Step 8: Test Cache Sharing

```bash
# Create a student via Instance 1
curl -X POST http://localhost:8081/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "hallTicketNo": "CACHE001",
    "fullName": "Cache Test",
    "gender": "MALE",
    "dateOfBirth": "2000-01-01",
    "email": "cache@example.com",
    "phone": "1234567890",
    "yearOfStudy": 1,
    "semester": 1,
    "section": "A",
    "branch": "CSE"
  }'

# Retrieve via Instance 2 (should hit cache)
curl http://localhost:8082/api/students/1

# Check Redis cache
docker exec -it redis-cache redis-cli
KEYS student_result_*
```

---

## Scaling and Load Balancing

### Horizontal Scaling

To add more instances, add them to docker-compose.yml:

```yaml
student-app-4:
  build:
    context: .
    dockerfile: Dockerfile
  container_name: student-app-4
  ports:
    - "8084:8084"
  environment:
    SERVER_PORT: 8084
    SPRING_DATASOURCE_URL: jdbc:mysql://mysql-db:3306/student_results
    SPRING_DATASOURCE_USERNAME: root
    SPRING_DATASOURCE_PASSWORD: root
    SPRING_DATA_REDIS_HOST: redis-cache
    SPRING_DATA_REDIS_PORT: 6379
  depends_on:
    mysql-db:
      condition: service_healthy
    redis-cache:
      condition: service_healthy
  networks:
    - student-result-network
  restart: unless-stopped
  healthcheck:
    test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8084/actuator/health"]
    interval: 30s
    timeout: 10s
    retries: 3
    start_period: 60s
```

### Load Balancing Options

#### Option 1: Nginx Load Balancer

Create `nginx.conf`:

```nginx
upstream student_backend {
    server student-app-1:8081;
    server student-app-2:8082;
    server student-app-3:8083;
}

server {
    listen 80;
    
    location / {
        proxy_pass http://student_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

Add to docker-compose.yml:

```yaml
nginx:
  image: nginx:latest
  ports:
    - "80:80"
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf
  depends_on:
    - student-app-1
    - student-app-2
    - student-app-3
  networks:
    - student-result-network
```

#### Option 2: HAProxy Load Balancer

Create `haproxy.cfg`:

```
defaults
    mode http
    timeout connect 5000ms
    timeout client 50000ms
    timeout server 50000ms

frontend student_frontend
    bind *:80
    default_backend student_backend

backend student_backend
    balance roundrobin
    server app1 student-app-1:8081 check
    server app2 student-app-2:8082 check
    server app3 student-app-3:8083 check
```

#### Option 3: DNS Round Robin

Configure DNS to return multiple IP addresses for the same hostname.

---

## Troubleshooting

### Common Issues and Solutions

#### Issue 1: Container Won't Start

**Symptoms**:
- Container status shows "Exited"
- Docker logs show connection errors

**Solutions**:
```bash
# Check logs
docker-compose logs <service-name>

# Restart specific service
docker-compose restart <service-name>

# Rebuild and start
docker-compose up --build -d <service-name>
```

#### Issue 2: MySQL Connection Refused

**Symptoms**:
- Spring Boot app can't connect to MySQL
- Logs show "Connection refused"

**Solutions**:
```bash
# Check MySQL is healthy
docker-compose ps mysql-db

# Check MySQL logs
docker-compose logs mysql-db

# Verify MySQL is running
docker exec -it mysql-db mysql -u root -proot -e "SELECT 1"

# Wait for MySQL to be healthy before starting apps
docker-compose up -d mysql-db redis-cache
# Wait 30 seconds
docker-compose up -d student-app-1 student-app-2 student-app-3
```

#### Issue 3: Redis Connection Failed

**Symptoms**:
- Spring Boot app can't connect to Redis
- Logs show "Connection refused" or "Unable to connect to Redis"

**Solutions**:
```bash
# Check Redis is healthy
docker-compose ps redis-cache

# Check Redis logs
docker-compose logs redis-cache

# Verify Redis is running
docker exec -it redis-cache redis-cli ping

# Test Redis connection from app container
docker exec -it student-app-1 wget -O- http://redis-cache:6379
```

#### Issue 4: Port Already in Use

**Symptoms**:
- Error: "port is already allocated"
- Container won't start

**Solutions**:
```bash
# Find what's using the port
netstat -ano | findstr :8081

# Kill the process using the port
taskkill /PID <PID> /F

# Or change the port mapping in docker-compose.yml
ports:
  - "8081:8081"  # Change to "8081:8081" or different port
```

#### Issue 5: Health Check Failing

**Symptoms**:
- Container status shows "unhealthy"
- Service keeps restarting

**Solutions**:
```bash
# Check health status
docker inspect --format='{{.State.Health.Status}}' <container-name>

# Manually test health endpoint
docker exec -it student-app-1 wget -O- http://localhost:8081/actuator/health

# Increase health check timeout in docker-compose.yml
healthcheck:
  timeout: 20s  # Increase from 10s
  retries: 5    # Increase from 3
```

#### Issue 6: Out of Memory

**Symptoms**:
- Container exits with code 137
- Logs show "OOMKilled"

**Solutions**:
```bash
# Check Docker memory limits
docker system df

# Increase Docker memory allocation in Docker Desktop settings
# Settings → Resources → Memory → Increase to 8GB+

# Add memory limits to docker-compose.yml
deploy:
  resources:
    limits:
      memory: 1G
```

#### Issue 7: Docker Hub Pull Timeout

**Symptoms**:
- Error: "TLS handshake timeout"
- Can't pull MySQL or Redis images

**Solutions**:
```bash
# Configure Docker registry mirror
# In Docker Desktop: Settings → Docker Engine
# Add:
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}

# Or pull images manually first
docker pull mysql:8.0
docker pull redis:latest
```

---

## Monitoring and Maintenance

### Monitoring Commands

```bash
# Real-time container monitoring
docker stats

# Check container resource usage
docker stats student-app-1 student-app-2 student-app-3

# View container events
docker events

# Monitor disk usage
docker system df
```

### Log Management

```bash
# View logs with timestamps
docker-compose logs -t

# View last 100 lines
docker-compose logs --tail=100

# View logs since specific time
docker-compose logs --since 2024-01-01T00:00:00

# Export logs to file
docker-compose logs > deployment.log
```

### Database Backup

```bash
# Backup MySQL database
docker exec mysql-db mysqldump -u root -proot student_results > backup.sql

# Restore MySQL database
docker exec -i mysql-db mysql -u root -proot student_results < backup.sql

# Schedule automated backups (cron)
# Add to crontab:
0 2 * * * docker exec mysql-db mysqldump -u root -proot student_results > /backup/student_results_$(date +\%Y\%m\%d).sql
```

### Redis Backup

```bash
# Backup Redis data
docker exec redis-cache redis-cli BGSAVE

# Copy Redis data directory
docker cp redis-cache:/data ./redis-backup

# Restore Redis data
docker cp ./redis-backup redis-cache:/data
```

### Container Cleanup

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes data)
docker-compose down -v

# Remove unused images
docker image prune -a

# Remove unused containers
docker container prune

# Remove unused volumes
docker volume prune

# Complete cleanup
docker system prune -a
```

### Updating the Application

```bash
# Pull latest code
git pull

# Rebuild and restart
docker-compose up --build -d

# Restart specific service
docker-compose up --build -d student-app-1
```

---

## Production Best Practices

### Security

1. **Change Default Passwords**: Update MySQL and Redis passwords
2. **Use Secrets Management**: Use Docker secrets or environment files
3. **Network Isolation**: Use custom networks for isolation
4. **TLS/SSL**: Enable HTTPS for production
5. **Firewall Rules**: Restrict port access

### Performance

1. **Connection Pooling**: Configure database connection pool
2. **Cache Tuning**: Optimize Redis cache settings
3. **Resource Limits**: Set CPU and memory limits
4. **Load Testing**: Test with realistic load
5. **Monitoring**: Use monitoring tools (Prometheus, Grafana)

### High Availability

1. **Database Replication**: Use MySQL master-slave replication
2. **Redis Cluster**: Use Redis Sentinel or Cluster
3. **Load Balancer**: Use HAProxy or Nginx
4. **Health Checks**: Configure proper health checks
5. **Auto-scaling**: Use Kubernetes for auto-scaling

### Backup Strategy

1. **Regular Backups**: Schedule daily database backups
2. **Off-site Storage**: Store backups in cloud storage
3. **Backup Testing**: Test restore procedures regularly
4. **Retention Policy**: Keep backups for 30 days
5. **Disaster Recovery**: Have a disaster recovery plan

---

## Summary

This deployment guide provides a complete solution for running the Student Result Management System as a distributed system with three independent Spring Boot instances. All instances share the same MySQL database and Redis cache, ensuring data consistency and cache coherence across the system.

### Key Achievements

- ✅ Multi-instance deployment (3 Spring Boot apps)
- ✅ Shared MySQL database
- ✅ Shared Redis cache
- ✅ Docker networking configuration
- ✅ Health checks for all services
- ✅ Environment variable configuration
- ✅ Port mapping (8081, 8082, 8083)
- ✅ Comprehensive documentation
- ✅ Troubleshooting guide
- ✅ Monitoring and maintenance procedures

### Next Steps

1. Deploy using the provided docker-compose.yml
2. Verify all instances are running correctly
3. Test API endpoints on all instances
4. Configure load balancer for production
5. Set up monitoring and alerting
6. Implement backup strategy
7. Plan for high availability

For additional support or questions, refer to the troubleshooting section or check the application logs.
