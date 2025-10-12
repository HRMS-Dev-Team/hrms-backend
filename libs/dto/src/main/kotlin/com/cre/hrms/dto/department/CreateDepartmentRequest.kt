package com.cre.hrms.dto.department

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateDepartmentRequest(
    @field:NotBlank(message = "Department name is required")
    val name: String,

    @field:NotBlank(message = "Department code is required")
    val code: String,

    val description: String? = null,

    @field:NotNull(message = "Company ID is required")
    val companyId: UUID,

    val managerId: UUID? = null,

    val isActive: Boolean = true
)
