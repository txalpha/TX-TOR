package com.example.data.downloader

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.example.data.local.DownloadDao
import com.example.data.local.DownloadTaskEntity
import com.example.model.DownloadStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class DownloadEngine(
    private val context: Context,
    private val downloadDao: DownloadDao,
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val activeJobs = ConcurrentHashMap<String, Job>()

    fun getDownloadDir(): File {
        val external = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (external != null && external.exists()) {
            return external
        }
        val internal = File(context.filesDir, "downloads")
        if (!internal.exists()) internal.mkdirs()
        return internal
    }

    fun getAvailableStorageBytes(): Long {
        return try {
            val stat = StatFs(getDownloadDir().path)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (_: Exception) {
            -1L
        }
    }

    fun startOrEnqueueDownload(
        url: String,
        fileName: String,
        mimeType: String,
        totalBytes: Long = -1L
    ): String {
        val taskId = UUID.randomUUID().toString()
        val sanitizedName = sanitizeFileName(fileName)
        val destFile = File(getDownloadDir(), sanitizedName)

        val task = DownloadTaskEntity(
            taskId = taskId,
            fileName = sanitizedName,
            url = url,
            destinationPath = destFile.absolutePath,
            mimeType = mimeType,
            totalBytes = totalBytes,
            downloadedBytes = 0L,
            status = DownloadStatus.QUEUED.name
        )

        engineScope.launch {
            downloadDao.insertTask(task)
            executeDownload(taskId)
        }

        return taskId
    }

    fun pauseDownload(taskId: String) {
        val job = activeJobs.remove(taskId)
        job?.cancel()
        engineScope.launch {
            val current = downloadDao.getTaskById(taskId)
            if (current != null && current.status == DownloadStatus.DOWNLOADING.name) {
                downloadDao.updateTask(
                    current.copy(
                        status = DownloadStatus.PAUSED.name,
                        speedBps = 0L
                    )
                )
            }
        }
    }

    fun resumeDownload(taskId: String) {
        engineScope.launch {
            val task = downloadDao.getTaskById(taskId)
            if (task != null && (task.status == DownloadStatus.PAUSED.name || task.status == DownloadStatus.FAILED.name)) {
                downloadDao.updateTask(task.copy(status = DownloadStatus.QUEUED.name, errorReason = null))
                executeDownload(taskId)
            }
        }
    }

    fun cancelDownload(taskId: String) {
        val job = activeJobs.remove(taskId)
        job?.cancel()
        engineScope.launch {
            val task = downloadDao.getTaskById(taskId)
            if (task != null) {
                // Delete partial downloaded file
                val file = File(task.destinationPath)
                if (file.exists()) {
                    file.delete()
                }
                downloadDao.updateTask(task.copy(status = DownloadStatus.CANCELLED.name, speedBps = 0L))
            }
        }
    }

    fun removeTaskRecord(taskId: String, deleteFile: Boolean = false) {
        activeJobs.remove(taskId)?.cancel()
        engineScope.launch {
            val task = downloadDao.getTaskById(taskId)
            if (task != null) {
                if (deleteFile) {
                    val file = File(task.destinationPath)
                    if (file.exists()) file.delete()
                }
                downloadDao.deleteTask(task)
            }
        }
    }

    private fun executeDownload(taskId: String) {
        if (activeJobs.containsKey(taskId)) return

        val job = engineScope.launch {
            var task = downloadDao.getTaskById(taskId) ?: return@launch
            val destFile = File(task.destinationPath)

            // Storage space check
            val available = getAvailableStorageBytes()
            if (task.totalBytes > 0 && available > 0 && available < task.totalBytes) {
                downloadDao.updateTask(
                    task.copy(
                        status = DownloadStatus.FAILED.name,
                        errorReason = "Insufficient device storage space"
                    )
                )
                return@launch
            }

            var startBytes = 0L
            if (destFile.exists()) {
                startBytes = destFile.length()
            }

            // Update to DOWNLOADING
            task = task.copy(status = DownloadStatus.DOWNLOADING.name, errorReason = null)
            downloadDao.updateTask(task)

            val requestBuilder = Request.Builder()
                .url(task.url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/128.0.0.0 Safari/537.36")

            if (startBytes > 0) {
                requestBuilder.header("Range", "bytes=$startBytes-")
            }

            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null

            try {
                val response = okHttpClient.newCall(requestBuilder.build()).execute()
                val isPartial = response.code == 206
                val isOk = response.isSuccessful

                if (!isOk) {
                    throw IllegalStateException("HTTP ${response.code}: ${response.message}")
                }

                val body = response.body ?: throw IllegalStateException("Empty response body")
                val responseLength = body.contentLength()
                val totalBytes = if (isPartial && startBytes > 0) {
                    startBytes + responseLength
                } else if (responseLength > 0) {
                    responseLength
                } else {
                    task.totalBytes
                }

                // If server ignored range header, overwrite file from 0
                val append = isPartial && startBytes > 0
                var currentBytes = if (append) startBytes else 0L

                inputStream = body.byteStream()
                outputStream = FileOutputStream(destFile, append)

                val buffer = ByteArray(32 * 1024)
                var bytesRead: Int
                var lastTime = System.currentTimeMillis()
                var bytesSinceLastUpdate = 0L

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    if (!isActive) throw CancellationException("Download cancelled or paused")
                    outputStream.write(buffer, 0, bytesRead)
                    currentBytes += bytesRead
                    bytesSinceLastUpdate += bytesRead

                    val now = System.currentTimeMillis()
                    val elapsed = now - lastTime
                    if (elapsed >= 800) {
                        val speed = (bytesSinceLastUpdate * 1000L) / elapsed
                        downloadDao.updateTask(
                            task.copy(
                                downloadedBytes = currentBytes,
                                totalBytes = totalBytes,
                                speedBps = speed
                            )
                        )
                        lastTime = now
                        bytesSinceLastUpdate = 0L
                    }
                }

                outputStream.flush()
                // Mark COMPLETED
                downloadDao.updateTask(
                    task.copy(
                        downloadedBytes = currentBytes,
                        totalBytes = currentBytes,
                        status = DownloadStatus.COMPLETED.name,
                        speedBps = 0L,
                        completedAt = System.currentTimeMillis()
                    )
                )

            } catch (e: CancellationException) {
                // Handled gracefully (Paused or Cancelled)
            } catch (e: Exception) {
                val current = downloadDao.getTaskById(taskId)
                if (current != null) {
                    downloadDao.updateTask(
                        current.copy(
                            status = DownloadStatus.FAILED.name,
                            errorReason = e.message ?: "Network error",
                            speedBps = 0L
                        )
                    )
                }
            } finally {
                try { inputStream?.close() } catch (_: Exception) {}
                try { outputStream?.close() } catch (_: Exception) {}
                activeJobs.remove(taskId)
            }
        }

        activeJobs[taskId] = job
    }

    private fun sanitizeFileName(name: String): String {
        val sanitized = name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim()
        return if (sanitized.isBlank()) "file_${System.currentTimeMillis()}" else sanitized
    }
}
