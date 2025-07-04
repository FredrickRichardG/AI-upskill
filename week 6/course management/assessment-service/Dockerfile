# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/assessment-service-0.0.1-SNAPSHOT.jar assessment-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "assessment-service.jar"] 