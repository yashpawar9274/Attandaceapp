package com.example.data.model

data class StaffPayrollSummary(
    val staff: StaffMember,
    val daysPresent: Int,
    val totalRegularHours: Double,
    val totalOvertimeHours: Double,
    val regularPay: Double,
    val overtimePay: Double,
    val paidLeaveDays: Int = 0,
    val paidLeavePay: Double = 0.0,
    val grossPay: Double
)

data class CompanyPayrollSummary(
    val monthPrefix: String, // "YYYY-MM"
    val monthName: String,   // "September 2026"
    val totalStaffCount: Int,
    val totalRegularHours: Double,
    val totalOvertimeHours: Double,
    val totalRegularPayout: Double,
    val totalOvertimePayout: Double,
    val totalLeavePayout: Double,
    val totalPayrollDisbursed: Double,
    val staffSummaries: List<StaffPayrollSummary>
)
