# Leave Management Service - API Testing Checklist

## Prerequisites
- [ ] Database is running and migrations are applied
- [ ] Auth service is running (for JWT token generation)
- [ ] Leave service is running on port 8083
- [ ] You have a valid JWT token with employee context

---

## 1. Leave Types - Basic CRUD

### Setup Test Data
```json
{
  "name": "Test Annual Leave",
  "code": "TEST_ANNUAL",
  "category": "ANNUAL",
  "companyId": "YOUR_COMPANY_ID",
  "defaultDaysPerYear": 20,
  "isPaid": true,
  "isActive": true
}
```

### Tests
- [ ] **Create Leave Type** - `POST /api/v1/leave-types`
  - Expected: 201 Created with leave type ID
  - Save `leaveTypeId` for subsequent tests

- [ ] **Get Leave Type by ID** - `GET /api/v1/leave-types/{id}`
  - Expected: 200 OK with complete leave type details

- [ ] **Get Leave Type by Code** - `GET /api/v1/leave-types/code/TEST_ANNUAL`
  - Expected: 200 OK with matching leave type

- [ ] **Get All Leave Types** - `GET /api/v1/leave-types?page=0&size=20`
  - Expected: 200 OK with paginated results

- [ ] **Get Leave Types by Company** - `GET /api/v1/leave-types/company/{companyId}`
  - Expected: 200 OK with list of company leave types

- [ ] **Update Leave Type** - `PUT /api/v1/leave-types/{id}`
  ```json
  {
    "name": "Updated Annual Leave",
    "defaultDaysPerYear": 22
  }
  ```
  - Expected: 200 OK with updated data

---

## 2. Leave Balances - Allocation & Retrieval

### Tests
- [ ] **Allocate Leave Balance** - `POST /api/v1/leave-balances/allocate`
  ```json
  {
    "employeeId": "YOUR_EMPLOYEE_ID",
    "leaveTypeId": "LEAVE_TYPE_ID_FROM_STEP_1",
    "year": 2025,
    "totalAllocated": 20.0,
    "carriedForward": 0
  }
  ```
  - Expected: 201 Created with balance details
  - Verify: `available = 20.0, used = 0, pending = 0`

- [ ] **Get My Leave Balances** - `GET /api/v1/leave-balances/my-balances`
  - Expected: 200 OK with list of current year balances
  - Verify: Contains balance created in previous step

- [ ] **Get Employee Balances** - `GET /api/v1/leave-balances/employee/{employeeId}`
  - Expected: 200 OK with employee's balances
  - Note: Requires HR/ADMIN role

- [ ] **Get Specific Balance** - `GET /api/v1/leave-balances/employee/{employeeId}/leave-type/{leaveTypeId}/year/2025`
  - Expected: 200 OK with exact balance match

---

## 3. Leave Requests - Full Workflow

### Test Scenario 1: Full Day Leave Request
- [ ] **Create Leave Request** - `POST /api/v1/leave-requests`
  ```json
  {
    "leaveTypeId": "LEAVE_TYPE_ID",
    "startDate": "2025-12-23",
    "endDate": "2025-12-27",
    "startDayType": "FULL_DAY",
    "endDayType": "FULL_DAY",
    "reason": "Test full day leave"
  }
  ```
  - Expected: 201 Created with `status: PENDING`
  - Save `leaveRequestId`
  - Verify: `totalDays` excludes weekends (Dec 24-27 = 4 days, minus weekend = 3 days)

- [ ] **Verify Balance Changed** - `GET /api/v1/leave-balances/my-balances`
  - Expected: `pending` increased by 3.0, `available` decreased by 3.0

- [ ] **Get My Leave Requests** - `GET /api/v1/leave-requests/my-leaves`
  - Expected: 200 OK with list containing the request

- [ ] **Get Request by ID** - `GET /api/v1/leave-requests/{id}`
  - Expected: 200 OK with full request details

- [ ] **Get My Pending Requests** - `GET /api/v1/leave-requests/my-leaves/status/PENDING`
  - Expected: 200 OK with pending request

### Test Scenario 2: Half Day Leave Request
- [ ] **Create Half Day Request** - `POST /api/v1/leave-requests`
  ```json
  {
    "leaveTypeId": "LEAVE_TYPE_ID",
    "startDate": "2025-12-30",
    "endDate": "2025-12-30",
    "startDayType": "FIRST_HALF",
    "endDayType": "FIRST_HALF",
    "reason": "Morning appointment"
  }
  ```
  - Expected: `totalDays = 0.5`

- [ ] **Create Multi-Day with Half Days** - `POST /api/v1/leave-requests`
  ```json
  {
    "leaveTypeId": "LEAVE_TYPE_ID",
    "startDate": "2025-12-02",
    "endDate": "2025-12-05",
    "startDayType": "SECOND_HALF",
    "endDayType": "FIRST_HALF",
    "reason": "Partial days"
  }
  ```
  - Expected: Working days calculation with half-day adjustments

---

## 4. Leave Approval Workflow

### Test as Manager/HR User
- [ ] **Get Pending Approvals** - `GET /api/v1/leave-requests/pending`
  - Expected: 200 OK with list of pending requests
  - Verify: Contains requests from previous tests

- [ ] **Approve Leave Request** - `PUT /api/v1/leave-requests/{id}/approve`
  ```json
  {
    "comments": "Approved for testing"
  }
  ```
  - Expected: 200 OK with `status: APPROVED`
  - Save approved request for next tests

- [ ] **Verify Balance Updated** - `GET /api/v1/leave-balances/my-balances`
  - Expected: `pending` decreased, `used` increased by same amount

- [ ] **Create Another Request to Reject** - `POST /api/v1/leave-requests`
  - Use different dates

- [ ] **Reject Leave Request** - `PUT /api/v1/leave-requests/{id}/reject`
  ```json
  {
    "rejectionReason": "Insufficient coverage"
  }
  ```
  - Expected: 200 OK with `status: REJECTED`
  - Verify: Balance refunded (`pending` back to `available`)

---

## 5. Leave Request Cancellation

### Tests
- [ ] **Cancel Pending Request** - `PUT /api/v1/leave-requests/{pendingId}/cancel`
  ```json
  {
    "cancellationReason": "Plans changed"
  }
  ```
  - Expected: 200 OK with `status: CANCELLED`
  - Verify: Balance refunded

- [ ] **Cancel Approved Request** - `PUT /api/v1/leave-requests/{approvedId}/cancel`
  ```json
  {
    "cancellationReason": "Emergency - need to work"
  }
  ```
  - Expected: 200 OK with `status: CANCELLED`
  - Verify: Days moved from `used` back to `available`

---

## 6. Multi-Level Approval Workflow

### Setup (Requires creating workflow)
Create a leave request that triggers multi-level approval

### Tests
- [ ] **Get Workflows for Request** - `GET /api/leave/approvals/leave-request/{leaveRequestId}`
  - Expected: 200 OK with array of workflow steps
  - Verify: `sequenceOrder` is correct (1, 2, 3...)

- [ ] **Get My Pending Approvals** - `GET /api/leave/approvals/pending`
  - Expected: Only shows workflows where previous levels are approved

- [ ] **Approve Level 1** - `POST /api/leave/approvals/{workflowId}/approve`
  ```json
  {
    "comments": "Level 1 approved"
  }
  ```
  - Expected: 200 OK with `status: APPROVED`
  - Verify: Next level becomes available for approval

- [ ] **Approve Level 2** - Repeat for next workflow step
  - Verify: If final level, leave request status changes to `APPROVED`

- [ ] **Test Rejection at Level 2**
  ```json
  {
    "rejectionReason": "Budget constraints"
  }
  ```
  - Expected: All remaining levels rejected, leave request rejected

- [ ] **Test Delegation** - `POST /api/leave/approvals/{workflowId}/delegate?newApproverId={id}`
  - Expected: Workflow approver changed

---

## 7. Leave Calendar & Availability

### Tests
- [ ] **Get Team Calendar** - `GET /api/leave/calendar/team`
  ```
  ?employeeIds=id1,id2,id3
  &startDate=2025-12-01
  &endDate=2025-12-31
  ```
  - Expected: 200 OK with calendar showing all team leaves
  - Verify: `leaveEntries` contains daily breakdown
  - Verify: `employeesOnLeave` map shows count per day

- [ ] **Check Availability for Date** - `GET /api/leave/calendar/availability`
  ```
  ?employeeIds=id1,id2
  &date=2025-12-25
  ```
  - Expected: Array showing who's available/on leave

- [ ] **Get Day Summary** - `GET /api/leave/calendar/day-summary`
  ```
  ?employeeIds=id1,id2,id3
  &startDate=2025-12-01
  &endDate=2025-12-31
  ```
  - Expected: Day-by-day breakdown
  - Verify: `isWorkingDay` correctly identifies weekends/holidays
  - Verify: Availability counts are correct

- [ ] **Get Employee Calendar** - `GET /api/leave/calendar/employee/{employeeId}`
  ```
  ?startDate=2025-12-01
  &endDate=2025-12-31
  ```
  - Expected: All leave entries for that employee

- [ ] **Find Optimal Dates** - `GET /api/leave/calendar/optimal-dates`
  ```
  ?employeeIds=id1,id2,id3,id4,id5
  &startDate=2025-12-01
  &endDate=2025-12-31
  &requiredAttendees=4
  ```
  - Expected: Dates sorted by availability (best first)
  - Verify: Only includes dates with >= 4 available

---

## 8. Edge Cases & Validation

### Leave Request Validation
- [ ] **Overlapping Leave Requests**
  - Create request for Dec 20-24
  - Try creating another for Dec 22-26
  - Expected: Error - "Leave request overlaps with existing leave"

- [ ] **Insufficient Balance**
  - Try requesting more days than available
  - Expected: Error - "Insufficient leave balance"

- [ ] **Invalid Date Range**
  - Try `endDate` before `startDate`
  - Expected: Error - "End date must be after start date"

- [ ] **Minimum Notice Period**
  - Try requesting leave starting tomorrow (if minNoticeDays > 1)
  - Expected: Error - "Minimum notice period is X days"

- [ ] **Maximum Consecutive Days**
  - Request more days than `maxConsecutiveDays` allows
  - Expected: Error - "Maximum consecutive days allowed is X"

### Balance Allocation Validation
- [ ] **Duplicate Allocation**
  - Try allocating same employee/leave type/year twice
  - Expected: Error - "Leave balance already exists"

- [ ] **Negative Values**
  - Try allocating negative days
  - Expected: Validation error

### Weekend & Holiday Exclusion
- [ ] **Weekend Exclusion**
  - Request leave from Friday to Monday (includes Sat-Sun)
  - Verify: Only Friday and Monday counted (2 days, not 4)

- [ ] **Holiday Exclusion**
  - Request leave including a public holiday
  - Verify: Holiday not counted in total days

---

## 9. Authorization Tests

### Access Control
- [ ] **Employee accessing other's balances**
  - As EMPLOYEE, try `GET /api/v1/leave-balances/employee/{otherId}`
  - Expected: 403 Forbidden (unless MANAGER/HR/ADMIN)

- [ ] **Employee approving leave**
  - As EMPLOYEE, try approving a request
  - Expected: 403 Forbidden

- [ ] **Manager accessing HR endpoints**
  - As MANAGER, try deleting leave type
  - Expected: 403 Forbidden (only ADMIN can delete)

- [ ] **Unauthenticated access**
  - Try any endpoint without JWT token
  - Expected: 401 Unauthorized

---

## 10. Performance & Stress Tests

### Load Tests
- [ ] **Bulk Leave Requests**
  - Create 100+ leave requests rapidly
  - Verify: All processed correctly
  - Check: Database connection pool handling

- [ ] **Paginated Results**
  - Create 50+ leave types
  - Test pagination with different page sizes
  - Verify: Correct total elements and pages

- [ ] **Large Date Ranges**
  - Get calendar for entire year (365 days)
  - Verify: Response time < 2 seconds

---

## 11. Data Integrity Tests

### Balance Consistency
- [ ] **Create → Approve → Cancel Flow**
  1. Note initial balance
  2. Create request (balance.pending increases)
  3. Approve request (pending → used)
  4. Cancel request (used → available)
  5. Verify: Final balance = initial balance

- [ ] **Create → Reject Flow**
  1. Note initial balance
  2. Create request (balance.pending increases)
  3. Reject request (pending → available)
  4. Verify: Final balance = initial balance

### Concurrent Requests
- [ ] **Simultaneous Leave Requests**
  - Submit 5 requests simultaneously for same employee
  - Verify: All processed correctly without balance corruption
  - Check: No negative balances

---

## 12. Health Checks

- [ ] **Leave Types Health** - `GET /api/v1/leave-types/health`
  - Expected: 200 OK - "Leave type endpoints are working"

- [ ] **Leave Balances Health** - `GET /api/v1/leave-balances/health`
  - Expected: 200 OK - "Leave balance endpoints are working"

- [ ] **Leave Requests Health** - `GET /api/v1/leave-requests/health`
  - Expected: 200 OK - "Leave request endpoints are working"

---

## Test Data Cleanup

After testing, clean up:
- [ ] Delete test leave requests
- [ ] Delete test leave balances
- [ ] Delete test leave types
- [ ] Restore any modified production data

---

## Automated Testing Recommendations

### Unit Tests
- Service layer business logic
- Balance calculations
- Date calculations (weekend/holiday exclusion)
- Validation rules

### Integration Tests
- Controller endpoints
- Repository queries
- Service interactions
- Transaction handling

### End-to-End Tests
- Complete leave request workflow
- Multi-level approval flow
- Balance lifecycle management

---

## Known Issues & Workarounds

Document any issues found during testing:

| Issue | Severity | Workaround | Status |
|-------|----------|------------|--------|
| Example: Calendar slow for large teams | Medium | Use date range filters | Open |

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
