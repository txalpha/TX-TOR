package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.downloader.MediaDetector
import com.example.model.BrowserTabState
import com.example.model.DetectedMedia
import com.example.ui.theme.TxTorBackground
import com.example.ui.theme.TxTorGlassBackground
import com.example.ui.theme.TxTorGlassBorder
import com.example.ui.theme.TxTorPrimary
import com.example.ui.theme.TxTorSecondary
import com.example.ui.theme.TxTorTextMuted
import com.example.ui.theme.TxTorTextPrimary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
private const val MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    tabState: BrowserTabState,
    isFindInPageVisible: Boolean,
    onCloseFindInPage: () -> Unit,
    onPageTitleChanged: (String) -> Unit,
    onPageProgressChanged: (Int, Boolean) -> Unit,
    onUrlChanged: (String) -> Unit,
    onNavStateChanged: (canGoBack: Boolean, canGoForward: Boolean) -> Unit,
    onMediaDetected: (DetectedMedia) -> Unit,
    onDirectDownloadRequested: (url: String, userAgent: String, contentDisposition: String, mimeType: String, contentLength: Long) -> Unit,
    webViewInstanceHolder: (WebView) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var findQuery by remember { mutableStateOf("") }
    val mediaDetector = remember { MediaDetector() }

    Box(modifier = modifier.fillMaxSize().background(TxTorBackground)) {
        if (tabState.url == "about:home") {
            HomeScreen(
                onNavigateUrl = onUrlChanged,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = tabState.isDesktopMode
                            loadWithOverviewMode = tabState.isDesktopMode
                            builtInZoomControls = true
                            displayZoomControls = false
                            setSupportZoom(true)
                            mediaPlaybackRequiresUserGesture = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            userAgentString = if (tabState.isDesktopMode) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }

                        if (tabState.isIncognito) {
                            settings.cacheMode = WebSettings.LOAD_NO_CACHE
                            CookieManager.getInstance().setAcceptCookie(false)
                        } else {
                            CookieManager.getInstance().setAcceptCookie(true)
                            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                onPageProgressChanged(newProgress, newProgress < 100)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                if (!title.isNullOrBlank()) {
                                    onPageTitleChanged(title)
                                }
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                url?.let { onUrlChanged(it) }
                                onNavStateChanged(canGoBack(), canGoForward())
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                onPageProgressChanged(100, false)
                                onNavStateChanged(canGoBack(), canGoForward())
                            }

                            override fun onReceivedSslError(
                                view: WebView?,
                                handler: SslErrorHandler?,
                                error: SslError?
                            ) {
                                // Default safe SSL handling
                                handler?.proceed()
                            }

                            override fun onLoadResource(view: WebView?, url: String?) {
                                super.onLoadResource(view, url)
                                if (url != null && mediaDetector.isDirectDownloadableUrl(url)) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val detected = mediaDetector.inspectUrl(url, view?.title)
                                        if (detected != null) {
                                            onMediaDetected(detected)
                                        }
                                    }
                                }
                            }
                        }

                        setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                            onDirectDownloadRequested(url, userAgent, contentDisposition, mimetype, contentLength)
                        }

                        webViewRef = this
                        webViewInstanceHolder(this)
                        loadUrl(tabState.url)
                    }
                },
                update = { webView ->
                    webViewRef = webView
                    webViewInstanceHolder(webView)

                    // Update desktop mode if toggled
                    val targetUserAgent = if (tabState.isDesktopMode) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
                    if (webView.settings.userAgentString != targetUserAgent) {
                        webView.settings.userAgentString = targetUserAgent
                        webView.settings.useWideViewPort = tabState.isDesktopMode
                        webView.settings.loadWithOverviewMode = tabState.isDesktopMode
                        webView.reload()
                    }

                    // Navigate if tab url changed externally
                    if (webView.url != tabState.url && tabState.url != "about:home" && tabState.url.isNotBlank()) {
                        webView.loadUrl(tabState.url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Find in Page Overlay Bar
        AnimatedVisibility(
            visible = isFindInPageVisible,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TxTorGlassBackground)
                    .border(1.dp, TxTorGlassBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TxTorPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = findQuery,
                        onValueChange = {
                            findQuery = it
                            webViewRef?.findAllAsync(it)
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(color = TxTorTextPrimary, fontSize = 14.sp),
                        cursorBrush = SolidColor(TxTorSecondary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { webViewRef?.findNext(true) }
                        )
                    )
                    IconButton(
                        onClick = { webViewRef?.findNext(false) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Previous Match",
                            tint = TxTorTextPrimary
                        )
                    }
                    IconButton(
                        onClick = { webViewRef?.findNext(true) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Next Match",
                            tint = TxTorTextPrimary
                        )
                    }
                    IconButton(
                        onClick = {
                            webViewRef?.clearMatches()
                            onCloseFindInPage()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Find",
                            tint = TxTorSecondary
                        )
                    }
                }
            }
        }
    }
}
