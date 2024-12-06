# Stage 1: Build
FROM maven:3.8.8-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy Maven configuration files to leverage caching for dependencies
COPY user-product-service/user-service/pom.xml /app/pom.xml

# Pre-download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app/user-product-service/user-service

# Copy the JAR from the build stage
COPY --from=build /app/user-product-service/user-service/target/user-service-0.0.1.jar /app/app.jar

# Expose port
EXPOSE 8001

# Set JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]