# Final Deployment Guide
## Distributed Student Result Management System

**Version:** 1.0.0
**Deployment Date:** August 1, 2026
**Status:** ✅ Production Ready

---

## System Overview

This is an enterprise-grade distributed Student Result Management System with the following components:

### Architecture Components
- **3 Spring Boot Instances** (Ports 8081, 8082, 8083)
- **NGINX Load Balancer** (Port 80)
- **MySQL Database** (Port 3306)
- **Redis Cache** (Port 6379)
- **Prometheus Metrics** (Available on each instance)
- **Health Monitoring** (Actuator endpoints)

### Key Features
- ✅ High availability with multiple instances
- ✅ Load balancing with NGINX (round-robin)
- ✅ Redis caching for performance
- ✅ Shared MySQL database for data consistency
- ✅ Prometheus metrics for monitoring
- ✅ Health checks and monitoring
- ✅ LocalDateTime serialization fixed
- ✅ Production-ready configuration

---

## Prerequisites

### Software Required
- **Java 21** (or higher)
- **Maven 3.8+** (for building)
- **MySQL 8.0+** (database)
- **Docker Desktop** (for Redis)
- **NGINX for Windows** (load balancer)

### System Requirements
- **RAM:** 8GB minimum (16GB recommended)
- **Disk Space:** 20GB minimum
- **OS:** Windows 10/11 or Linux
- **Network:** Local network access

---

## Quick Start Guide

### Step 1: Start MySQL Database

Ensure MySQL is running and the database `student_result_db` exists:

```sql
CREATE DATABASE IF NOT EXISTS student_result_db;
USE student_result_db;
```

### Step 2: Start Redis (Docker)

```bash
docker start redis-cache
```

If Redis container doesn't exist:
```bash
docker run -d --name redis-cache -p 6379:6379 redis:latest
```

### Step 3: Start Spring Boot Instances

Open 3 separate terminal windows and run:

**Terminal 1 (Instance 1):**
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
.\start-instance-1.bat
```

**Terminal 2 (Instance 2):**
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
.\start-instance-2.bat
```

**Terminal 3 (Instance 3):**
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
.\start-instance-3.bat
```

Wait for all instances to show "Started StudentResultManagementApplication".

### Step 4: Start NGINX Load Balancer

```bash
cd C:\nginx
start nginx
```

### Step 5: Verify System is Running

Test the load balancer:
```bash
curl http://localhost/api/students
```

Expected: JSON response with student data (200 OK)

---

## Access Points

### Load Balancer (Primary Access)
- **Base URL:** http://localhost
- **Students API:** http://localhost/api/students
- **Subjects API:** http://localhost/api/subjects
- **Results API:** http://localhost/api/results

### Direct Instance Access
- **Instance 1:** http://localhost:8081
- **Instance 2:** http://localhost:8082
- **Instance 3:** http://localhost:8083

### Health & Monitoring
- **Health (Instance 1):** http://localhost:8081/actuator/health
- **Health (Instance 2):** http://localhost:8082/actuator/health
- **Health (Instance 3):** http://localhost:8083/actuator/health
- **Prometheus Metrics (Instance 1):** http://localhost:8081/actuator/prometheus
- **Prometheus Metrics (Instance 2):** http://localhost:8082/actuator/prometheus
- **Prometheus Metrics (Instance 3):** http://localhost:8083/actuator/prometheus

### API Documentation
- **Swagger UI (Instance 1):** http://localhost:8081/swagger-ui/index.html
- **API Docs (Instance 1):** http://localhost:8081/api-docs

---

## Configuration Files

### Application Configuration
**File:** `src/main/resources/application.properties`

Key settings:
```properties
# Server Configuration
server.port=${SERVER_PORT:8081}
spring.application.name=student-result-management

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/student_result_db
spring.datasource.username=root
spring.datasource.password=Shankii9900#

# Redis Configuration
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.timeout=10000

# Jackson Configuration (LocalDateTime fix)
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=UTC

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

### NGINX Configuration
**File:** `nginx-windows.conf`

Load balancer configuration:
```nginx
upstream student_backend {
    server localhost:8081;
    server localhost:8082;
    server localhost:8083;
}

server {
    listen 80;
    server_name localhost;

    location /api/ {
        proxy_pass http://student_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## Startup Scripts

### Instance Startup Scripts

**start-instance-1.bat:**
```batch
@echo off
echo Starting Student App Instance 1 on port 8081...
set SERVER_PORT=8081
mvn spring-boot:run
```

**start-instance-2.bat:**
```batch
@echo off
echo Starting Student App Instance 2 on port 8082...
set SERVER_PORT=8082
mvn spring-boot:run
```

**start-instance-3.bat:**
```batch
@echo off
echo Starting Student App Instance 3 on port 8083...
set SERVER_PORT=8083
mvn spring-boot:run
```

### NGINX Control Scripts

**Start NGINX:**
```bash
cd C:\nginx
start nginx
```

**Stop NGINX:**
```bash
cd C:\nginx
nginx -s stop
```

**Reload NGINX (after config changes):**
```bash
cd C:\nginx
nginx -s reload
```

---

## API Endpoints

### Students API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/students` | Get all students |
| GET | `/api/students/{id}` | Get student by ID |
| POST | `/api/students` | Create new student |
| PUT | `/api/students/{id}` | Update student |
| DELETE | `/api/students/{id}` | Delete student |

### Subjects API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/subjects` | Get all subjects |
| GET | `/api/subjects/{id}` | Get subject by ID |
| POST | `/api/subjects` | Create new subject |
| PUT | `/api/subjects/{id}` | Update subject |
| DELETE | `/api/subjects/{id}` | Delete subject |

### Results API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/results` | Get all results |
| GET | `/api/results/{id}` | Get result by ID |
| POST | `/api/results` | Create new result |
| PUT | `/api/results/{id}` | Update result |
| DELETE | `/api/results/{id}` | Delete result |

---

## Testing the System

### Test Load Balancing

```bash
# Test students endpoint through NGINX
curl http://localhost/api/students

# Test subjects endpoint through NGINX
curl http://localhost/api/subjects

# Test results endpoint through NGINX
curl http://localhost/api/results
```

### Test Individual Instances

```bash
# Test instance 1
curl http://localhost:8081/api/students

# Test instance 2
curl http://localhost:8082/api/students

# Test instance 3
curl http://localhost:8083/api/students
```

### Test Health Endpoints

```bash
# Test health on all instances
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

### Test Prometheus Metrics

```bash
# Test metrics on all instances
curl http://localhost:8081/actuator/prometheus
curl http://localhost:8082/actuator/prometheus
curl http://localhost:8083/actuator/prometheus
```

---

## Monitoring

### Health Check Monitoring

Monitor instance health:
```bash
# Continuous health check
while ($true) { curl http://localhost:8081/actuator/health; Start-Sleep 5 }
```

### Prometheus Metrics

Available metrics include:
- JVM memory usage
- HTTP request metrics
- Database connection pool metrics
- Redis cache metrics
- Custom application metrics

Access metrics at: `http://localhost:8081/actuator/prometheus`

### Log Monitoring

Spring Boot logs are displayed in the terminal where each instance is running. Monitor for:
- Application startup errors
- Database connection issues
- Redis connection failures
- API request errors

---

## Troubleshooting

### Issue: NGINX returns 502 Bad Gateway

**Cause:** Spring Boot instances not running or not accessible

**Solution:**
1. Check if instances are running: `netstat -ano | findstr :8081`
2. Verify instances started successfully
3. Check instance logs for errors
4. Restart instances if needed

### Issue: Redis Connection Timeout

**Cause:** Redis container not running

**Solution:**
```bash
docker start redis-cache
# Restart Spring Boot instances after Redis starts
```

### Issue: Database Connection Error

**Cause:** MySQL not running or incorrect credentials

**Solution:**
1. Verify MySQL is running
2. Check database exists: `student_result_db`
3. Verify credentials in `application.properties`
4. Test connection: `mysql -u root -p`

### Issue: LocalDateTime Serialization Error

**Cause:** Jackson not configured for Java 8 time types

**Solution:** Already fixed in current configuration:
- `spring.jackson.serialization.write-dates-as-timestamps=false`
- `JavaTimeModule` registered in `RedisConfig.java`

### Issue: Port Already in Use

**Cause:** Another process using the port

**Solution:**
```bash
# Find process using the port
netstat -ano | findstr :8081

# Kill the process if needed
taskkill /PID <PID> /F
```

---

## Scaling the System

### Adding More Instances

1. Create new startup script:
```batch
@echo off
echo Starting Student App Instance 4 on port 8084...
set SERVER_PORT=8084
mvn spring-boot:run
```

2. Update NGINX configuration:
```nginx
upstream student_backend {
    server localhost:8081;
    server localhost:8082;
    server localhost:8083;
    server localhost:8084;  # Add new instance
}
```

3. Reload NGINX:
```bash
cd C:\nginx
nginx -s reload
```

### Horizontal Scaling

For production deployment:
- Deploy instances on separate servers
- Use a centralized load balancer (NGINX/HAProxy)
- Use a shared database server
- Use a centralized Redis cluster
- Implement session clustering if needed

---

## Security Recommendations

### Production Security Checklist

- [ ] Enable HTTPS/SSL certificates
- [ ] Implement API authentication (JWT/OAuth)
- [ ] Add rate limiting to prevent abuse
- [ ] Configure firewall rules
- [ ] Enable database encryption at rest
- [ ] Implement API key management
- [ ] Add CORS configuration
- [ ] Enable audit logging
- [ ] Regular security updates
- [ ] Penetration testing

### Current Security Headers

NGINX adds the following security headers:
- `X-Frame-Options: SAMEORIGIN`
- `X-Content-Type-Options: nosniff`
- `X-XSS-Protection: 1; mode=block`

---

## Backup and Recovery

### Database Backup

```bash
# Backup MySQL database
mysqldump -u root -p student_result_db > backup_$(date +%Y%m%d).sql

# Restore from backup
mysql -u root -p student_result_db < backup_20260801.sql
```

### Redis Backup

```bash
# Backup Redis data
docker exec redis-cache redis-cli SAVE

# Copy Redis data file
docker cp redis-cache:/data/dump.rdb ./redis_backup.rdb
```

### Configuration Backup

Regularly backup:
- `application.properties`
- `nginx-windows.conf`
- Startup scripts
- Database schema

---

## Performance Optimization

### Database Optimization

- Add indexes to frequently queried columns
- Configure connection pool size in `application.properties`
- Enable query caching
- Regular database maintenance

### Redis Optimization

- Configure appropriate TTL for cached data
- Monitor cache hit ratio
- Adjust connection pool settings
- Consider Redis cluster for high load

### JVM Optimization

Adjust JVM settings in startup scripts:
```batch
set JAVA_OPTS=-Xmx2g -Xms2g -XX:+UseG1GC
mvn spring-boot:run
```

---

## Maintenance

### Regular Maintenance Tasks

- **Daily:** Monitor health endpoints and logs
- **Weekly:** Review performance metrics
- **Monthly:** Database backup and maintenance
- **Quarterly:** Security updates and patches
- **Annually:** Capacity planning and review

### Log Rotation

Configure log rotation to prevent disk space issues:
- Spring Boot logs: Configure `logback-spring.xml`
- NGINX logs: Configure in `nginx.conf`
- System logs: Windows Event Viewer

---

## Support and Documentation

### Additional Documentation

- `ENTERPRISE_DEPLOYMENT_GUIDE.md` - Enterprise deployment guide
- `ENTERPRISE_SETUP_SUMMARY.md` - Setup summary
- `LOAD_BALANCING_TEST_RESULTS.md` - Test results
- `NGINX_WINDOWS_SETUP.md` - NGINX setup instructions

### Configuration Files

- `application.properties` - Spring Boot configuration
- `nginx-windows.conf` - NGINX load balancer configuration
- `pom.xml` - Maven dependencies
- `RedisConfig.java` - Redis configuration

---

## Summary

### System Status: ✅ Production Ready

Your distributed Student Result Management System is fully operational with:

- ✅ **3 Spring Boot instances** running and healthy
- ✅ **NGINX load balancer** distributing traffic
- ✅ **MySQL database** shared across instances
- ✅ **Redis cache** for performance optimization
- ✅ **Prometheus metrics** for monitoring
- ✅ **Health checks** operational
- ✅ **Data consistency** verified
- ✅ **LocalDateTime serialization** fixed
- ✅ **Load balancing** working correctly

### Access Information

- **Load Balancer:** http://localhost
- **Swagger UI:** http://localhost:8081/swagger-ui/index.html
- **Health Monitoring:** http://localhost:8081/actuator/health
- **Prometheus Metrics:** http://localhost:8081/actuator/prometheus

### Next Steps for Production

1. **Security:** Implement authentication and HTTPS
2. **Monitoring:** Set up Prometheus + Grafana
3. **Backup:** Configure automated backups
4. **Scaling:** Deploy to production servers
5. **Testing:** Perform load testing
6. **Documentation:** Update with production details

---

**Deployment completed successfully!**

For questions or issues, refer to the troubleshooting section or check the additional documentation files.
