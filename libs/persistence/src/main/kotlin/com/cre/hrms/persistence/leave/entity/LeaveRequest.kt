package com.cre.hrms.persistence.leave.entity

import com.cre.hrms.core.enums.LeaveDayType
import com.cre.hrms.core.enums.LeaveRequestStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "leave_requests")
data class LeaveRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "employee_id", nullable = false)
    var employeeId: UUID,

    @Column(name = "employee_name")
    var employeeName: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    var leaveType: LeaveType,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "start_day_type", nullable = false)
    var startDayType: LeaveDayType = LeaveDayType.FULL_DAY,

    @Enumerated(EnumType.STRING)
    @Column(name = "end_day_type", nullable = false)
    var endDayType: LeaveDayType = LeaveDayType.FULL_DAY,

    @Column(name = "total_days", precision = 5, scale = 2, nullable = false)
    var totalDays: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: LeaveRequestStatus = LeaveRequestStatus.PENDING,

    @Column(name = "reason", columnDefinition = "TEXT")
    var reason: String? = null,

    @Column(name = "document_url")
    var documentUrl: String? = null,

    @Column(name = "approver_id")
    var approverId: UUID? = null,

    @Column(name = "approver_name")
    var approverName: String? = null,

    @Column(name = "approved_at")
    var approvedAt: LocalDateTime? = null,

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    var rejectionReason: String? = null,

    @Column(name = "modification_note", columnDefinition = "TEXT")
    var modificationNote: String? = null,

    @Column(name = "cancelled_at")
    var cancelledAt: LocalDateTime? = null,

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    var cancellationReason: String? = null,

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
