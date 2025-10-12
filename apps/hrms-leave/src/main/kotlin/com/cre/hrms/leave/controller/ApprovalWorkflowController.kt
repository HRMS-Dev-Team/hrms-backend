package com.cre.hrms.leave.controller

import com.cre.hrms.dto.leave.ApprovalActionDto
import com.cre.hrms.dto.leave.ApprovalWorkflowResponse
import com.cre.hrms.dto.leave.RejectionActionDto
import com.cre.hrms.leave.service.ApprovalWorkflowService
import com.cre.hrms.security.authorization.SecurityUtils
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/leave/approvals")
class ApprovalWorkflowController(
    private val approvalWorkflowService: ApprovalWorkflowService
) {

    @GetMapping("/leave-request/{leaveRequestId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    fun getWorkflowsByLeaveRequest(
        @PathVariable leaveRequestId: UUID
    ): ResponseEntity<List<ApprovalWorkflowResponse>> {
        val workflows = approvalWorkflowService.getWorkflowsByLeaveRequest(leaveRequestId)
        return ResponseEntity.ok(workflows)
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    fun getPendingApprovals(): ResponseEntity<List<ApprovalWorkflowResponse>> {
        val approverId = SecurityUtils.getCurrentEmployeeIdOrThrow()
        val workflows = approvalWorkflowService.getPendingWorkflowsForApprover(approverId)
        return ResponseEntity.ok(workflows)
    }

    @PostMapping("/{workflowId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    fun approveWorkflow(
        @PathVariable workflowId: UUID,
        @Valid @RequestBody action: ApprovalActionDto
    ): ResponseEntity<ApprovalWorkflowResponse> {
        val approverId = SecurityUtils.getCurrentEmployeeIdOrThrow()
        val approverName = SecurityUtils.getCurrentEmployeeName().orElse(null)

        val workflow = approvalWorkflowService.processApproval(
            workflowId,
            approverId,
            approverName,
            action.comments
        )

        return ResponseEntity.ok(workflow)
    }

    @PostMapping("/{workflowId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    fun rejectWorkflow(
        @PathVariable workflowId: UUID,
        @Valid @RequestBody action: RejectionActionDto
    ): ResponseEntity<ApprovalWorkflowResponse> {
        val approverId = SecurityUtils.getCurrentEmployeeIdOrThrow()
        val approverName = SecurityUtils.getCurrentEmployeeName().orElse(null)

        val workflow = approvalWorkflowService.processRejection(
            workflowId,
            approverId,
            approverName,
            action.rejectionReason
        )

        return ResponseEntity.ok(workflow)
    }

    @PostMapping("/{workflowId}/delegate")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    fun delegateApproval(
        @PathVariable workflowId: UUID,
        @RequestParam newApproverId: UUID,
        @RequestParam newApproverName: String?
    ): ResponseEntity<ApprovalWorkflowResponse> {
        val currentApproverId = SecurityUtils.getCurrentEmployeeIdOrThrow()

        val workflow = approvalWorkflowService.delegateApproval(
            workflowId,
            currentApproverId,
            newApproverId,
            newApproverName
        )

        return ResponseEntity.ok(workflow)
    }
}
