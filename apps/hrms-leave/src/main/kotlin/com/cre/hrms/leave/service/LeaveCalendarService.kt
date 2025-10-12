package com.cre.hrms.leave.service

import com.cre.hrms.core.enums.LeaveDayType
import com.cre.hrms.core.enums.LeaveRequestStatus
import com.cre.hrms.dto.leave.*
import com.cre.hrms.leave.util.WorkingDaysCalculator
import com.cre.hrms.persistence.leave.repository.LeaveRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class LeaveCalendarService(
    private val leaveRequestRepository: LeaveRequestRepository,
    private val workingDaysCalculator: WorkingDaysCalculator
) {

    /**
     * Get team leave calendar for a date range
     * Shows all approved and pending leaves for the team
     */
    @Transactional(readOnly = true)
    fun getTeamLeaveCalendar(
        employeeIds: List<UUID>,
        startDate: LocalDate,
        endDate: LocalDate,
        includeStatus: List<LeaveRequestStatus> = listOf(LeaveRequestStatus.APPROVED, LeaveRequestStatus.PENDING)
    ): TeamLeaveCalendarDto {
        val leaveRequests = leaveRequestRepository.findByEmployeeIdInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            employeeIds,
            endDate,
            startDate
        ).filter { it.status in includeStatus }

        val leaveEntries = mutableListOf<LeaveCalendarEntryDto>()

        // Expand each leave request into daily entries
        for (leaveRequest in leaveRequests) {
            var currentDate = if (leaveRequest.startDate.isBefore(startDate)) startDate else leaveRequest.startDate
            val lastDate = if (leaveRequest.endDate.isAfter(endDate)) endDate else leaveRequest.endDate

            while (!currentDate.isAfter(lastDate)) {
                // Determine day type for this date
                val dayType = when {
                    currentDate == leaveRequest.startDate && currentDate == leaveRequest.endDate -> {
                        // Single day leave - use start day type
                        leaveRequest.startDayType
                    }
                    currentDate == leaveRequest.startDate -> leaveRequest.startDayType
                    currentDate == leaveRequest.endDate -> leaveRequest.endDayType
                    else -> LeaveDayType.FULL_DAY
                }

                leaveEntries.add(
                    LeaveCalendarEntryDto(
                        date = currentDate,
                        employeeId = leaveRequest.employeeId,
                        employeeName = leaveRequest.employeeName,
                        leaveRequestId = leaveRequest.id!!,
                        leaveTypeName = leaveRequest.leaveType.name,
                        leaveTypeCode = leaveRequest.leaveType.code,
                        dayType = dayType,
                        status = leaveRequest.status
                    )
                )

                currentDate = currentDate.plusDays(1)
            }
        }

        // Calculate employees on leave per day
        val employeesOnLeavePerDay = leaveEntries
            .groupBy { it.date }
            .mapValues { (_, entries) -> entries.distinctBy { it.employeeId }.count() }

        return TeamLeaveCalendarDto(
            startDate = startDate,
            endDate = endDate,
            leaveEntries = leaveEntries.sortedWith(compareBy({ it.date }, { it.employeeName })),
            totalEmployees = employeeIds.size,
            employeesOnLeave = employeesOnLeavePerDay
        )
    }

    /**
     * Check availability of specific employees on a given date
     */
    @Transactional(readOnly = true)
    fun checkEmployeeAvailability(
        employeeIds: List<UUID>,
        date: LocalDate
    ): List<EmployeeAvailabilityDto> {
        val leaveRequests = leaveRequestRepository.findByEmployeeIdInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            employeeIds,
            date,
            date
        ).filter { it.status in listOf(LeaveRequestStatus.APPROVED, LeaveRequestStatus.PENDING) }

        val employeesOnLeave = leaveRequests.associateBy { it.employeeId }

        return employeeIds.map { employeeId ->
            val leaveRequest = employeesOnLeave[employeeId]

            if (leaveRequest != null) {
                // Determine day type for this date
                val dayType = when {
                    date == leaveRequest.startDate && date == leaveRequest.endDate -> leaveRequest.startDayType
                    date == leaveRequest.startDate -> leaveRequest.startDayType
                    date == leaveRequest.endDate -> leaveRequest.endDayType
                    else -> LeaveDayType.FULL_DAY
                }

                EmployeeAvailabilityDto(
                    employeeId = employeeId,
                    employeeName = leaveRequest.employeeName,
                    isAvailable = false,
                    leaveInfo = LeaveCalendarEntryDto(
                        date = date,
                        employeeId = employeeId,
                        employeeName = leaveRequest.employeeName,
                        leaveRequestId = leaveRequest.id!!,
                        leaveTypeName = leaveRequest.leaveType.name,
                        leaveTypeCode = leaveRequest.leaveType.code,
                        dayType = dayType,
                        status = leaveRequest.status
                    )
                )
            } else {
                EmployeeAvailabilityDto(
                    employeeId = employeeId,
                    employeeName = null,
                    isAvailable = true,
                    leaveInfo = null
                )
            }
        }
    }

    /**
     * Get day-by-day availability summary for a date range
     */
    @Transactional(readOnly = true)
    fun getDayAvailabilitySummary(
        employeeIds: List<UUID>,
        startDate: LocalDate,
        endDate: LocalDate,
        companyId: UUID?
    ): List<DayAvailabilityDto> {
        val calendar = getTeamLeaveCalendar(employeeIds, startDate, endDate)
        val results = mutableListOf<DayAvailabilityDto>()

        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            val isWorkingDay = !workingDaysCalculator.isWeekend(currentDate) &&
                    !workingDaysCalculator.isHoliday(currentDate, companyId)

            val employeesOnLeaveToday = calendar.leaveEntries
                .filter { it.date == currentDate }
                .map { entry ->
                    EmployeeAvailabilityDto(
                        employeeId = entry.employeeId,
                        employeeName = entry.employeeName,
                        isAvailable = false,
                        leaveInfo = entry
                    )
                }

            val onLeaveCount = employeesOnLeaveToday.distinctBy { it.employeeId }.count()
            val availableCount = if (isWorkingDay) employeeIds.size - onLeaveCount else 0

            results.add(
                DayAvailabilityDto(
                    date = currentDate,
                    isWorkingDay = isWorkingDay,
                    totalEmployees = employeeIds.size,
                    availableEmployees = availableCount,
                    onLeaveEmployees = onLeaveCount,
                    employeesOnLeave = employeesOnLeaveToday
                )
            )

            currentDate = currentDate.plusDays(1)
        }

        return results
    }

    /**
     * Get leaves for a specific employee in a date range
     */
    @Transactional(readOnly = true)
    fun getEmployeeLeaveCalendar(
        employeeId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<LeaveCalendarEntryDto> {
        val leaveRequests = leaveRequestRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            employeeId,
            endDate,
            startDate
        )

        val leaveEntries = mutableListOf<LeaveCalendarEntryDto>()

        for (leaveRequest in leaveRequests) {
            var currentDate = if (leaveRequest.startDate.isBefore(startDate)) startDate else leaveRequest.startDate
            val lastDate = if (leaveRequest.endDate.isAfter(endDate)) endDate else leaveRequest.endDate

            while (!currentDate.isAfter(lastDate)) {
                val dayType = when {
                    currentDate == leaveRequest.startDate && currentDate == leaveRequest.endDate -> {
                        leaveRequest.startDayType
                    }
                    currentDate == leaveRequest.startDate -> leaveRequest.startDayType
                    currentDate == leaveRequest.endDate -> leaveRequest.endDayType
                    else -> LeaveDayType.FULL_DAY
                }

                leaveEntries.add(
                    LeaveCalendarEntryDto(
                        date = currentDate,
                        employeeId = leaveRequest.employeeId,
                        employeeName = leaveRequest.employeeName,
                        leaveRequestId = leaveRequest.id!!,
                        leaveTypeName = leaveRequest.leaveType.name,
                        leaveTypeCode = leaveRequest.leaveType.code,
                        dayType = dayType,
                        status = leaveRequest.status
                    )
                )

                currentDate = currentDate.plusDays(1)
            }
        }

        return leaveEntries.sortedBy { it.date }
    }

    /**
     * Find optimal dates for team events (days with max availability)
     */
    @Transactional(readOnly = true)
    fun findOptimalDatesForEvent(
        employeeIds: List<UUID>,
        startDate: LocalDate,
        endDate: LocalDate,
        requiredAttendees: Int,
        companyId: UUID?
    ): List<DayAvailabilityDto> {
        val dayAvailability = getDayAvailabilitySummary(employeeIds, startDate, endDate, companyId)

        return dayAvailability
            .filter { it.isWorkingDay && it.availableEmployees >= requiredAttendees }
            .sortedByDescending { it.availableEmployees }
    }
}
