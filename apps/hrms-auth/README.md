# Authentication & Authorization Service

A secure JWT-based authentication and authorization service for the HRMS system with role-based access control and Kafka event integration.

## 📚 Documentation

- **[Complete API Documentation](AUTH_API_DOCUMENTATION.md)** - Full API reference with examples
- **[API Testing Checklist](API_TESTING_CHECKLIST.md)** - Comprehensive testing guide
- **[Quick Reference](API_QUICK_REFERENCE.md)** - Quick lookup for common operations

## 🚀 Features

### Core Functionality
- ✅ **User Registration** - Create new user accounts with role assignment
- ✅ **User Authentication** - JWT-based login with access and refresh tokens
- ✅ **Token Refresh** - Seamless token renewal without re-authentication
- ✅ **Role-Based Access Control** - 4-tier role hierarchy (ADMIN, HR, MANAGER, EMPLOYEE)
- ✅ **Password Security** - BCrypt hashing with validation rules

### Advanced Features
- ✅ **JWT Token Management** - Separate access (24h) and refresh (7d) tokens
- ✅ **Employee Context** - employeeId embedded in JWT claims
- ✅ **Kafka Integration** - Event-driven employee creation
- ✅ **Spring Security** - Method-level security with @PreAuthorize
- ✅ **Custom Claims** - Extensible JWT payload with user context
- ✅ **Health Checks** - Service availability monitoring

## 🏗️ Architecture

### Tech Stack
- **Language:** Kotlin 1.9.21
- **Framework:** Spring Boot 3.2.0
- **Security:** Spring Security 6.x with JWT
- **Database:** PostgreSQL with Flyway migrations
- **Messaging:** Apache Kafka for event streaming
- **Token Format:** JSON Web Tokens (JWT) with HS256 signing

### Project Structure
```
apps/hrms-auth/
├── src/main/kotlin/com/cre/hrms/auth/
│   ├── controller/          # REST endpoints
│   │   ├── AuthenticationController.kt
│   │   └── UserController.kt
│   ├── service/             # Business logic
│   │   ├── AuthenticationService.kt
│   │   ├── JwtService.kt
│   │   └── UserService.kt
│   ├── entity/              # Database entities
│   │   └── User.kt
│   ├── repository/          # Data access
│   │   └── UserRepository.kt
│   ├── config/              # Configuration
│   │   └── SecurityConfig.kt
│   └── kafka/               # Event producers
│       └── EmployeeEventProducer.kt
└── src/main/resources/
    └── db/migration/        # Database migrations
```

## 🔧 Setup & Configuration

### Prerequisites
- Java 17+
- PostgreSQL database
- Kafka (for event streaming)

### Database Setup
```sql
-- Create database
CREATE DATABASE hrms_auth;

-- Migrations run automatically with Flyway
-- Located in: src/main/resources/db/migration/
```

### Running the Service
```bash
# From project root
./gradlew :apps:hrms-auth:bootRun

# Service will start on port 8081
```

### Environment Variables
```properties
# Application
server.port=8081

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms_auth
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# JWT Configuration
jwt.secret=${JWT_SECRET:your-secret-key-at-least-256-bits}
jwt.expiration=86400000          # 24 hours in milliseconds
jwt.refresh-token.expiration=604800000  # 7 days in milliseconds

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
```

## 📊 API Overview

### Base URL
```
http://localhost:8081
```

### Endpoint Categories

**Authentication** - `/api/v1/auth`
- Register new users
- Login and get tokens
- Refresh access tokens
- 4 endpoints total

**User Management** - `/api/v1/users`
- Get current user info
- Role-based test endpoints
- 4 endpoints total

### Total: 8 API Endpoints

## 🎯 Quick Start Examples

### 1. Register New User
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

**Response:**
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

### 2. Login
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "SecurePassword123!"
  }'
```

### 3. Get Current User
```bash
curl -X GET http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 4. Refresh Token
```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Authorization: Bearer YOUR_REFRESH_TOKEN"
```

## 🔐 Security & Authorization

### Role Hierarchy
```
ADMIN > HR > MANAGER > EMPLOYEE
```

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full system access, user management, system configuration |
| **HR** | Employee management, leave/salary administration, department management |
| **MANAGER** | Team management, approval workflows, team reports |
| **EMPLOYEE** | Personal data access, leave requests, profile management |

### JWT Token Structure

**Access Token Claims:**
```json
{
  "sub": "john.doe",
  "employeeId": "770e8400-e29b-41d4-a716-446655440000",
  "roles": ["ROLE_EMPLOYEE"],
  "iat": 1634567890,
  "exp": 1634654290
}
```

**Token Lifecycle:**
- Access Token: 24 hours (used for API authentication)
- Refresh Token: 7 days (used to obtain new access tokens)

### Password Requirements
- ✅ Minimum 8 characters
- ✅ Hashed with BCrypt
- ✅ Not stored in plain text
- ✅ Validated on registration

## 📈 Authentication Flow

```
┌─────────────┐
│   Register  │
│     or      │──────┐
│    Login    │      │
└─────────────┘      │
                     ↓
            ┌─────────────────┐
            │  Receive Tokens │
            │  - Access Token │
            │  - Refresh Token│
            └─────────────────┘
                     ↓
            ┌─────────────────┐
            │   Use Access    │
            │  Token for API  │
            │    Requests     │
            └─────────────────┘
                     ↓
            ┌─────────────────┐
            │ Token Expires?  │──No──┐
            │   (24 hours)    │      │
            └─────────────────┘      │
                     │                │
                    Yes               │
                     ↓                │
            ┌─────────────────┐      │
            │ Use Refresh     │      │
            │ Token to Get    │      │
            │ New Access Token│      │
            └─────────────────┘      │
                     │                │
                     └────────────────┘

            ┌─────────────────┐
            │Refresh Expires? │
            │   (7 days)      │
            └─────────────────┘
                     │
                    Yes
                     ↓
            ┌─────────────────┐
            │  Login Again    │
            └─────────────────┘
```

## 🔄 Kafka Integration

### Employee Creation Event

When a user registers, the auth service publishes an event to Kafka:

**Topic:** `employee-events`

**Event Payload:**
```json
{
  "eventType": "EMPLOYEE_CREATED",
  "employeeId": "uuid-here",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "username": "john.doe"
}
```

**Consumer:** Employee Service
- Listens for events
- Creates employee record
- Links to user account via employeeId

## 📝 Data Models

### Key Entities

**User**
```kotlin
data class User(
    val id: UUID,
    val username: String,
    val email: String,
    val password: String,  // BCrypt hashed
    val firstName: String,
    val lastName: String,
    val employeeId: UUID?,
    val roles: Set<Role>,
    val enabled: Boolean,
    val accountNonLocked: Boolean
)
```

**Role Enum**
```kotlin
enum class Role {
    ADMIN,
    HR,
    MANAGER,
    EMPLOYEE
}
```

## 🧪 Testing

### Health Check
```bash
# Check service health
curl http://localhost:8081/api/v1/auth/health
```

**Response:** `"Auth service is running"`

### Testing Tools
- See [API_TESTING_CHECKLIST.md](API_TESTING_CHECKLIST.md) for comprehensive test scenarios
- Includes 15 test categories with 100+ test cases
- Covers registration, login, token refresh, authorization, validation, and security

## 🔄 Integration with Other Services

### Employee Service
```
Auth Service                    Employee Service
     │                                 │
     │ 1. User registers               │
     │──────────────────────────────> │
     │                                 │
     │ 2. Kafka event: EMPLOYEE_CREATED
     │─────────────────────────────> │
     │                                 │
     │                           3. Create employee
     │                           4. Return employeeId
     │ <──────────────────────────────│
     │                                 │
     │ 5. Update user.employeeId       │
     │                                 │
```

### Leave Service
- **Authentication:** Validates JWT tokens from Auth service
- **Authorization:** Uses roles from JWT for access control
- **Context:** Extracts employeeId from token claims

### Salary Advance Service
- Similar JWT validation pattern
- Role-based access control
- Employee context extraction

## 🔧 Using JWT Tokens in Other Services

### Add Security Dependency
```kotlin
implementation(project(":libs:security"))
```

### Configure Security
```kotlin
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthenticationFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/public/**").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
```

### Use Authorization Annotations
```kotlin
@RestController
@RequestMapping("/api/v1/employees")
class EmployeeController {

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getAllEmployees(): List<Employee> {
        // Only HR and ADMIN can access
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createEmployee(@RequestBody employee: Employee): Employee {
        // Only ADMIN can create
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentEmployee(): Employee {
        val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
        // Any authenticated user can access
    }
}
```

## 🐛 Common Issues & Solutions

### "Username already exists"
**Solution:** Use a different username or check if user is already registered

### "Authentication failed: Bad credentials"
**Solution:**
- Verify username and password are correct
- Check caps lock is off
- Ensure account is not locked or disabled

### "Token refresh failed: Invalid refresh token"
**Solution:**
- Login again to get new tokens
- Check token hasn't expired (7 days)
- Verify you're using refresh token, not access token

### "Access Denied" (403 Forbidden)
**Solution:**
- Check user has required role
- Verify token is valid and not expired
- Ensure correct endpoint is being accessed

## 📖 Additional Resources

- **Full API Docs**: [AUTH_API_DOCUMENTATION.md](AUTH_API_DOCUMENTATION.md)
- **Testing Guide**: [API_TESTING_CHECKLIST.md](API_TESTING_CHECKLIST.md)
- **Quick Reference**: [API_QUICK_REFERENCE.md](API_QUICK_REFERENCE.md)

## 🤝 Contributing

1. Follow existing code patterns
2. Add tests for new features
3. Update documentation
4. Follow Kotlin coding conventions

## 🔒 Security Best Practices

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
✅ SQL injection prevention via JPA

### Password Security
✅ BCrypt hashing with salt
✅ Minimum length enforcement
✅ Never store plain text passwords
✅ Secure password reset flow (if implemented)

## 📄 License

Part of the HRMS Backend System

---

**Version:** 1.0
**Last Updated:** October 12, 2025
**Service Port:** 8081
**Status:** Production Ready ✅
