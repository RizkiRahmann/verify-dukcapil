# ===== Dockerfile =====
# File: Dockerfile

# Use Eclipse Temurin JDK 21
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# ===== Runtime Stage =====
FROM eclipse-temurin:21-jre-alpine

# Install packages for better monitoring
RUN apk add --no-cache curl jq

# Create app user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy jar file
COPY --from=builder /app/target/dukcapil-service-*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

# Switch to app user
USER appuser

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8081/api/dukcapil/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
