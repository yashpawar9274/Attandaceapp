package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.CompanyPayrollSummary
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object PdfPayrollExporter {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }

    private fun formatMoney(amount: Double): String {
        return try {
            "₹" + String.format(Locale.US, "%,.0f", amount)
        } catch (e: Exception) {
            "₹$amount"
        }
    }

    /**
     * Generates a corporate PDF payroll report for the Accounting Department.
     * Returns the created PDF file.
     */
    fun generatePayrollPdf(context: Context, summary: CompanyPayrollSummary): File {
        val pdfDocument = PdfDocument()

        // Page setup: Standard A4 dimensions (595 x 842 points)
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Paints
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Header Background Banner (Navy Blue)
        paint.color = Color.parseColor("#0F172A")
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 85f, paint)

        // Top gold accent line
        paint.color = Color.parseColor("#D97706")
        canvas.drawRect(0f, 85f, pageWidth.toFloat(), 89f, paint)

        // Company Name
        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("OM VALUE HOMES", 24f, 34f, paint)

        // Tagline
        paint.textSize = 8.5f
        paint.isFakeBoldText = false
        paint.color = Color.parseColor("#CBD5E1")
        canvas.drawText("Building Quality Homes • Sites & Projects Division", 24f, 48f, paint)

        // Subtitle badge on right
        paint.color = Color.parseColor("#FEF3C7")
        paint.textSize = 10f
        paint.isFakeBoldText = true
        val docTitle = "STAFF PAYROLL REPORT"
        val docTitleWidth = paint.measureText(docTitle)
        canvas.drawText(docTitle, pageWidth - 24f - docTitleWidth, 32f, paint)

        paint.color = Color.parseColor("#94A3B8")
        paint.textSize = 8f
        paint.isFakeBoldText = false
        val forAcc = "FOR: ACCOUNTING & WAGE DISBURSAL"
        val forAccWidth = paint.measureText(forAcc)
        canvas.drawText(forAcc, pageWidth - 24f - forAccWidth, 46f, paint)

        val genDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH))
        val genText = "Generated: $genDate"
        val genWidth = paint.measureText(genText)
        canvas.drawText(genText, pageWidth - 24f - genWidth, 60f, paint)

        // 2. Period & Summary Information Cards (y: 100 to 160)
        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText("Payroll Period: ${summary.monthName}", 24f, 110f, paint)

        paint.color = Color.parseColor("#64748B")
        paint.textSize = 8.5f
        paint.isFakeBoldText = false
        canvas.drawText("Ref No: OMVH-PAY-${summary.monthPrefix.replace("-", "")}-001", 24f, 122f, paint)

        // 4 Summary Metric Boxes
        val cardY = 130f
        val cardHeight = 44f
        val cardSpacing = 8f
        val cardWidth = (pageWidth - 48f - (cardSpacing * 3)) / 4f

        val metrics = listOf(
            Triple("TOTAL STAFF", "${summary.totalStaffCount}", "#3B82F6"),
            Triple("REGULAR HOURS", "${String.format(Locale.US, "%.1f", summary.totalRegularHours)} hrs", "#10B981"),
            Triple("OVERTIME HOURS", "${String.format(Locale.US, "%.1f", summary.totalOvertimeHours)} hrs", "#F59E0B"),
            Triple("TOTAL PAYROLL", formatMoney(summary.totalPayrollDisbursed), "#EF4444")
        )

        for (i in metrics.indices) {
            val cx = 24f + (i * (cardWidth + cardSpacing))
            val rect = RectF(cx, cardY, cx + cardWidth, cardY + cardHeight)

            // Card background
            paint.color = Color.parseColor("#F8FAFC")
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(rect, 4f, 4f, paint)

            // Card border
            paint.color = Color.parseColor("#E2E8F0")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRoundRect(rect, 4f, 4f, paint)

            // Card accent top line
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor(metrics[i].third)
            canvas.drawRoundRect(RectF(cx, cardY, cx + cardWidth, cardY + 3f), 2f, 2f, paint)

            // Metric label
            paint.color = Color.parseColor("#64748B")
            paint.textSize = 7f
            paint.isFakeBoldText = true
            canvas.drawText(metrics[i].first, cx + 6f, cardY + 16f, paint)

            // Metric value
            paint.color = Color.parseColor("#0F172A")
            paint.textSize = 10.5f
            paint.isFakeBoldText = true
            canvas.drawText(metrics[i].second, cx + 6f, cardY + 34f, paint)
        }

        // 3. Itemized Table of Staff Earnings
        val tableTopY = 190f
        val tableHeaderHeight = 22f

        // Table Header Background
        paint.color = Color.parseColor("#1E293B")
        paint.style = Paint.Style.FILL
        canvas.drawRect(24f, tableTopY, pageWidth - 24f, tableTopY + tableHeaderHeight, paint)

        // Column Coordinates & Headers
        // Total usable width = 595 - 48 = 547
        val colX = floatArrayOf(
            28f,   // Code (36 width)
            64f,   // Staff Name (105 width)
            169f,  // Department (80 width)
            249f,  // Rate/hr (42 width)
            291f,  // Days (32 width)
            323f,  // Reg Hr (36 width)
            359f,  // OT Hr (34 width)
            393f,  // Reg Pay (48 width)
            441f,  // OT Pay (46 width)
            487f   // Gross Pay (58 width)
        )

        val headers = arrayOf(
            "Code", "Staff Member", "Department", "Rate/h", "Days", "Reg H", "OT H", "Reg Pay", "OT Pay", "Total Pay"
        )

        paint.color = Color.WHITE
        paint.textSize = 7.5f
        paint.isFakeBoldText = true

        for (i in headers.indices) {
            canvas.drawText(headers[i], colX[i], tableTopY + 14f, paint)
        }

        // Table Rows
        var currentY = tableTopY + tableHeaderHeight
        val rowHeight = 24f

        summary.staffSummaries.forEachIndexed { index, item ->
            // Row background (alternating zebra striping)
            paint.style = Paint.Style.FILL
            paint.color = if (index % 2 == 0) Color.WHITE else Color.parseColor("#F8FAFC")
            canvas.drawRect(24f, currentY, pageWidth - 24f, currentY + rowHeight, paint)

            // Divider line
            paint.color = Color.parseColor("#E2E8F0")
            paint.strokeWidth = 0.5f
            paint.style = Paint.Style.STROKE
            canvas.drawLine(24f, currentY + rowHeight, pageWidth - 24f, currentY + rowHeight, paint)

            // Row text
            paint.style = Paint.Style.FILL
            paint.isFakeBoldText = false
            paint.textSize = 7.5f

            // Code
            paint.color = Color.parseColor("#0F172A")
            paint.isFakeBoldText = true
            canvas.drawText(item.staff.employeeCode, colX[0], currentY + 15f, paint)

            // Name
            paint.isFakeBoldText = true
            val displayName = if (item.staff.fullName.length > 18) item.staff.fullName.take(16) + ".." else item.staff.fullName
            canvas.drawText(displayName, colX[1], currentY + 15f, paint)

            // Department
            paint.isFakeBoldText = false
            paint.color = Color.parseColor("#475569")
            val displayDept = if (item.staff.department.length > 15) item.staff.department.take(13) + ".." else item.staff.department
            canvas.drawText(displayDept, colX[2], currentY + 15f, paint)

            // Rate/hr
            canvas.drawText("₹${item.staff.hourlyRate.toInt()}", colX[3], currentY + 15f, paint)

            // Days
            canvas.drawText("${item.daysPresent}", colX[4], currentY + 15f, paint)

            // Reg Hours
            canvas.drawText(String.format(Locale.US, "%.1f", item.totalRegularHours), colX[5], currentY + 15f, paint)

            // OT Hours (highlighted if > 0)
            if (item.totalOvertimeHours > 0) {
                paint.color = Color.parseColor("#D97706")
                paint.isFakeBoldText = true
            } else {
                paint.color = Color.parseColor("#94A3B8")
                paint.isFakeBoldText = false
            }
            canvas.drawText(String.format(Locale.US, "%.1f", item.totalOvertimeHours), colX[6], currentY + 15f, paint)

            // Reg Pay
            paint.color = Color.parseColor("#334155")
            paint.isFakeBoldText = false
            canvas.drawText(formatMoney(item.regularPay), colX[7], currentY + 15f, paint)

            // OT Pay
            if (item.overtimePay > 0) {
                paint.color = Color.parseColor("#D97706")
                paint.isFakeBoldText = true
            } else {
                paint.color = Color.parseColor("#94A3B8")
                paint.isFakeBoldText = false
            }
            canvas.drawText(formatMoney(item.overtimePay), colX[8], currentY + 15f, paint)

            // Gross Pay (bold)
            paint.color = Color.parseColor("#0F172A")
            paint.isFakeBoldText = true
            canvas.drawText(formatMoney(item.grossPay), colX[9], currentY + 15f, paint)

            currentY += rowHeight
        }

        // Totals Row
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#EEF2F6")
        canvas.drawRect(24f, currentY, pageWidth - 24f, currentY + rowHeight + 2f, paint)

        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 8f
        paint.isFakeBoldText = true
        canvas.drawText("GRAND TOTALS:", colX[0], currentY + 16f, paint)
        canvas.drawText(String.format(Locale.US, "%.1f", summary.totalRegularHours), colX[5], currentY + 16f, paint)
        canvas.drawText(String.format(Locale.US, "%.1f", summary.totalOvertimeHours), colX[6], currentY + 16f, paint)
        canvas.drawText(formatMoney(summary.totalRegularPayout), colX[7], currentY + 16f, paint)
        canvas.drawText(formatMoney(summary.totalOvertimePayout), colX[8], currentY + 16f, paint)
        paint.color = Color.parseColor("#B45309")
        canvas.drawText(formatMoney(summary.totalPayrollDisbursed), colX[9], currentY + 16f, paint)

        currentY += rowHeight + 15f

        // 4. Payment Disbursal & Bank Accounts Reference Table
        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 9.5f
        paint.isFakeBoldText = true
        canvas.drawText("Disbursement Routing & Bank Account Details", 24f, currentY, paint)

        currentY += 10f
        paint.color = Color.parseColor("#64748B")
        paint.textSize = 7.5f
        paint.isFakeBoldText = false

        summary.staffSummaries.take(5).forEach { item ->
            currentY += 13f
            val info = "${item.staff.employeeCode} - ${item.staff.fullName}: ${item.staff.paymentType} (${item.staff.accountDetails.ifBlank { "NEFT / Cheque" }}) -> Disburse: ${formatMoney(item.grossPay)}"
            canvas.drawText(info, 24f, currentY, paint)
        }

        // 5. Approvals and Signatures Section
        val signY = pageHeight - 115f

        paint.color = Color.parseColor("#E2E8F0")
        paint.strokeWidth = 1f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(24f, signY, pageWidth - 24f, signY, paint)

        val signBoxWidth = (pageWidth - 48f) / 3f
        val signPositions = listOf("Prepared By: Site HR Admin", "Reviewed By: Project Manager", "Approved By: Accounts & Finance Head")

        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#334155")
        paint.textSize = 8f
        paint.isFakeBoldText = true

        for (i in signPositions.indices) {
            val sx = 24f + (i * signBoxWidth)
            canvas.drawText(signPositions[i], sx, signY + 20f, paint)

            // Signature line
            paint.color = Color.parseColor("#CBD5E1")
            paint.strokeWidth = 0.8f
            paint.style = Paint.Style.STROKE
            canvas.drawLine(sx, signY + 60f, sx + signBoxWidth - 20f, signY + 60f, paint)

            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#94A3B8")
            paint.textSize = 7f
            paint.isFakeBoldText = false
            canvas.drawText("Authorized Signature & Date", sx, signY + 72f, paint)
        }

        // 6. Corporate Footer
        val footerY = pageHeight - 20f
        paint.color = Color.parseColor("#64748B")
        paint.textSize = 7f
        paint.isFakeBoldText = false
        val footerText = "OM Value Homes Pvt. Ltd. • Confidential Document for Internal Accounting Use Only • Page 1 of 1"
        val fw = paint.measureText(footerText)
        canvas.drawText(footerText, (pageWidth - fw) / 2f, footerY, paint)

        // Finish page
        pdfDocument.finishPage(page)

        // Save PDF to cache or external files directory
        val dir = File(context.cacheDir, "payroll")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val fileName = "OMVH_Payroll_${summary.monthPrefix}_${System.currentTimeMillis()}.pdf"
        val file = File(dir, fileName)

        val fos = FileOutputStream(file)
        pdfDocument.writeTo(fos)
        fos.flush()
        fos.close()
        pdfDocument.close()

        return file
    }

    /**
     * Creates an Intent to view the PDF file with an external PDF viewer.
     */
    fun getViewPdfIntent(context: Context, file: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * Creates an Intent to share the PDF file with the Accounting Department (Email, WhatsApp, Drive, etc.).
     */
    fun getSharePdfIntent(context: Context, file: File, monthName: String): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "OM Value Homes - Staff Payroll Statement ($monthName)")
            putExtra(
                Intent.EXTRA_TEXT,
                "Please find attached the official staff attendance & payroll report for OM Value Homes ($monthName) for the accounting department."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
