package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StaffMember
import com.example.ui.components.AddStaffDialog
import com.example.ui.theme.OmEmerald
import com.example.ui.theme.OmGold
import com.example.ui.theme.OmNavyDark
import com.example.ui.theme.OmRed
import com.example.ui.viewmodel.AttendanceViewModel

@Composable
fun StaffDirectoryScreen(
    viewModel: AttendanceViewModel,
    staffList: List<StaffMember>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStaff by remember { mutableStateOf<StaffMember?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val filteredStaff = remember(staffList, searchQuery) {
        if (searchQuery.isBlank()) staffList
        else staffList.filter {
            it.fullName.contains(searchQuery, ignoreCase = true) ||
                    it.employeeCode.contains(searchQuery, ignoreCase = true) ||
                    it.department.contains(searchQuery, ignoreCase = true) ||
                    it.role.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Staff Members Directory",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Manage hourly rates, roles & payment accounts",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(OmNavyDark, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${staffList.size} Total",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Data Management Action Bar (Clear Mock Data / Reload Demo)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_clear_mock_data"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = OmRed
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Data",
                            modifier = Modifier.size(16.dp),
                            tint = OmRed
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Clear Mock Data",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = OmRed
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.loadDemoData() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_load_demo_data"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = OmNavyDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Load Sample Data",
                            modifier = Modifier.size(16.dp),
                            tint = OmNavyDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Load Sample Data",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = OmNavyDark
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, role, department or code...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }

            // Empty state when staff list is empty
            if (filteredStaff.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (staffList.isEmpty()) "Mock Data Cleared" else "No Staff Found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (staffList.isEmpty())
                                    "Database is clean. You can add new staff members with the (+) button below or reload sample data at any time."
                                else
                                    "No staff member matched \"$searchQuery\".",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            if (staffList.isEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.loadDemoData() },
                                    colors = ButtonDefaults.buttonColors(containerColor = OmGold)
                                ) {
                                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Restore Demo Sample Data", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Staff Cards
            items(filteredStaff, key = { it.id }) { staff ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                                        .size(42.dp)
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
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Hourly Rate Chip
                            Box(
                                modifier = Modifier
                                    .background(OmGold.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "₹${staff.hourlyRate.toInt()}/h",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = OmGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Payment & Wage details box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Overtime Rate (1.5x):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${(staff.hourlyRate * 1.5).toInt()}/hr", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = OmEmerald)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Full Day Rate (8h):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${(staff.hourlyRate * 8).toInt()}/day", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Payment Type:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(staff.paymentType, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                }
                                if (staff.accountDetails.isNotBlank()) {
                                    Text(
                                        text = "Routing: ${staff.accountDetails}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action row: Edit staff
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (staff.phone.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(staff.phone, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            OutlinedButton(
                                onClick = { editingStaff = staff },
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text("Edit Details", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // FAB to add new staff member
        FloatingActionButton(
            onClick = {
                editingStaff = null
                showAddDialog = true
            },
            containerColor = OmGold,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 20.dp)
                .testTag("fab_add_staff")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Staff")
        }
    }

    if (showAddDialog || editingStaff != null) {
        AddStaffDialog(
            existingStaff = editingStaff,
            nextStaffIdCount = staffList.size,
            onDismiss = {
                showAddDialog = false
                editingStaff = null
            },
            onSave = { staff ->
                if (editingStaff != null) {
                    viewModel.updateStaff(staff)
                } else {
                    viewModel.addNewStaff(staff)
                }
                showAddDialog = false
                editingStaff = null
            }
        )
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = OmRed,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Clear All Mock Data?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "This will remove all sample staff members, attendance check-ins, overtime logs, and leave requests from the local database. You will be able to add real staff or reload demo data at any time.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllMockData()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OmRed),
                    modifier = Modifier.testTag("btn_confirm_clear_mock_data")
                ) {
                    Text("Clear All Data", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
