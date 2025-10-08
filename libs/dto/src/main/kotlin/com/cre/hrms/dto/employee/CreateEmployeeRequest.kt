package com.cre.hrms.dto.employee

import com.cre.hrms.core.enums.Gender
import com.cre.hrms.core.enums.MaritalStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateEmployeeRequest(
    @field:NotBlank(message = "Employee number is required")
    val employeeNumber: String,

    val documentType: String? = null,
    val documentNumber: String? = null,

    @field:NotBlank(message = "First name is required")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    val lastName: String,

    @field:Email(message = "Email should be valid")
    val email: String? = null,

    val phoneNumber: String? = null,
    val fatherNames: String? = null,
    val motherNames: String? = null,
    val rssbNumber: String? = null,

    @field:NotNull(message = "Company ID is required")
    val companyId: UUID,

    val companyName: String? = null,
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
