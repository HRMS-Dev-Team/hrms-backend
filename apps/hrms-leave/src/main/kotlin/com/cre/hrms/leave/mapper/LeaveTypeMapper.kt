package com.cre.hrms.leave.mapper

import com.cre.hrms.dto.leave.CreateLeaveTypeRequest
import com.cre.hrms.dto.leave.LeaveTypeResponse
import com.cre.hrms.dto.leave.UpdateLeaveTypeRequest
import com.cre.hrms.persistence.leave.entity.LeaveType
import org.springframework.stereotype.Component

@Component
class LeaveTypeMapper {

    fun toEntity(request: CreateLeaveTypeRequest): LeaveType {
        return LeaveType(
            name = request.name,
            code = request.code,
            category = request.category,
            description = request.description,
            companyId = request.companyId,
            defaultDaysPerYear = request.defaultDaysPerYear,
            maxConsecutiveDays = request.maxConsecutiveDays,
            requiresDocument = request.requiresDocument,
            minNoticeDays = request.minNoticeDays,
            isPaid = request.isPaid,
            isActive = request.isActive,
            allowCarryForward = request.allowCarryForward,
            maxCarryForwardDays = request.maxCarryForwardDays
        )
    }

    fun updateEntity(leaveType: LeaveType, request: UpdateLeaveTypeRequest) {
        request.name?.let { leaveType.name = it }
        request.code?.let { leaveType.code = it }
        request.category?.let { leaveType.category = it }
        request.description?.let { leaveType.description = it }
        request.defaultDaysPerYear?.let { leaveType.defaultDaysPerYear = it }
        request.maxConsecutiveDays?.let { leaveType.maxConsecutiveDays = it }
        request.requiresDocument?.let { leaveType.requiresDocument = it }
        request.minNoticeDays?.let { leaveType.minNoticeDays = it }
        request.isPaid?.let { leaveType.isPaid = it }
        request.isActive?.let { leaveType.isActive = it }
        request.allowCarryForward?.let { leaveType.allowCarryForward = it }
        request.maxCarryForwardDays?.let { leaveType.maxCarryForwardDays = it }
    }

    fun toResponse(leaveType: LeaveType): LeaveTypeResponse {
        return LeaveTypeResponse(
            id = leaveType.id!!,
            name = leaveType.name,
            code = leaveType.code,
            category = leaveType.category,
            description = leaveType.description,
            companyId = leaveType.companyId,
            defaultDaysPerYear = leaveType.defaultDaysPerYear,
            maxConsecutiveDays = leaveType.maxConsecutiveDays,
            requiresDocument = leaveType.requiresDocument,
            minNoticeDays = leaveType.minNoticeDays,
            isPaid = leaveType.isPaid,
            isActive = leaveType.isActive,
            allowCarryForward = leaveType.allowCarryForward,
            maxCarryForwardDays = leaveType.maxCarryForwardDays,
            createdAt = leaveType.createdAt,
            updatedAt = leaveType.updatedAt
        )
    }
}
