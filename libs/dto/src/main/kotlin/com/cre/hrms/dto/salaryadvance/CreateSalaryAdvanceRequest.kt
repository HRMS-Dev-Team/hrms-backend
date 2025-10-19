package com.cre.hrms.dto.salaryadvance

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreateSalaryAdvanceRequest(
    @field:NotNull(message = "Requested amount is required")
    @field:DecimalMin(value = "50.0", message = "Minimum advance amount is 50.0")
    val requestedAmount: BigDecimal,

    @field:NotNull(message = "Number of installments is required")
    @field:Min(value = 1, message = "Minimum installments is 1")
    @field:Max(value = 12, message = "Maximum installments is 12")
    val installments: Int = 3,

    val currency: String = "USD",

    val reason: String? = null
)
