package com.example.data.downloader

import android.net.Uri
import com.example.model.DetectedMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Locale
import java.util.concurrent.TimeUnit

class MediaDetector(
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()
) {
    private val directExtensions = setOf(
        "mp4", "webm", "m4v", "mov", "mkv", "avi",
        "mp3", "m4a", "aac", "wav", "flac", "ogg", "opus",
        "pdf", "zip", "tar", "gz", "apk", "iso", "dmg"
    )

    private val mediaMimeTypes = mapOf(
        "mp4" to "video/mp4",
        "webm" to "video/webm",
        "m4v" to "video/x-m4v",
        "mov" to "video/quicktime",
        "mkv" to "video/x-matroska",
        "mp3" to "audio/mpeg",
        "m4a" to "audio/mp4",
        "aac" to "audio/aac",
        "wav" to "audio/wav",
        "ogg" to "audio/ogg",
        "pdf" to "application/pdf",
        "zip" to "application/zip",
        "apk" to "application/vnd.android.package-archive"
    )

    fun isDirectDownloadableUrl(rawUrl: String): Boolean {
        if (rawUrl.isBlank()) return false
        val uri = try {
            Uri.parse(rawUrl)
        } catch (_: Exception) {
            return false
        }
        val scheme = uri.scheme?.lowercase(Locale.ROOT)
        if (scheme != "http" && scheme != "https") return false

        val path = uri.path?.lowercase(Locale.ROOT) ?: ""
        val lastSegment = path.substringAfterLast('/', "")
        val ext = lastSegment.substringAfterLast('.', "")
        return ext in directExtensions
    }

    suspend fun inspectUrl(rawUrl: String, pageTitle: String? = null): DetectedMedia? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(rawUrl)
            val path = uri.path ?: ""
            val lastSegment = path.substringAfterLast('/', "media_file")
            var ext = lastSegment.substringAfterLast('.', "").lowercase(Locale.ROOT)
            if (ext.length !in 2..5 || ext !in directExtensions) {
                ext = "mp4"
            }

            var cleanName = lastSegment.ifBlank {
                pageTitle?.take(40)?.replace(Regex("[^a-zA-Z0-9_-]"), "_") ?: "downloaded_media"
            }
            if (!cleanName.endsWith(".$ext", ignoreCase = true)) {
                cleanName = "$cleanName.$ext"
            }

            var mimeType = mediaMimeTypes[ext] ?: "application/octet-stream"
            var sizeBytes = -1L

            // Quick HEAD probe to determine genuine size and content-type if server allows
            try {
                val headRequest = Request.Builder()
                    .url(rawUrl)
                    .head()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/128.0.0.0 Safari/537.36")
                    .build()
                val response = okHttpClient.newCall(headRequest).execute()
                if (response.isSuccessful) {
                    val contentLength = response.header("Content-Length")?.toLongOrNull()
                    if (contentLength != null && contentLength > 0) {
                        sizeBytes = contentLength
                    }
                    val headerMime = response.header("Content-Type")?.substringBefore(';')
                    if (!headerMime.isNullOrBlank()) {
                        mimeType = headerMime.trim()
                    }
                }
                response.close()
            } catch (_: Exception) {
                // Ignore network head probe failures, proceed with parsed info
            }

            val qualityLabel = when (ext) {
                "mp4", "webm", "mkv" -> "HD Video ($ext)"
                "mp3", "m4a", "aac" -> "Audio Stream ($ext)"
                "pdf" -> "Document"
                else -> "Direct File (${ext.uppercase(Locale.ROOT)})"
            }

            DetectedMedia(
                url = rawUrl,
                title = cleanName,
                mimeType = mimeType,
                sizeBytes = sizeBytes,
                quality = qualityLabel,
                extension = ext,
                timestamp = System.currentTimeMillis()
            )
        } catch (_: Exception) {
            null
        }
    }
}
