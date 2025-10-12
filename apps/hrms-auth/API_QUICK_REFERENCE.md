# Authentication Service - Quick Reference

## Base URL
```
http://localhost:8081
```

---

## Quick Endpoints Reference

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| **AUTHENTICATION** |
| POST | `/api/v1/auth/register` | Public | Register new user |
| POST | `/api/v1/auth/login` | Public | Login and get tokens |
| POST | `/api/v1/auth/refresh` | Refresh Token | Get new access token |
| GET | `/api/v1/auth/health` | Public | Health check |
| **USER MANAGEMENT** |
| GET | `/api/v1/users/me` | Access Token | Get current user info |
| GET | `/api/v1/users/admin` | ADMIN | Admin-only test endpoint |
| GET | `/api/v1/users/hr` | HR, ADMIN | HR test endpoint |
| GET | `/api/v1/users/manager` | MANAGER, ADMIN | Manager test endpoint |

---

## Common Request Examples

### Register New User
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john.doe@company.com",
    "password": "SecurePassword123!",
    "firstName": "John",
    "lastName": "Doe",
    "roles": ["EMPLOYEE"]
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "SecurePassword123!"
  }'
```

### Get Current User
```bash
curl -X GET http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Refresh Token
```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Authorization: Bearer YOUR_REFRESH_TOKEN"
```

---

## Request/Response Format

### Register/Login Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "john.doe",
  "email": "john.doe@company.com"
}
```

### Get Current User Response
```json
{
  "username": "john.doe",
  "roles": ["ROLE_EMPLOYEE", "ROLE_MANAGER"]
}
```

### Error Response
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Username already exists",
  "path": "/api/v1/auth/register"
}
```

---

## Available Roles

```
ADMIN    - Full system access
HR       - Employee management, leave/salary administration
MANAGER  - Team management, approval workflows
EMPLOYEE - Personal data access, leave requests
```

### Role Hierarchy
```
ADMIN > HR > MANAGER > EMPLOYEE
```

---

## Token Information

### Token Types
- **Access Token**: Used for API authentication
- **Refresh Token**: Used to get new access tokens

### Token Expiry
- **Access Token**: 24 hours (86400000 ms)
- **Refresh Token**: 7 days (604800000 ms)

### JWT Token Claims
```json
{
  "sub": "john.doe",
  "employeeId": "uuid-here",
  "roles": ["ROLE_EMPLOYEE"],
  "iat": 1634567890,
  "exp": 1634654290
}
```

### Using Tokens
```
Authorization: Bearer <access_token>
```

---

## Validation Rules

| Field | Rule |
|-------|------|
| username | 3-50 characters, required, unique |
| email | Valid email format, required, unique |
| password | Minimum 8 characters, required |
| firstName | Required, not blank |
| lastName | Required, not blank |
| roles | Optional (defaults to [EMPLOYEE]) |

---

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Successful request |
| 201 | Created - User registered |
| 400 | Bad Request - Validation error |
| 401 | Unauthorized - Invalid credentials/token |
| 403 | Forbidden - Insufficient permissions |
| 500 | Internal Server Error |

---

## Authentication Flow

```
1. Register or Login
   ↓
2. Receive access_token + refresh_token
   ↓
3. Use access_token for API requests
   ↓
4. When access_token expires (24h)
   ↓
5. Use refresh_token to get new access_token
   ↓
6. When refresh_token expires (7 days)
   ↓
7. User must login again
```

---

## Common Error Messages

### Registration Errors
- `"Username already exists"`
- `"Email already exists"`
- `"Username must be between 3 and 50 characters"`
- `"Password must be at least 8 characters"`
- `"Email should be valid"`

### Login Errors
- `"Authentication failed: Bad credentials"`
- `"User not found"`
- `"Account is locked"`
- `"Account is disabled"`

### Token Errors
- `"Token refresh failed: Invalid refresh token"`
- `"Token expired"`
- `"Invalid token format"`

### Authorization Errors
- `"Access Denied"` (403 Forbidden)

---

## Quick Tips

### Password Security
✅ Minimum 8 characters
✅ Mix of uppercase and lowercase
✅ Include numbers and special characters
✅ Avoid common passwords

### Token Management
✅ Store access token for API requests
✅ Store refresh token securely (HttpOnly cookies recommended)
✅ Never expose tokens in URLs or logs
✅ Refresh proactively before expiry

### Axios Interceptor (Auto-attach Token)
```javascript
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);
```

### Token Refresh Strategy (Proactive)
```javascript
// Refresh token 5 minutes before expiry
if (tokenExpiresIn < 5 * 60 * 1000) {
  refreshAccessToken();
}
```

---

## Postman Environment Variables

```json
{
  "base_url": "http://localhost:8081",
  "access_token": "YOUR_ACCESS_TOKEN",
  "refresh_token": "YOUR_REFRESH_TOKEN",
  "username": "john.doe",
  "email": "john.doe@company.com"
}
```

---

## Database Tables

```
users             - User accounts with credentials
user_roles        - User role assignments
employee_link     - Links to Employee service via employeeId
```

---

## Integration Points

### Employee Service
- After registration, Kafka event triggers employee creation
- User record linked via `employeeId` field

### Leave Service
- Validates JWT tokens
- Extracts employeeId from token claims
- Uses roles for authorization

### Salary Advance Service
- Similar JWT validation
- Role-based access control

---

## Testing Quick Start

```bash
# 1. Register user
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"Test1234!","firstName":"Test","lastName":"User","roles":["EMPLOYEE"]}'

# 2. Login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"Test1234!"}'

# 3. Copy access_token from response

# 4. Get current user
curl -X GET http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer <paste_access_token>"
```

---

## Rate Limiting (Recommended)

| Endpoint | Limit |
|----------|-------|
| /register | 5 requests per 15 minutes per IP |
| /login | 10 requests per 15 minutes per IP |
| /refresh | 100 requests per 15 minutes per user |

---

## Security Best Practices

✅ Use HTTPS in production
✅ Implement rate limiting
✅ Enable account lockout after failed attempts
✅ Configure CORS properly
✅ Validate and sanitize all inputs
✅ Log authentication attempts
✅ Monitor for suspicious activity
✅ Rotate refresh tokens
✅ Implement token blacklisting (optional)

---

**Last Updated:** October 12, 2025
**Version:** 1.0
**Service Port:** 8081
