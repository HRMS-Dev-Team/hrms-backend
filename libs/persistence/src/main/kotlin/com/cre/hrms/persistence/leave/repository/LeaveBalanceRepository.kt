package com.cre.hrms.persistence.leave.repository

import com.cre.hrms.persistence.leave.entity.LeaveBalance
import com.cre.hrms.persistence.leave.entity.LeaveType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LeaveBalanceRepository : JpaRepository<LeaveBalance, UUID> {
    fun findByEmployeeIdAndLeaveTypeAndYear(employeeId: UUID, leaveType: LeaveType, year: Int): LeaveBalance?
    fun findByEmployeeIdAndYear(employeeId: UUID, year: Int): List<LeaveBalance>
    fun findByEmployeeId(employeeId: UUID): List<LeaveBalance>
    fun findByLeaveTypeAndYear(leaveType: LeaveType, year: Int): List<LeaveBalance>
    fun existsByEmployeeIdAndLeaveTypeAndYear(employeeId: UUID, leaveType: LeaveType, year: Int): Boolean
}
