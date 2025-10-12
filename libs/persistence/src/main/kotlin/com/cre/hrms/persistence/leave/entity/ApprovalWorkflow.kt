package com.cre.hrms.persistence.leave.entity

import com.cre.hrms.core.enums.ApprovalLevel
import com.cre.hrms.core.enums.LeaveRequestStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "approval_workflows")
data class ApprovalWorkflow(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id", nullable = false)
    var leaveRequest: LeaveRequest,

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_level", nullable = false)
    var approvalLevel: ApprovalLevel,

    @Column(name = "approver_id", nullable = false)
    var approverId: UUID,

    @Column(name = "approver_name")
    var approverName: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: LeaveRequestStatus = LeaveRequestStatus.PENDING,

    @Column(name = "comments", columnDefinition = "TEXT")
    var comments: String? = null,

    @Column(name = "action_at")
    var actionAt: LocalDateTime? = null,

    @Column(name = "sequence_order", nullable = false)
    var sequenceOrder: Int,

    @Column(name = "is_required")
    var isRequired: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    protected fun onCreate() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    protected fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
