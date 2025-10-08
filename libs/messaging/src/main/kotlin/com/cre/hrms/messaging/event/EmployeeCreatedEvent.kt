package com.cre.hrms.messaging.event

import java.util.UUID

data class EmployeeCreatedEvent(
    val employeeId: UUID,
    val employeeNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val companyId: UUID
)
