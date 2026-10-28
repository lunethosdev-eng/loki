package com.music.bitchord.ui.tv.pokke

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.ui.tv.theme.TvDimensions

/**
 * Pokke TV-first chrome — designed for 10-foot UI and D-pad focus,
 * not a stretched phone layout.
 *
 * Wire this above [com.music.bitchord.ui.tv.screens.TvHomeScreen] shelves
 * when ready (hero + quick actions row). Playback still uses the shared
 * player; only the chrome/layout is TV-native.
 */
@Composable
fun PokkeTvHeroHeader(
    title: String = "Pokke",
    subtitle: String = "Music on the big screen",
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF0D0D12),
                    ),
                ),
            )
            .padding(horizontal = TvDimensions.SafeMarginHorizontal, vertical = 28.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.align(Alignment.BottomStart),
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp,
            )
        }
    }
}

/**
 * Horizontal quick-actions strip for remote navigation (Search, Library, Party…).
 * Prefer large focus targets (≥48dp) and clear selected state.
 */
@Composable
fun PokkeTvQuickRow(
    labels: List<String> = listOf("Home", "Search", "Library", "Party", "Settings"),
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvDimensions.SafeMarginHorizontal, vertical = 12.dp),
    ) {
        labels.forEach { label ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(48.dp)
                    .width(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .focusProperties { canFocus = true },
            ) {
                Text(text = label, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
