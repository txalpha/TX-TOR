package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTabState
import com.example.ui.theme.TxTorBackground
import com.example.ui.theme.TxTorCardBorderGradient
import com.example.ui.theme.TxTorGlassBackground
import com.example.ui.theme.TxTorGlassBorder
import com.example.ui.theme.TxTorGreen
import com.example.ui.theme.TxTorPrimary
import com.example.ui.theme.TxTorSecondary
import com.example.ui.theme.TxTorSurfaceElevated
import com.example.ui.theme.TxTorTextMuted
import com.example.ui.theme.TxTorTextPrimary

@Composable
fun BrowserTopBar(
    tabState: BrowserTabState,
    onNavigate: (String) -> Unit,
    onReload: () -> Unit,
    onStop: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onOpenMediaSheet: () -> Unit,
    onBookmarkCurrentPage: () -> Unit,
    onFindInPage: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenBookmarks: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingUrl by remember { mutableStateOf(false) }
    var urlInput by remember(tabState.url) {
        mutableStateOf(if (tabState.url == "about:home" || tabState.url == "about:blank") "" else tabState.url)
    }
    var menuExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val isHttps = tabState.url.startsWith("https://", ignoreCase = true)
    val displayUrl = when {
        tabState.url == "about:home" -> "TX TOR • Speed Dial"
        tabState.url == "about:blank" -> "New Tab"
        else -> tabState.url.removePrefix("https://").removePrefix("http://").removePrefix("www.")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TxTorBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // URL / Search Capsule
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(TxTorGlassBackground)
                    .border(1.dp, if (isEditingUrl) TxTorSecondary else TxTorGlassBorder, RoundedCornerShape(22.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (isEditingUrl) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TxTorPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .testTag("url_input_field"),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = TxTorTextPrimary,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(TxTorSecondary),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    keyboardController?.hide()
                                    isEditingUrl = false
                                    onNavigate(urlInput.trim())
                                }
                            )
                        )
                        if (urlInput.isNotEmpty()) {
                            IconButton(
                                onClick = { urlInput = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TxTorTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                urlInput = if (tabState.url.startsWith("about:")) "" else tabState.url
                                isEditingUrl = true
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isHttps) Icons.Default.Lock else Icons.Default.Shield,
                            contentDescription = if (isHttps) "Secure HTTPS" else "Web",
                            tint = if (isHttps) TxTorGreen else TxTorPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = displayUrl,
                            color = TxTorTextPrimary,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // Reload / Stop button
                        if (tabState.isLoading) {
                            IconButton(
                                onClick = onStop,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Stop",
                                    tint = TxTorSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = onReload,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reload",
                                    tint = TxTorTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Detected Video/Media Badge Button
            if (tabState.detectedMedia.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(19.dp))
                        .background(TxTorSecondary.copy(alpha = 0.2f))
                        .border(1.dp, TxTorSecondary, RoundedCornerShape(19.dp))
                        .clickable(onClick = onOpenMediaSheet)
                        .padding(horizontal = 10.dp)
                        .testTag("media_detect_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Media Available",
                            tint = TxTorSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${tabState.detectedMedia.size}",
                            color = Color.White,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            // Desktop Mode quick toggle icon
            IconButton(
                onClick = onToggleDesktopMode,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (tabState.isDesktopMode) TxTorPrimary.copy(alpha = 0.25f) else Color.Transparent)
                    .border(
                        1.dp,
                        if (tabState.isDesktopMode) TxTorPrimary else TxTorGlassBorder,
                        CircleShape
                    )
                    .testTag("desktop_toggle_button")
            ) {
                Icon(
                    imageVector = if (tabState.isDesktopMode) Icons.Default.Computer else Icons.Default.Smartphone,
                    contentDescription = if (tabState.isDesktopMode) "Desktop Mode Active" else "Mobile Mode Active",
                    tint = if (tabState.isDesktopMode) TxTorPrimary else TxTorTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Menu button & Dropdown
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = TxTorTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .background(TxTorSurfaceElevated)
                        .border(1.dp, TxTorGlassBorder, RoundedCornerShape(8.dp))
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (tabState.isDesktopMode) "Switch to Mobile Site" else "Request Desktop Site",
                                color = TxTorTextPrimary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                if (tabState.isDesktopMode) Icons.Default.Smartphone else Icons.Default.Computer,
                                contentDescription = null,
                                tint = TxTorPrimary
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onToggleDesktopMode()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add Bookmark", color = TxTorTextPrimary) },
                        leadingIcon = {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = TxTorSecondary)
                        },
                        onClick = {
                            menuExpanded = false
                            onBookmarkCurrentPage()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Bookmarks", color = TxTorTextPrimary) },
                        onClick = {
                            menuExpanded = false
                            onOpenBookmarks()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("History", color = TxTorTextPrimary) },
                        onClick = {
                            menuExpanded = false
                            onOpenHistory()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Find in Page", color = TxTorTextPrimary) },
                        onClick = {
                            menuExpanded = false
                            onFindInPage()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings", color = TxTorTextPrimary) },
                        onClick = {
                            menuExpanded = false
                            onOpenSettings()
                        }
                    )
                }
            }
        }

        // Web Loading indicator
        WebProgressBar(
            progress = tabState.progress,
            isLoading = tabState.isLoading
        )
    }
}
