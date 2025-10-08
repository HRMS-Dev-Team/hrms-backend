package com.cre.hrms.auth.controller

import com.cre.hrms.auth.service.AuthenticationService
import com.cre.hrms.dto.auth.AuthenticationResponse
import com.cre.hrms.dto.auth.LoginRequest
import com.cre.hrms.dto.auth.RegisterRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<AuthenticationResponse> {
        return try {
            val response = authenticationService.register(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Registration failed: ${e.message}")
        }
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<AuthenticationResponse> {
        return try {
            val response = authenticationService.login(request)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Authentication failed: ${e.message}")
        }
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestHeader("Authorization") refreshTokenHeader: String
    ): ResponseEntity<AuthenticationResponse> {
        return try {
            val refreshToken = if (refreshTokenHeader.startsWith("Bearer ")) {
                refreshTokenHeader.substring(7)
            } else {
                refreshTokenHeader
            }
            val response = authenticationService.refreshToken(refreshToken)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Token refresh failed: ${e.message}")
        }
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Auth service is running")
    }
}
