package com.cre.hrms.dto.employee

import com.cre.hrms.core.enums.Gender
import com.cre.hrms.core.enums.MaritalStatus
import jakarta.validation.constraints.Email
import java.util.UUID

data class UpdateEmployeeRequest(
    val employeeNumber: String? = null,
    val documentType: String? = null,
    val documentNumber: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,

    @field:Email(message = "Email should be valid")
    val email: String? = null,

    val phoneNumber: String? = null,
    val fatherNames: String? = null,
    val motherNames: String? = null,
    val rssbNumber: String? = null,
    val companyId: UUID? = null,
    val companyName: String? = null,
    val departmentId: UUID? = null,
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
    val bankDetails: BankDetailsDto? = null
)
