package com.cre.hrms.employee.mapper

import com.cre.hrms.dto.department.CreateDepartmentRequest
import com.cre.hrms.dto.department.DepartmentResponse
import com.cre.hrms.dto.department.UpdateDepartmentRequest
import com.cre.hrms.persistence.department.entity.Department
import org.springframework.stereotype.Component

@Component
class DepartmentMapper {

    fun toEntity(request: CreateDepartmentRequest): Department {
        return Department(
            name = request.name,
            code = request.code,
            description = request.description,
            companyId = request.companyId,
            managerId = request.managerId,
            isActive = request.isActive
        )
    }

    fun updateEntity(department: Department, request: UpdateDepartmentRequest) {
        request.name?.let { department.name = it }
        request.code?.let { department.code = it }
        request.description?.let { department.description = it }
        request.managerId?.let { department.managerId = it }
        request.isActive?.let { department.isActive = it }
    }

    fun toResponse(department: Department): DepartmentResponse {
        return DepartmentResponse(
            id = department.id!!,
            name = department.name,
            code = department.code,
            description = department.description,
            companyId = department.companyId,
            managerId = department.managerId,
            isActive = department.isActive,
            createdAt = department.createdAt,
            updatedAt = department.updatedAt
        )
    }
}
