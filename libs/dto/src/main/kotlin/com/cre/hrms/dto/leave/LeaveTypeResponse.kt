package com.cre.hrms.dto.leave

import com.cre.hrms.core.enums.LeaveCategory
import java.time.LocalDateTime
import java.util.UUID

data class LeaveTypeResponse(
    val id: UUID,
    val name: String,
    val code: String,
    val category: LeaveCategory,
    val description: String?,
    val companyId: UUID,
    val defaultDaysPerYear: Int?,
    val maxConsecutiveDays: Int?,
    val requiresDocument: Boolean,
    val minNoticeDays: Int,
    val isPaid: Boolean,
    val isActive: Boolean,
    val allowCarryForward: Boolean,
    val maxCarryForwardDays: Int?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
