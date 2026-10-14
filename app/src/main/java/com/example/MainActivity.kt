package com.example

import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.BrowserNavScreen
import com.example.model.DownloadStatus
import com.example.ui.components.BrowserBottomBar
import com.example.ui.components.BrowserTopBar
import com.example.ui.components.MediaDownloadSheet
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TabSwitcherScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TxTorBackground
import com.example.viewmodel.BrowserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainBrowserApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBrowserApp(
    viewModel: BrowserViewModel = viewModel()
) {
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isFindInPageVisible by viewModel.isFindInPageVisible.collectAsState()

    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()
    val downloads by viewModel.downloads.collectAsState()

    val searchEngine by viewModel.preferences.searchEngine.collectAsState()
    val desktopModeDefault by viewModel.preferences.desktopModeDefault.collectAsState()
    val trackerBlockerEnabled by viewModel.preferences.trackerBlockerEnabled.collectAsState()
    val clearOnExit by viewModel.preferences.clearOnExit.collectAsState()
    val amoledTheme by viewModel.preferences.amoledTheme.collectAsState()

    var activeWebView by remember { mutableStateOf<WebView?>(null) }
    var showMediaSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val activeTab = viewModel.getActiveTab() ?: return
    val activeDownloadsCount = downloads.count { it.status == DownloadStatus.DOWNLOADING.name }

    // Intercept Back Press for sub-screens and WebView history
    BackHandler(enabled = true) {
        if (currentScreen != BrowserNavScreen.BROWSER) {
            viewModel.navigateScreen(BrowserNavScreen.BROWSER)
        } else if (activeWebView?.canGoBack() == true) {
            activeWebView?.goBack()
        } else if (activeTab.url != "about:home") {
            viewModel.updateTabUrl(activeTab.id, "about:home")
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(TxTorBackground),
        containerColor = TxTorBackground,
        topBar = {
            if (currentScreen == BrowserNavScreen.BROWSER) {
                BrowserTopBar(
                    tabState = activeTab,
                    onNavigate = { input ->
                        val targetUrl = viewModel.resolveNavigationInput(input)
                        viewModel.updateTabUrl(activeTab.id, targetUrl)
                    },
                    onReload = { activeWebView?.reload() },
                    onStop = { activeWebView?.stopLoading() },
                    onToggleDesktopMode = { viewModel.toggleDesktopMode(activeTab.id) },
                    onOpenMediaSheet = { showMediaSheet = true },
                    onBookmarkCurrentPage = { viewModel.bookmarkCurrentPage() },
                    onFindInPage = { viewModel.setFindInPageVisible(true) },
                    onOpenSettings = { viewModel.navigateScreen(BrowserNavScreen.SETTINGS) },
                    onOpenHistory = { viewModel.navigateScreen(BrowserNavScreen.HISTORY) },
                    onOpenBookmarks = { viewModel.navigateScreen(BrowserNavScreen.BOOKMARKS) }
                )
            }
        },
        bottomBar = {
            BrowserBottomBar(
                canGoBack = activeTab.canGoBack || activeWebView?.canGoBack() == true,
                canGoForward = activeTab.canGoForward || activeWebView?.canGoForward() == true,
                openTabsCount = tabs.size,
                activeDownloadsCount = activeDownloadsCount,
                onBackClick = { activeWebView?.goBack() },
                onForwardClick = { activeWebView?.goForward() },
                onHomeClick = {
                    if (currentScreen != BrowserNavScreen.BROWSER) {
                        viewModel.navigateScreen(BrowserNavScreen.BROWSER)
                    }
                    viewModel.updateTabUrl(activeTab.id, "about:home")
                },
                onTabsClick = {
                    if (currentScreen == BrowserNavScreen.TABS) {
                        viewModel.navigateScreen(BrowserNavScreen.BROWSER)
                    } else {
                        viewModel.navigateScreen(BrowserNavScreen.TABS)
                    }
                },
                onDownloadsClick = {
                    if (currentScreen == BrowserNavScreen.DOWNLOADS) {
                        viewModel.navigateScreen(BrowserNavScreen.BROWSER)
                    } else {
                        viewModel.navigateScreen(BrowserNavScreen.DOWNLOADS)
                    }
                },
                onSettingsClick = {
                    if (currentScreen == BrowserNavScreen.SETTINGS) {
                        viewModel.navigateScreen(BrowserNavScreen.BROWSER)
                    } else {
                        viewModel.navigateScreen(BrowserNavScreen.SETTINGS)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TxTorBackground)
        ) {
            when (currentScreen) {
                BrowserNavScreen.BROWSER -> {
                    BrowserScreen(
                        tabState = activeTab,
                        isFindInPageVisible = isFindInPageVisible,
                        onCloseFindInPage = { viewModel.setFindInPageVisible(false) },
                        onPageTitleChanged = { title -> viewModel.updateTabTitle(activeTab.id, title) },
                        onPageProgressChanged = { progress, isLoading -> viewModel.updateTabProgress(activeTab.id, progress, isLoading) },
                        onUrlChanged = { url -> viewModel.updateTabUrl(activeTab.id, url) },
                        onNavStateChanged = { canBack, canForward -> viewModel.updateTabNavState(activeTab.id, canBack, canForward) },
                        onMediaDetected = { media -> viewModel.addDetectedMedia(activeTab.id, media) },
                        onDirectDownloadRequested = { url, _, contentDisposition, mimeType, contentLength ->
                            val fileName = url.substringAfterLast('/').substringBefore('?').ifBlank { "download_file" }
                            viewModel.downloadEngine.startOrEnqueueDownload(
                                url = url,
                                fileName = fileName,
                                mimeType = mimeType,
                                totalBytes = contentLength
                            )
                            Toast.makeText(activeWebView?.context, "Starting download: $fileName", Toast.LENGTH_SHORT).show()
                        },
                        webViewInstanceHolder = { webView -> activeWebView = webView }
                    )
                }

                BrowserNavScreen.TABS -> {
                    TabSwitcherScreen(
                        tabs = tabs,
                        activeTabId = activeTabId,
                        onSelectTab = { tabId -> viewModel.switchTab(tabId) },
                        onCloseTab = { tabId -> viewModel.closeTab(tabId) },
                        onNewTab = { isIncognito -> viewModel.createTab("about:home", isIncognito) },
                        onCloseAllTabs = { viewModel.closeAllTabs() }
                    )
                }

                BrowserNavScreen.DOWNLOADS -> {
                    DownloadsScreen(
                        downloads = downloads,
                        availableStorageBytes = viewModel.downloadEngine.getAvailableStorageBytes(),
                        onPause = { taskId -> viewModel.downloadEngine.pauseDownload(taskId) },
                        onResume = { taskId -> viewModel.downloadEngine.resumeDownload(taskId) },
                        onCancel = { taskId -> viewModel.downloadEngine.cancelDownload(taskId) },
                        onDelete = { taskId, deleteFile -> viewModel.downloadEngine.removeTaskRecord(taskId, deleteFile) },
                        onClearCompleted = {
                            viewModel.clearCompletedDownloads()
                        }
                    )
                }

                BrowserNavScreen.BOOKMARKS -> {
                    BookmarksScreen(
                        bookmarks = bookmarks,
                        onOpenBookmark = { url ->
                            viewModel.updateTabUrl(activeTab.id, url)
                            viewModel.navigateScreen(BrowserNavScreen.BROWSER)
                        },
                        onDeleteBookmark = { bookmark -> viewModel.deleteBookmark(bookmark) },
                        onAddBookmark = { title, url, folder -> viewModel.addBookmark(title, url, folder) }
                    )
                }

                BrowserNavScreen.HISTORY -> {
                    HistoryScreen(
                        historyList = history,
                        onOpenHistoryItem = { url ->
                            viewModel.updateTabUrl(activeTab.id, url)
                            viewModel.navigateScreen(BrowserNavScreen.BROWSER)
                        },
                        onDeleteHistoryItem = { item -> viewModel.deleteHistoryItem(item) },
                        onClearAllHistory = { viewModel.clearAllHistory() }
                    )
                }

                BrowserNavScreen.SETTINGS -> {
                    SettingsScreen(
                        browserPreferences = viewModel.preferences,
                        searchEngine = searchEngine,
                        desktopModeDefault = desktopModeDefault,
                        trackerBlockerEnabled = trackerBlockerEnabled,
                        clearOnExit = clearOnExit,
                        amoledTheme = amoledTheme,
                        downloadPath = viewModel.downloadEngine.getDownloadDir().path,
                        onClearAllData = {
                            viewModel.clearAllHistory()
                        }
                    )
                }
            }
        }
    }

    // Media Sniffer Bottom Sheet
    if (showMediaSheet) {
        MediaDownloadSheet(
            mediaList = activeTab.detectedMedia,
            sheetState = sheetState,
            onDismiss = { showMediaSheet = false },
            onDownloadMedia = { media ->
                viewModel.downloadEngine.startOrEnqueueDownload(
                    url = media.url,
                    fileName = media.title,
                    mimeType = media.mimeType,
                    totalBytes = media.sizeBytes
                )
                Toast.makeText(
                    activeWebView?.context,
                    "Queued: ${media.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}
