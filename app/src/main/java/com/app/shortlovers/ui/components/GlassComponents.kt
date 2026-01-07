package com.app.shortlovers.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.shortlovers.ui.theme.FrostedBlack
import com.app.shortlovers.ui.theme.GlassBorder
import com.app.shortlovers.ui.theme.GlassWhite
import com.app.shortlovers.ui.theme.GlassYellow
import com.app.shortlovers.ui.theme.GlowYellow
import com.app.shortlovers.ui.theme.KumbhSansFamily

/**
 * Glass effect colors using theme tokens Provides consistent glassmorphism styling across the app
 */
object GlassColors {
    val glassWhite: Color
        @Composable get() = GlassWhite

    val glassBorder: Color
        @Composable get() = GlassBorder

    val glassYellow: Color
        @Composable get() = GlassYellow

    val glowYellow: Color
        @Composable get() = GlowYellow

    val frostedBlack: Color
        @Composable get() = FrostedBlack
}

/**
 * Creates a frosted glass background modifier
 * @param cornerRadius Corner radius for rounded shape
 * @param borderWidth Border width for glass effect
 */
fun Modifier.glassBackground(cornerRadius: Dp = 20.dp, borderWidth: Dp = 1.dp): Modifier =
    this
        .clip(RoundedCornerShape(cornerRadius))
        .background(GlassWhite)
        .border(borderWidth, GlassBorder, RoundedCornerShape(cornerRadius))

/**
 * Creates a glass card background with subtle gradient glow
 * @param cornerRadius Corner radius for rounded shape
 */
fun Modifier.glassCard(cornerRadius: Dp = 12.dp): Modifier =
    this
        .clip(RoundedCornerShape(cornerRadius))
        .background(
            Brush.verticalGradient(
                colors =
                    listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.04f)
                    )
            )
        )
        .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(cornerRadius))

/**
 * Frosted glass overlay for title sections
 * @param cornerRadius Corner radius for rounded shape
 */
fun Modifier.frostedOverlay(cornerRadius: Dp = 16.dp): Modifier =
    this
        .clip(RoundedCornerShape(cornerRadius))
        .background(FrostedBlack)
        .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(cornerRadius))

/**
 * Glass tab button component with frosted glass or solid yellow effect
 *
 * Reusable tab button supporting:
 * - Selected state: Yellow solid background
 * - Unselected state: Frosted glass with border
 * - Optional icon support
 *
 * @param text Tab label text
 * @param icon Optional leading icon
 * @param isSelected Whether this tab is currently selected
 * @param onClick Callback when tab is clicked
 * @param modifier Optional modifier for customization
 */
@Composable
fun GlassTab(
    text: String,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(24.dp))
                .then(
                    if (isSelected) {
                        Modifier.background(GlassYellow)
                    } else {
                        Modifier
                            .background(GlassWhite)
                            .border(
                                1.dp,
                                GlassBorder,
                                RoundedCornerShape(24.dp)
                            )
                    }
                )
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint =
                        if (isSelected) Color.Black
                        else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = text,
                color = if (isSelected) Color.Black else Color.White,
                fontFamily = KumbhSansFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

// ==================== Previews ====================

@Preview(showBackground = true, backgroundColor = 0xFF1B1A1A)
@Composable
private fun GlassTabSelectedPreview() {
    GlassTab(text = "Utama", isSelected = true, onClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF1B1A1A)
@Composable
private fun GlassTabUnselectedPreview() {
    GlassTab(text = "Terbaru", isSelected = false, onClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF1B1A1A)
@Composable
private fun GlassBackgroundPreview() {
    Box(modifier = Modifier
        .glassBackground()
        .padding(16.dp)) {
        Text(text = "Glass Background", color = Color.White, fontFamily = KumbhSansFamily)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1B1A1A)
@Composable
private fun FrostedOverlayPreview() {
    Box(modifier = Modifier
        .frostedOverlay()
        .padding(16.dp)) {
        Text(text = "Frosted Overlay", color = Color.White, fontFamily = KumbhSansFamily)
    }
}
