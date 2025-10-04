# HRMS Authentication Service

This service provides authentication and authorization functionality for the HRMS system using JWT tokens.

## Features

- User registration with validation
- User login with JWT token generation
- JWT token refresh
- Password encryption using BCrypt
- Role-based access control (RBAC)
- Spring Security integration
- PostgreSQL database with Flyway migrations

## Roles

The system supports the following roles:
- `ADMIN` - Full system access
- `HR` - Human Resources access
- `MANAGER` - Manager-level access
- `EMPLOYEE` - Basic employee access (default)

## API Endpoints

### Public Endpoints (No Authentication Required)

#### Register a new user
```
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "john.doe",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["EMPLOYEE"]
}
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "john.doe",
  "email": "john.doe@example.com"
}
```

#### Login
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "securePassword123"
}
```

Response: Same as registration

#### Refresh Token
```
POST /api/v1/auth/refresh
Authorization: Bearer <refresh_token>
```

Response: New access and refresh tokens

#### Health Check
```
GET /api/v1/auth/health
```

### Protected Endpoints (Authentication Required)

#### Get Current User
```
GET /api/v1/users/me
Authorization: Bearer <access_token>
```

Response:
```json
{
  "username": "john.doe",
  "roles": ["ROLE_EMPLOYEE"]
}
```

#### Admin Only Endpoint
```
GET /api/v1/users/admin
Authorization: Bearer <access_token>
```
Requires: `ADMIN` role

#### HR Endpoint
```
GET /api/v1/users/hr
Authorization: Bearer <access_token>
```
Requires: `HR` or `ADMIN` role

#### Manager Endpoint
```
GET /api/v1/users/manager
Authorization: Bearer <access_token>
```
Requires: `MANAGER` or `ADMIN` role

## Using JWT Tokens in Other Services

To secure other microservices, add the following dependencies:

```kotlin
implementation(project(":libs:security"))
```

Then configure security:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### Using Authorization Annotations

```java
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public List<Employee> getAllEmployees() {
        // Only HR and ADMIN can access
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Employee createEmployee(@RequestBody Employee employee) {
        // Only ADMIN can create
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Employee getCurrentEmployee() {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        // Any authenticated user can access
    }
}
```

## Configuration

### Environment Variables

- `DB_USERNAME` - Database username (default: postgres)
- `DB_PASSWORD` - Database password (default: postgres)
- `JWT_SECRET` - Secret key for JWT signing (use a strong secret in production)

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hrms_auth
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 86400000 # 24 hours
  refresh-token:
    expiration: 604800000 # 7 days

server:
  port: 8081
```

## Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE hrms_auth;
```

2. Run the application - Flyway will automatically create the tables

## Running the Service

```bash
./gradlew :apps:hrms-auth:bootRun
```

The service will start on port 8081.

## Testing

Example using cURL:

```bash
# Register
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123",
    "firstName": "Admin",
    "lastName": "User",
    "roles": ["ADMIN"]
  }'

# Login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Use token to access protected endpoint
curl -X GET http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer <your_access_token>"
```

## Security Features

1. **Password Encryption**: BCrypt with strength 10
2. **JWT Signing**: HS256 algorithm
3. **Token Expiration**: 24 hours for access tokens, 7 days for refresh tokens
4. **Stateless Sessions**: No server-side session storage
5. **Role-Based Access Control**: Fine-grained permissions using Spring Security annotations
6. **Account Status**: Support for account locking, expiration, and credential expiration

## Next Steps

- Implement email verification
- Add password reset functionality
- Implement account lockout after failed login attempts
- Add OAuth2 integration
- Implement audit logging
