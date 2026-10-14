package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.downloader.DownloadEngine
import com.example.data.local.BookmarkEntity
import com.example.data.local.BrowserDatabase
import com.example.data.local.BrowserPreferences
import com.example.data.local.HistoryEntity
import com.example.model.BrowserNavScreen
import com.example.model.BrowserTabState
import com.example.model.DetectedMedia
import com.example.model.SearchEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BrowserDatabase.getInstance(application)
    private val bookmarkDao = db.bookmarkDao()
    private val historyDao = db.historyDao()
    private val downloadDao = db.downloadDao()

    val preferences = BrowserPreferences(application)
    val downloadEngine = DownloadEngine(application, downloadDao)

    val bookmarks: StateFlow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val history: StateFlow<List<HistoryEntity>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val downloads = downloadDao.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _tabs = MutableStateFlow<List<BrowserTabState>>(emptyList())
    val tabs: StateFlow<List<BrowserTabState>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String>("")
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    private val _currentScreen = MutableStateFlow(BrowserNavScreen.BROWSER)
    val currentScreen: StateFlow<BrowserNavScreen> = _currentScreen.asStateFlow()

    private val _isFindInPageVisible = MutableStateFlow(false)
    val isFindInPageVisible: StateFlow<Boolean> = _isFindInPageVisible.asStateFlow()

    init {
        // Initialize default tab
        createTab(url = "about:home", isIncognito = false)
    }

    fun navigateScreen(screen: BrowserNavScreen) {
        _currentScreen.value = screen
    }

    fun setFindInPageVisible(visible: Boolean) {
        _isFindInPageVisible.value = visible
    }

    fun createTab(url: String = "about:home", isIncognito: Boolean = false): String {
        val tabId = UUID.randomUUID().toString()
        val isDesktop = preferences.desktopModeDefault.value
        val newTab = BrowserTabState(
            id = tabId,
            url = url,
            title = if (url == "about:home") "TX TOR Home" else "New Tab",
            isDesktopMode = isDesktop,
            isIncognito = isIncognito
        )
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = tabId
        _currentScreen.value = BrowserNavScreen.BROWSER
        return tabId
    }

    fun switchTab(tabId: String) {
        if (_tabs.value.any { it.id == tabId }) {
            _activeTabId.value = tabId
            _currentScreen.value = BrowserNavScreen.BROWSER
        }
    }

    fun closeTab(tabId: String) {
        val currentList = _tabs.value
        val remaining = currentList.filterNot { it.id == tabId }
        _tabs.value = remaining

        if (_activeTabId.value == tabId) {
            if (remaining.isNotEmpty()) {
                _activeTabId.value = remaining.last().id
            } else {
                createTab(url = "about:home")
            }
        }
    }

    fun closeAllTabs() {
        _tabs.value = emptyList()
        createTab(url = "about:home")
    }

    fun updateTabUrl(tabId: String, newUrl: String) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                tab.copy(url = newUrl)
            } else tab
        }

        // Record in history if not incognito and not internal about: page
        val active = getActiveTab()
        if (active != null && !active.isIncognito && !newUrl.startsWith("about:") && newUrl.isNotBlank()) {
            recordHistory(newUrl, active.title)
        }
    }

    fun updateTabTitle(tabId: String, title: String) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) tab.copy(title = title) else tab
        }
        val active = getActiveTab()
        if (active != null && !active.isIncognito && active.id == tabId && !active.url.startsWith("about:")) {
            recordHistory(active.url, title)
        }
    }

    fun updateTabProgress(tabId: String, progress: Int, isLoading: Boolean) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) tab.copy(progress = progress, isLoading = isLoading) else tab
        }
    }

    fun updateTabNavState(tabId: String, canGoBack: Boolean, canGoForward: Boolean) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) tab.copy(canGoBack = canGoBack, canGoForward = canGoForward) else tab
        }
    }

    fun toggleDesktopMode(tabId: String) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) tab.copy(isDesktopMode = !tab.isDesktopMode) else tab
        }
    }

    fun addDetectedMedia(tabId: String, media: DetectedMedia) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                val exists = tab.detectedMedia.any { it.url == media.url }
                if (!exists) {
                    tab.copy(detectedMedia = tab.detectedMedia + media)
                } else tab
            } else tab
        }
    }

    fun clearDetectedMedia(tabId: String) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) tab.copy(detectedMedia = emptyList()) else tab
        }
    }

    fun resolveNavigationInput(rawInput: String): String {
        val input = rawInput.trim()
        if (input.isBlank()) return "about:home"
        if (input == "about:home" || input == "about:blank") return input

        // URL pattern recognition
        val isExplicitUrl = input.startsWith("http://", ignoreCase = true) || input.startsWith("https://", ignoreCase = true)
        if (isExplicitUrl) return input

        val hasTld = input.contains(".") && !input.contains(" ") && input.length > 3
        if (hasTld) {
            return "https://$input"
        }

        // Otherwise perform web search using configured search engine
        val searchPrefix = preferences.searchEngine.value.searchUrlPrefix
        val encoded = try {
            URLEncoder.encode(input, StandardCharsets.UTF_8.name())
        } catch (_: Exception) {
            input
        }
        return "$searchPrefix$encoded"
    }

    fun getActiveTab(): BrowserTabState? {
        val currentId = _activeTabId.value
        return _tabs.value.firstOrNull { it.id == currentId } ?: _tabs.value.firstOrNull()
    }

    fun bookmarkCurrentPage() {
        val active = getActiveTab() ?: return
        if (active.url.startsWith("about:")) return
        viewModelScope.launch {
            val title = active.title.ifBlank { active.url }
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    title = title,
                    url = active.url,
                    folder = "Favorites"
                )
            )
        }
    }

    fun addBookmark(title: String, url: String, folder: String) {
        viewModelScope.launch {
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    title = title,
                    url = url,
                    folder = folder
                )
            )
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            bookmarkDao.deleteBookmark(bookmark)
        }
    }

    private fun recordHistory(url: String, title: String) {
        viewModelScope.launch {
            val existing = historyDao.getHistoryByUrl(url)
            if (existing != null) {
                historyDao.updateHistory(
                    existing.copy(
                        title = title.ifBlank { existing.title },
                        timestamp = System.currentTimeMillis(),
                        visitCount = existing.visitCount + 1
                    )
                )
            } else {
                historyDao.insertHistory(
                    HistoryEntity(
                        title = title.ifBlank { url },
                        url = url,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun deleteHistoryItem(item: HistoryEntity) {
        viewModelScope.launch {
            historyDao.deleteHistory(item)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.clearAllHistory()
        }
    }

    fun clearCompletedDownloads() {
        viewModelScope.launch {
            downloadDao.clearCompleted()
        }
    }
}
