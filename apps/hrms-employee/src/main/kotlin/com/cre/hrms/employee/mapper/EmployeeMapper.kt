package com.cre.hrms.employee.mapper

import com.cre.hrms.dto.employee.*
import com.cre.hrms.persistence.employee.entity.Employee
import com.cre.hrms.persistence.employee.entity.embedded.*
import com.cre.hrms.persistence.department.repository.DepartmentRepository
import org.springframework.stereotype.Component

@Component
class EmployeeMapper(
    private val departmentRepository: DepartmentRepository
) {

    fun toEntity(request: CreateEmployeeRequest): Employee {
        return Employee(
            employeeNumber = request.employeeNumber,
            documentType = request.documentType,
            documentNumber = request.documentNumber,
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            phoneNumber = request.phoneNumber,
            fatherNames = request.fatherNames,
            motherNames = request.motherNames,
            rssbNumber = request.rssbNumber,
            companyId = request.companyId,
            companyName = request.companyName,
            department = request.departmentId?.let { departmentRepository.findById(it).orElse(null) },
            nationality = request.nationality,
            dateOfBirth = request.dateOfBirth,
            gender = request.gender,
            maritalStatus = request.maritalStatus,
            country = request.country,
            address = request.address,
            dependents = request.dependents?.map { toDependentEntity(it) }?.toMutableList() ?: mutableListOf(),
            profilePicture = request.profilePicture,
            contactPersons = request.contactPersons?.map { toContactPersonEntity(it) }?.toMutableList() ?: mutableListOf(),
            contractDetails = request.contractDetails?.let { toContractDetailsEntity(it) },
            bankDetails = request.bankDetails?.let { toBankDetailsEntity(it) }
        )
    }

    fun updateEntity(employee: Employee, request: UpdateEmployeeRequest) {
        request.employeeNumber?.let { employee.employeeNumber = it }
        request.documentType?.let { employee.documentType = it }
        request.documentNumber?.let { employee.documentNumber = it }
        request.firstName?.let { employee.firstName = it }
        request.lastName?.let { employee.lastName = it }
        request.email?.let { employee.email = it }
        request.phoneNumber?.let { employee.phoneNumber = it }
        request.fatherNames?.let { employee.fatherNames = it }
        request.motherNames?.let { employee.motherNames = it }
        request.rssbNumber?.let { employee.rssbNumber = it }
        request.companyId?.let { employee.companyId = it }
        request.companyName?.let { employee.companyName = it }
        request.departmentId?.let { employee.department = departmentRepository.findById(it).orElse(null) }
        request.nationality?.let { employee.nationality = it }
        request.dateOfBirth?.let { employee.dateOfBirth = it }
        request.gender?.let { employee.gender = it }
        request.maritalStatus?.let { employee.maritalStatus = it }
        request.country?.let { employee.country = it }
        request.address?.let { employee.address = it }
        request.dependents?.let { employee.dependents = it.map { dto -> toDependentEntity(dto) }.toMutableList() }
        request.profilePicture?.let { employee.profilePicture = it }
        request.contactPersons?.let { employee.contactPersons = it.map { dto -> toContactPersonEntity(dto) }.toMutableList() }
        request.contractDetails?.let { employee.contractDetails = toContractDetailsEntity(it) }
        request.bankDetails?.let { employee.bankDetails = toBankDetailsEntity(it) }
    }

    fun toResponse(employee: Employee): EmployeeResponse {
        return EmployeeResponse(
            id = employee.id,
            employeeNumber = employee.employeeNumber,
            documentType = employee.documentType,
            documentNumber = employee.documentNumber,
            firstName = employee.firstName,
            lastName = employee.lastName,
            email = employee.email,
            phoneNumber = employee.phoneNumber,
            fatherNames = employee.fatherNames,
            motherNames = employee.motherNames,
            rssbNumber = employee.rssbNumber,
            companyId = employee.companyId,
            companyName = employee.companyName,
            departmentId = employee.department?.id,
            departmentName = employee.department?.name,
            nationality = employee.nationality,
            dateOfBirth = employee.dateOfBirth,
            gender = employee.gender,
            maritalStatus = employee.maritalStatus,
            country = employee.country,
            address = employee.address,
            dependents = employee.dependents.map { toDependentDto(it) },
            profilePicture = employee.profilePicture,
            contactPersons = employee.contactPersons.map { toContactPersonDto(it) },
            contractDetails = employee.contractDetails?.let { toContractDetailsDto(it) },
            bankDetails = employee.bankDetails?.let { toBankDetailsDto(it) },
            createdAt = employee.createdAt,
            updatedAt = employee.updatedAt
        )
    }

    private fun toDependentEntity(dto: DependentDto): Dependent {
        return Dependent(
            dependentTypeId = dto.dependentTypeId,
            dateOfBirth = dto.dateOfBirth,
            firstName = dto.firstName,
            lastName = dto.lastName,
            gender = dto.gender,
            phoneNumber = dto.phoneNumber
        )
    }

    private fun toContactPersonEntity(dto: ContactPersonDto): ContactPerson {
        return ContactPerson(
            firstName = dto.firstName,
            lastName = dto.lastName,
            phoneNumber = dto.phoneNumber,
            email = dto.email,
            relationship = dto.relationship
        )
    }

    private fun toContractDetailsEntity(dto: ContractDetailsDto): ContractDetails {
        return ContractDetails(
            contractDocumentId = dto.contractDocumentId,
            contractStartDate = dto.contractStartDate,
            contractEndDate = dto.contractEndDate,
            contractStatus = dto.contractStatus,
            isInProbationPeriod = dto.isInProbationPeriod,
            probationStartDate = dto.probationStartDate,
            probationEndDate = dto.probationEndDate,
            probationPeriodRemarks = dto.probationPeriodRemarks,
            salaryType = dto.salaryType,
            salaryPeriodCyclePeriod = dto.salaryPeriodCyclePeriod,
            baseSalary = dto.baseSalary,
            salaryCurrency = dto.salaryCurrency,
            levelId = dto.levelId,
            roles = dto.roles?.toMutableList() ?: mutableListOf(),
            contractJobType = dto.contractJobType,
            contractPeriodType = dto.contractPeriodType,
            supervisorId = dto.supervisorId,
            isBadgingMember = dto.isBadgingMember,
            positionCode = dto.positionCode,
            positionId = dto.positionId,
            levelCode = dto.levelCode
        )
    }

    private fun toBankDetailsEntity(dto: BankDetailsDto): BankDetails {
        return BankDetails(
            bankId = dto.bankId,
            accountNumber = dto.accountNumber,
            accountNames = dto.accountNames,
            referenceCode = dto.referenceCode
        )
    }

    private fun toDependentDto(dependent: Dependent): DependentDto {
        return DependentDto(
            dependentTypeId = dependent.dependentTypeId,
            dateOfBirth = dependent.dateOfBirth,
            firstName = dependent.firstName,
            lastName = dependent.lastName,
            gender = dependent.gender,
            phoneNumber = dependent.phoneNumber,
            documents = null
        )
    }

    private fun toContactPersonDto(contactPerson: ContactPerson): ContactPersonDto {
        return ContactPersonDto(
            firstName = contactPerson.firstName,
            lastName = contactPerson.lastName,
            phoneNumber = contactPerson.phoneNumber,
            email = contactPerson.email,
            relationship = contactPerson.relationship
        )
    }

    private fun toContractDetailsDto(contractDetails: ContractDetails): ContractDetailsDto {
        return ContractDetailsDto(
            contractDocumentId = contractDetails.contractDocumentId,
            contractStartDate = contractDetails.contractStartDate,
            contractEndDate = contractDetails.contractEndDate,
            contractStatus = contractDetails.contractStatus,
            isInProbationPeriod = contractDetails.isInProbationPeriod,
            probationStartDate = contractDetails.probationStartDate,
            probationEndDate = contractDetails.probationEndDate,
            probationPeriodRemarks = contractDetails.probationPeriodRemarks,
            salaryType = contractDetails.salaryType,
            salaryPeriodCyclePeriod = contractDetails.salaryPeriodCyclePeriod,
            baseSalary = contractDetails.baseSalary,
            salaryCurrency = contractDetails.salaryCurrency,
            levelId = contractDetails.levelId,
            roles = contractDetails.roles,
            contractJobType = contractDetails.contractJobType,
            contractPeriodType = contractDetails.contractPeriodType,
            supervisorId = contractDetails.supervisorId,
            isBadgingMember = contractDetails.isBadgingMember,
            positionCode = contractDetails.positionCode,
            positionId = contractDetails.positionId,
            levelCode = contractDetails.levelCode
        )
    }

    private fun toBankDetailsDto(bankDetails: BankDetails): BankDetailsDto {
        return BankDetailsDto(
            bankId = bankDetails.bankId,
            accountNumber = bankDetails.accountNumber,
            accountNames = bankDetails.accountNames,
            referenceCode = bankDetails.referenceCode
        )
    }
}
