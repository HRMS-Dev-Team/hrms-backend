package com.cre.hrms.dto.department

import java.util.UUID

data class UpdateDepartmentRequest(
    val name: String? = null,
    val code: String? = null,
    val description: String? = null,
    val managerId: UUID? = null,
    val isActive: Boolean? = null
)
