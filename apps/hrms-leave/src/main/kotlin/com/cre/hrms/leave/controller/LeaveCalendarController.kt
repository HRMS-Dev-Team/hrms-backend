package com.cre.hrms.leave.controller

import com.cre.hrms.dto.leave.*
import com.cre.hrms.leave.service.LeaveCalendarService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/leave/calendar")
class LeaveCalendarController(
    private val leaveCalendarService: LeaveCalendarService
) {

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    fun getTeamLeaveCalendar(
        @RequestParam employeeIds: List<UUID>,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<TeamLeaveCalendarDto> {
        val calendar = leaveCalendarService.getTeamLeaveCalendar(employeeIds, startDate, endDate)
        return ResponseEntity.ok(calendar)
    }

    @GetMapping("/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    fun checkEmployeeAvailability(
        @RequestParam employeeIds: List<UUID>,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<List<EmployeeAvailabilityDto>> {
        val availability = leaveCalendarService.checkEmployeeAvailability(employeeIds, date)
        return ResponseEntity.ok(availability)
    }

    @GetMapping("/day-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    fun getDayAvailabilitySummary(
        @RequestParam employeeIds: List<UUID>,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        @RequestParam(required = false) companyId: UUID?
    ): ResponseEntity<List<DayAvailabilityDto>> {
        val summary = leaveCalendarService.getDayAvailabilitySummary(
            employeeIds,
            startDate,
            endDate,
            companyId
        )
        return ResponseEntity.ok(summary)
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    fun getEmployeeLeaveCalendar(
        @PathVariable employeeId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<List<LeaveCalendarEntryDto>> {
        val calendar = leaveCalendarService.getEmployeeLeaveCalendar(employeeId, startDate, endDate)
        return ResponseEntity.ok(calendar)
    }

    @GetMapping("/optimal-dates")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    fun findOptimalDatesForEvent(
        @RequestParam employeeIds: List<UUID>,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        @RequestParam requiredAttendees: Int,
        @RequestParam(required = false) companyId: UUID?
    ): ResponseEntity<List<DayAvailabilityDto>> {
        val optimalDates = leaveCalendarService.findOptimalDatesForEvent(
            employeeIds,
            startDate,
            endDate,
            requiredAttendees,
            companyId
        )
        return ResponseEntity.ok(optimalDates)
    }
}
