package com.example.data.repository

import com.example.data.dao.AttendanceDao
import com.example.data.dao.LeaveDao
import com.example.data.dao.StaffDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceType
import com.example.data.model.CompanyPayrollSummary
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.data.model.StaffMember
import com.example.data.model.StaffPayrollSummary
import com.example.data.seed.SeedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class AttendanceRepository(
    private val staffDao: StaffDao,
    private val attendanceDao: AttendanceDao,
    private val leaveDao: LeaveDao
) {
    val allActiveStaff: Flow<List<StaffMember>> = staffDao.getAllActiveStaff()
    val allStaff: Flow<List<StaffMember>> = staffDao.getAllStaff()
    val allLeaveRequests: Flow<List<LeaveRequest>> = leaveDao.getAllLeaveRequests()
    val pendingLeaveCount: Flow<Int> = leaveDao.getPendingCount()

    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceByDate(date)

    fun getAttendanceForMonth(monthPrefix: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceForMonth(monthPrefix)

    fun getOvertimeForMonth(monthPrefix: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getOvertimeRecordsForMonth(monthPrefix)

    fun getLeavesByStatus(status: LeaveStatus): Flow<List<LeaveRequest>> =
        leaveDao.getLeaveRequestsByStatus(status)

    // Calculate monthly payroll combining staff, attendance records, and approved paid leaves
    fun getMonthlyPayroll(monthPrefix: String): Flow<CompanyPayrollSummary> {
        return combine(
            allActiveStaff,
            attendanceDao.getAttendanceForMonth(monthPrefix),
            leaveDao.getAllLeaveRequests()
        ) { staffList, attendanceList, leaveList ->
            val attendanceByStaff = attendanceList.groupBy { it.staffId }
            val approvedPaidLeaves = leaveList.filter {
                it.status == LeaveStatus.APPROVED && it.isPaid &&
                        (it.startDate.startsWith(monthPrefix) || it.endDate.startsWith(monthPrefix))
            }.groupBy { it.staffId }

            val summaries = staffList.map { staff ->
                val records = attendanceByStaff[staff.id] ?: emptyList()
                val staffLeaves = approvedPaidLeaves[staff.id] ?: emptyList()

                val presentRecords = records.filter {
                    it.attendanceType != AttendanceType.ABSENT
                }
                val daysPresent = presentRecords.size
                val totalRegularHours = records.sumOf { it.regularHours }
                val totalOvertimeHours = records.sumOf { it.overtimeHours }

                val paidLeaveDays = staffLeaves.sumOf { it.daysCount }
                val paidLeavePay = paidLeaveDays * (8.0 * staff.hourlyRate)

                val regularPay = totalRegularHours * staff.hourlyRate
                val overtimePay = totalOvertimeHours * (staff.hourlyRate * staff.overtimeMultiplier)
                val grossPay = regularPay + overtimePay + paidLeavePay

                StaffPayrollSummary(
                    staff = staff,
                    daysPresent = daysPresent,
                    totalRegularHours = totalRegularHours,
                    totalOvertimeHours = totalOvertimeHours,
                    regularPay = regularPay,
                    overtimePay = overtimePay,
                    paidLeaveDays = paidLeaveDays,
                    paidLeavePay = paidLeavePay,
                    grossPay = grossPay
                )
            }

            val monthName = try {
                val ym = YearMonth.parse(monthPrefix)
                ym.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
            } catch (e: Exception) {
                monthPrefix
            }

            val totalRegHours = summaries.sumOf { it.totalRegularHours }
            val totalOtHours = summaries.sumOf { it.totalOvertimeHours }
            val totalRegPayout = summaries.sumOf { it.regularPay }
            val totalOtPayout = summaries.sumOf { it.overtimePay }
            val totalLeavePayout = summaries.sumOf { it.paidLeavePay }
            val totalGross = summaries.sumOf { it.grossPay }

            CompanyPayrollSummary(
                monthPrefix = monthPrefix,
                monthName = monthName,
                totalStaffCount = staffList.size,
                totalRegularHours = totalRegHours,
                totalOvertimeHours = totalOtHours,
                totalRegularPayout = totalRegPayout,
                totalOvertimePayout = totalOtPayout,
                totalLeavePayout = totalLeavePayout,
                totalPayrollDisbursed = totalGross,
                staffSummaries = summaries
            )
        }
    }

    // Database operations
    suspend fun insertStaff(staff: StaffMember): Long = staffDao.insertStaff(staff)
    suspend fun updateStaff(staff: StaffMember) = staffDao.updateStaff(staff)
    suspend fun deleteStaff(staff: StaffMember) = staffDao.deleteStaff(staff)

    suspend fun recordAttendance(record: AttendanceRecord): Long =
        attendanceDao.insertOrUpdate(record)

    suspend fun deleteAttendance(id: Long) = attendanceDao.deleteById(id)

    suspend fun submitLeaveRequest(request: LeaveRequest): Long =
        leaveDao.insert(request)

    suspend fun updateLeaveStatus(id: Long, status: LeaveStatus, reviewNotes: String, isPaid: Boolean) =
        leaveDao.updateStatus(id, status, reviewNotes, isPaid)

    suspend fun deleteLeaveRequest(request: LeaveRequest) = leaveDao.delete(request)

    // Seed data initializer
    suspend fun initializeSeedDataIfNeeded() {
        if (staffDao.getStaffCount() == 0) {
            staffDao.insertAllStaff(SeedData.sampleStaff)
            attendanceDao.insertAll(SeedData.generateSampleAttendance())
            leaveDao.insertAll(SeedData.sampleLeaveRequests)
        }
    }
}
