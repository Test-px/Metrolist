/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.metrolist.music.R
import com.metrolist.music.ui.screens.Screens
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Stable
private fun isRouteSelected(currentRoute: String?, screenRoute: String, navigationItems: List<Screens>): Boolean {
    if (currentRoute == null) return false
    if (currentRoute == screenRoute) return true
    if (navigationItems.any { it.route == screenRoute } &&
        currentRoute.startsWith("$screenRoute/")) return true

    if (screenRoute == "search_input" &&
        (currentRoute.startsWith("search/") || currentRoute == "search/{query}")) return true

    return false
}

@Composable
fun AppNavigationRail(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    onSearchLongClick: (() -> Unit)? = null,
    onHomeLongHold: (() -> Unit)? = null,
) {
    val containerColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer
    val haptics = LocalHapticFeedback.current
    val viewConfiguration = LocalViewConfiguration.current

    NavigationRail(
        modifier = modifier,
        containerColor = containerColor
    ) {
        Spacer(modifier = Modifier.weight(1f))

        navigationItems.forEach { screen ->
            val isSelected = remember(currentRoute, screen.route) {
                isRouteSelected(currentRoute, screen.route, navigationItems)
            }
            val currentIsSelected by rememberUpdatedState(isSelected)
            val iconRes = remember(isSelected, screen) {
                if (isSelected) screen.iconIdActive else screen.iconIdInactive
            }

            val isSearchItem = screen == Screens.Search && onSearchLongClick != null
            val isHomeHoldItem = screen == Screens.Home && onHomeLongHold != null
            val interactionSource = remember { MutableInteractionSource() }

            if (isSearchItem || isHomeHoldItem) {
                LaunchedEffect(interactionSource) {
                    var isLongClick = false
                    interactionSource.interactions.collectLatest { interaction ->
                        when (interaction) {
                            is PressInteraction.Press -> {
                                isLongClick = false
                                delay(if (isHomeHoldItem) 15_000L else viewConfiguration.longPressTimeoutMillis)
                                isLongClick = true
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (isHomeHoldItem) onHomeLongHold.invoke() else onSearchLongClick?.invoke()
                            }
                            is PressInteraction.Release -> {
                                if (!isLongClick) {
                                    onItemClick(screen, currentIsSelected)
                                }
                            }
                            is PressInteraction.Cancel -> {
                                isLongClick = false
                            }
                        }
                    }
                }
            }

            NavigationRailItem(
                selected = isSelected,
                onClick = {
                    if (!isSearchItem && !isHomeHoldItem) {
                        onItemClick(screen, currentIsSelected)
                    }
                },
                interactionSource = interactionSource,
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = stringResource(screen.titleId)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * PixelMusic Expressive Floating Pill Navigation Bar with Detached Settings Button
 */
@Composable
fun AppNavigationBar(
    navigationItems: List<Screens>,
    currentRoute: String?,
    onItemClick: (Screens, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    slimNav: Boolean = false,
    onSearchLongClick: (() -> Unit)? = null,
    onHomeLongHold: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
) {
    val haptics = LocalHapticFeedback.current
    val viewConfiguration = LocalViewConfiguration.current

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Floating Pill Container for main navigation items
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp),
                shape = CircleShape,
                color = if (pureBlack) Color(0xFF141414) else MaterialTheme.colorScheme.surfaceContainer,
                shadowElevation = 6.dp,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navigationItems.forEach { screen ->
                        val isSelected = remember(currentRoute, screen.route) {
                            isRouteSelected(currentRoute, screen.route, navigationItems)
                        }
                        val currentIsSelected by rememberUpdatedState(isSelected)
                        val iconRes = remember(isSelected, screen) {
                            if (isSelected) screen.iconIdActive else screen.iconIdInactive
                        }

                        val animatedWeight by animateFloatAsState(
                            targetValue = if (isSelected) 2.0f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "tab_weight_${screen.route}"
                        )

                        val containerColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            animationSpec = tween(200),
                            label = "container_color_${screen.route}"
                        )

                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            animationSpec = tween(200),
                            label = "content_color_${screen.route}"
                        )

                        val isSearchItem = screen == Screens.Search && onSearchLongClick != null
                        val isHomeHoldItem = screen == Screens.Home && onHomeLongHold != null
                        val interactionSource = remember { MutableInteractionSource() }

                        if (isSearchItem || isHomeHoldItem) {
                            LaunchedEffect(interactionSource) {
                                var isLongClick = false
                                interactionSource.interactions.collectLatest { interaction ->
                                    when (interaction) {
                                        is PressInteraction.Press -> {
                                            isLongClick = false
                                            delay(if (isHomeHoldItem) 15_000L else viewConfiguration.longPressTimeoutMillis)
                                            isLongClick = true
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                            if (isHomeHoldItem) onHomeLongHold.invoke() else onSearchLongClick?.invoke()
                                        }
                                        is PressInteraction.Release -> {
                                            if (!isLongClick) {
                                                onItemClick(screen, currentIsSelected)
                                            }
                                        }
                                        is PressInteraction.Cancel -> {
                                            isLongClick = false
                                        }
                                    }
                                }
                            }
                        }

                        Surface(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (!isSearchItem && !isHomeHoldItem) {
                                    onItemClick(screen, currentIsSelected)
                                }
                            },
                            modifier = Modifier
                                .weight(animatedWeight)
                                .height(46.dp),
                            shape = CircleShape,
                            color = containerColor,
                            contentColor = contentColor,
                            interactionSource = interactionSource
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = stringResource(screen.titleId),
                                    modifier = Modifier.size(22.dp)
                                )
                                AnimatedVisibility(
                                    visible = isSelected && !slimNav,
                                    enter = fadeIn(animationSpec = tween(200)) + expandHorizontally(expandFrom = Alignment.Start),
                                    exit = fadeOut(animationSpec = tween(150)) + shrinkHorizontally(shrinkTowards = Alignment.Start)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = stringResource(screen.titleId),
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1,
                                            fontWeight = FontWeight.Bold,
                                            color = contentColor,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Detached Floating Settings Button (PixelMusic signature design)
            val isSettingsSelected = currentRoute == "settings"
            Surface(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSettingsClick?.invoke()
                },
                modifier = Modifier.size(54.dp),
                shape = CircleShape,
                color = if (isSettingsSelected) MaterialTheme.colorScheme.primary else if (pureBlack) Color(0xFF141414) else MaterialTheme.colorScheme.surfaceContainer,
                contentColor = if (isSettingsSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                shadowElevation = 6.dp,
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        painter = painterResource(id = R.drawable.rounded_settings_24),
                        contentDescription = stringResource(R.string.settings),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
