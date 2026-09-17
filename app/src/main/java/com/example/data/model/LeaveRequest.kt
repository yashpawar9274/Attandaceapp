package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class LeaveType(val displayName: String) {
    CASUAL_LEAVE("Casual Leave"),
    SICK_LEAVE("Sick Leave"),
    EARNED_LEAVE("Earned Leave"),
    EMERGENCY_LEAVE("Emergency Leave"),
    UNPAID_LEAVE("Unpaid Leave");

    companion object {
        fun fromString(value: String): LeaveType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: CASUAL_LEAVE
        }
    }
}

enum class LeaveStatus(val displayName: String) {
    PENDING("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    companion object {
        fun fromString(value: String): LeaveStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}

@Entity(
    tableName = "leave_requests",
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
        Index(value = ["status"])
    ]
)
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val staffId: Long,
    val leaveType: LeaveType = LeaveType.CASUAL_LEAVE,
    val startDate: String,       // YYYY-MM-DD
    val endDate: String,         // YYYY-MM-DD
    val daysCount: Int = 1,
    val reason: String,
    val isPaid: Boolean = true,
    val status: LeaveStatus = LeaveStatus.PENDING,
    val reviewNotes: String = "",
    val reviewedBy: String = "Manager",
    val appliedAtMillis: Long = System.currentTimeMillis()
)
