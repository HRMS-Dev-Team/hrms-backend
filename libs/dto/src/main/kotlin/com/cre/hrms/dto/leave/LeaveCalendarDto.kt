package com.cre.hrms.dto.leave

import com.cre.hrms.core.enums.LeaveDayType
import com.cre.hrms.core.enums.LeaveRequestStatus
import java.time.LocalDate
import java.util.UUID

data class LeaveCalendarEntryDto(
    val date: LocalDate,
    val employeeId: UUID,
    val employeeName: String?,
    val leaveRequestId: UUID,
    val leaveTypeName: String,
    val leaveTypeCode: String,
    val dayType: LeaveDayType,
    val status: LeaveRequestStatus
)

data class TeamLeaveCalendarDto(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val leaveEntries: List<LeaveCalendarEntryDto>,
    val totalEmployees: Int,
    val employeesOnLeave: Map<LocalDate, Int>
)

data class EmployeeAvailabilityDto(
    val employeeId: UUID,
    val employeeName: String?,
    val isAvailable: Boolean,
    val leaveInfo: LeaveCalendarEntryDto?
)

data class DayAvailabilityDto(
    val date: LocalDate,
    val isWorkingDay: Boolean,
    val totalEmployees: Int,
    val availableEmployees: Int,
    val onLeaveEmployees: Int,
    val employeesOnLeave: List<EmployeeAvailabilityDto>
)
