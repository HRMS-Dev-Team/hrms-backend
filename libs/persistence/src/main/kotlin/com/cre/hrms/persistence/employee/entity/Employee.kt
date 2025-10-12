package com.cre.hrms.persistence.employee.entity

import com.cre.hrms.persistence.employee.entity.embedded.BankDetails
import com.cre.hrms.persistence.employee.entity.embedded.ContactPerson
import com.cre.hrms.persistence.employee.entity.embedded.ContractDetails
import com.cre.hrms.persistence.employee.entity.embedded.Dependent
import com.cre.hrms.persistence.department.entity.Department
import com.cre.hrms.core.enums.Gender
import com.cre.hrms.core.enums.MaritalStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "employees")
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "employee_number", unique = true, nullable = false)
    var employeeNumber: String,

    @Column(name = "document_type")
    var documentType: String? = null,

    @Column(name = "document_number")
    var documentNumber: String? = null,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(name = "email", unique = true)
    var email: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "father_names")
    var fatherNames: String? = null,

    @Column(name = "mother_names")
    var motherNames: String? = null,

    @Column(name = "rssb_number")
    var rssbNumber: String? = null,

    @Column(name = "company_id", nullable = false)
    var companyId: UUID,

    @Column(name = "company_name")
    var companyName: String? = null,

    @Column(name = "nationality")
    var nationality: String? = null,

    @Column(name = "date_of_birth")
    var dateOfBirth: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    var gender: Gender? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    var maritalStatus: MaritalStatus? = null,

    @Column(name = "country")
    var country: String? = null,

    @Column(name = "address", columnDefinition = "TEXT")
    var address: String? = null,

    @ElementCollection
    @CollectionTable(name = "employee_dependents", joinColumns = [JoinColumn(name = "employee_id")])
    var dependents: MutableList<Dependent> = mutableListOf(),

    @Column(name = "profile_picture")
    var profilePicture: UUID? = null,

    @ElementCollection
    @CollectionTable(name = "employee_contact_persons", joinColumns = [JoinColumn(name = "employee_id")])
    var contactPersons: MutableList<ContactPerson> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    var department: Department? = null,

    @Embedded
    var contractDetails: ContractDetails? = null,

    @Embedded
    var bankDetails: BankDetails? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    protected fun onCreate() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    protected fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
