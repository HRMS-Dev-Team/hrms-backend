package com.cre.hrms.dto.leave

import com.cre.hrms.core.enums.LeaveCategory
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateLeaveTypeRequest(
    @field:NotBlank(message = "Leave type name is required")
    val name: String,

    @field:NotBlank(message = "Leave type code is required")
    val code: String,

    @field:NotNull(message = "Leave category is required")
    val category: LeaveCategory,

    val description: String? = null,

    @field:NotNull(message = "Company ID is required")
    val companyId: UUID,

    val defaultDaysPerYear: Int? = null,
    val maxConsecutiveDays: Int? = null,
    val requiresDocument: Boolean = false,
    val minNoticeDays: Int = 0,
    val isPaid: Boolean = true,
    val isActive: Boolean = true,
    val allowCarryForward: Boolean = false,
    val maxCarryForwardDays: Int? = null
)
