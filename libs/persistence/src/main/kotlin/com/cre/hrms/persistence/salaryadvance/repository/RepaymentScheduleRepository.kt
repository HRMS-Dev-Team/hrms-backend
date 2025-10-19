package com.cre.hrms.persistence.salaryadvance.repository

import com.cre.hrms.core.enums.RepaymentStatus
import com.cre.hrms.persistence.salaryadvance.entity.RepaymentSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface RepaymentScheduleRepository : JpaRepository<RepaymentSchedule, UUID> {

    fun findBySalaryAdvanceId(salaryAdvanceId: UUID): List<RepaymentSchedule>

    fun findBySalaryAdvanceIdOrderByInstallmentNumberAsc(salaryAdvanceId: UUID): List<RepaymentSchedule>

    fun findByStatus(status: RepaymentStatus): List<RepaymentSchedule>

    @Query("""
        SELECT rs FROM RepaymentSchedule rs
        WHERE rs.status = :status
        AND rs.dueDate <= :dueDate
    """)
    fun findOverduePayments(
        @Param("status") status: RepaymentStatus,
        @Param("dueDate") dueDate: LocalDate
    ): List<RepaymentSchedule>

    @Query("""
        SELECT rs FROM RepaymentSchedule rs
        WHERE rs.salaryAdvance.id = :salaryAdvanceId
        AND rs.status IN :statuses
    """)
    fun findBySalaryAdvanceIdAndStatusIn(
        @Param("salaryAdvanceId") salaryAdvanceId: UUID,
        @Param("statuses") statuses: List<RepaymentStatus>
    ): List<RepaymentSchedule>

    @Query("""
        SELECT COALESCE(SUM(rs.dueAmount - COALESCE(rs.paidAmount, 0)), 0)
        FROM RepaymentSchedule rs
        WHERE rs.salaryAdvance.id = :salaryAdvanceId
        AND rs.status != 'PAID'
    """)
    fun calculateOutstandingBalance(@Param("salaryAdvanceId") salaryAdvanceId: UUID): java.math.BigDecimal
}
