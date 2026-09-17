package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OmEmerald
import com.example.ui.theme.OmGold
import com.example.ui.theme.OmNavyDark
import com.example.ui.theme.OmRed
import com.example.ui.viewmodel.PdfExportStatus
import com.example.util.PdfPayrollExporter
import java.io.File

@Composable
fun PdfExportDialog(
    status: PdfExportStatus,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current

    when (status) {
        is PdfExportStatus.Idle -> { /* do nothing */ }

        is PdfExportStatus.Generating -> {
            AlertDialog(
                onDismissRequest = {},
                modifier = Modifier.testTag("pdf_generating_dialog"),
                title = { Text("Generating Accounting Report", fontWeight = FontWeight.Bold) },
                text = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = OmGold)
                        Column {
                            Text("Compiling staff hours & overtime...", fontSize = 13.sp)
                            Text("Rendering formal PDF for Accounting Dept.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                confirmButton = {}
            )
        }

        is PdfExportStatus.Success -> {
            val file = status.file
            val monthName = status.monthName

            AlertDialog(
                onDismissRequest = onDismiss,
                modifier = Modifier.testTag("pdf_success_dialog"),
                icon = {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(OmEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = OmEmerald,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                title = {
                    Text(
                        "Payroll PDF Exported!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Official attendance and earnings statement ready for OM Value Homes Accounting Department ($monthName).",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = file.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Size: ${file.length() / 1024} KB • Verified A4 Statement",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Buttons for viewing and sharing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    try {
                                        val intent = PdfPayrollExporter.getViewPdfIntent(context, file)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No PDF viewer app found on device.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OmNavyDark),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("view_pdf_btn")
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open PDF", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    try {
                                        val intent = PdfPayrollExporter.getSharePdfIntent(context, file, monthName)
                                        context.startActivity(android.content.Intent.createChooser(intent, "Share Payroll PDF with Accounting"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Unable to share file.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OmGold),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("share_pdf_btn")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", fontSize = 12.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Done")
                    }
                }
            )
        }

        is PdfExportStatus.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                icon = {
                    Icon(Icons.Default.Error, contentDescription = null, tint = OmRed, modifier = Modifier.size(36.dp))
                },
                title = { Text("PDF Export Error", fontWeight = FontWeight.Bold) },
                text = { Text(status.message, fontSize = 13.sp) },
                confirmButton = {
                    Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = OmGold)) {
                        Text("Retry")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
