package com.example.ui.screens

import android.webkit.CookieManager
import android.webkit.WebStorage
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BrowserPreferences
import com.example.model.SearchEngine
import com.example.ui.components.CyberBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.TxTorBackground
import com.example.ui.theme.TxTorCardBorderGradient
import com.example.ui.theme.TxTorCyan
import com.example.ui.theme.TxTorGlassBackground
import com.example.ui.theme.TxTorGlassBorder
import com.example.ui.theme.TxTorGreen
import com.example.ui.theme.TxTorPrimary
import com.example.ui.theme.TxTorRed
import com.example.ui.theme.TxTorSecondary
import com.example.ui.theme.TxTorSurfaceElevated
import com.example.ui.theme.TxTorTextMuted
import com.example.ui.theme.TxTorTextPrimary
import com.example.ui.theme.TxTorTextSecondary

@Composable
fun SettingsScreen(
    browserPreferences: BrowserPreferences,
    searchEngine: SearchEngine,
    desktopModeDefault: Boolean,
    trackerBlockerEnabled: Boolean,
    clearOnExit: Boolean,
    amoledTheme: Boolean,
    downloadPath: String,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEngineDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TxTorBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Settings Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = TxTorPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Settings & Privacy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TxTorTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION 1: SEARCH & BROWSING
        SectionHeader(title = "SEARCH & NAVIGATION")

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingRowClickable(
                    icon = Icons.Default.Search,
                    title = "Default Search Engine",
                    subtitle = searchEngine.displayName,
                    badgeText = "V8",
                    onClick = { showEngineDialog = true }
                )

                SettingDivider()

                SettingRowToggle(
                    icon = Icons.Default.Computer,
                    title = "Always Request Desktop Site",
                    subtitle = "Sends desktop user-agent to all loaded websites",
                    checked = desktopModeDefault,
                    onCheckedChange = { browserPreferences.setDesktopModeDefault(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION 2: PRIVACY & SECURITY
        SectionHeader(title = "PRIVACY & PROTECTION")

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingRowToggle(
                    icon = Icons.Default.Shield,
                    title = "Tracker & Ad Blocker",
                    subtitle = "Blocks known telemetry and tracking scripts",
                    checked = trackerBlockerEnabled,
                    onCheckedChange = { browserPreferences.setTrackerBlockerEnabled(it) }
                )

                SettingDivider()

                SettingRowToggle(
                    icon = Icons.Default.Security,
                    title = "Clear Data On App Exit",
                    subtitle = "Automatically wipe cookies and session cache on close",
                    checked = clearOnExit,
                    onCheckedChange = { browserPreferences.setClearOnExit(it) }
                )

                SettingDivider()

                SettingRowClickable(
                    icon = Icons.Default.CleaningServices,
                    title = "Clear Cookies & Web Storage",
                    subtitle = "Wipe cached images, site cookies, and storage now",
                    badgeText = "CLEAN",
                    badgeColor = TxTorRed,
                    onClick = { showClearDataDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION 3: DOWNLOADS & STORAGE
        SectionHeader(title = "STORAGE & DOWNLOADS")

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingRowClickable(
                    icon = Icons.Default.Folder,
                    title = "Download Location",
                    subtitle = downloadPath,
                    badgeText = "PATH",
                    onClick = {
                        Toast.makeText(context, "Downloads saved in: $downloadPath", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION 4: APPEARANCE
        SectionHeader(title = "APPEARANCE")

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingRowToggle(
                    icon = Icons.Default.DarkMode,
                    title = "AMOLED Black Theme",
                    subtitle = "Pure black glassmorphic background for OLED screens",
                    checked = amoledTheme,
                    onCheckedChange = { browserPreferences.setAmoledTheme(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION 5: ABOUT & DISCLAIMER
        SectionHeader(title = "ABOUT TX TOR")

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = TxTorCardBorderGradient
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TxTorPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TX TOR v1.0.0 (Release Edition)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TxTorTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "High-performance browser built with Jetpack Compose, Chromium Android WebView, and OkHttp async download streaming.",
                    fontSize = 12.sp,
                    color = TxTorTextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TxTorGlassBackground)
                        .border(1.dp, TxTorGlassBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Network Notice: TX TOR is an ultra-fast private browser name. It communicates through direct high-speed HTTP/HTTPS connections and does not connect to the Tor onion routing network unless an external Tor proxy is configured.",
                        fontSize = 11.sp,
                        color = TxTorCyan,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Search Engine Picker Dialog
    if (showEngineDialog) {
        AlertDialog(
            onDismissRequest = { showEngineDialog = false },
            title = { Text("Choose Search Engine", color = TxTorTextPrimary) },
            text = {
                Column {
                    SearchEngine.values().forEach { engine ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    browserPreferences.setSearchEngine(engine)
                                    showEngineDialog = false
                                }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = (searchEngine == engine),
                                onClick = {
                                    browserPreferences.setSearchEngine(engine)
                                    showEngineDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = TxTorSecondary,
                                    unselectedColor = TxTorGlassBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = engine.displayName,
                                color = TxTorTextPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showEngineDialog = false }) {
                    Text("Close", color = TxTorTextMuted)
                }
            },
            containerColor = TxTorSurfaceElevated
        )
    }

    // Clear Data Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Web Storage?", color = TxTorTextPrimary) },
            text = {
                Text(
                    text = "This will immediately purge WebView cookies, cache files, and local DOM databases.",
                    color = TxTorTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            CookieManager.getInstance().removeAllCookies(null)
                            CookieManager.getInstance().flush()
                            WebStorage.getInstance().deleteAllData()
                            onClearAllData()
                            Toast.makeText(context, "Browsing data cleared successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        showClearDataDialog = false
                    }
                ) {
                    Text("Clear Now", color = TxTorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = TxTorTextMuted)
                }
            },
            containerColor = TxTorSurfaceElevated
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TxTorPrimary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(1.dp)
            .background(TxTorGlassBorder.copy(alpha = 0.3f))
    )
}

@Composable
private fun SettingRowClickable(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    badgeColor: Color = TxTorPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TxTorGlassBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = TxTorPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TxTorTextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TxTorTextMuted)
        }
        if (badgeText != null) {
            CyberBadge(label = badgeText, badgeColor = badgeColor)
        }
    }
}

@Composable
private fun SettingRowToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TxTorGlassBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = TxTorPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TxTorTextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TxTorTextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TxTorSecondary,
                uncheckedThumbColor = TxTorTextMuted,
                uncheckedTrackColor = TxTorGlassBackground
            )
        )
    }
}
