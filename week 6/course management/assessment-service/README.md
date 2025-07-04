# Assessment Service

This is the Assessment Service for the LMS microservices architecture.

## Build

```
mvn clean package -DskipTests
```

## Run with Docker Compose

From the project root:

```
docker-compose up --build
```

The service will be available at http://localhost:8084

## Endpoints
- POST /quizzes/{quizId}/submit – Submit quiz answers
- GET /submissions/{id} – Get submission result
- POST /assignments/{id}/grade – Manually grade 