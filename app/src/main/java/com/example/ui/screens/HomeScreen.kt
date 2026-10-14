package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CyberBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.TxTorBackground
import com.example.ui.theme.TxTorCardBorderGradient
import com.example.ui.theme.TxTorCyan
import com.example.ui.theme.TxTorGlassBackground
import com.example.ui.theme.TxTorGlassBorder
import com.example.ui.theme.TxTorGreen
import com.example.ui.theme.TxTorPrimary
import com.example.ui.theme.TxTorPurpleGradient
import com.example.ui.theme.TxTorSecondary
import com.example.ui.theme.TxTorTextMuted
import com.example.ui.theme.TxTorTextPrimary

data class QuickDialItem(
    val title: String,
    val url: String,
    val iconVector: ImageVector,
    val color: Color
)

@Composable
fun HomeScreen(
    onNavigateUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickDials = listOf(
        QuickDialItem("Google", "https://www.google.com", Icons.Default.Search, TxTorCyan),
        QuickDialItem("Wikipedia", "https://www.wikipedia.org", Icons.Default.Public, TxTorPrimary),
        QuickDialItem("GitHub", "https://github.com", Icons.Default.Security, TxTorSecondary),
        QuickDialItem("DuckDuckGo", "https://duckduckgo.com", Icons.Default.Shield, TxTorGreen),
        QuickDialItem("Reddit", "https://www.reddit.com", Icons.Default.Star, Color(0xFFFF5722)),
        QuickDialItem("Hacker News", "https://news.ycombinator.com", Icons.Default.Speed, Color(0xFFFF9800)),
        QuickDialItem("BBC News", "https://www.bbc.com/news", Icons.Default.Public, TxTorPrimary),
        QuickDialItem("Archive", "https://archive.org", Icons.Default.Download, TxTorCyan)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TxTorBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo & Brand Header
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(TxTorGlassBackground)
                .border(2.dp, TxTorPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = TxTorPrimary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "TX TOR",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TxTorTextPrimary,
            letterSpacing = 2.sp
        )

        Text(
            text = "Super Fast • Ultra Dark • Private Browser",
            fontSize = 13.sp,
            color = TxTorSecondary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy & Engine Status Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = TxTorCardBorderGradient
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatusPill(
                    icon = Icons.Default.Shield,
                    title = "Tracker Shield",
                    value = "ACTIVE",
                    color = TxTorGreen
                )
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(TxTorGlassBorder)
                )
                StatusPill(
                    icon = Icons.Default.Speed,
                    title = "V8 Engine",
                    value = "TURBO",
                    color = TxTorCyan
                )
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(TxTorGlassBorder)
                )
                StatusPill(
                    icon = Icons.Default.Download,
                    title = "Media Sniffer",
                    value = "READY",
                    color = TxTorSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Access / Speed Dial Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SPEED DIAL",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TxTorTextMuted,
                letterSpacing = 1.sp
            )
            CyberBadge(label = "8 FAVORITES", badgeColor = TxTorPrimary)
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(quickDials) { item ->
                QuickDialTile(
                    item = item,
                    onClick = { onNavigateUrl(item.url) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feature Highlights
        Text(
            text = "PREMIUM CAPABILITIES",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TxTorTextMuted,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureHighlightRow(
            icon = Icons.Default.Download,
            title = "Integrated Media Sniffer",
            description = "Detects direct MP4, WebM, and audio sources with instant download queueing.",
            color = TxTorSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureHighlightRow(
            icon = Icons.Default.Computer,
            title = "Desktop / PC Viewport",
            description = "Toggle desktop user agent and full desktop rendering for responsive desktop sites.",
            color = TxTorCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureHighlightRow(
            icon = Icons.Default.Shield,
            title = "Zero Leak Privacy",
            description = "Incognito browsing mode, clear cache & cookies on demand, and no cross-site tracking.",
            color = TxTorPrimary
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun StatusPill(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = TxTorTextMuted
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun QuickDialTile(
    item: QuickDialItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(TxTorGlassBackground)
                .border(1.dp, item.color.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.iconVector,
                contentDescription = item.title,
                tint = item.color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            fontSize = 11.sp,
            color = TxTorTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FeatureHighlightRow(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TxTorTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TxTorTextMuted,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
