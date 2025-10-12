package com.cre.hrms.persistence.leave.repository

import com.cre.hrms.core.enums.LeaveRequestStatus
import com.cre.hrms.persistence.leave.entity.LeaveRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface LeaveRequestRepository : JpaRepository<LeaveRequest, UUID> {
    fun findByEmployeeId(employeeId: UUID, pageable: Pageable): Page<LeaveRequest>
    fun findByEmployeeIdAndStatus(employeeId: UUID, status: LeaveRequestStatus, pageable: Pageable): Page<LeaveRequest>
    fun findByStatus(status: LeaveRequestStatus, pageable: Pageable): Page<LeaveRequest>
    fun findByApproverId(approverId: UUID, pageable: Pageable): Page<LeaveRequest>

    @Query("""
        SELECT lr FROM LeaveRequest lr
        WHERE lr.employeeId = :employeeId
        AND lr.status IN :statuses
        AND ((lr.startDate BETWEEN :startDate AND :endDate)
        OR (lr.endDate BETWEEN :startDate AND :endDate)
        OR (:startDate BETWEEN lr.startDate AND lr.endDate))
    """)
    fun findOverlappingLeaves(
        @Param("employeeId") employeeId: UUID,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("statuses") statuses: List<LeaveRequestStatus>
    ): List<LeaveRequest>

    @Query("""
        SELECT lr FROM LeaveRequest lr
        WHERE lr.status = :status
        AND lr.startDate >= :startDate
        AND lr.endDate <= :endDate
    """)
    fun findByStatusAndDateRange(
        @Param("status") status: LeaveRequestStatus,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        pageable: Pageable
    ): Page<LeaveRequest>

    @Query("""
        SELECT lr FROM LeaveRequest lr
        WHERE lr.employeeId IN :employeeIds
        AND lr.startDate <= :endDate
        AND lr.endDate >= :startDate
    """)
    fun findByEmployeeIdInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        @Param("employeeIds") employeeIds: List<UUID>,
        @Param("endDate") endDate: LocalDate,
        @Param("startDate") startDate: LocalDate
    ): List<LeaveRequest>

    @Query("""
        SELECT lr FROM LeaveRequest lr
        WHERE lr.employeeId = :employeeId
        AND lr.startDate <= :endDate
        AND lr.endDate >= :startDate
    """)
    fun findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        @Param("employeeId") employeeId: UUID,
        @Param("endDate") endDate: LocalDate,
        @Param("startDate") startDate: LocalDate
    ): List<LeaveRequest>
}
