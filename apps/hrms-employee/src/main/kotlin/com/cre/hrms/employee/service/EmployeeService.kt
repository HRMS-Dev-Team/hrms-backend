package com.cre.hrms.employee.service

import com.cre.hrms.dto.employee.CreateEmployeeRequest
import com.cre.hrms.dto.employee.EmployeeResponse
import com.cre.hrms.dto.employee.UpdateEmployeeRequest
import com.cre.hrms.employee.mapper.EmployeeMapper
import com.cre.hrms.persistence.employee.repository.EmployeeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val employeeMapper: EmployeeMapper
) {

    @Transactional
    fun createEmployee(request: CreateEmployeeRequest): EmployeeResponse {
        // Check if employee number already exists
        if (employeeRepository.existsByEmployeeNumber(request.employeeNumber)) {
            throw RuntimeException("Employee number already exists: ${request.employeeNumber}")
        }

        // Check if email already exists
        request.email?.let {
            if (employeeRepository.existsByEmail(it)) {
                throw RuntimeException("Email already exists: $it")
            }
        }

        val employee = employeeMapper.toEntity(request)
        val savedEmployee = employeeRepository.save(employee)
        return employeeMapper.toResponse(savedEmployee)
    }

    @Transactional(readOnly = true)
    fun getEmployeeById(id: UUID): EmployeeResponse {
        val employee = employeeRepository.findById(id)
            .orElseThrow { RuntimeException("Employee not found with id: $id") }
        return employeeMapper.toResponse(employee)
    }

    @Transactional(readOnly = true)
    fun getEmployeeByEmployeeNumber(employeeNumber: String): EmployeeResponse {
        val employee = employeeRepository.findByEmployeeNumber(employeeNumber)
            ?: throw RuntimeException("Employee not found with employee number: $employeeNumber")
        return employeeMapper.toResponse(employee)
    }

    @Transactional(readOnly = true)
    fun getAllEmployees(pageable: Pageable): Page<EmployeeResponse> {
        return employeeRepository.findAll(pageable)
            .map { employeeMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getEmployeesByCompanyId(companyId: UUID): List<EmployeeResponse> {
        return employeeRepository.findByCompanyId(companyId)
            .map { employeeMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun searchEmployeesByName(name: String): List<EmployeeResponse> {
        return employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name)
            .map { employeeMapper.toResponse(it) }
    }

    @Transactional
    fun updateEmployee(id: UUID, request: UpdateEmployeeRequest): EmployeeResponse {
        val employee = employeeRepository.findById(id)
            .orElseThrow { RuntimeException("Employee not found with id: $id") }

        // Check if employee number is being changed and if it already exists
        request.employeeNumber?.let {
            if (it != employee.employeeNumber && employeeRepository.existsByEmployeeNumber(it)) {
                throw RuntimeException("Employee number already exists: $it")
            }
        }

        // Check if email is being changed and if it already exists
        request.email?.let {
            if (it != employee.email && employeeRepository.existsByEmail(it)) {
                throw RuntimeException("Email already exists: $it")
            }
        }

        employeeMapper.updateEntity(employee, request)
        val updatedEmployee = employeeRepository.save(employee)
        return employeeMapper.toResponse(updatedEmployee)
    }

    @Transactional
    fun deleteEmployee(id: UUID) {
        if (!employeeRepository.existsById(id)) {
            throw RuntimeException("Employee not found with id: $id")
        }
        employeeRepository.deleteById(id)
    }
}
