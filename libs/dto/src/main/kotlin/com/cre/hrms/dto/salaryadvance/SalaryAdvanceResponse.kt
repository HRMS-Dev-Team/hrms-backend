package com.cre.hrms.dto.salaryadvance

import com.cre.hrms.core.enums.SalaryAdvanceStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class SalaryAdvanceResponse(
    val id: UUID,
    val employeeId: UUID,
    val requestedAmount: BigDecimal,
    val approvedAmount: BigDecimal?,
    val installments: Int,
    val installmentAmount: BigDecimal?,
    val currency: String,
    val status: SalaryAdvanceStatus,
    val reason: String?,
    val requestedAt: LocalDateTime,
    val approvedAt: LocalDateTime?,
    val approvedBy: String?,
    val scheduledRepaymentStart: LocalDate?,
    val paidOffAt: LocalDateTime?,
    val rejectionReason: String?,
    val createdBy: String?,
    val updatedBy: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
