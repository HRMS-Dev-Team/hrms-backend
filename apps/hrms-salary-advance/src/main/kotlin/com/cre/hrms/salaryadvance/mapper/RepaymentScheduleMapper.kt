package com.cre.hrms.salaryadvance.mapper

import com.cre.hrms.dto.salaryadvance.RepaymentScheduleResponse
import com.cre.hrms.persistence.salaryadvance.entity.RepaymentSchedule
import org.springframework.stereotype.Component

@Component
class RepaymentScheduleMapper {

    fun toResponse(repaymentSchedule: RepaymentSchedule): RepaymentScheduleResponse {
        return RepaymentScheduleResponse(
            id = repaymentSchedule.id!!,
            salaryAdvanceId = repaymentSchedule.salaryAdvance.id!!,
            installmentNumber = repaymentSchedule.installmentNumber,
            dueDate = repaymentSchedule.dueDate,
            dueAmount = repaymentSchedule.dueAmount,
            paidAmount = repaymentSchedule.paidAmount,
            status = repaymentSchedule.status,
            paidAt = repaymentSchedule.paidAt,
            paymentReference = repaymentSchedule.paymentReference,
            notes = repaymentSchedule.notes,
            createdAt = repaymentSchedule.createdAt,
            updatedAt = repaymentSchedule.updatedAt
        )
    }
}
