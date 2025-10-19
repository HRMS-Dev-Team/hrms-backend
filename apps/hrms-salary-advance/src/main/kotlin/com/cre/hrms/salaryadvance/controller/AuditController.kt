package com.cre.hrms.salaryadvance.controller

import com.cre.hrms.dto.salaryadvance.SalaryAdvanceAuditResponse
import com.cre.hrms.salaryadvance.service.AuditService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/salary-advance-audits")
class AuditController(
    private val auditService: AuditService
) {

    @GetMapping("/salary-advance/{salaryAdvanceId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getAuditLogs(@PathVariable salaryAdvanceId: UUID): ResponseEntity<List<SalaryAdvanceAuditResponse>> {
        val response = auditService.getAuditLogs(salaryAdvanceId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/actor/{actor}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getAuditLogsByActor(@PathVariable actor: String): ResponseEntity<List<SalaryAdvanceAuditResponse>> {
        val response = auditService.getAuditLogsByActor(actor)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Audit endpoints are working")
    }
}
