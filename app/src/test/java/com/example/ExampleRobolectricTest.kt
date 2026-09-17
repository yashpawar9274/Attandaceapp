package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceType
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.StaffMember
import com.example.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: AppDatabase
  private lateinit var repository: AttendanceRepository

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = AttendanceRepository(
      staffDao = db.staffDao(),
      attendanceDao = db.attendanceDao(),
      leaveDao = db.leaveDao()
    )
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OM Value Homes", appName)
  }

  @Test
  fun `clearAllData removes all records from database`() = runBlocking {
    // 1. Insert initial mock data
    repository.loadDemoSampleData()

    val initialStaff = repository.allStaff.first()
    assertTrue("Initial staff should not be empty", initialStaff.isNotEmpty())

    val initialAttendance = repository.getAttendanceForDate("2026-09-17").first()
    assertTrue("Initial attendance should not be empty", initialAttendance.isNotEmpty())

    val initialLeaves = repository.allLeaveRequests.first()
    assertTrue("Initial leaves should not be empty", initialLeaves.isNotEmpty())

    // 2. Perform Clear Mock Data
    repository.clearAllData()

    // 3. Verify all tables are cleared
    val clearedStaff = repository.allStaff.first()
    val clearedAttendance = repository.getAttendanceForDate("2026-09-17").first()
    val clearedLeaves = repository.allLeaveRequests.first()

    assertEquals(0, clearedStaff.size)
    assertEquals(0, clearedAttendance.size)
    assertEquals(0, clearedLeaves.size)
  }
}
