# School Attendance Management System

## Architecture Overview

```
+------------------+     +------------------+     +------------------+
|                  |     |                  |     |                  |
|  Client Layer    |     |  API Gateway     |     |  Service Layer   |
|  (Web/Mobile)    |<--->|  (Spring Cloud)  |<--->|  (Spring Boot)   |
|                  |     |                  |     |                  |
+------------------+     +------------------+     +------------------+
                                                          |
                                                          v
+------------------+     +------------------+     +------------------+
|                  |     |                  |     |                  |
|  Auth Service    |<--->|  Data Layer      |<--->|  Cache Layer     |
|  (Keycloak)      |     |  (PostgreSQL)    |     |  (Redis)         |
|                  |     |                  |     |                  |
+------------------+     +------------------+     +------------------+
```

## Technology Stack

- **Backend Framework**: Spring Boot 3.x
- **API Gateway**: Spring Cloud Gateway
- **Authentication**: Keycloak
- **Database**: PostgreSQL
- **Caching**: Redis
- **Containerization**: Docker
- **CI/CD**: GitHub Actions
- **Cloud Platform**: AWS (Lambda + API Gateway)

## Key Components

1. **API Gateway**
   - Request routing
   - Rate limiting
   - Load balancing
   - API documentation (Swagger)

2. **Authentication Service**
   - User authentication
   - Role-based access control
   - JWT token management

3. **Attendance Service**
   - Student attendance management
   - Class-wise attendance tracking
   - Attendance reports generation

4. **Student Management Service**
   - Student registration
   - Student profile management
   - Class assignment

5. **Reporting Service**
   - Attendance analytics
   - Custom report generation
   - Data export

## Database Schema

```sql
-- Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Classes Table
CREATE TABLE classes (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    teacher_id UUID REFERENCES users(id),
    academic_year VARCHAR(9) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Students Table
CREATE TABLE students (
    id UUID PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    class_id UUID REFERENCES classes(id),
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    joined_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Attendance Table
CREATE TABLE attendance (
    id UUID PRIMARY KEY,
    student_id UUID REFERENCES students(id),
    class_id UUID REFERENCES classes(id),
    date DATE NOT NULL,
    status VARCHAR(10) NOT NULL,
    marked_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, date)
);
```

## API Endpoints

### Authentication
- POST /api/auth/login
- POST /api/auth/logout
- POST /api/auth/refresh-token

### Attendance
- POST /api/attendance/mark
- GET /api/attendance/class/{classId}
- GET /api/attendance/student/{studentId}
- GET /api/attendance/summary/class/{classId}
- GET /api/attendance/summary/school

### Students
- POST /api/students
- GET /api/students/class/{classId}
- PUT /api/students/{studentId}
- DELETE /api/students/{studentId}

### Classes
- POST /api/classes
- GET /api/classes
- GET /api/classes/{classId}
- PUT /api/classes/{classId}

## Security Considerations

1. **Authentication**
   - JWT-based authentication
   - Token refresh mechanism
   - Secure password storage with bcrypt

2. **Authorization**
   - Role-based access control (TEACHER, ADMIN)
   - Resource-level permissions
   - API endpoint security

3. **Data Protection**
   - HTTPS encryption
   - Input validation
   - SQL injection prevention
   - XSS protection

## Performance Optimization

1. **Caching Strategy**
   - Redis for frequently accessed data
   - Cache invalidation policies
   - Distributed caching

2. **Database Optimization**
   - Indexed queries
   - Connection pooling
   - Query optimization

3. **Scalability**
   - Horizontal scaling
   - Load balancing
   - Microservices architecture 