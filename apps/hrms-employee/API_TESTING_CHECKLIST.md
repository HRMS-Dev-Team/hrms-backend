# Employee Management Service - API Testing Checklist

## Prerequisites
- [ ] Database is running and migrations are applied
- [ ] Auth service is running (for JWT token generation)
- [ ] Employee service is running on port 8082
- [ ] You have valid JWT tokens with different roles (ADMIN, HR, MANAGER, EMPLOYEE)

---

## 1. Employee Management - Basic CRUD

### Setup Test Data
```json
{
  "employeeNumber": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phoneNumber": "+250788123456",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "departmentId": "660e8400-e29b-41d4-a716-446655440000",
  "gender": "MALE",
  "dateOfBirth": "1990-05-15"
}
```

### Tests
- [ ] **Create Employee** - `POST /api/v1/employees`
  - Expected: 201 Created with employee ID
  - Save `employeeId` for subsequent tests
  - Verify: Response contains all provided fields

- [ ] **Get Employee by ID** - `GET /api/v1/employees/{id}`
  - Expected: 200 OK with complete employee details
  - Verify: All fields match creation data

- [ ] **Get Employee by Employee Number** - `GET /api/v1/employees/number/EMP001`
  - Expected: 200 OK with matching employee
  - Verify: Employee number matches

- [ ] **Get All Employees (Paginated)** - `GET /api/v1/employees?page=0&size=20`
  - Expected: 200 OK with paginated results
  - Verify: Response includes pagination metadata
  - Verify: Created employee appears in results

- [ ] **Update Employee** - `PUT /api/v1/employees/{id}`
  ```json
  {
    "firstName": "Jonathan",
    "phoneNumber": "+250788999888",
    "email": "jonathan.doe@company.com"
  }
  ```
  - Expected: 200 OK with updated data
  - Verify: Only specified fields are updated
  - Verify: updatedAt timestamp changed

---

## 2. Department Management - Basic CRUD

### Setup Test Data
```json
{
  "name": "Engineering",
  "code": "ENG",
  "description": "Software Engineering Department",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "isActive": true
}
```

### Tests
- [ ] **Create Department** - `POST /api/v1/departments`
  - Expected: 201 Created with department ID
  - Save `departmentId` for subsequent tests

- [ ] **Get Department by ID** - `GET /api/v1/departments/{id}`
  - Expected: 200 OK with complete department details

- [ ] **Get Department by Code** - `GET /api/v1/departments/code/ENG`
  - Expected: 200 OK with matching department

- [ ] **Get All Departments (Paginated)** - `GET /api/v1/departments?page=0&size=20`
  - Expected: 200 OK with paginated results

- [ ] **Update Department** - `PUT /api/v1/departments/{id}`
  ```json
  {
    "name": "Software Engineering",
    "description": "Updated description"
  }
  ```
  - Expected: 200 OK with updated data

---

## 3. Employee Complex Data - Nested Objects

### Test Contract Details
- [ ] **Create Employee with Contract** - `POST /api/v1/employees`
  ```json
  {
    "employeeNumber": "EMP002",
    "firstName": "Jane",
    "lastName": "Smith",
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    "contractDetails": {
      "contractStartDate": "2024-01-15",
      "contractEndDate": "2025-01-14",
      "contractStatus": "ACTIVE",
      "salaryType": "MONTHLY",
      "baseSalary": 1500000.00,
      "contractJobType": "FULL_TIME",
      "contractPeriodType": "FIXED_TERM"
    }
  }
  ```
  - Expected: 201 Created
  - Verify: contractDetails saved correctly

### Test Bank Details
- [ ] **Create Employee with Bank Details**
  ```json
  {
    "employeeNumber": "EMP003",
    "firstName": "Mike",
    "lastName": "Johnson",
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    "bankDetails": {
      "accountNumber": "0001234567890",
      "accountNames": "Mike Johnson",
      "bankId": "880e8400-e29b-41d4-a716-446655440000"
    }
  }
  ```
  - Expected: 201 Created
  - Verify: bankDetails saved correctly

### Test Dependents
- [ ] **Create Employee with Dependents**
  ```json
  {
    "employeeNumber": "EMP004",
    "firstName": "Sarah",
    "lastName": "Williams",
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    "dependents": [
      {
        "fullNames": "Emily Williams",
        "gender": "FEMALE",
        "dateOfBirth": "2015-03-10",
        "relationshipType": "DAUGHTER",
        "isDependent": true
      }
    ]
  }
  ```
  - Expected: 201 Created
  - Verify: dependents array saved correctly

### Test Contact Persons
- [ ] **Create Employee with Contact Persons**
  ```json
  {
    "employeeNumber": "EMP005",
    "firstName": "David",
    "lastName": "Brown",
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    "contactPersons": [
      {
        "fullNames": "Mary Brown",
        "email": "mary.brown@email.com",
        "phoneNumber": "+250788654321",
        "relationshipType": "SPOUSE"
      }
    ]
  }
  ```
  - Expected: 201 Created
  - Verify: contactPersons array saved correctly

---

## 4. Search and Filter Tests

### Employee Search
- [ ] **Search by First Name** - `GET /api/v1/employees/search?name=john`
  - Expected: 200 OK with matching employees
  - Verify: Results include "John", "Jonathan", etc.

- [ ] **Search by Last Name** - `GET /api/v1/employees/search?name=doe`
  - Expected: 200 OK with matching employees

- [ ] **Search Case Insensitive** - `GET /api/v1/employees/search?name=JOHN`
  - Expected: Same results as lowercase search

- [ ] **Search No Results** - `GET /api/v1/employees/search?name=xyz999`
  - Expected: 200 OK with empty array

### Department Search
- [ ] **Search Departments by Name** - `GET /api/v1/departments/search?name=eng`
  - Expected: 200 OK with matching departments
  - Verify: Results include "Engineering", etc.

### Filter by Company
- [ ] **Get Employees by Company** - `GET /api/v1/employees/company/{companyId}`
  - Expected: 200 OK with company employees only
  - Verify: All employees have matching companyId

- [ ] **Get Departments by Company** - `GET /api/v1/departments/company/{companyId}`
  - Expected: 200 OK with company departments only

### Filter Active Departments
- [ ] **Get All Active Departments** - `GET /api/v1/departments/active`
  - Expected: 200 OK with active departments only
  - Verify: All have isActive = true

- [ ] **Get Active Departments by Company** - `GET /api/v1/departments/company/{companyId}/active`
  - Expected: 200 OK with active departments for specific company

---

## 5. Pagination Tests

### Employee Pagination
- [ ] **First Page** - `GET /api/v1/employees?page=0&size=10`
  - Expected: 200 OK
  - Verify: Returns max 10 employees
  - Verify: Pagination metadata (totalElements, totalPages, first, last)

- [ ] **Second Page** - `GET /api/v1/employees?page=1&size=10`
  - Expected: 200 OK
  - Verify: Different employees than first page

- [ ] **Custom Page Size** - `GET /api/v1/employees?page=0&size=5`
  - Expected: Returns max 5 employees

### Department Pagination
- [ ] **Paginated Departments** - `GET /api/v1/departments?page=0&size=10`
  - Expected: 200 OK with pagination metadata

---

## 6. Sorting Tests

### Employee Sorting
- [ ] **Sort by First Name Ascending** - `GET /api/v1/employees?sort=firstName,asc`
  - Expected: Employees sorted alphabetically by first name

- [ ] **Sort by First Name Descending** - `GET /api/v1/employees?sort=firstName,desc`
  - Expected: Employees sorted reverse alphabetically

- [ ] **Sort by Employee Number** - `GET /api/v1/employees?sort=employeeNumber,asc`
  - Expected: Employees sorted by employee number

### Department Sorting
- [ ] **Sort by Name** - `GET /api/v1/departments?sort=name,asc`
  - Expected: Departments sorted alphabetically

- [ ] **Sort by Code** - `GET /api/v1/departments?sort=code,asc`
  - Expected: Departments sorted by code

---

## 7. Validation Tests

### Employee Validation
- [ ] **Missing Required Fields**
  - Try creating employee without employeeNumber
  - Expected: 400 Bad Request - "Employee number is required"

- [ ] **Missing First Name**
  - Expected: 400 Bad Request - "First name is required"

- [ ] **Missing Last Name**
  - Expected: 400 Bad Request - "Last name is required"

- [ ] **Missing Company ID**
  - Expected: 400 Bad Request - "Company ID is required"

- [ ] **Invalid Email Format**
  ```json
  {"email": "not-an-email"}
  ```
  - Expected: 400 Bad Request - "Email should be valid"

- [ ] **Duplicate Employee Number**
  - Try creating employee with existing employeeNumber
  - Expected: 400 Bad Request - "Employee number already exists"

### Department Validation
- [ ] **Missing Department Name**
  - Expected: 400 Bad Request - "Department name is required"

- [ ] **Missing Department Code**
  - Expected: 400 Bad Request - "Department code is required"

- [ ] **Missing Company ID**
  - Expected: 400 Bad Request - "Company ID is required"

- [ ] **Duplicate Department Code**
  - Try creating department with existing code in same company
  - Expected: 400 Bad Request - "Department code already exists"

---

## 8. Authorization Tests

### ADMIN Access
- [ ] **ADMIN Creating Employee**
  - Expected: 201 Created ✅

- [ ] **ADMIN Updating Employee**
  - Expected: 200 OK ✅

- [ ] **ADMIN Deleting Employee** - `DELETE /api/v1/employees/{id}`
  - Expected: 204 No Content ✅

- [ ] **ADMIN Deleting Department** - `DELETE /api/v1/departments/{id}`
  - Expected: 204 No Content ✅

### HR Access
- [ ] **HR Creating Employee**
  - Expected: 201 Created ✅

- [ ] **HR Updating Employee**
  - Expected: 200 OK ✅

- [ ] **HR Viewing Employees**
  - Expected: 200 OK ✅

- [ ] **HR Creating Department**
  - Expected: 201 Created ✅

- [ ] **HR Deleting Employee**
  - Expected: 403 Forbidden ❌ (Only ADMIN can delete)

### MANAGER Access
- [ ] **MANAGER Viewing Employees**
  - Expected: 200 OK ✅

- [ ] **MANAGER Viewing Departments**
  - Expected: 200 OK ✅

- [ ] **MANAGER Creating Employee**
  - Expected: 403 Forbidden ❌

- [ ] **MANAGER Updating Employee**
  - Expected: 403 Forbidden ❌

### EMPLOYEE Access
- [ ] **EMPLOYEE Viewing Own Profile** - `GET /api/v1/employees/me`
  - Expected: 200 OK ✅ (or 501 if not implemented)

- [ ] **EMPLOYEE Viewing Other Employees**
  - Expected: 403 Forbidden ❌

- [ ] **EMPLOYEE Creating Employee**
  - Expected: 403 Forbidden ❌

### Unauthenticated Access
- [ ] **No JWT Token**
  - Try any endpoint without Authorization header
  - Expected: 401 Unauthorized

- [ ] **Invalid JWT Token**
  - Use malformed or expired token
  - Expected: 401 Unauthorized

---

## 9. Update Tests

### Partial Updates
- [ ] **Update Only Name**
  ```json
  {"firstName": "UpdatedName"}
  ```
  - Expected: 200 OK
  - Verify: Only firstName changed, other fields unchanged

- [ ] **Update Only Contact Info**
  ```json
  {
    "email": "new.email@company.com",
    "phoneNumber": "+250788111222"
  }
  ```
  - Expected: 200 OK

- [ ] **Update Contract Details**
  ```json
  {
    "contractDetails": {
      "contractStatus": "TERMINATED",
      "baseSalary": 2000000.00
    }
  }
  ```
  - Expected: 200 OK
  - Verify: Only contract details updated

### Department Updates
- [ ] **Update Department Name**
  ```json
  {"name": "Updated Engineering"}
  ```
  - Expected: 200 OK

- [ ] **Deactivate Department**
  ```json
  {"isActive": false}
  ```
  - Expected: 200 OK
  - Verify: isActive = false

- [ ] **Assign Manager**
  ```json
  {"managerId": "770e8400-e29b-41d4-a716-446655440000"}
  ```
  - Expected: 200 OK

---

## 10. Deletion Tests

### Employee Deletion
- [ ] **Delete Existing Employee** - `DELETE /api/v1/employees/{id}`
  - Use ADMIN token
  - Expected: 204 No Content

- [ ] **Verify Employee Deleted**
  - Try GET /api/v1/employees/{deletedId}
  - Expected: 404 Not Found

- [ ] **Delete Non-Existent Employee**
  - Expected: 404 Not Found

### Department Deletion
- [ ] **Delete Existing Department** - `DELETE /api/v1/departments/{id}`
  - Use ADMIN token
  - Expected: 204 No Content

- [ ] **Delete Non-Existent Department**
  - Expected: 404 Not Found

---

## 11. Edge Cases

### Empty Responses
- [ ] **Search with No Matches**
  - Search for non-existent name
  - Expected: 200 OK with empty array []

- [ ] **Get Employees for Empty Company**
  - Use company with no employees
  - Expected: 200 OK with empty array []

### Special Characters
- [ ] **Employee with Special Characters in Name**
  ```json
  {
    "firstName": "José",
    "lastName": "O'Brien-Smith"
  }
  ```
  - Expected: 201 Created
  - Verify: Special characters preserved

- [ ] **Search with Special Characters**
  - Search for "José"
  - Expected: Finds employee with special characters

### Large Data Sets
- [ ] **Create 100+ Employees**
  - Verify: All created successfully
  - Check: Pagination works correctly

- [ ] **Get All with Large Dataset**
  - Verify: Response time acceptable
  - Check: Memory usage reasonable

---

## 12. Data Integrity Tests

### Referential Integrity
- [ ] **Create Employee with Invalid Company ID**
  - Use non-existent UUID for companyId
  - Document behavior (depends on foreign key constraints)

- [ ] **Create Employee with Invalid Department ID**
  - Use non-existent UUID for departmentId
  - Document behavior

- [ ] **Create Department with Invalid Manager ID**
  - Use non-existent UUID for managerId
  - Document behavior

### Data Consistency
- [ ] **Update Employee Department**
  - Change departmentId
  - Verify: Old department unaffected
  - Verify: New department linked correctly

- [ ] **Department Manager Assignment**
  - Assign manager to department
  - Verify: Manager is an existing employee

---

## 13. Get My Profile Endpoint

- [ ] **Test Get My Profile** - `GET /api/v1/employees/me`
  - Expected: 200 OK with current employee's profile
  - OR: 501 Not Implemented (current status)
  - Note: Implementation requires JWT integration

---

## 14. Enum Validation Tests

### Gender Enum
- [ ] **Valid Gender Values**
  - Test: MALE, FEMALE, OTHER
  - Expected: All accepted

- [ ] **Invalid Gender Value**
  - Try: "UNKNOWN"
  - Expected: 400 Bad Request

### Marital Status Enum
- [ ] **Valid Marital Status**
  - Test: SINGLE, MARRIED, DIVORCED, WIDOWED
  - Expected: All accepted

### Contract Enums
- [ ] **Valid Contract Status**
  - Test: ACTIVE, INACTIVE, TERMINATED, SUSPENDED, PENDING
  - Expected: All accepted

- [ ] **Valid Contract Job Type**
  - Test: FULL_TIME, PART_TIME, CONTRACT, INTERN, TEMPORARY
  - Expected: All accepted

- [ ] **Valid Salary Type**
  - Test: HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
  - Expected: All accepted

---

## 15. Performance Tests

### Response Times
- [ ] **Create Employee Response Time**
  - Expected: < 500ms

- [ ] **Get Employee Response Time**
  - Expected: < 200ms

- [ ] **Search Response Time**
  - Expected: < 500ms

- [ ] **Paginated List Response Time**
  - Expected: < 500ms for 20 items

### Concurrent Operations
- [ ] **Create 10 Employees Simultaneously**
  - Verify: All created successfully
  - Check: No duplicate employee numbers
  - Check: No data corruption

- [ ] **Create 5 Departments Simultaneously**
  - Verify: All created successfully
  - Check: No duplicate codes

---

## Test Data Cleanup

After testing, clean up:
- [ ] Delete test employees
- [ ] Delete test departments
- [ ] Restore any modified production data
- [ ] Clear test data from database

---

## Automated Testing Recommendations

### Unit Tests
- Service layer business logic
- Employee number uniqueness
- Email validation
- Enum validations
- Data mapping (Entity ↔ DTO)

### Integration Tests
- Controller endpoints
- Repository queries
- Database transactions
- Kafka event publishing

### End-to-End Tests
- Complete employee creation workflow
- Department assignment flow
- Update and deletion flows
- Search and filter operations

---

## Known Issues & Workarounds

Document any issues found during testing:

| Issue | Severity | Workaround | Status |
|-------|----------|------------|--------|
| Get My Profile not implemented | Medium | Use Get by ID with known employeeId | Open |

---

## Testing Sign-off

**Tested By:** _________________
**Date:** _________________
**Environment:** [ ] Local [ ] Dev [ ] Staging [ ] Production
**All Tests Passed:** [ ] Yes [ ] No

**Notes:**
_______________________________________
_______________________________________
_______________________________________

---

**Last Updated:** October 12, 2025
**Version:** 1.0
