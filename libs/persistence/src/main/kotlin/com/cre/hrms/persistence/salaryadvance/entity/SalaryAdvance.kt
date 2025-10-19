package com.cre.hrms.persistence.salaryadvance.entity

import com.cre.hrms.core.enums.SalaryAdvanceStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "salary_advance")
data class SalaryAdvance(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "employee_id", nullable = false)
    var employeeId: UUID,

    @Column(name = "requested_amount", precision = 10, scale = 2, nullable = false)
    var requestedAmount: BigDecimal,

    @Column(name = "approved_amount", precision = 10, scale = 2)
    var approvedAmount: BigDecimal? = null,

    @Column(name = "installments", nullable = false)
    var installments: Int = 3,

    @Column(name = "installment_amount", precision = 10, scale = 2)
    var installmentAmount: BigDecimal? = null,

    @Column(name = "currency", length = 3, nullable = false)
    var currency: String = "USD",

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    var status: SalaryAdvanceStatus = SalaryAdvanceStatus.REQUESTED,

    @Column(name = "reason", columnDefinition = "TEXT")
    var reason: String? = null,

    @Column(name = "requested_at", nullable = false)
    var requestedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "approved_at")
    var approvedAt: LocalDateTime? = null,

    @Column(name = "approved_by")
    var approvedBy: String? = null,

    @Column(name = "scheduled_repayment_start")
    var scheduledRepaymentStart: LocalDate? = null,

    @Column(name = "paid_off_at")
    var paidOffAt: LocalDateTime? = null,

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    var rejectionReason: String? = null,

    @Column(name = "created_by", length = 100)
    var createdBy: String? = null,

    @Column(name = "updated_by", length = 100)
    var updatedBy: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    protected fun onCreate() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
        requestedAt = LocalDateTime.now()
    }

    @PreUpdate
    protected fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
