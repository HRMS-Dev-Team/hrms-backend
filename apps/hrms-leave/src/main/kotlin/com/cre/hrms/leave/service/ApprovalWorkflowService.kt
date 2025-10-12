package com.cre.hrms.leave.service

import com.cre.hrms.core.enums.ApprovalLevel
import com.cre.hrms.core.enums.LeaveRequestStatus
import com.cre.hrms.dto.leave.ApprovalWorkflowResponse
import com.cre.hrms.persistence.leave.entity.ApprovalWorkflow
import com.cre.hrms.persistence.leave.entity.LeaveRequest
import com.cre.hrms.persistence.leave.repository.ApprovalWorkflowRepository
import com.cre.hrms.persistence.leave.repository.LeaveRequestRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ApprovalWorkflowService(
    private val approvalWorkflowRepository: ApprovalWorkflowRepository,
    private val leaveRequestRepository: LeaveRequestRepository
) {
    private val logger = LoggerFactory.getLogger(ApprovalWorkflowService::class.java)

    /**
     * Create approval workflow steps for a leave request
     * This creates a multi-level approval chain
     */
    @Transactional
    fun createApprovalWorkflow(
        leaveRequest: LeaveRequest,
        approverIds: List<Pair<ApprovalLevel, UUID>>
    ): List<ApprovalWorkflow> {
        val workflows = approverIds.mapIndexed { index, (level, approverId) ->
            ApprovalWorkflow(
                leaveRequest = leaveRequest,
                approvalLevel = level,
                approverId = approverId,
                status = LeaveRequestStatus.PENDING,
                sequenceOrder = index + 1,
                isRequired = true
            )
        }

        val savedWorkflows = approvalWorkflowRepository.saveAll(workflows)
        logger.info("Created ${workflows.size} approval workflow steps for leave request: ${leaveRequest.id}")

        return savedWorkflows.toList()
    }

    /**
     * Process approval for a specific workflow step
     * Automatically moves to next approval level if current level is complete
     */
    @Transactional
    fun processApproval(
        workflowId: UUID,
        approverId: UUID,
        approverName: String?,
        comments: String?
    ): ApprovalWorkflowResponse {
        val workflow = approvalWorkflowRepository.findById(workflowId)
            .orElseThrow { RuntimeException("Approval workflow not found: $workflowId") }

        // Verify the approver
        if (workflow.approverId != approverId) {
            throw RuntimeException("You are not authorized to approve this request")
        }

        // Check if already processed
        if (workflow.status != LeaveRequestStatus.PENDING) {
            throw RuntimeException("This approval has already been processed")
        }

        // Check if previous levels are approved
        val allWorkflows = approvalWorkflowRepository.findByLeaveRequestOrderBySequenceOrder(workflow.leaveRequest)
        val previousWorkflows = allWorkflows.filter { it.sequenceOrder < workflow.sequenceOrder }

        if (previousWorkflows.any { it.status != LeaveRequestStatus.APPROVED }) {
            throw RuntimeException("Previous approval levels must be completed first")
        }

        // Approve the current workflow step
        workflow.status = LeaveRequestStatus.APPROVED
        workflow.approverName = approverName
        workflow.comments = comments
        workflow.actionAt = LocalDateTime.now()
        val updatedWorkflow = approvalWorkflowRepository.save(workflow)

        logger.info("Approval workflow step approved: $workflowId by approver: $approverId")

        // Check if all required approvals are complete
        checkAndFinalizeRequest(workflow.leaveRequest, allWorkflows)

        return toResponse(updatedWorkflow)
    }

    /**
     * Process rejection for a specific workflow step
     * Rejects the entire leave request
     */
    @Transactional
    fun processRejection(
        workflowId: UUID,
        approverId: UUID,
        approverName: String?,
        rejectionReason: String
    ): ApprovalWorkflowResponse {
        val workflow = approvalWorkflowRepository.findById(workflowId)
            .orElseThrow { RuntimeException("Approval workflow not found: $workflowId") }

        // Verify the approver
        if (workflow.approverId != approverId) {
            throw RuntimeException("You are not authorized to reject this request")
        }

        // Check if already processed
        if (workflow.status != LeaveRequestStatus.PENDING) {
            throw RuntimeException("This approval has already been processed")
        }

        // Reject the current workflow step
        workflow.status = LeaveRequestStatus.REJECTED
        workflow.approverName = approverName
        workflow.comments = rejectionReason
        workflow.actionAt = LocalDateTime.now()
        val updatedWorkflow = approvalWorkflowRepository.save(workflow)

        // Reject all remaining pending workflow steps
        val allWorkflows = approvalWorkflowRepository.findByLeaveRequestOrderBySequenceOrder(workflow.leaveRequest)
        allWorkflows
            .filter { it.status == LeaveRequestStatus.PENDING && it.id != workflow.id }
            .forEach {
                it.status = LeaveRequestStatus.REJECTED
                it.comments = "Rejected due to rejection at level ${workflow.approvalLevel}"
                it.actionAt = LocalDateTime.now()
                approvalWorkflowRepository.save(it)
            }

        // Update the leave request status to REJECTED
        val leaveRequest = workflow.leaveRequest
        leaveRequest.status = LeaveRequestStatus.REJECTED
        leaveRequest.approverId = approverId
        leaveRequest.approverName = approverName
        leaveRequest.rejectionReason = rejectionReason
        leaveRequest.approvedAt = LocalDateTime.now()
        leaveRequestRepository.save(leaveRequest)

        logger.info("Approval workflow step rejected: $workflowId by approver: $approverId")

        return toResponse(updatedWorkflow)
    }

    /**
     * Get all workflow steps for a leave request
     */
    @Transactional(readOnly = true)
    fun getWorkflowsByLeaveRequest(leaveRequestId: UUID): List<ApprovalWorkflowResponse> {
        val leaveRequest = leaveRequestRepository.findById(leaveRequestId)
            .orElseThrow { RuntimeException("Leave request not found: $leaveRequestId") }

        return approvalWorkflowRepository.findByLeaveRequestOrderBySequenceOrder(leaveRequest)
            .map { toResponse(it) }
    }

    /**
     * Get pending workflows for a specific approver
     */
    @Transactional(readOnly = true)
    fun getPendingWorkflowsForApprover(approverId: UUID): List<ApprovalWorkflowResponse> {
        return approvalWorkflowRepository.findByApproverIdAndStatus(approverId, LeaveRequestStatus.PENDING)
            .filter { canApproverActNow(it) }
            .map { toResponse(it) }
    }

    /**
     * Delegate approval to another user
     */
    @Transactional
    fun delegateApproval(
        workflowId: UUID,
        currentApproverId: UUID,
        newApproverId: UUID,
        newApproverName: String?
    ): ApprovalWorkflowResponse {
        val workflow = approvalWorkflowRepository.findById(workflowId)
            .orElseThrow { RuntimeException("Approval workflow not found: $workflowId") }

        // Verify the current approver
        if (workflow.approverId != currentApproverId) {
            throw RuntimeException("You are not authorized to delegate this approval")
        }

        // Check if still pending
        if (workflow.status != LeaveRequestStatus.PENDING) {
            throw RuntimeException("Only pending approvals can be delegated")
        }

        // Update approver
        workflow.approverId = newApproverId
        workflow.approverName = newApproverName
        workflow.comments = "Delegated from approver: $currentApproverId"
        val updatedWorkflow = approvalWorkflowRepository.save(workflow)

        logger.info("Approval workflow delegated: $workflowId from $currentApproverId to $newApproverId")

        return toResponse(updatedWorkflow)
    }

    /**
     * Check if all approvals are complete and finalize the leave request
     */
    private fun checkAndFinalizeRequest(leaveRequest: LeaveRequest, workflows: List<ApprovalWorkflow>) {
        val allRequired = workflows.filter { it.isRequired }

        // Check if all required approvals are approved
        val allApproved = allRequired.all { it.status == LeaveRequestStatus.APPROVED }

        if (allApproved) {
            // Update the leave request status to APPROVED
            leaveRequest.status = LeaveRequestStatus.APPROVED
            leaveRequest.approvedAt = LocalDateTime.now()
            // Set the last approver as the main approver
            val lastApprover = workflows.maxByOrNull { it.sequenceOrder }
            leaveRequest.approverId = lastApprover?.approverId
            leaveRequest.approverName = lastApprover?.approverName

            leaveRequestRepository.save(leaveRequest)
            logger.info("Leave request approved after all workflow steps completed: ${leaveRequest.id}")
        }
    }

    /**
     * Check if the approver can act on this workflow step now
     * (i.e., all previous levels are approved)
     */
    private fun canApproverActNow(workflow: ApprovalWorkflow): Boolean {
        val allWorkflows = approvalWorkflowRepository.findByLeaveRequestOrderBySequenceOrder(workflow.leaveRequest)
        val previousWorkflows = allWorkflows.filter { it.sequenceOrder < workflow.sequenceOrder }

        return previousWorkflows.all { it.status == LeaveRequestStatus.APPROVED }
    }

    /**
     * Convert entity to response DTO
     */
    private fun toResponse(workflow: ApprovalWorkflow): ApprovalWorkflowResponse {
        return ApprovalWorkflowResponse(
            id = workflow.id!!,
            leaveRequestId = workflow.leaveRequest.id!!,
            approvalLevel = workflow.approvalLevel,
            approverId = workflow.approverId,
            approverName = workflow.approverName,
            status = workflow.status,
            comments = workflow.comments,
            actionAt = workflow.actionAt,
            sequenceOrder = workflow.sequenceOrder,
            isRequired = workflow.isRequired,
            createdAt = workflow.createdAt,
            updatedAt = workflow.updatedAt
        )
    }
}
