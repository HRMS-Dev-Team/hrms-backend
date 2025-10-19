package com.cre.hrms.dto.salaryadvance

import jakarta.validation.constraints.NotBlank

data class RejectAdvanceRequest(
    @field:NotBlank(message = "Rejection reason is required")
    val rejectionReason: String
)
