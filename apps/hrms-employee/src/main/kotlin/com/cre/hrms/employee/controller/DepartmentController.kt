package com.cre.hrms.employee.controller

import com.cre.hrms.dto.department.CreateDepartmentRequest
import com.cre.hrms.dto.department.DepartmentResponse
import com.cre.hrms.dto.department.UpdateDepartmentRequest
import com.cre.hrms.employee.service.DepartmentService
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
@RequestMapping("/api/v1/departments")
class DepartmentController(
    private val departmentService: DepartmentService
) {

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun createDepartment(
        @Valid @RequestBody request: CreateDepartmentRequest
    ): ResponseEntity<DepartmentResponse> {
        return try {
            val response = departmentService.createDepartment(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to create department: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun getDepartmentById(
        @PathVariable id: UUID
    ): ResponseEntity<DepartmentResponse> {
        return try {
            val response = departmentService.getDepartmentById(id)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get department: ${e.message}")
        }
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun getDepartmentByCode(
        @PathVariable code: String
    ): ResponseEntity<DepartmentResponse> {
        return try {
            val response = departmentService.getDepartmentByCode(code)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to get department: ${e.message}")
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun getAllDepartments(
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ): ResponseEntity<Page<DepartmentResponse>> {
        val response = departmentService.getAllDepartments(pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun getDepartmentsByCompanyId(
        @PathVariable companyId: UUID
    ): ResponseEntity<List<DepartmentResponse>> {
        val response = departmentService.getDepartmentsByCompanyId(companyId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    fun getActiveDepartments(): ResponseEntity<List<DepartmentResponse>> {
        val response = departmentService.getActiveDepartments()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/company/{companyId}/active")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun getDepartmentsByCompanyIdAndActive(
        @PathVariable companyId: UUID,
        @RequestParam(defaultValue = "true") isActive: Boolean
    ): ResponseEntity<List<DepartmentResponse>> {
        val response = departmentService.getDepartmentsByCompanyIdAndActive(companyId, isActive)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    fun searchDepartmentsByName(
        @RequestParam name: String
    ): ResponseEntity<List<DepartmentResponse>> {
        val response = departmentService.searchDepartmentsByName(name)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    fun updateDepartment(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateDepartmentRequest
    ): ResponseEntity<DepartmentResponse> {
        return try {
            val response = departmentService.updateDepartment(id, request)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to update department: ${e.message}")
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteDepartment(
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        return try {
            departmentService.deleteDepartment(id)
            ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            throw RuntimeException("Failed to delete department: ${e.message}")
        }
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Department endpoints are working")
    }
}
