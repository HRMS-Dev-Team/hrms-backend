# Salary Advance Service - API Documentation

## Overview
The Salary Advance Service allows employees to request salary advances and manages the complete lifecycle including approval, repayment scheduling, and tracking.

**Base URL**: `http://localhost:8083/api/v1`

**Authentication**: All endpoints (except health checks) require JWT authentication via Bearer token.

---

## Endpoints

### Salary Advance Management

#### 1. Create Salary Advance Request
**POST** `/salary-advances`

Request a salary advance as an employee.

**Authorization**: `EMPLOYEE`, `MANAGER`, `HR`, `ADMIN`

**Request Body**:
```json
{
  "requestedAmount": 1000.00,
  "installments": 3,
  "currency": "USD",
  "reason": "Medical emergency"
}
```

**Response** (201 Created):
```json
{
  "id": "uuid",
  "employeeId": "uuid",
  "requestedAmount": 1000.00,
  "approvedAmount": null,
  "installments": 3,
  "installmentAmount": null,
  "currency": "USD",
  "status": "REQUESTED",
  "reason": "Medical emergency",
  "requestedAt": "2025-10-19T10:30:00",
  "approvedAt": null,
  "approvedBy": null,
  "scheduledRepaymentStart": null,
  "paidOffAt": null,
  "rejectionReason": null,
  "createdBy": "John Doe",
  "updatedBy": null,
  "createdAt": "2025-10-19T10:30:00",
  "updatedAt": "2025-10-19T10:30:00"
}
```

---

#### 2. Get Salary Advance by ID
**GET** `/salary-advances/{id}`

Retrieve details of a specific salary advance.

**Authorization**: `EMPLOYEE`, `MANAGER`, `HR`, `ADMIN`

**Response** (200 OK):
```json
{
  "id": "uuid",
  "employeeId": "uuid",
  "requestedAmount": 1000.00,
  "approvedAmount": 900.00,
  "installments": 3,
  "installmentAmount": 300.00,
  "currency": "USD",
  "status": "APPROVED",
  ...
}
```

---

#### 3. Get My Salary Advances
**GET** `/salary-advances/my-advances`

Get all salary advances for the current employee.

**Authorization**: `EMPLOYEE`, `MANAGER`, `HR`, `ADMIN`

**Query Parameters**:
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: requestedAt)

**Response** (200 OK):
```json
{
  "content": [...],
  "pageable": {...},
  "totalElements": 5,
  "totalPages": 1
}
```

---

#### 4. Get My Advances by Status
**GET** `/salary-advances/my-advances/status/{status}`

Filter employee's advances by status.

**Authorization**: `EMPLOYEE`, `MANAGER`, `HR`, `ADMIN`

**Path Parameters**:
- `status`: One of `REQUESTED`, `APPROVED`, `ACTIVE`, `PAID_OFF`, `REJECTED`, `CANCELLED`

**Response** (200 OK): Paginated list of advances

---

#### 5. Get Pending Advances (HR/Admin)
**GET** `/salary-advances/pending`

Get all pending salary advance requests for review.

**Authorization**: `HR`, `ADMIN`

**Response** (200 OK): Paginated list of pending advances

---

#### 6. Get All Advances (HR/Admin)
**GET** `/salary-advances`

Get all salary advances across all employees.

**Authorization**: `HR`, `ADMIN`

**Response** (200 OK): Paginated list of all advances

---

#### 7. Approve Salary Advance
**PUT** `/salary-advances/{id}/approve`

Approve a salary advance request.

**Authorization**: `HR`, `ADMIN`

**Request Body**:
```json
{
  "approvedAmount": 900.00,
  "scheduledRepaymentStart": "2025-11-01"
}
```

**Response** (200 OK):
```json
{
  "id": "uuid",
  "status": "APPROVED",
  "approvedAmount": 900.00,
  "installmentAmount": 300.00,
  "scheduledRepaymentStart": "2025-11-01",
  "approvedAt": "2025-10-19T14:30:00",
  "approvedBy": "HR Manager",
  ...
}
```

---

#### 8. Reject Salary Advance
**PUT** `/salary-advances/{id}/reject`

Reject a salary advance request.

**Authorization**: `HR`, `ADMIN`

**Request Body**:
```json
{
  "rejectionReason": "Insufficient tenure period"
}
```

**Response** (200 OK):
```json
{
  "id": "uuid",
  "status": "REJECTED",
  "rejectionReason": "Insufficient tenure period",
  ...
}
```

---

#### 9. Cancel Salary Advance
**PUT** `/salary-advances/{id}/cancel`

Cancel your own salary advance request (only if status is REQUESTED).

**Authorization**: `EMPLOYEE`, `MANAGER`, `HR`, `ADMIN`

**Response** (200 OK):
```json
{
  "id": "uuid",
  "status": "CANCELLED",
  ...
}
```

---

#### 10. Activate Salary Advance
**PUT** `/salary-advances/{id}/activate`

Activate an approved salary advance (moves from APPROVED to ACTIVE).

**Authorization**: `HR`, `ADMIN`

**Response** (200 OK):
```json
{
  "id": "uuid",
  "status": "ACTIVE",
  ...
}
```

---

### Repayment Schedule Management

#### 11. Get Repayment Schedule
**GET** `/repayment-schedules/salary-advance/{salaryAdvanceId}`

Get the repayment schedule for a salary advance.

**Authorization**: `EMPLOYEE`, `MANAGER`, `HR`, `ADMIN`

**Response** (200 OK):
```json
[
  {
    "id": "uuid",
    "salaryAdvanceId": "uuid",
    "installmentNumber": 1,
    "dueDate": "2025-11-01",
    "dueAmount": 300.00,
    "paidAmount": 300.00,
    "status": "PAID",
    "paidAt": "2025-11-01T09:00:00",
    "paymentReference": "PAY-2025-11-001",
    "notes": "Deducted from November salary",
    "createdAt": "2025-10-19T14:30:00",
    "updatedAt": "2025-11-01T09:00:00"
  },
  {
    "id": "uuid",
    "salaryAdvanceId": "uuid",
    "installmentNumber": 2,
    "dueDate": "2025-12-01",
    "dueAmount": 300.00,
    "paidAmount": null,
    "status": "PENDING",
    "paidAt": null,
    "paymentReference": null,
    "notes": null,
    "createdAt": "2025-10-19T14:30:00",
    "updatedAt": "2025-10-19T14:30:00"
  }
]
```

---

#### 12. Get Repayment Schedule by ID
**GET** `/repayment-schedules/{id}`

Get details of a specific repayment schedule entry.

**Authorization**: `EMPLOYEE`, `MANAGER`, `HR`, `ADMIN`

**Response** (200 OK): Repayment schedule details

---

#### 13. Get Pending Repayments
**GET** `/repayment-schedules/pending`

Get all pending repayment installments.

**Authorization**: `HR`, `ADMIN`

**Response** (200 OK): List of pending repayments

---

#### 14. Record Payment
**PUT** `/repayment-schedules/{id}/record-payment`

Record a payment for a repayment installment.

**Authorization**: `HR`, `ADMIN`

**Request Body**:
```json
{
  "paidAmount": 300.00,
  "paymentReference": "PAY-2025-11-001",
  "notes": "Deducted from November salary"
}
```

**Response** (200 OK):
```json
{
  "id": "uuid",
  "salaryAdvanceId": "uuid",
  "installmentNumber": 1,
  "dueAmount": 300.00,
  "paidAmount": 300.00,
  "status": "PAID",
  "paidAt": "2025-11-01T09:00:00",
  ...
}
```

---

#### 15. Get Outstanding Balance
**GET** `/repayment-schedules/salary-advance/{salaryAdvanceId}/outstanding-balance`

Get the remaining outstanding balance for a salary advance.

**Authorization**: `EMPLOYEE`, `MANAGER`, `HR`, `ADMIN`

**Response** (200 OK):
```json
{
  "outstandingBalance": 600.00
}
```

---

### Audit Trail

#### 16. Get Audit Logs for Salary Advance
**GET** `/salary-advance-audits/salary-advance/{salaryAdvanceId}`

Get all audit logs for a specific salary advance.

**Authorization**: `HR`, `ADMIN`

**Response** (200 OK):
```json
[
  {
    "id": "uuid",
    "salaryAdvanceId": "uuid",
    "action": "REQUESTED",
    "actor": "John Doe",
    "details": {
      "employeeId": "uuid",
      "requestedAmount": "1000.00",
      "installments": 3,
      "currency": "USD"
    },
    "createdAt": "2025-10-19T10:30:00"
  },
  {
    "id": "uuid",
    "salaryAdvanceId": "uuid",
    "action": "APPROVED",
    "actor": "HR Manager",
    "details": {
      "approvedAmount": "900.00",
      "scheduledRepaymentStart": "2025-11-01",
      "installmentAmount": "300.00"
    },
    "createdAt": "2025-10-19T14:30:00"
  }
]
```

---

#### 17. Get Audit Logs by Actor
**GET** `/salary-advance-audits/actor/{actor}`

Get all audit logs for a specific actor.

**Authorization**: `HR`, `ADMIN`

**Response** (200 OK): List of audit logs

---

### Health Check

#### 18. Health Check Endpoints
**GET** `/salary-advances/health`
**GET** `/repayment-schedules/health`
**GET** `/salary-advance-audits/health`

Check if the service endpoints are working.

**Authorization**: None required

**Response** (200 OK):
```
Salary advance endpoints are working
```

---

## Status Workflow

```
REQUESTED → APPROVED → ACTIVE → PAID_OFF
    ↓           ↓
CANCELLED   REJECTED
```

### Status Descriptions:
- **REQUESTED**: Initial state when employee creates a request
- **APPROVED**: HR/Admin has approved the request
- **ACTIVE**: Advance is active and repayments are being processed
- **PAID_OFF**: All installments have been paid
- **REJECTED**: Request was rejected by HR/Admin
- **CANCELLED**: Employee cancelled their own request

---

## Repayment Status

- **PENDING**: Payment not yet made
- **PARTIAL**: Partial payment received
- **PAID**: Fully paid
- **FAILED**: Payment failed

---

## Business Rules

1. **Minimum Advance**: $50.00
2. **Maximum Advance**: 50% of employee's monthly salary (configurable)
3. **Minimum Tenure**: 3 months (configurable)
4. **Installment Range**: 1-12 months
5. **Active Advances**: Only one active advance allowed per employee
6. **Cancellation**: Only REQUESTED advances can be cancelled by employee
7. **Rejection**: Only REQUESTED advances can be rejected
8. **Approval**: Only REQUESTED advances can be approved
9. **Auto Completion**: Advance status changes to PAID_OFF when all installments are paid

---

## Error Handling

All endpoints return appropriate HTTP status codes:
- **200 OK**: Successful operation
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid input data
- **401 Unauthorized**: Missing or invalid authentication
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

Error responses follow this format:
```json
{
  "timestamp": "2025-10-19T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Failed to create salary advance request: Minimum advance amount is $50.0",
  "path": "/api/v1/salary-advances"
}
```

---

## Database Schema

### salary_advance
- `id` (UUID, PK)
- `employee_id` (UUID)
- `requested_amount` (NUMERIC)
- `approved_amount` (NUMERIC)
- `installments` (INT)
- `installment_amount` (NUMERIC)
- `currency` (VARCHAR)
- `status` (VARCHAR)
- `reason` (TEXT)
- Timestamps and audit fields

### repayment_schedule
- `id` (UUID, PK)
- `salary_advance_id` (UUID, FK)
- `installment_number` (INT)
- `due_date` (DATE)
- `due_amount` (NUMERIC)
- `paid_amount` (NUMERIC)
- `status` (VARCHAR)
- Payment tracking fields

### salary_advance_audit
- `id` (UUID, PK)
- `salary_advance_id` (UUID, FK)
- `action` (VARCHAR)
- `actor` (VARCHAR)
- `details` (JSONB)
- `created_at` (TIMESTAMP)

---

## Running the Service

1. Start the database:
   ```bash
   docker-compose up -d postgres
   ```

2. Build and run the service:
   ```bash
   ./gradlew :apps:hrms-salary-advance:bootRun
   ```

3. Service will be available at: `http://localhost:8083`

4. Health check: `http://localhost:8083/actuator/health`
