# Employee Management Service - API Documentation

**Base URL:** `http://localhost:8082` (or your configured port)

**Version:** 1.0

**Description:** Comprehensive employee and department management service for the HRMS system with full CRUD operations, Kafka event integration, and role-based access control

---

## Table of Contents
1. [Employee Management API](#employee-management-api)
2. [Department Management API](#department-management-api)
3. [Data Models](#data-models)
4. [Security & Authorization](#security--authorization)

---

## Employee Management API

All employee endpoints require authentication via JWT Bearer token:
```
Authorization: Bearer <access_token>
```

### 1. Create Employee
**Endpoint:** `POST /api/v1/employees`

**Authorization:** HR, ADMIN roles

**Description:** Create a new employee record in the system

**Request Body:**
```json
{
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phoneNumber": "+250788123456",
  "documentType": "NATIONAL_ID",
  "documentNumber": "1199780012345678",
  "fatherNames": "Michael Doe",
  "motherNames": "Sarah Smith",
  "rssbNumber": "RSSB123456",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "companyName": "Acme Corporation",
  "departmentId": "660e8400-e29b-41d4-a716-446655440000",
  "nationality": "Rwandan",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "maritalStatus": "MARRIED",
  "country": "Rwanda",
  "address": "KG 123 St, Kigali",
  "dependents": [
    {
      "fullNames": "Jane Doe",
      "gender": "FEMALE",
      "dateOfBirth": "2015-03-10",
      "relationshipType": "DAUGHTER",
      "isDependent": true
    }
  ],
  "contactPersons": [
    {
      "fullNames": "Sarah Smith",
      "email": "sarah.smith@email.com",
      "phoneNumber": "+250788654321",
      "relationshipType": "MOTHER"
    }
  ],
  "contractDetails": {
    "contractStartDate": "2024-01-15",
    "contractEndDate": "2025-01-14",
    "contractStatus": "ACTIVE",
    "isInProbationPeriod": false,
    "salaryType": "MONTHLY",
    "baseSalary": 1500000.00,
    "contractJobType": "FULL_TIME",
    "contractPeriodType": "FIXED_TERM",
    "supervisorId": "770e8400-e29b-41d4-a716-446655440000",
    "positionCode": "SE001"
  },
  "bankDetails": {
    "accountNumber": "0001234567890",
    "accountNames": "John Doe",
    "bankId": "880e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Validation Rules:**
- `employeeNumber`: Required, must be unique
- `firstName`: Required
- `lastName`: Required
- `email`: Optional, must be valid email format if provided
- `companyId`: Required UUID
- `dateOfBirth`: Optional, format: YYYY-MM-DD

**Available Enums:**

**Gender:**
```
MALE, FEMALE, OTHER
```

**MaritalStatus:**
```
SINGLE, MARRIED, DIVORCED, WIDOWED
```

**ContractStatus:**
```
ACTIVE, INACTIVE, TERMINATED, SUSPENDED, PENDING
```

**ContractJobType:**
```
FULL_TIME, PART_TIME, CONTRACT, INTERN, TEMPORARY
```

**ContractPeriodType:**
```
PERMANENT, FIXED_TERM, PROBATION, SEASONAL
```

**SalaryType:**
```
HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
```

**Response:** `201 Created`
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phoneNumber": "+250788123456",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "companyName": "Acme Corporation",
  "departmentId": "660e8400-e29b-41d4-a716-446655440000",
  "departmentName": "Engineering",
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

**Error Responses:**

**400 Bad Request** - Validation Error
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Employee number already exists",
  "path": "/api/v1/employees"
}
```

**Common Error Messages:**
- "Employee number is required"
- "First name is required"
- "Last name is required"
- "Company ID is required"
- "Email should be valid"
- "Employee number already exists"

---

### 2. Get Employee by ID
**Endpoint:** `GET /api/v1/employees/{id}`

**Authorization:** HR, ADMIN, MANAGER roles

**Description:** Retrieve employee details by employee ID

**Path Parameters:**
- `id` - Employee UUID

**Response:** `200 OK`
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phoneNumber": "+250788123456",
  "documentType": "NATIONAL_ID",
  "documentNumber": "1199780012345678",
  "fatherNames": "Michael Doe",
  "motherNames": "Sarah Smith",
  "rssbNumber": "RSSB123456",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "companyName": "Acme Corporation",
  "departmentId": "660e8400-e29b-41d4-a716-446655440000",
  "departmentName": "Engineering",
  "nationality": "Rwandan",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "maritalStatus": "MARRIED",
  "country": "Rwanda",
  "address": "KG 123 St, Kigali",
  "dependents": [...],
  "contactPersons": [...],
  "contractDetails": {...},
  "bankDetails": {...},
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

**Error Response:**

**404 Not Found**
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 770e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/employees/770e8400-e29b-41d4-a716-446655440000"
}
```

---

### 3. Get Employee by Employee Number
**Endpoint:** `GET /api/v1/employees/number/{employeeNumber}`

**Authorization:** HR, ADMIN, MANAGER roles

**Description:** Retrieve employee details by employee number

**Path Parameters:**
- `employeeNumber` - Employee's unique number (e.g., EMP001)

**Response:** `200 OK`
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  ...
}
```

**Error Response:** Same as "Get Employee by ID"

---

### 4. Get All Employees (Paginated)
**Endpoint:** `GET /api/v1/employees`

**Authorization:** HR, ADMIN, MANAGER roles

**Description:** Retrieve all employees with pagination and sorting

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)
- `sort` - Sort field and direction (e.g., "firstName,asc")

**Example Request:**
```
GET /api/v1/employees?page=0&size=20&sort=firstName,asc
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "770e8400-e29b-41d4-a716-446655440000",
      "employeeNumber": "EMP001",
      "firstName": "John",
      "lastName": "Doe",
      ...
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 150,
  "totalPages": 8,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 20,
  "empty": false
}
```

---

### 5. Get Employees by Company
**Endpoint:** `GET /api/v1/employees/company/{companyId}`

**Authorization:** HR, ADMIN, MANAGER roles

**Description:** Retrieve all employees for a specific company

**Path Parameters:**
- `companyId` - Company UUID

**Response:** `200 OK`
```json
[
  {
    "id": "770e8400-e29b-41d4-a716-446655440000",
    "employeeNumber": "EMP001",
    "firstName": "John",
    "lastName": "Doe",
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    ...
  }
]
```

---

### 6. Search Employees by Name
**Endpoint:** `GET /api/v1/employees/search`

**Authorization:** HR, ADMIN, MANAGER roles

**Description:** Search employees by first name or last name (case-insensitive, partial match)

**Query Parameters:**
- `name` - Search term (searches both firstName and lastName)

**Example Request:**
```
GET /api/v1/employees/search?name=john
```

**Response:** `200 OK`
```json
[
  {
    "id": "770e8400-e29b-41d4-a716-446655440000",
    "employeeNumber": "EMP001",
    "firstName": "John",
    "lastName": "Doe",
    ...
  },
  {
    "id": "880e8400-e29b-41d4-a716-446655440001",
    "employeeNumber": "EMP025",
    "firstName": "Johnny",
    "lastName": "Smith",
    ...
  }
]
```

---

### 7. Update Employee
**Endpoint:** `PUT /api/v1/employees/{id}`

**Authorization:** HR, ADMIN roles

**Description:** Update employee information (all fields optional)

**Path Parameters:**
- `id` - Employee UUID

**Request Body:**
```json
{
  "firstName": "Jonathan",
  "phoneNumber": "+250788999888",
  "email": "jonathan.doe@company.com",
  "departmentId": "660e8400-e29b-41d4-a716-446655440001",
  "address": "KG 456 St, Kigali",
  "maritalStatus": "SINGLE",
  "contractDetails": {
    "contractStatus": "ACTIVE",
    "baseSalary": 1800000.00
  }
}
```

**Note:** All fields are optional. Only provided fields will be updated.

**Response:** `200 OK`
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "employeeNumber": "EMP001",
  "firstName": "Jonathan",
  "lastName": "Doe",
  "phoneNumber": "+250788999888",
  "email": "jonathan.doe@company.com",
  "updatedAt": "2025-10-12T14:30:00",
  ...
}
```

**Error Response:**

**404 Not Found** - Employee does not exist
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 770e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/employees/770e8400-e29b-41d4-a716-446655440000"
}
```

---

### 8. Delete Employee
**Endpoint:** `DELETE /api/v1/employees/{id}`

**Authorization:** ADMIN role only

**Description:** Delete an employee from the system (soft delete recommended)

**Path Parameters:**
- `id` - Employee UUID

**Response:** `204 No Content`

**Error Response:**

**404 Not Found**
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 770e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/employees/770e8400-e29b-41d4-a716-446655440000"
}
```

**403 Forbidden** - Insufficient permissions
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/employees/770e8400-e29b-41d4-a716-446655440000"
}
```

---

### 9. Get My Profile
**Endpoint:** `GET /api/v1/employees/me`

**Authorization:** All authenticated users

**Description:** Get the profile of the currently authenticated employee

**Response:** `200 OK`
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  ...
}
```

**Note:** Currently returns 501 Not Implemented. Implementation requires extracting employeeId from JWT token.

**Error Response:**

**501 Not Implemented**
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 501,
  "error": "Not Implemented",
  "message": "Get my profile endpoint is not yet implemented",
  "path": "/api/v1/employees/me"
}
```

---

## Department Management API

All department endpoints require authentication via JWT Bearer token:
```
Authorization: Bearer <access_token>
```

### 1. Create Department
**Endpoint:** `POST /api/v1/departments`

**Authorization:** HR, ADMIN roles

**Description:** Create a new department within a company

**Request Body:**
```json
{
  "name": "Engineering",
  "code": "ENG",
  "description": "Software and Hardware Engineering Department",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "managerId": "770e8400-e29b-41d4-a716-446655440000",
  "isActive": true
}
```

**Validation Rules:**
- `name`: Required
- `code`: Required, must be unique within company
- `companyId`: Required UUID
- `isActive`: Optional, defaults to true

**Response:** `201 Created`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "name": "Engineering",
  "code": "ENG",
  "description": "Software and Hardware Engineering Department",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "managerId": "770e8400-e29b-41d4-a716-446655440000",
  "isActive": true,
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

**Error Responses:**

**400 Bad Request**
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Department code already exists",
  "path": "/api/v1/departments"
}
```

**Common Error Messages:**
- "Department name is required"
- "Department code is required"
- "Company ID is required"
- "Department code already exists"

---

### 2. Get Department by ID
**Endpoint:** `GET /api/v1/departments/{id}`

**Authorization:** All authenticated users

**Description:** Retrieve department details by ID

**Path Parameters:**
- `id` - Department UUID

**Response:** `200 OK`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "name": "Engineering",
  "code": "ENG",
  "description": "Software and Hardware Engineering Department",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "managerId": "770e8400-e29b-41d4-a716-446655440000",
  "isActive": true,
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

**Error Response:**

**404 Not Found**
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Department not found with id: 660e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/departments/660e8400-e29b-41d4-a716-446655440000"
}
```

---

### 3. Get Department by Code
**Endpoint:** `GET /api/v1/departments/code/{code}`

**Authorization:** All authenticated users

**Description:** Retrieve department details by department code

**Path Parameters:**
- `code` - Department code (e.g., ENG, HR, FIN)

**Response:** `200 OK`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "name": "Engineering",
  "code": "ENG",
  ...
}
```

---

### 4. Get All Departments (Paginated)
**Endpoint:** `GET /api/v1/departments`

**Authorization:** HR, ADMIN, MANAGER roles

**Description:** Retrieve all departments with pagination and sorting

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)
- `sort` - Sort field and direction (e.g., "name,asc")

**Example Request:**
```
GET /api/v1/departments?page=0&size=20&sort=name,asc
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440000",
      "name": "Engineering",
      "code": "ENG",
      ...
    }
  ],
  "totalElements": 25,
  "totalPages": 2,
  "size": 20,
  "number": 0
}
```

---

### 5. Get Departments by Company
**Endpoint:** `GET /api/v1/departments/company/{companyId}`

**Authorization:** All authenticated users

**Description:** Retrieve all departments for a specific company

**Path Parameters:**
- `companyId` - Company UUID

**Response:** `200 OK`
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440000",
    "name": "Engineering",
    "code": "ENG",
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    ...
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "name": "Human Resources",
    "code": "HR",
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    ...
  }
]
```

---

### 6. Get Active Departments
**Endpoint:** `GET /api/v1/departments/active`

**Authorization:** All authenticated users

**Description:** Retrieve all active departments across all companies

**Response:** `200 OK`
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440000",
    "name": "Engineering",
    "code": "ENG",
    "isActive": true,
    ...
  }
]
```

---

### 7. Get Active Departments by Company
**Endpoint:** `GET /api/v1/departments/company/{companyId}/active`

**Authorization:** All authenticated users

**Description:** Retrieve all active departments for a specific company

**Path Parameters:**
- `companyId` - Company UUID

**Response:** `200 OK`
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440000",
    "name": "Engineering",
    "code": "ENG",
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    "isActive": true,
    ...
  }
]
```

---

### 8. Search Departments by Name
**Endpoint:** `GET /api/v1/departments/search`

**Authorization:** All authenticated users

**Description:** Search departments by name (case-insensitive, partial match)

**Query Parameters:**
- `name` - Search term

**Example Request:**
```
GET /api/v1/departments/search?name=eng
```

**Response:** `200 OK`
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440000",
    "name": "Engineering",
    "code": "ENG",
    ...
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440002",
    "name": "Engine Maintenance",
    "code": "ENG_MNT",
    ...
  }
]
```

---

### 9. Update Department
**Endpoint:** `PUT /api/v1/departments/{id}`

**Authorization:** HR, ADMIN roles

**Description:** Update department information (all fields optional)

**Path Parameters:**
- `id` - Department UUID

**Request Body:**
```json
{
  "name": "Software Engineering",
  "description": "Updated description for Software Engineering",
  "managerId": "770e8400-e29b-41d4-a716-446655440001",
  "isActive": true
}
```

**Response:** `200 OK`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "name": "Software Engineering",
  "code": "ENG",
  "description": "Updated description for Software Engineering",
  "managerId": "770e8400-e29b-41d4-a716-446655440001",
  "updatedAt": "2025-10-12T14:30:00",
  ...
}
```

---

### 10. Delete Department
**Endpoint:** `DELETE /api/v1/departments/{id}`

**Authorization:** ADMIN role only

**Description:** Delete a department from the system

**Path Parameters:**
- `id` - Department UUID

**Response:** `204 No Content`

**Error Response:**

**404 Not Found**
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Department not found with id: 660e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/departments/660e8400-e29b-41d4-a716-446655440000"
}
```

---

## Data Models

### CreateEmployeeRequest
```typescript
{
  employeeNumber: string;           // Required, unique
  firstName: string;                // Required
  lastName: string;                 // Required
  email?: string;                   // Optional, valid email
  phoneNumber?: string;
  documentType?: string;
  documentNumber?: string;
  fatherNames?: string;
  motherNames?: string;
  rssbNumber?: string;
  companyId: UUID;                  // Required
  companyName?: string;
  departmentId?: UUID;
  nationality?: string;
  dateOfBirth?: string;             // Format: YYYY-MM-DD
  gender?: Gender;                  // MALE, FEMALE, OTHER
  maritalStatus?: MaritalStatus;    // SINGLE, MARRIED, DIVORCED, WIDOWED
  country?: string;
  address?: string;
  dependents?: DependentDto[];
  profilePicture?: UUID;
  contactPersons?: ContactPersonDto[];
  contractDetails?: ContractDetailsDto;
  bankDetails?: BankDetailsDto;
}
```

### UpdateEmployeeRequest
```typescript
{
  // All fields optional, same structure as CreateEmployeeRequest
}
```

### EmployeeResponse
```typescript
{
  id: UUID;
  employeeNumber: string;
  firstName: string;
  lastName: string;
  email?: string;
  phoneNumber?: string;
  documentType?: string;
  documentNumber?: string;
  companyId: UUID;
  companyName?: string;
  departmentId?: UUID;
  departmentName?: string;
  nationality?: string;
  dateOfBirth?: string;
  gender?: Gender;
  maritalStatus?: MaritalStatus;
  country?: string;
  address?: string;
  dependents?: DependentDto[];
  profilePicture?: UUID;
  contactPersons?: ContactPersonDto[];
  contractDetails?: ContractDetailsDto;
  bankDetails?: BankDetailsDto;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
}
```

### ContractDetailsDto
```typescript
{
  contractDocumentId?: UUID;
  contractStartDate?: string;       // Format: YYYY-MM-DD
  contractEndDate?: string;
  contractStatus?: ContractStatus;  // ACTIVE, INACTIVE, TERMINATED, etc.
  isInProbationPeriod?: boolean;
  probationStartDate?: string;
  probationEndDate?: string;
  probationPeriodRemarks?: string;
  salaryType?: SalaryType;          // HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
  salaryPeriodCyclePeriod?: number;
  baseSalary?: BigDecimal;
  salaryCurrency?: UUID;
  levelId?: UUID;
  roles?: UUID[];
  contractJobType?: ContractJobType;  // FULL_TIME, PART_TIME, CONTRACT, etc.
  contractPeriodType?: ContractPeriodType;  // PERMANENT, FIXED_TERM, etc.
  supervisorId?: UUID;
  isBadgingMember?: boolean;
  positionCode?: string;
  positionId?: UUID;
  levelCode?: string;
}
```

### BankDetailsDto
```typescript
{
  bankId?: UUID;
  accountNumber?: string;
  accountNames?: string;
  referenceCode?: string;
}
```

### CreateDepartmentRequest
```typescript
{
  name: string;                     // Required
  code: string;                     // Required, unique within company
  description?: string;
  companyId: UUID;                  // Required
  managerId?: UUID;
  isActive?: boolean;               // Default: true
}
```

### DepartmentResponse
```typescript
{
  id: UUID;
  name: string;
  code: string;
  description?: string;
  companyId: UUID;
  managerId?: UUID;
  isActive: boolean;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
}
```

---

## Security & Authorization

### Role-Based Access Control

| Endpoint | Required Role |
|----------|--------------|
| **Employees** |
| Create Employee | HR, ADMIN |
| Get Employee by ID | HR, ADMIN, MANAGER |
| Get Employee by Number | HR, ADMIN, MANAGER |
| Get All Employees | HR, ADMIN, MANAGER |
| Get Employees by Company | HR, ADMIN, MANAGER |
| Search Employees | HR, ADMIN, MANAGER |
| Update Employee | HR, ADMIN |
| Delete Employee | ADMIN only |
| Get My Profile | All authenticated |
| **Departments** |
| Create Department | HR, ADMIN |
| Get Department by ID | All authenticated |
| Get Department by Code | All authenticated |
| Get All Departments | HR, ADMIN, MANAGER |
| Get Departments by Company | All authenticated |
| Get Active Departments | All authenticated |
| Get Active Departments by Company | All authenticated |
| Search Departments | All authenticated |
| Update Department | HR, ADMIN |
| Delete Department | ADMIN only |

### Role Hierarchy
```
ADMIN > HR > MANAGER > EMPLOYEE
```

**ADMIN:**
- Full access to all endpoints
- Can delete employees and departments
- Complete system management

**HR:**
- Create, read, and update employees
- Create, read, and update departments
- Cannot delete resources

**MANAGER:**
- Read-only access to employees
- Read access to departments
- Can view team members and department structure

**EMPLOYEE:**
- Can view own profile
- Read access to departments

---

## Kafka Integration

### Employee Creation Event

When an employee is created, the service publishes events to Kafka for other services to consume.

**Topic:** `employee-events`

**Event Example:**
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

**Consumers:** Other HRMS services (Leave, Salary Advance, etc.)

---

## Testing Examples

### Using cURL

**1. Create Employee**
```bash
curl -X POST http://localhost:8082/api/v1/employees \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeNumber": "EMP001",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@company.com",
    "companyId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**2. Get Employee by ID**
```bash
curl -X GET http://localhost:8082/api/v1/employees/770e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**3. Search Employees**
```bash
curl -X GET "http://localhost:8082/api/v1/employees/search?name=john" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**4. Create Department**
```bash
curl -X POST http://localhost:8082/api/v1/departments \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Engineering",
    "code": "ENG",
    "description": "Engineering Department",
    "companyId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**5. Get Departments by Company**
```bash
curl -X GET http://localhost:8082/api/v1/departments/company/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

### Using JavaScript/Axios

**Create Employee:**
```javascript
const createEmployee = async (employeeData) => {
  try {
    const response = await axios.post(
      'http://localhost:8082/api/v1/employees',
      employeeData,
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('access_token')}`
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('Failed to create employee:', error.response.data.message);
    throw error;
  }
};
```

**Search Employees:**
```javascript
const searchEmployees = async (searchTerm) => {
  try {
    const response = await axios.get(
      `http://localhost:8082/api/v1/employees/search?name=${searchTerm}`,
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('access_token')}`
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('Search failed:', error.response.data.message);
    throw error;
  }
};
```

---

## Error Codes Summary

| Code | Meaning | Common Causes |
|------|---------|---------------|
| 200 | OK | Successful request |
| 201 | Created | Employee/Department created successfully |
| 204 | No Content | Deletion successful |
| 400 | Bad Request | Validation error, invalid input, duplicate employee number |
| 401 | Unauthorized | Invalid/missing JWT token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Employee/Department not found |
| 500 | Internal Server Error | Server-side error |
| 501 | Not Implemented | Endpoint not yet implemented |

---

## Common Issues & Solutions

### Issue: "Employee number already exists"
**Solution:** Use a unique employee number or check if employee is already registered

### Issue: "Department code already exists"
**Solution:** Use a unique department code within the company

### Issue: "Company ID is required"
**Solution:** Ensure companyId is provided in the request body

### Issue: "Access Denied" (403 Forbidden)
**Solution:**
- Check user has required role (HR, ADMIN, MANAGER)
- Verify JWT token is valid and not expired
- Ensure correct endpoint is being accessed

### Issue: "Employee not found" (404 Not Found)
**Solution:**
- Verify employee ID is correct
- Check employee hasn't been deleted
- Ensure employee exists in the system

---

## Best Practices

### Employee Management
✅ Always provide unique employee numbers
✅ Validate email addresses before submission
✅ Include department assignment for better organization
✅ Store sensitive data securely (bank details, salaries)
✅ Use proper date formats (YYYY-MM-DD)

### Department Management
✅ Use descriptive department codes (ENG, HR, FIN)
✅ Assign department managers
✅ Keep department structure organized
✅ Mark inactive departments instead of deleting

### API Usage
✅ Always include Authorization header
✅ Use pagination for large datasets
✅ Implement proper error handling
✅ Cache frequently accessed data
✅ Use HTTPS in production

---

**Documentation Version:** 1.0
**Last Updated:** October 12, 2025
**Service Port:** 8082
