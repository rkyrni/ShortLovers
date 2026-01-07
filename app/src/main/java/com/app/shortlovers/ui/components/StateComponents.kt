package com.app.shortlovers.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.shortlovers.ui.theme.BaseYellow
import com.app.shortlovers.ui.theme.KumbhSansFamily

/** Shimmer effect modifier for loading placeholders */
@Composable
fun shimmerBrush(): Brush {
    val shimmerColors =
        listOf(
            Color.DarkGray.copy(alpha = 0.6f),
            Color.DarkGray.copy(alpha = 0.2f),
            Color.DarkGray.copy(alpha = 0.6f)
        )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by
    transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

/** Loading view with shimmer skeleton placeholders */
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    val shimmer = shimmerBrush()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Carousel placeholder
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(shimmer)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tab placeholder
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                Box(
                    modifier =
                        Modifier
                            .width(80.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(shimmer)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter placeholder
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Box(
                    modifier =
                        Modifier
                            .width(100.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(shimmer)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid placeholder
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(6) {
                Column {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.7f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(shimmer)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(0.8f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(shimmer)
                    )
                }
            }
        }
    }
}

/** Empty state view when no data is available */
@Composable
fun EmptyView(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info,
    title: String = "Tidak Ada Data",
    message: String = "Belum ada konten untuk ditampilkan",
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            color = Color.White,
            fontFamily = KumbhSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = Color.Gray,
            fontFamily = KumbhSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onAction,
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = BaseYellow
                    ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = actionLabel,
                    fontFamily = KumbhSansFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/** Error state view with retry option */
@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    title: String = "Terjadi Kesalahan",
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFFF6B6B),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            color = Color.White,
            fontFamily = KumbhSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = Color.Gray,
            fontFamily = KumbhSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = BaseYellow,
                    contentColor = Color.Black
                ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Coba Lagi",
                fontFamily = KumbhSansFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Offline state view when no internet connection */
@Composable
fun OfflineView(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tidak Ada Koneksi",
            color = Color.White,
            fontFamily = KumbhSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Periksa koneksi internet Anda dan coba lagi",
            color = Color.Gray,
            fontFamily = KumbhSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = BaseYellow,
                    contentColor = Color.Black
                ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Coba Lagi",
                fontFamily = KumbhSansFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
