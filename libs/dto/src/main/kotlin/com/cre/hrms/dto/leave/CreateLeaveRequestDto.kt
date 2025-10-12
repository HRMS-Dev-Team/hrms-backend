package com.cre.hrms.dto.leave

import com.cre.hrms.core.enums.LeaveDayType
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.util.UUID

data class CreateLeaveRequestDto(
    @field:NotNull(message = "Leave type ID is required")
    val leaveTypeId: UUID,

    @field:NotNull(message = "Start date is required")
    val startDate: LocalDate,

    @field:NotNull(message = "End date is required")
    val endDate: LocalDate,

    val startDayType: LeaveDayType = LeaveDayType.FULL_DAY,
    val endDayType: LeaveDayType = LeaveDayType.FULL_DAY,

    val reason: String? = null,
    val documentUrl: String? = null
)
