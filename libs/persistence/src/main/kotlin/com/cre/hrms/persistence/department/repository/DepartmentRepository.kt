package com.cre.hrms.persistence.department.repository

import com.cre.hrms.persistence.department.entity.Department
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DepartmentRepository : JpaRepository<Department, UUID> {
    fun findByCode(code: String): Department?
    fun existsByCode(code: String): Boolean
    fun findByCompanyId(companyId: UUID): List<Department>
    fun findByIsActive(isActive: Boolean): List<Department>
    fun findByCompanyIdAndIsActive(companyId: UUID, isActive: Boolean): List<Department>
    fun findByNameContainingIgnoreCase(name: String): List<Department>
}
