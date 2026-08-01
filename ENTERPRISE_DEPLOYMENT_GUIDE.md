# Enterprise Deployment Guide
## Distributed Student Result Management System

This guide covers the complete enterprise-grade deployment with HAProxy, Prometheus, and Grafana monitoring.

---

## Architecture Overview

```
Client
   |
HAProxy Load Balancer (Port 80)
   |
----------------------------------
|              |                |
Spring Boot    Spring Boot      Spring Boot
Instance 1     Instance 2       Instance 3
(8081)         (8082)           (8083)
   |              |                |
   -------------------------------
               |
          Redis Cache (6379)
               |
            MySQL Database
               |
        Prometheus (9090)
               |
            Grafana (3000)
```

---

## Prerequisites

### Software Required
- **Docker Desktop** - Latest version
- **Java 21** - For Spring Boot instances
- **Maven 3.9+** - For building the project
- **MySQL 8.0+** - Database (local or Docker)
- **PowerShell** - For running batch scripts

### System Requirements
- **RAM**: Minimum 8GB (16GB recommended)
- **Disk Space**: 20GB free space
- **CPU**: Quad-core or higher

---

## Quick Start

### Step 1: Start Spring Boot Instances

Open **three separate terminal windows** and run:

**Terminal 1 - Instance 1:**
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
.\start-instance-1.bat
```

**Terminal 2 - Instance 2:**
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
.\start-instance-2.bat
```

**Terminal 3 - Instance 3:**
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
.\start-instance-3.bat
```

Wait for all instances to start (you'll see "Started StudentResultManagementApplication").

### Step 2: Start Enterprise Stack

Run the enterprise startup script:
```bash
cd "c:\Users\sivas\Desktop\distubted server equally"
.\START_ENTERPRISE.bat
```

This will start:
- HAProxy Load Balancer
- Prometheus Monitoring
- Grafana Visualization
- Redis Cache

### Step 3: Verify Deployment

**Check HAProxy Stats:**
- Open: http://localhost:8404/stats
- Login: admin / admin123
- Verify all three Spring Boot instances are UP

**Check Prometheus:**
- Open: http://localhost:9090
- Go to Status → Targets
- Verify all targets are UP

**Check Grafana:**
- Open: http://localhost:3000
- Login: admin / admin123
- Verify Prometheus datasource is connected

**Test Load Balancing:**
```bash
curl http://localhost/api/students
```

---

## Configuration Files

### HAProxy Configuration (`haproxy.cfg`)

**Key Features:**
- Round-robin load balancing
- Health checks every 2 seconds
- Automatic failover (3 failures = DOWN)
- Statistics page on port 8404
- Security headers

**Load Balancing Algorithm:**
```haproxy
backend student_backend
    balance roundrobin
    option httpchk GET /actuator/health
    server student-app-1 host.docker.internal:8081 check inter 2000 rise 2 fall 3
    server student-app-2 host.docker.internal:8082 check inter 2000 rise 2 fall 3
    server student-app-3 host.docker.internal:8083 check inter 2000 rise 2 fall 3
```

### Prometheus Configuration (`prometheus.yml`)

**Monitored Services:**
- Spring Boot instances (ports 8081, 8082, 8083)
- Redis cache
- MySQL database
- HAProxy load balancer
- Prometheus self-monitoring

**Scrape Interval:** 15 seconds

### Grafana Configuration

**Datasources:**
- Prometheus (auto-configured)

**Default Credentials:**
- Username: admin
- Password: admin123

---

## Access Points

### Application Endpoints

| Service | URL | Description |
|---------|-----|-------------|
| HAProxy Load Balancer | http://localhost | Main application entry point |
| HAProxy Stats | http://localhost:8404/stats | Load balancer statistics |
| Students API | http://localhost/api/students | Student management |
| Subjects API | http://localhost/api/subjects | Subject management |
| Results API | http://localhost/api/results | Result management |
| Swagger UI | http://localhost/swagger-ui/index.html | API documentation |
| Health Check | http://localhost/actuator/health | Application health |
| Prometheus Metrics | http://localhost/actuator/prometheus | Application metrics |

### Monitoring Endpoints

| Service | URL | Credentials |
|---------|-----|-------------|
| Prometheus | http://localhost:9090 | None |
| Grafana | http://localhost:3000 | admin/admin123 |
| Redis | localhost:6379 | None |

---

## Monitoring & Observability

### Prometheus Metrics

**Available Metrics:**
- JVM metrics (memory, GC, threads)
- HTTP request metrics (latency, throughput)
- Database connection pool metrics
- Cache hit/miss ratios
- Custom business metrics

**Access Metrics:**
```bash
# Via HAProxy
curl http://localhost/actuator/prometheus

# Direct to instance
curl http://localhost:8081/actuator/prometheus
```

### Grafana Dashboards

**Recommended Dashboards:**
1. **JVM Micrometer Dashboard** - JVM and application metrics
2. **Spring Boot Statistics** - Request/response metrics
3. **Redis Dashboard** - Cache performance
4. **HAProxy Dashboard** - Load balancer statistics

**Import Dashboards:**
1. Open Grafana: http://localhost:3000
2. Go to Dashboards → Import
3. Enter dashboard ID or upload JSON
4. Select Prometheus datasource

---

## Health Checks & Failover

### HAProxy Health Checks

**Configuration:**
- Check interval: 2000ms
- Rise threshold: 2 consecutive successes
- Fall threshold: 3 consecutive failures
- Check endpoint: `/actuator/health`

**Failover Behavior:**
- If an instance fails 3 consecutive health checks, it's marked DOWN
- Traffic is automatically routed to remaining healthy instances
- When instance recovers, it's marked UP after 2 consecutive successes

### Manual Health Checks

```bash
# Check all instances
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# Check via HAProxy
curl http://localhost/actuator/health
```

---

## Scaling

### Adding More Instances

1. **Create new instance batch script:**
   ```bash
   copy start-instance-1.bat start-instance-4.bat
   # Edit to use port 8084
   ```

2. **Update HAProxy configuration:**
   ```haproxy
   server student-app-4 host.docker.internal:8084 check inter 2000 rise 2 fall 3
   ```

3. **Update Prometheus configuration:**
   ```yaml
   - job_name: 'student-app-4'
     metrics_path: '/actuator/prometheus'
     static_configs:
       - targets: ['host.docker.internal:8084']
   ```

4. **Restart services:**
   ```bash
   .\STOP_ENTERPRISE.bat
   .\START_ENTERPRISE.bat
   ```

---

## Troubleshooting

### HAProxy Issues

**HAProxy won't start:**
```bash
# Check configuration
docker exec haproxy-lb haproxy -c -f /usr/local/etc/haproxy/haproxy.cfg

# View logs
docker logs haproxy-lb
```

**Instances showing DOWN in HAProxy stats:**
- Verify Spring Boot instances are running
- Check firewall settings
- Verify `/actuator/health` endpoint is accessible
- Check HAProxy health check configuration

### Prometheus Issues

**Targets not showing up:**
- Verify Spring Boot instances expose `/actuator/prometheus`
- Check Prometheus configuration
- Verify network connectivity
- Check Prometheus logs: `docker logs prometheus`

### Grafana Issues

**Can't connect to Prometheus:**
- Verify Prometheus datasource configuration
- Check Prometheus is running: `docker ps | findstr prometheus`
- Verify network connectivity
- Check Grafana logs: `docker logs grafana`

### General Issues

**Port conflicts:**
```bash
# Check what's using the port
netstat -ano | findstr :80
netstat -ano | findstr :9090
netstat -ano | findstr :3000
```

**Docker issues:**
```bash
# Restart Docker Desktop
# Check Docker is running
docker ps
```

---

## Performance Tuning

### HAProxy Tuning

**Increase connection limits:**
```haproxy
global
    maxconn 10000

backend student_backend
    server student-app-1 host.docker.internal:8081 maxconn 500
```

**Enable compression:**
```haproxy
defaults
    compression algo gzip
```

### Spring Boot Tuning

**Increase thread pool:**
```properties
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10
```

**Optimize connection pool:**
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

### Redis Tuning

**Increase memory:**
```yaml
redis:
  command: redis-server --maxmemory 512mb --maxmemory-policy allkeys-lru
```

---

## Security Considerations

### Production Security

1. **Change default passwords:**
   - Grafana admin password
   - HAProxy stats password
   - MySQL root password

2. **Enable SSL/TLS:**
   - Configure HTTPS for HAProxy
   - Use SSL for database connections

3. **Network isolation:**
   - Use Docker networks for service isolation
   - Restrict access to monitoring endpoints

4. **Authentication:**
   - Enable authentication for Prometheus
   - Use reverse proxy with authentication

---

## Backup & Recovery

### Data Backup

**MySQL Backup:**
```bash
mysqldump -u root -p student_results > backup.sql
```

**Redis Backup:**
```bash
docker exec redis-cache redis-cli BGSAVE
docker cp redis-cache:/data/dump.rdb ./redis-backup.rdb
```

**Configuration Backup:**
```bash
# Backup all configuration files
copy haproxy.cfg haproxy.cfg.backup
copy prometheus.yml prometheus.yml.backup
copy application.properties application.properties.backup
```

### Disaster Recovery

1. Restore MySQL from backup
2. Restore Redis from backup
3. Restore configuration files
4. Restart all services
5. Verify health checks

---

## Maintenance

### Regular Tasks

**Daily:**
- Check HAProxy stats for errors
- Monitor Prometheus alerts
- Review Grafana dashboards

**Weekly:**
- Review application logs
- Check disk space usage
- Verify backup integrity

**Monthly:**
- Update Docker images
- Review and update configurations
- Performance tuning based on metrics

---

## Support & Documentation

### Useful Links

- **HAProxy Documentation**: http://www.haproxy.org/#docs
- **Prometheus Documentation**: https://prometheus.io/docs/
- **Grafana Documentation**: https://grafana.com/docs/
- **Spring Boot Actuator**: https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html

### Log Locations

**HAProxy Logs:**
```bash
docker logs haproxy-lb
```

**Prometheus Logs:**
```bash
docker logs prometheus
```

**Grafana Logs:**
```bash
docker logs grafana
```

**Spring Boot Logs:**
- Check individual terminal windows

---

## Summary

This enterprise deployment provides:
- ✅ High availability with HAProxy load balancing
- ✅ Automatic failover and health checks
- ✅ Comprehensive monitoring with Prometheus
- ✅ Beautiful visualization with Grafana
- ✅ Scalable architecture
- ✅ Production-ready configuration
- ✅ Redis caching for performance
- ✅ MySQL database for persistence

Your distributed Student Result Management System is now enterprise-ready!
