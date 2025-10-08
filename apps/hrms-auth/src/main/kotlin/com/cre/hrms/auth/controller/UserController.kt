package com.cre.hrms.auth.controller

import com.cre.hrms.security.authorization.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController {

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(): ResponseEntity<Map<String, Any>> {
        val response = mutableMapOf<String, Any>()
        SecurityUtils.getCurrentUsername().ifPresent { username ->
            response["username"] = username
            response["roles"] = SecurityUtils.getCurrentUserRoles()
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminEndpoint(): ResponseEntity<String> {
        return ResponseEntity.ok("This is an admin-only endpoint")
    }

    @GetMapping("/hr")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun hrEndpoint(): ResponseEntity<String> {
        return ResponseEntity.ok("This is an HR or Admin endpoint")
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    fun managerEndpoint(): ResponseEntity<String> {
        return ResponseEntity.ok("This is a Manager or Admin endpoint")
    }
}
