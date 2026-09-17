package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.ui.theme.OmEmerald
import com.example.ui.theme.OmGold
import com.example.ui.theme.OmNavyDark
import com.example.ui.theme.OmOrange

@Composable
fun RecordAttendanceDialog(
    staffList: List<StaffMember>,
    initialStaff: StaffMember? = null,
    selectedDate: String,
    existingRecord: AttendanceRecord? = null,
    onDismiss: () -> Unit,
    onSave: (AttendanceRecord) -> Unit
) {
    var selectedStaffId by remember {
        mutableStateOf(existingRecord?.staffId ?: initialStaff?.id ?: staffList.firstOrNull()?.id ?: 1L)
    }
    var attendanceType by remember {
        mutableStateOf(existingRecord?.attendanceType ?: AttendanceType.FULL_DAY)
    }
    var regularHours by remember {
        mutableDoubleStateOf(existingRecord?.regularHours ?: attendanceType.defaultRegularHours)
    }
    var overtimeHours by remember {
        mutableDoubleStateOf(existingRecord?.overtimeHours ?: 0.0)
    }
    var clockIn by remember {
        mutableStateOf(existingRecord?.clockInTime ?: "09:00 AM")
    }
    var clockOut by remember {
        mutableStateOf(existingRecord?.clockOutTime ?: "06:00 PM")
    }
    var siteLocation by remember {
        mutableStateOf(existingRecord?.siteLocation ?: "Green Valley Phase 2 Site")
    }
    var notes by remember {
        mutableStateOf(existingRecord?.notes ?: "")
    }

    val selectedStaff = staffList.find { it.id == selectedStaffId }

    val sites = listOf(
        "Green Valley Phase 2 Site",
        "Skyline Horizon Tower A",
        "Palm Grove Residency",
        "OM Corporate HQ Office"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("record_attendance_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = if (existingRecord != null) "Update Attendance" else "Record Staff Attendance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Date: $selectedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Staff Selector Card
                Text(
                    text = "STAFF MEMBER",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = OmGold
                )

                if (initialStaff != null || existingRecord != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(OmNavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedStaff?.employeeCode?.takeLast(3) ?: "EMP",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = selectedStaff?.fullName ?: "Staff Member",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${selectedStaff?.role} • ₹${selectedStaff?.hourlyRate?.toInt()}/hr",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // Quick select staff chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        staffList.forEach { staff ->
                            val isSelected = staff.id == selectedStaffId
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedStaffId = staff.id },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) OmGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (isSelected) OmGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = staff.fullName,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                            Text(
                                                text = "${staff.employeeCode} • ${staff.role}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = OmGold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Attendance Type Selector
                Text(
                    text = "ATTENDANCE TYPE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = OmGold
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = attendanceType == AttendanceType.FULL_DAY,
                            onClick = {
                                attendanceType = AttendanceType.FULL_DAY
                                regularHours = 8.0
                            },
                            label = { Text("Full Day (8h)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = attendanceType == AttendanceType.HALF_DAY,
                            onClick = {
                                attendanceType = AttendanceType.HALF_DAY
                                regularHours = 4.0
                            },
                            label = { Text("Half Day (4h)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = attendanceType == AttendanceType.OVERTIME_DAY,
                            onClick = {
                                attendanceType = AttendanceType.OVERTIME_DAY
                                regularHours = 8.0
                                if (overtimeHours == 0.0) overtimeHours = 2.0
                            },
                            label = { Text("Overtime Day", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OmOrange.copy(alpha = 0.2f),
                                selectedLabelColor = OmOrange
                            )
                        )
                        FilterChip(
                            selected = attendanceType == AttendanceType.SITE_VISIT,
                            onClick = {
                                attendanceType = AttendanceType.SITE_VISIT
                                regularHours = 8.0
                            },
                            label = { Text("Site Visit (8h)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = attendanceType == AttendanceType.PAID_LEAVE,
                            onClick = {
                                attendanceType = AttendanceType.PAID_LEAVE
                                regularHours = 8.0
                                overtimeHours = 0.0
                            },
                            label = { Text("Paid Leave", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = attendanceType == AttendanceType.ABSENT,
                            onClick = {
                                attendanceType = AttendanceType.ABSENT
                                regularHours = 0.0
                                overtimeHours = 0.0
                            },
                            label = { Text("Absent", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Regular & Overtime Hours Controls
                if (attendanceType != AttendanceType.ABSENT) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Regular Hours row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Regular Hours", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("Standard shift rate", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (regularHours > 0.5) regularHours -= 0.5 },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "${regularHours}h",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { if (regularHours < 12) regularHours += 0.5 },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            // Overtime Hours row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Overtime Hours", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = OmOrange)
                                    Text("Paid at 1.5x hourly rate", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (overtimeHours >= 0.5) overtimeHours -= 0.5 },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease OT", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "${overtimeHours}h",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (overtimeHours > 0) OmOrange else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { if (overtimeHours < 8) overtimeHours += 0.5 },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase OT", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            // Quick OT preset chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(1.0, 2.0, 3.0, 4.0).forEach { ot ->
                                    OutlinedButton(
                                        onClick = { overtimeHours = ot },
                                        modifier = Modifier.weight(1f).height(32.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                    ) {
                                        Text("+${ot.toInt()}h", fontSize = 11.sp)
                                    }
                                }
                            }

                            // Estimated Day Earnings Preview
                            if (selectedStaff != null) {
                                val regEarnings = regularHours * selectedStaff.hourlyRate
                                val otEarnings = overtimeHours * (selectedStaff.hourlyRate * selectedStaff.overtimeMultiplier)
                                val totalDay = regEarnings + otEarnings

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(OmEmerald.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Est. Day Earnings:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(
                                            "₹${totalDay.toInt()} (Reg: ₹${regEarnings.toInt()} + OT: ₹${otEarnings.toInt()})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OmEmerald
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Clock In / Out Times
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = clockIn,
                        onValueChange = { clockIn = it },
                        label = { Text("Clock In", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = clockOut,
                        onValueChange = { clockOut = it },
                        label = { Text("Clock Out", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Site Location
                OutlinedTextField(
                    value = siteLocation,
                    onValueChange = { siteLocation = it },
                    label = { Text("Site / Project Location", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Quick site chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    sites.take(2).forEach { site ->
                        OutlinedButton(
                            onClick = { siteLocation = site },
                            modifier = Modifier.weight(1f).height(30.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(site.take(18) + "..", fontSize = 10.sp)
                        }
                    }
                }

                // Work Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Task / Work Notes (optional)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val record = AttendanceRecord(
                        id = existingRecord?.id ?: 0L,
                        staffId = selectedStaffId,
                        date = selectedDate,
                        attendanceType = attendanceType,
                        regularHours = regularHours,
                        overtimeHours = overtimeHours,
                        clockInTime = clockIn,
                        clockOutTime = clockOut,
                        siteLocation = siteLocation,
                        notes = notes
                    )
                    onSave(record)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OmGold),
                modifier = Modifier.testTag("save_attendance_btn")
            ) {
                Text("Save Record", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
