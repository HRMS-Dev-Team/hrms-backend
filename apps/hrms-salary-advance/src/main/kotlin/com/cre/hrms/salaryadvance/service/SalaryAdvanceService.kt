package com.cre.hrms.salaryadvance.service

import com.cre.hrms.core.enums.SalaryAdvanceStatus
import com.cre.hrms.dto.salaryadvance.ApproveAdvanceRequest
import com.cre.hrms.dto.salaryadvance.CreateSalaryAdvanceRequest
import com.cre.hrms.dto.salaryadvance.SalaryAdvanceResponse
import com.cre.hrms.persistence.salaryadvance.entity.SalaryAdvance
import com.cre.hrms.persistence.salaryadvance.repository.SalaryAdvanceRepository
import com.cre.hrms.salaryadvance.mapper.SalaryAdvanceMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID

@Service
class SalaryAdvanceService(
    private val salaryAdvanceRepository: SalaryAdvanceRepository,
    private val repaymentScheduleService: RepaymentScheduleService,
    private val auditService: AuditService,
    private val salaryAdvanceMapper: SalaryAdvanceMapper
) {
    private val logger = LoggerFactory.getLogger(SalaryAdvanceService::class.java)

    // Business rule constants (these could be configurable)
    companion object {
        const val MIN_ADVANCE_AMOUNT = 50.0
        const val MAX_ADVANCE_PERCENTAGE = 0.5 // 50% of monthly salary
        const val MIN_TENURE_MONTHS = 3
    }

    @Transactional
    fun createAdvanceRequest(
        employeeId: UUID,
        employeeName: String?,
        request: CreateSalaryAdvanceRequest
    ): SalaryAdvanceResponse {
        // Validate minimum amount
        if (request.requestedAmount < BigDecimal.valueOf(MIN_ADVANCE_AMOUNT)) {
            throw RuntimeException("Minimum advance amount is $$MIN_ADVANCE_AMOUNT")
        }

        // Check for active advances
        val activeAdvances = salaryAdvanceRepository.countActiveAdvances(
            employeeId,
            listOf(SalaryAdvanceStatus.REQUESTED, SalaryAdvanceStatus.APPROVED, SalaryAdvanceStatus.ACTIVE)
        )

        if (activeAdvances > 0) {
            throw RuntimeException("You already have an active salary advance request")
        }

        // Create salary advance
        val salaryAdvance = SalaryAdvance(
            employeeId = employeeId,
            requestedAmount = request.requestedAmount,
            installments = request.installments,
            currency = request.currency,
            status = SalaryAdvanceStatus.REQUESTED,
            reason = request.reason,
            createdBy = employeeName
        )

        val savedAdvance = salaryAdvanceRepository.save(salaryAdvance)
        logger.info("Salary advance request created for employee: $employeeId, id: ${savedAdvance.id}")

        // Create audit log
        auditService.logAction(
            savedAdvance.id!!,
            "REQUESTED",
            employeeName,
            mapOf(
                "employeeId" to employeeId.toString(),
                "requestedAmount" to request.requestedAmount.toString(),
                "installments" to request.installments,
                "currency" to request.currency
            )
        )

        return salaryAdvanceMapper.toResponse(savedAdvance)
    }

    @Transactional
    fun approveAdvance(
        advanceId: UUID,
        approverName: String?,
        request: ApproveAdvanceRequest
    ): SalaryAdvanceResponse {
        val salaryAdvance = salaryAdvanceRepository.findById(advanceId)
            .orElseThrow { RuntimeException("Salary advance not found: $advanceId") }

        if (salaryAdvance.status != SalaryAdvanceStatus.REQUESTED) {
            throw RuntimeException("Only requested advances can be approved")
        }

        // Validate approved amount doesn't exceed requested amount
        if (request.approvedAmount > salaryAdvance.requestedAmount) {
            throw RuntimeException("Approved amount cannot exceed requested amount")
        }

        // Calculate installment amount
        val installmentAmount = request.approvedAmount
            .divide(BigDecimal.valueOf(salaryAdvance.installments.toLong()), 2, RoundingMode.HALF_UP)

        salaryAdvance.approvedAmount = request.approvedAmount
        salaryAdvance.installmentAmount = installmentAmount
        salaryAdvance.status = SalaryAdvanceStatus.APPROVED
        salaryAdvance.approvedAt = LocalDateTime.now()
        salaryAdvance.approvedBy = approverName
        salaryAdvance.scheduledRepaymentStart = request.scheduledRepaymentStart
        salaryAdvance.updatedBy = approverName

        val updatedAdvance = salaryAdvanceRepository.save(salaryAdvance)
        logger.info("Salary advance approved: $advanceId by approver: $approverName")

        // Create repayment schedule
        repaymentScheduleService.createRepaymentSchedule(updatedAdvance)

        // Create audit log
        auditService.logAction(
            advanceId,
            "APPROVED",
            approverName,
            mapOf(
                "approvedAmount" to request.approvedAmount.toString(),
                "scheduledRepaymentStart" to request.scheduledRepaymentStart.toString(),
                "installmentAmount" to installmentAmount.toString()
            )
        )

        return salaryAdvanceMapper.toResponse(updatedAdvance)
    }

    @Transactional
    fun rejectAdvance(
        advanceId: UUID,
        approverName: String?,
        rejectionReason: String
    ): SalaryAdvanceResponse {
        val salaryAdvance = salaryAdvanceRepository.findById(advanceId)
            .orElseThrow { RuntimeException("Salary advance not found: $advanceId") }

        if (salaryAdvance.status != SalaryAdvanceStatus.REQUESTED) {
            throw RuntimeException("Only requested advances can be rejected")
        }

        salaryAdvance.status = SalaryAdvanceStatus.REJECTED
        salaryAdvance.rejectionReason = rejectionReason
        salaryAdvance.updatedBy = approverName

        val updatedAdvance = salaryAdvanceRepository.save(salaryAdvance)
        logger.info("Salary advance rejected: $advanceId by approver: $approverName")

        // Create audit log
        auditService.logAction(
            advanceId,
            "REJECTED",
            approverName,
            mapOf("rejectionReason" to rejectionReason)
        )

        return salaryAdvanceMapper.toResponse(updatedAdvance)
    }

    @Transactional
    fun cancelAdvance(
        advanceId: UUID,
        employeeId: UUID,
        employeeName: String?
    ): SalaryAdvanceResponse {
        val salaryAdvance = salaryAdvanceRepository.findById(advanceId)
            .orElseThrow { RuntimeException("Salary advance not found: $advanceId") }

        if (salaryAdvance.employeeId != employeeId) {
            throw RuntimeException("You can only cancel your own advance requests")
        }

        if (salaryAdvance.status != SalaryAdvanceStatus.REQUESTED) {
            throw RuntimeException("Only requested advances can be cancelled")
        }

        salaryAdvance.status = SalaryAdvanceStatus.CANCELLED
        salaryAdvance.updatedBy = employeeName

        val updatedAdvance = salaryAdvanceRepository.save(salaryAdvance)
        logger.info("Salary advance cancelled: $advanceId by employee: $employeeId")

        // Create audit log
        auditService.logAction(
            advanceId,
            "CANCELLED",
            employeeName,
            mapOf("employeeId" to employeeId.toString())
        )

        return salaryAdvanceMapper.toResponse(updatedAdvance)
    }

    @Transactional
    fun activateAdvance(advanceId: UUID): SalaryAdvanceResponse {
        val salaryAdvance = salaryAdvanceRepository.findById(advanceId)
            .orElseThrow { RuntimeException("Salary advance not found: $advanceId") }

        if (salaryAdvance.status != SalaryAdvanceStatus.APPROVED) {
            throw RuntimeException("Only approved advances can be activated")
        }

        salaryAdvance.status = SalaryAdvanceStatus.ACTIVE

        val updatedAdvance = salaryAdvanceRepository.save(salaryAdvance)
        logger.info("Salary advance activated: $advanceId")

        // Create audit log
        auditService.logAction(
            advanceId,
            "ACTIVATED",
            "SYSTEM",
            mapOf("status" to "ACTIVE")
        )

        return salaryAdvanceMapper.toResponse(updatedAdvance)
    }

    @Transactional(readOnly = true)
    fun getAdvanceById(advanceId: UUID): SalaryAdvanceResponse {
        val salaryAdvance = salaryAdvanceRepository.findById(advanceId)
            .orElseThrow { RuntimeException("Salary advance not found: $advanceId") }
        return salaryAdvanceMapper.toResponse(salaryAdvance)
    }

    @Transactional(readOnly = true)
    fun getEmployeeAdvances(employeeId: UUID, pageable: Pageable): Page<SalaryAdvanceResponse> {
        return salaryAdvanceRepository.findByEmployeeId(employeeId, pageable)
            .map { salaryAdvanceMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getEmployeeAdvancesByStatus(
        employeeId: UUID,
        status: SalaryAdvanceStatus,
        pageable: Pageable
    ): Page<SalaryAdvanceResponse> {
        return salaryAdvanceRepository.findByEmployeeIdAndStatus(employeeId, status, pageable)
            .map { salaryAdvanceMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getPendingAdvances(pageable: Pageable): Page<SalaryAdvanceResponse> {
        return salaryAdvanceRepository.findByStatus(SalaryAdvanceStatus.REQUESTED, pageable)
            .map { salaryAdvanceMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getAllAdvances(pageable: Pageable): Page<SalaryAdvanceResponse> {
        return salaryAdvanceRepository.findAll(pageable)
            .map { salaryAdvanceMapper.toResponse(it) }
    }
}
