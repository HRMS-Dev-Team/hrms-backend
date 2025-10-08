package com.cre.hrms.persistence.employee.entity.embedded

import com.cre.hrms.core.enums.Gender
import jakarta.persistence.*
import java.util.UUID

@Embeddable
data class Dependent(
    @Column(name = "dependent_type_id")
    var dependentTypeId: UUID? = null,

    @Column(name = "date_of_birth")
    var dateOfBirth: String? = null,

    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    var gender: Gender? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @ElementCollection
    @CollectionTable(name = "dependent_documents", joinColumns = [JoinColumn(name = "employee_id")])
    var documents: MutableList<DependentDocument> = mutableListOf()
)
