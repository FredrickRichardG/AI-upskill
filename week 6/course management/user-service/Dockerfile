# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built jar (will be named in build step)
COPY target/user-service-0.0.1-SNAPSHOT.jar user-service.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "user-service.jar"] 