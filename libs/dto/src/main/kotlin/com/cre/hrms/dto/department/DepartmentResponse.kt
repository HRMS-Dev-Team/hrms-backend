package com.cre.hrms.dto.department

import java.time.LocalDateTime
import java.util.UUID

data class DepartmentResponse(
    val id: UUID,
    val name: String,
    val code: String,
    val description: String?,
    val companyId: UUID,
    val managerId: UUID?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
