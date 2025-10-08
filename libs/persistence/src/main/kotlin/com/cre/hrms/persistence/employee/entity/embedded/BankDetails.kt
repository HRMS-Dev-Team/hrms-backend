package com.cre.hrms.persistence.employee.entity.embedded

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.util.UUID

@Embeddable
data class BankDetails(
    @Column(name = "bank_id")
    var bankId: UUID? = null,

    @Column(name = "account_number")
    var accountNumber: String? = null,

    @Column(name = "account_names")
    var accountNames: String? = null,

    @Column(name = "reference_code")
    var referenceCode: String? = null
)
