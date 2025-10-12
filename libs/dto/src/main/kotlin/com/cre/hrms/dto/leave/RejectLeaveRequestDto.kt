package com.cre.hrms.dto.leave

import jakarta.validation.constraints.NotBlank

data class RejectLeaveRequestDto(
    @field:NotBlank(message = "Rejection reason is required")
    val rejectionReason: String
)
