package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.model.AttendanceType
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType

class Converters {
    @TypeConverter
    fun fromAttendanceType(value: AttendanceType?): String {
        return value?.name ?: AttendanceType.FULL_DAY.name
    }

    @TypeConverter
    fun toAttendanceType(value: String?): AttendanceType {
        return value?.let { AttendanceType.fromString(it) } ?: AttendanceType.FULL_DAY
    }

    @TypeConverter
    fun fromLeaveType(value: LeaveType?): String {
        return value?.name ?: LeaveType.CASUAL_LEAVE.name
    }

    @TypeConverter
    fun toLeaveType(value: String?): LeaveType {
        return value?.let { LeaveType.fromString(it) } ?: LeaveType.CASUAL_LEAVE
    }

    @TypeConverter
    fun fromLeaveStatus(value: LeaveStatus?): String {
        return value?.name ?: LeaveStatus.PENDING.name
    }

    @TypeConverter
    fun toLeaveStatus(value: String?): LeaveStatus {
        return value?.let { LeaveStatus.fromString(it) } ?: LeaveStatus.PENDING
    }
}
