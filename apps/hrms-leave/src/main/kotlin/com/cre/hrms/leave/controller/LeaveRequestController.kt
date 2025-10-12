package com.cre.hrms.leave.controller

import com.cre.hrms.core.enums.LeaveRequestStatus
import com.cre.hrms.dto.leave.*
import com.cre.hrms.leave.service.LeaveRequestService
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
@RequestMapping("/api/v1/leave-requests")
class LeaveRequestController(
    private val leaveRequestService: LeaveRequestService
) {

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun createLeaveRequest(
        @Valid @RequestBody request: CreateLeaveRequestDto
    ): ResponseEntity<LeaveRequestResponse> {
        return try {
            val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
            val employeeName = SecurityUtils.getCurrentEmployeeName().orElse(null)

            val response = leaveRequestService.createLeaveRequest(employeeId, employeeName, request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to create leave request: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getLeaveRequestById(@PathVariable id: UUID): ResponseEntity<LeaveRequestResponse> {
        return try {
            val response = leaveRequestService.getLeaveRequestById(id)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get leave request: ${e.message}")
        }
    }

    @GetMapping("/my-leaves")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getMyLeaveRequests(
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ): ResponseEntity<Page<LeaveRequestResponse>> {
        val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
        val response = leaveRequestService.getEmployeeLeaveRequests(employeeId, pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/my-leaves/status/{status}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getMyLeaveRequestsByStatus(
        @PathVariable status: LeaveRequestStatus,
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ): ResponseEntity<Page<LeaveRequestResponse>> {
        val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
        val response = leaveRequestService.getEmployeeLeaveRequestsByStatus(employeeId, status, pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR', 'ADMIN')")
    fun getPendingLeaveRequests(
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ): ResponseEntity<Page<LeaveRequestResponse>> {
        val response = leaveRequestService.getPendingLeaveRequests(pageable)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR', 'ADMIN')")
    fun approveLeaveRequest(
        @PathVariable id: UUID,
        @RequestBody request: ApproveLeaveRequestDto
    ): ResponseEntity<LeaveRequestResponse> {
        return try {
            val approverId = SecurityUtils.getCurrentEmployeeIdOrThrow()
            val approverName = SecurityUtils.getCurrentEmployeeName().orElse(null)
            val response = leaveRequestService.approveLeaveRequest(id, approverId, approverName)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to approve leave request: ${e.message}")
        }
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR', 'ADMIN')")
    fun rejectLeaveRequest(
        @PathVariable id: UUID,
        @Valid @RequestBody request: RejectLeaveRequestDto
    ): ResponseEntity<LeaveRequestResponse> {
        return try {
            val approverId = SecurityUtils.getCurrentEmployeeIdOrThrow()
            val approverName = SecurityUtils.getCurrentEmployeeName().orElse(null)
            val response = leaveRequestService.rejectLeaveRequest(id, approverId, approverName, request.rejectionReason)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to reject leave request: ${e.message}")
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun cancelLeaveRequest(
        @PathVariable id: UUID,
        @RequestBody request: CancelLeaveRequestDto
    ): ResponseEntity<LeaveRequestResponse> {
        return try {
            val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
            val response = leaveRequestService.cancelLeaveRequest(id, employeeId, request.cancellationReason)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to cancel leave request: ${e.message}")
        }
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Leave request endpoints are working")
    }
}
