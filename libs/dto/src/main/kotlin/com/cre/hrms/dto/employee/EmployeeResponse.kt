package com.cre.hrms.dto.employee

import com.cre.hrms.core.enums.Gender
import com.cre.hrms.core.enums.MaritalStatus
import java.time.LocalDateTime
import java.util.UUID

data class EmployeeResponse(
    val id: UUID? = null,
    val employeeNumber: String,
    val documentType: String? = null,
    val documentNumber: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val fatherNames: String? = null,
    val motherNames: String? = null,
    val rssbNumber: String? = null,
    val companyId: UUID,
    val companyName: String? = null,
    val departmentId: UUID? = null,
    val departmentName: String? = null,
    val nationality: String? = null,
    val dateOfBirth: String? = null,
    val gender: Gender? = null,
    val maritalStatus: MaritalStatus? = null,
    val country: String? = null,
    val address: String? = null,
    val dependents: List<DependentDto>? = null,
    val profilePicture: UUID? = null,
    val contactPersons: List<ContactPersonDto>? = null,
    val contractDetails: ContractDetailsDto? = null,
    val bankDetails: BankDetailsDto? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
