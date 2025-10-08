package com.cre.hrms.dto.employee

import com.cre.hrms.core.enums.Gender
import java.util.UUID

data class DependentDto(
    val dependentTypeId: UUID? = null,
    val dateOfBirth: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val gender: Gender? = null,
    val phoneNumber: String? = null,
    val documents: List<DependentDocumentDto>? = null
)
