package com.cre.hrms.dto.leave

import com.cre.hrms.core.enums.LeaveCategory

data class UpdateLeaveTypeRequest(
    val name: String? = null,
    val code: String? = null,
    val category: LeaveCategory? = null,
    val description: String? = null,
    val defaultDaysPerYear: Int? = null,
    val maxConsecutiveDays: Int? = null,
    val requiresDocument: Boolean? = null,
    val minNoticeDays: Int? = null,
    val isPaid: Boolean? = null,
    val isActive: Boolean? = null,
    val allowCarryForward: Boolean? = null,
    val maxCarryForwardDays: Int? = null
)
