package com.example.model

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

enum class SearchEngine(val displayName: String, val searchUrlPrefix: String, val iconResName: String) {
    GOOGLE("Google", "https://www.google.com/search?q=", "ic_google"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "ic_ddg"),
    BING("Bing", "https://www.bing.com/search?q=", "ic_bing"),
    BRAVE("Brave Search", "https://search.brave.com/search?q=", "ic_brave"),
    STARTPAGE("Startpage", "https://www.startpage.com/sp/search?query=", "ic_startpage")
}

data class DetectedMedia(
    val url: String,
    val title: String,
    val mimeType: String,
    val sizeBytes: Long = -1L,
    val quality: String = "Direct Stream",
    val extension: String = "mp4",
    val timestamp: Long = System.currentTimeMillis()
)

data class BrowserTabState(
    val id: String,
    val url: String = "about:blank",
    val title: String = "New Tab",
    val isDesktopMode: Boolean = false,
    val isIncognito: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val detectedMedia: List<DetectedMedia> = emptyList()
)

enum class BrowserNavScreen {
    BROWSER,
    TABS,
    DOWNLOADS,
    BOOKMARKS,
    HISTORY,
    SETTINGS
}
