package com.metrolist.music.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ToggleSegmentButton(
    modifier: Modifier,
    active: Boolean,
    enabled: Boolean = true,
    activeColor: Color,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    activeContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    inactiveContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    activeCornerRadius: Dp = 60.dp,
    onClick: () -> Unit,
    iconId: Int,
    contentDesc: String
) {
    val targetBgColor = if (active) activeColor else inactiveColor
    val bgColor by animateColorAsState(
        targetValue = if (enabled) targetBgColor else targetBgColor.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 250),
        label = "toggle_bg"
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (active) activeCornerRadius else 12.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "toggle_radius"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(cornerRadius))
            .background(bgColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.graphicsLayer(alpha = if (enabled) 1f else 0.38f)) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = contentDesc,
                tint = if (active) activeContentColor else inactiveContentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
