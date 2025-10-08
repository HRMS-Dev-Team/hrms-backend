package com.cre.hrms.auth.service

import com.cre.hrms.core.enums.Role
import com.cre.hrms.messaging.event.EmployeeCreatedEvent
import com.cre.hrms.persistence.user.entity.User
import com.cre.hrms.persistence.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun createUserForEmployee(event: EmployeeCreatedEvent) {
        logger.info("Creating user for employee: ${event.employeeId}")

        // Check if email exists
        val email = event.email ?: throw IllegalArgumentException("Employee email is required")

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            logger.warn("User already exists for email: $email")
            return
        }

        // Generate username from employee number
        val username = event.employeeNumber

        // Generate default password (employee number + first 4 chars of last name)
        val defaultPassword = generateDefaultPassword(event.employeeNumber, event.lastName)

        // Create user entity
        val user = User(
            username = username,
            email = email,
            password = passwordEncoder.encode(defaultPassword),
            firstName = event.firstName,
            lastName = event.lastName,
            roles = mutableSetOf(Role.EMPLOYEE)
        )

        userRepository.save(user)

        logger.info("User created successfully for employee: ${event.employeeId}, username: $username, default password: $defaultPassword")
        // In production, you should send the password to the employee via email or other secure channel
    }

    private fun generateDefaultPassword(employeeNumber: String, lastName: String): String {
        val lastNamePrefix = if (lastName.length >= 4) lastName.substring(0, 4) else lastName
        return "${employeeNumber}${lastNamePrefix}@123"
    }
}
