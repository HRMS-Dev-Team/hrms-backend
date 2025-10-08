package com.cre.hrms.persistence.employee.entity.embedded

import com.cre.hrms.core.enums.ContractJobType
import com.cre.hrms.core.enums.ContractPeriodType
import com.cre.hrms.core.enums.ContractStatus
import com.cre.hrms.core.enums.SalaryType
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

@Embeddable
data class ContractDetails(
    @Column(name = "contract_document_id")
    var contractDocumentId: UUID? = null,

    @Column(name = "contract_start_date")
    var contractStartDate: String? = null,

    @Column(name = "contract_end_date")
    var contractEndDate: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status")
    var contractStatus: ContractStatus? = null,

    @Column(name = "is_in_probation_period")
    var isInProbationPeriod: Boolean? = null,

    @Column(name = "probation_start_date")
    var probationStartDate: String? = null,

    @Column(name = "probation_end_date")
    var probationEndDate: String? = null,

    @Column(name = "probation_period_remarks", columnDefinition = "TEXT")
    var probationPeriodRemarks: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_type")
    var salaryType: SalaryType? = null,

    @Column(name = "salary_period_cycle_period")
    var salaryPeriodCyclePeriod: Int? = null,

    @Column(name = "base_salary", precision = 19, scale = 2)
    var baseSalary: BigDecimal? = null,

    @Column(name = "salary_currency")
    var salaryCurrency: UUID? = null,

    @Column(name = "level_id")
    var levelId: UUID? = null,

    @ElementCollection
    @CollectionTable(name = "employee_roles", joinColumns = [JoinColumn(name = "employee_id")])
    @Column(name = "role_id")
    var roles: MutableList<UUID> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_job_type")
    var contractJobType: ContractJobType? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_period_type")
    var contractPeriodType: ContractPeriodType? = null,

    @Column(name = "supervisor_id")
    var supervisorId: UUID? = null,

    @Column(name = "is_badging_member")
    var isBadgingMember: Boolean? = null,

    @Column(name = "position_code")
    var positionCode: String? = null,

    @Column(name = "position_id")
    var positionId: UUID? = null,

    @Column(name = "level_code")
    var levelCode: String? = null
)
