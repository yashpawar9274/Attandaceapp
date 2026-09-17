package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StaffMember
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffDao {
    @Query("SELECT * FROM staff_members WHERE isActive = 1 ORDER BY employeeCode ASC")
    fun getAllActiveStaff(): Flow<List<StaffMember>>

    @Query("SELECT * FROM staff_members ORDER BY employeeCode ASC")
    fun getAllStaff(): Flow<List<StaffMember>>

    @Query("SELECT * FROM staff_members WHERE id = :id LIMIT 1")
    suspend fun getStaffById(id: Long): StaffMember?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffMember): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStaff(staffList: List<StaffMember>)

    @Update
    suspend fun updateStaff(staff: StaffMember)

    @Delete
    suspend fun deleteStaff(staff: StaffMember)

    @Query("SELECT COUNT(*) FROM staff_members")
    suspend fun getStaffCount(): Int
}
