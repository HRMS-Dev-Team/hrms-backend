package com.cre.hrms.salaryadvance.controller

import com.cre.hrms.core.enums.SalaryAdvanceStatus
import com.cre.hrms.dto.salaryadvance.*
import com.cre.hrms.salaryadvance.service.SalaryAdvanceService
import com.cre.hrms.security.authorization.SecurityUtils
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/salary-advances")
class SalaryAdvanceController(
    private val salaryAdvanceService: SalaryAdvanceService
) {

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun createAdvanceRequest(
        @Valid @RequestBody request: CreateSalaryAdvanceRequest
    ): ResponseEntity<SalaryAdvanceResponse> {
        return try {
            val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
            val employeeName = SecurityUtils.getCurrentEmployeeName().orElse(null)

            val response = salaryAdvanceService.createAdvanceRequest(employeeId, employeeName, request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to create salary advance request: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getAdvanceById(@PathVariable id: UUID): ResponseEntity<SalaryAdvanceResponse> {
        return try {
            val response = salaryAdvanceService.getAdvanceById(id)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get salary advance: ${e.message}")
        }
    }

    @GetMapping("/my-advances")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getMyAdvances(
        @PageableDefault(size = 20, sort = ["requestedAt"]) pageable: Pageable
    ): ResponseEntity<Page<SalaryAdvanceResponse>> {
        val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
        val response = salaryAdvanceService.getEmployeeAdvances(employeeId, pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/my-advances/status/{status}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getMyAdvancesByStatus(
        @PathVariable status: SalaryAdvanceStatus,
        @PageableDefault(size = 20, sort = ["requestedAt"]) pageable: Pageable
    ): ResponseEntity<Page<SalaryAdvanceResponse>> {
        val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
        val response = salaryAdvanceService.getEmployeeAdvancesByStatus(employeeId, status, pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getPendingAdvances(
        @PageableDefault(size = 20, sort = ["requestedAt"]) pageable: Pageable
    ): ResponseEntity<Page<SalaryAdvanceResponse>> {
        val response = salaryAdvanceService.getPendingAdvances(pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getAllAdvances(
        @PageableDefault(size = 20, sort = ["requestedAt"]) pageable: Pageable
    ): ResponseEntity<Page<SalaryAdvanceResponse>> {
        val response = salaryAdvanceService.getAllAdvances(pageable)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun approveAdvance(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ApproveAdvanceRequest
    ): ResponseEntity<SalaryAdvanceResponse> {
        return try {
            val approverName = SecurityUtils.getCurrentEmployeeName().orElse(null)
            val response = salaryAdvanceService.approveAdvance(id, approverName, request)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to approve salary advance: ${e.message}")
        }
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun rejectAdvance(
        @PathVariable id: UUID,
        @Valid @RequestBody request: RejectAdvanceRequest
    ): ResponseEntity<SalaryAdvanceResponse> {
        return try {
            val approverName = SecurityUtils.getCurrentEmployeeName().orElse(null)
            val response = salaryAdvanceService.rejectAdvance(id, approverName, request.rejectionReason)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to reject salary advance: ${e.message}")
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun cancelAdvance(@PathVariable id: UUID): ResponseEntity<SalaryAdvanceResponse> {
        return try {
            val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
            val employeeName = SecurityUtils.getCurrentEmployeeName().orElse(null)
            val response = salaryAdvanceService.cancelAdvance(id, employeeId, employeeName)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to cancel salary advance: ${e.message}")
        }
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun activateAdvance(@PathVariable id: UUID): ResponseEntity<SalaryAdvanceResponse> {
        return try {
            val response = salaryAdvanceService.activateAdvance(id)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to activate salary advance: ${e.message}")
        }
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Salary advance endpoints are working")
    }
}
