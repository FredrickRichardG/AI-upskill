# Enrollment Service

This is the Enrollment Service for the LMS microservices architecture.

## Build

```
mvn clean package -DskipTests
```

## Run with Docker Compose

From the project root:

```
docker-compose up --build
```

The service will be available at http://localhost:8083/enrollments

## Endpoints
- POST /enrollments – Enroll a user to a course
- GET /enrollments/{id}/progress – Get progress
- PATCH /enrollments/{id}/lessons/{lessonId} – Mark lesson complete 