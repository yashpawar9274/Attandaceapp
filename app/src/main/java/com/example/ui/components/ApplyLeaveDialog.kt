package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.StaffMember
import com.example.ui.theme.OmGold

@Composable
fun ApplyLeaveDialog(
    staffList: List<StaffMember>,
    initialStaffId: Long? = null,
    onDismiss: () -> Unit,
    onSubmit: (LeaveRequest) -> Unit
) {
    var selectedStaffId by remember {
        mutableStateOf(initialStaffId ?: staffList.firstOrNull()?.id ?: 1L)
    }
    var leaveType by remember { mutableStateOf(LeaveType.CASUAL_LEAVE) }
    var startDate by remember { mutableStateOf("2026-09-22") }
    var endDate by remember { mutableStateOf("2026-09-23") }
    var daysCount by remember { mutableIntStateOf(2) }
    var reason by remember { mutableStateOf("") }
    var isPaid by remember { mutableStateOf(true) }

    val selectedStaff = staffList.find { it.id == selectedStaffId }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("apply_leave_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Submit Leave Request", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("STAFF APPLICANT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = OmGold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    staffList.take(5).forEach { staff ->
                        FilterChip(
                            selected = staff.id == selectedStaffId,
                            onClick = { selectedStaffId = staff.id },
                            label = { Text("${staff.employeeCode} - ${staff.fullName}", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Text("LEAVE TYPE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = OmGold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(LeaveType.CASUAL_LEAVE, LeaveType.SICK_LEAVE).forEach { lt ->
                        FilterChip(
                            selected = leaveType == lt,
                            onClick = { leaveType = lt },
                            label = { Text(lt.displayName, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(LeaveType.EARNED_LEAVE, LeaveType.EMERGENCY_LEAVE).forEach { lt ->
                        FilterChip(
                            selected = leaveType == lt,
                            onClick = { leaveType = lt },
                            label = { Text(lt.displayName, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("From Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("To Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Days:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf(1, 2, 3, 5).forEach { d ->
                            FilterChip(
                                selected = daysCount == d,
                                onClick = { daysCount = d },
                                label = { Text("${d}d", fontSize = 11.sp) },
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Paid Leave", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text("Include in monthly salary calculation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = isPaid,
                        onCheckedChange = { isPaid = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = OmGold)
                    )
                }

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Leave *") },
                    placeholder = { Text("e.g. Medical appointment, family event...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reason.isNotBlank()) {
                        val request = LeaveRequest(
                            staffId = selectedStaffId,
                            leaveType = leaveType,
                            startDate = startDate,
                            endDate = endDate,
                            daysCount = daysCount,
                            reason = reason,
                            isPaid = isPaid,
                            status = LeaveStatus.PENDING,
                            appliedAtMillis = System.currentTimeMillis()
                        )
                        onSubmit(request)
                    }
                },
                enabled = reason.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OmGold),
                modifier = Modifier.testTag("submit_leave_btn")
            ) {
                Text("Submit Application", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
