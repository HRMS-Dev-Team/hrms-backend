package com.cre.hrms.leave.service

import com.cre.hrms.dto.leave.AllocateLeaveBalanceRequest
import com.cre.hrms.dto.leave.LeaveBalanceResponse
import com.cre.hrms.leave.mapper.LeaveBalanceMapper
import com.cre.hrms.persistence.leave.entity.LeaveBalance
import com.cre.hrms.persistence.leave.repository.LeaveBalanceRepository
import com.cre.hrms.persistence.leave.repository.LeaveTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Service
class LeaveBalanceService(
    private val leaveBalanceRepository: LeaveBalanceRepository,
    private val leaveTypeRepository: LeaveTypeRepository,
    private val leaveBalanceMapper: LeaveBalanceMapper
) {
    private val logger = LoggerFactory.getLogger(LeaveBalanceService::class.java)

    @Transactional
    fun allocateLeaveBalance(request: AllocateLeaveBalanceRequest): LeaveBalanceResponse {
        val leaveType = leaveTypeRepository.findById(request.leaveTypeId)
            .orElseThrow { RuntimeException("Leave type not found: ${request.leaveTypeId}") }

        // Check if balance already exists
        if (leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeAndYear(
                request.employeeId, leaveType, request.year
            )) {
            throw RuntimeException("Leave balance already exists for employee ${request.employeeId}, leave type ${request.leaveTypeId}, year ${request.year}")
        }

        val carriedForward = request.carriedForward ?: BigDecimal.ZERO
        val totalAllocated = request.totalAllocated.add(carriedForward)

        val leaveBalance = LeaveBalance(
            employeeId = request.employeeId,
            leaveType = leaveType,
            year = request.year,
            totalAllocated = totalAllocated,
            used = BigDecimal.ZERO,
            pending = BigDecimal.ZERO,
            available = totalAllocated,
            carriedForward = carriedForward
        )

        val savedBalance = leaveBalanceRepository.save(leaveBalance)
        logger.info("Leave balance allocated for employee: ${request.employeeId}, leave type: ${leaveType.code}, year: ${request.year}")

        return leaveBalanceMapper.toResponse(savedBalance)
    }

    /**
     * Overloaded method for direct allocation (used by scheduler)
     */
    @Transactional
    fun allocateLeaveBalance(
        employeeId: UUID,
        leaveTypeId: UUID,
        year: Int,
        totalAllocated: BigDecimal,
        carriedForward: BigDecimal = BigDecimal.ZERO
    ): LeaveBalanceResponse {
        val leaveType = leaveTypeRepository.findById(leaveTypeId)
            .orElseThrow { RuntimeException("Leave type not found: $leaveTypeId") }

        // Check if balance already exists
        if (leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeAndYear(employeeId, leaveType, year)) {
            throw RuntimeException("Leave balance already exists for employee $employeeId, leave type $leaveTypeId, year $year")
        }

        val leaveBalance = LeaveBalance(
            employeeId = employeeId,
            leaveType = leaveType,
            year = year,
            totalAllocated = totalAllocated,
            used = BigDecimal.ZERO,
            pending = BigDecimal.ZERO,
            available = totalAllocated,
            carriedForward = carriedForward
        )

        val savedBalance = leaveBalanceRepository.save(leaveBalance)
        logger.info("Leave balance allocated for employee: $employeeId, leave type: ${leaveType.code}, year: $year")

        return leaveBalanceMapper.toResponse(savedBalance)
    }

    @Transactional(readOnly = true)
    fun getEmployeeLeaveBalances(employeeId: UUID): List<LeaveBalanceResponse> {
        val currentYear = LocalDate.now().year
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, currentYear)
            .map { leaveBalanceMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getEmployeeLeaveBalanceByYear(employeeId: UUID, year: Int): List<LeaveBalanceResponse> {
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year)
            .map { leaveBalanceMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getEmployeeLeaveBalanceForType(employeeId: UUID, leaveTypeId: UUID, year: Int): LeaveBalanceResponse {
        val leaveType = leaveTypeRepository.findById(leaveTypeId)
            .orElseThrow { RuntimeException("Leave type not found: $leaveTypeId") }

        val leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(employeeId, leaveType, year)
            ?: throw RuntimeException("Leave balance not found for employee $employeeId, leave type $leaveTypeId, year $year")

        return leaveBalanceMapper.toResponse(leaveBalance)
    }

    @Transactional
    fun adjustLeaveBalance(employeeId: UUID, leaveTypeId: UUID, year: Int, adjustment: BigDecimal): LeaveBalanceResponse {
        val leaveType = leaveTypeRepository.findById(leaveTypeId)
            .orElseThrow { RuntimeException("Leave type not found: $leaveTypeId") }

        val leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(employeeId, leaveType, year)
            ?: throw RuntimeException("Leave balance not found")

        leaveBalance.totalAllocated = leaveBalance.totalAllocated.add(adjustment)
        leaveBalance.calculateAvailable()

        val updatedBalance = leaveBalanceRepository.save(leaveBalance)
        logger.info("Leave balance adjusted for employee: $employeeId, adjustment: $adjustment")

        return leaveBalanceMapper.toResponse(updatedBalance)
    }

    @Transactional
    fun deductLeaveBalance(employeeId: UUID, leaveTypeId: UUID, days: BigDecimal) {
        val currentYear = LocalDate.now().year
        val leaveType = leaveTypeRepository.findById(leaveTypeId)
            .orElseThrow { RuntimeException("Leave type not found: $leaveTypeId") }

        val leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(employeeId, leaveType, currentYear)
            ?: throw RuntimeException("Leave balance not found")

        if (leaveBalance.available < days) {
            throw RuntimeException("Insufficient leave balance. Available: ${leaveBalance.available}, Required: $days")
        }

        leaveBalance.used = leaveBalance.used.add(days)
        leaveBalance.calculateAvailable()

        leaveBalanceRepository.save(leaveBalance)
        logger.info("Leave balance deducted: $days days for employee: $employeeId")
    }

    @Transactional
    fun reserveLeaveBalance(employeeId: UUID, leaveTypeId: UUID, days: BigDecimal) {
        val currentYear = LocalDate.now().year
        val leaveType = leaveTypeRepository.findById(leaveTypeId)
            .orElseThrow { RuntimeException("Leave type not found: $leaveTypeId") }

        val leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(employeeId, leaveType, currentYear)
            ?: throw RuntimeException("Leave balance not found")

        if (leaveBalance.available < days) {
            throw RuntimeException("Insufficient leave balance. Available: ${leaveBalance.available}, Required: $days")
        }

        leaveBalance.pending = leaveBalance.pending.add(days)
        leaveBalance.calculateAvailable()

        leaveBalanceRepository.save(leaveBalance)
        logger.info("Leave balance reserved: $days days for employee: $employeeId")
    }

    @Transactional
    fun releaseLeaveBalance(employeeId: UUID, leaveTypeId: UUID, days: BigDecimal) {
        val currentYear = LocalDate.now().year
        val leaveType = leaveTypeRepository.findById(leaveTypeId)
            .orElseThrow { RuntimeException("Leave type not found: $leaveTypeId") }

        val leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(employeeId, leaveType, currentYear)
            ?: throw RuntimeException("Leave balance not found")

        leaveBalance.pending = leaveBalance.pending.subtract(days)
        leaveBalance.calculateAvailable()

        leaveBalanceRepository.save(leaveBalance)
        logger.info("Leave balance released: $days days for employee: $employeeId")
    }

    @Transactional
    fun confirmLeaveUsage(employeeId: UUID, leaveTypeId: UUID, days: BigDecimal) {
        val currentYear = LocalDate.now().year
        val leaveType = leaveTypeRepository.findById(leaveTypeId)
            .orElseThrow { RuntimeException("Leave type not found: $leaveTypeId") }

        val leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(employeeId, leaveType, currentYear)
            ?: throw RuntimeException("Leave balance not found")

        leaveBalance.pending = leaveBalance.pending.subtract(days)
        leaveBalance.used = leaveBalance.used.add(days)
        leaveBalance.calculateAvailable()

        leaveBalanceRepository.save(leaveBalance)
        logger.info("Leave usage confirmed: $days days for employee: $employeeId")
    }
}
