package com.cre.hrms.persistence.employee.repository

import com.cre.hrms.persistence.employee.entity.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EmployeeRepository : JpaRepository<Employee, UUID> {

    fun findByEmployeeNumber(employeeNumber: String): Employee?

    fun existsByEmployeeNumber(employeeNumber: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findByCompanyId(companyId: UUID): List<Employee>

    fun findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(firstName: String, lastName: String): List<Employee>
}
