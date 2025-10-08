package com.cre.hrms.dto.employee

import java.util.UUID

data class DependentDocumentDto(
    val documentType: UUID? = null,
    val documentNumber: String? = null
)
