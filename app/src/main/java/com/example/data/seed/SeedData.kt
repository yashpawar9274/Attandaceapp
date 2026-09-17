package com.example.data.seed

import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceType
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.StaffMember

object SeedData {
    val sampleStaff = listOf(
        StaffMember(
            id = 1L,
            employeeCode = "OMVH-101",
            fullName = "Rajesh Sharma",
            department = "Construction & Site",
            role = "Site Supervisor",
            hourlyRate = 350.0,
            overtimeMultiplier = 1.5,
            phone = "+91 98201 44521",
            email = "rajesh.sharma@omvaluehomes.com",
            paymentType = "Bank Transfer",
            accountDetails = "HDFC Bank A/C: 501004928192 | IFSC: HDFC000123"
        ),
        StaffMember(
            id = 2L,
            employeeCode = "OMVH-102",
            fullName = "Priya Kulkarni",
            department = "Civil Engineering",
            role = "Senior Civil Engineer",
            hourlyRate = 500.0,
            overtimeMultiplier = 1.5,
            phone = "+91 98334 11290",
            email = "priya.k@omvaluehomes.com",
            paymentType = "Bank Transfer",
            accountDetails = "ICICI Bank A/C: 002105018274 | IFSC: ICIC0000021"
        ),
        StaffMember(
            id = 3L,
            employeeCode = "OMVH-103",
            fullName = "Amit Patel",
            department = "Architecture & Design",
            role = "Project Architect",
            hourlyRate = 600.0,
            overtimeMultiplier = 1.5,
            phone = "+91 98210 77632",
            email = "amit.patel@omvaluehomes.com",
            paymentType = "Bank Transfer",
            accountDetails = "Axis Bank A/C: 918020045123 | IFSC: UTIB0000456"
        ),
        StaffMember(
            id = 4L,
            employeeCode = "OMVH-104",
            fullName = "Suresh Verma",
            department = "Interiors & Carpentry",
            role = "Senior Carpenter & Joiner",
            hourlyRate = 300.0,
            overtimeMultiplier = 1.5,
            phone = "+91 97654 32189",
            email = "suresh.v@omvaluehomes.com",
            paymentType = "UPI Payment",
            accountDetails = "UPI: suresh.verma@okaxis"
        ),
        StaffMember(
            id = 5L,
            employeeCode = "OMVH-105",
            fullName = "Manoj Gaikwad",
            department = "Quality & Safety",
            role = "Safety & Quality Inspector",
            hourlyRate = 400.0,
            overtimeMultiplier = 1.5,
            phone = "+91 98901 23456",
            email = "manoj.g@omvaluehomes.com",
            paymentType = "Bank Transfer",
            accountDetails = "SBI A/C: 30981726543 | IFSC: SBIN0001423"
        ),
        StaffMember(
            id = 6L,
            employeeCode = "OMVH-106",
            fullName = "Sunita Deshmukh",
            department = "Accounts & CRM",
            role = "Accounts Coordinator",
            hourlyRate = 320.0,
            overtimeMultiplier = 1.5,
            phone = "+91 98190 99881",
            email = "sunita.d@omvaluehomes.com",
            paymentType = "Bank Transfer",
            accountDetails = "Kotak Mahindra A/C: 8412938102 | IFSC: KKBK0000841"
        ),
        StaffMember(
            id = 7L,
            employeeCode = "OMVH-107",
            fullName = "Vikram Rathi",
            department = "MEP & Utilities",
            role = "Master Electrician",
            hourlyRate = 380.0,
            overtimeMultiplier = 1.5,
            phone = "+91 97541 66720",
            email = "vikram.r@omvaluehomes.com",
            paymentType = "UPI Payment",
            accountDetails = "UPI: vikram.mep@okhdfc"
        )
    )

    fun generateSampleAttendance(currentDate: String = "2026-09-17"): List<AttendanceRecord> {
        val list = mutableListOf<AttendanceRecord>()
        val dates = listOf(
            "2026-09-01", "2026-09-02", "2026-09-03", "2026-09-04", "2026-09-05",
            "2026-09-07", "2026-09-08", "2026-09-09", "2026-09-10", "2026-09-11",
            "2026-09-12", "2026-09-14", "2026-09-15", "2026-09-16", currentDate
        )

        val sites = listOf(
            "Green Valley Phase 2 Site",
            "Skyline Horizon Tower A",
            "Palm Grove Residency Vasai",
            "OM Corporate HQ Office",
            "Riverside Villas Site"
        )

        var idCounter = 1L

        for (date in dates) {
            // Rajesh Sharma (Staff 1) - high overtime contributor
            val isOt = date.endsWith("3") || date.endsWith("8") || date.endsWith("5")
            val otHours = if (isOt) 3.0 else if (date.endsWith("1") || date.endsWith("6")) 2.0 else 0.0
            list.add(
                AttendanceRecord(
                    id = idCounter++,
                    staffId = 1L,
                    date = date,
                    attendanceType = if (otHours > 0) AttendanceType.OVERTIME_DAY else AttendanceType.FULL_DAY,
                    regularHours = 8.0,
                    overtimeHours = otHours,
                    clockInTime = "08:30 AM",
                    clockOutTime = if (otHours > 0) "08:30 PM" else "05:30 PM",
                    siteLocation = sites[0],
                    notes = if (otHours > 0) "Concrete slab pouring & curing supervision" else "Normal site operations"
                )
            )

            // Priya Kulkarni (Staff 2) - Civil Engineer
            val priyaOt = if (date.endsWith("2") || date.endsWith("7")) 2.5 else 0.0
            list.add(
                AttendanceRecord(
                    id = idCounter++,
                    staffId = 2L,
                    date = date,
                    attendanceType = if (priyaOt > 0) AttendanceType.OVERTIME_DAY else AttendanceType.SITE_VISIT,
                    regularHours = 8.0,
                    overtimeHours = priyaOt,
                    clockInTime = "09:00 AM",
                    clockOutTime = if (priyaOt > 0) "07:30 PM" else "06:00 PM",
                    siteLocation = sites[1],
                    notes = "Structural beam inspection & municipal certification"
                )
            )

            // Amit Patel (Staff 3) - Architect
            list.add(
                AttendanceRecord(
                    id = idCounter++,
                    staffId = 3L,
                    date = date,
                    attendanceType = AttendanceType.FULL_DAY,
                    regularHours = 8.0,
                    overtimeHours = if (date.endsWith("4") || date.endsWith("9")) 2.0 else 0.0,
                    clockInTime = "09:30 AM",
                    clockOutTime = "06:30 PM",
                    siteLocation = sites[3],
                    notes = "Floor plan revision for Tower B & 3D client presentation"
                )
            )

            // Suresh Verma (Staff 4) - Carpenter
            val isHalfDay = date.endsWith("5")
            list.add(
                AttendanceRecord(
                    id = idCounter++,
                    staffId = 4L,
                    date = date,
                    attendanceType = if (isHalfDay) AttendanceType.HALF_DAY else AttendanceType.FULL_DAY,
                    regularHours = if (isHalfDay) 4.0 else 8.0,
                    overtimeHours = if (date.endsWith("0") || date.endsWith("6")) 3.0 else 0.0,
                    clockInTime = "09:00 AM",
                    clockOutTime = if (isHalfDay) "01:00 PM" else "06:00 PM",
                    siteLocation = sites[2],
                    notes = "Custom cabinetry and modular kitchen fitting"
                )
            )

            // Manoj Gaikwad (Staff 5) - Safety
            list.add(
                AttendanceRecord(
                    id = idCounter++,
                    staffId = 5L,
                    date = date,
                    attendanceType = AttendanceType.SITE_VISIT,
                    regularHours = 8.0,
                    overtimeHours = if (date.endsWith("1") || date.endsWith("8")) 1.5 else 0.0,
                    clockInTime = "08:45 AM",
                    clockOutTime = "05:45 PM",
                    siteLocation = sites[4],
                    notes = "Scaffolding safety audit and fire gear checklist"
                )
            )

            // Sunita Deshmukh (Staff 6) - Accounts
            list.add(
                AttendanceRecord(
                    id = idCounter++,
                    staffId = 6L,
                    date = date,
                    attendanceType = AttendanceType.FULL_DAY,
                    regularHours = 8.0,
                    overtimeHours = 0.0,
                    clockInTime = "09:30 AM",
                    clockOutTime = "06:00 PM",
                    siteLocation = sites[3],
                    notes = "Material vendor invoice audit & staff attendance tallying"
                )
            )

            // Vikram Rathi (Staff 7) - Electrician
            list.add(
                AttendanceRecord(
                    id = idCounter++,
                    staffId = 7L,
                    date = date,
                    attendanceType = AttendanceType.FULL_DAY,
                    regularHours = 8.0,
                    overtimeHours = if (date.endsWith("2") || date.endsWith("9")) 4.0 else 0.0,
                    clockInTime = "08:30 AM",
                    clockOutTime = if (date.endsWith("2") || date.endsWith("9")) "09:30 PM" else "05:30 PM",
                    siteLocation = sites[0],
                    notes = "Main electrical conduit laying & transformer testing"
                )
            )
        }

        return list
    }

    val sampleLeaveRequests = listOf(
        LeaveRequest(
            id = 1L,
            staffId = 4L, // Suresh Verma
            leaveType = LeaveType.CASUAL_LEAVE,
            startDate = "2026-09-21",
            endDate = "2026-09-22",
            daysCount = 2,
            reason = "Attending family wedding function in ancestral village.",
            isPaid = true,
            status = LeaveStatus.PENDING,
            reviewNotes = "",
            reviewedBy = "",
            appliedAtMillis = System.currentTimeMillis() - 86400000L * 2
        ),
        LeaveRequest(
            id = 2L,
            staffId = 5L, // Manoj Gaikwad
            leaveType = LeaveType.SICK_LEAVE,
            startDate = "2026-09-18",
            endDate = "2026-09-18",
            daysCount = 1,
            reason = "Severe viral fever and scheduled doctor consultation.",
            isPaid = true,
            status = LeaveStatus.PENDING,
            reviewNotes = "",
            reviewedBy = "",
            appliedAtMillis = System.currentTimeMillis() - 43200000L
        ),
        LeaveRequest(
            id = 3L,
            staffId = 2L, // Priya Kulkarni
            leaveType = LeaveType.EARNED_LEAVE,
            startDate = "2026-09-08",
            endDate = "2026-09-10",
            daysCount = 3,
            reason = "Annual scheduled family vacation approved during project milestone gap.",
            isPaid = true,
            status = LeaveStatus.APPROVED,
            reviewNotes = "Approved as per annual entitlement. Milestone review completed.",
            reviewedBy = "Arun Mehta (Site Director)",
            appliedAtMillis = System.currentTimeMillis() - 86400000L * 15
        ),
        LeaveRequest(
            id = 4L,
            staffId = 7L, // Vikram Rathi
            leaveType = LeaveType.EMERGENCY_LEAVE,
            startDate = "2026-08-20",
            endDate = "2026-08-21",
            daysCount = 2,
            reason = "Personal family medical emergency.",
            isPaid = true,
            status = LeaveStatus.APPROVED,
            reviewNotes = "Emergency sanctioned with full pay.",
            reviewedBy = "Operations Head",
            appliedAtMillis = System.currentTimeMillis() - 86400000L * 28
        )
    )
}
