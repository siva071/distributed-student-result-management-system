# Redis Caching Testing Guide

## Overview
This guide provides comprehensive testing steps for verifying Redis caching functionality in the Distributed Student Result Management System.

## Prerequisites

### 1. Start Redis Server
```bash
# Using Docker
docker run -d -p 6379:6379 redis:latest

# Or using local Redis installation
redis-server
```

### 2. Verify Redis is Running
```bash
redis-cli ping
# Expected response: PONG
```

### 3. Start the Spring Boot Application
```bash
mvn spring-boot:run
```

## Cache Configuration

### Cache Names
- `students` - Student data cache
- `subjects` - Subject data cache
- `results` - Result data cache

### Cache Keys
- Student: `student_{id}` and `all`
- Subject: `subject_{id}` and `all`
- Result: `result_{id}` and `all`

### Cache TTL
- 10 minutes (600,000 milliseconds)

## Testing Scenarios

### Scenario 1: Student API Caching

#### Test 1.1: GET /api/students (Cache Miss → MySQL → Redis)

**Steps:**
1. Clear Redis cache: `redis-cli FLUSHALL`
2. Make GET request: `GET http://localhost:8080/api/students`
3. Check application logs for: `Cache Miss → Loading all students from MySQL`
4. Verify in Redis: `redis-cli KEYS student_result_*`
5. Make same GET request again
6. Check application logs: No "Cache Miss" log (Cache Hit)
7. Response time should be significantly faster

**Expected Behavior:**
- First request: Loads from MySQL, stores in Redis
- Second request: Loads from Redis (faster response)

#### Test 1.2: GET /api/students/{id} (Cache Miss → MySQL → Redis)

**Steps:**
1. Clear Redis cache: `redis-cli FLUSHALL`
2. Make GET request: `GET http://localhost:8080/api/students/1`
3. Check application logs for: `Cache Miss → Loading student with ID: 1 from MySQL`
4. Verify in Redis: `redis-cli GET student_result_student_1`
5. Make same GET request again
6. Check application logs: No "Cache Miss" log (Cache Hit)

**Expected Behavior:**
- First request: Loads from MySQL, stores in Redis
- Second request: Loads from Redis (faster response)

#### Test 1.3: POST /api/students (No Caching)

**Steps:**
1. Create new student via POST: `POST http://localhost:8080/api/students`
2. Check application logs: No cache-related logs
3. Verify student is created in MySQL
4. Cache should not be affected

**Expected Behavior:**
- POST requests are NOT cached
- Data is stored directly in MySQL

#### Test 1.4: PUT /api/students/{id} (Cache Refresh)

**Steps:**
1. First, cache a student: `GET http://localhost:8080/api/students/1`
2. Update student via PUT: `PUT http://localhost:8080/api/students/1`
3. Check application logs for: `Updating student with ID: 1 - Cache Refresh`
4. Check application logs for: `Student updated successfully with ID: 1 - Cache Refreshed`
5. Verify cache is updated: `redis-cli GET student_result_student_1`
6. Make GET request: `GET http://localhost:8080/api/students/1`
7. Verify updated data is returned

**Expected Behavior:**
- Cache is refreshed with updated data
- Subsequent GET requests return updated data from cache

#### Test 1.5: DELETE /api/students/{id} (Cache Evict)

**Steps:**
1. First, cache a student: `GET http://localhost:8080/api/students/1`
2. Delete student via DELETE: `DELETE http://localhost:8080/api/students/1`
3. Check application logs for: `Deleting student with ID: 1 - Cache Evict`
4. Check application logs for: `Student deleted successfully with ID: 1 - Cache Evicted`
5. Verify cache is cleared: `redis-cli KEYS student_result_student_1` (should be empty)
6. Make GET request: `GET http://localhost:8080/api/students/1`
7. Should return 404 Not Found

**Expected Behavior:**
- Cache entry is evicted on delete
- Subsequent GET requests return 404

### Scenario 2: Subject API Caching

#### Test 2.1: GET /api/subjects (Cache Miss → MySQL → Redis)

**Steps:**
1. Clear Redis cache: `redis-cli FLUSHALL`
2. Make GET request: `GET http://localhost:8080/api/subjects`
3. Check application logs for: `Cache Miss → Loading all subjects from MySQL`
4. Verify in Redis: `redis-cli KEYS student_result_*`
5. Make same GET request again
6. Check application logs: No "Cache Miss" log (Cache Hit)

**Expected Behavior:**
- First request: Loads from MySQL, stores in Redis
- Second request: Loads from Redis (faster response)

#### Test 2.2: GET /api/subjects/{id} (Cache Miss → MySQL → Redis)

**Steps:**
1. Clear Redis cache: `redis-cli FLUSHALL`
2. Make GET request: `GET http://localhost:8080/api/subjects/1`
3. Check application logs for: `Cache Miss → Loading subject with ID: 1 from MySQL`
4. Verify in Redis: `redis-cli GET student_result_subject_1`
5. Make same GET request again
6. Check application logs: No "Cache Miss" log (Cache Hit)

**Expected Behavior:**
- First request: Loads from MySQL, stores in Redis
- Second request: Loads from Redis (faster response)

#### Test 2.3: PUT /api/subjects/{id} (Cache Refresh)

**Steps:**
1. First, cache a subject: `GET http://localhost:8080/api/subjects/1`
2. Update subject via PUT: `PUT http://localhost:8080/api/subjects/1`
3. Check application logs for: `Updating subject with ID: 1 - Cache Refresh`
4. Check application logs for: `Subject updated successfully with ID: 1 - Cache Refreshed`
5. Verify cache is updated: `redis-cli GET student_result_subject_1`
6. Make GET request: `GET http://localhost:8080/api/subjects/1`
7. Verify updated data is returned

**Expected Behavior:**
- Cache is refreshed with updated data
- Subsequent GET requests return updated data from cache

#### Test 2.4: DELETE /api/subjects/{id} (Cache Evict)

**Steps:**
1. First, cache a subject: `GET http://localhost:8080/api/subjects/1`
2. Delete subject via DELETE: `DELETE http://localhost:8080/api/subjects/1`
3. Check application logs for: `Deleting subject with ID: 1 - Cache Evict`
4. Check application logs for: `Subject deleted successfully with ID: 1 - Cache Evicted`
5. Verify cache is cleared: `redis-cli KEYS student_result_subject_1` (should be empty)
6. Make GET request: `GET http://localhost:8080/api/subjects/1`
7. Should return 404 Not Found

**Expected Behavior:**
- Cache entry is evicted on delete
- Subsequent GET requests return 404

### Scenario 3: Result API Caching

#### Test 3.1: GET /api/results (Cache Miss → MySQL → Redis)

**Steps:**
1. Clear Redis cache: `redis-cli FLUSHALL`
2. Make GET request: `GET http://localhost:8080/api/results`
3. Check application logs for: `Cache Miss → Loading all results from MySQL`
4. Verify in Redis: `redis-cli KEYS student_result_*`
5. Make same GET request again
6. Check application logs: No "Cache Miss" log (Cache Hit)

**Expected Behavior:**
- First request: Loads from MySQL, stores in Redis
- Second request: Loads from Redis (faster response)

#### Test 3.2: GET /api/results/{id} (Cache Miss → MySQL → Redis)

**Steps:**
1. Clear Redis cache: `redis-cli FLUSHALL`
2. Make GET request: `GET http://localhost:8080/api/results/1`
3. Check application logs for: `Cache Miss → Loading result with ID: 1 from MySQL`
4. Verify in Redis: `redis-cli GET student_result_result_1`
5. Make same GET request again
6. Check application logs: No "Cache Miss" log (Cache Hit)

**Expected Behavior:**
- First request: Loads from MySQL, stores in Redis
- Second request: Loads from Redis (faster response)

#### Test 3.3: PUT /api/results/{id} (Cache Refresh)

**Steps:**
1. First, cache a result: `GET http://localhost:8080/api/results/1`
2. Update result via PUT: `PUT http://localhost:8080/api/results/1`
3. Check application logs for: `Updating result with ID: 1 - Cache Refresh`
4. Check application logs for: `Result updated successfully with ID: 1 - Cache Refreshed`
5. Verify cache is updated: `redis-cli GET student_result_result_1`
6. Make GET request: `GET http://localhost:8080/api/results/1`
7. Verify updated data is returned

**Expected Behavior:**
- Cache is refreshed with updated data
- Subsequent GET requests return updated data from cache

#### Test 3.4: DELETE /api/results/{id} (Cache Evict)

**Steps:**
1. First, cache a result: `GET http://localhost:8080/api/results/1`
2. Delete result via DELETE: `DELETE http://localhost:8080/api/results/1`
3. Check application logs for: `Deleting result with ID: 1 - Cache Evict`
4. Check application logs for: `Result deleted successfully with ID: 1 - Cache Evicted`
5. Verify cache is cleared: `redis-cli KEYS student_result_result_1` (should be empty)
6. Make GET request: `GET http://localhost:8080/api/results/1`
7. Should return 404 Not Found

**Expected Behavior:**
- Cache entry is evicted on delete
- Subsequent GET requests return 404

### Scenario 4: Redis Connection Failure (Graceful Degradation)

#### Test 4.1: Application Continues When Redis is Down

**Steps:**
1. Stop Redis server: `docker stop <redis-container-id>` or `redis-cli shutdown`
2. Make GET request: `GET http://localhost:8080/api/students`
3. Application should NOT crash
4. Data should be loaded from MySQL
5. Application logs may show connection errors but should continue functioning

**Expected Behavior:**
- Application continues to work
- Data is loaded from MySQL
- No application crash
- Graceful degradation

#### Test 4.2: Redis Recovery

**Steps:**
1. Start Redis server again: `docker start <redis-container-id>` or `redis-server`
2. Make GET request: `GET http://localhost:8080/api/students`
3. Check application logs: Should show normal caching behavior
4. Verify data is cached in Redis

**Expected Behavior:**
- Redis connection is restored
- Caching resumes automatically
- No application restart required

## Performance Testing

### Measure Response Times

#### Without Cache (MySQL Only)
```bash
# Clear cache
redis-cli FLUSHALL

# Measure time
time curl http://localhost:8080/api/students
```

#### With Cache (Redis)
```bash
# Warm up cache
curl http://localhost:8080/api/students

# Measure time
time curl http://localhost:8080/api/students
```

**Expected Result:**
- Cached response should be significantly faster (2-10x improvement)

## Redis CLI Commands for Testing

### View All Cache Keys
```bash
redis-cli KEYS student_result_*
```

### View Specific Cache Entry
```bash
redis-cli GET student_result_student_1
redis-cli GET student_result_subject_1
redis-cli GET student_result_result_1
```

### View Cache TTL
```bash
redis-cli TTL student_result_student_1
```

### Clear All Cache
```bash
redis-cli FLUSHALL
```

### Clear Specific Cache
```bash
redis-cli DEL student_result_student_1
```

### Monitor Redis Operations
```bash
redis-cli MONITOR
```

## Sample Test Data

### Create Student
```http
POST http://localhost:8080/api/students
Content-Type: application/json

{
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
}
```

### Create Subject
```http
POST http://localhost:8080/api/subjects
Content-Type: application/json

{
  "subjectCode": "CS501",
  "subjectName": "Data Structures",
  "department": "Computer Science",
  "semester": 5,
  "credits": 4
}
```

### Create Result
```http
POST http://localhost:8080/api/results
Content-Type: application/json

{
  "studentId": 1,
  "subjectId": 1,
  "internalMarks": 35,
  "externalMarks": 45
}
```

## Expected Log Messages

### Cache Miss
```
Cache Miss → Loading all students from MySQL
Cache Miss → Loading student with ID: 1 from MySQL
Cache Miss → Loading all subjects from MySQL
Cache Miss → Loading subject with ID: 1 from MySQL
Cache Miss → Loading all results from MySQL
Cache Miss → Loading result with ID: 1 from MySQL
```

### Cache Refresh
```
Updating student with ID: 1 - Cache Refresh
Student updated successfully with ID: 1 - Cache Refreshed
Updating subject with ID: 1 - Cache Refresh
Subject updated successfully with ID: 1 - Cache Refreshed
Updating result with ID: 1 - Cache Refresh
Result updated successfully with ID: 1 - Cache Refreshed
```

### Cache Evict
```
Deleting student with ID: 1 - Cache Evict
Student deleted successfully with ID: 1 - Cache Evicted
Deleting subject with ID: 1 - Cache Evict
Subject deleted successfully with ID: 1 - Cache Evicted
Deleting result with ID: 1 - Cache Evict
Result deleted successfully with ID: 1 - Cache Evicted
```

## Troubleshooting

### Issue: Cache Not Working
**Solution:**
1. Verify Redis is running: `redis-cli ping`
2. Check application.properties Redis configuration
3. Verify @EnableCaching annotation is present
4. Check RedisConfig class is properly configured

### Issue: Application Crashes When Redis is Down
**Solution:**
1. Check Redis connection timeout settings
2. Verify graceful error handling in RedisConfig
3. Check application logs for specific errors

### Issue: Cache Not Refreshing on Update
**Solution:**
1. Verify @CachePut annotation is present
2. Check cache key matches between GET and PUT
3. Verify method signature matches cache key pattern

### Issue: Cache Not Evicting on Delete
**Solution:**
1. Verify @CacheEvict annotation is present
2. Check cache key matches between GET and DELETE
3. Verify allEntries flag is set correctly

## Summary

The Redis caching implementation provides:
- **Performance Improvement**: 2-10x faster response times for cached data
- **Automatic Cache Management**: @Cacheable, @CachePut, @CacheEvict annotations
- **Graceful Degradation**: Application continues when Redis is unavailable
- **Comprehensive Logging**: Cache hit/miss logging for monitoring
- **Production-Ready**: JSON serialization, TTL configuration, proper error handling

All GET APIs are cached, POST APIs are not cached, and UPDATE/DELETE operations automatically refresh/evict cache entries.
