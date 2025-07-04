# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/notification-service-0.0.1-SNAPSHOT.jar notification-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "notification-service.jar"] 