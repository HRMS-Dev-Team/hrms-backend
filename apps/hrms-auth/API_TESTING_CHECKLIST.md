# Authentication Service - API Testing Checklist

## Prerequisites
- [ ] Database is running and migrations are applied
- [ ] Auth service is running on port 8081
- [ ] You have a REST client (Postman, cURL, or similar)

---

## 1. User Registration - Basic Flow

### Setup Test Data
```json
{
  "username": "test.user",
  "email": "test.user@company.com",
  "password": "SecurePassword123!",
  "firstName": "Test",
  "lastName": "User",
  "roles": ["EMPLOYEE"]
}
```

### Tests
- [ ] **Register New User** - `POST /api/v1/auth/register`
  - Expected: 201 Created with JWT tokens
  - Save `accessToken` and `refreshToken` for subsequent tests
  - Verify: Response contains username and email
  - Verify: Token type is "Bearer"
  - Verify: expiresIn = 86400000 (24 hours)

- [ ] **Verify User Created in Database**
  - Check: User exists in users table
  - Check: Password is hashed (not plain text)
  - Check: Roles are correctly assigned
  - Check: Account is enabled and not locked

---

## 2. User Login - Authentication Flow

### Tests
- [ ] **Login with Valid Credentials** - `POST /api/v1/auth/login`
  ```json
  {
    "username": "test.user",
    "password": "SecurePassword123!"
  }
  ```
  - Expected: 200 OK with JWT tokens
  - Save new tokens for testing
  - Verify: accessToken is different from registration token
  - Verify: Response structure matches registration response

- [ ] **Login with Email (if supported)**
  - Try logging in with email instead of username
  - Document behavior

- [ ] **Verify Token Claims**
  - Decode JWT token (use jwt.io)
  - Verify: `sub` claim contains username
  - Verify: `employeeId` claim exists
  - Verify: `roles` claim contains correct roles (e.g., ["ROLE_EMPLOYEE"])
  - Verify: `iat` (issued at) and `exp` (expiry) claims are present
  - Verify: Token expires in 24 hours

---

## 3. Token Refresh Flow

### Tests
- [ ] **Refresh Access Token** - `POST /api/v1/auth/refresh`
  - Use refresh token from login in Authorization header
  ```
  Authorization: Bearer <refresh_token>
  ```
  - Expected: 200 OK with new access token and refresh token
  - Save new tokens
  - Verify: New accessToken is different from old one

- [ ] **Use New Access Token**
  - Call authenticated endpoint with new access token
  - Expected: Works correctly

- [ ] **Verify Old Access Token Still Valid**
  - Try using old access token immediately after refresh
  - Expected: Should still work (not invalidated)
  - Note: Token invalidation depends on implementation

---

## 4. User Management - Current User Info

### Tests
- [ ] **Get Current User Info** - `GET /api/v1/users/me`
  - Use valid access token
  - Expected: 200 OK with username and roles
  ```json
  {
    "username": "test.user",
    "roles": ["ROLE_EMPLOYEE"]
  }
  ```

- [ ] **Verify Roles Format**
  - Check: Roles have "ROLE_" prefix
  - Verify: All assigned roles are present

---

## 5. Role-Based Access Control Tests

### ADMIN Role Tests
- [ ] **Register ADMIN User**
  ```json
  {
    "username": "admin.user",
    "email": "admin@company.com",
    "password": "AdminPassword123!",
    "firstName": "Admin",
    "lastName": "User",
    "roles": ["ADMIN"]
  }
  ```
  - Expected: 201 Created

- [ ] **Login as ADMIN** - Save admin tokens

- [ ] **Access Admin Endpoint** - `GET /api/v1/users/admin`
  - Use admin access token
  - Expected: 200 OK - "This is an admin-only endpoint"

### HR Role Tests
- [ ] **Register HR User**
  ```json
  {
    "username": "hr.user",
    "email": "hr@company.com",
    "password": "HrPassword123!",
    "firstName": "HR",
    "lastName": "User",
    "roles": ["HR"]
  }
  ```

- [ ] **Login as HR** - Save HR tokens

- [ ] **Access HR Endpoint** - `GET /api/v1/users/hr`
  - Use HR access token
  - Expected: 200 OK - "This is an HR or Admin endpoint"

- [ ] **Access Admin Endpoint as HR**
  - Use HR token on `/api/v1/users/admin`
  - Expected: 403 Forbidden

### MANAGER Role Tests
- [ ] **Register MANAGER User**
  ```json
  {
    "username": "manager.user",
    "email": "manager@company.com",
    "password": "ManagerPassword123!",
    "firstName": "Manager",
    "lastName": "User",
    "roles": ["MANAGER"]
  }
  ```

- [ ] **Login as MANAGER** - Save manager tokens

- [ ] **Access Manager Endpoint** - `GET /api/v1/users/manager`
  - Expected: 200 OK - "This is a Manager or Admin endpoint"

### Multiple Roles Test
- [ ] **Register User with Multiple Roles**
  ```json
  {
    "username": "multi.role",
    "email": "multi@company.com",
    "password": "MultiRole123!",
    "firstName": "Multi",
    "lastName": "Role",
    "roles": ["EMPLOYEE", "MANAGER"]
  }
  ```
  - Expected: 201 Created
  - Verify: User can access both EMPLOYEE and MANAGER endpoints

---

## 6. Validation Tests

### Registration Validation
- [ ] **Duplicate Username**
  - Try registering with existing username
  - Expected: 400 Bad Request - "Username already exists"

- [ ] **Duplicate Email**
  - Try registering with existing email
  - Expected: 400 Bad Request - "Email already exists"

- [ ] **Short Username** (less than 3 characters)
  ```json
  {"username": "ab", ...}
  ```
  - Expected: 400 Bad Request - "Username must be between 3 and 50 characters"

- [ ] **Short Password** (less than 8 characters)
  ```json
  {"password": "Short1!", ...}
  ```
  - Expected: 400 Bad Request - "Password must be at least 8 characters"

- [ ] **Invalid Email Format**
  ```json
  {"email": "not-an-email", ...}
  ```
  - Expected: 400 Bad Request - "Email should be valid"

- [ ] **Missing Required Fields**
  - Try omitting username, email, password, firstName, lastName
  - Expected: 400 Bad Request with validation errors

- [ ] **Empty String Fields**
  ```json
  {"username": "", "email": "", ...}
  ```
  - Expected: 400 Bad Request

### Login Validation
- [ ] **Invalid Username**
  ```json
  {"username": "nonexistent", "password": "anything"}
  ```
  - Expected: 401 Unauthorized - "Authentication failed: Bad credentials"

- [ ] **Invalid Password**
  ```json
  {"username": "test.user", "password": "wrongpassword"}
  ```
  - Expected: 401 Unauthorized - "Authentication failed: Bad credentials"

- [ ] **Missing Username**
  - Expected: 400 Bad Request

- [ ] **Missing Password**
  - Expected: 400 Bad Request

---

## 7. Token Security Tests

### Invalid Token Tests
- [ ] **Access Protected Endpoint without Token**
  - Call `/api/v1/users/me` without Authorization header
  - Expected: 401 Unauthorized

- [ ] **Access with Invalid Token Format**
  ```
  Authorization: Bearer invalid-token-format
  ```
  - Expected: 401 Unauthorized

- [ ] **Access with Malformed Token**
  ```
  Authorization: NotBearer token
  ```
  - Expected: 401 Unauthorized

- [ ] **Access with Expired Token**
  - Use a token that has passed its expiry time
  - Expected: 401 Unauthorized
  - Note: May need to manually create expired token or wait 24 hours

### Token Refresh Security
- [ ] **Refresh with Access Token**
  - Try using access token (not refresh token) on `/api/v1/auth/refresh`
  - Expected: 401 Unauthorized - "Token refresh failed: Invalid refresh token"

- [ ] **Refresh with Expired Refresh Token**
  - Use refresh token that has expired (7+ days old)
  - Expected: 401 Unauthorized

- [ ] **Refresh without Token**
  - Call refresh endpoint without Authorization header
  - Expected: 401 Unauthorized

---

## 8. Authorization Tests

### EMPLOYEE Role Restrictions
- [ ] **EMPLOYEE Accessing Admin Endpoint**
  - Use EMPLOYEE token on `/api/v1/users/admin`
  - Expected: 403 Forbidden - "Access Denied"

- [ ] **EMPLOYEE Accessing HR Endpoint**
  - Use EMPLOYEE token on `/api/v1/users/hr`
  - Expected: 403 Forbidden

- [ ] **EMPLOYEE Accessing Manager Endpoint**
  - Use EMPLOYEE token on `/api/v1/users/manager`
  - Expected: 403 Forbidden

### MANAGER Role Restrictions
- [ ] **MANAGER Accessing Admin Endpoint**
  - Expected: 403 Forbidden

- [ ] **MANAGER Accessing HR Endpoint**
  - Expected: 403 Forbidden

### HR Role Access
- [ ] **HR Accessing Manager Endpoint**
  - Expected: 403 Forbidden (unless role hierarchy allows)

- [ ] **HR Accessing Admin Endpoint**
  - Expected: 403 Forbidden

---

## 9. Health Check Tests

- [ ] **Auth Health Check** - `GET /api/v1/auth/health`
  - Expected: 200 OK - "Auth service is running"
  - No authentication required

---

## 10. Integration Tests

### Employee Service Integration
- [ ] **Register User and Check Employee Creation**
  - Register a new user
  - Check: Employee record created in Employee service (if Kafka event streaming is working)
  - Verify: employeeId is set in User table

### Cross-Service Authentication
- [ ] **Use Auth Token in Leave Service**
  - Login and get access token
  - Call Leave service endpoint with token
  - Expected: Works correctly, employeeId extracted from JWT

- [ ] **Use Auth Token in Employee Service**
  - Call Employee service endpoint with token
  - Expected: Works correctly

---

## 11. Password Security Tests

### Password Requirements
- [ ] **Weak Password Detection** (if implemented)
  - Try: "password", "12345678", "qwerty123"
  - Expected: Should reject common weak passwords

- [ ] **Password Complexity** (if implemented)
  - Verify: Passwords require mix of uppercase, lowercase, numbers, special characters

### Password Hashing
- [ ] **Verify Password Not Stored in Plain Text**
  - Check database: Password field should be hashed
  - Verify: Hash format (BCrypt: starts with $2a$, $2b$, or $2y$)

---

## 12. Edge Cases & Stress Tests

### Concurrent Registrations
- [ ] **Simultaneous Registration with Same Username**
  - Send 2+ registration requests at exact same time with same username
  - Expected: Only one succeeds, others get "Username already exists"

### Case Sensitivity
- [ ] **Username Case Sensitivity**
  - Register "TestUser"
  - Try logging in with "testuser"
  - Document behavior

- [ ] **Email Case Sensitivity**
  - Register "Test@Company.com"
  - Try registering "test@company.com"
  - Document behavior

### Token Load Testing
- [ ] **Multiple Rapid Logins**
  - Login 100 times rapidly
  - Verify: All succeed and return valid tokens

- [ ] **Multiple Rapid Token Refreshes**
  - Refresh token 50 times rapidly
  - Verify: All succeed

### Long-Running Tokens
- [ ] **Token Usage Near Expiry**
  - Use access token just before 24-hour expiry
  - Expected: Still works until exact expiry time

---

## 13. Data Consistency Tests

### User State
- [ ] **User Enabled/Disabled State**
  - Register user (enabled by default)
  - Verify: Can login
  - If admin disables user, verify: Cannot login

- [ ] **Account Locking**
  - Check if account locking is implemented
  - Try multiple failed login attempts
  - Verify: Account locks after threshold (if implemented)

---

## 14. Response Format Tests

### Successful Responses
- [ ] **Verify Response Structure for Registration**
  ```json
  {
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "username": "string",
    "email": "string"
  }
  ```

- [ ] **Verify Response Structure for Login**
  - Same as registration

- [ ] **Verify Response Structure for Refresh**
  - Same as registration/login

### Error Responses
- [ ] **Verify Error Response Format**
  ```json
  {
    "timestamp": "2025-10-12T10:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Specific error message",
    "path": "/api/v1/auth/register"
  }
  ```

- [ ] **Test All HTTP Status Codes**
  - 200 OK - Successful operations
  - 201 Created - Registration
  - 400 Bad Request - Validation errors
  - 401 Unauthorized - Invalid credentials/tokens
  - 403 Forbidden - Insufficient permissions
  - 500 Internal Server Error - Server errors

---

## 15. Performance Tests

### Response Times
- [ ] **Registration Response Time**
  - Expected: < 500ms

- [ ] **Login Response Time**
  - Expected: < 300ms

- [ ] **Token Refresh Response Time**
  - Expected: < 200ms

- [ ] **Get Current User Response Time**
  - Expected: < 100ms

### Concurrent Users
- [ ] **50 Concurrent Logins**
  - Verify: All succeed
  - Check: No database connection pool exhaustion

- [ ] **100 Concurrent Token Refreshes**
  - Verify: All succeed

---

## Test Data Cleanup

After testing, clean up:
- [ ] Delete test users from database
- [ ] Clear any test-related logs
- [ ] Reset any modified configurations

---

## Automated Testing Recommendations

### Unit Tests
- Password hashing functionality
- JWT token generation and validation
- Role assignment logic
- Username/email uniqueness checking

### Integration Tests
- Complete registration flow
- Complete login flow
- Token refresh flow
- Role-based access control

### End-to-End Tests
- Full authentication lifecycle
- Cross-service authentication
- Employee creation via Kafka events

---

## Known Issues & Workarounds

Document any issues found during testing:

| Issue | Severity | Workaround | Status |
|-------|----------|------------|--------|
| Example: Refresh token not rotating | Low | Manually login again | Open |

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
