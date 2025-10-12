package com.cre.hrms.leave.util

import com.cre.hrms.core.enums.LeaveDayType
import com.cre.hrms.persistence.leave.repository.HolidayRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

@Component
class WorkingDaysCalculator(
    private val holidayRepository: HolidayRepository
) {

    /**
     * Calculate working days between startDate and endDate, excluding weekends and holidays
     * Supports half-day leave calculations
     */
    fun calculateWorkingDays(
        startDate: LocalDate,
        endDate: LocalDate,
        startDayType: LeaveDayType,
        endDayType: LeaveDayType,
        companyId: UUID?
    ): BigDecimal {
        if (endDate.isBefore(startDate)) {
            throw IllegalArgumentException("End date must be on or after start date")
        }

        // Get holidays within the date range
        val holidays = holidayRepository.findHolidaysBetweenDates(startDate, endDate, companyId)
            .map { it.date }
            .toSet()

        // Handle single day leave
        if (startDate == endDate) {
            // Check if it's a weekend or holiday
            if (isWeekend(startDate) || holidays.contains(startDate)) {
                return BigDecimal.ZERO
            }

            return when {
                startDayType == LeaveDayType.FULL_DAY -> BigDecimal.ONE
                startDayType == LeaveDayType.FIRST_HALF && endDayType == LeaveDayType.SECOND_HALF -> BigDecimal.ONE
                else -> BigDecimal("0.5") // Either FIRST_HALF or SECOND_HALF only
            }
        }

        // Handle multiple days leave
        var workingDays = BigDecimal.ZERO
        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            // Skip weekends and holidays
            if (!isWeekend(currentDate) && !holidays.contains(currentDate)) {
                when {
                    // First day - check if it's a half day
                    currentDate == startDate -> {
                        workingDays = when (startDayType) {
                            LeaveDayType.FULL_DAY -> workingDays.add(BigDecimal.ONE)
                            else -> workingDays.add(BigDecimal("0.5")) // Half day
                        }
                    }
                    // Last day - check if it's a half day
                    currentDate == endDate -> {
                        workingDays = when (endDayType) {
                            LeaveDayType.FULL_DAY -> workingDays.add(BigDecimal.ONE)
                            else -> workingDays.add(BigDecimal("0.5")) // Half day
                        }
                    }
                    // Middle days are always full days
                    else -> workingDays = workingDays.add(BigDecimal.ONE)
                }
            }

            currentDate = currentDate.plusDays(1)
        }

        return workingDays
    }

    /**
     * Check if the given date is a weekend (Saturday or Sunday)
     */
    fun isWeekend(date: LocalDate): Boolean {
        val dayOfWeek = date.dayOfWeek
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
    }

    /**
     * Check if the given date is a holiday
     */
    fun isHoliday(date: LocalDate, companyId: UUID?): Boolean {
        val holidays = holidayRepository.findHolidaysBetweenDates(date, date, companyId)
        return holidays.isNotEmpty()
    }

    /**
     * Get the count of weekends between startDate and endDate (inclusive)
     */
    fun countWeekends(startDate: LocalDate, endDate: LocalDate): Int {
        var count = 0
        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            if (isWeekend(currentDate)) {
                count++
            }
            currentDate = currentDate.plusDays(1)
        }

        return count
    }

    /**
     * Get the count of holidays between startDate and endDate (inclusive)
     * excluding those that fall on weekends
     */
    fun countHolidays(startDate: LocalDate, endDate: LocalDate, companyId: UUID?): Int {
        val holidays = holidayRepository.findHolidaysBetweenDates(startDate, endDate, companyId)
        return holidays.count { !isWeekend(it.date) }
    }

    /**
     * Get the next working day after the given date
     */
    fun getNextWorkingDay(date: LocalDate, companyId: UUID?): LocalDate {
        var nextDay = date.plusDays(1)

        while (isWeekend(nextDay) || isHoliday(nextDay, companyId)) {
            nextDay = nextDay.plusDays(1)
        }

        return nextDay
    }

    /**
     * Get the previous working day before the given date
     */
    fun getPreviousWorkingDay(date: LocalDate, companyId: UUID?): LocalDate {
        var previousDay = date.minusDays(1)

        while (isWeekend(previousDay) || isHoliday(previousDay, companyId)) {
            previousDay = previousDay.minusDays(1)
        }

        return previousDay
    }
}
