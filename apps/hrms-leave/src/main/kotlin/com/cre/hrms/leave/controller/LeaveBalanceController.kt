package com.cre.hrms.leave.controller

import com.cre.hrms.dto.leave.AllocateLeaveBalanceRequest
import com.cre.hrms.dto.leave.LeaveBalanceResponse
import com.cre.hrms.leave.service.LeaveBalanceService
import com.cre.hrms.security.authorization.SecurityUtils
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/leave-balances")
class LeaveBalanceController(
    private val leaveBalanceService: LeaveBalanceService
) {

    @PostMapping("/allocate")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun allocateLeaveBalance(@Valid @RequestBody request: AllocateLeaveBalanceRequest): ResponseEntity<LeaveBalanceResponse> {
        return try {
            val response = leaveBalanceService.allocateLeaveBalance(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to allocate leave balance: ${e.message}")
        }
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun getEmployeeLeaveBalances(@PathVariable employeeId: UUID): ResponseEntity<List<LeaveBalanceResponse>> {
        val response = leaveBalanceService.getEmployeeLeaveBalances(employeeId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/employee/{employeeId}/year/{year}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun getEmployeeLeaveBalanceByYear(
        @PathVariable employeeId: UUID,
        @PathVariable year: Int
    ): ResponseEntity<List<LeaveBalanceResponse>> {
        val response = leaveBalanceService.getEmployeeLeaveBalanceByYear(employeeId, year)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/employee/{employeeId}/leave-type/{leaveTypeId}/year/{year}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    fun getEmployeeLeaveBalanceForType(
        @PathVariable employeeId: UUID,
        @PathVariable leaveTypeId: UUID,
        @PathVariable year: Int
    ): ResponseEntity<LeaveBalanceResponse> {
        return try {
            val response = leaveBalanceService.getEmployeeLeaveBalanceForType(employeeId, leaveTypeId, year)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get leave balance: ${e.message}")
        }
    }

    @GetMapping("/my-balances")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    fun getMyLeaveBalances(): ResponseEntity<List<LeaveBalanceResponse>> {
        val employeeId = SecurityUtils.getCurrentEmployeeIdOrThrow()
        val response = leaveBalanceService.getEmployeeLeaveBalances(employeeId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Leave balance endpoints are working")
    }
}
