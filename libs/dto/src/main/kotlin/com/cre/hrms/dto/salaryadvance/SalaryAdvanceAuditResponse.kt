package com.cre.hrms.dto.salaryadvance

import java.time.LocalDateTime
import java.util.UUID

data class SalaryAdvanceAuditResponse(
    val id: UUID,
    val salaryAdvanceId: UUID,
    val action: String,
    val actor: String?,
    val details: Map<String, Any>?,
    val createdAt: LocalDateTime
)
