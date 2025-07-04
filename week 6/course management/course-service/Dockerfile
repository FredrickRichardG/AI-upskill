# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/course-service-0.0.1-SNAPSHOT.jar course-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "course-service.jar"] 