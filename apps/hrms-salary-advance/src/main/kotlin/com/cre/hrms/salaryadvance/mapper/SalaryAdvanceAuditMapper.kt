package com.cre.hrms.salaryadvance.mapper

import com.cre.hrms.dto.salaryadvance.SalaryAdvanceAuditResponse
import com.cre.hrms.persistence.salaryadvance.entity.SalaryAdvanceAudit
import org.springframework.stereotype.Component

@Component
class SalaryAdvanceAuditMapper {

    fun toResponse(audit: SalaryAdvanceAudit): SalaryAdvanceAuditResponse {
        return SalaryAdvanceAuditResponse(
            id = audit.id!!,
            salaryAdvanceId = audit.salaryAdvanceId,
            action = audit.action,
            actor = audit.actor,
            details = audit.details,
            createdAt = audit.createdAt
        )
    }
}
