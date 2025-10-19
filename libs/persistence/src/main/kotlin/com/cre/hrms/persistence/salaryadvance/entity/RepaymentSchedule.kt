package com.cre.hrms.persistence.salaryadvance.entity

import com.cre.hrms.core.enums.RepaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "repayment_schedule")
data class RepaymentSchedule(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_advance_id", nullable = false)
    var salaryAdvance: SalaryAdvance,

    @Column(name = "installment_number", nullable = false)
    var installmentNumber: Int,

    @Column(name = "due_date", nullable = false)
    var dueDate: LocalDate,

    @Column(name = "due_amount", precision = 10, scale = 2, nullable = false)
    var dueAmount: BigDecimal,

    @Column(name = "paid_amount", precision = 10, scale = 2)
    var paidAmount: BigDecimal? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    var status: RepaymentStatus = RepaymentStatus.PENDING,

    @Column(name = "paid_at")
    var paidAt: LocalDateTime? = null,

    @Column(name = "payment_reference")
    var paymentReference: String? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,

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
