# Authentication Service - API Documentation

**Base URL:** `http://localhost:8081` (or your configured port)

**Version:** 1.0

**Description:** JWT-based authentication and authorization service for the HRMS system

---

## Table of Contents
1. [Authentication API](#authentication-api)
2. [User Management API](#user-management-api)
3. [Data Models](#data-models)
4. [Security & Authorization](#security--authorization)

---

## Authentication API

### 1. Register New User
**Endpoint:** `POST /api/v1/auth/register`

**Authorization:** Public (no token required)

**Description:** Register a new user account in the system

**Request Body:**
```json
{
  "username": "john.doe",
  "email": "john.doe@company.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["EMPLOYEE"]
}
```

**Validation Rules:**
- `username`: 3-50 characters, required
- `email`: Valid email format, required
- `password`: Minimum 8 characters, required
- `firstName`: Required
- `lastName`: Required
- `roles`: Optional (defaults to `["EMPLOYEE"]`)

**Available Roles:**
```
ADMIN, HR, MANAGER, EMPLOYEE
```

**Response:** `201 Created`
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

**Token Expiry:**
- Access Token: 24 hours (86400000 ms)
- Refresh Token: 7 days (604800000 ms)

**Error Responses:**

**400 Bad Request** - Validation Error
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Username already exists",
  "path": "/api/v1/auth/register"
}
```

**Common Error Messages:**
- "Username already exists"
- "Email already exists"
- "Username must be between 3 and 50 characters"
- "Password must be at least 8 characters"
- "Email should be valid"

---

### 2. Login
**Endpoint:** `POST /api/v1/auth/login`

**Authorization:** Public (no token required)

**Description:** Authenticate user and obtain JWT tokens

**Request Body:**
```json
{
  "username": "john.doe",
  "password": "SecurePassword123!"
}
```

**Response:** `200 OK`
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

**Error Responses:**

**401 Unauthorized** - Invalid Credentials
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication failed: Bad credentials",
  "path": "/api/v1/auth/login"
}
```

**Common Error Messages:**
- "Authentication failed: Bad credentials"
- "User not found"
- "Account is locked"
- "Account is disabled"

---

### 3. Refresh Token
**Endpoint:** `POST /api/v1/auth/refresh`

**Authorization:** Refresh Token Required

**Description:** Obtain a new access token using a valid refresh token

**Headers:**
```
Authorization: Bearer <refresh_token>
```

**Response:** `200 OK`
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

**Error Responses:**

**401 Unauthorized** - Invalid/Expired Token
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token refresh failed: Invalid refresh token",
  "path": "/api/v1/auth/refresh"
}
```

---

## User Management API

All User Management endpoints require authentication via JWT Bearer token:
```
Authorization: Bearer <access_token>
```

---

### 1. Get Current User Info
**Endpoint:** `GET /api/v1/users/me`

**Authorization:** Any authenticated user

**Description:** Get information about the currently authenticated user

**Response:** `200 OK`
```json
{
  "username": "john.doe",
  "roles": ["ROLE_EMPLOYEE", "ROLE_MANAGER"]
}
```

**Usage:**
This endpoint is useful for:
- Profile pages
- Checking user permissions in the UI
- Validating authentication status

---

### 2. Admin Test Endpoint
**Endpoint:** `GET /api/v1/users/admin`

**Authorization:** ADMIN role only

**Description:** Test endpoint to verify ADMIN role access

**Response:** `200 OK`
```json
"This is an admin-only endpoint"
```

**Error Response:**

**403 Forbidden** - Insufficient Permissions
```json
{
  "timestamp": "2025-10-12T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/users/admin"
}
```

---

### 3. HR Test Endpoint
**Endpoint:** `GET /api/v1/users/hr`

**Authorization:** HR or ADMIN role

**Description:** Test endpoint to verify HR or ADMIN role access

**Response:** `200 OK`
```json
"This is an HR or Admin endpoint"
```

---

### 4. Manager Test Endpoint
**Endpoint:** `GET /api/v1/users/manager`

**Authorization:** MANAGER or ADMIN role

**Description:** Test endpoint to verify MANAGER or ADMIN role access

**Response:** `200 OK`
```json
"This is a Manager or Admin endpoint"
```

---

## Data Models

### RegisterRequest
```typescript
{
  username: string;        // 3-50 characters, required
  email: string;           // Valid email, required
  password: string;        // Min 8 characters, required
  firstName: string;       // Required
  lastName: string;        // Required
  roles?: string[];        // Optional, defaults to ["EMPLOYEE"]
}
```

### LoginRequest
```typescript
{
  username: string;        // Required
  password: string;        // Required
}
```

### AuthenticationResponse
```typescript
{
  accessToken: string;     // JWT access token
  refreshToken: string;    // JWT refresh token
  tokenType: string;       // Always "Bearer"
  expiresIn: number;       // Milliseconds (24 hours)
  username: string;        // User's username
  email: string;           // User's email
}
```

---

## Security & Authorization

### Role Hierarchy

```
ADMIN > HR > MANAGER > EMPLOYEE
```

**ADMIN:**
- Full system access
- Can access all endpoints
- Can manage users and system settings

**HR:**
- Employee management
- Leave and salary administration
- Department management

**MANAGER:**
- Team management
- Approval workflows
- Team reports

**EMPLOYEE:**
- Personal data access
- Leave requests
- Profile management

---

### JWT Token Structure

**Access Token Claims:**
```json
{
  "sub": "john.doe",
  "employeeId": "uuid-here",
  "roles": ["ROLE_EMPLOYEE"],
  "iat": 1634567890,
  "exp": 1634654290
}
```

**Token Storage:**
- Store access token for API requests
- Store refresh token securely (HttpOnly cookie recommended)
- Never expose tokens in URLs or logs

---

### Authentication Flow

```
1. Register/Login
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

### Token Refresh Strategy

**Proactive Refresh (Recommended):**
```javascript
// Refresh token 5 minutes before expiry
if (tokenExpiresIn < 5 * 60 * 1000) {
  refreshAccessToken();
}
```

**Reactive Refresh:**
```javascript
// Refresh on 401 Unauthorized
axios.interceptors.response.use(
  response => response,
  async error => {
    if (error.response.status === 401) {
      await refreshAccessToken();
      return axios.request(error.config);
    }
    return Promise.reject(error);
  }
);
```

---

## Testing Examples

### Using cURL

**1. Register**
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

**2. Login**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "SecurePassword123!"
  }'
```

**3. Get Current User**
```bash
curl -X GET http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**4. Refresh Token**
```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Authorization: Bearer YOUR_REFRESH_TOKEN"
```

---

### Using JavaScript/Axios

**Register:**
```javascript
const register = async (userData) => {
  try {
    const response = await axios.post(
      'http://localhost:8081/api/v1/auth/register',
      userData
    );

    // Store tokens
    localStorage.setItem('access_token', response.data.accessToken);
    localStorage.setItem('refresh_token', response.data.refreshToken);

    return response.data;
  } catch (error) {
    console.error('Registration failed:', error.response.data.message);
    throw error;
  }
};
```

**Login:**
```javascript
const login = async (username, password) => {
  try {
    const response = await axios.post(
      'http://localhost:8081/api/v1/auth/login',
      { username, password }
    );

    // Store tokens
    localStorage.setItem('access_token', response.data.accessToken);
    localStorage.setItem('refresh_token', response.data.refreshToken);

    return response.data;
  } catch (error) {
    console.error('Login failed:', error.response.data.message);
    throw error;
  }
};
```

**Axios Interceptor (Auto-attach token):**
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

---

## Health Check

**Endpoint:** `GET /api/v1/auth/health`

**Authorization:** Public

**Response:** `200 OK`
```json
"Auth service is running"
```

---

## Common Issues & Solutions

### Issue: "Username already exists"
**Solution:** Use a different username or check if user is already registered

### Issue: "Authentication failed: Bad credentials"
**Solution:**
- Verify username and password are correct
- Check caps lock is off
- Ensure password is not expired

### Issue: "Token refresh failed: Invalid refresh token"
**Solution:**
- Login again to get new tokens
- Check token hasn't expired
- Verify token is the refresh token, not access token

### Issue: "Access Denied" (403 Forbidden)
**Solution:**
- Check user has required role
- Verify token is valid and not expired
- Ensure correct endpoint is being accessed

---

## Security Best Practices

### Password Requirements
✅ Minimum 8 characters
✅ Mix of uppercase and lowercase
✅ Include numbers and special characters
✅ Avoid common passwords

### Token Security
✅ Never expose tokens in URLs
✅ Don't log tokens
✅ Store refresh tokens securely (HttpOnly cookies)
✅ Implement token rotation
✅ Use HTTPS in production

### API Security
✅ Rate limiting on auth endpoints
✅ Account lockout after failed attempts
✅ CORS configuration
✅ Input validation and sanitization

---

## Error Codes Summary

| Code | Meaning | Common Causes |
|------|---------|---------------|
| 200 | OK | Successful request |
| 201 | Created | User registered successfully |
| 400 | Bad Request | Validation error, invalid input |
| 401 | Unauthorized | Invalid credentials, expired token |
| 403 | Forbidden | Insufficient permissions |
| 500 | Internal Server Error | Server-side error |

---

## Rate Limiting

**Auth Endpoints** (Recommended):
- Registration: 5 requests per 15 minutes per IP
- Login: 10 requests per 15 minutes per IP
- Token Refresh: 100 requests per 15 minutes per user

---

## Integration with Other Services

### Employee Service Integration
After successful registration:
1. Auth service creates user account
2. Employee service creates employee record
3. Link via `employeeId` in user account

### Leave Service Integration
Leave service validates JWT and extracts:
- User ID
- Employee ID
- Roles
- Username

---

**Documentation Version:** 1.0
**Last Updated:** October 12, 2025
**Service Port:** 8081
