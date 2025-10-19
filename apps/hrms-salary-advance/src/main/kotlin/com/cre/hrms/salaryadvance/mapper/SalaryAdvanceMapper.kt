package com.cre.hrms.salaryadvance.mapper

import com.cre.hrms.dto.salaryadvance.SalaryAdvanceResponse
import com.cre.hrms.persistence.salaryadvance.entity.SalaryAdvance
import org.springframework.stereotype.Component

@Component
class SalaryAdvanceMapper {

    fun toResponse(salaryAdvance: SalaryAdvance): SalaryAdvanceResponse {
        return SalaryAdvanceResponse(
            id = salaryAdvance.id!!,
            employeeId = salaryAdvance.employeeId,
            requestedAmount = salaryAdvance.requestedAmount,
            approvedAmount = salaryAdvance.approvedAmount,
            installments = salaryAdvance.installments,
            installmentAmount = salaryAdvance.installmentAmount,
            currency = salaryAdvance.currency,
            status = salaryAdvance.status,
            reason = salaryAdvance.reason,
            requestedAt = salaryAdvance.requestedAt,
            approvedAt = salaryAdvance.approvedAt,
            approvedBy = salaryAdvance.approvedBy,
            scheduledRepaymentStart = salaryAdvance.scheduledRepaymentStart,
            paidOffAt = salaryAdvance.paidOffAt,
            rejectionReason = salaryAdvance.rejectionReason,
            createdBy = salaryAdvance.createdBy,
            updatedBy = salaryAdvance.updatedBy,
            createdAt = salaryAdvance.createdAt,
            updatedAt = salaryAdvance.updatedAt
        )
    }
}
