package com.cre.hrms.persistence.employee.entity.embedded

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class ContactPerson(
    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "relationship")
    var relationship: String? = null
)
