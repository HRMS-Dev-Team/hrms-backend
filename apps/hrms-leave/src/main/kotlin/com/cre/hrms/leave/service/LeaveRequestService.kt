package com.cre.hrms.leave.service

import com.cre.hrms.core.enums.LeaveDayType
import com.cre.hrms.core.enums.LeaveRequestStatus
import com.cre.hrms.dto.leave.CreateLeaveRequestDto
import com.cre.hrms.dto.leave.LeaveRequestResponse
import com.cre.hrms.leave.mapper.LeaveRequestMapper
import com.cre.hrms.persistence.leave.entity.LeaveRequest
import com.cre.hrms.persistence.leave.repository.LeaveRequestRepository
import com.cre.hrms.persistence.leave.repository.LeaveTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class LeaveRequestService(
    private val leaveRequestRepository: LeaveRequestRepository,
    private val leaveTypeRepository: LeaveTypeRepository,
    private val leaveBalanceService: LeaveBalanceService,
    private val leaveRequestMapper: LeaveRequestMapper,
    private val workingDaysCalculator: com.cre.hrms.leave.util.WorkingDaysCalculator
) {
    private val logger = LoggerFactory.getLogger(LeaveRequestService::class.java)

    @Transactional
    fun createLeaveRequest(employeeId: UUID, employeeName: String?, request: CreateLeaveRequestDto): LeaveRequestResponse {
        // Validate dates
        if (request.endDate.isBefore(request.startDate)) {
            throw RuntimeException("End date must be after start date")
        }

        val leaveType = leaveTypeRepository.findById(request.leaveTypeId)
            .orElseThrow { RuntimeException("Leave type not found: ${request.leaveTypeId}") }

        // Validate minimum notice period
        val daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), request.startDate)
        if (daysBetween < leaveType.minNoticeDays) {
            throw RuntimeException("Minimum notice period is ${leaveType.minNoticeDays} days")
        }

        // Calculate working days excluding weekends and holidays, with half-day support
        val totalDays = calculateLeaveDays(request.startDate, request.endDate, request.startDayType, request.endDayType, null)

        // Validate maximum consecutive days
        leaveType.maxConsecutiveDays?.let {
            if (totalDays > BigDecimal.valueOf(it.toLong())) {
                throw RuntimeException("Maximum consecutive days allowed is $it days")
            }
        }

        // Check for overlapping leaves
        val overlappingLeaves = leaveRequestRepository.findOverlappingLeaves(
            employeeId,
            request.startDate,
            request.endDate,
            listOf(LeaveRequestStatus.PENDING, LeaveRequestStatus.APPROVED)
        )

        if (overlappingLeaves.isNotEmpty()) {
            throw RuntimeException("Leave request overlaps with existing leave")
        }

        // Validate document requirement
        if (leaveType.requiresDocument && request.documentUrl.isNullOrBlank()) {
            throw RuntimeException("Document is required for ${leaveType.name}")
        }

        // Reserve leave balance
        try {
            leaveBalanceService.reserveLeaveBalance(employeeId, leaveType.id!!, totalDays)
        } catch (e: Exception) {
            throw RuntimeException("Failed to reserve leave balance: ${e.message}")
        }

        // Create leave request
        val leaveRequest = LeaveRequest(
            employeeId = employeeId,
            employeeName = employeeName,
            leaveType = leaveType,
            startDate = request.startDate,
            endDate = request.endDate,
            startDayType = request.startDayType,
            endDayType = request.endDayType,
            totalDays = totalDays,
            status = LeaveRequestStatus.PENDING,
            reason = request.reason,
            documentUrl = request.documentUrl
        )

        val savedRequest = leaveRequestRepository.save(leaveRequest)
        logger.info("Leave request created for employee: $employeeId, id: ${savedRequest.id}")

        return leaveRequestMapper.toResponse(savedRequest)
    }

    @Transactional
    fun approveLeaveRequest(requestId: UUID, approverId: UUID, approverName: String?): LeaveRequestResponse {
        val leaveRequest = leaveRequestRepository.findById(requestId)
            .orElseThrow { RuntimeException("Leave request not found: $requestId") }

        if (leaveRequest.status != LeaveRequestStatus.PENDING) {
            throw RuntimeException("Only pending leave requests can be approved")
        }

        // Confirm leave usage (move from pending to used)
        leaveBalanceService.confirmLeaveUsage(
            leaveRequest.employeeId,
            leaveRequest.leaveType.id!!,
            leaveRequest.totalDays
        )

        leaveRequest.status = LeaveRequestStatus.APPROVED
        leaveRequest.approverId = approverId
        leaveRequest.approverName = approverName
        leaveRequest.approvedAt = LocalDateTime.now()

        val updatedRequest = leaveRequestRepository.save(leaveRequest)
        logger.info("Leave request approved: $requestId by approver: $approverId")

        return leaveRequestMapper.toResponse(updatedRequest)
    }

    @Transactional
    fun rejectLeaveRequest(requestId: UUID, approverId: UUID, approverName: String?, rejectionReason: String): LeaveRequestResponse {
        val leaveRequest = leaveRequestRepository.findById(requestId)
            .orElseThrow { RuntimeException("Leave request not found: $requestId") }

        if (leaveRequest.status != LeaveRequestStatus.PENDING) {
            throw RuntimeException("Only pending leave requests can be rejected")
        }

        // Release reserved balance
        leaveBalanceService.releaseLeaveBalance(
            leaveRequest.employeeId,
            leaveRequest.leaveType.id!!,
            leaveRequest.totalDays
        )

        leaveRequest.status = LeaveRequestStatus.REJECTED
        leaveRequest.approverId = approverId
        leaveRequest.approverName = approverName
        leaveRequest.rejectionReason = rejectionReason
        leaveRequest.approvedAt = LocalDateTime.now()

        val updatedRequest = leaveRequestRepository.save(leaveRequest)
        logger.info("Leave request rejected: $requestId by approver: $approverId")

        return leaveRequestMapper.toResponse(updatedRequest)
    }

    @Transactional
    fun cancelLeaveRequest(requestId: UUID, employeeId: UUID, cancellationReason: String?): LeaveRequestResponse {
        val leaveRequest = leaveRequestRepository.findById(requestId)
            .orElseThrow { RuntimeException("Leave request not found: $requestId") }

        if (leaveRequest.employeeId != employeeId) {
            throw RuntimeException("You can only cancel your own leave requests")
        }

        if (leaveRequest.status !in listOf(LeaveRequestStatus.PENDING, LeaveRequestStatus.APPROVED)) {
            throw RuntimeException("Only pending or approved leave requests can be cancelled")
        }

        // Release or refund balance based on status
        when (leaveRequest.status) {
            LeaveRequestStatus.PENDING -> {
                leaveBalanceService.releaseLeaveBalance(
                    leaveRequest.employeeId,
                    leaveRequest.leaveType.id!!,
                    leaveRequest.totalDays
                )
            }
            LeaveRequestStatus.APPROVED -> {
                // Refund used leave back to available
                val currentYear = LocalDate.now().year
                leaveBalanceService.adjustLeaveBalance(
                    leaveRequest.employeeId,
                    leaveRequest.leaveType.id!!,
                    currentYear,
                    leaveRequest.totalDays
                )
            }
            else -> {}
        }

        leaveRequest.status = LeaveRequestStatus.CANCELLED
        leaveRequest.cancelledAt = LocalDateTime.now()
        leaveRequest.cancellationReason = cancellationReason

        val updatedRequest = leaveRequestRepository.save(leaveRequest)
        logger.info("Leave request cancelled: $requestId by employee: $employeeId")

        return leaveRequestMapper.toResponse(updatedRequest)
    }

    @Transactional(readOnly = true)
    fun getLeaveRequestById(requestId: UUID): LeaveRequestResponse {
        val leaveRequest = leaveRequestRepository.findById(requestId)
            .orElseThrow { RuntimeException("Leave request not found: $requestId") }
        return leaveRequestMapper.toResponse(leaveRequest)
    }

    @Transactional(readOnly = true)
    fun getEmployeeLeaveRequests(employeeId: UUID, pageable: Pageable): Page<LeaveRequestResponse> {
        return leaveRequestRepository.findByEmployeeId(employeeId, pageable)
            .map { leaveRequestMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getEmployeeLeaveRequestsByStatus(employeeId: UUID, status: LeaveRequestStatus, pageable: Pageable): Page<LeaveRequestResponse> {
        return leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, status, pageable)
            .map { leaveRequestMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getPendingLeaveRequests(pageable: Pageable): Page<LeaveRequestResponse> {
        return leaveRequestRepository.findByStatus(LeaveRequestStatus.PENDING, pageable)
            .map { leaveRequestMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getLeaveRequestsForApprover(approverId: UUID, pageable: Pageable): Page<LeaveRequestResponse> {
        return leaveRequestRepository.findByApproverId(approverId, pageable)
            .map { leaveRequestMapper.toResponse(it) }
    }

    private fun calculateLeaveDays(
        startDate: LocalDate,
        endDate: LocalDate,
        startDayType: LeaveDayType,
        endDayType: LeaveDayType,
        companyId: UUID?
    ): BigDecimal {
        // Use the WorkingDaysCalculator to exclude weekends and holidays
        return workingDaysCalculator.calculateWorkingDays(
            startDate,
            endDate,
            startDayType,
            endDayType,
            companyId
        )
    }
}
