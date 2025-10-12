package com.cre.hrms.dto.leave

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class LeaveBalanceResponse(
    val id: UUID,
    val employeeId: UUID,
    val leaveTypeId: UUID,
    val leaveTypeName: String,
    val leaveTypeCode: String,
    val year: Int,
    val totalAllocated: BigDecimal,
    val used: BigDecimal,
    val pending: BigDecimal,
    val available: BigDecimal,
    val carriedForward: BigDecimal?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
