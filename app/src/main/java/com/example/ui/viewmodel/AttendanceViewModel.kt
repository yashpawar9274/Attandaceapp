package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceType
import com.example.data.model.CompanyPayrollSummary
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.data.model.StaffMember
import com.example.data.repository.AttendanceRepository
import com.example.util.PdfPayrollExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class PdfExportStatus {
    object Idle : PdfExportStatus()
    object Generating : PdfExportStatus()
    data class Success(val file: File, val monthName: String) : PdfExportStatus()
    data class Error(val message: String) : PdfExportStatus()
}

@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceViewModel(
    private val repository: AttendanceRepository
) : ViewModel() {

    // Default to current date context: September 17, 2026
    private val _selectedDate = MutableStateFlow("2026-09-17")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedMonthPrefix = MutableStateFlow("2026-09")
    val selectedMonthPrefix: StateFlow<String> = _selectedMonthPrefix.asStateFlow()

    private val _pdfExportStatus = MutableStateFlow<PdfExportStatus>(PdfExportStatus.Idle)
    val pdfExportStatus: StateFlow<PdfExportStatus> = _pdfExportStatus.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    val activeStaffList: StateFlow<List<StaffMember>> = repository.allActiveStaff
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allStaffList: StateFlow<List<StaffMember>> = repository.allStaff
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dailyAttendance: StateFlow<List<AttendanceRecord>> = _selectedDate
        .flatMapLatest { date -> repository.getAttendanceForDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val monthlyPayroll: StateFlow<CompanyPayrollSummary?> = _selectedMonthPrefix
        .flatMapLatest { month -> repository.getMonthlyPayroll(month) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val overtimeRecordsForMonth: StateFlow<List<AttendanceRecord>> = _selectedMonthPrefix
        .flatMapLatest { month -> repository.getOvertimeForMonth(month) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val leaveRequests: StateFlow<List<LeaveRequest>> = repository.allLeaveRequests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingLeaveCount: StateFlow<Int> = repository.pendingLeaveCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        // Update month prefix if needed
        val monthPart = date.take(7)
        if (monthPart != _selectedMonthPrefix.value) {
            _selectedMonthPrefix.value = monthPart
        }
    }

    fun setSelectedMonth(monthPrefix: String) {
        _selectedMonthPrefix.value = monthPrefix
        // If selected date is outside month, update selected date to 1st of month
        if (!_selectedDate.value.startsWith(monthPrefix)) {
            _selectedDate.value = "$monthPrefix-01"
        }
    }

    fun quickMarkPresent(staffId: Long, date: String, siteLocation: String = "Green Valley Phase 2 Site") {
        viewModelScope.launch {
            val record = AttendanceRecord(
                staffId = staffId,
                date = date,
                attendanceType = AttendanceType.FULL_DAY,
                regularHours = 8.0,
                overtimeHours = 0.0,
                clockInTime = "09:00 AM",
                clockOutTime = "06:00 PM",
                siteLocation = siteLocation,
                notes = "Marked via Quick Check-in"
            )
            repository.recordAttendance(record)
        }
    }

    fun quickMarkOvertime(staffId: Long, date: String, otHours: Double, siteLocation: String = "Green Valley Phase 2 Site") {
        viewModelScope.launch {
            val record = AttendanceRecord(
                staffId = staffId,
                date = date,
                attendanceType = AttendanceType.OVERTIME_DAY,
                regularHours = 8.0,
                overtimeHours = otHours,
                clockInTime = "08:30 AM",
                clockOutTime = "08:30 PM",
                siteLocation = siteLocation,
                notes = "Logged $otHours hrs overtime on concrete & framing works"
            )
            repository.recordAttendance(record)
        }
    }

    fun saveAttendanceRecord(record: AttendanceRecord) {
        viewModelScope.launch {
            repository.recordAttendance(record)
        }
    }

    fun deleteAttendance(id: Long) {
        viewModelScope.launch {
            repository.deleteAttendance(id)
        }
    }

    fun addNewStaff(staff: StaffMember) {
        viewModelScope.launch {
            repository.insertStaff(staff)
        }
    }

    fun updateStaff(staff: StaffMember) {
        viewModelScope.launch {
            repository.updateStaff(staff)
        }
    }

    fun deleteStaff(staff: StaffMember) {
        viewModelScope.launch {
            repository.deleteStaff(staff)
        }
    }

    fun submitLeaveRequest(leave: LeaveRequest) {
        viewModelScope.launch {
            repository.submitLeaveRequest(leave)
        }
    }

    fun approveLeave(id: Long, isPaid: Boolean, notes: String = "Approved by Operations Manager") {
        viewModelScope.launch {
            repository.updateLeaveStatus(id, LeaveStatus.APPROVED, notes, isPaid)
        }
    }

    fun rejectLeave(id: Long, notes: String = "Rejected due to urgent site milestone") {
        viewModelScope.launch {
            repository.updateLeaveStatus(id, LeaveStatus.REJECTED, notes, false)
        }
    }

    fun exportPayrollPdf(context: Context) {
        val summary = monthlyPayroll.value
        if (summary == null || summary.staffSummaries.isEmpty()) {
            _pdfExportStatus.value = PdfExportStatus.Error("No payroll data available for ${selectedMonthPrefix.value} to export.")
            return
        }

        viewModelScope.launch {
            _pdfExportStatus.value = PdfExportStatus.Generating
            try {
                val file = withContext(Dispatchers.IO) {
                    PdfPayrollExporter.generatePayrollPdf(context, summary)
                }
                _pdfExportStatus.value = PdfExportStatus.Success(file, summary.monthName)
            } catch (e: Exception) {
                _pdfExportStatus.value = PdfExportStatus.Error("Failed to generate PDF: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun clearAllMockData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun loadDemoData() {
        viewModelScope.launch {
            repository.loadDemoSampleData()
        }
    }

    fun dismissPdfExportDialog() {
        _pdfExportStatus.value = PdfExportStatus.Idle
    }
}

class AttendanceViewModelFactory(
    private val repository: AttendanceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            return AttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
