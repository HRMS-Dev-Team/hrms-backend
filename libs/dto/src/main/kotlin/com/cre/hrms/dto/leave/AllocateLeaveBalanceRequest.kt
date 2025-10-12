package com.cre.hrms.dto.leave

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.util.UUID

data class AllocateLeaveBalanceRequest(
    @field:NotNull(message = "Employee ID is required")
    val employeeId: UUID,

    @field:NotNull(message = "Leave type ID is required")
    val leaveTypeId: UUID,

    @field:NotNull(message = "Year is required")
    val year: Int,

    @field:NotNull(message = "Total allocated days is required")
    @field:Positive(message = "Total allocated days must be positive")
    val totalAllocated: BigDecimal,

    val carriedForward: BigDecimal? = null
)
