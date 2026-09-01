package com.remindme.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// ---- Brutalist V2 base palette ----
val Ink = Color(0xFF121212)        // primary text / borders
val Paper = Color(0xFFF4F2EC)      // light background
val SurfaceWhite = Color(0xFFFFFFFF)
val MutedGray = Color(0xFF636363)
val MutedLightGray = Color(0xFF8A8A8A)

val Night = Color(0xFF0D0D0D)      // dark background
val SurfaceDark = Color(0xFF171717)
val TextLight = Color(0xFFF5F3EC)  // primary text (dark mode)
val MutedDarkGray = Color(0xFF9B9B9B)

// ---- Type accents ----
val AccentRed = Color(0xFFD90429)    // MEDICAL
val AccentBlue = Color(0xFF0037FF)   // MONTHLY
val AccentGreen = Color(0xFF009E60)  // completion / success
val AccentAmber = Color(0xFFFFB703)  // overdue / warnings

// ============================================================
// Material 3 color scheme — LIGHT (brutalist)
// ============================================================
val md_theme_light_primary = Ink
val md_theme_light_onPrimary = SurfaceWhite
val md_theme_light_primaryContainer = SurfaceWhite
val md_theme_light_onPrimaryContainer = Ink
val md_theme_light_secondary = MutedGray
val md_theme_light_onSecondary = SurfaceWhite
val md_theme_light_secondaryContainer = SurfaceWhite
val md_theme_light_onSecondaryContainer = Ink
val md_theme_light_tertiary = AccentBlue
val md_theme_light_onTertiary = SurfaceWhite
val md_theme_light_background = Paper
val md_theme_light_onBackground = Ink
val md_theme_light_surface = SurfaceWhite
val md_theme_light_onSurface = Ink
val md_theme_light_surfaceVariant = Paper
val md_theme_light_onSurfaceVariant = MutedGray
val md_theme_light_outline = Ink
val md_theme_light_error = AccentRed
val md_theme_light_onError = SurfaceWhite

// ============================================================
// Material 3 color scheme — DARK (brutalist)
// ============================================================
val md_theme_dark_primary = TextLight
val md_theme_dark_onPrimary = Night
val md_theme_dark_primaryContainer = SurfaceDark
val md_theme_dark_onPrimaryContainer = TextLight
val md_theme_dark_secondary = MutedDarkGray
val md_theme_dark_onSecondary = Night
val md_theme_dark_secondaryContainer = SurfaceDark
val md_theme_dark_onSecondaryContainer = TextLight
val md_theme_dark_tertiary = AccentBlue
val md_theme_dark_onTertiary = SurfaceWhite
val md_theme_dark_background = Night
val md_theme_dark_onBackground = TextLight
val md_theme_dark_surface = SurfaceDark
val md_theme_dark_onSurface = TextLight
val md_theme_dark_surfaceVariant = Night
val md_theme_dark_onSurfaceVariant = MutedDarkGray
val md_theme_dark_outline = TextLight
val md_theme_dark_error = Color(0xFFFF5252)
val md_theme_dark_onError = Night