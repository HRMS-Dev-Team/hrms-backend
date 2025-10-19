package com.cre.hrms.dto.salaryadvance

import com.cre.hrms.core.enums.RepaymentStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class RepaymentScheduleResponse(
    val id: UUID,
    val salaryAdvanceId: UUID,
    val installmentNumber: Int,
    val dueDate: LocalDate,
    val dueAmount: BigDecimal,
    val paidAmount: BigDecimal?,
    val status: RepaymentStatus,
    val paidAt: LocalDateTime?,
    val paymentReference: String?,
    val notes: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
