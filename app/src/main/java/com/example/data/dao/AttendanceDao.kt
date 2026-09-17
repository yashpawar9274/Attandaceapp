package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE date = :date ORDER BY id DESC")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC")
    fun getAttendanceForMonth(monthPrefix: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE staffId = :staffId AND date LIKE :monthPrefix || '%' ORDER BY date ASC")
    fun getAttendanceForStaffAndMonth(staffId: Long, monthPrefix: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE overtimeHours > 0 AND date LIKE :monthPrefix || '%' ORDER BY date DESC")
    fun getOvertimeRecordsForMonth(monthPrefix: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE staffId = :staffId AND date = :date LIMIT 1")
    suspend fun getAttendanceForStaffDate(staffId: Long, date: String): AttendanceRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AttendanceRecord>)

    @Update
    suspend fun update(record: AttendanceRecord)

    @Delete
    suspend fun delete(record: AttendanceRecord)

    @Query("DELETE FROM attendance_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM attendance_records")
    suspend fun getCount(): Int
}
