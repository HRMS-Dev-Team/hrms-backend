package com.cre.hrms.persistence.leave.repository

import com.cre.hrms.core.enums.HolidayType
import com.cre.hrms.persistence.leave.entity.Holiday
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface HolidayRepository : JpaRepository<Holiday, UUID> {
    fun findByDateAndIsActive(date: LocalDate, isActive: Boolean): List<Holiday>
    fun findByCompanyIdAndIsActive(companyId: UUID?, isActive: Boolean): List<Holiday>

    @Query("""
        SELECT h FROM Holiday h
        WHERE h.date BETWEEN :startDate AND :endDate
        AND h.isActive = true
        AND (h.companyId IS NULL OR h.companyId = :companyId)
    """)
    fun findHolidaysBetweenDates(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("companyId") companyId: UUID?
    ): List<Holiday>

    fun findByType(type: HolidayType): List<Holiday>
    fun findByCountryAndIsActive(country: String, isActive: Boolean): List<Holiday>
}
