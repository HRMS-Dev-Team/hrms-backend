package com.cre.hrms.leave.mapper

import com.cre.hrms.dto.leave.LeaveRequestResponse
import com.cre.hrms.persistence.leave.entity.LeaveRequest
import org.springframework.stereotype.Component

@Component
class LeaveRequestMapper {

    fun toResponse(leaveRequest: LeaveRequest): LeaveRequestResponse {
        return LeaveRequestResponse(
            id = leaveRequest.id!!,
            employeeId = leaveRequest.employeeId,
            employeeName = leaveRequest.employeeName,
            leaveTypeId = leaveRequest.leaveType.id!!,
            leaveTypeName = leaveRequest.leaveType.name,
            leaveTypeCode = leaveRequest.leaveType.code,
            startDate = leaveRequest.startDate,
            endDate = leaveRequest.endDate,
            startDayType = leaveRequest.startDayType,
            endDayType = leaveRequest.endDayType,
            totalDays = leaveRequest.totalDays,
            status = leaveRequest.status,
            reason = leaveRequest.reason,
            documentUrl = leaveRequest.documentUrl,
            approverId = leaveRequest.approverId,
            approverName = leaveRequest.approverName,
            approvedAt = leaveRequest.approvedAt,
            rejectionReason = leaveRequest.rejectionReason,
            modificationNote = leaveRequest.modificationNote,
            cancelledAt = leaveRequest.cancelledAt,
            cancellationReason = leaveRequest.cancellationReason,
            createdAt = leaveRequest.createdAt,
            updatedAt = leaveRequest.updatedAt
        )
    }
}
