package com.cre.hrms.employee.service

import com.cre.hrms.dto.department.CreateDepartmentRequest
import com.cre.hrms.dto.department.DepartmentResponse
import com.cre.hrms.dto.department.UpdateDepartmentRequest
import com.cre.hrms.employee.mapper.DepartmentMapper
import com.cre.hrms.persistence.department.repository.DepartmentRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository,
    private val departmentMapper: DepartmentMapper
) {
    private val logger = LoggerFactory.getLogger(DepartmentService::class.java)

    @Transactional
    fun createDepartment(request: CreateDepartmentRequest): DepartmentResponse {
        // Check if department code already exists
        if (departmentRepository.existsByCode(request.code)) {
            throw RuntimeException("Department code already exists: ${request.code}")
        }

        val department = departmentMapper.toEntity(request)
        val savedDepartment = departmentRepository.save(department)

        logger.info("Department created successfully: ${savedDepartment.id}")
        return departmentMapper.toResponse(savedDepartment)
    }

    @Transactional(readOnly = true)
    fun getDepartmentById(id: UUID): DepartmentResponse {
        val department = departmentRepository.findById(id)
            .orElseThrow { RuntimeException("Department not found with id: $id") }
        return departmentMapper.toResponse(department)
    }

    @Transactional(readOnly = true)
    fun getDepartmentByCode(code: String): DepartmentResponse {
        val department = departmentRepository.findByCode(code)
            ?: throw RuntimeException("Department not found with code: $code")
        return departmentMapper.toResponse(department)
    }

    @Transactional(readOnly = true)
    fun getAllDepartments(pageable: Pageable): Page<DepartmentResponse> {
        return departmentRepository.findAll(pageable)
            .map { departmentMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getDepartmentsByCompanyId(companyId: UUID): List<DepartmentResponse> {
        return departmentRepository.findByCompanyId(companyId)
            .map { departmentMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getActiveDepartments(): List<DepartmentResponse> {
        return departmentRepository.findByIsActive(true)
            .map { departmentMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getDepartmentsByCompanyIdAndActive(companyId: UUID, isActive: Boolean): List<DepartmentResponse> {
        return departmentRepository.findByCompanyIdAndIsActive(companyId, isActive)
            .map { departmentMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun searchDepartmentsByName(name: String): List<DepartmentResponse> {
        return departmentRepository.findByNameContainingIgnoreCase(name)
            .map { departmentMapper.toResponse(it) }
    }

    @Transactional
    fun updateDepartment(id: UUID, request: UpdateDepartmentRequest): DepartmentResponse {
        val department = departmentRepository.findById(id)
            .orElseThrow { RuntimeException("Department not found with id: $id") }

        // Check if code is being changed and if it already exists
        request.code?.let {
            if (it != department.code && departmentRepository.existsByCode(it)) {
                throw RuntimeException("Department code already exists: $it")
            }
        }

        departmentMapper.updateEntity(department, request)
        val updatedDepartment = departmentRepository.save(department)

        logger.info("Department updated successfully: ${updatedDepartment.id}")
        return departmentMapper.toResponse(updatedDepartment)
    }

    @Transactional
    fun deleteDepartment(id: UUID) {
        if (!departmentRepository.existsById(id)) {
            throw RuntimeException("Department not found with id: $id")
        }
        departmentRepository.deleteById(id)
        logger.info("Department deleted successfully: $id")
    }
}
