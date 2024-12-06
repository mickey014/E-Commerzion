# Stage 1: Build
FROM maven:3.8.8-eclipse-temurin-17-alpine AS build

# Set working directory for the build stage
WORKDIR /app

# Copy only Maven configuration files first to leverage caching for dependencies
COPY user-product-service/seller-service/pom.xml /app/user-product-service/seller-service/
COPY user-product-service/pom.xml /app/user-product-service/
COPY pom.xml /app/

# Pre-download dependencies to optimize build time
RUN mvn dependency:go-offline -B

# Copy the full project source
COPY . .

# Navigate to the seller-service module directory
WORKDIR /app/user-product-service/seller-service

# Build the application JAR without running tests
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Set working directory for runtime
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/user-product-service/seller-service/target/seller-service-0.0.1.jar /app/app.jar

# Expose the application port
EXPOSE 8101

# Add JVM optimizations via environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"

# Set the entry point for the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
