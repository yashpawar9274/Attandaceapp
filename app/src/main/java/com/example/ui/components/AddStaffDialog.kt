package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.model.StaffMember
import com.example.ui.theme.OmGold

@Composable
fun AddStaffDialog(
    existingStaff: StaffMember? = null,
    nextStaffIdCount: Int,
    onDismiss: () -> Unit,
    onSave: (StaffMember) -> Unit
) {
    var employeeCode by remember {
        mutableStateOf(existingStaff?.employeeCode ?: "OMVH-${101 + nextStaffIdCount}")
    }
    var fullName by remember { mutableStateOf(existingStaff?.fullName ?: "") }
    var department by remember { mutableStateOf(existingStaff?.department ?: "Construction & Site") }
    var role by remember { mutableStateOf(existingStaff?.role ?: "Site Engineer") }
    var hourlyRateStr by remember {
        mutableStateOf(existingStaff?.hourlyRate?.let { if (it % 1 == 0.0) it.toInt().toString() else it.toString() } ?: "350")
    }
    var phone by remember { mutableStateOf(existingStaff?.phone ?: "") }
    var email by remember { mutableStateOf(existingStaff?.email ?: "") }
    var paymentType by remember { mutableStateOf(existingStaff?.paymentType ?: "Bank Transfer") }
    var accountDetails by remember { mutableStateOf(existingStaff?.accountDetails ?: "") }

    val departments = listOf(
        "Construction & Site",
        "Civil Engineering",
        "Architecture & Design",
        "Interiors & Carpentry",
        "Quality & Safety",
        "Accounts & CRM",
        "MEP & Utilities"
    )

    val paymentTypes = listOf("Bank Transfer", "UPI Payment", "Cheque")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("add_staff_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingStaff != null) "Edit Staff Member" else "Add New Staff Member",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
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
                OutlinedTextField(
                    value = employeeCode,
                    onValueChange = { employeeCode = it },
                    label = { Text("Employee ID Code") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Job Role / Designation *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("DEPARTMENT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = OmGold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    departments.chunked(2).forEach { pair ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            pair.forEach { dept ->
                                FilterChip(
                                    selected = department == dept,
                                    onClick = { department = dept },
                                    label = { Text(dept, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Hourly Rate with preview
                OutlinedTextField(
                    value = hourlyRateStr,
                    onValueChange = { hourlyRateStr = it },
                    label = { Text("Hourly Rate (₹ / hr) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        val rate = hourlyRateStr.toDoubleOrNull() ?: 0.0
                        Text("Day Rate (8h): ₹${(rate * 8).toInt()} • Overtime (1.5x): ₹${(rate * 1.5).toInt()}/hr")
                    }
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("PAYMENT METHOD & DISBURSAL INFO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = OmGold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    paymentTypes.forEach { pt ->
                        FilterChip(
                            selected = paymentType == pt,
                            onClick = { paymentType = pt },
                            label = { Text(pt, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = accountDetails,
                    onValueChange = { accountDetails = it },
                    label = { Text("Bank A/C / IFSC / UPI ID") },
                    placeholder = { Text("e.g. HDFC A/C: 501004928192 IFSC: HDFC000123") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank()) {
                        val rate = hourlyRateStr.toDoubleOrNull() ?: 350.0
                        val staff = StaffMember(
                            id = existingStaff?.id ?: 0L,
                            employeeCode = employeeCode.ifBlank { "OMVH-100" },
                            fullName = fullName,
                            department = department,
                            role = role.ifBlank { "Staff Member" },
                            hourlyRate = rate,
                            overtimeMultiplier = 1.5,
                            phone = phone,
                            email = email,
                            paymentType = paymentType,
                            accountDetails = accountDetails
                        )
                        onSave(staff)
                    }
                },
                enabled = fullName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OmGold),
                modifier = Modifier.testTag("save_staff_btn")
            ) {
                Text(if (existingStaff != null) "Update" else "Save Staff", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
