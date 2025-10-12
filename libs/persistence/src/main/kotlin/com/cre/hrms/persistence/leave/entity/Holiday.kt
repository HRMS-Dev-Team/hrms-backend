package com.cre.hrms.persistence.leave.entity

import com.cre.hrms.core.enums.HolidayType
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "holidays")
data class Holiday(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "date", nullable = false)
    var date: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: HolidayType,

    @Column(name = "company_id")
    var companyId: UUID? = null,  // Null for public holidays

    @Column(name = "country")
    var country: String? = null,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "is_recurring")
    var isRecurring: Boolean = false,

    @Column(name = "is_active")
    var isActive: Boolean = true,

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
