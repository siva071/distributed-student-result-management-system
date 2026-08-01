# Multi-stage Dockerfile for Spring Boot Application
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install wget for health checks
RUN apk add --no-cache wget

# Add a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR file from build stage
COPY --from=build /app/target/student-result-management-1.0.0.jar app.jar

# Expose port (will be configured via environment variable)
EXPOSE 8080

# Health check (uses SERVER_PORT environment variable)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
