package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveDao {
    @Query("SELECT * FROM leave_requests ORDER BY appliedAtMillis DESC")
    fun getAllLeaveRequests(): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE status = :status ORDER BY appliedAtMillis DESC")
    fun getLeaveRequestsByStatus(status: LeaveStatus): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE staffId = :staffId ORDER BY appliedAtMillis DESC")
    fun getLeavesForStaff(staffId: Long): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE status = 'APPROVED' AND isPaid = 1 AND (startDate LIKE :monthPrefix || '%' OR endDate LIKE :monthPrefix || '%')")
    fun getApprovedPaidLeavesForMonth(monthPrefix: String): Flow<List<LeaveRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(leave: LeaveRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(leaves: List<LeaveRequest>)

    @Update
    suspend fun update(leave: LeaveRequest)

    @Query("UPDATE leave_requests SET status = :status, reviewNotes = :reviewNotes, isPaid = :isPaid WHERE id = :id")
    suspend fun updateStatus(id: Long, status: LeaveStatus, reviewNotes: String, isPaid: Boolean)

    @Delete
    suspend fun delete(leave: LeaveRequest)

    @Query("SELECT COUNT(*) FROM leave_requests WHERE status = 'PENDING'")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM leave_requests")
    suspend fun getCount(): Int

    @Query("DELETE FROM leave_requests")
    suspend fun deleteAllLeaves()
}
