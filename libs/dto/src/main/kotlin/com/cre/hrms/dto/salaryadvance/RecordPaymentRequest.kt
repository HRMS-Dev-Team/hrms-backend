package com.cre.hrms.dto.salaryadvance

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class RecordPaymentRequest(
    @field:NotNull(message = "Payment amount is required")
    @field:DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    val paidAmount: BigDecimal,

    val paymentReference: String? = null,

    val notes: String? = null
)
