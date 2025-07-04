# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/enrollment-service-0.0.1-SNAPSHOT.jar enrollment-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "enrollment-service.jar"] 