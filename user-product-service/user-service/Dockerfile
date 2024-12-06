# Stage 1: Build
FROM maven:3.8.8-eclipse-temurin-17-alpine AS build

# Set working directory for the build stage
WORKDIR /app

# Copy the Maven configuration files to leverage caching for dependencies
COPY user-product-service/user-service/pom.xml /app/pom.xml

# Pre-download dependencies to optimize build time
RUN mvn dependency:go-offline -B

# Copy the entire source code
COPY . .

# Build the application JAR without running tests
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Set working directory for runtime
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/user-service-0.0.1.jar /app.jar

# Expose the application port
EXPOSE 8001

# Add JVM optimizations via environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"

# Set the entry point for the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
