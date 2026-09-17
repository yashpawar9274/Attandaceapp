package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AttendanceType(val displayName: String, val defaultRegularHours: Double) {
    FULL_DAY("Full Day Present", 8.0),
    HALF_DAY("Half Day", 4.0),
    OVERTIME_DAY("Overtime Day", 8.0),
    SITE_VISIT("Site / Field Duty", 8.0),
    PAID_LEAVE("Paid Leave", 8.0),
    ABSENT("Absent / Unpaid", 0.0);

    companion object {
        fun fromString(value: String): AttendanceType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: FULL_DAY
        }
    }
}

@Entity(
    tableName = "attendance_records",
    foreignKeys = [
        ForeignKey(
            entity = StaffMember::class,
            parentColumns = ["id"],
            childColumns = ["staffId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["staffId"]),
        Index(value = ["date"]),
        Index(value = ["staffId", "date"], unique = true)
    ]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val staffId: Long,
    val date: String,             // YYYY-MM-DD
    val attendanceType: AttendanceType = AttendanceType.FULL_DAY,
    val regularHours: Double = 8.0,
    val overtimeHours: Double = 0.0,
    val clockInTime: String = "09:00 AM",
    val clockOutTime: String = "06:00 PM",
    val siteLocation: String = "Green Valley Project Site",
    val notes: String = ""
)
