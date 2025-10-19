package com.cre.hrms.persistence.salaryadvance.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "salary_advance_audit")
data class SalaryAdvanceAudit(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "salary_advance_id", nullable = false)
    var salaryAdvanceId: UUID,

    @Column(name = "action", nullable = false)
    var action: String,

    @Column(name = "actor")
    var actor: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    var details: Map<String, Any>? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    protected fun onCreate() {
        createdAt = LocalDateTime.now()
    }
}
