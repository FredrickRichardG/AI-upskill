# User Service

This is the User Service for the LMS microservices architecture.

## Build

```
mvn clean package -DskipTests
```

## Run with Docker Compose

From the project root:

```
docker-compose up --build
```

The service will be available at http://localhost:8081/users

## Endpoints
- POST /users – Create user
- GET /users/{id} – Fetch user profile
- PATCH /users/{id}/roles – Update roles 