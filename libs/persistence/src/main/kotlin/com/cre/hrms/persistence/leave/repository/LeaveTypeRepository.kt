package com.cre.hrms.persistence.leave.repository

import com.cre.hrms.core.enums.LeaveCategory
import com.cre.hrms.persistence.leave.entity.LeaveType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LeaveTypeRepository : JpaRepository<LeaveType, UUID> {
    fun findByCode(code: String): LeaveType?
    fun existsByCode(code: String): Boolean
    fun findByCompanyId(companyId: UUID): List<LeaveType>
    fun findByCompanyIdAndIsActive(companyId: UUID, isActive: Boolean): List<LeaveType>
    fun findByIsActive(isActive: Boolean): List<LeaveType>
    fun findByCategory(category: LeaveCategory): List<LeaveType>
    fun findByCompanyIdAndCategory(companyId: UUID, category: LeaveCategory): List<LeaveType>
}
