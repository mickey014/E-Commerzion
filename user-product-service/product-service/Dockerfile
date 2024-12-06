# Stage 1: Build
FROM maven:3.8.8-eclipse-temurin-17-alpine AS build

# Set the working directory for the build stage
WORKDIR /app

# Copy Maven configuration files first for dependency caching
COPY user-product-service/product-service/pom.xml /app/user-product-service/product-service/
COPY user-product-service/pom.xml /app/user-product-service/

# Pre-download dependencies to leverage Docker caching
RUN mvn dependency:go-offline -B

# Copy the entire project source
COPY . .

# Navigate to the product-service module
WORKDIR /app/user-product-service/product-service

# Build the application JAR without tests
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Set the working directory for the runtime stage
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/user-product-service/product-service/target/product-service-0.0.1.jar /app/app.jar

# Expose the application's port
EXPOSE 8201

# JVM optimizations for containers
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"

# Set the entry point for the container
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
