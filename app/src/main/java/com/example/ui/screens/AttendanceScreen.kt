package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceType
import com.example.data.model.StaffMember
import com.example.ui.components.RecordAttendanceDialog
import com.example.ui.theme.OmBlue
import com.example.ui.theme.OmEmerald
import com.example.ui.theme.OmGold
import com.example.ui.theme.OmNavyDark
import com.example.ui.theme.OmOrange
import com.example.ui.theme.OmRed
import com.example.ui.viewmodel.AttendanceViewModel

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    staffList: List<StaffMember>,
    attendanceRecords: List<AttendanceRecord>,
    selectedDate: String,
    modifier: Modifier = Modifier
) {
    var showRecordDialog by remember { mutableStateOf(false) }
    var staffForDialog by remember { mutableStateOf<StaffMember?>(null) }
    var existingRecordForDialog by remember { mutableStateOf<AttendanceRecord?>(null) }
    var selectedDepartment by remember { mutableStateOf("All") }

    val attendanceMap = remember(attendanceRecords) {
        attendanceRecords.associateBy { it.staffId }
    }

    val departments = listOf("All", "Construction & Site", "Civil Engineering", "Architecture & Design", "Interiors & Carpentry", "Quality & Safety", "Accounts & CRM", "MEP & Utilities")

    val filteredStaff = remember(staffList, selectedDepartment) {
        if (selectedDepartment == "All") staffList
        else staffList.filter { it.department == selectedDepartment }
    }

    // Attendance stats for selected date
    val totalStaffCount = staffList.size
    val presentCount = attendanceRecords.count { it.attendanceType != AttendanceType.ABSENT }
    val overtimeCount = attendanceRecords.count { it.overtimeHours > 0 }
    val totalOtHoursToday = attendanceRecords.sumOf { it.overtimeHours }

    val recentDates = listOf(
        "2026-09-14", "2026-09-15", "2026-09-16", "2026-09-17", "2026-09-18"
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Date Selector Bar
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daily Attendance Roster",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(OmGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = selectedDate,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = OmGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Date Quick Selection Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recentDates.forEach { date ->
                                val isSelected = date == selectedDate
                                val dayNum = date.takeLast(2)
                                val dayLabel = when (dayNum) {
                                    "14" -> "Mon 14"
                                    "15" -> "Tue 15"
                                    "16" -> "Wed 16"
                                    "17" -> "Today 17"
                                    "18" -> "Fri 18"
                                    else -> dayNum
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) OmNavyDark else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { viewModel.setSelectedDate(date) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayLabel,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Summary KPI Cards for Today
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = OmEmerald.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("PRESENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OmEmerald)
                            Text("$presentCount / $totalStaffCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OmEmerald)
                            Text("${(presentCount.toFloat() / totalStaffCount.coerceAtLeast(1) * 100).toInt()}% On Duty", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = OmOrange.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("OVERTIME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OmOrange)
                            Text("${totalOtHoursToday.toInt()} hrs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OmOrange)
                            Text("$overtimeCount staff overtime", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = OmGold.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("PENDING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OmGold)
                            val unrecorded = totalStaffCount - attendanceRecords.size
                            Text("$unrecorded", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OmGold)
                            Text("To be checked in", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Department filter chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    departments.forEach { dept ->
                        FilterChip(
                            selected = selectedDepartment == dept,
                            onClick = { selectedDepartment = dept },
                            label = { Text(dept, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Staff Attendance Roster List
            item {
                Text(
                    text = "STAFF MEMBERS (${filteredStaff.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(filteredStaff, key = { it.id }) { staff ->
                val record = attendanceMap[staff.id]
                StaffAttendanceCard(
                    staff = staff,
                    record = record,
                    selectedDate = selectedDate,
                    onQuickPresent = { viewModel.quickMarkPresent(staff.id, selectedDate) },
                    onQuickOvertime = { viewModel.quickMarkOvertime(staff.id, selectedDate, 2.0) },
                    onEditRecord = {
                        staffForDialog = staff
                        existingRecordForDialog = record
                        showRecordDialog = true
                    }
                )
            }
        }

        // FAB to Record Attendance
        FloatingActionButton(
            onClick = {
                staffForDialog = null
                existingRecordForDialog = null
                showRecordDialog = true
            },
            containerColor = OmGold,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 20.dp)
                .testTag("fab_record_attendance")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Record Attendance")
        }
    }

    if (showRecordDialog) {
        RecordAttendanceDialog(
            staffList = staffList,
            initialStaff = staffForDialog,
            selectedDate = selectedDate,
            existingRecord = existingRecordForDialog,
            onDismiss = { showRecordDialog = false },
            onSave = { updatedRecord ->
                viewModel.saveAttendanceRecord(updatedRecord)
                showRecordDialog = false
            }
        )
    }
}

@Composable
fun StaffAttendanceCard(
    staff: StaffMember,
    record: AttendanceRecord?,
    selectedDate: String,
    onQuickPresent: () -> Unit,
    onQuickOvertime: () -> Unit,
    onEditRecord: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Staff Info & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(OmNavyDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = staff.employeeCode.takeLast(3),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = staff.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${staff.role} • ${staff.department}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Hourly Rate Chip
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "₹${staff.hourlyRate.toInt()}/h",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = OmGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Attendance Status Details
            if (record != null) {
                val (badgeBg, badgeFg, label) = when (record.attendanceType) {
                    AttendanceType.FULL_DAY -> Triple(OmEmerald.copy(alpha = 0.15f), OmEmerald, "Full Day (8 hrs)")
                    AttendanceType.HALF_DAY -> Triple(OmBlue.copy(alpha = 0.15f), OmBlue, "Half Day (4 hrs)")
                    AttendanceType.OVERTIME_DAY -> Triple(OmOrange.copy(alpha = 0.15f), OmOrange, "Overtime (+${record.overtimeHours.toInt()}h OT)")
                    AttendanceType.SITE_VISIT -> Triple(OmGold.copy(alpha = 0.15f), OmGold, "Site Visit (8 hrs)")
                    AttendanceType.PAID_LEAVE -> Triple(Color(0xFFE0E7FF), Color(0xFF4338CA), "Paid Leave")
                    AttendanceType.ABSENT -> Triple(OmRed.copy(alpha = 0.15f), OmRed, "Absent / Unpaid")
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(badgeBg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(badgeFg)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = badgeFg)
                        }

                        // Regular + OT pay for this day
                        val regPay = record.regularHours * staff.hourlyRate
                        val otPay = record.overtimeHours * (staff.hourlyRate * staff.overtimeMultiplier)
                        val totalDay = regPay + otPay
                        Text(
                            text = "Earned: ₹${totalDay.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Location and Clock Times
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${record.clockInTime} - ${record.clockOutTime}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(record.siteLocation.take(18) + "..", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (record.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Note: ${record.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Edit button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onEditRecord,
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Record", fontSize = 11.sp)
                    }
                }

            } else {
                // Not Marked Yet - Quick Action Buttons
                Text(
                    text = "No attendance recorded for this date",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onQuickPresent,
                        colors = ButtonDefaults.buttonColors(containerColor = OmEmerald),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Present (8h)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onQuickOvertime,
                        colors = ButtonDefaults.buttonColors(containerColor = OmOrange),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.MoreTime, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Present + 2h OT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onEditRecord,
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Custom", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
