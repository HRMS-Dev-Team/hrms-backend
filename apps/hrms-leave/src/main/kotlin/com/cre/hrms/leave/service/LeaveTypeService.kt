package com.cre.hrms.leave.service

import com.cre.hrms.core.enums.LeaveCategory
import com.cre.hrms.dto.leave.CreateLeaveTypeRequest
import com.cre.hrms.dto.leave.LeaveTypeResponse
import com.cre.hrms.dto.leave.UpdateLeaveTypeRequest
import com.cre.hrms.leave.mapper.LeaveTypeMapper
import com.cre.hrms.persistence.leave.repository.LeaveTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class LeaveTypeService(
    private val leaveTypeRepository: LeaveTypeRepository,
    private val leaveTypeMapper: LeaveTypeMapper
) {
    private val logger = LoggerFactory.getLogger(LeaveTypeService::class.java)

    @Transactional
    fun createLeaveType(request: CreateLeaveTypeRequest): LeaveTypeResponse {
        if (leaveTypeRepository.existsByCode(request.code)) {
            throw RuntimeException("Leave type code already exists: ${request.code}")
        }

        val leaveType = leaveTypeMapper.toEntity(request)
        val savedLeaveType = leaveTypeRepository.save(leaveType)

        logger.info("Leave type created: ${savedLeaveType.id}")
        return leaveTypeMapper.toResponse(savedLeaveType)
    }

    @Transactional(readOnly = true)
    fun getLeaveTypeById(id: UUID): LeaveTypeResponse {
        val leaveType = leaveTypeRepository.findById(id)
            .orElseThrow { RuntimeException("Leave type not found: $id") }
        return leaveTypeMapper.toResponse(leaveType)
    }

    @Transactional(readOnly = true)
    fun getLeaveTypeByCode(code: String): LeaveTypeResponse {
        val leaveType = leaveTypeRepository.findByCode(code)
            ?: throw RuntimeException("Leave type not found with code: $code")
        return leaveTypeMapper.toResponse(leaveType)
    }

    @Transactional(readOnly = true)
    fun getAllLeaveTypes(pageable: Pageable): Page<LeaveTypeResponse> {
        return leaveTypeRepository.findAll(pageable)
            .map { leaveTypeMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getLeaveTypesByCompany(companyId: UUID, activeOnly: Boolean): List<LeaveTypeResponse> {
        val leaveTypes = if (activeOnly) {
            leaveTypeRepository.findByCompanyIdAndIsActive(companyId, true)
        } else {
            leaveTypeRepository.findByCompanyId(companyId)
        }
        return leaveTypes.map { leaveTypeMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getLeaveTypesByCategory(category: LeaveCategory): List<LeaveTypeResponse> {
        return leaveTypeRepository.findByCategory(category)
            .map { leaveTypeMapper.toResponse(it) }
    }

    @Transactional
    fun updateLeaveType(id: UUID, request: UpdateLeaveTypeRequest): LeaveTypeResponse {
        val leaveType = leaveTypeRepository.findById(id)
            .orElseThrow { RuntimeException("Leave type not found: $id") }

        request.code?.let {
            if (it != leaveType.code && leaveTypeRepository.existsByCode(it)) {
                throw RuntimeException("Leave type code already exists: $it")
            }
        }

        leaveTypeMapper.updateEntity(leaveType, request)
        val updatedLeaveType = leaveTypeRepository.save(leaveType)

        logger.info("Leave type updated: ${updatedLeaveType.id}")
        return leaveTypeMapper.toResponse(updatedLeaveType)
    }

    @Transactional
    fun deleteLeaveType(id: UUID) {
        if (!leaveTypeRepository.existsById(id)) {
            throw RuntimeException("Leave type not found: $id")
        }
        leaveTypeRepository.deleteById(id)
        logger.info("Leave type deleted: $id")
    }
}
