package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.StaffMember
import com.example.ui.components.RecordAttendanceDialog
import com.example.ui.theme.OmEmerald
import com.example.ui.theme.OmGold
import com.example.ui.theme.OmNavyDark
import com.example.ui.theme.OmOrange
import com.example.ui.viewmodel.AttendanceViewModel
import java.util.Locale

@Composable
fun OvertimeScreen(
    viewModel: AttendanceViewModel,
    staffList: List<StaffMember>,
    overtimeRecords: List<AttendanceRecord>,
    selectedMonthPrefix: String,
    modifier: Modifier = Modifier
) {
    var showLogOvertimeDialog by remember { mutableStateOf(false) }

    val totalOvertimeHours = remember(overtimeRecords) {
        overtimeRecords.sumOf { it.overtimeHours }
    }

    val staffMap = remember(staffList) { staffList.associateBy { it.id } }

    // Aggregate overtime by staff
    val staffOvertimeAggregates = remember(overtimeRecords, staffList) {
        val grouped = overtimeRecords.groupBy { it.staffId }
        staffList.map { staff ->
            val records = grouped[staff.id] ?: emptyList()
            val otHours = records.sumOf { it.overtimeHours }
            val otPay = otHours * (staff.hourlyRate * staff.overtimeMultiplier)
            Triple(staff, otHours, otPay)
        }.sortedByDescending { it.second }
    }

    val maxOtHours = remember(staffOvertimeAggregates) {
        (staffOvertimeAggregates.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)
    }

    val totalOtPayout = remember(staffOvertimeAggregates) {
        staffOvertimeAggregates.sumOf { it.third }
    }

    fun formatMoney(amount: Double): String {
        return "₹" + String.format(Locale.US, "%,.0f", amount)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Header
            item {
                Column {
                    Text(
                        text = "Overtime Tracker",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Monitor extra shifts & 1.5x wage payout for $selectedMonthPrefix",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Overtime Banner Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OmOrange.copy(alpha = 0.12f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL OVERTIME HOURS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OmOrange
                            )
                            Box(
                                modifier = Modifier
                                    .background(OmOrange.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "1.5x Wage Multiplier",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OmOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${String.format(Locale.US, "%.1f", totalOvertimeHours)} Hours",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = OmOrange
                        )

                        Text(
                            text = "Estimated Overtime Payout: ${formatMoney(totalOtPayout)} across ${overtimeRecords.size} extra shifts",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Staff Overtime Ranking & Hours Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STAFF OVERTIME LEADERBOARD",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = OmOrange, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        staffOvertimeAggregates.forEach { (staff, otHours, otPay) ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(staff.fullName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("${staff.role} • Rate: ₹${(staff.hourlyRate * 1.5).toInt()}/ot hr", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${otHours.toInt()} hrs",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (otHours > 0) OmOrange else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = formatMoney(otPay),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = OmGold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (otHours / maxOtHours).toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = OmOrange,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Recent Overtime Shift Logs Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OVERTIME SHIFT LOGS (${overtimeRecords.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Overtime Shift Cards
            items(overtimeRecords, key = { it.id }) { record ->
                val staff = staffMap[record.staffId]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(OmOrange.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.MoreTime, contentDescription = null, tint = OmOrange, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = staff?.fullName ?: "Staff Member",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${record.date} • ${staff?.role}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Overtime badge
                            Box(
                                modifier = Modifier
                                    .background(OmOrange.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+${record.overtimeHours.toInt()} hrs OT",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = OmOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(record.siteLocation, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (staff != null) {
                                val otPayout = record.overtimeHours * (staff.hourlyRate * staff.overtimeMultiplier)
                                Text("OT Pay: ₹${otPayout.toInt()}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = OmEmerald)
                            }
                        }

                        if (record.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Work: ${record.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Floating button to log new overtime shift
        FloatingActionButton(
            onClick = { showLogOvertimeDialog = true },
            containerColor = OmOrange,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 20.dp)
                .testTag("fab_log_overtime")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Log Overtime")
        }
    }

    if (showLogOvertimeDialog) {
        RecordAttendanceDialog(
            staffList = staffList,
            selectedDate = viewModel.selectedDate.value,
            onDismiss = { showLogOvertimeDialog = false },
            onSave = { record ->
                viewModel.saveAttendanceRecord(record)
                showLogOvertimeDialog = false
            }
        )
    }
}
