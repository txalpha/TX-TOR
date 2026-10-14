package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// TX TOR Cyber Glassmorphic Color Palette
val TxTorBackground = Color(0xFF06030D)
val TxTorBackgroundAlt = Color(0xFF0A0515)
val TxTorSurface = Color(0xFF120824)
val TxTorSurfaceElevated = Color(0xFF1B0E36)
val TxTorSurfaceCard = Color(0xFF221142)

// Glassmorphism overlays
val TxTorGlassBackground = Color(0xB8140A28)
val TxTorGlassBorder = Color(0x40A855F7)
val TxTorGlassBorderBright = Color(0x80D946EF)
val TxTorGlassHighlight = Color(0x20FFFFFF)

// Neon & Accent Colors
val TxTorPrimary = Color(0xFFA855F7)        // Vivid Violet
val TxTorPrimaryVariant = Color(0xFF7C3AED) // Deep Electric Purple
val TxTorSecondary = Color(0xFFD946EF)      // Neon Magenta
val TxTorCyan = Color(0xFF06B6D4)           // Cyber Cyan
val TxTorGreen = Color(0xFF10B981)          // Safe / Complete
val TxTorAmber = Color(0xFFF59E0B)          // Warning / Paused
val TxTorRed = Color(0xFFEF4444)            // Error / Failed

// Text & Neutral Shades
val TxTorTextPrimary = Color(0xFFF8FAFC)
val TxTorTextSecondary = Color(0xFFCBD5E1)
val TxTorTextMuted = Color(0xFF7E8B9B)
val TxTorDivider = Color(0x228B5CF6)

// Gradients
val TxTorPurpleGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF7C3AED), Color(0xFFA855F7), Color(0xFFD946EF))
)

val TxTorHeaderGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF190B35), Color(0xFF0A0515))
)

val TxTorGlassGradient = Brush.verticalGradient(
    colors = listOf(Color(0xD9221142), Color(0xCC120824))
)

val TxTorCardBorderGradient = Brush.linearGradient(
    colors = listOf(Color(0x99A855F7), Color(0x33D946EF), Color(0x1A7C3AED))
)
