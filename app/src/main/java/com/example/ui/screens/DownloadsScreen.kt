package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.local.DownloadTaskEntity
import com.example.model.DownloadStatus
import com.example.ui.components.CyberBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.TxTorAmber
import com.example.ui.theme.TxTorBackground
import com.example.ui.theme.TxTorCyan
import com.example.ui.theme.TxTorGlassBackground
import com.example.ui.theme.TxTorGlassBorder
import com.example.ui.theme.TxTorGreen
import com.example.ui.theme.TxTorPrimary
import com.example.ui.theme.TxTorRed
import com.example.ui.theme.TxTorSecondary
import com.example.ui.theme.TxTorSurfaceElevated
import com.example.ui.theme.TxTorTextMuted
import com.example.ui.theme.TxTorTextPrimary
import com.example.ui.theme.TxTorTextSecondary
import java.io.File
import java.util.Locale

@Composable
fun DownloadsScreen(
    downloads: List<DownloadTaskEntity>,
    availableStorageBytes: Long,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onCancel: (String) -> Unit,
    onDelete: (taskId: String, deleteFile: Boolean) -> Unit,
    onClearCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("ALL") }
    var taskPendingDelete by remember { mutableStateOf<DownloadTaskEntity?>(null) }

    val filteredList = when (selectedFilter) {
        "ACTIVE" -> downloads.filter { it.status == DownloadStatus.DOWNLOADING.name || it.status == DownloadStatus.QUEUED.name }
        "COMPLETED" -> downloads.filter { it.status == DownloadStatus.COMPLETED.name }
        "PAUSED" -> downloads.filter { it.status == DownloadStatus.PAUSED.name }
        "FAILED" -> downloads.filter { it.status == DownloadStatus.FAILED.name }
        else -> downloads
    }

    val availableStorageFormatted = if (availableStorageBytes > 0) {
        val gb = availableStorageBytes / (1024.0 * 1024.0 * 1024.0)
        "%.1f GB Free".format(Locale.ROOT, gb)
    } else {
        "Storage Available"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TxTorBackground)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = TxTorSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download Manager",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TxTorTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = TxTorCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = availableStorageFormatted,
                        fontSize = 11.sp,
                        color = TxTorCyan
                    )
                }
            }

            if (downloads.any { it.status == DownloadStatus.COMPLETED.name }) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TxTorGlassBackground)
                        .border(1.dp, TxTorGlassBorder, RoundedCornerShape(8.dp))
                        .clickable(onClick = onClearCompleted)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Clear Done",
                        color = TxTorTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Chips Row
        val filters = listOf("ALL", "ACTIVE", "COMPLETED", "PAUSED", "FAILED")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) TxTorPrimary else TxTorGlassBackground)
                        .border(
                            1.dp,
                            if (isSelected) TxTorSecondary else TxTorGlassBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else TxTorTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = TxTorTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No Downloads Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TxTorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Downloads and sniffed media will appear here in real time.",
                        fontSize = 12.sp,
                        color = TxTorTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.taskId }) { task ->
                    DownloadCard(
                        task = task,
                        context = context,
                        onPause = { onPause(task.taskId) },
                        onResume = { onResume(task.taskId) },
                        onCancel = { onCancel(task.taskId) },
                        onDeleteRequested = { taskPendingDelete = task }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    taskPendingDelete?.let { task ->
        var deleteFromDisk by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { taskPendingDelete = null },
            title = { Text("Delete Download?", color = TxTorTextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Remove '${task.fileName}' from download records?",
                        color = TxTorTextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { deleteFromDisk = !deleteFromDisk }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (deleteFromDisk) TxTorRed else Color.Transparent)
                                .border(1.dp, TxTorRed, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (deleteFromDisk) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Also delete file from storage",
                            fontSize = 13.sp,
                            color = TxTorTextPrimary
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(task.taskId, deleteFromDisk)
                        taskPendingDelete = null
                    }
                ) {
                    Text("Delete", color = TxTorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskPendingDelete = null }) {
                    Text("Cancel", color = TxTorTextMuted)
                }
            },
            containerColor = TxTorSurfaceElevated
        )
    }
}

@Composable
private fun DownloadCard(
    task: DownloadTaskEntity,
    context: Context,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onDeleteRequested: () -> Unit
) {
    val statusEnum = try {
        DownloadStatus.valueOf(task.status)
    } catch (_: Exception) {
        DownloadStatus.QUEUED
    }

    val statusColor = when (statusEnum) {
        DownloadStatus.COMPLETED -> TxTorGreen
        DownloadStatus.DOWNLOADING -> TxTorSecondary
        DownloadStatus.PAUSED -> TxTorAmber
        DownloadStatus.FAILED -> TxTorRed
        DownloadStatus.CANCELLED -> TxTorTextMuted
        DownloadStatus.QUEUED -> TxTorCyan
    }

    val progressPercent = if (task.totalBytes > 0) {
        ((task.downloadedBytes.toDouble() / task.totalBytes.toDouble()) * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    val speedFormatted = if (task.speedBps > 0) {
        val mbps = task.speedBps / (1024.0 * 1024.0)
        if (mbps >= 1.0) "%.1f MB/s".format(Locale.ROOT, mbps) else "${task.speedBps / 1024} KB/s"
    } else ""

    val etaFormatted = if (task.speedBps > 0 && task.totalBytes > task.downloadedBytes) {
        val remainingBytes = task.totalBytes - task.downloadedBytes
        val remainingSec = (remainingBytes / task.speedBps).toInt()
        when {
            remainingSec < 60 -> "${remainingSec}s left"
            remainingSec < 3600 -> "${remainingSec / 60}m left"
            else -> "${remainingSec / 3600}h left"
        }
    } else ""

    val sizeFormatted = if (task.totalBytes > 0) {
        val curMb = task.downloadedBytes / (1024.0 * 1024.0)
        val totMb = task.totalBytes / (1024.0 * 1024.0)
        "%.1f / %.1f MB".format(Locale.ROOT, curMb, totMb)
    } else {
        val curMb = task.downloadedBytes / (1024.0 * 1024.0)
        "%.1f MB".format(Locale.ROOT, curMb)
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Name & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.fileName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TxTorTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                CyberBadge(label = task.status, badgeColor = statusColor)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            if (statusEnum == DownloadStatus.DOWNLOADING || statusEnum == DownloadStatus.PAUSED || statusEnum == DownloadStatus.QUEUED) {
                LinearProgressIndicator(
                    progress = { progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (statusEnum == DownloadStatus.PAUSED) TxTorAmber else TxTorSecondary,
                    trackColor = TxTorGlassBackground
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$sizeFormatted ($progressPercent%)",
                        fontSize = 11.sp,
                        color = TxTorTextMuted
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (speedFormatted.isNotEmpty()) {
                            Text(
                                text = speedFormatted,
                                fontSize = 11.sp,
                                color = TxTorCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (etaFormatted.isNotEmpty()) {
                            Text(
                                text = etaFormatted,
                                fontSize = 11.sp,
                                color = TxTorSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else if (statusEnum == DownloadStatus.COMPLETED) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TxTorGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Saved in Downloads folder • $sizeFormatted",
                        fontSize = 11.sp,
                        color = TxTorTextMuted
                    )
                }
            } else if (statusEnum == DownloadStatus.FAILED) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = TxTorRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = task.errorReason ?: "Download interrupted",
                        fontSize = 11.sp,
                        color = TxTorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (statusEnum) {
                    DownloadStatus.DOWNLOADING -> {
                        IconButton(onClick = onPause, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pause",
                                tint = TxTorAmber
                            )
                        }
                        IconButton(onClick = onCancel, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = TxTorRed
                            )
                        }
                    }
                    DownloadStatus.PAUSED -> {
                        IconButton(onClick = onResume, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume",
                                tint = TxTorGreen
                            )
                        }
                        IconButton(onClick = onDeleteRequested, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = TxTorTextMuted
                            )
                        }
                    }
                    DownloadStatus.FAILED -> {
                        IconButton(onClick = onResume, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = TxTorCyan
                            )
                        }
                        IconButton(onClick = onDeleteRequested, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = TxTorTextMuted
                            )
                        }
                    }
                    DownloadStatus.COMPLETED -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TxTorPrimary.copy(alpha = 0.2f))
                                .border(1.dp, TxTorPrimary, RoundedCornerShape(8.dp))
                                .clickable {
                                    openDownloadedFile(context, task.destinationPath, task.mimeType)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = TxTorPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Open File",
                                    color = TxTorTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = onDeleteRequested, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = TxTorTextMuted
                            )
                        }
                    }
                    else -> {
                        IconButton(onClick = onDeleteRequested, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = TxTorTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun openDownloadedFile(context: Context, filePath: String, mimeType: String) {
    try {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(context, "File does not exist on disk", Toast.LENGTH_SHORT).show()
            return
        }

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "com.aistudio.txtor.browser.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType.ifBlank { "*/*" })
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Open with"))
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
