package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.repository.AttendanceRepository
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.LeaveApprovalScreen
import com.example.ui.screens.OvertimeScreen
import com.example.ui.screens.PayrollScreen
import com.example.ui.screens.StaffDirectoryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OmEmerald
import com.example.ui.theme.OmGold
import com.example.ui.theme.OmNavyDark
import com.example.ui.theme.OmOrange
import com.example.ui.viewmodel.AttendanceViewModel
import com.example.ui.viewmodel.AttendanceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = AttendanceRepository(
            staffDao = database.staffDao(),
            attendanceDao = database.attendanceDao(),
            leaveDao = database.leaveDao()
        )
        val viewModelFactory = AttendanceViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: AttendanceViewModel = viewModel(factory = viewModelFactory)
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationTab(val title: String, val icon: ImageVector, val tag: String) {
    ATTENDANCE("Attendance", Icons.Default.DateRange, "tab_attendance"),
    PAYROLL("Earnings", Icons.Default.Payments, "tab_payroll"),
    OVERTIME("Overtime", Icons.Default.MoreTime, "tab_overtime"),
    LEAVE("Leaves", Icons.Default.AssignmentTurnedIn, "tab_leave"),
    STAFF("Staff", Icons.Default.Badge, "tab_staff")
}

@Composable
fun MainAppScreen(viewModel: AttendanceViewModel) {
    val context = LocalContext.current
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val staffList by viewModel.activeStaffList.collectAsStateWithLifecycle()
    val allStaffList by viewModel.allStaffList.collectAsStateWithLifecycle()
    val dailyAttendance by viewModel.dailyAttendance.collectAsStateWithLifecycle()
    val monthlyPayroll by viewModel.monthlyPayroll.collectAsStateWithLifecycle()
    val overtimeRecords by viewModel.overtimeRecordsForMonth.collectAsStateWithLifecycle()
    val leaveRequests by viewModel.leaveRequests.collectAsStateWithLifecycle()
    val pendingLeaveCount by viewModel.pendingLeaveCount.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedMonthPrefix by viewModel.selectedMonthPrefix.collectAsStateWithLifecycle()
    val pdfExportStatus by viewModel.pdfExportStatus.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = OmNavyDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Company Logo Emblem
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OmGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "OM Value Homes",
                                tint = OmNavyDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OM VALUE HOMES",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Staff Attendance & Payroll System",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // PDF Export Quick Action in Top Bar
                    IconButton(
                        onClick = { viewModel.exportPayrollPdf(context) },
                        modifier = Modifier.testTag("top_export_pdf_btn")
                    ) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = "Export Payroll PDF",
                            tint = OmGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationTab.entries.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier.testTag(tab.tag),
                        icon = {
                            if (tab == NavigationTab.LEAVE && pendingLeaveCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = OmGold) {
                                            Text("$pendingLeaveCount", color = Color.White, fontSize = 10.sp)
                                        }
                                    }
                                ) {
                                    Icon(tab.icon, contentDescription = tab.title)
                                }
                            } else {
                                Icon(tab.icon, contentDescription = tab.title)
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OmNavyDark,
                            selectedTextColor = OmNavyDark,
                            indicatorColor = OmGold.copy(alpha = 0.25f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (NavigationTab.entries[selectedTabIndex]) {
                NavigationTab.ATTENDANCE -> {
                    AttendanceScreen(
                        viewModel = viewModel,
                        staffList = staffList,
                        attendanceRecords = dailyAttendance,
                        selectedDate = selectedDate
                    )
                }
                NavigationTab.PAYROLL -> {
                    PayrollScreen(
                        viewModel = viewModel,
                        payrollSummary = monthlyPayroll,
                        selectedMonthPrefix = selectedMonthPrefix,
                        pdfExportStatus = pdfExportStatus
                    )
                }
                NavigationTab.OVERTIME -> {
                    OvertimeScreen(
                        viewModel = viewModel,
                        staffList = staffList,
                        overtimeRecords = overtimeRecords,
                        selectedMonthPrefix = selectedMonthPrefix
                    )
                }
                NavigationTab.LEAVE -> {
                    LeaveApprovalScreen(
                        viewModel = viewModel,
                        staffList = staffList,
                        leaveRequests = leaveRequests,
                        pendingCount = pendingLeaveCount
                    )
                }
                NavigationTab.STAFF -> {
                    StaffDirectoryScreen(
                        viewModel = viewModel,
                        staffList = allStaffList
                    )
                }
            }
        }
    }
}
