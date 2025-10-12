package com.cre.hrms.leave.mapper

import com.cre.hrms.dto.leave.LeaveBalanceResponse
import com.cre.hrms.persistence.leave.entity.LeaveBalance
import org.springframework.stereotype.Component

@Component
class LeaveBalanceMapper {

    fun toResponse(leaveBalance: LeaveBalance): LeaveBalanceResponse {
        return LeaveBalanceResponse(
            id = leaveBalance.id!!,
            employeeId = leaveBalance.employeeId,
            leaveTypeId = leaveBalance.leaveType.id!!,
            leaveTypeName = leaveBalance.leaveType.name,
            leaveTypeCode = leaveBalance.leaveType.code,
            year = leaveBalance.year,
            totalAllocated = leaveBalance.totalAllocated,
            used = leaveBalance.used,
            pending = leaveBalance.pending,
            available = leaveBalance.available,
            carriedForward = leaveBalance.carriedForward,
            createdAt = leaveBalance.createdAt,
            updatedAt = leaveBalance.updatedAt
        )
    }
}
