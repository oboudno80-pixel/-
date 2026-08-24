package com.example.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Liquid Glass Color Palette
val LiquidSapphire = Color(0xFF0D1B2A)
val LiquidNavy = Color(0xFF0F172A)
val LiquidDeepBlue = Color(0xFF1B263B)
val LiquidCyan = Color(0xFF00E5FF)
val LiquidElectricBlue = Color(0xFF2979FF)
val LiquidViolet = Color(0xFF7C4DFF)
val LiquidPurple = Color(0xFFD500F9)
val LiquidEmerald = Color(0xFF00E676)
val LiquidCoral = Color(0xFFFF5252)

// Glass Frosted Surfaces
val GlassSurfaceDark = Color(0x22FFFFFF)
val GlassSurfaceLight = Color(0x66FFFFFF)
val GlassSurfaceElevated = Color(0x33FFFFFF)
val GlassCardBackground = Color(0x1AFFFFFF)
val GlassHighlightBorder = Color(0x55FFFFFF)

// Chat Bubbles in Liquid Glass
val GlassUserBubble = Color(0x501E88E5)
val GlassUserBubbleGradient = listOf(
    Color(0x7000B4D8),
    Color(0x900077B6)
)
val GlassOtherBubbleGradient = listOf(
    Color(0x35FFFFFF),
    Color(0x18FFFFFF)
)
val GlassAiBubbleGradient = listOf(
    Color(0x507C4DFF),
    Color(0x654A148C)
)
val GlassMediaBubbleGradient = listOf(
    Color(0x40D500F9),
    Color(0x502979FF)
)

/**
 * Animated Liquid Aura Background with moving glowing nodes
 */
@Composable
fun liquidAuraBackgroundBrush(): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_mesh")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mesh_float"
    )

    return Brush.radialGradient(
        colors = listOf(
            LiquidDeepBlue,
            LiquidSapphire,
            Color(0xFF080D1A)
        ),
        center = Offset(300f + animOffset * 0.3f, 400f + animOffset * 0.2f),
        radius = 1200f
    )
}

/**
 * High-end Liquid Glass Card Modifier with Specular Gloss highlights and soft shadow
 */
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassCardBackground,
    borderAlpha: Float = 0.35f,
    glowColor: Color = LiquidCyan.copy(alpha = 0.08f),
    elevation: Dp = 0.dp
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = LiquidNavy,
        spotColor = glowColor
    )
    .clip(shape)
    .background(
        Brush.linearGradient(
            colors = listOf(
                backgroundColor.copy(alpha = (backgroundColor.alpha * 1.3f).coerceAtMost(0.45f)),
                backgroundColor.copy(alpha = (backgroundColor.alpha * 0.7f).coerceAtLeast(0.08f))
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 600f)
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha),
                Color.White.copy(alpha = borderAlpha * 0.15f),
                Color.Transparent,
                Color.White.copy(alpha = borderAlpha * 0.3f)
            ),
            start = Offset(0f, 0f),
            end = Offset(300f, 300f)
        ),
        shape = shape
    )

/**
 * Glowing Accent Glass for action buttons and chips
 */
fun Modifier.liquidGlassAccent(
    shape: Shape = RoundedCornerShape(16.dp),
    accentColor: Color = LiquidCyan,
    glowAlpha: Float = 0.25f
): Modifier = this
    .clip(shape)
    .background(
        Brush.horizontalGradient(
            colors = listOf(
                accentColor.copy(alpha = glowAlpha),
                accentColor.copy(alpha = glowAlpha * 0.5f)
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.6f),
                accentColor.copy(alpha = 0.8f)
            )
        ),
        shape = shape
    )
