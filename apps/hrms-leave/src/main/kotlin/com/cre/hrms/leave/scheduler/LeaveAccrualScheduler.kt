package com.cre.hrms.leave.scheduler

import com.cre.hrms.leave.service.LeaveBalanceService
import com.cre.hrms.persistence.leave.repository.LeaveBalanceRepository
import com.cre.hrms.persistence.leave.repository.LeaveTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Component
class LeaveAccrualScheduler(
    private val leaveBalanceService: LeaveBalanceService,
    private val leaveBalanceRepository: LeaveBalanceRepository,
    private val leaveTypeRepository: LeaveTypeRepository
) {
    private val logger = LoggerFactory.getLogger(LeaveAccrualScheduler::class.java)

    /**
     * Monthly accrual job - Runs on the 1st day of every month at 1:00 AM
     * Allocates monthly leave balances for leave types with monthly accrual
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    @Transactional
    fun monthlyLeaveAccrual() {
        logger.info("Starting monthly leave accrual job")

        try {
            val currentYear = LocalDate.now().year
            val leaveTypes = leaveTypeRepository.findByIsActive(true)
                .filter { it.accrualFrequency == "MONTHLY" }

            // TODO: Get actual list of active employees from employee service
            val activeEmployeeIds = getActiveEmployeeIds()

            var totalAccrued = 0

            for (leaveType in leaveTypes) {
                val monthlyAccrual = leaveType.defaultDaysPerYear.divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP)

                for (employeeId in activeEmployeeIds) {
                    try {
                        // Check if balance exists for this employee and leave type
                        val existingBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(
                            employeeId,
                            leaveType,
                            currentYear
                        )

                        if (existingBalance != null) {
                            // Add to existing balance
                            leaveBalanceService.adjustLeaveBalance(
                                employeeId,
                                leaveType.id!!,
                                currentYear,
                                monthlyAccrual
                            )
                        } else {
                            // Create new balance with monthly accrual
                            leaveBalanceService.allocateLeaveBalance(
                                employeeId,
                                leaveType.id!!,
                                currentYear,
                                monthlyAccrual
                            )
                        }

                        totalAccrued++
                    } catch (e: Exception) {
                        logger.error("Failed to accrue leave for employee $employeeId, leave type ${leaveType.name}: ${e.message}")
                    }
                }
            }

            logger.info("Monthly leave accrual job completed. Total accruals: $totalAccrued")
        } catch (e: Exception) {
            logger.error("Monthly leave accrual job failed: ${e.message}", e)
        }
    }

    /**
     * Yearly accrual job - Runs on January 1st at 2:00 AM
     * Allocates annual leave balances and handles carry-forward
     */
    @Scheduled(cron = "0 0 2 1 1 ?")
    @Transactional
    fun yearlyLeaveAccrual() {
        logger.info("Starting yearly leave accrual job")

        try {
            val currentYear = LocalDate.now().year
            val previousYear = currentYear - 1
            val leaveTypes = leaveTypeRepository.findByIsActive(true)
                .filter { it.accrualFrequency == "YEARLY" }

            // TODO: Get actual list of active employees from employee service
            val activeEmployeeIds = getActiveEmployeeIds()

            var totalAccrued = 0

            for (leaveType in leaveTypes) {
                for (employeeId in activeEmployeeIds) {
                    try {
                        // Handle carry-forward from previous year
                        val previousYearBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(
                            employeeId,
                            leaveType,
                            previousYear
                        )

                        var carryForward = BigDecimal.ZERO

                        if (previousYearBalance != null && leaveType.allowCarryForward) {
                            val available = previousYearBalance.calculateAvailable()
                            val maxCarryForward = leaveType.maxCarryForwardDays ?: leaveType.defaultDaysPerYear.toInt()

                            carryForward = if (available > BigDecimal.ZERO) {
                                available.min(BigDecimal.valueOf(maxCarryForward.toLong()))
                            } else {
                                BigDecimal.ZERO
                            }

                            logger.info("Carrying forward $carryForward days for employee $employeeId, leave type ${leaveType.name}")
                        }

                        // Allocate new balance for current year
                        val totalAllocation = leaveType.defaultDaysPerYear.add(carryForward)

                        leaveBalanceService.allocateLeaveBalance(
                            employeeId,
                            leaveType.id!!,
                            currentYear,
                            totalAllocation,
                            carryForward
                        )

                        totalAccrued++
                    } catch (e: Exception) {
                        logger.error("Failed to accrue yearly leave for employee $employeeId, leave type ${leaveType.name}: ${e.message}")
                    }
                }
            }

            logger.info("Yearly leave accrual job completed. Total accruals: $totalAccrued")
        } catch (e: Exception) {
            logger.error("Yearly leave accrual job failed: ${e.message}", e)
        }
    }

    /**
     * Carry-forward expiry job - Runs on March 31st at 3:00 AM
     * Expires carry-forward balances that have a validity period
     */
    @Scheduled(cron = "0 0 3 31 3 ?")
    @Transactional
    fun expireCarryForwardBalances() {
        logger.info("Starting carry-forward expiry job")

        try {
            val currentYear = LocalDate.now().year
            val leaveTypes = leaveTypeRepository.findByIsActive(true)
                .filter { it.allowCarryForward && it.carryForwardExpiryMonths != null }

            var totalExpired = 0

            for (leaveType in leaveTypes) {
                val balances = leaveBalanceRepository.findByLeaveTypeAndYear(leaveType, currentYear)

                for (balance in balances) {
                    if (balance.carriedForward > BigDecimal.ZERO) {
                        // Expire the carried forward balance
                        balance.carriedForward = BigDecimal.ZERO
                        leaveBalanceRepository.save(balance)
                        totalExpired++

                        logger.info("Expired carry-forward balance for employee ${balance.employeeId}, leave type ${leaveType.name}")
                    }
                }
            }

            logger.info("Carry-forward expiry job completed. Total expired: $totalExpired")
        } catch (e: Exception) {
            logger.error("Carry-forward expiry job failed: ${e.message}", e)
        }
    }

    /**
     * Balance cleanup job - Runs every Sunday at 4:00 AM
     * Cleans up old leave balances and releases stuck pending balances
     */
    @Scheduled(cron = "0 0 4 ? * SUN")
    @Transactional
    fun cleanupLeaveBalances() {
        logger.info("Starting balance cleanup job")

        try {
            val currentYear = LocalDate.now().year
            val oldYear = currentYear - 3 // Keep balances for last 3 years

            // Delete very old balances
            val oldBalances = leaveBalanceRepository.findAll()
                .filter { it.year < oldYear }

            if (oldBalances.isNotEmpty()) {
                leaveBalanceRepository.deleteAll(oldBalances)
                logger.info("Deleted ${oldBalances.size} old leave balances from year $oldYear and earlier")
            }

            logger.info("Balance cleanup job completed")
        } catch (e: Exception) {
            logger.error("Balance cleanup job failed: ${e.message}", e)
        }
    }

    /**
     * Get list of active employee IDs
     * TODO: Replace with actual call to employee service
     */
    private fun getActiveEmployeeIds(): List<UUID> {
        // For now, get unique employee IDs from existing leave balances
        // In production, this should fetch from employee service
        return leaveBalanceRepository.findAll()
            .map { it.employeeId }
            .distinct()
    }
}
