# Multi-stage build untuk Dukcapil Dummy
# Stage 1: Build stage  
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml dan download dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build aplikasi
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy JAR dari build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variables (Spring Boot properties mapping)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/dukcapil_ktp
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres123
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

# Expose port
EXPOSE 8081

# Run aplikasi dengan nama JAR yang fixed
CMD ["java", "-jar", "app.jar"]