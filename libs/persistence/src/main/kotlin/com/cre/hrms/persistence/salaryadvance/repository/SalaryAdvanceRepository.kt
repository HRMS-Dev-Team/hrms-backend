package com.cre.hrms.persistence.salaryadvance.repository

import com.cre.hrms.core.enums.SalaryAdvanceStatus
import com.cre.hrms.persistence.salaryadvance.entity.SalaryAdvance
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SalaryAdvanceRepository : JpaRepository<SalaryAdvance, UUID> {

    fun findByEmployeeId(employeeId: UUID, pageable: Pageable): Page<SalaryAdvance>

    fun findByEmployeeIdAndStatus(employeeId: UUID, status: SalaryAdvanceStatus, pageable: Pageable): Page<SalaryAdvance>

    fun findByStatus(status: SalaryAdvanceStatus, pageable: Pageable): Page<SalaryAdvance>

    @Query("""
        SELECT sa FROM SalaryAdvance sa
        WHERE sa.employeeId = :employeeId
        AND sa.status IN :statuses
    """)
    fun findActiveAdvances(
        @Param("employeeId") employeeId: UUID,
        @Param("statuses") statuses: List<SalaryAdvanceStatus>
    ): List<SalaryAdvance>

    @Query("""
        SELECT COUNT(sa) FROM SalaryAdvance sa
        WHERE sa.employeeId = :employeeId
        AND sa.status IN :statuses
    """)
    fun countActiveAdvances(
        @Param("employeeId") employeeId: UUID,
        @Param("statuses") statuses: List<SalaryAdvanceStatus>
    ): Long

    @Query("""
        SELECT COALESCE(SUM(sa.approvedAmount), 0) FROM SalaryAdvance sa
        WHERE sa.employeeId = :employeeId
        AND sa.status IN :statuses
    """)
    fun sumOutstandingAmount(
        @Param("employeeId") employeeId: UUID,
        @Param("statuses") statuses: List<SalaryAdvanceStatus>
    ): java.math.BigDecimal
}
