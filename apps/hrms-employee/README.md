# Employee Management Service

A comprehensive employee and department management service for the HRMS system with full CRUD operations, Kafka event integration, and role-based access control.

## 📚 Documentation

- **[Complete API Documentation](EMPLOYEE_API_DOCUMENTATION.md)** - Full API reference with examples
- **[API Testing Checklist](API_TESTING_CHECKLIST.md)** - Comprehensive testing guide
- **[Quick Reference](API_QUICK_REFERENCE.md)** - Quick lookup for common operations

## 🚀 Features

### Core Functionality
- ✅ **Employee Management** - Complete CRUD operations for employee records
- ✅ **Department Management** - Organizational structure and department hierarchy
- ✅ **Search & Filter** - Powerful search by name, company, department
- ✅ **Pagination & Sorting** - Efficient data retrieval for large datasets
- ✅ **Role-Based Access Control** - 4-tier authorization (ADMIN, HR, MANAGER, EMPLOYEE)

### Advanced Features
- ✅ **Comprehensive Employee Data** - Personal info, contact details, documents
- ✅ **Contract Management** - Job type, salary, probation tracking
- ✅ **Bank Details** - Secure storage of payment information
- ✅ **Dependents Tracking** - Family member information
- ✅ **Emergency Contacts** - Contact person management
- ✅ **Kafka Integration** - Event-driven architecture for employee creation
- ✅ **JWT Authentication** - Secure API access with token validation

## 🏗️ Architecture

### Tech Stack
- **Language:** Kotlin 1.9.21
- **Framework:** Spring Boot 3.2.0
- **Database:** PostgreSQL with Flyway migrations
- **Security:** Spring Security with JWT
- **Messaging:** Apache Kafka for event streaming
- **ORM:** Spring Data JPA with Hibernate

### Project Structure
```
apps/hrms-employee/
├── src/main/kotlin/com/cre/hrms/employee/
│   ├── controller/          # REST endpoints
│   │   ├── EmployeeController.kt
│   │   └── DepartmentController.kt
│   ├── service/             # Business logic
│   │   ├── EmployeeService.kt
│   │   └── DepartmentService.kt
│   ├── repository/          # Data access
│   │   ├── EmployeeRepository.kt
│   │   └── DepartmentRepository.kt
│   ├── entity/              # Database entities
│   │   ├── Employee.kt
│   │   └── Department.kt
│   ├── mapper/              # Entity-DTO mapping
│   │   ├── EmployeeMapper.kt
│   │   └── DepartmentMapper.kt
│   └── kafka/               # Event consumers/producers
│       └── EmployeeEventConsumer.kt
└── src/main/resources/
    └── db/migration/        # Database migrations
```

## 🔧 Setup & Configuration

### Prerequisites
- Java 17+
- PostgreSQL database
- Kafka (for event streaming)
- Auth service running (for JWT tokens)

### Database Setup
```sql
-- Create database
CREATE DATABASE hrms_employee;

-- Migrations run automatically with Flyway
-- Located in: src/main/resources/db/migration/
```

### Running the Service
```bash
# From project root
./gradlew :apps:hrms-employee:bootRun

# Service will start on port 8082
```

### Environment Variables
```properties
# Application
server.port=8082

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms_employee
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT
jwt.secret=your_secret_key

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
```

## 📊 API Overview

### Base URL
```
http://localhost:8082
```

### Endpoint Categories

**Employees** - `/api/v1/employees`
- Complete employee lifecycle management
- Search and filtering capabilities
- 9 endpoints total

**Departments** - `/api/v1/departments`
- Department structure management
- Active/inactive department filtering
- 10 endpoints total (including /active variants)

### Total: 19 API Endpoints

## 🎯 Quick Start Examples

### 1. Create Employee
```bash
curl -X POST http://localhost:8082/api/v1/employees \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeNumber": "EMP001",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@company.com",
    "companyId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**Response:**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

### 2. Get Employee by ID
```bash
curl -X GET http://localhost:8082/api/v1/employees/770e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Search Employees
```bash
curl -X GET "http://localhost:8082/api/v1/employees/search?name=john" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. Create Department
```bash
curl -X POST http://localhost:8082/api/v1/departments \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Engineering",
    "code": "ENG",
    "companyId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

## 🔐 Security & Authorization

### Role Hierarchy
```
ADMIN > HR > MANAGER > EMPLOYEE
```

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full system access, can delete resources |
| **HR** | Create, read, update employees and departments |
| **MANAGER** | Read-only access to employees and departments |
| **EMPLOYEE** | View own profile and departments |

### Endpoint Access Matrix

| Operation | ADMIN | HR | MANAGER | EMPLOYEE |
|-----------|-------|----|---------| ---------|
| Create Employee | ✅ | ✅ | ❌ | ❌ |
| View Employees | ✅ | ✅ | ✅ | ❌ |
| Update Employee | ✅ | ✅ | ❌ | ❌ |
| Delete Employee | ✅ | ❌ | ❌ | ❌ |
| View Own Profile | ✅ | ✅ | ✅ | ✅ |
| Create Department | ✅ | ✅ | ❌ | ❌ |
| View Departments | ✅ | ✅ | ✅ | ✅ |
| Update Department | ✅ | ✅ | ❌ | ❌ |
| Delete Department | ✅ | ❌ | ❌ | ❌ |

## 📝 Data Models

### Key Entities

**Employee**
```kotlin
data class Employee(
    val id: UUID,
    val employeeNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phoneNumber: String?,
    val companyId: UUID,
    val departmentId: UUID?,
    val gender: Gender?,
    val dateOfBirth: LocalDate?,
    val maritalStatus: MaritalStatus?,
    val contractDetails: ContractDetails?,
    val bankDetails: BankDetails?,
    val dependents: List<Dependent>?,
    val contactPersons: List<ContactPerson>?
)
```

**Department**
```kotlin
data class Department(
    val id: UUID,
    val name: String,
    val code: String,
    val description: String?,
    val companyId: UUID,
    val managerId: UUID?,
    val isActive: Boolean
)
```

### Enums

**Gender:** MALE, FEMALE, OTHER

**MaritalStatus:** SINGLE, MARRIED, DIVORCED, WIDOWED

**ContractStatus:** ACTIVE, INACTIVE, TERMINATED, SUSPENDED, PENDING

**ContractJobType:** FULL_TIME, PART_TIME, CONTRACT, INTERN, TEMPORARY

**ContractPeriodType:** PERMANENT, FIXED_TERM, PROBATION, SEASONAL

**SalaryType:** HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY

## 🧪 Testing

### Testing Tools
- See [API_TESTING_CHECKLIST.md](API_TESTING_CHECKLIST.md) for comprehensive test scenarios
- Includes 15 test categories with 100+ test cases
- Covers CRUD, search, filtering, pagination, validation, authorization, and edge cases

## 🔄 Kafka Integration

### Employee Creation Event

When an employee is created, the service publishes an event to Kafka:

**Topic:** `employee-events`

**Event Payload:**
```json
{
  "eventType": "EMPLOYEE_CREATED",
  "employeeId": "770e8400-e29b-41d4-a716-446655440000",
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "departmentId": "660e8400-e29b-41d4-a716-446655440000"
}
```

**Event Consumers:**
- Auth Service - Updates user record with employeeId
- Leave Service - Creates leave balances
- Other services requiring employee data

## 🔄 Integration with Other Services

### Auth Service Integration
```
Auth Service                    Employee Service
     │                                 │
     │ 1. User registers               │
     │ 2. Kafka: EMPLOYEE_CREATED      │
     │─────────────────────────────>  │
     │                                 │
     │                           3. Create employee
     │                           4. Return employeeId
     │ <─────────────────────────────│
     │                                 │
     │ 5. Update user.employeeId       │
```

### Leave Service Integration
- **Employee IDs** used for leave request creation
- **Department info** used for approval workflows
- **Manager assignments** determine approval chains

### Salary Advance Service Integration
- **Employee IDs** used for advance requests
- **Bank details** used for disbursement
- **Contract info** validates eligibility

## 📈 Business Rules

### Employee Management
✅ Employee number must be unique across system
✅ Email must be valid format if provided
✅ Employee must be assigned to a valid company
✅ Date of birth format: YYYY-MM-DD
✅ Department assignment is optional but recommended

### Department Management
✅ Department code must be unique within company
✅ Department name is required
✅ Manager must be an existing employee
✅ Departments can be marked inactive instead of deleted
✅ Company assignment is required

## 🔄 Workflow Examples

### Employee Creation Flow
```
1. Client sends POST /api/v1/employees
   └─> Validates required fields
   └─> Checks employee number uniqueness

2. Service creates employee record
   └─> Saves to database
   └─> Generates UUID

3. Publishes Kafka event
   └─> Topic: employee-events
   └─> Other services consume

4. Returns employee response
   └─> Status: 201 Created
   └─> Includes generated ID
```

### Department Creation Flow
```
1. Client sends POST /api/v1/departments
   └─> Validates required fields
   └─> Checks code uniqueness within company

2. Service creates department record
   └─> Saves to database
   └─> Links to company

3. Returns department response
   └─> Status: 201 Created
```

## 🐛 Common Issues & Solutions

### "Employee number already exists"
**Solution:** Use a unique employee number or check if employee is already registered

### "Department code already exists"
**Solution:** Use a unique department code within the company

### "Company ID is required"
**Solution:** Ensure valid companyId is provided in request

### "Access Denied" (403 Forbidden)
**Solution:**
- Check user has required role (HR, ADMIN, MANAGER)
- Verify JWT token is valid and not expired
- Ensure correct endpoint is being accessed

### "Employee not found" (404 Not Found)
**Solution:**
- Verify employee ID is correct
- Check employee hasn't been deleted
- Ensure employee exists in system

## 📖 Additional Resources

- **Full API Docs**: [EMPLOYEE_API_DOCUMENTATION.md](EMPLOYEE_API_DOCUMENTATION.md)
- **Testing Guide**: [API_TESTING_CHECKLIST.md](API_TESTING_CHECKLIST.md)
- **Quick Reference**: [API_QUICK_REFERENCE.md](API_QUICK_REFERENCE.md)

## 🤝 Contributing

1. Follow existing code patterns
2. Add tests for new features
3. Update documentation
4. Follow Kotlin coding conventions

## 🔒 Best Practices

### Employee Data
✅ Validate email addresses
✅ Use unique employee numbers
✅ Encrypt sensitive data (bank details)
✅ Maintain audit trail (createdAt, updatedAt)
✅ Link to departments for better organization

### Department Structure
✅ Use descriptive codes (ENG, HR, FIN)
✅ Assign managers to departments
✅ Mark inactive instead of deleting
✅ Maintain clear hierarchy

### API Usage
✅ Always include Authorization header
✅ Use pagination for large datasets
✅ Implement proper error handling
✅ Cache frequently accessed data
✅ Use HTTPS in production

## 📄 License

Part of the HRMS Backend System

---

**Version:** 1.0
**Last Updated:** October 12, 2025
**Service Port:** 8082
**Status:** Production Ready ✅
