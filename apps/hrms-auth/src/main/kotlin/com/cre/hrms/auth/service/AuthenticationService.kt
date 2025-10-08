package com.cre.hrms.auth.service

import com.cre.hrms.core.enums.Role
import com.cre.hrms.dto.auth.AuthenticationResponse
import com.cre.hrms.dto.auth.LoginRequest
import com.cre.hrms.dto.auth.RegisterRequest
import com.cre.hrms.persistence.user.entity.User
import com.cre.hrms.persistence.user.repository.UserRepository
import com.cre.hrms.security.jwt.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(request: RegisterRequest): AuthenticationResponse {
        // Check if username already exists
        if (userRepository.existsByUsername(request.username)) {
            throw RuntimeException("Username already exists")
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("Email already exists")
        }

        // Set default role if not provided
        val roles = request.roles?.toMutableSet() ?: mutableSetOf(Role.EMPLOYEE)

        // Create new user
        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            roles = roles,
            enabled = true,
            accountNonExpired = true,
            accountNonLocked = true,
            credentialsNonExpired = true
        )

        userRepository.save(user)

        val jwtToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)

        return AuthenticationResponse(
            accessToken = jwtToken,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            expiresIn = 86400000L, // 24 hours
            username = user.username,
            email = user.email
        )
    }

    fun login(request: LoginRequest): AuthenticationResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password
            )
        )

        val user = userRepository.findByUsername(request.username)
            ?: throw RuntimeException("User not found")

        val jwtToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)

        return AuthenticationResponse(
            accessToken = jwtToken,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            expiresIn = 86400000L, // 24 hours
            username = user.username,
            email = user.email
        )
    }

    fun refreshToken(refreshToken: String): AuthenticationResponse {
        val username = jwtService.extractUsername(refreshToken)
            ?: throw RuntimeException("Invalid refresh token")

        val user = userRepository.findByUsername(username)
            ?: throw RuntimeException("User not found")

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw RuntimeException("Invalid refresh token")
        }

        val accessToken = jwtService.generateToken(user)
        val newRefreshToken = jwtService.generateRefreshToken(user)

        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = newRefreshToken,
            tokenType = "Bearer",
            expiresIn = 86400000L,
            username = user.username,
            email = user.email
        )
    }
}
