package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.downloader.MediaDetector
import com.example.model.BrowserTabState
import com.example.model.DownloadStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context validates app name is TX TOR`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TX TOR", appName)
    }

    @Test
    fun `media detector correctly identifies direct media urls`() {
        val detector = MediaDetector()

        assertTrue(detector.isDirectDownloadableUrl("https://example.com/videos/stream_sample.mp4"))
        assertTrue(detector.isDirectDownloadableUrl("http://media.test.org/audio/sample_podcast.mp3"))
        assertTrue(detector.isDirectDownloadableUrl("https://cdn.site.com/archive.zip"))
        assertTrue(detector.isDirectDownloadableUrl("https://download.site.com/app.apk"))

        assertFalse(detector.isDirectDownloadableUrl("https://example.com/"))
        assertFalse(detector.isDirectDownloadableUrl("https://example.com/search?q=test"))
        assertFalse(detector.isDirectDownloadableUrl("javascript:void(0)"))
    }

    @Test
    fun `browser tab state maintains independent navigation properties`() {
        val tab = BrowserTabState(
            id = "test-tab-1",
            url = "https://duckduckgo.com",
            title = "DuckDuckGo",
            isDesktopMode = true,
            isIncognito = false
        )

        assertEquals("test-tab-1", tab.id)
        assertEquals("https://duckduckgo.com", tab.url)
        assertTrue(tab.isDesktopMode)
        assertFalse(tab.isIncognito)
        assertFalse(tab.canGoBack)
    }

    @Test
    fun `download status enums are valid`() {
        val status = DownloadStatus.valueOf("DOWNLOADING")
        assertEquals(DownloadStatus.DOWNLOADING, status)
        assertEquals("COMPLETED", DownloadStatus.COMPLETED.name)
        assertEquals("PAUSED", DownloadStatus.PAUSED.name)
    }
}
