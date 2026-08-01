# Docker Troubleshooting Guide

## Overview
This guide provides solutions for common issues encountered when deploying the Distributed Student Result Management System with Docker Compose.

---

## Table of Contents
1. [Build Issues](#build-issues)
2. [Startup Issues](#startup-issues)
3. [Network Issues](#network-issues)
4. [Database Issues](#database-issues)
5. [Redis Issues](#redis-issues)
6. [Application Issues](#application-issues)
7. [Performance Issues](#performance-issues)
8. [Port Conflicts](#port-conflicts)
9. [Volume Issues](#volume-issues)
10. [Health Check Issues](#health-check-issues)

---

## Build Issues

### Issue: Maven Build Fails in Docker

**Symptoms:**
- Docker build fails during Maven compilation
- Error: "Could not resolve dependencies"
- Build hangs indefinitely

**Solutions:**

1. **Check Network Connectivity**
```bash
# Test internet connectivity from Docker
docker run --rm alpine ping -c 3 google.com
```

2. **Use Maven Local Repository**
```dockerfile
# Add to Dockerfile build stage
VOLUME /root/.m2
```

3. **Increase Maven Memory**
```dockerfile
# Update Dockerfile build stage
RUN mvn clean package -DskipTests -B -Dmaven.compiler.fork=false
```

4. **Clear Docker Build Cache**
```bash
docker-compose build --no-cache
docker system prune -a
```

5. **Check Maven Dependencies**
```bash
# Verify pom.xml is correct
mvn dependency:tree
```

---

### Issue: Docker Build Context Too Large

**Symptoms:**
- Build takes very long time
- "Build context exceeds Docker daemon limits" error

**Solutions:**

1. **Verify .dockerignore**
```bash
# Check .dockerignore exists
cat .dockerignore
```

2. **Add More Exclusions to .dockerignore**
```
# Add to .dockerignore
*.md
docs/
.git/
.gitignore
target/
```

3. **Use .dockerignore Effectively**
```
# Exclude everything, include only necessary
*
!pom.xml
!src/
```

---

## Startup Issues

### Issue: Container Fails to Start

**Symptoms:**
- Container status: "Exited"
- Container restarts repeatedly
- No logs visible

**Solutions:**

1. **Check Container Logs**
```bash
docker-compose logs student-app-1
docker logs student-app-1
```

2. **Inspect Container Exit Code**
```bash
docker inspect student-app-1 --format='{{.State.ExitCode}}'
```

3. **Run Container in Foreground**
```bash
docker-compose up student-app-1
```

4. **Check Resource Limits**
```bash
docker stats
```

5. **Verify Docker Daemon is Running**
```bash
docker info
```

---

### Issue: Spring Boot Application Won't Start

**Symptoms:**
- Application logs show startup errors
- Health check fails
- Port not accessible

**Solutions:**

1. **Check Application Logs**
```bash
docker-compose logs -f student-app-1
```

2. **Verify Environment Variables**
```bash
docker exec student-app-1 env | grep SPRING
```

3. **Check Database Connection**
```bash
# Test MySQL connection from container
docker exec student-app-1 ping mysql-db
docker exec student-app-1 telnet mysql-db 3306
```

4. **Check Redis Connection**
```bash
# Test Redis connection from container
docker exec student-app-1 ping redis-cache
docker exec student-app-1 telnet redis-cache 6379
```

5. **Verify Port Configuration**
```bash
# Check if port is already in use
netstat -ano | findstr :8081
```

6. **Increase Startup Timeout**
```yaml
# Update docker-compose.yml
healthcheck:
  start_period: 120s  # Increase from 60s
```

---

### Issue: MySQL Container Won't Start

**Symptoms:**
- MySQL container exits immediately
- "Can't connect to MySQL server" errors
- Database not accessible

**Solutions:**

1. **Check MySQL Logs**
```bash
docker-compose logs mysql-db
```

2. **Remove Old Volume and Restart**
```bash
docker-compose down -v
docker-compose up -d mysql-db
```

3. **Check MySQL Initialization**
```bash
# Wait for MySQL to fully initialize
docker exec mysql-db mysqladmin ping -h localhost -u root -proot
```

4. **Verify MySQL Configuration**
```yaml
# Check docker-compose.yml
environment:
  MYSQL_ROOT_PASSWORD: root
  MYSQL_DATABASE: student_results
```

5. **Check Disk Space**
```bash
docker system df
```

---

### Issue: Redis Container Won't Start

**Symptoms:**
- Redis container exits immediately
- "Connection refused" errors
- Cache not accessible

**Solutions:**

1. **Check Redis Logs**
```bash
docker-compose logs redis-cache
```

2. **Remove Old Volume and Restart**
```bash
docker-compose down -v
docker-compose up -d redis-cache
```

3. **Test Redis Connection**
```bash
docker exec redis-cache redis-cli ping
```

4. **Check Redis Configuration**
```yaml
# Verify port mapping
ports:
  - "6379:6379"
```

---

## Network Issues

### Issue: Containers Can't Communicate

**Symptoms:**
- "Connection refused" between containers
- "Unknown host" errors
- Services can't reach each other

**Solutions:**

1. **Verify Network Configuration**
```bash
docker network ls
docker network inspect student-result-network
```

2. **Check Container Network Mode**
```bash
docker inspect student-app-1 --format='{{.HostConfig.NetworkMode}}'
```

3. **Test Connectivity Between Containers**
```bash
# From student-app-1
docker exec student-app-1 ping mysql-db
docker exec student-app-1 ping redis-cache
```

4. **Recreate Network**
```bash
docker-compose down
docker network prune
docker-compose up -d
```

5. **Verify Service Names Match**
```yaml
# Ensure service names match environment variables
services:
  mysql-db:  # Used as hostname
    container_name: mysql-db
  student-app-1:
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-db:3306/student_results
```

---

### Issue: Application Can't Reach MySQL from Docker

**Symptoms:**
- "Communications link failure"
- "Could not create connection to database server"
- Connection timeout

**Solutions:**

1. **Verify MySQL Hostname**
```bash
# Should be mysql-db, not localhost
docker exec student-app-1 env | grep SPRING_DATASOURCE_URL
```

2. **Check MySQL is Healthy**
```bash
docker-compose ps
docker exec mysql-db mysqladmin ping -h localhost -u root -proot
```

3. **Test Connection from Container**
```bash
docker exec student-app-1 telnet mysql-db 3306
```

4. **Verify Network Connectivity**
```bash
docker network inspect student-result-network
```

5. **Check MySQL Port**
```yaml
# Ensure port is correct
ports:
  - "3306:3306"
```

---

### Issue: Application Can't Reach Redis from Docker

**Symptoms:**
- "Unable to connect to Redis"
- "Connection refused"
- Cache not working

**Solutions:**

1. **Verify Redis Hostname**
```bash
# Should be redis-cache, not localhost
docker exec student-app-1 env | grep SPRING_DATA_REDIS_HOST
```

2. **Check Redis is Healthy**
```bash
docker-compose ps
docker exec redis-cache redis-cli ping
```

3. **Test Connection from Container**
```bash
docker exec student-app-1 telnet redis-cache 6379
```

4. **Verify Network Connectivity**
```bash
docker network inspect student-result-network
```

5. **Check Redis Port**
```yaml
# Ensure port is correct
ports:
  - "6379:6379"
```

---

## Database Issues

### Issue: Database Schema Not Created

**Symptoms:**
- Tables not found
- "Table doesn't exist" errors
- Application fails to start

**Solutions:**

1. **Verify DDL Auto Configuration**
```properties
# Check application.properties
spring.jpa.hibernate.ddl-auto=update
```

2. **Check Database Connection**
```bash
docker exec mysql-db mysql -u root -proot -e "SHOW DATABASES;"
```

3. **Verify Database Exists**
```bash
docker exec mysql-db mysql -u root -proot -e "USE student_results; SHOW TABLES;"
```

4. **Check Hibernate Logs**
```bash
docker-compose logs student-app-1 | grep -i hibernate
```

5. **Manually Create Database**
```bash
docker exec mysql-db mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS student_results;"
```

---

### Issue: Connection Pool Exhausted

**Symptoms:**
- "Connection pool exhausted"
- "Timeout waiting for idle object"
- Slow response times

**Solutions:**

1. **Increase Connection Pool Size**
```properties
# Add to application.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

2. **Check Active Connections**
```bash
docker exec mysql-db mysql -u root -proot -e "SHOW PROCESSLIST;"
```

3. **Reduce Connection Timeout**
```properties
spring.datasource.hikari.connection-timeout=30000
```

4. **Check for Connection Leaks**
```bash
# Review application logs for connection issues
docker-compose logs student-app-1 | grep -i connection
```

---

### Issue: Database Lock Timeout

**Symptoms:**
- "Lock wait timeout exceeded"
- Transactions failing
- Deadlock errors

**Solutions:**

1. **Check for Long-Running Transactions**
```bash
docker exec mysql-db mysql -u root -proot -e "SHOW ENGINE INNODB STATUS\G"
```

2. **Increase Lock Timeout**
```properties
# Add to application.properties
spring.jpa.properties.hibernate.jdbc.batch_size=20
```

3. **Kill Blocking Transactions**
```bash
docker exec mysql-db mysql -u root -proot -e "KILL <process_id>;"
```

4. **Optimize Queries**
```bash
# Review slow query log
docker exec mysql-db mysql -u root -proot -e "SHOW VARIABLES LIKE 'slow_query_log';"
```

---

## Redis Issues

### Issue: Redis Connection Refused

**Symptoms:**
- "Connection refused" errors
- Cache not working
- Application falls back to MySQL

**Solutions:**

1. **Verify Redis is Running**
```bash
docker-compose ps redis-cache
docker exec redis-cache redis-cli ping
```

2. **Check Redis Logs**
```bash
docker-compose logs redis-cache
```

3. **Restart Redis**
```bash
docker-compose restart redis-cache
```

4. **Verify Port Accessibility**
```bash
docker exec student-app-1 telnet redis-cache 6379
```

5. **Check Redis Configuration**
```properties
# Verify application.properties
spring.data.redis.host=redis-cache
spring.data.redis.port=6379
```

---

### Issue: Redis Out of Memory

**Symptoms:**
- "OOM command not allowed"
- Cache eviction errors
- Performance degradation

**Solutions:**

1. **Check Redis Memory Usage**
```bash
docker exec redis-cache redis-cli INFO memory
```

2. **Increase Redis Memory Limit**
```yaml
# Add to docker-compose.yml
redis-cache:
  command: redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru
```

3. **Configure Eviction Policy**
```yaml
redis-cache:
  command: redis-server --maxmemory-policy volatile-lru
```

4. **Monitor Memory Usage**
```bash
docker stats redis-cache
```

---

### Issue: Cache Not Working

**Symptoms:**
- No cache hit logs
- Always cache miss
- No performance improvement

**Solutions:**

1. **Verify Caching is Enabled**
```bash
# Check @EnableCaching annotation
# Check RedisConfig class
```

2. **Check Cache Annotations**
```bash
# Verify @Cacheable annotations are present
# Verify cache keys are correct
```

3. **Test Redis Connection**
```bash
docker exec redis-cache redis-cli KEYS student_result_*
```

4. **Check Application Logs**
```bash
docker-compose logs student-app-1 | grep -i cache
```

5. **Verify Cache Configuration**
```properties
# Check application.properties
spring.cache.type=redis
spring.cache.redis.time-to-live=600000
```

---

## Application Issues

### Issue: Health Check Fails

**Symptoms:**
- Container marked as unhealthy
- Health check returns error
- Container restarts repeatedly

**Solutions:**

1. **Check Health Check Configuration**
```yaml
# Verify docker-compose.yml
healthcheck:
  test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8081/actuator/health"]
```

2. **Test Health Endpoint Manually**
```bash
curl http://localhost:8081/actuator/health
```

3. **Check if wget is Installed**
```bash
docker exec student-app-1 which wget
```

4. **Increase Health Check Timeout**
```yaml
healthcheck:
  timeout: 10s  # Increase from 3s
```

5. **Disable Health Check Temporarily**
```yaml
healthcheck:
  disable: true
```

---

### Issue: Swagger UI Not Accessible

**Symptoms:**
- Swagger UI returns 404
- Swagger UI shows errors
- API docs not loading

**Solutions:**

1. **Verify Swagger Configuration**
```properties
# Check application.properties
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
```

2. **Check Application is Running**
```bash
curl http://localhost:8081/actuator/health
```

3. **Verify Port Mapping**
```yaml
# Check docker-compose.yml
ports:
  - "8081:8081"
```

4. **Check Browser Console**
- Open browser developer tools
- Check for JavaScript errors
- Check network requests

5. **Test API Endpoint Directly**
```bash
curl http://localhost:8081/api-docs
```

---

### Issue: Application Runs Slowly

**Symptoms:**
- Slow response times
- High CPU usage
- Memory leaks

**Solutions:**

1. **Check Resource Usage**
```bash
docker stats student-app-1
```

2. **Increase JVM Memory**
```yaml
# Add to docker-compose.yml
environment:
  JAVA_OPTS: "-Xmx512m -Xms256m"
```

3. **Check Database Performance**
```bash
docker exec mysql-db mysql -u root -proot -e "SHOW PROCESSLIST;"
```

4. **Enable Connection Pooling**
```properties
# Add to application.properties
spring.datasource.hikari.maximum-pool-size=20
```

5. **Review Application Logs**
```bash
docker-compose logs student-app-1 | grep -i error
```

---

## Performance Issues

### Issue: High Memory Usage

**Symptoms:**
- Container OOM killed
- System becomes slow
- Docker daemon crashes

**Solutions:**

1. **Check Memory Usage**
```bash
docker stats --no-stream
```

2. **Limit Container Memory**
```yaml
# Add to docker-compose.yml
deploy:
  resources:
    limits:
      memory: 1G
```

3. **Increase JVM Heap**
```yaml
environment:
  JAVA_OPTS: "-Xmx512m -Xms256m"
```

4. **Check for Memory Leaks**
```bash
# Use JVM profiling tools
docker exec student-app-1 jmap -heap 1
```

5. **Reduce Connection Pool Size**
```properties
spring.datasource.hikari.maximum-pool-size=10
```

---

### Issue: High CPU Usage

**Symptoms:**
- Container uses 100% CPU
- System becomes unresponsive
- Other containers slow down

**Solutions:**

1. **Check CPU Usage**
```bash
docker stats --no-stream
```

2. **Limit Container CPU**
```yaml
# Add to docker-compose.yml
deploy:
  resources:
    limits:
      cpus: '0.5'
```

3. **Check for Infinite Loops**
```bash
# Review application logs
docker-compose logs student-app-1
```

4. **Optimize Database Queries**
```bash
# Review slow query log
docker exec mysql-db mysql -u root -proot -e "SHOW PROCESSLIST;"
```

---

## Port Conflicts

### Issue: Port Already in Use

**Symptoms:**
- "Port is already allocated"
- Container fails to start
- Service not accessible

**Solutions:**

1. **Check Port Usage**
```bash
netstat -ano | findstr :8081
```

2. **Kill Process Using Port**
```bash
# Windows
taskkill /PID <pid> /F

# Linux/Mac
kill -9 <pid>
```

3. **Change Port Mapping**
```yaml
# Update docker-compose.yml
ports:
  - "8084:8081"  # Use different host port
```

4. **Stop Conflicting Service**
```bash
# Stop other service using the port
```

5. **Use Different Ports for Instances**
```yaml
student-app-1:
  ports:
    - "8081:8081"
student-app-2:
  ports:
    - "8082:8082"
student-app-3:
  ports:
    - "8083:8083"
```

---

## Volume Issues

### Issue: Volume Not Persisting Data

**Symptoms:**
- Data lost after container restart
- Database empty after restart
- Cache empty after restart

**Solutions:**

1. **Verify Volume Exists**
```bash
docker volume ls
```

2. **Check Volume Mount**
```bash
docker inspect student-app-1 --format='{{.Mounts}}'
```

3. **Verify Volume Configuration**
```yaml
# Check docker-compose.yml
volumes:
  mysql-data:
    driver: local
```

4. **Check Volume Permissions**
```bash
docker exec mysql-db ls -la /var/lib/mysql
```

5. **Backup and Restore Volume**
```bash
# Backup
docker run --rm -v mysql-data:/data -v $(pwd):/backup alpine tar czf /backup/mysql-backup.tar.gz /data

# Restore
docker run --rm -v mysql-data:/data -v $(pwd):/backup alpine tar xzf /backup/mysql-backup.tar.gz -C /
```

---

### Issue: Volume Permission Denied

**Symptoms:**
- "Permission denied" errors
- Container can't write to volume
- Application fails to start

**Solutions:**

1. **Check Volume Permissions**
```bash
docker exec mysql-db ls -la /var/lib/mysql
```

2. **Fix Permissions**
```bash
docker exec mysql-db chown -R mysql:mysql /var/lib/mysql
```

3. **Run Container as Root (Not Recommended)**
```yaml
# Remove user directive from Dockerfile
# USER spring:spring  # Comment out
```

4. **Use Named User**
```yaml
# Add to docker-compose.yml
user: "1000:1000"
```

---

## Health Check Issues

### Issue: Health Check Always Fails

**Symptoms:**
- Container marked as unhealthy
- Health check returns error
- Container restarts repeatedly

**Solutions:**

1. **Test Health Endpoint**
```bash
curl http://localhost:8081/actuator/health
```

2. **Check Health Check Command**
```yaml
# Verify docker-compose.yml
healthcheck:
  test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8081/actuator/health"]
```

3. **Increase Start Period**
```yaml
healthcheck:
  start_period: 120s  # Increase from 60s
```

4. **Increase Timeout**
```yaml
healthcheck:
  timeout: 10s  # Increase from 3s
```

5. **Increase Retries**
```yaml
healthcheck:
  retries: 5  # Increase from 3
```

6. **Disable Health Check Temporarily**
```yaml
healthcheck:
  disable: true
```

---

## General Troubleshooting Commands

### View All Containers
```bash
docker ps -a
```

### View Container Logs
```bash
docker-compose logs -f
docker logs <container-name>
```

### Inspect Container
```bash
docker inspect <container-name>
```

### Execute Command in Container
```bash
docker exec -it <container-name> /bin/sh
docker exec <container-name> <command>
```

### Restart Container
```bash
docker-compose restart <service-name>
docker restart <container-name>
```

### Stop All Services
```bash
docker-compose down
```

### Remove All Containers and Volumes
```bash
docker-compose down -v
```

### Clean Docker System
```bash
docker system prune -a
```

### View Network Configuration
```bash
docker network ls
docker network inspect <network-name>
```

### View Volume Information
```bash
docker volume ls
docker volume inspect <volume-name>
```

### View Resource Usage
```bash
docker stats
```

---

## Getting Help

### Collect Diagnostic Information
```bash
# Save all logs
docker-compose logs > docker-compose.log

# Save container status
docker-compose ps > docker-compose-ps.log

# Save system information
docker info > docker-info.log

# Save network information
docker network inspect student-result-network > docker-network.log
```

### Check Documentation
- Docker Compose Documentation: https://docs.docker.com/compose/
- Spring Boot Docker Guide: https://spring.io/guides/topicals/spring-boot-docker/
- MySQL Docker Hub: https://hub.docker.com/_/mysql
- Redis Docker Hub: https://hub.docker.com/_/redis

---

## Summary

This troubleshooting guide covers the most common issues encountered when deploying the Distributed Student Result Management System with Docker Compose. For issues not covered here, please:

1. Check the application logs
2. Verify configuration files
3. Test individual components
4. Consult official documentation
5. Check Docker and Spring Boot community forums
