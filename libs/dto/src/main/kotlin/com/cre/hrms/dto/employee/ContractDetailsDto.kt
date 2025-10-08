package com.cre.hrms.dto.employee

import com.cre.hrms.core.enums.ContractJobType
import com.cre.hrms.core.enums.ContractPeriodType
import com.cre.hrms.core.enums.ContractStatus
import com.cre.hrms.core.enums.SalaryType
import java.math.BigDecimal
import java.util.UUID

data class ContractDetailsDto(
    val contractDocumentId: UUID? = null,
    val contractStartDate: String? = null,
    val contractEndDate: String? = null,
    val contractStatus: ContractStatus? = null,
    val isInProbationPeriod: Boolean? = null,
    val probationStartDate: String? = null,
    val probationEndDate: String? = null,
    val probationPeriodRemarks: String? = null,
    val salaryType: SalaryType? = null,
    val salaryPeriodCyclePeriod: Int? = null,
    val baseSalary: BigDecimal? = null,
    val salaryCurrency: UUID? = null,
    val levelId: UUID? = null,
    val roles: List<UUID>? = null,
    val contractJobType: ContractJobType? = null,
    val contractPeriodType: ContractPeriodType? = null,
    val supervisorId: UUID? = null,
    val isBadgingMember: Boolean? = null,
    val positionCode: String? = null,
    val positionId: UUID? = null,
    val levelCode: String? = null
)
