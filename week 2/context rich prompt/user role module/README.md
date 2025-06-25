# User Role Service

A Spring Boot service for managing users and roles with audit capabilities.

## Features

- User management (CRUD operations)
- Role management and assignment
- Audit fields for all entities
- Spring Cloud Config integration
- HashiCorp Vault integration for secrets
- OpenAPI documentation
- MySQL database
- JPA auditing

## Prerequisites

- Java 17 or higher
- Maven
- MySQL
- Spring Cloud Config Server (running on port 8888)
- HashiCorp Vault (running on port 8200)

## Environment Variables

The following environment variables need to be set:

```bash
MYSQL_USER=your_mysql_username
MYSQL_PASSWORD=your_mysql_password
VAULT_TOKEN=your_vault_token
```

## Setup

1. Clone the repository
2. Set up the environment variables
3. Make sure MySQL is running
4. Make sure Spring Cloud Config Server is running
5. Make sure HashiCorp Vault is running and configured

## Building the Application

```bash
mvn clean install
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Documentation

Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

The OpenAPI specification is available at:

```
http://localhost:8080/api-docs
```

## Security

The application uses basic authentication. All endpoints except Swagger UI and API docs require authentication.

## Database Schema

The application will automatically create the following tables:
- users
- roles
- user_roles (junction table)

All entities include audit fields:
- created
- updated
- created_by
- updated_by
- row_version

## API Endpoints

### User Management
- POST /api/users - Create a new user
- GET /api/users/{id} - Get user by ID
- GET /api/users/username/{username} - Get user by username
- GET /api/users - Get all users
- PUT /api/users/{id} - Update user
- DELETE /api/users/{id} - Delete user
- PUT /api/users/{id}/roles - Assign roles to user 