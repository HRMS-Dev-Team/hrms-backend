package com.cre.hrms.dto.employee

import java.util.UUID

data class BankDetailsDto(
    val bankId: UUID? = null,
    val accountNumber: String? = null,
    val accountNames: String? = null,
    val referenceCode: String? = null
)
