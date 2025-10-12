package com.cre.hrms.dto.leave

import jakarta.validation.constraints.NotBlank

data class ApprovalActionDto(
    val comments: String? = null
)

data class RejectionActionDto(
    @field:NotBlank(message = "Rejection reason is required")
    val rejectionReason: String
)
