package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class TelegramColors(
    val userBubble: Color,
    val otherBubble: Color,
    val chatBackground: Color,
    val unreadBadge: Color,
    val onlineIndicator: Color,
    val aiAccent: Color,
    val messageTimeMe: Color,
    val messageTimeOther: Color,
    val glassCard: Color,
    val glassBorder: Color,
    val glassSurface: Color
)

val LocalTelegramColors = staticCompositionLocalOf {
    TelegramColors(
        userBubble = Color(0x7000B4D8),
        otherBubble = Color(0x35FFFFFF),
        chatBackground = Color(0xFF0A1128),
        unreadBadge = LiquidCyan,
        onlineIndicator = LiquidEmerald,
        aiAccent = LiquidViolet,
        messageTimeMe = Color(0xFFD4F1F9),
        messageTimeOther = Color(0xFFBAC3CE),
        glassCard = Color(0x20FFFFFF),
        glassBorder = Color(0x44FFFFFF),
        glassSurface = Color(0x18FFFFFF)
    )
}

private val LiquidDarkColorScheme = darkColorScheme(
    primary = LiquidCyan,
    onPrimary = Color(0xFF001F29),
    primaryContainer = Color(0x3300E5FF),
    onPrimaryContainer = Color(0xFFD4FAFF),
    secondary = LiquidElectricBlue,
    onSecondary = Color.White,
    tertiary = LiquidPurple,
    background = Color(0xFF080D1A),
    onBackground = Color(0xFFF0F4F8),
    surface = Color(0xFF0E1626),
    onSurface = Color(0xFFF0F4F8),
    surfaceVariant = Color(0x26FFFFFF),
    onSurfaceVariant = Color(0xFFD1D8E0)
)

private val LiquidLightColorScheme = lightColorScheme(
    primary = Color(0xFF0077B6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF003644),
    secondary = Color(0xFF5E35B1),
    onSecondary = Color.White,
    tertiary = Color(0xFFD81B60),
    background = Color(0xFFF2F6FC),
    onBackground = Color(0xFF101928),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF101928),
    surfaceVariant = Color(0xFFE1E7F0),
    onSurfaceVariant = Color(0xFF4A5568)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LiquidDarkColorScheme else LiquidDarkColorScheme // Liquid glass looks best in dark aura mode

    val tgColors = TelegramColors(
        userBubble = Color(0x750077B6),
        otherBubble = Color(0x28FFFFFF),
        chatBackground = Color(0xFF080D1A),
        unreadBadge = LiquidCyan,
        onlineIndicator = LiquidEmerald,
        aiAccent = LiquidViolet,
        messageTimeMe = Color(0xFFE0F7FA),
        messageTimeOther = Color(0xFFB0BEC5),
        glassCard = Color(0x1EFFFFFF),
        glassBorder = Color(0x40FFFFFF),
        glassSurface = Color(0x18FFFFFF)
    )

    CompositionLocalProvider(LocalTelegramColors provides tgColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
