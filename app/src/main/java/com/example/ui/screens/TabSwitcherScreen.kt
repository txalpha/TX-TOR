package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTabState
import com.example.ui.components.CyberBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.TxTorBackground
import com.example.ui.theme.TxTorCardBorderGradient
import com.example.ui.theme.TxTorCyan
import com.example.ui.theme.TxTorGlassBackground
import com.example.ui.theme.TxTorGlassBorder
import com.example.ui.theme.TxTorGlassBorderBright
import com.example.ui.theme.TxTorPrimary
import com.example.ui.theme.TxTorSecondary
import com.example.ui.theme.TxTorTextMuted
import com.example.ui.theme.TxTorTextPrimary

@Composable
fun TabSwitcherScreen(
    tabs: List<BrowserTabState>,
    activeTabId: String,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: (isIncognito: Boolean) -> Unit,
    onCloseAllTabs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TxTorBackground)
            .padding(16.dp)
    ) {
        // Tab switcher header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = TxTorPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tabs (${tabs.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TxTorTextPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (tabs.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TxTorGlassBackground)
                            .border(1.dp, TxTorGlassBorder, RoundedCornerShape(8.dp))
                            .clickable(onClick = onCloseAllTabs)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Close All",
                            color = TxTorTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                NeonGradientButton(
                    text = "New Tab",
                    onClick = { onNewTab(false) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.testTag("new_tab_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tabs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = TxTorTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Open Tabs",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TxTorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap below to launch a super fast private browsing tab",
                        fontSize = 13.sp,
                        color = TxTorTextMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NeonGradientButton(
                        text = "Create Tab",
                        onClick = { onNewTab(false) }
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tabs, key = { it.id }) { tab ->
                    TabCard(
                        tab = tab,
                        isActive = tab.id == activeTabId,
                        onSelect = { onSelectTab(tab.id) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabCard(
    tab: BrowserTabState,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    val borderColor = if (isActive) TxTorSecondary else TxTorGlassBorder
    val borderWidth = if (isActive) 2.dp else 1.dp

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        borderBrush = if (isActive) TxTorCardBorderGradient else TxTorCardBorderGradient,
        borderWidth = borderWidth,
        onClick = onSelect
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Tab Header (Icon, Title, Close Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (tab.isIncognito) Icons.Default.Security else Icons.Default.Public,
                    contentDescription = null,
                    tint = if (tab.isIncognito) TxTorSecondary else TxTorPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (tab.title.isBlank() || tab.title == "about:home") "TX TOR Home" else tab.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TxTorTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("close_tab_${tab.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Tab",
                        tint = TxTorTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Tab Preview Representation Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TxTorGlassBackground)
                    .border(1.dp, TxTorGlassBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (tab.url.startsWith("about:")) "Speed Dial" else tab.url.removePrefix("https://").removePrefix("http://"),
                        fontSize = 11.sp,
                        color = TxTorTextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (tab.detectedMedia.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        CyberBadge(
                            label = "${tab.detectedMedia.size} Media Ready",
                            badgeColor = TxTorSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Footer Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tab.isDesktopMode) {
                    CyberBadge(label = "Desktop", badgeColor = TxTorCyan)
                } else {
                    CyberBadge(label = "Mobile", badgeColor = TxTorPrimary)
                }

                if (isActive) {
                    Text(
                        text = "Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TxTorSecondary
                    )
                }
            }
        }
    }
}
