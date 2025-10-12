package com.cre.hrms.persistence.leave.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "leave_balances",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["employee_id", "leave_type_id", "year"])
    ]
)
data class LeaveBalance(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "employee_id", nullable = false)
    var employeeId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    var leaveType: LeaveType,

    @Column(name = "year", nullable = false)
    var year: Int,

    @Column(name = "total_allocated", precision = 5, scale = 2, nullable = false)
    var totalAllocated: BigDecimal,

    @Column(name = "used", precision = 5, scale = 2, nullable = false)
    var used: BigDecimal = BigDecimal.ZERO,

    @Column(name = "pending", precision = 5, scale = 2, nullable = false)
    var pending: BigDecimal = BigDecimal.ZERO,

    @Column(name = "available", precision = 5, scale = 2, nullable = false)
    var available: BigDecimal,

    @Column(name = "carried_forward", precision = 5, scale = 2)
    var carriedForward: BigDecimal? = null,

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

    // Helper method to calculate available balance
    fun calculateAvailable() {
        available = totalAllocated.subtract(used).subtract(pending)
    }
}
