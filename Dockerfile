# Multi-stage build for Spring Boot with Kotlin

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 as builder

WORKDIR /app

# Copy pom.xml and download dependencies (for better caching)
COPY brick-server/pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY brick-server/src ./src

# Build application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/brick-server-*.jar app.jar

# Expose port (Render will use PORT env var)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8080}/api/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
