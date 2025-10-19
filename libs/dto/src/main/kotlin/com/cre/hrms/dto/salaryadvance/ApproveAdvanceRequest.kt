package com.cre.hrms.dto.salaryadvance

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class ApproveAdvanceRequest(
    @field:NotNull(message = "Approved amount is required")
    @field:DecimalMin(value = "50.0", message = "Minimum approved amount is 50.0")
    val approvedAmount: BigDecimal,

    @field:NotNull(message = "Scheduled repayment start date is required")
    val scheduledRepaymentStart: LocalDate
)
