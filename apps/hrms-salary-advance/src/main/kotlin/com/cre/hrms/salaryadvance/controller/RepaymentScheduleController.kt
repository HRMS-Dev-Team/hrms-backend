package com.cre.hrms.salaryadvance.controller

import com.cre.hrms.dto.salaryadvance.RecordPaymentRequest
import com.cre.hrms.dto.salaryadvance.RepaymentScheduleResponse
import com.cre.hrms.salaryadvance.service.RepaymentScheduleService
import com.cre.hrms.security.authorization.SecurityUtils
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/v1/repayment-schedules")
class RepaymentScheduleController(
    private val repaymentScheduleService: RepaymentScheduleService
) {

    @GetMapping("/salary-advance/{salaryAdvanceId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getRepaymentSchedule(
        @PathVariable salaryAdvanceId: UUID
    ): ResponseEntity<List<RepaymentScheduleResponse>> {
        val response = repaymentScheduleService.getRepaymentSchedule(salaryAdvanceId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getScheduleById(@PathVariable id: UUID): ResponseEntity<RepaymentScheduleResponse> {
        return try {
            val response = repaymentScheduleService.getScheduleById(id)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get repayment schedule: ${e.message}")
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getPendingRepayments(): ResponseEntity<List<RepaymentScheduleResponse>> {
        val response = repaymentScheduleService.getPendingRepayments()
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}/record-payment")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun recordPayment(
        @PathVariable id: UUID,
        @Valid @RequestBody request: RecordPaymentRequest
    ): ResponseEntity<RepaymentScheduleResponse> {
        return try {
            val recordedBy = SecurityUtils.getCurrentEmployeeName().orElse(null)
            val response = repaymentScheduleService.recordPayment(id, request, recordedBy)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to record payment: ${e.message}")
        }
    }

    @GetMapping("/salary-advance/{salaryAdvanceId}/outstanding-balance")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getOutstandingBalance(@PathVariable salaryAdvanceId: UUID): ResponseEntity<Map<String, BigDecimal>> {
        val balance = repaymentScheduleService.getOutstandingBalance(salaryAdvanceId)
        return ResponseEntity.ok(mapOf("outstandingBalance" to balance))
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Repayment schedule endpoints are working")
    }
}
