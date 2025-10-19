package com.cre.hrms.salaryadvance.service

import com.cre.hrms.dto.salaryadvance.SalaryAdvanceAuditResponse
import com.cre.hrms.persistence.salaryadvance.entity.SalaryAdvanceAudit
import com.cre.hrms.persistence.salaryadvance.repository.SalaryAdvanceAuditRepository
import com.cre.hrms.salaryadvance.mapper.SalaryAdvanceAuditMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuditService(
    private val auditRepository: SalaryAdvanceAuditRepository,
    private val auditMapper: SalaryAdvanceAuditMapper
) {
    private val logger = LoggerFactory.getLogger(AuditService::class.java)

    @Transactional
    fun logAction(
        salaryAdvanceId: UUID,
        action: String,
        actor: String?,
        details: Map<String, Any>? = null
    ) {
        val audit = SalaryAdvanceAudit(
            salaryAdvanceId = salaryAdvanceId,
            action = action,
            actor = actor,
            details = details
        )

        auditRepository.save(audit)
        logger.debug("Audit log created for salary advance: $salaryAdvanceId, action: $action")
    }

    @Transactional(readOnly = true)
    fun getAuditLogs(salaryAdvanceId: UUID): List<SalaryAdvanceAuditResponse> {
        return auditRepository.findBySalaryAdvanceIdOrderByCreatedAtDesc(salaryAdvanceId)
            .map { auditMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getAuditLogsByActor(actor: String): List<SalaryAdvanceAuditResponse> {
        return auditRepository.findByActorOrderByCreatedAtDesc(actor)
            .map { auditMapper.toResponse(it) }
    }
}
