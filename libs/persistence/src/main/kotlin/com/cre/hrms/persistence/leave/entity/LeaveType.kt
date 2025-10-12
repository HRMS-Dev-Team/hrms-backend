package com.cre.hrms.persistence.leave.entity

import com.cre.hrms.core.enums.LeaveCategory
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "leave_types")
data class LeaveType(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "code", unique = true, nullable = false)
    var code: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    var category: LeaveCategory,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "company_id", nullable = false)
    var companyId: UUID,

    @Column(name = "default_days_per_year", precision = 5, scale = 2)
    var defaultDaysPerYear: BigDecimal = BigDecimal.ZERO,

    @Column(name = "max_consecutive_days")
    var maxConsecutiveDays: Int? = null,

    @Column(name = "requires_document")
    var requiresDocument: Boolean = false,

    @Column(name = "min_notice_days")
    var minNoticeDays: Int = 0,

    @Column(name = "is_paid")
    var isPaid: Boolean = true,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "accrual_frequency")
    var accrualFrequency: String = "YEARLY", // MONTHLY, YEARLY

    @Column(name = "allow_carry_forward")
    var allowCarryForward: Boolean = false,

    @Column(name = "max_carry_forward_days")
    var maxCarryForwardDays: Int? = null,

    @Column(name = "carry_forward_expiry_months")
    var carryForwardExpiryMonths: Int? = null,

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
