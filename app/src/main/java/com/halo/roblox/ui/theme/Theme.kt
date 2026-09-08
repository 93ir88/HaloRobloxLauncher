package com.halo.roblox.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val DarkPalette = darkColorScheme(
    primary            = Color(0xFFCFAFFF),
    onPrimary          = Color(0xFF3A0073),
    primaryContainer   = Color(0xFF55009E),
    onPrimaryContainer = Color(0xFFEDD6FF),
    secondary          = Color(0xFFCCC2DC),
    background         = Color(0xFF09090F),
    surface            = Color(0xFF131320),
    onBackground       = Color(0xFFE6E1E5),
    onSurface          = Color(0xFFE6E1E5),
    error              = Color(0xFFFF5449),
    errorContainer     = Color(0xFF410E0B),
    onErrorContainer   = Color(0xFFF9DEDC),
)

val HaloTypography = Typography(
    displayLarge = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.ExtraBold,
        fontSize      = 56.sp,
        lineHeight    = 64.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 32.sp,
        lineHeight = 40.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    )
)

@Composable
fun HaloRobloxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        else -> DarkPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor     = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = HaloTypography,
        content     = content
    )
}
