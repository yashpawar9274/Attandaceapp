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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.data.model.StaffMember
import com.example.ui.components.ApplyLeaveDialog
import com.example.ui.theme.OmEmerald
import com.example.ui.theme.OmGold
import com.example.ui.theme.OmNavyDark
import com.example.ui.theme.OmRed
import com.example.ui.viewmodel.AttendanceViewModel

@Composable
fun LeaveApprovalScreen(
    viewModel: AttendanceViewModel,
    staffList: List<StaffMember>,
    leaveRequests: List<LeaveRequest>,
    pendingCount: Int,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showApplyDialog by remember { mutableStateOf(false) }

    // Dialog state for reviewing a request
    var reviewRequestTarget by remember { mutableStateOf<LeaveRequest?>(null) }
    var reviewActionType by remember { mutableStateOf<LeaveStatus?>(null) }
    var reviewNoteText by remember { mutableStateOf("") }
    var reviewIsPaid by remember { mutableStateOf(true) }

    val staffMap = remember(staffList) { staffList.associateBy { it.id } }

    val filteredLeaves = remember(leaveRequests, selectedTab) {
        when (selectedTab) {
            0 -> leaveRequests.filter { it.status == LeaveStatus.PENDING }
            1 -> leaveRequests.filter { it.status == LeaveStatus.APPROVED }
            else -> leaveRequests
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Manager Dashboard Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Manager Leave Dashboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Review & approve staff leave applications",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (pendingCount > 0) {
                        Box(
                            modifier = Modifier
                                .background(OmGold, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$pendingCount Pending",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Tab Selector: Pending, Approved, All
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = OmNavyDark
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Pending ($pendingCount)",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) OmGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Approved",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "All (${leaveRequests.size})",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Empty state if no requests in tab
            if (filteredLeaves.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No leave applications in this list", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("All requests are up to date.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Leave Cards
            items(filteredLeaves, key = { it.id }) { leave ->
                val staff = staffMap[leave.staffId]
                LeaveRequestCard(
                    leave = leave,
                    staff = staff,
                    onApprove = { isPaid ->
                        reviewRequestTarget = leave
                        reviewActionType = LeaveStatus.APPROVED
                        reviewIsPaid = isPaid
                        reviewNoteText = if (isPaid) "Approved as paid leave by Operations Manager" else "Approved as unpaid leave"
                    },
                    onReject = {
                        reviewRequestTarget = leave
                        reviewActionType = LeaveStatus.REJECTED
                        reviewIsPaid = false
                        reviewNoteText = "Declined due to ongoing project milestone schedule"
                    }
                )
            }
        }

        // FAB to submit new leave request
        FloatingActionButton(
            onClick = { showApplyDialog = true },
            containerColor = OmNavyDark,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 20.dp)
                .testTag("fab_apply_leave")
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Leave Application")
        }
    }

    // Modal to confirm Approval / Rejection with review note
    reviewRequestTarget?.let { target ->
        val isApproval = reviewActionType == LeaveStatus.APPROVED
        AlertDialog(
            onDismissRequest = { reviewRequestTarget = null },
            modifier = Modifier.testTag("manager_review_dialog"),
            title = {
                Text(
                    text = if (isApproval) "Approve Leave Request" else "Reject Leave Request",
                    fontWeight = FontWeight.Bold,
                    color = if (isApproval) OmEmerald else OmRed
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val staff = staffMap[target.staffId]
                    Text("Staff: ${staff?.fullName ?: "Staff"} (${target.daysCount} days • ${target.leaveType.displayName})", fontSize = 13.sp)

                    if (isApproval) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = reviewIsPaid,
                                onClick = { reviewIsPaid = true },
                                label = { Text("Paid Leave (Count towards salary)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = !reviewIsPaid,
                                onClick = { reviewIsPaid = false },
                                label = { Text("Unpaid Leave", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = reviewNoteText,
                        onValueChange = { reviewNoteText = it },
                        label = { Text("Manager Review Note / Remarks") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isApproval) {
                            viewModel.approveLeave(target.id, reviewIsPaid, reviewNoteText)
                        } else {
                            viewModel.rejectLeave(target.id, reviewNoteText)
                        }
                        reviewRequestTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isApproval) OmEmerald else OmRed)
                ) {
                    Text(if (isApproval) "Confirm Approval" else "Confirm Rejection", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { reviewRequestTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showApplyDialog) {
        ApplyLeaveDialog(
            staffList = staffList,
            onDismiss = { showApplyDialog = false },
            onSubmit = { request ->
                viewModel.submitLeaveRequest(request)
                showApplyDialog = false
            }
        )
    }
}

@Composable
fun LeaveRequestCard(
    leave: LeaveRequest,
    staff: StaffMember?,
    onApprove: (isPaid: Boolean) -> Unit,
    onReject: () -> Unit
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OmNavyDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = staff?.employeeCode?.takeLast(3) ?: "EMP",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = staff?.fullName ?: "Staff Member",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${staff?.role} • ${staff?.department}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Badge
                val (statusBg, statusFg, statusText) = when (leave.status) {
                    LeaveStatus.PENDING -> Triple(OmGold.copy(alpha = 0.15f), OmGold, "Pending")
                    LeaveStatus.APPROVED -> Triple(OmEmerald.copy(alpha = 0.15f), OmEmerald, "Approved")
                    LeaveStatus.REJECTED -> Triple(OmRed.copy(alpha = 0.15f), OmRed, "Rejected")
                }

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(statusText, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = statusFg)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Leave Details Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(13.dp), tint = OmNavyDark)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${leave.startDate} to ${leave.endDate} (${leave.daysCount} days)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = leave.leaveType.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = OmNavyDark
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reason: ${leave.reason}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

                    if (leave.reviewNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Manager Note: ${leave.reviewNotes} (${if (leave.isPaid) "Paid" else "Unpaid"})",
                            fontSize = 11.sp,
                            color = if (leave.status == LeaveStatus.APPROVED) OmEmerald else OmRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Manager Action Buttons for Pending requests
            if (leave.status == LeaveStatus.PENDING) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onApprove(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = OmEmerald),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve (Paid)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onApprove(false) },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Unpaid", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = OmRed),
                        modifier = Modifier
                            .weight(0.9f)
                            .height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
