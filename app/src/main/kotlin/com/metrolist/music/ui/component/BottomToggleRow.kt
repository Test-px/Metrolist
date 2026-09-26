package com.metrolist.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.metrolist.music.R
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape

@Composable
fun BottomToggleRow(
    isShuffleEnabled: Boolean,
    repeatMode: Int,
    isFavorite: Boolean,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
    activeColorMain: Color = MaterialTheme.colorScheme.primary,
    activeColorSecondary: Color = MaterialTheme.colorScheme.secondary,
    activeColorTertiary: Color = MaterialTheme.colorScheme.tertiary,
    onActiveColorMain: Color = MaterialTheme.colorScheme.onPrimary,
    onActiveColorSecondary: Color = MaterialTheme.colorScheme.onSecondary,
    onActiveColorTertiary: Color = MaterialTheme.colorScheme.onTertiary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f),
    inactiveContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
) {
    val rowCorners = 32.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = containerColor,
                shape = AbsoluteSmoothCornerShape(cornerRadius = rowCorners, smoothnessAsPercent = 60)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .clip(AbsoluteSmoothCornerShape(cornerRadius = rowCorners, smoothnessAsPercent = 60)),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val commonModifier = Modifier.weight(1f)

            ToggleSegmentButton(
                modifier = commonModifier,
                active = isShuffleEnabled,
                activeColor = activeColorMain,
                activeCornerRadius = rowCorners,
                activeContentColor = onActiveColorMain,
                inactiveColor = inactiveColor,
                inactiveContentColor = inactiveContentColor,
                onClick = onShuffleToggle,
                iconId = R.drawable.shuffle,
                contentDesc = "Shuffle"
            )

            val repeatActive = repeatMode != Player.REPEAT_MODE_OFF
            val repeatIcon = when (repeatMode) {
                Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                else -> R.drawable.repeat
            }
            ToggleSegmentButton(
                modifier = commonModifier,
                active = repeatActive,
                activeColor = activeColorSecondary,
                activeCornerRadius = rowCorners,
                activeContentColor = onActiveColorSecondary,
                inactiveColor = inactiveColor,
                inactiveContentColor = inactiveContentColor,
                onClick = onRepeatToggle,
                iconId = repeatIcon,
                contentDesc = "Repeat"
            )

            ToggleSegmentButton(
                modifier = commonModifier,
                active = isFavorite,
                activeColor = activeColorTertiary,
                activeCornerRadius = rowCorners,
                activeContentColor = onActiveColorTertiary,
                inactiveColor = inactiveColor,
                inactiveContentColor = inactiveContentColor,
                onClick = onFavoriteToggle,
                iconId = if (isFavorite) R.drawable.favorite else R.drawable.favorite_border,
                contentDesc = "Favorite"
            )
        }
    }
}
