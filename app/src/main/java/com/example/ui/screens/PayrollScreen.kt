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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanyPayrollSummary
import com.example.data.model.StaffPayrollSummary
import com.example.ui.components.PdfExportDialog
import com.example.ui.theme.OmEmerald
import com.example.ui.theme.OmGold
import com.example.ui.theme.OmNavyDark
import com.example.ui.theme.OmOrange
import com.example.ui.viewmodel.AttendanceViewModel
import com.example.ui.viewmodel.PdfExportStatus
import java.util.Locale

@Composable
fun PayrollScreen(
    viewModel: AttendanceViewModel,
    payrollSummary: CompanyPayrollSummary?,
    selectedMonthPrefix: String,
    pdfExportStatus: PdfExportStatus,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMonthDropdown by remember { mutableStateOf(false) }
    var selectedStaffForModal by remember { mutableStateOf<StaffPayrollSummary?>(null) }

    val months = listOf(
        Pair("2026-09", "September 2026"),
        Pair("2026-08", "August 2026"),
        Pair("2026-07", "July 2026"),
        Pair("2026-10", "October 2026")
    )

    val currentMonthLabel = months.find { it.first == selectedMonthPrefix }?.second ?: selectedMonthPrefix

    fun formatMoney(amount: Double): String {
        return "₹" + String.format(Locale.US, "%,.0f", amount)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Month Selector and Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Staff Monthly Payroll",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Hourly rate calculation & accounting disbursement",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Month Selector Dropdown Button
                    Box {
                        Card(
                            modifier = Modifier.clickable { showMonthDropdown = true },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentMonthLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Month")
                            }
                        }

                        DropdownMenu(
                            expanded = showMonthDropdown,
                            onDismissRequest = { showMonthDropdown = false }
                        ) {
                            months.forEach { (prefix, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, fontWeight = if (prefix == selectedMonthPrefix) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        viewModel.setSelectedMonth(prefix)
                                        showMonthDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Executive Monthly Earnings Summary Card
            item {
                if (payrollSummary != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = OmNavyDark),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TOTAL DISBURSEMENT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OmGold
                                )
                                Box(
                                    modifier = Modifier
                                        .background(OmGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${payrollSummary.totalStaffCount} Staff Members",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OmGold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = formatMoney(payrollSummary.totalPayrollDisbursed),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "Period: ${payrollSummary.monthName} • OM Value Homes",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFF334155))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Breakdown Metrics Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("REGULAR PAY", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                    Text(formatMoney(payrollSummary.totalRegularPayout), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("${payrollSummary.totalRegularHours.toInt()} hrs", fontSize = 11.sp, color = OmEmerald)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("OVERTIME (1.5x)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = OmOrange)
                                    Text(formatMoney(payrollSummary.totalOvertimePayout), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OmOrange)
                                    Text("${payrollSummary.totalOvertimeHours.toInt()} hrs logged", fontSize = 11.sp, color = OmOrange)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("PAID LEAVES", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                    Text(formatMoney(payrollSummary.totalLeavePayout), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Approved days", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        }
                    }
                }
            }

            // Export PDF CTA Card (Accounting Department)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(OmGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = OmGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Accounting Department Report",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Export official PDF with hourly rates, hours & bank routing",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.exportPayrollPdf(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = OmGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("export_pdf_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Export PDF Payroll Report",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Itemized Staff List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STAFF EARNINGS BREAKDOWN (${payrollSummary?.staffSummaries?.size ?: 0})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Formula: (Reg H × Rate) + (OT H × Rate × 1.5)",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Staff Earnings Cards
            payrollSummary?.staffSummaries?.let { list ->
                items(list, key = { it.staff.id }) { item ->
                    StaffPayrollCard(
                        summary = item,
                        formatMoney = ::formatMoney,
                        onViewDetails = { selectedStaffForModal = item }
                    )
                }
            }
        }
    }

    // Modal dialog showing full calculation breakdown for a staff member
    selectedStaffForModal?.let { modalItem ->
        AlertDialog(
            onDismissRequest = { selectedStaffForModal = null },
            modifier = Modifier.testTag("payslip_breakdown_dialog"),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(modalItem.staff.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${modalItem.staff.employeeCode} • ${modalItem.staff.role}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OmNavyDark, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("NET MONTHLY EARNINGS", fontSize = 10.sp, color = OmGold, fontWeight = FontWeight.Bold)
                            Text(formatMoney(modalItem.grossPay), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Hourly Base Rate: ₹${modalItem.staff.hourlyRate.toInt()}/hr", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                        }
                    }

                    // Calculation breakdown table
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Days Present:", fontSize = 12.sp)
                            Text("${modalItem.daysPresent} days", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Regular Hours (${modalItem.totalRegularHours}h × ₹${modalItem.staff.hourlyRate.toInt()}):", fontSize = 12.sp)
                            Text(formatMoney(modalItem.regularPay), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Overtime (${modalItem.totalOvertimeHours}h × ₹${(modalItem.staff.hourlyRate * 1.5).toInt()}):", fontSize = 12.sp, color = OmOrange)
                            Text(formatMoney(modalItem.overtimePay), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OmOrange)
                        }
                        if (modalItem.paidLeaveDays > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Approved Paid Leaves (${modalItem.paidLeaveDays}d):", fontSize = 12.sp)
                                Text(formatMoney(modalItem.paidLeavePay), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Gross Earnings:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(formatMoney(modalItem.grossPay), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OmGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Bank Account Info
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Disbursal Method: ${modalItem.staff.paymentType}", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = modalItem.staff.accountDetails.ifBlank { "Direct Account Transfer" },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { selectedStaffForModal = null }) {
                    Text("Close")
                }
            }
        )
    }

    // PDF Export Progress / Success Dialog
    PdfExportDialog(
        status = pdfExportStatus,
        onDismiss = { viewModel.dismissPdfExportDialog() },
        onRetry = { viewModel.exportPayrollPdf(context) }
    )
}

@Composable
fun StaffPayrollCard(
    summary: StaffPayrollSummary,
    formatMoney: (Double) -> String,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Staff info + Total Pay
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(OmNavyDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = summary.staff.employeeCode.takeLast(3),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = summary.staff.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${summary.staff.role} • ₹${summary.staff.hourlyRate.toInt()}/h",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Total Gross Earnings
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatMoney(summary.grossPay),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = OmGold
                    )
                    Text(
                        text = "${summary.daysPresent} days present",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Split Bar showing Regular Hours vs Overtime Hours
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Regular pay column
                    Column {
                        Text("Regular Pay", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${summary.totalRegularHours.toInt()}h = ${formatMoney(summary.regularPay)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Overtime pay column
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Overtime (1.5x)", fontSize = 10.sp, color = OmOrange)
                        Text(
                            text = if (summary.totalOvertimeHours > 0) "${summary.totalOvertimeHours.toInt()}h = ${formatMoney(summary.overtimePay)}" else "0h",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (summary.totalOvertimeHours > 0) OmOrange else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Payment details column
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Disbursal", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(summary.staff.paymentType, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action row: View Payslip Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Payslip Breakdown", fontSize = 11.sp)
                }
            }
        }
    }
}
