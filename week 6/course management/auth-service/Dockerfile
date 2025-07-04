# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/auth-service-0.0.1-SNAPSHOT.jar auth-service.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "auth-service.jar"] 