# Leave Management API - Quick Reference

## Base URL
```
http://localhost:8083
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
| **LEAVE TYPES** |
| POST | `/api/v1/leave-types` | HR, ADMIN | Create leave type |
| GET | `/api/v1/leave-types/{id}` | All | Get by ID |
| GET | `/api/v1/leave-types/code/{code}` | All | Get by code |
| GET | `/api/v1/leave-types` | HR, ADMIN, MANAGER | List all (paginated) |
| GET | `/api/v1/leave-types/company/{companyId}` | All | Get by company |
| PUT | `/api/v1/leave-types/{id}` | HR, ADMIN | Update |
| DELETE | `/api/v1/leave-types/{id}` | ADMIN | Delete |
| **LEAVE BALANCES** |
| POST | `/api/v1/leave-balances/allocate` | HR, ADMIN | Allocate balance |
| GET | `/api/v1/leave-balances/my-balances` | All | Get my balances |
| GET | `/api/v1/leave-balances/employee/{id}` | HR, ADMIN, MANAGER | Get employee balances |
| GET | `/api/v1/leave-balances/employee/{id}/year/{year}` | HR, ADMIN, MANAGER | Get by year |
| **LEAVE REQUESTS** |
| POST | `/api/v1/leave-requests` | All | Create request |
| GET | `/api/v1/leave-requests/{id}` | All | Get by ID |
| GET | `/api/v1/leave-requests/my-leaves` | All | Get my requests |
| GET | `/api/v1/leave-requests/my-leaves/status/{status}` | All | Get my requests by status |
| GET | `/api/v1/leave-requests/pending` | MANAGER, HR, ADMIN | Get all pending |
| PUT | `/api/v1/leave-requests/{id}/approve` | MANAGER, HR, ADMIN | Approve request |
| PUT | `/api/v1/leave-requests/{id}/reject` | MANAGER, HR, ADMIN | Reject request |
| PUT | `/api/v1/leave-requests/{id}/cancel` | All | Cancel request |
| **APPROVAL WORKFLOW** |
| GET | `/api/leave/approvals/leave-request/{id}` | All | Get workflows for request |
| GET | `/api/leave/approvals/pending` | MANAGER, HR, ADMIN | Get my pending approvals |
| POST | `/api/leave/approvals/{id}/approve` | MANAGER, HR, ADMIN | Approve workflow step |
| POST | `/api/leave/approvals/{id}/reject` | MANAGER, HR, ADMIN | Reject workflow step |
| POST | `/api/leave/approvals/{id}/delegate` | MANAGER, HR, ADMIN | Delegate approval |
| **LEAVE CALENDAR** |
| GET | `/api/leave/calendar/team` | MANAGER, HR, ADMIN | Team calendar |
| GET | `/api/leave/calendar/availability` | MANAGER, HR, ADMIN | Check availability |
| GET | `/api/leave/calendar/day-summary` | MANAGER, HR, ADMIN | Day summary |
| GET | `/api/leave/calendar/employee/{id}` | All | Employee calendar |
| GET | `/api/leave/calendar/optimal-dates` | MANAGER, HR, ADMIN | Find optimal dates |

---

## Common Request Examples

### Create Leave Request
```bash
curl -X POST http://localhost:8083/api/v1/leave-requests \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveTypeId": "uuid",
    "startDate": "2025-12-20",
    "endDate": "2025-12-24",
    "startDayType": "FULL_DAY",
    "endDayType": "FULL_DAY",
    "reason": "Vacation"
  }'
```

### Get My Balances
```bash
curl -X GET http://localhost:8083/api/v1/leave-balances/my-balances \
  -H "Authorization: Bearer TOKEN"
```

### Approve Request
```bash
curl -X PUT http://localhost:8083/api/v1/leave-requests/{id}/approve \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"comments": "Approved"}'
```

---

## Enums Reference

### Leave Categories
```
ANNUAL | SICK | MATERNITY | PATERNITY | UNPAID | COMPASSIONATE | STUDY | OTHER
```

### Leave Request Status
```
PENDING | APPROVED | REJECTED | CANCELLED | MODIFICATION_REQUESTED
```

### Day Types
```
FULL_DAY | FIRST_HALF | SECOND_HALF
```

### Approval Levels
```
LEVEL_1 | LEVEL_2 | LEVEL_3 | LEVEL_4
```

---

## Half-Day Leave Examples

### Morning only (0.5 day)
```json
{
  "startDate": "2025-12-20",
  "endDate": "2025-12-20",
  "startDayType": "FIRST_HALF",
  "endDayType": "FIRST_HALF"
}
```

### Afternoon only (0.5 day)
```json
{
  "startDate": "2025-12-20",
  "endDate": "2025-12-20",
  "startDayType": "SECOND_HALF",
  "endDayType": "SECOND_HALF"
}
```

### Full day split across two dates (1.0 day)
```json
{
  "startDate": "2025-12-20",
  "endDate": "2025-12-21",
  "startDayType": "SECOND_HALF",
  "endDayType": "FIRST_HALF"
}
```

### Multi-day with half days (2.5 days)
```json
{
  "startDate": "2025-12-20",
  "endDate": "2025-12-23",
  "startDayType": "SECOND_HALF",  // Start afternoon Dec 20
  "endDayType": "FIRST_HALF"      // End morning Dec 23
}
// Dec 20 (0.5) + Dec 21 (1.0) + Dec 22 (1.0) + Dec 23 (0.5) = 3.0 days
// Minus weekend = 2.5 working days
```

---

## Status Transitions

```
PENDING ──approve──> APPROVED
   │
   ├──reject──> REJECTED
   │
   └──cancel──> CANCELLED

APPROVED ──cancel──> CANCELLED
```

---

## Balance State Machine

```
Initial: available = 20.0, pending = 0, used = 0

Create Request (5 days):
  available = 15.0, pending = 5.0, used = 0

Approve:
  available = 15.0, pending = 0, used = 5.0

Cancel:
  available = 20.0, pending = 0, used = 0 (refunded)
```

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

---

## Query Parameters Cheat Sheet

### Pagination
```
?page=0&size=20&sort=name,asc
```

### Date Filtering
```
?startDate=2025-01-01&endDate=2025-12-31
```

### Multiple IDs
```
?employeeIds=id1,id2,id3
```

### Boolean Filters
```
?activeOnly=true
```

---

## Validation Rules

| Field | Rule |
|-------|------|
| Leave Request | endDate >= startDate |
| | minNoticeDays respected |
| | maxConsecutiveDays not exceeded |
| | sufficient balance available |
| | no overlapping requests |
| Balance Allocation | totalAllocated > 0 |
| | no duplicate (employee, type, year) |
| Leave Type | unique code |
| | name required |

---

## Automatic Calculations

✅ **Weekend Exclusion** - Saturday and Sunday automatically excluded

✅ **Holiday Exclusion** - Public and company holidays excluded

✅ **Half-Day Support** - Precise 0.5 day calculations

✅ **Balance Reservation** - Automatic pending → used → available transitions

---

## Scheduled Jobs

| Job | Schedule | Description |
|-----|----------|-------------|
| Monthly Accrual | 1st @ 1:00 AM | Monthly leave allocation |
| Yearly Accrual | Jan 1 @ 2:00 AM | Annual allocation + carry-forward |
| Carry-forward Expiry | Mar 31 @ 3:00 AM | Expire carried forward balances |
| Balance Cleanup | Sunday @ 4:00 AM | Remove old balances (3+ years) |

---

## Health Checks

```bash
# Leave Types
curl http://localhost:8083/api/v1/leave-types/health

# Leave Balances
curl http://localhost:8083/api/v1/leave-balances/health

# Leave Requests
curl http://localhost:8083/api/v1/leave-requests/health
```

---

## Common Errors

| Error Message | Solution |
|---------------|----------|
| "No employee ID found" | Ensure user has employeeId in database |
| "Insufficient leave balance" | Check available balance |
| "Overlaps with existing leave" | Check existing requests |
| "Minimum notice period" | Submit request earlier |
| "Maximum consecutive days" | Split into multiple requests |

---

## Postman Environment Variables

```json
{
  "base_url": "http://localhost:8083",
  "jwt_token": "YOUR_JWT_TOKEN",
  "employee_id": "770e8400-e29b-41d4-a716-446655440000",
  "leave_type_id": "660e8400-e29b-41d4-a716-446655440000",
  "company_id": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## Database Tables

```
leave_types          - Leave type definitions
leave_balances       - Employee leave balances
leave_requests       - Leave requests
approval_workflows   - Multi-level approval tracking
holidays             - Public and company holidays
```

---

**Last Updated:** October 12, 2025
**Version:** 1.0
