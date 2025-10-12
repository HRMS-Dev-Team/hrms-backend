package com.cre.hrms.dto.leave

import com.cre.hrms.core.enums.ApprovalLevel
import com.cre.hrms.core.enums.LeaveRequestStatus
import java.time.LocalDateTime
import java.util.UUID

data class ApprovalWorkflowResponse(
    val id: UUID,
    val leaveRequestId: UUID,
    val approvalLevel: ApprovalLevel,
    val approverId: UUID,
    val approverName: String?,
    val status: LeaveRequestStatus,
    val comments: String?,
    val actionAt: LocalDateTime?,
    val sequenceOrder: Int,
    val isRequired: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
