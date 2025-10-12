# Leave Management Service

A comprehensive leave management system for HRMS with multi-level approval workflows, half-day leave support, and automated accrual processing.

## 📚 Documentation

- **[Complete API Documentation](LEAVE_API_DOCUMENTATION.md)** - Full API reference with examples
- **[API Testing Checklist](API_TESTING_CHECKLIST.md)** - Comprehensive testing guide
- **[Quick Reference](API_QUICK_REFERENCE.md)** - Quick lookup for common operations

## 🚀 Features

### Core Functionality
- ✅ **Leave Types Management** - Define company-specific leave types (Annual, Sick, Maternity, etc.)
- ✅ **Balance Management** - Track employee leave balances with carry-forward support
- ✅ **Leave Requests** - Submit, approve, reject, and cancel leave requests
- ✅ **Multi-Level Approval** - Sequential approval workflow (up to 4 levels)
- ✅ **Leave Calendar** - Visual calendar views and team availability tracking

### Advanced Features
- ✅ **Half-Day Leave Support** - Request 0.5 day increments (morning/afternoon)
- ✅ **Smart Working Days Calculation** - Automatically excludes weekends and holidays
- ✅ **Holiday Management** - Track public, company, and optional holidays
- ✅ **Automated Accrual** - Scheduled monthly/yearly leave allocation
- ✅ **Balance Reservation** - Three-state balance system (available/pending/used)
- ✅ **JWT Integration** - Full authentication with employee context
- ✅ **Approval Delegation** - Transfer approval authority to others

## 🏗️ Architecture

### Tech Stack
- **Language:** Kotlin 1.9.21
- **Framework:** Spring Boot 3.2.0
- **Database:** PostgreSQL with Flyway migrations
- **Security:** JWT with Spring Security
- **Scheduling:** Spring @Scheduled for automated jobs

### Project Structure
```
apps/hrms-leave/
├── src/main/kotlin/com/cre/hrms/leave/
│   ├── controller/          # REST endpoints
│   │   ├── LeaveTypeController.kt
│   │   ├── LeaveBalanceController.kt
│   │   ├── LeaveRequestController.kt
│   │   ├── ApprovalWorkflowController.kt
│   │   └── LeaveCalendarController.kt
│   ├── service/             # Business logic
│   │   ├── LeaveTypeService.kt
│   │   ├── LeaveBalanceService.kt
│   │   ├── LeaveRequestService.kt
│   │   ├── ApprovalWorkflowService.kt
│   │   └── LeaveCalendarService.kt
│   ├── mapper/              # Entity-DTO mapping
│   ├── scheduler/           # Automated jobs
│   │   └── LeaveAccrualScheduler.kt
│   └── util/                # Utilities
│       └── WorkingDaysCalculator.kt
└── src/main/resources/
    └── db/migration/        # Database migrations
```

## 🔧 Setup & Configuration

### Prerequisites
- Java 17+
- PostgreSQL database
- Kafka (for event streaming)

### Database Migrations
```bash
# Migrations run automatically with Flyway
# Located in: src/main/resources/db/migration/

V1__Create_leave_types_table.sql
V2__Create_leave_balances_table.sql
V3__Create_leave_requests_table.sql
V4__Insert_default_leave_types.sql
V5__Update_leave_types_for_enhancements.sql
V6__Update_leave_requests_for_half_day_support.sql
V7__Create_holidays_table.sql
V8__Create_approval_workflows_table.sql
```

### Running the Service
```bash
# From project root
./gradlew :apps:hrms-leave:bootRun

# Service will start on port 8083
```

### Environment Variables
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms_leave
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT
jwt.secret=your_secret_key
jwt.expiration=86400000

# Scheduling (enabled by default)
spring.task.scheduling.enabled=true
```

## 📊 API Overview

### Base URL
```
http://localhost:8083
```

### Endpoint Categories

**Leave Types** - `/api/v1/leave-types`
- Manage leave type definitions
- 8 endpoints for CRUD operations

**Leave Balances** - `/api/v1/leave-balances`
- Allocate and track employee balances
- 5 endpoints for balance management

**Leave Requests** - `/api/v1/leave-requests`
- Submit and manage leave requests
- 8 endpoints for full lifecycle

**Approval Workflow** - `/api/leave/approvals`
- Multi-level approval processing
- 5 endpoints for workflow management

**Leave Calendar** - `/api/leave/calendar`
- Calendar views and availability
- 5 endpoints for team coordination

### Total: 31 API Endpoints

## 🎯 Quick Start Examples

### 1. Create Leave Request
```bash
curl -X POST http://localhost:8083/api/v1/leave-requests \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveTypeId": "uuid-here",
    "startDate": "2025-12-20",
    "endDate": "2025-12-24",
    "startDayType": "FULL_DAY",
    "endDayType": "FULL_DAY",
    "reason": "Family vacation"
  }'
```

### 2. Get My Leave Balances
```bash
curl -X GET http://localhost:8083/api/v1/leave-balances/my-balances \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Approve Leave Request
```bash
curl -X PUT http://localhost:8083/api/v1/leave-requests/{id}/approve \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"comments": "Approved"}'
```

## 📅 Scheduled Jobs

### Automatic Accrual System
| Job | Schedule | Description |
|-----|----------|-------------|
| **Monthly Accrual** | 1st of month @ 1:00 AM | Allocate monthly leave balances |
| **Yearly Accrual** | Jan 1 @ 2:00 AM | Allocate annual leave + handle carry-forward |
| **Carry-forward Expiry** | Mar 31 @ 3:00 AM | Expire carried forward balances |
| **Balance Cleanup** | Every Sunday @ 4:00 AM | Remove old balances (3+ years) |

## 🔐 Security & Authorization

### Role-Based Access Control

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full access to all endpoints |
| **HR** | Manage leave types, balances, approve requests |
| **MANAGER** | Approve team requests, view team data |
| **EMPLOYEE** | Submit requests, view own data |

### JWT Integration
All endpoints automatically extract:
- Employee ID from JWT token
- Employee name from JWT token
- User roles for authorization

## 📈 Business Rules

### Leave Request Validation
✅ End date must be >= start date
✅ Minimum notice period respected
✅ Maximum consecutive days limit enforced
✅ Sufficient balance available
✅ No overlapping requests
✅ Document required for specific leave types

### Balance Management
✅ Three-state system: available → pending → used
✅ Automatic reservation on request creation
✅ Automatic release on rejection
✅ Automatic confirmation on approval
✅ Automatic refund on cancellation

### Working Days Calculation
✅ Weekends (Sat-Sun) automatically excluded
✅ Public holidays excluded
✅ Company holidays excluded
✅ Half-day precision (0.5 increments)

## 🧪 Testing

### Health Check Endpoints
```bash
# Check service health
curl http://localhost:8083/api/v1/leave-types/health
curl http://localhost:8083/api/v1/leave-balances/health
curl http://localhost:8083/api/v1/leave-requests/health
```

### Testing Tools
- See [API_TESTING_CHECKLIST.md](API_TESTING_CHECKLIST.md) for comprehensive test scenarios
- Includes 12 test categories with 100+ test cases
- Covers CRUD, workflows, validation, authorization, and edge cases

## 📝 Data Models

### Key Entities
- **LeaveType** - Leave type definitions with policies
- **LeaveBalance** - Employee leave balances per year/type
- **LeaveRequest** - Leave requests with approval status
- **ApprovalWorkflow** - Multi-level approval tracking
- **Holiday** - Public and company holidays

### Enums
- **LeaveCategory**: ANNUAL, SICK, MATERNITY, PATERNITY, UNPAID, COMPASSIONATE, STUDY, OTHER
- **LeaveRequestStatus**: PENDING, APPROVED, REJECTED, CANCELLED, MODIFICATION_REQUESTED
- **LeaveDayType**: FULL_DAY, FIRST_HALF, SECOND_HALF
- **ApprovalLevel**: LEVEL_1, LEVEL_2, LEVEL_3, LEVEL_4

## 🔄 Workflow Examples

### Standard Approval Flow
```
1. Employee creates leave request
   └─> Status: PENDING
   └─> Balance: available → pending

2. Manager approves
   └─> Status: APPROVED
   └─> Balance: pending → used

3. Employee takes leave
   └─> Deducted from balance
```

### Multi-Level Approval Flow
```
1. Employee creates request
   └─> Level 1: PENDING (Manager)
   └─> Level 2: PENDING (Department Head)
   └─> Level 3: PENDING (HR)

2. Manager approves (Level 1)
   └─> Level 1: APPROVED
   └─> Level 2: Now actionable

3. Department Head approves (Level 2)
   └─> Level 2: APPROVED
   └─> Level 3: Now actionable

4. HR approves (Level 3)
   └─> Level 3: APPROVED
   └─> Request Status: APPROVED
```

### Cancellation Flow
```
Pending Request:
  Cancel → pending balance refunded → status: CANCELLED

Approved Request:
  Cancel → used balance refunded → status: CANCELLED
```

## 🐛 Common Issues & Solutions

### "No employee ID found for current user"
**Solution:** Ensure user account has `employeeId` set in database

### "Insufficient leave balance"
**Solution:** Check balance using `/my-balances`, verify pending requests

### "Overlaps with existing leave"
**Solution:** Check existing requests, cancel conflicting ones first

### "Minimum notice period"
**Solution:** Submit requests with adequate advance notice

## 📖 Additional Resources

- **Full API Docs**: [LEAVE_API_DOCUMENTATION.md](LEAVE_API_DOCUMENTATION.md)
- **Testing Guide**: [API_TESTING_CHECKLIST.md](API_TESTING_CHECKLIST.md)
- **Quick Reference**: [API_QUICK_REFERENCE.md](API_QUICK_REFERENCE.md)

## 🤝 Contributing

1. Follow existing code patterns
2. Add tests for new features
3. Update documentation
4. Follow Kotlin coding conventions

## 📄 License

Part of the HRMS Backend System

---

**Version:** 1.0
**Last Updated:** October 12, 2025
**Service Port:** 8083
**Status:** Production Ready ✅
