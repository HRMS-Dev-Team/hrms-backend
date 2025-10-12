# Employee Management Service - Quick Reference

## Base URL
```
http://localhost:8082
```

## Authentication
All endpoints require JWT Bearer token:
```
Authorization: Bearer <token>
```

---

## Quick Endpoints Reference

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| **EMPLOYEES** |
| POST | `/api/v1/employees` | HR, ADMIN | Create employee |
| GET | `/api/v1/employees/{id}` | HR, ADMIN, MANAGER | Get by ID |
| GET | `/api/v1/employees/number/{number}` | HR, ADMIN, MANAGER | Get by employee number |
| GET | `/api/v1/employees` | HR, ADMIN, MANAGER | List all (paginated) |
| GET | `/api/v1/employees/company/{companyId}` | HR, ADMIN, MANAGER | Get by company |
| GET | `/api/v1/employees/search?name={name}` | HR, ADMIN, MANAGER | Search by name |
| PUT | `/api/v1/employees/{id}` | HR, ADMIN | Update employee |
| DELETE | `/api/v1/employees/{id}` | ADMIN | Delete employee |
| GET | `/api/v1/employees/me` | All | Get my profile (501) |
| **DEPARTMENTS** |
| POST | `/api/v1/departments` | HR, ADMIN | Create department |
| GET | `/api/v1/departments/{id}` | All | Get by ID |
| GET | `/api/v1/departments/code/{code}` | All | Get by code |
| GET | `/api/v1/departments` | HR, ADMIN, MANAGER | List all (paginated) |
| GET | `/api/v1/departments/company/{companyId}` | All | Get by company |
| GET | `/api/v1/departments/active` | All | Get all active |
| GET | `/api/v1/departments/company/{companyId}/active` | All | Get active by company |
| GET | `/api/v1/departments/search?name={name}` | All | Search by name |
| PUT | `/api/v1/departments/{id}` | HR, ADMIN | Update department |
| DELETE | `/api/v1/departments/{id}` | ADMIN | Delete department |

---

## Common Request Examples

### Create Employee
```bash
curl -X POST http://localhost:8082/api/v1/employees \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeNumber": "EMP001",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@company.com",
    "companyId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

### Get Employee by ID
```bash
curl -X GET http://localhost:8082/api/v1/employees/770e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer TOKEN"
```

### Search Employees
```bash
curl -X GET "http://localhost:8082/api/v1/employees/search?name=john" \
  -H "Authorization: Bearer TOKEN"
```

### Create Department
```bash
curl -X POST http://localhost:8082/api/v1/departments \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Engineering",
    "code": "ENG",
    "companyId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

### Update Employee
```bash
curl -X PUT http://localhost:8082/api/v1/employees/{id} \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jonathan",
    "email": "jonathan.doe@company.com"
  }'
```

---

## Enums Reference

### Gender
```
MALE | FEMALE | OTHER
```

### Marital Status
```
SINGLE | MARRIED | DIVORCED | WIDOWED
```

### Contract Status
```
ACTIVE | INACTIVE | TERMINATED | SUSPENDED | PENDING
```

### Contract Job Type
```
FULL_TIME | PART_TIME | CONTRACT | INTERN | TEMPORARY
```

### Contract Period Type
```
PERMANENT | FIXED_TERM | PROBATION | SEASONAL
```

### Salary Type
```
HOURLY | DAILY | WEEKLY | MONTHLY | YEARLY
```

---

## Request/Response Format

### Create Employee Request (Minimal)
```json
{
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "companyId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Create Employee Request (Complete)
```json
{
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phoneNumber": "+250788123456",
  "documentType": "NATIONAL_ID",
  "documentNumber": "1199780012345678",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "departmentId": "660e8400-e29b-41d4-a716-446655440000",
  "gender": "MALE",
  "dateOfBirth": "1990-05-15",
  "maritalStatus": "MARRIED",
  "contractDetails": {
    "contractStartDate": "2024-01-15",
    "contractStatus": "ACTIVE",
    "salaryType": "MONTHLY",
    "baseSalary": 1500000.00,
    "contractJobType": "FULL_TIME"
  },
  "bankDetails": {
    "accountNumber": "0001234567890",
    "accountNames": "John Doe"
  }
}
```

### Employee Response
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "departmentId": "660e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

### Create Department Request
```json
{
  "name": "Engineering",
  "code": "ENG",
  "description": "Engineering Department",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "managerId": "770e8400-e29b-41d4-a716-446655440000",
  "isActive": true
}
```

### Department Response
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "name": "Engineering",
  "code": "ENG",
  "description": "Engineering Department",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "managerId": "770e8400-e29b-41d4-a716-446655440000",
  "isActive": true,
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

### Error Response
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Employee number already exists",
  "path": "/api/v1/employees"
}
```

---

## Validation Rules

| Field | Rule |
|-------|------|
| employeeNumber | Required, unique |
| firstName | Required |
| lastName | Required |
| email | Optional, must be valid email |
| companyId | Required UUID |
| dateOfBirth | Format: YYYY-MM-DD |
| department.name | Required |
| department.code | Required, unique within company |

---

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Success |
| 201 | Created - Resource created |
| 204 | No Content - Deletion successful |
| 400 | Bad Request - Validation error |
| 401 | Unauthorized - Invalid/missing token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error |
| 501 | Not Implemented - Feature pending |

---

## Pagination Parameters

```
?page=0&size=20&sort=firstName,asc
```

| Parameter | Description | Default |
|-----------|-------------|---------|
| page | Page number (0-indexed) | 0 |
| size | Items per page | 20 |
| sort | Sort field and direction | unsorted |

### Sort Examples
```
sort=firstName,asc
sort=lastName,desc
sort=employeeNumber,asc
sort=createdAt,desc
```

---

## Search Parameters

### Employee Search
```
?name=john              # Searches firstName and lastName
```

### Department Search
```
?name=eng               # Searches department name
```

**Features:**
- Case-insensitive
- Partial match (contains)
- Searches multiple fields

---

## Role-Based Access

### ADMIN
✅ All employee operations (including delete)
✅ All department operations (including delete)

### HR
✅ Create, read, update employees
✅ Create, read, update departments
❌ Cannot delete resources

### MANAGER
✅ Read employees
✅ Read departments
❌ Cannot create, update, or delete

### EMPLOYEE
✅ Read own profile (/me)
❌ Cannot access other employees
✅ Read departments

---

## Common Nested Objects

### Contract Details
```json
{
  "contractStartDate": "2024-01-15",
  "contractEndDate": "2025-01-14",
  "contractStatus": "ACTIVE",
  "salaryType": "MONTHLY",
  "baseSalary": 1500000.00,
  "contractJobType": "FULL_TIME",
  "contractPeriodType": "FIXED_TERM"
}
```

### Bank Details
```json
{
  "bankId": "880e8400-e29b-41d4-a716-446655440000",
  "accountNumber": "0001234567890",
  "accountNames": "John Doe",
  "referenceCode": "REF123"
}
```

### Dependents
```json
[
  {
    "fullNames": "Jane Doe",
    "gender": "FEMALE",
    "dateOfBirth": "2015-03-10",
    "relationshipType": "DAUGHTER",
    "isDependent": true
  }
]
```

### Contact Persons
```json
[
  {
    "fullNames": "Sarah Smith",
    "email": "sarah.smith@email.com",
    "phoneNumber": "+250788654321",
    "relationshipType": "MOTHER"
  }
]
```

---

## Common Error Messages

### Employee Errors
- `"Employee number is required"`
- `"First name is required"`
- `"Last name is required"`
- `"Company ID is required"`
- `"Email should be valid"`
- `"Employee number already exists"`
- `"Employee not found with id: ..."`

### Department Errors
- `"Department name is required"`
- `"Department code is required"`
- `"Company ID is required"`
- `"Department code already exists"`
- `"Department not found with id: ..."`

### Authorization Errors
- `"Access Denied"` (403 Forbidden)
- `"Unauthorized"` (401 Unauthorized)

---

## Quick Tips

### Creating Employees
✅ Ensure unique employee numbers
✅ Validate email before submission
✅ Use correct date format (YYYY-MM-DD)
✅ Assign to existing company and department

### Creating Departments
✅ Use descriptive codes (ENG, HR, FIN)
✅ Assign valid manager IDs
✅ Keep codes unique within company

### Searching
✅ Use partial names for broader results
✅ Search is case-insensitive
✅ Returns empty array if no matches

### Updating
✅ Only include fields you want to change
✅ Unspecified fields remain unchanged
✅ updatedAt timestamp auto-updates

---

## Postman Environment Variables

```json
{
  "base_url": "http://localhost:8082",
  "jwt_token": "YOUR_JWT_TOKEN",
  "employee_id": "770e8400-e29b-41d4-a716-446655440000",
  "department_id": "660e8400-e29b-41d4-a716-446655440000",
  "company_id": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## Database Tables

```
employees            - Employee records
departments          - Department structure
employee_dependents  - Employee dependent information
employee_contacts    - Emergency contacts
```

---

## Integration Points

### Auth Service
- JWT token validation
- Role-based authorization
- Employee-user account linking

### Leave Service
- Uses employee IDs for leave requests
- Department information for approvals

### Salary Advance Service
- Uses employee IDs for advance requests
- Bank details for disbursement

---

## Testing Quick Start

```bash
# 1. Create employee
curl -X POST http://localhost:8082/api/v1/employees \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"employeeNumber":"EMP001","firstName":"John","lastName":"Doe","companyId":"550e8400-e29b-41d4-a716-446655440000"}'

# 2. Copy employee ID from response

# 3. Get employee
curl -X GET http://localhost:8082/api/v1/employees/{employee_id} \
  -H "Authorization: Bearer TOKEN"

# 4. Search employees
curl -X GET "http://localhost:8082/api/v1/employees/search?name=john" \
  -H "Authorization: Bearer TOKEN"
```

---

## Kafka Events

### Employee Created
**Topic:** `employee-events`

**Event:**
```json
{
  "eventType": "EMPLOYEE_CREATED",
  "employeeId": "770e8400-e29b-41d4-a716-446655440000",
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "companyId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

**Last Updated:** October 12, 2025
**Version:** 1.0
**Service Port:** 8082
