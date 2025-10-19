package com.cre.hrms.persistence.salaryadvance.repository

import com.cre.hrms.persistence.salaryadvance.entity.SalaryAdvanceAudit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SalaryAdvanceAuditRepository : JpaRepository<SalaryAdvanceAudit, UUID> {

    fun findBySalaryAdvanceIdOrderByCreatedAtDesc(salaryAdvanceId: UUID): List<SalaryAdvanceAudit>

    fun findByActorOrderByCreatedAtDesc(actor: String): List<SalaryAdvanceAudit>
}
