package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DetectedMedia
import com.example.ui.theme.TxTorCardBorderGradient
import com.example.ui.theme.TxTorGlassBackground
import com.example.ui.theme.TxTorGlassBorder
import com.example.ui.theme.TxTorPrimary
import com.example.ui.theme.TxTorSecondary
import com.example.ui.theme.TxTorSurfaceElevated
import com.example.ui.theme.TxTorTextMuted
import com.example.ui.theme.TxTorTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDownloadSheet(
    mediaList: List<DetectedMedia>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDownloadMedia: (DetectedMedia) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TxTorSurfaceElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TxTorGlassBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = TxTorSecondary,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "Detected Media Downloads",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TxTorTextPrimary
                    )
                    Text(
                        text = "${mediaList.size} eligible direct source(s) found",
                        fontSize = 13.sp,
                        color = TxTorTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (mediaList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = TxTorTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No direct media found on this page",
                            color = TxTorTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "This page does not expose direct unencrypted media files. DRM or authenticated streams cannot be downloaded.",
                            color = TxTorTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(mediaList) { media ->
                        MediaItemCard(
                            media = media,
                            onDownload = {
                                onDownloadMedia(media)
                                onDismiss()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Policy & authorized media notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(TxTorGlassBackground)
                    .border(1.dp, TxTorGlassBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TxTorPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TX TOR respects content owners and terms of service. Only download media you are authorized to access.",
                    color = TxTorTextMuted,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MediaItemCard(
    media: DetectedMedia,
    onDownload: () -> Unit
) {
    val sizeText = if (media.sizeBytes > 0) {
        val mb = media.sizeBytes / (1024.0 * 1024.0)
        "%.1f MB".format(mb)
    } else {
        "Direct Stream"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderBrush = TxTorCardBorderGradient
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CyberBadge(
                        label = media.extension.uppercase(),
                        badgeColor = TxTorSecondary
                    )
                    CyberBadge(
                        label = sizeText,
                        badgeColor = TxTorPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = media.title,
                    color = TxTorTextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = media.mimeType,
                    color = TxTorTextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            NeonGradientButton(
                text = "Download",
                onClick = onDownload,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}
