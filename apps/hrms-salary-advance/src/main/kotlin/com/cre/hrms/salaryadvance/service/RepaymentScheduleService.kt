package com.cre.hrms.salaryadvance.service

import com.cre.hrms.core.enums.RepaymentStatus
import com.cre.hrms.core.enums.SalaryAdvanceStatus
import com.cre.hrms.dto.salaryadvance.RecordPaymentRequest
import com.cre.hrms.dto.salaryadvance.RepaymentScheduleResponse
import com.cre.hrms.persistence.salaryadvance.entity.RepaymentSchedule
import com.cre.hrms.persistence.salaryadvance.entity.SalaryAdvance
import com.cre.hrms.persistence.salaryadvance.repository.RepaymentScheduleRepository
import com.cre.hrms.persistence.salaryadvance.repository.SalaryAdvanceRepository
import com.cre.hrms.salaryadvance.mapper.RepaymentScheduleMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Service
class RepaymentScheduleService(
    private val repaymentScheduleRepository: RepaymentScheduleRepository,
    private val salaryAdvanceRepository: SalaryAdvanceRepository,
    private val auditService: AuditService,
    private val repaymentScheduleMapper: RepaymentScheduleMapper
) {
    private val logger = LoggerFactory.getLogger(RepaymentScheduleService::class.java)

    @Transactional
    fun createRepaymentSchedule(salaryAdvance: SalaryAdvance) {
        if (salaryAdvance.approvedAmount == null || salaryAdvance.scheduledRepaymentStart == null) {
            throw RuntimeException("Approved amount and repayment start date must be set")
        }

        val installments = salaryAdvance.installments
        val installmentAmount = salaryAdvance.installmentAmount!!
        var startDate = salaryAdvance.scheduledRepaymentStart!!

        // Create repayment schedule entries
        for (i in 1..installments) {
            val repayment = RepaymentSchedule(
                salaryAdvance = salaryAdvance,
                installmentNumber = i,
                dueDate = startDate.plusMonths((i - 1).toLong()),
                dueAmount = if (i == installments) {
                    // Last installment: calculate remaining amount to handle rounding
                    salaryAdvance.approvedAmount!! - (installmentAmount * BigDecimal.valueOf((i - 1).toLong()))
                } else {
                    installmentAmount
                },
                status = RepaymentStatus.PENDING
            )

            repaymentScheduleRepository.save(repayment)
        }

        logger.info("Repayment schedule created for salary advance: ${salaryAdvance.id}, $installments installments")

        // Create audit log
        auditService.logAction(
            salaryAdvance.id!!,
            "REPAYMENT_SCHEDULE_CREATED",
            "SYSTEM",
            mapOf(
                "installments" to installments,
                "startDate" to startDate.toString()
            )
        )
    }

    @Transactional
    fun recordPayment(
        scheduleId: UUID,
        request: RecordPaymentRequest,
        recordedBy: String?
    ): RepaymentScheduleResponse {
        val schedule = repaymentScheduleRepository.findById(scheduleId)
            .orElseThrow { RuntimeException("Repayment schedule not found: $scheduleId") }

        if (schedule.status == RepaymentStatus.PAID) {
            throw RuntimeException("This installment is already paid")
        }

        val currentPaidAmount = schedule.paidAmount ?: BigDecimal.ZERO
        val totalPaid = currentPaidAmount + request.paidAmount

        schedule.paidAmount = totalPaid
        schedule.paymentReference = request.paymentReference
        schedule.notes = request.notes

        // Update status based on payment
        schedule.status = when {
            totalPaid >= schedule.dueAmount -> {
                schedule.paidAt = LocalDateTime.now()
                RepaymentStatus.PAID
            }
            totalPaid > BigDecimal.ZERO -> RepaymentStatus.PARTIAL
            else -> RepaymentStatus.PENDING
        }

        val updatedSchedule = repaymentScheduleRepository.save(schedule)
        logger.info("Payment recorded for schedule: $scheduleId, amount: ${request.paidAmount}")

        // Check if all installments are paid
        checkAndCompleteSalaryAdvance(schedule.salaryAdvance.id!!)

        // Create audit log
        auditService.logAction(
            schedule.salaryAdvance.id!!,
            "PAYMENT_RECORDED",
            recordedBy,
            mapOf(
                "scheduleId" to scheduleId.toString(),
                "paidAmount" to request.paidAmount.toString(),
                "installmentNumber" to schedule.installmentNumber,
                "status" to schedule.status.toString()
            )
        )

        return repaymentScheduleMapper.toResponse(updatedSchedule)
    }

    @Transactional
    fun checkAndCompleteSalaryAdvance(salaryAdvanceId: UUID) {
        val outstandingBalance = repaymentScheduleRepository.calculateOutstandingBalance(salaryAdvanceId)

        if (outstandingBalance <= BigDecimal.ZERO) {
            val salaryAdvance = salaryAdvanceRepository.findById(salaryAdvanceId)
                .orElseThrow { RuntimeException("Salary advance not found: $salaryAdvanceId") }

            if (salaryAdvance.status == SalaryAdvanceStatus.ACTIVE) {
                salaryAdvance.status = SalaryAdvanceStatus.PAID_OFF
                salaryAdvance.paidOffAt = LocalDateTime.now()

                salaryAdvanceRepository.save(salaryAdvance)
                logger.info("Salary advance paid off: $salaryAdvanceId")

                // Create audit log
                auditService.logAction(
                    salaryAdvanceId,
                    "PAID_OFF",
                    "SYSTEM",
                    mapOf("paidOffAt" to LocalDateTime.now().toString())
                )
            }
        }
    }

    @Transactional(readOnly = true)
    fun getRepaymentSchedule(salaryAdvanceId: UUID): List<RepaymentScheduleResponse> {
        return repaymentScheduleRepository.findBySalaryAdvanceIdOrderByInstallmentNumberAsc(salaryAdvanceId)
            .map { repaymentScheduleMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getScheduleById(scheduleId: UUID): RepaymentScheduleResponse {
        val schedule = repaymentScheduleRepository.findById(scheduleId)
            .orElseThrow { RuntimeException("Repayment schedule not found: $scheduleId") }
        return repaymentScheduleMapper.toResponse(schedule)
    }

    @Transactional(readOnly = true)
    fun getPendingRepayments(): List<RepaymentScheduleResponse> {
        return repaymentScheduleRepository.findByStatus(RepaymentStatus.PENDING)
            .map { repaymentScheduleMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getOutstandingBalance(salaryAdvanceId: UUID): BigDecimal {
        return repaymentScheduleRepository.calculateOutstandingBalance(salaryAdvanceId)
    }
}
