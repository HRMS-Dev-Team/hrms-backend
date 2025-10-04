# Security Integration Guide for HRMS Microservices

This guide shows how to secure your other microservices (employee, leave, salary-advance) using the JWT tokens from the auth service.

## Step 1: Add Security Dependency

For each microservice (`hrms-employee`, `hrms-leave`, `hrms-salary-advance`), update their `build.gradle.kts`:

```kotlin
dependencies {
    // Add security library
    implementation(project(":libs:security"))

    // Existing dependencies...
    implementation(project(":libs:persistence"))
    implementation(project(":libs:dto"))
    implementation(project(":libs:config"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
}
```

## Step 2: Create Security Configuration

Create `SecurityConfig.java` in each service's config package:

### For Employee Service

File: `apps/hrms-employee/src/main/java/com/cre/hrms/employee/config/SecurityConfig.java`

```java
package com.cre.hrms.employee.config;

import com.cre.hrms.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/api/v1/employees/public/**"  // Public endpoints if any
                        ).permitAll()
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

### For Leave Service

File: `apps/hrms-leave/src/main/java/com/cre/hrms/leave/config/SecurityConfig.java`

```java
package com.cre.hrms.leave.config;

import com.cre.hrms.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
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

### For Salary Advance Service

File: `apps/hrms-salary-advance/src/main/java/com/cre/hrms/salaryadvance/config/SecurityConfig.java`

```java
package com.cre.hrms.salaryadvance.config;

import com.cre.hrms.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
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

## Step 3: Update Application Configuration

Add JWT configuration to each service's `application.yml`:

```yaml
jwt:
  secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
  expiration: 86400000 # 24 hours
  refresh-token:
    expiration: 604800000 # 7 days
```

## Step 4: Update Main Application Class

Add component scanning for security package:

### Employee Service

```java
package com.cre.hrms.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.cre.hrms.employee",
        "com.cre.hrms.security"  // Add this line
})
public class EmployeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }
}
```

Repeat for Leave and Salary Advance services.

## Step 5: Secure Your Controllers

Use `@PreAuthorize` annotations to protect endpoints:

### Example: Employee Controller

```java
package com.cre.hrms.employee.controller;

import com.cre.hrms.security.authorization.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    // Only HR and ADMIN can view all employees
    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public List<Employee> getAllEmployees() {
        // Implementation
    }

    // Any authenticated user can view their own profile
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Employee getMyProfile() {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        // Get employee by username
    }

    // Only ADMIN can create employees
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Employee createEmployee(@RequestBody Employee employee) {
        // Implementation
    }

    // HR and ADMIN can update employees
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        // Implementation
    }

    // Only ADMIN can delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(@PathVariable Long id) {
        // Implementation
    }
}
```

### Example: Leave Controller

```java
package com.cre.hrms.leave.controller;

import com.cre.hrms.security.authorization.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leaves")
public class LeaveController {

    // Employees can submit leave requests
    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    public LeaveRequest submitLeaveRequest(@RequestBody LeaveRequest request) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        // Set employee from authenticated user
    }

    // Managers and HR can approve/reject
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR', 'ADMIN')")
    public LeaveRequest approveLeave(@PathVariable Long id) {
        // Implementation
    }

    // Employees can view their own leaves
    @GetMapping("/my-leaves")
    @PreAuthorize("isAuthenticated()")
    public List<LeaveRequest> getMyLeaves() {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        // Get leaves for current user
    }

    // HR can view all leaves
    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public List<LeaveRequest> getAllLeaves() {
        // Implementation
    }
}
```

### Example: Salary Advance Controller

```java
package com.cre.hrms.salaryadvance.controller;

import com.cre.hrms.security.authorization.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/salary-advances")
public class SalaryAdvanceController {

    // Employees can request salary advance
    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    public SalaryAdvance requestAdvance(@RequestBody SalaryAdvance request) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        // Implementation
    }

    // HR and ADMIN can approve
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public SalaryAdvance approveAdvance(@PathVariable Long id) {
        // Implementation
    }

    // Employees can view their own requests
    @GetMapping("/my-requests")
    @PreAuthorize("isAuthenticated()")
    public List<SalaryAdvance> getMyRequests() {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        // Implementation
    }

    // HR can view all requests
    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public List<SalaryAdvance> getAllRequests() {
        // Implementation
    }
}
```

## Step 6: Testing Protected Endpoints

### 1. Get a token from auth service:

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123456"}'
```

### 2. Use the token to access protected endpoints:

```bash
# Access employee service
curl -X GET http://localhost:8082/api/v1/employees \
  -H "Authorization: Bearer <your_access_token>"

# Access leave service
curl -X GET http://localhost:8083/api/v1/leaves/my-leaves \
  -H "Authorization: Bearer <your_access_token>"

# Access salary advance service
curl -X GET http://localhost:8084/api/v1/salary-advances/my-requests \
  -H "Authorization: Bearer <your_access_token>"
```

## Summary

Your HRMS system now has:

1. ✅ Centralized authentication service with JWT tokens
2. ✅ Secure endpoints across all microservices
3. ✅ Role-based access control (ADMIN, HR, MANAGER, EMPLOYEE)
4. ✅ Stateless authentication
5. ✅ Password encryption
6. ✅ Token refresh mechanism

All microservices can validate JWT tokens independently using the shared security library!
