package com.cre.hrms.dto.leave

import com.cre.hrms.core.enums.LeaveDayType
import com.cre.hrms.core.enums.LeaveRequestStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class LeaveRequestResponse(
    val id: UUID,
    val employeeId: UUID,
    val employeeName: String?,
    val leaveTypeId: UUID,
    val leaveTypeName: String,
    val leaveTypeCode: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startDayType: LeaveDayType,
    val endDayType: LeaveDayType,
    val totalDays: BigDecimal,
    val status: LeaveRequestStatus,
    val reason: String?,
    val documentUrl: String?,
    val approverId: UUID?,
    val approverName: String?,
    val approvedAt: LocalDateTime?,
    val rejectionReason: String?,
    val modificationNote: String?,
    val cancelledAt: LocalDateTime?,
    val cancellationReason: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
