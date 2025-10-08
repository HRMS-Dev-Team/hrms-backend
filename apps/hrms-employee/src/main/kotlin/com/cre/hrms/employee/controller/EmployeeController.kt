package com.cre.hrms.employee.controller

import com.cre.hrms.dto.employee.CreateEmployeeRequest
import com.cre.hrms.dto.employee.EmployeeResponse
import com.cre.hrms.dto.employee.UpdateEmployeeRequest
import com.cre.hrms.employee.service.EmployeeService
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
@RequestMapping("/api/v1/employees")
class EmployeeController(
    private val employeeService: EmployeeService
) {

    @PostMapping
    @PreAuthorize("hasRole('HR', 'ADMIN')")
    fun createEmployee(
        @Valid @RequestBody request: CreateEmployeeRequest
    ): ResponseEntity<EmployeeResponse> {
        return try {
            val response = employeeService.createEmployee(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to create employee: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getEmployeeById(
        @PathVariable id: UUID
    ): ResponseEntity<EmployeeResponse> {
        return try {
            val response = employeeService.getEmployeeById(id)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get employee: ${e.message}")
        }
    }

    @GetMapping("/employee-number/{employeeNumber}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getEmployeeByEmployeeNumber(
        @PathVariable employeeNumber: String
    ): ResponseEntity<EmployeeResponse> {
        return try {
            val response = employeeService.getEmployeeByEmployeeNumber(employeeNumber)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get employee: ${e.message}")
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getAllEmployees(
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ): ResponseEntity<Page<EmployeeResponse>> {
        val response = employeeService.getAllEmployees(pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun getEmployeesByCompanyId(
        @PathVariable companyId: UUID
    ): ResponseEntity<List<EmployeeResponse>> {
        val response = employeeService.getEmployeesByCompanyId(companyId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun searchEmployeesByName(
        @RequestParam name: String
    ): ResponseEntity<List<EmployeeResponse>> {
        val response = employeeService.searchEmployeesByName(name)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun updateEmployee(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateEmployeeRequest
    ): ResponseEntity<EmployeeResponse> {
        return try {
            val response = employeeService.updateEmployee(id, request)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to update employee: ${e.message}")
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteEmployee(
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        return try {
            employeeService.deleteEmployee(id)
            ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to delete employee: ${e.message}")
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getMyProfile(): ResponseEntity<EmployeeResponse> {
        // TODO: Implement getting employee by current username
        // val username = SecurityUtils.getCurrentUsername().orElseThrow()
        // val response = employeeService.getEmployeeByUsername(username)
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Employee service is running")
    }
}
