# HRMS Backend - Employee-Auth Integration

## Overview
The employee and auth services are integrated using **reactive async event-driven architecture** with Apache Kafka for real-time communication.

When an employee is created, a user account is automatically created in the auth service with a default password.

## Architecture

### Event Flow
1. **Employee Service** creates a new employee
2. **Employee Service** publishes `EmployeeCreatedEvent` to Kafka topic `employee.created` (async)
3. **Auth Service** consumes the event asynchronously
4. **Auth Service** creates a user account with a default password
5. User credentials are logged (in production, send via email/SMS)

### Technology Stack
- **Message Broker**: Apache Kafka
- **Serialization**: JSON (Jackson)
- **Async Processing**: Spring `@Async` + Kafka async consumer
- **Pattern**: Event-Driven Architecture

## Quick Start

### 1. Start Infrastructure
```bash
# Start Kafka and PostgreSQL
docker-compose up -d

# Verify services are running
docker ps
```

### 2. Start Services
```bash
# Terminal 1 - Start Auth Service (Port 8080)
./gradlew :apps:hrms-auth:bootRun

# Terminal 2 - Start Employee Service (Port 8081)
./gradlew :apps:hrms-employee:bootRun
```

### 3. Test Integration

#### Create an Employee
```bash
curl -X POST http://localhost:8081/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "employeeNumber": "EMP001",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "companyId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

#### Check Auth Service Logs
You should see:
```
User created successfully for employee: <UUID>, username: EMP001, default password: EMP001Doe@123
```

#### Test Login with Generated Credentials
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "EMP001",
    "password": "EMP001Doe@123"
  }'
```

## Password Generation

Default password format: `{employeeNumber}{first4CharsOfLastName}@123`

**Example:**
- Employee Number: `EMP001`
- Last Name: `Johnson`
- Generated Password: `EMP001John@123`

## Service Ports

| Service          | Port |
|------------------|------|
| Auth Service     | 8080 |
| Employee Service | 8081 |
| Kafka            | 9092 |
| PostgreSQL       | 5432 |
| Zookeeper        | 2181 |

## Key Features

### ✅ Async/Reactive Architecture
- Event publishing is non-blocking
- Kafka handles message delivery asynchronously
- Consumer processes events in the background

### ✅ Decoupled Services
- Employee service doesn't know about auth service
- Auth service reacts to events independently
- Services can scale independently

### ✅ Fault Tolerance
- Kafka ensures message delivery
- Consumer group provides load balancing
- Failed events can be retried

### ✅ Auditability
- All events are logged
- Event history available in Kafka

## Implementation Details

### Messaging Infrastructure (`libs/messaging`)
- `EventPublisher.kt` - Generic async event publisher
- `EmployeeCreatedEvent.kt` - Event DTO
- `KafkaProducerConfig.kt` / `KafkaConsumerConfig.kt` - Kafka configuration

### Employee Service
Publishes events after employee creation:
```kotlin
eventPublisher.publish(Topics.EMPLOYEE_CREATED, savedEmployee.id.toString(), event)
```

### Auth Service
Listens to events and creates users:
```kotlin
@Async
@KafkaListener(topics = [Topics.EMPLOYEE_CREATED], groupId = "auth-service")
fun handleEmployeeCreated(event: EmployeeCreatedEvent) {
    userService.createUserForEmployee(event)
}
```

## Database Schema

The system uses separate databases:
- `hrms_auth` - User authentication data
- `hrms_employee` - Employee data
- `hrms_leave` - Leave management
- `hrms_salary_advance` - Salary advances

## Building the Project

```bash
# Build all services
./gradlew build

# Build specific service
./gradlew :apps:hrms-employee:build
./gradlew :apps:hrms-auth:build
```

## Production Considerations

1. **Password Delivery**: Replace log output with secure email/SMS notification
2. **Error Handling**: Implement dead letter queue for failed user creation
3. **Security**: Encrypt sensitive data in Kafka messages
4. **Monitoring**: Add metrics for event processing latency
5. **Password Policy**: Implement stronger password generation
6. **Event Versioning**: Add schema versioning for backward compatibility

## Troubleshooting

### Kafka Connection Issues
```bash
# Check Kafka is running
docker ps | grep kafka

# Check Kafka topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Database Connection Issues
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Connect to database
psql -U postgres -h localhost -d hrms_auth
```

### Service Won't Start
```bash
# Check logs
./gradlew :apps:hrms-employee:bootRun --info
./gradlew :apps:hrms-auth:bootRun --info
```

## Project Structure

```
hrms-backend/
├── apps/
│   ├── hrms-auth/          # Authentication service
│   ├── hrms-employee/      # Employee management service
│   ├── hrms-leave/         # Leave management service
│   └── hrms-salary-advance/# Salary advance service
├── libs/
│   ├── core/               # Core utilities
│   ├── dto/                # Data transfer objects
│   ├── messaging/          # Kafka messaging infrastructure
│   ├── persistence/        # Database entities
│   └── security/           # Security configuration
├── docker-compose.yml      # Infrastructure setup
└── README.md              # This file
```

## Summary

This project demonstrates a microservices architecture with:
- ✅ **Reactive async communication** via Kafka
- ✅ **Event-driven design** for loose coupling
- ✅ **Automatic user provisioning** from employee data
- ✅ **Scalable infrastructure** with Docker
- ✅ **Clean separation of concerns** across services

The integration ensures that when an employee is created, their user account is automatically provisioned in real-time through asynchronous event processing.
