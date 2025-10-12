package com.cre.hrms.persistence.leave.repository

import com.cre.hrms.core.enums.LeaveRequestStatus
import com.cre.hrms.persistence.leave.entity.ApprovalWorkflow
import com.cre.hrms.persistence.leave.entity.LeaveRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ApprovalWorkflowRepository : JpaRepository<ApprovalWorkflow, UUID> {
    fun findByLeaveRequestOrderBySequenceOrder(leaveRequest: LeaveRequest): List<ApprovalWorkflow>
    fun findByLeaveRequestAndStatus(leaveRequest: LeaveRequest, status: LeaveRequestStatus): List<ApprovalWorkflow>
    fun findByApproverId(approverId: UUID): List<ApprovalWorkflow>
    fun findByApproverIdAndStatus(approverId: UUID, status: LeaveRequestStatus): List<ApprovalWorkflow>
}
