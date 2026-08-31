/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Performance optimized MiniPlayer - prevents unnecessary recomposition
 */

package com.metrolist.music.ui.player

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.Stable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import com.metrolist.music.LocalDatabase
import com.metrolist.music.LocalListenTogetherManager
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.R
import com.metrolist.music.constants.CropAlbumArtKey
import com.metrolist.music.constants.DarkModeKey
import com.metrolist.music.constants.MiniPlayerHeight
import com.metrolist.music.constants.PureBlackMiniPlayerKey
import com.metrolist.music.constants.SwipeSensitivityKey
import com.metrolist.music.constants.SwipeThumbnailKey
import com.metrolist.music.constants.ThumbnailCornerRadius
import com.metrolist.music.constants.UseNewMiniPlayerDesignKey
import com.metrolist.music.db.entities.ArtistEntity
import com.metrolist.music.listentogether.ListenTogetherManager
import com.metrolist.music.models.MediaMetadata
import com.metrolist.music.playback.CastConnectionHandler
import com.metrolist.music.playback.PlayerConnection
import com.metrolist.music.ui.screens.settings.DarkMode
import com.metrolist.music.ui.utils.resize
import com.metrolist.music.utils.joinToArtistString
import com.metrolist.music.utils.rememberEnumPreference
import com.metrolist.music.utils.rememberPreference
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import com.metrolist.music.ui.component.Icon as MIcon
import androidx.compose.ui.draw.blur
import com.metrolist.music.constants.MiniPlayerBackgroundStyle
import com.metrolist.music.constants.MiniPlayerBackgroundStyleKey
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.metrolist.music.ui.theme.PlayerColorExtractor

/**
 * Stable wrapper for progress state - reads values only during draw phase
 * This prevents recomposition when position/duration change
 */
@Stable
class ProgressState(
    private val positionState: MutableLongState,
    private val durationState: MutableLongState,
) {
    val progress: Float
        get() {
            val duration = durationState.longValue
            return if (duration > 0) (positionState.longValue.toFloat() / duration).coerceIn(0f, 1f) else 0f
        }
}


@Composable
fun MiniPlayer(
    positionState: MutableLongState, 
    durationState: MutableLongState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val playbackState by playerConnection.playbackState.collectAsState()
    
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()

    // Metrolist's cast handling
    val castHandler = remember(playerConnection) { 
        runCatching { playerConnection.service.castConnectionHandler }.getOrNull() 
    }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    // Pixel Music base layout specs: 64.dp height, 10.dp start padding, 12.dp end padding
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null, 
                onClick = onClick
            )
            .padding(start = 10.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Pixel Music Circular Album Art (44.dp)
        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
                model = mediaMetadata?.thumbnailUrl,
                contentDescription = "Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(44.dp).clip(CircleShape)
            )
            if (isCasting || playbackState == Player.STATE_BUFFERING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 2. Pixel Music Typography (15.sp SemiBold Title, 13.sp Artist)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = mediaMetadata?.title ?: "",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = mediaMetadata?.artists?.joinToArtistString { it.name } ?: "",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3. Pixel Music Previous Button (36.dp Circle)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(
                    enabled = canSkipPrevious,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = androidx.compose.material3.ripple(bounded = false)
                ) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    playerConnection.seekToPrevious()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_previous),
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 4. Pixel Music Play/Pause Button (36.dp Circle, Primary Background)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = androidx.compose.material3.ripple(bounded = false)
                ) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    playerConnection.togglePlayPause()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                contentDescription = "Play/Pause",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 5. Pixel Music Next Button (36.dp Circle)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable(
                    enabled = canSkipNext,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = androidx.compose.material3.ripple(bounded = false)
                ) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    playerConnection.seekToNext()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_next),
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
