package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff_members")
data class StaffMember(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeCode: String,      // e.g. OMVH-101
    val fullName: String,
    val department: String,        // Site Construction, Civil Eng, Architecture, Sales & CRM, etc.
    val role: String,              // Site Supervisor, Senior Carpenter, Mason, Architect, etc.
    val hourlyRate: Double,        // e.g. 350.0 (in ₹ or $)
    val overtimeMultiplier: Double = 1.5, // Standard 1.5x hourly rate
    val phone: String = "",
    val email: String = "",
    val paymentType: String = "Bank Transfer", // Bank Transfer, UPI, Cheque
    val accountDetails: String = "",           // e.g. HDFC Bank A/C: 50100... or UPI: name@upi
    val joinDate: String = "2025-01-10",
    val isActive: Boolean = true
)
