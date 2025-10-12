# Leave Management Service - API Documentation

**Base URL:** `http://localhost:8083` (or your configured port)

**Version:** 1.0

**Authentication:** All endpoints require JWT Bearer token in Authorization header
```
Authorization: Bearer <your-jwt-token>
```

---

## Table of Contents
1. [Leave Types API](#leave-types-api)
2. [Leave Balances API](#leave-balances-api)
3. [Leave Requests API](#leave-requests-api)
4. [Approval Workflow API](#approval-workflow-api)
5. [Leave Calendar API](#leave-calendar-api)
6. [Data Models](#data-models)

---

## Leave Types API

Manage different types of leaves (Annual, Sick, Maternity, etc.)

### 1. Create Leave Type
**Endpoint:** `POST /api/v1/leave-types`

**Authorization:** HR, ADMIN

**Description:** Create a new leave type for the company

**Request Body:**
```json
{
  "name": "Annual Leave",
  "code": "ANNUAL",
  "category": "ANNUAL",
  "description": "Annual paid leave for employees",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "defaultDaysPerYear": 20,
  "maxConsecutiveDays": 15,
  "requiresDocument": false,
  "minNoticeDays": 7,
  "isPaid": true,
  "isActive": true,
  "allowCarryForward": true,
  "maxCarryForwardDays": 5
}
```

**Response:** `201 Created`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "name": "Annual Leave",
  "code": "ANNUAL",
  "category": "ANNUAL",
  "description": "Annual paid leave for employees",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "defaultDaysPerYear": 20,
  "maxConsecutiveDays": 15,
  "requiresDocument": false,
  "minNoticeDays": 7,
  "isPaid": true,
  "isActive": true,
  "allowCarryForward": true,
  "maxCarryForwardDays": 5,
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

**Categories:** `ANNUAL`, `SICK`, `MATERNITY`, `PATERNITY`, `UNPAID`, `COMPASSIONATE`, `STUDY`, `OTHER`

---

### 2. Get Leave Type by ID
**Endpoint:** `GET /api/v1/leave-types/{id}`

**Authorization:** HR, ADMIN, MANAGER, EMPLOYEE

**Description:** Retrieve a specific leave type by ID

**Response:** `200 OK`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "name": "Annual Leave",
  "code": "ANNUAL",
  "category": "ANNUAL",
  ...
}
```

---

### 3. Get Leave Type by Code
**Endpoint:** `GET /api/v1/leave-types/code/{code}`

**Authorization:** HR, ADMIN, MANAGER, EMPLOYEE

**Description:** Retrieve a leave type by its unique code

**Example:** `GET /api/v1/leave-types/code/ANNUAL`

---

### 4. Get All Leave Types (Paginated)
**Endpoint:** `GET /api/v1/leave-types`

**Authorization:** HR, ADMIN, MANAGER

**Query Parameters:**
- `page` (optional): Page number, default: 0
- `size` (optional): Page size, default: 20
- `sort` (optional): Sort field and direction, e.g., `name,asc`

**Response:** `200 OK`
```json
{
  "content": [...],
  "pageable": {...},
  "totalElements": 10,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

---

### 5. Get Leave Types by Company
**Endpoint:** `GET /api/v1/leave-types/company/{companyId}`

**Authorization:** HR, ADMIN, MANAGER, EMPLOYEE

**Query Parameters:**
- `activeOnly` (optional): Filter active only, default: true

**Example:** `GET /api/v1/leave-types/company/550e8400-e29b-41d4-a716-446655440000?activeOnly=true`

---

### 6. Get Leave Types by Category
**Endpoint:** `GET /api/v1/leave-types/category/{category}`

**Authorization:** HR, ADMIN, MANAGER

**Example:** `GET /api/v1/leave-types/category/ANNUAL`

---

### 7. Update Leave Type
**Endpoint:** `PUT /api/v1/leave-types/{id}`

**Authorization:** HR, ADMIN

**Request Body:**
```json
{
  "name": "Annual Leave Updated",
  "description": "Updated description",
  "defaultDaysPerYear": 22,
  "isActive": true
}
```

---

### 8. Delete Leave Type
**Endpoint:** `DELETE /api/v1/leave-types/{id}`

**Authorization:** ADMIN only

**Response:** `204 No Content`

---

## Leave Balances API

Manage employee leave balances and allocations

### 1. Allocate Leave Balance
**Endpoint:** `POST /api/v1/leave-balances/allocate`

**Authorization:** HR, ADMIN

**Description:** Allocate leave balance to an employee for a specific year

**Request Body:**
```json
{
  "employeeId": "770e8400-e29b-41d4-a716-446655440000",
  "leaveTypeId": "660e8400-e29b-41d4-a716-446655440000",
  "year": 2025,
  "totalAllocated": 20.0,
  "carriedForward": 2.5
}
```

**Response:** `201 Created`
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "employeeId": "770e8400-e29b-41d4-a716-446655440000",
  "leaveTypeId": "660e8400-e29b-41d4-a716-446655440000",
  "leaveTypeName": "Annual Leave",
  "leaveTypeCode": "ANNUAL",
  "year": 2025,
  "totalAllocated": 22.5,
  "used": 0.0,
  "pending": 0.0,
  "available": 22.5,
  "carriedForward": 2.5,
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

**Note:** `totalAllocated` includes both allocated days and carried forward days

---

### 2. Get Employee Leave Balances (Current Year)
**Endpoint:** `GET /api/v1/leave-balances/employee/{employeeId}`

**Authorization:** HR, ADMIN, MANAGER

**Description:** Get all leave balances for an employee in the current year

**Response:** `200 OK`
```json
[
  {
    "id": "880e8400-e29b-41d4-a716-446655440000",
    "employeeId": "770e8400-e29b-41d4-a716-446655440000",
    "leaveTypeName": "Annual Leave",
    "leaveTypeCode": "ANNUAL",
    "year": 2025,
    "totalAllocated": 22.5,
    "used": 5.0,
    "pending": 2.0,
    "available": 15.5,
    "carriedForward": 2.5
  }
]
```

**Balance Calculation:**
- `available = totalAllocated - used - pending`
- `pending` = leave requests awaiting approval
- `used` = approved leave already taken

---

### 3. Get Employee Leave Balances by Year
**Endpoint:** `GET /api/v1/leave-balances/employee/{employeeId}/year/{year}`

**Authorization:** HR, ADMIN, MANAGER

**Example:** `GET /api/v1/leave-balances/employee/770e8400-e29b-41d4-a716-446655440000/year/2025`

---

### 4. Get Specific Leave Balance
**Endpoint:** `GET /api/v1/leave-balances/employee/{employeeId}/leave-type/{leaveTypeId}/year/{year}`

**Authorization:** HR, ADMIN, MANAGER, EMPLOYEE

**Description:** Get balance for a specific leave type, employee, and year

**Example:**
```
GET /api/v1/leave-balances/employee/770e8400-e29b-41d4-a716-446655440000
    /leave-type/660e8400-e29b-41d4-a716-446655440000/year/2025
```

---

### 5. Get My Leave Balances
**Endpoint:** `GET /api/v1/leave-balances/my-balances`

**Authorization:** EMPLOYEE, MANAGER, HR, ADMIN

**Description:** Get current user's leave balances (uses JWT to identify employee)

**Response:** Same as endpoint #2 but for authenticated user

---

## Leave Requests API

Submit and manage leave requests

### 1. Create Leave Request
**Endpoint:** `POST /api/v1/leave-requests`

**Authorization:** EMPLOYEE, MANAGER, HR, ADMIN

**Description:** Submit a new leave request. Employee ID is automatically extracted from JWT token.

**Request Body:**
```json
{
  "leaveTypeId": "660e8400-e29b-41d4-a716-446655440000",
  "startDate": "2025-12-20",
  "endDate": "2025-12-24",
  "startDayType": "FULL_DAY",
  "endDayType": "FULL_DAY",
  "reason": "Family vacation",
  "documentUrl": null
}
```

**Day Types:** `FULL_DAY`, `FIRST_HALF` (morning), `SECOND_HALF` (afternoon)

**Half-Day Examples:**
```json
// Half day (morning only)
{
  "startDate": "2025-12-20",
  "endDate": "2025-12-20",
  "startDayType": "FIRST_HALF",
  "endDayType": "FIRST_HALF"
}

// Multi-day with half days
{
  "startDate": "2025-12-20",
  "endDate": "2025-12-23",
  "startDayType": "SECOND_HALF",  // Start afternoon of Dec 20
  "endDayType": "FIRST_HALF"      // End morning of Dec 23
}
```

**Response:** `201 Created`
```json
{
  "id": "990e8400-e29b-41d4-a716-446655440000",
  "employeeId": "770e8400-e29b-41d4-a716-446655440000",
  "employeeName": "John Doe",
  "leaveTypeId": "660e8400-e29b-41d4-a716-446655440000",
  "leaveTypeName": "Annual Leave",
  "leaveTypeCode": "ANNUAL",
  "startDate": "2025-12-20",
  "endDate": "2025-12-24",
  "startDayType": "FULL_DAY",
  "endDayType": "FULL_DAY",
  "totalDays": 3.0,
  "status": "PENDING",
  "reason": "Family vacation",
  "documentUrl": null,
  "approverId": null,
  "approverName": null,
  "approvedAt": null,
  "rejectionReason": null,
  "modificationNote": null,
  "cancelledAt": null,
  "cancellationReason": null,
  "createdAt": "2025-10-12T10:00:00",
  "updatedAt": "2025-10-12T10:00:00"
}
```

**Note:** `totalDays` automatically excludes weekends and public holidays!

---

### 2. Get Leave Request by ID
**Endpoint:** `GET /api/v1/leave-requests/{id}`

**Authorization:** EMPLOYEE, MANAGER, HR, ADMIN

---

### 3. Get My Leave Requests
**Endpoint:** `GET /api/v1/leave-requests/my-leaves`

**Authorization:** EMPLOYEE, MANAGER, HR, ADMIN

**Description:** Get all leave requests for the authenticated user (paginated)

**Query Parameters:**
- `page`, `size`, `sort`

---

### 4. Get My Leave Requests by Status
**Endpoint:** `GET /api/v1/leave-requests/my-leaves/status/{status}`

**Authorization:** EMPLOYEE, MANAGER, HR, ADMIN

**Statuses:** `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`, `MODIFICATION_REQUESTED`

**Example:** `GET /api/v1/leave-requests/my-leaves/status/PENDING`

---

### 5. Get Pending Leave Requests (All)
**Endpoint:** `GET /api/v1/leave-requests/pending`

**Authorization:** MANAGER, HR, ADMIN

**Description:** Get all pending leave requests that need approval

---

### 6. Approve Leave Request
**Endpoint:** `PUT /api/v1/leave-requests/{id}/approve`

**Authorization:** MANAGER, HR, ADMIN

**Description:** Approve a leave request. Approver info is automatically extracted from JWT.

**Request Body:**
```json
{
  "comments": "Approved"
}
```

**Response:** Updated leave request with status `APPROVED`

---

### 7. Reject Leave Request
**Endpoint:** `PUT /api/v1/leave-requests/{id}/reject`

**Authorization:** MANAGER, HR, ADMIN

**Request Body:**
```json
{
  "rejectionReason": "Insufficient coverage during that period"
}
```

**Response:** Updated leave request with status `REJECTED`

---

### 8. Cancel Leave Request
**Endpoint:** `PUT /api/v1/leave-requests/{id}/cancel`

**Authorization:** EMPLOYEE, MANAGER, HR, ADMIN

**Description:** Cancel a pending or approved leave request

**Request Body:**
```json
{
  "cancellationReason": "Plans changed"
}
```

**Response:** Updated leave request with status `CANCELLED`

**Note:** Balance is automatically refunded when cancelling

---

## Approval Workflow API

Multi-level approval workflow management

### 1. Get Workflows for Leave Request
**Endpoint:** `GET /api/leave/approvals/leave-request/{leaveRequestId}`

**Authorization:** ADMIN, HR, MANAGER, EMPLOYEE

**Description:** Get all approval workflow steps for a leave request

**Response:** `200 OK`
```json
[
  {
    "id": "aa0e8400-e29b-41d4-a716-446655440000",
    "leaveRequestId": "990e8400-e29b-41d4-a716-446655440000",
    "approvalLevel": "LEVEL_1",
    "approverId": "bb0e8400-e29b-41d4-a716-446655440000",
    "approverName": "Manager Name",
    "status": "APPROVED",
    "comments": "Looks good",
    "actionAt": "2025-10-12T11:00:00",
    "sequenceOrder": 1,
    "isRequired": true,
    "createdAt": "2025-10-12T10:00:00",
    "updatedAt": "2025-10-12T11:00:00"
  },
  {
    "id": "cc0e8400-e29b-41d4-a716-446655440000",
    "leaveRequestId": "990e8400-e29b-41d4-a716-446655440000",
    "approvalLevel": "LEVEL_2",
    "approverId": "dd0e8400-e29b-41d4-a716-446655440000",
    "approverName": "HR Manager",
    "status": "PENDING",
    "comments": null,
    "actionAt": null,
    "sequenceOrder": 2,
    "isRequired": true
  }
]
```

**Approval Levels:** `LEVEL_1`, `LEVEL_2`, `LEVEL_3`, `LEVEL_4`

---

### 2. Get My Pending Approvals
**Endpoint:** `GET /api/leave/approvals/pending`

**Authorization:** ADMIN, HR, MANAGER

**Description:** Get all workflows awaiting approval from the authenticated user

**Note:** Only returns workflows where previous levels are completed

---

### 3. Approve Workflow Step
**Endpoint:** `POST /api/leave/approvals/{workflowId}/approve`

**Authorization:** ADMIN, HR, MANAGER

**Description:** Approve a specific workflow step

**Request Body:**
```json
{
  "comments": "Approved. Enjoy your vacation!"
}
```

**Response:** Updated workflow with status `APPROVED`

**Note:** If all required approvals are complete, the leave request status is automatically updated to `APPROVED`

---

### 4. Reject Workflow Step
**Endpoint:** `POST /api/leave/approvals/{workflowId}/reject`

**Authorization:** ADMIN, HR, MANAGER

**Description:** Reject a workflow step (rejects entire leave request)

**Request Body:**
```json
{
  "rejectionReason": "Insufficient coverage during that period"
}
```

**Note:** Rejecting any level automatically rejects all remaining levels and the leave request

---

### 5. Delegate Approval
**Endpoint:** `POST /api/leave/approvals/{workflowId}/delegate`

**Authorization:** ADMIN, HR, MANAGER

**Description:** Delegate approval to another user

**Query Parameters:**
- `newApproverId` (required): UUID of new approver
- `newApproverName` (optional): Name of new approver

**Example:**
```
POST /api/leave/approvals/aa0e8400-e29b-41d4-a716-446655440000/delegate
    ?newApproverId=ee0e8400-e29b-41d4-a716-446655440000
    &newApproverName=Jane Smith
```

---

## Leave Calendar API

Visual calendar views and team availability

### 1. Get Team Leave Calendar
**Endpoint:** `GET /api/leave/calendar/team`

**Authorization:** ADMIN, HR, MANAGER

**Description:** Get leave calendar for a team with daily entries

**Query Parameters:**
- `employeeIds` (required): Comma-separated list of employee UUIDs
- `startDate` (required): ISO date format (YYYY-MM-DD)
- `endDate` (required): ISO date format (YYYY-MM-DD)

**Example:**
```
GET /api/leave/calendar/team
    ?employeeIds=770e8400-e29b-41d4-a716-446655440000,880e8400-e29b-41d4-a716-446655440000
    &startDate=2025-12-01
    &endDate=2025-12-31
```

**Response:** `200 OK`
```json
{
  "startDate": "2025-12-01",
  "endDate": "2025-12-31",
  "totalEmployees": 2,
  "leaveEntries": [
    {
      "date": "2025-12-20",
      "employeeId": "770e8400-e29b-41d4-a716-446655440000",
      "employeeName": "John Doe",
      "leaveRequestId": "990e8400-e29b-41d4-a716-446655440000",
      "leaveTypeName": "Annual Leave",
      "leaveTypeCode": "ANNUAL",
      "dayType": "FULL_DAY",
      "status": "APPROVED"
    }
  ],
  "employeesOnLeave": {
    "2025-12-20": 1,
    "2025-12-21": 1,
    "2025-12-22": 1
  }
}
```

---

### 2. Check Employee Availability
**Endpoint:** `GET /api/leave/calendar/availability`

**Authorization:** ADMIN, HR, MANAGER

**Description:** Check which employees are available on a specific date

**Query Parameters:**
- `employeeIds` (required): Comma-separated employee UUIDs
- `date` (required): ISO date format

**Example:**
```
GET /api/leave/calendar/availability
    ?employeeIds=770e8400-e29b-41d4-a716-446655440000,880e8400-e29b-41d4-a716-446655440000
    &date=2025-12-20
```

**Response:** `200 OK`
```json
[
  {
    "employeeId": "770e8400-e29b-41d4-a716-446655440000",
    "employeeName": "John Doe",
    "isAvailable": false,
    "leaveInfo": {
      "date": "2025-12-20",
      "leaveTypeName": "Annual Leave",
      "dayType": "FULL_DAY",
      "status": "APPROVED"
    }
  },
  {
    "employeeId": "880e8400-e29b-41d4-a716-446655440000",
    "employeeName": "Jane Smith",
    "isAvailable": true,
    "leaveInfo": null
  }
]
```

---

### 3. Get Day Availability Summary
**Endpoint:** `GET /api/leave/calendar/day-summary`

**Authorization:** ADMIN, HR, MANAGER

**Description:** Get day-by-day availability summary for a date range

**Query Parameters:**
- `employeeIds` (required): Comma-separated employee UUIDs
- `startDate` (required): ISO date
- `endDate` (required): ISO date
- `companyId` (optional): For company-specific holiday checking

**Response:** `200 OK`
```json
[
  {
    "date": "2025-12-20",
    "isWorkingDay": true,
    "totalEmployees": 10,
    "availableEmployees": 8,
    "onLeaveEmployees": 2,
    "employeesOnLeave": [...]
  },
  {
    "date": "2025-12-21",
    "isWorkingDay": false,
    "totalEmployees": 10,
    "availableEmployees": 0,
    "onLeaveEmployees": 0,
    "employeesOnLeave": []
  }
]
```

**Note:** `isWorkingDay` considers weekends and holidays

---

### 4. Get Employee Leave Calendar
**Endpoint:** `GET /api/leave/calendar/employee/{employeeId}`

**Authorization:** ADMIN, HR, MANAGER, EMPLOYEE

**Description:** Get all leave entries for a specific employee in a date range

**Query Parameters:**
- `startDate` (required)
- `endDate` (required)

**Example:**
```
GET /api/leave/calendar/employee/770e8400-e29b-41d4-a716-446655440000
    ?startDate=2025-12-01
    &endDate=2025-12-31
```

---

### 5. Find Optimal Dates for Events
**Endpoint:** `GET /api/leave/calendar/optimal-dates`

**Authorization:** ADMIN, HR, MANAGER

**Description:** Find the best dates for team events based on maximum availability

**Query Parameters:**
- `employeeIds` (required): Team members
- `startDate` (required)
- `endDate` (required)
- `requiredAttendees` (required): Minimum number of attendees needed
- `companyId` (optional)

**Example:**
```
GET /api/leave/calendar/optimal-dates
    ?employeeIds=id1,id2,id3
    &startDate=2025-12-01
    &endDate=2025-12-31
    &requiredAttendees=8
```

**Response:** List of days sorted by availability (most available first)

---

## Data Models

### Leave Categories
- `ANNUAL` - Annual/vacation leave
- `SICK` - Sick leave
- `MATERNITY` - Maternity leave
- `PATERNITY` - Paternity leave
- `UNPAID` - Unpaid leave
- `COMPASSIONATE` - Compassionate/bereavement leave
- `STUDY` - Study leave
- `OTHER` - Other types

### Leave Request Statuses
- `PENDING` - Awaiting approval
- `APPROVED` - Approved by manager/HR
- `REJECTED` - Rejected by manager/HR
- `CANCELLED` - Cancelled by employee
- `MODIFICATION_REQUESTED` - Changes requested

### Day Types (Half-Day Support)
- `FULL_DAY` - Full working day (8 hours)
- `FIRST_HALF` - Morning half (4 hours)
- `SECOND_HALF` - Afternoon half (4 hours)

### Approval Levels
- `LEVEL_1` - First level (usually direct manager)
- `LEVEL_2` - Second level (department head)
- `LEVEL_3` - Third level (HR manager)
- `LEVEL_4` - Fourth level (executive approval)

---

## Special Features

### 1. Automatic Working Days Calculation
The system automatically:
- Excludes weekends (Saturday, Sunday)
- Excludes public and company holidays
- Calculates precise half-day leave (0.5 increments)

**Example:**
```
Request: Dec 20-24 (5 calendar days)
Excludes: Dec 21-22 (weekend)
Result: 3 working days
```

### 2. Balance Reservation System
When a leave request is created:
1. Days move from `available` to `pending`
2. Upon approval: `pending` → `used`
3. Upon rejection: `pending` → `available` (refunded)
4. Upon cancellation: `used` → `available` (refunded)

### 3. Automated Accrual Jobs
- **Monthly accrual**: Runs 1st of every month at 1:00 AM
- **Yearly accrual**: Runs January 1st at 2:00 AM with carry-forward
- **Carry-forward expiry**: Runs March 31st at 3:00 AM
- **Balance cleanup**: Runs every Sunday at 4:00 AM

### 4. JWT Integration
All authenticated endpoints automatically:
- Extract employee ID from JWT token
- Extract employee name from JWT token
- No need to pass employee info in request body

---

## Error Responses

All error responses follow this format:

```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient leave balance. Available: 5.0, Required: 10.0",
  "path": "/api/v1/leave-requests"
}
```

### Common Error Codes
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (missing/invalid token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found (resource doesn't exist)
- `500` - Internal Server Error

---

## Testing Examples

### Using cURL

**1. Create Leave Request**
```bash
curl -X POST http://localhost:8083/api/v1/leave-requests \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveTypeId": "660e8400-e29b-41d4-a716-446655440000",
    "startDate": "2025-12-20",
    "endDate": "2025-12-24",
    "startDayType": "FULL_DAY",
    "endDayType": "FULL_DAY",
    "reason": "Family vacation"
  }'
```

**2. Get My Leave Balances**
```bash
curl -X GET http://localhost:8083/api/v1/leave-balances/my-balances \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**3. Approve Leave Request**
```bash
curl -X PUT http://localhost:8083/api/v1/leave-requests/{id}/approve \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "comments": "Approved"
  }'
```

---

## Postman Collection

Import this collection structure into Postman:

**Collection Structure:**
```
HRMS Leave Management
├── Leave Types
│   ├── Create Leave Type
│   ├── Get All Leave Types
│   ├── Get by ID
│   ├── Get by Code
│   ├── Update Leave Type
│   └── Delete Leave Type
├── Leave Balances
│   ├── Allocate Balance
│   ├── Get Employee Balances
│   ├── Get My Balances
│   └── Get Specific Balance
├── Leave Requests
│   ├── Create Request
│   ├── Get My Requests
│   ├── Approve Request
│   ├── Reject Request
│   └── Cancel Request
├── Approval Workflow
│   ├── Get Pending Approvals
│   ├── Approve Workflow
│   ├── Reject Workflow
│   └── Delegate Approval
└── Leave Calendar
    ├── Team Calendar
    ├── Check Availability
    ├── Day Summary
    └── Optimal Dates
```

**Environment Variables:**
- `base_url`: `http://localhost:8083`
- `jwt_token`: `YOUR_JWT_TOKEN`
- `employee_id`: `770e8400-e29b-41d4-a716-446655440000`
- `leave_type_id`: `660e8400-e29b-41d4-a716-446655440000`

---

## Support & Troubleshooting

### Health Check Endpoints
- Leave Types: `GET /api/v1/leave-types/health`
- Leave Balances: `GET /api/v1/leave-balances/health`
- Leave Requests: `GET /api/v1/leave-requests/health`

### Common Issues

**1. "No employee ID found for current user"**
- Solution: Ensure your user account has an `employeeId` set in the database
- Check JWT token contains employee information

**2. "Insufficient leave balance"**
- Solution: Verify balance using `/my-balances` endpoint
- Check if there are pending requests reserving balance

**3. "Minimum notice period is X days"**
- Solution: Submit leave requests with adequate advance notice
- Each leave type has different notice period requirements

**4. "Leave request overlaps with existing leave"**
- Solution: Check existing requests using `/my-leaves` endpoint
- Cancel conflicting request before submitting new one

---

**Documentation Version:** 1.0
**Last Updated:** October 12, 2025
**Service Port:** 8083
