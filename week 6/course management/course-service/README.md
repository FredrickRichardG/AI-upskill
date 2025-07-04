# Course Service

This is the Course Service for the LMS microservices architecture.

## Build

```
mvn clean package -DskipTests
```

## Run with Docker Compose

From the project root:

```
docker-compose up --build
```

The service will be available at http://localhost:8082/courses

## Endpoints
- POST /courses – Create course
- GET /courses – List/search courses
- GET /courses/{id}/outline – Get course structure 