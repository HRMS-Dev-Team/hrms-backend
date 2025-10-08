package com.cre.hrms.auth.listener

import com.cre.hrms.auth.service.UserService
import com.cre.hrms.messaging.constants.Topics
import com.cre.hrms.messaging.event.EmployeeCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EmployeeEventListener(
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(EmployeeEventListener::class.java)

    @Async
    @KafkaListener(topics = [Topics.EMPLOYEE_CREATED], groupId = "auth-service")
    fun handleEmployeeCreated(event: EmployeeCreatedEvent) {
        logger.info("Received employee created event for employee: ${event.employeeId}")

        try {
            userService.createUserForEmployee(event)
            logger.info("Successfully created user for employee: ${event.employeeId}")
        } catch (e: Exception) {
            logger.error("Failed to create user for employee: ${event.employeeId}", e)
            // In production, you might want to publish a failure event or retry
        }
    }
}
