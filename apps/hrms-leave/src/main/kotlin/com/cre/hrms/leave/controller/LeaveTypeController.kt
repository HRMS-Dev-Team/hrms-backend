package com.cre.hrms.leave.controller

import com.cre.hrms.core.enums.LeaveCategory
import com.cre.hrms.dto.leave.CreateLeaveTypeRequest
import com.cre.hrms.dto.leave.LeaveTypeResponse
import com.cre.hrms.dto.leave.UpdateLeaveTypeRequest
import com.cre.hrms.leave.service.LeaveTypeService
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
@RequestMapping("/api/v1/leave-types")
class LeaveTypeController(
    private val leaveTypeService: LeaveTypeService
) {

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun createLeaveType(@Valid @RequestBody request: CreateLeaveTypeRequest): ResponseEntity<LeaveTypeResponse> {
        return try {
            val response = leaveTypeService.createLeaveType(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to create leave type: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    fun getLeaveTypeById(@PathVariable id: UUID): ResponseEntity<LeaveTypeResponse> {
        return try {
            val response = leaveTypeService.getLeaveTypeById(id)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get leave type: ${e.message}")
        }
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    fun getLeaveTypeByCode(@PathVariable code: String): ResponseEntity<LeaveTypeResponse> {
        return try {
            val response = leaveTypeService.getLeaveTypeByCode(code)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get leave type: ${e.message}")
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun getAllLeaveTypes(@PageableDefault(size = 20) pageable: Pageable): ResponseEntity<Page<LeaveTypeResponse>> {
        val response = leaveTypeService.getAllLeaveTypes(pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    fun getLeaveTypesByCompany(
        @PathVariable companyId: UUID,
        @RequestParam(defaultValue = "true") activeOnly: Boolean
    ): ResponseEntity<List<LeaveTypeResponse>> {
        val response = leaveTypeService.getLeaveTypesByCompany(companyId, activeOnly)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun getLeaveTypesByCategory(@PathVariable category: LeaveCategory): ResponseEntity<List<LeaveTypeResponse>> {
        val response = leaveTypeService.getLeaveTypesByCategory(category)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun updateLeaveType(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateLeaveTypeRequest
    ): ResponseEntity<LeaveTypeResponse> {
        return try {
            val response = leaveTypeService.updateLeaveType(id, request)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to update leave type: ${e.message}")
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteLeaveType(@PathVariable id: UUID): ResponseEntity<Void> {
        return try {
            leaveTypeService.deleteLeaveType(id)
            ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to delete leave type: ${e.message}")
        }
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Leave type endpoints are working")
    }
}
