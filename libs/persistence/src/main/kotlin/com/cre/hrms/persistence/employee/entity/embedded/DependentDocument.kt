package com.cre.hrms.persistence.employee.entity.embedded

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.util.UUID

@Embeddable
data class DependentDocument(
    @Column(name = "document_type")
    var documentType: UUID? = null,

    @Column(name = "document_number")
    var documentNumber: String? = null
)
