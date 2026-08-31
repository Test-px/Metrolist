/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.player

import androidx.activity.compose.BackHandler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.metrolist.music.LocalNavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.metrolist.music.LocalDatabase
import com.metrolist.music.LocalDownloadUtil
import com.metrolist.music.LocalListenTogetherManager
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.R
import com.metrolist.music.constants.CropAlbumArtKey
import com.metrolist.music.constants.DarkModeKey
import com.metrolist.music.constants.HidePlayerThumbnailKey
import com.metrolist.music.constants.HideStatusBarOnFullscreenKey
import com.metrolist.music.constants.KeepScreenOn
import com.metrolist.music.constants.PlayerBackgroundStyle
import com.metrolist.music.constants.PlayerBackgroundStyleKey
import com.metrolist.music.constants.PlayerButtonsStyle
import com.metrolist.music.constants.PlayerButtonsStyleKey
import com.metrolist.music.constants.PlayerHorizontalPadding
import com.metrolist.music.constants.QueuePeekHeight
import com.metrolist.music.constants.SleepTimerDefaultKey
import com.metrolist.music.constants.SleepTimerFadeOutKey
import com.metrolist.music.constants.SleepTimerStopAfterCurrentSongKey
import com.metrolist.music.constants.SliderStyle
import com.metrolist.music.constants.SliderStyleKey
import com.metrolist.music.constants.SquigglySliderKey
import com.metrolist.music.constants.ThumbnailCornerRadius
import com.metrolist.music.constants.UseNewPlayerDesignKey
import com.metrolist.music.db.entities.LyricsEntity
import com.metrolist.music.extensions.metadata
import com.metrolist.music.extensions.togglePlayPause
import com.metrolist.music.extensions.toggleRepeatMode
import com.metrolist.music.listentogether.RoomRole
import com.metrolist.music.models.MediaMetadata
import com.metrolist.music.ui.component.BottomSheet
import com.metrolist.music.ui.component.BottomSheetState
import com.metrolist.music.ui.component.LocalBottomSheetPageState
import com.metrolist.music.ui.component.LocalMenuState
import com.metrolist.music.ui.component.Lyrics
import com.metrolist.music.ui.component.PlayerSliderTrack
import com.metrolist.music.ui.component.ResizableIconButton
import com.metrolist.music.ui.component.SquigglySlider
import com.metrolist.music.ui.component.WavySlider
import com.metrolist.music.ui.component.rememberBottomSheetState
import com.metrolist.music.ui.menu.PlayerMenu
import com.metrolist.music.ui.screens.settings.DarkMode
import com.metrolist.music.ui.theme.PlayerColorExtractor
import com.metrolist.music.ui.theme.PlayerSliderColors
import com.metrolist.music.ui.utils.ShowMediaInfo
import com.metrolist.music.ui.utils.ShowOffsetDialog
import com.metrolist.music.utils.dataStore
import com.metrolist.music.utils.makeTimeString
import com.metrolist.music.utils.rememberEnumPreference
import com.metrolist.music.utils.rememberPreference
import com.metrolist.music.utils.safeDataStoreEdit
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt
import com.metrolist.music.ui.component.Icon as MIcon
import com.metrolist.music.constants.SleepTimerDefaultKey
import com.metrolist.music.constants.SleepTimerFadeOutKey
import com.metrolist.music.constants.SleepTimerStopAfterCurrentSongKey


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPlayer(
    state: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
    pureBlack: Boolean,
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val menuState = LocalMenuState.current
    val sleepTimerDefaultSetTemplate = stringResource(R.string.sleep_timer_default_set)
    val copiedTitleStr = stringResource(R.string.copied_title)
    val copiedArtistStr = stringResource(R.string.copied_artist)
    val bottomSheetPageState = LocalBottomSheetPageState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val (useNewPlayerDesign, onUseNewPlayerDesignChange) =
        rememberPreference(
            UseNewPlayerDesignKey,
            defaultValue = true,
        )
    val (hidePlayerThumbnail, onHidePlayerThumbnailChange) = rememberPreference(HidePlayerThumbnailKey, false)
    val (hideStatusBarOnFullscreen) = rememberPreference(HideStatusBarOnFullscreenKey, false)
    val cropAlbumArt by rememberPreference(CropAlbumArtKey, false)

    var showInlineLyrics by rememberSaveable {
        mutableStateOf(false)
    }

    var isFullScreen by rememberSaveable {
        mutableStateOf(false)
    }

    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.DEFAULT,
    )
    val playerButtonsStyle by rememberEnumPreference(
        key = PlayerButtonsStyleKey,
        defaultValue = PlayerButtonsStyle.DEFAULT,
    )

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val useDarkTheme =
        remember(darkTheme, isSystemInDarkTheme) {
            if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
        }

    val shouldUseDarkButtonColors =
        remember(playerBackground, useDarkTheme) {
            when (playerBackground) {
                PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> true
                PlayerBackgroundStyle.DEFAULT -> useDarkTheme
            }
        }

    val isPlaying by playerConnection.isPlaying.collectAsState()
    val isKeepScreenOn by rememberPreference(KeepScreenOn, false)
    val keepScreenOn = isPlaying && isKeepScreenOn

    DisposableEffect(playerBackground, state.isExpanded, useDarkTheme, keepScreenOn, isFullScreen, hideStatusBarOnFullscreen) {
        val window = (context as? android.app.Activity)?.window
        if (window != null && state.isExpanded) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)

            when (playerBackground) {
                PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> {
                    insetsController.isAppearanceLightStatusBars = false
                }

                PlayerBackgroundStyle.DEFAULT -> {
                    insetsController.isAppearanceLightStatusBars = !useDarkTheme
                }
            }

            if (isFullScreen && hideStatusBarOnFullscreen) {
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                insetsController.show(WindowInsetsCompat.Type.statusBars())
            }

            if (keepScreenOn && state.isExpanded) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        onDispose {
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !useDarkTheme
                insetsController.show(WindowInsetsCompat.Type.statusBars())
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    BackHandler(enabled = state.isExpanded) {
        state.collapseSoft()
    }

    val onBackgroundColor =
        when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.onSurface
        }
    val useBlackBackground =
        remember(isSystemInDarkTheme, darkTheme, pureBlack) {
            val useDarkTheme =
                if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
            useDarkTheme && pureBlack
        }

    val playbackState by playerConnection.playbackState.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val currentSong by playerConnection.currentSong.collectAsStateWithLifecycle(initialValue = null)
    val automix by playerConnection.service.automixItems.collectAsStateWithLifecycle()
    val repeatMode by playerConnection.repeatMode.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val isMuted by playerConnection.isMuted.collectAsStateWithLifecycle()

    val sliderStyle by rememberEnumPreference(SliderStyleKey, SliderStyle.DEFAULT)
    val squigglySlider by rememberPreference(SquigglySliderKey, defaultValue = false)

    // Listen Together state (reactive)
    val listenTogetherManager = LocalListenTogetherManager.current
    val listenTogetherRoleState = listenTogetherManager?.role?.collectAsStateWithLifecycle(initialValue = RoomRole.NONE)
    val isListenTogetherGuest = listenTogetherRoleState?.value == RoomRole.GUEST

    // Cast state - safely access castConnectionHandler to prevent crashes during service lifecycle changes
    val castHandler =
        remember(playerConnection) {
            try {
                playerConnection.service.castConnectionHandler
            } catch (e: Exception) {
                null
            }
        }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val castPosition by castHandler?.castPosition?.collectAsStateWithLifecycle() ?: remember { mutableLongStateOf(0L) }
    val castDuration by castHandler?.castDuration?.collectAsStateWithLifecycle() ?: remember { mutableLongStateOf(0L) }
    val castIsPlaying by castHandler?.castIsPlaying?.collectAsState() ?: remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isExpanded) {
        if (state.isExpanded) {
            delay(100)
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // Ignore if focus request fails
            }
        }
    }

    // Use Cast state when casting, otherwise local player
    val effectiveIsPlaying = if (isCasting) castIsPlaying else isPlaying

    // Use State objects for position/duration to pass to MiniPlayer without causing recomposition
    // These states persist across playback state changes to ensure continuous progress updates.
    // Seed from the player's current values so re-entering composition on resume shows the real
    // time immediately instead of flashing 0:00 until the first poll fires. runCatching guards the
    // player-not-ready race; the poll loop corrects duration if it isn't known yet.
    val positionState = remember { mutableLongStateOf(runCatching { playerConnection.player.currentPosition }.getOrDefault(0L)) }
    val durationState = remember {
        mutableLongStateOf(
            (mediaMetadata?.duration?.takeIf { it > 0 }?.toLong()?.times(1000L))
                ?: runCatching { playerConnection.player.duration }.getOrDefault(0L).coerceAtLeast(0L),
        )
    }

    // Convenience accessors for local use
    var position by positionState
    var duration by durationState

    val effectivePosition by remember {
        derivedStateOf {
            if (isCasting) {
                castPosition
            } else {
                position
            }
        }
    }

    var sliderPosition by remember {
        mutableStateOf<Long?>(null)
    }
    // Track when we last manually set position to avoid Cast overwriting it
    var lastManualSeekTime by remember { mutableLongStateOf(0L) }

    var gradientColors by remember {
        mutableStateOf<List<Color>>(emptyList())
    }
    val gradientColorsCache = remember { mutableMapOf<String, List<Color>>() }

    if (!canSkipNext && automix.isNotEmpty()) {
        playerConnection.service.addToQueueAutomix(automix[0], 0)
    }

    val defaultGradientColors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant)
    val fallbackColor = MaterialTheme.colorScheme.surface.toArgb()

    LaunchedEffect(mediaMetadata?.id, playerBackground) {
        if (playerBackground == PlayerBackgroundStyle.GRADIENT) {
            val currentMetadata = mediaMetadata
            if (currentMetadata != null && currentMetadata.thumbnailUrl != null) {
                val cachedColors = gradientColorsCache[currentMetadata.id]
                if (cachedColors != null) {
                    gradientColors = cachedColors
                    return@LaunchedEffect
                }
                withContext(Dispatchers.IO) {
                    val request =
                        ImageRequest
                            .Builder(context)
                            .data(currentMetadata.thumbnailUrl)
                            .size(100, 100)
                            .allowHardware(false)
                            .memoryCacheKey("gradient_${currentMetadata.id}")
                            .build()

                    val result = runCatching { context.imageLoader.execute(request) }.getOrNull()
                    if (result != null) {
                        val bitmap = result.image?.toBitmap()
                        if (bitmap != null) {
                            val palette =
                                withContext(Dispatchers.Default) {
                                    Palette
                                        .from(bitmap)
                                        .maximumColorCount(8)
                                        .resizeBitmapArea(100 * 100)
                                        .generate()
                                }
                            val extractedColors =
                                PlayerColorExtractor.extractGradientColors(
                                    palette = palette,
                                    fallbackColor = fallbackColor,
                                )
                            gradientColorsCache[currentMetadata.id] = extractedColors
                            withContext(Dispatchers.Main) { gradientColors = extractedColors }
                        }
                    }
                }
            }
        } else {
            gradientColors = emptyList()
        }
    }

    val TextBackgroundColor by animateColorAsState(
        targetValue =
            when (playerBackground) {
                PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.onBackground
                PlayerBackgroundStyle.BLUR -> Color.White
                PlayerBackgroundStyle.GRADIENT -> Color.White
            },
        label = "TextBackgroundColor",
    )

    val icBackgroundColor by animateColorAsState(
        targetValue =
            when (playerBackground) {
                PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.surface
                PlayerBackgroundStyle.BLUR -> Color.Black
                PlayerBackgroundStyle.GRADIENT -> Color.Black
            },
        label = "icBackgroundColor",
    )

    val (textButtonColor, iconButtonColor) =
        when {
            playerBackground == PlayerBackgroundStyle.BLUR ||
                playerBackground == PlayerBackgroundStyle.GRADIENT -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        Pair(Color.White, Color.Black)
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.onTertiary,
                        )
                    }
                }
            }

            else -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        if (useDarkTheme) {
                            Pair(Color.White, Color.Black)
                        } else {
                            Pair(Color.Black, Color.White)
                        }
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.onTertiary,
                        )
                    }
                }
            }
        }

    // Separate colors for Previous/Next buttons in PRIMARY/TERTIARY modes
    val (sideButtonContainerColor, sideButtonContentColor) =
        when {
            playerBackground == PlayerBackgroundStyle.BLUR ||
                playerBackground == PlayerBackgroundStyle.GRADIENT -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        Pair(
                            Color.White.copy(alpha = 0.2f),
                            Color.White,
                        )
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }

            else -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        Pair(
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }
        }

    val download by LocalDownloadUtil.current
        .getDownload(mediaMetadata?.id ?: "")
        .collectAsStateWithLifecycle(initialValue = null)

    val sleepTimerEnabled =
        remember(
            playerConnection.service.sleepTimer?.triggerTime,
            playerConnection.service.sleepTimer?.pauseWhenSongEnd,
        ) {
            playerConnection.service.sleepTimer?.isActive ?: false
        }

    var sleepTimerTimeLeft by remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(sleepTimerEnabled) {
        if (sleepTimerEnabled) {
            while (isActive) {
                sleepTimerTimeLeft =
                    if (playerConnection.service.sleepTimer?.pauseWhenSongEnd == true) {
                        playerConnection.player.duration - playerConnection.player.currentPosition
                    } else {
                        (playerConnection.service.sleepTimer?.triggerTime ?: 0L) - System.currentTimeMillis()
                    }
                delay(1000L)
            }
        }
    }

    val scope = rememberCoroutineScope()
    var showSleepTimerDialog by remember {
        mutableStateOf(false)
    }

    val sleepTimerDefault by rememberPreference(SleepTimerDefaultKey, 30f)
    var sleepTimerValue by remember { mutableFloatStateOf(sleepTimerDefault) }
    val isAtDefault by remember {
        derivedStateOf { sleepTimerValue.roundToInt() == sleepTimerDefault.roundToInt() }
    }
    LaunchedEffect(sleepTimerDefault) { sleepTimerValue = sleepTimerDefault }
    val sleepTimerStopAfterCurrentSong by rememberPreference(SleepTimerStopAfterCurrentSongKey, false)
    val sleepTimerFadeOut by rememberPreference(SleepTimerFadeOutKey, false)


    if (showSleepTimerDialog) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showSleepTimerDialog = false },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.bedtime),
                    contentDescription = null,
                )
            },
            title = { Text(stringResource(R.string.sleep_timer)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSleepTimerDialog = false
                        playerConnection.service.sleepTimer?.start(
                            minute = sleepTimerValue.roundToInt(),
                            stopAfterCurrentSong = sleepTimerStopAfterCurrentSong,
                            fadeOut = sleepTimerFadeOut,
                        )
                    },
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSleepTimerDialog = false },
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text =
                            pluralStringResource(
                                R.plurals.minute,
                                sleepTimerValue.roundToInt(),
                                sleepTimerValue.roundToInt(),
                            ),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Slider(
                        value = sleepTimerValue,
                        onValueChange = { sleepTimerValue = it },
                        valueRange = 5f..120f,
                        steps = (120 - 5) / 5 - 1,
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (isAtDefault) {
                            FilledIconButton(
                                onClick = {
                                    scope.launch {
                                        context.safeDataStoreEdit { settings ->
                                            settings[SleepTimerDefaultKey] = sleepTimerValue
                                        }
                                    }
                                    Toast.makeText(
                                        context,
                                        String.format(sleepTimerDefaultSetTemplate, sleepTimerValue.roundToInt()),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                            ) {
                                Text(stringResource(R.string.set_as_default))
                            }
                        } else {
                            OutlinedIconButton(
                                onClick = {
                                    scope.launch {
                                        context.safeDataStoreEdit { settings ->
                                            settings[SleepTimerDefaultKey] = sleepTimerValue
                                        }
                                    }
                                    Toast.makeText(
                                        context,
                                        String.format(sleepTimerDefaultSetTemplate, sleepTimerValue.roundToInt()),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                },
                            ) {
                                Text(stringResource(R.string.set_as_default))
                            }
                        }

                        OutlinedIconButton(
                            onClick = {
                                showSleepTimerDialog = false
                                playerConnection.service.sleepTimer?.start(minute = -1)
                            },
                        ) {
                            Text(stringResource(R.string.end_of_song))
                        }
                    }
                }
            },
        )
    }

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    // Position update - only for local playback
    // When casting, we use castPosition directly to avoid sync issues
    // Use isPlaying instead of playbackState to ensure continuous updates during playback
    LaunchedEffect(isPlaying, isCasting) {
        if (!isCasting && isPlaying) {
            while (isActive) {
                delay(100) // Update more frequently for smoother progress bar
                if (sliderPosition == null) { // Only update if user isn't dragging
                    position = playerConnection.player.currentPosition
                    // Don't clobber a valid (metadata-derived) duration with 0/UNSET mid-resolve.
                    playerConnection.player.duration.takeIf { it > 0 }?.let { duration = it }
                }
            }
        }
    }

    // Also update position when playback state changes (e.g., song change, seek)
    LaunchedEffect(playbackState, mediaMetadata?.id) {
        if (!isCasting) {
            position = playerConnection.player.currentPosition
            // Prefer the song's known duration (from metadata, available instantly from the restored
            // queue) so the slider range is right even when restored paused / before the stream
            // resolves; fall back to the player's duration once it is known.
            duration = (mediaMetadata?.duration?.takeIf { it > 0 }?.toLong()?.times(1000L))
                ?: playerConnection.player.duration
        }
    }

    // Auto-switch from repeat one to repeat all when song ends naturally
    var previousMediaId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(playbackState, mediaMetadata?.id) {
        val currentId = mediaMetadata?.id

        // Only switch from REPEAT_ONE to REPEAT_ALL when playback naturally ended
        // (i.e., the player transitioned to ENDED state and then moved to next track).
        // Do NOT switch on manual skips.
        if (currentId != null &&
            currentId != previousMediaId &&
            previousMediaId != null &&
            playbackState == Player.STATE_ENDED &&
            repeatMode == Player.REPEAT_MODE_ONE &&
            !isListenTogetherGuest) {
            playerConnection.player.setRepeatMode(Player.REPEAT_MODE_ALL)
        }

        previousMediaId = currentId
    }

    // When casting, use Cast position/duration directly
    // But wait a bit after manual seeks to let Cast catch up
    LaunchedEffect(isCasting, castPosition, castDuration) {
        if (isCasting && sliderPosition == null) {
            val timeSinceManualSeek = System.currentTimeMillis() - lastManualSeekTime
            if (timeSinceManualSeek > 1500) {
                // Only update from Cast if we haven't manually seeked recently
                position = castPosition
                if (castDuration > 0) duration = castDuration
            }
        }
    }

    val dismissedBound = QueuePeekHeight + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    val queueSheetState =
        rememberBottomSheetState(
            dismissedBound = dismissedBound,
            expandedBound = state.expandedBound,
            collapsedBound = dismissedBound + 1.dp,
            initialAnchor = 1,
        )

    val bottomSheetBackgroundColor =
        when (playerBackground) {
            PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> {
                MaterialTheme.colorScheme.surfaceContainer
            }

            else -> {
                if (useBlackBackground) {
                    Color.Black
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
            }
        }

    val backgroundAlpha = state.progress.coerceIn(0f, 1f)

    BottomSheet(
        state = state,
        modifier = modifier,
        background = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(bottomSheetBackgroundColor),
            ) {
                when (playerBackground) {
                    PlayerBackgroundStyle.BLUR -> {
                        AnimatedContent(
                            targetState = mediaMetadata?.thumbnailUrl,
                            transitionSpec = {
                                fadeIn(tween(800)).togetherWith(fadeOut(tween(800)))
                            },
                            label = "blurBackground",
                        ) { thumbnailUrl ->
                            if (thumbnailUrl != null) {
                                Box(modifier = Modifier.alpha(backgroundAlpha)) {
                                    AsyncImage(
                                        model =
                                            ImageRequest
                                                .Builder(context)
                                                .data(thumbnailUrl)
                                                .size(100, 100)
                                                .allowHardware(false)
                                                .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .blur(if (useDarkTheme) 150.dp else 100.dp),
                                    )
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.3f)),
                                    )
                                }
                            }
                        }
                    }

                    PlayerBackgroundStyle.GRADIENT -> {
                        AnimatedContent(
                            targetState = gradientColors,
                            transitionSpec = {
                                fadeIn(tween(800)).togetherWith(fadeOut(tween(800)))
                            },
                            label = "gradientBackground",
                        ) { colors ->
                            if (colors.isNotEmpty()) {
                                val gradientColorStops =
                                    if (colors.size >= 3) {
                                        arrayOf(
                                            0.0f to colors[0],
                                            0.5f to colors[1],
                                            1.0f to colors[2],
                                        )
                                    } else {
                                        arrayOf(
                                            0.0f to colors[0],
                                            0.6f to colors[0].copy(alpha = 0.7f),
                                            1.0f to Color.Black,
                                        )
                                    }
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .alpha(backgroundAlpha)
                                        .background(Brush.verticalGradient(colorStops = gradientColorStops))
                                        .background(Color.Black.copy(alpha = 0.2f)),
                                )
                            }
                        }
                    }

                    else -> {
                        PlayerBackgroundStyle.DEFAULT
                    }
                }
            }
        },
        onDismiss =
            if (!isListenTogetherGuest) {
                {
                    playerConnection.service.clearAutomix()
                    playerConnection.player.stop()
                    playerConnection.player.clearMediaItems()
                }
            } else {
                null
            },
        collapsedContent = {
            MiniPlayer(
                positionState = positionState,
                durationState = durationState,
                onClick = { state.expandSoft() },
            )
        },
    ) {
        // --- PIXEL MUSIC UPPER HALF ---
        val albumCoverSection: @Composable (Modifier) -> Unit = { modifier ->
            FullPlayerAlbumCoverSection(
                mediaMetadata = mediaMetadata,
                modifier = modifier
            )
        }

        val songMetadataSection: @Composable () -> Unit = {
            FullPlayerSongMetadataSection(
                mediaMetadata = mediaMetadata,
                showLyrics = showInlineLyrics,
                onToggleLyrics = { showInlineLyrics = !showInlineLyrics },
                onShowMore = { showSongInfoBottomSheet = true }
            )
        }

        val playerProgressSection: @Composable () -> Unit = {
            FullPlayerProgressSection(
                position = sliderPosition ?: effectivePosition,
                duration = duration,
                onPositionChange = {
                    if (!isListenTogetherGuest) sliderPosition = it
                },
                onPositionChangeFinished = {
                    if (!isListenTogetherGuest) {
                        sliderPosition?.let {
                            if (isCasting) {
                                castHandler?.seekTo(it)
                                lastManualSeekTime = System.currentTimeMillis()
                            } else {
                                playerConnection.player.seekTo(it)
                            }
                            position = it
                        }
                        sliderPosition = null
                    }
                },
                sliderStyle = sliderStyle,
                squigglySlider = squigglySlider,
                isPlaying = effectiveIsPlaying,
                textColor = TextBackgroundColor,
                sliderColors = com.metrolist.music.ui.theme.PlayerSliderColors.getSliderColors(textButtonColor, playerBackground, useDarkTheme)
            )
        }

        val playerControlsSection: @Composable () -> Unit = {
            val isEpisode = currentSong?.song?.isEpisode == true
            val isFavorite = if (isEpisode) currentSong?.song?.inLibrary != null else currentSong?.song?.liked == true
            
            FullPlayerControlsSection(
                context = context,
                isPlaying = effectiveIsPlaying,
                isMuted = isMuted,
                isListenTogetherGuest = isListenTogetherGuest,
                playbackState = playbackState,
                canSkipPrevious = canSkipPrevious,
                canSkipNext = canSkipNext,
                repeatMode = repeatMode,
                isFavorite = isFavorite,
                onPrevious = { playerConnection.seekToPrevious() },
                onPlayPause = {
                    if (isListenTogetherGuest) {
                        playerConnection.toggleMute()
                    } else if (isCasting) {
                        if (castIsPlaying) castHandler?.pause() else castHandler?.play()
                    } else if (playbackState == Player.STATE_ENDED) {
                        playerConnection.player.seekTo(0, 0)
                        playerConnection.player.playWhenReady = true
                    } else {
                        playerConnection.player.togglePlayPause()
                    }
                },
                onNext = { playerConnection.seekToNext() },
                onRepeatToggle = { playerConnection.player.toggleRepeatMode() },
                onFavoriteToggle = { playerConnection.toggleLike() },
                textButtonColor = textButtonColor,
                iconButtonColor = iconButtonColor,
                sideButtonContainerColor = sideButtonContainerColor,
                sideButtonContentColor = sideButtonContentColor
            )
        }

        // --- PIXEL MUSIC LAYOUT GRID ---
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                albumCoverSection(Modifier.weight(1f).fillMaxHeight())
                Spacer(Modifier.width(24.dp))
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    songMetadataSection()
                    playerProgressSection()
                    playerControlsSection()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                albumCoverSection(Modifier)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.align(Alignment.Start)) {
                        songMetadataSection()
                    }
                    playerProgressSection()
                }
                playerControlsSection()
            }
        }

        AnimatedVisibility(
            visible = !isFullScreen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            Queue(
                state = queueSheetState,
                playerBottomSheetState = state,
                background =
                    if (useBlackBackground) {
                        Color.Black
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    },
                onBackgroundColor = onBackgroundColor,
                TextBackgroundColor = TextBackgroundColor,
                textButtonColor = textButtonColor,
                iconButtonColor = iconButtonColor,
                pureBlack = pureBlack,
                showInlineLyrics = showInlineLyrics,
                playerBackground = playerBackground,
                onToggleLyrics = {
                    showInlineLyrics = !showInlineLyrics
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InlineLyricsView(
    mediaMetadata: MediaMetadata?,
    showLyrics: Boolean,
    positionProvider: () -> Long,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val currentLyrics by playerConnection.currentLyrics.collectAsStateWithLifecycle(initialValue = null)
    val queueWindows by playerConnection.queueWindows.collectAsStateWithLifecycle(initialValue = emptyList())
    val currentWindowIndex by playerConnection.currentWindowIndex.collectAsStateWithLifecycle(initialValue = -1)
    val lyrics = remember(currentLyrics) { currentLyrics?.lyrics?.trim() }
    val context = LocalContext.current
    val database = LocalDatabase.current
    val coroutineScope = rememberCoroutineScope()

    var appInForeground by remember {
        mutableStateOf(
            ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED),
        )
    }
    DisposableEffect(Unit) {
        val lifecycle = ProcessLifecycleOwner.get().lifecycle
        val observer =
            LifecycleEventObserver { _, _ ->
                appInForeground = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    val nextMetadata =
        remember(queueWindows, currentWindowIndex) {
            if (currentWindowIndex >= 0 && currentWindowIndex + 1 < queueWindows.size) {
                queueWindows[currentWindowIndex + 1].mediaItem.metadata
            } else {
                null
            }
        }

    LaunchedEffect(mediaMetadata?.id, currentLyrics) {
        if (mediaMetadata != null && currentLyrics == null) {
            delay(500)
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val entryPoint =
                        EntryPointAccessors.fromApplication(
                            context.applicationContext,
                            com.metrolist.music.di.LyricsHelperEntryPoint::class.java,
                        )
                    val lyricsHelper = entryPoint.lyricsHelper()
                    val fetchedLyricsWithProvider = lyricsHelper.getLyrics(mediaMetadata)
                    database.query {
                        upsert(LyricsEntity(mediaMetadata.id, fetchedLyricsWithProvider.lyrics, fetchedLyricsWithProvider.provider))
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    // Prefetch lyrics for the next queue item only while the lyrics pane is visible, the app is in the
    // foreground, and the current track's lyrics row has finished loading (avoids competing with the
    // active fetch).
    LaunchedEffect(
        nextMetadata?.id,
        showLyrics,
        appInForeground,
        mediaMetadata?.id,
        currentLyrics,
    ) {
        if (!showLyrics || !appInForeground || nextMetadata == null) return@LaunchedEffect
        val loadedForCurrent =
            currentLyrics?.let { lyrics ->
                mediaMetadata == null || lyrics.id == mediaMetadata.id
            } == true
        if (mediaMetadata != null && !loadedForCurrent) return@LaunchedEffect
        val nextId = nextMetadata.id
        delay(400)
        if (!showLyrics || !appInForeground || !isActive) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                val existing = database.lyrics(nextId).first()
                if (existing != null) return@withContext
                val entryPoint =
                    EntryPointAccessors.fromApplication(
                        context.applicationContext,
                        com.metrolist.music.di.LyricsHelperEntryPoint::class.java,
                    )
                val lyricsHelper = entryPoint.lyricsHelper()
                val fetched = lyricsHelper.getLyrics(nextMetadata)
                database.query {
                    upsert(LyricsEntity(nextId, fetched.lyrics, fetched.provider))
                }
            } catch (_: Exception) {
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        when {
            lyrics == null -> {
                ContainedLoadingIndicator()
            }

            lyrics == LyricsEntity.LYRICS_NOT_FOUND -> {
                Text(
                    text = stringResource(R.string.lyrics_not_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }

            else -> {
                val lyricsContent: @Composable () -> Unit = {
                    Lyrics(
                        sliderPositionProvider = positionProvider,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        showLyrics = showLyrics,
                    )
                }
                ProvideTextStyle(
                    value =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        ),
                ) {
                    lyricsContent()
                }
            }
        }
    }
}

@Composable
private fun PlayerMoreMenuButton(
    mediaMetadata: MediaMetadata,
    state: BottomSheetState,
    textButtonColor: Color,
    iconButtonColor: Color,
) {
    val navController = LocalNavController.current
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(textButtonColor)
                .clickable {
                    menuState.show {
                        PlayerMenu(
                            mediaMetadata = mediaMetadata,
                            playerBottomSheetState = state,
                            onShowDetailsDialog = {
                                mediaMetadata.id.let {
                                    bottomSheetPageState.show {
                                        ShowMediaInfo(it)
                                    }
                                }
                            },
                            onDismiss = menuState::dismiss,
                        )
                    }
                },
    ) {
        Image(
            painter = painterResource(R.drawable.more_horiz),
            contentDescription = null,
            colorFilter = ColorFilter.tint(iconButtonColor),
        )
    }
}

// ============================================================================
// PIXEL MUSIC COMPONENTS
// ============================================================================

@Composable
private fun FullPlayerAlbumCoverSection(
    mediaMetadata: MediaMetadata?,
    modifier: Modifier = Modifier
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isPlaying.collectAsState()
    
    // Pixel Music's signature "pause squish" effect
    val albumArtScale by animateFloatAsState(
        targetValue = if (!isPlaying) 0.95f else 1f,
        animationSpec = tween(durationMillis = 260, easing = LinearEasing),
        label = "AlbumArtScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = mediaMetadata?.thumbnailUrl?.let { com.metrolist.music.ui.utils.resize(it, 1200, 1200) },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .graphicsLayer {
                    scaleX = albumArtScale
                    scaleY = albumArtScale
                }
                .clip(RoundedCornerShape(18.dp))
        )
    }
}

@Composable
private fun FullPlayerSongMetadataSection(
    mediaMetadata: MediaMetadata?,
    showLyrics: Boolean,
    onToggleLyrics: () -> Unit,
    onShowMore: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 70.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = mediaMetadata?.title ?: "",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (mediaMetadata?.explicit == true) {
                    com.metrolist.music.ui.component.Icon.Explicit()
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = mediaMetadata?.artists?.joinToString(", ") { it.name } ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee()
                )
            }
        }

        // Pixel Music's Split Pill Buttons [Lyrics] [More]
        val chipColor = MaterialTheme.colorScheme.primaryContainer
        val chipContentColor = MaterialTheme.colorScheme.onPrimaryContainer

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.FilledIconButton(
                modifier = Modifier.size(width = 44.dp, height = 48.dp),
                colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (showLyrics) MaterialTheme.colorScheme.primary else chipColor,
                    contentColor = if (showLyrics) MaterialTheme.colorScheme.onPrimary else chipContentColor
                ),
                shape = RoundedCornerShape(
                    topStart = 50.dp, topEnd = 6.dp,
                    bottomStart = 50.dp, bottomEnd = 6.dp
                ),
                onClick = onToggleLyrics,
            ) {
                Icon(
                    painter = painterResource(R.drawable.lyrics),
                    contentDescription = "Lyrics"
                )
            }

            androidx.compose.material3.FilledIconButton(
                modifier = Modifier.size(width = 44.dp, height = 48.dp),
                colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                    containerColor = chipColor,
                    contentColor = chipContentColor
                ),
                shape = RoundedCornerShape(
                    topStart = 6.dp, topEnd = 50.dp,
                    bottomStart = 6.dp, bottomEnd = 50.dp
                ),
                onClick = onShowMore,
            ) {
                Icon(
                    painter = painterResource(R.drawable.more_vert),
                    contentDescription = "More options"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullPlayerProgressSection(
    position: Long,
    duration: Long,
    onPositionChange: (Long) -> Unit,
    onPositionChangeFinished: () -> Unit,
    sliderStyle: com.metrolist.music.constants.SliderStyle,
    squigglySlider: Boolean,
    isPlaying: Boolean,
    textColor: Color,
    sliderColors: androidx.compose.material3.SliderColors
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Slider matching Pixel Music padding
        when (sliderStyle) {
            com.metrolist.music.constants.SliderStyle.DEFAULT -> {
                Slider(
                    value = position.toFloat(),
                    valueRange = 0f..(if (duration <= 0) 0f else duration.toFloat()),
                    onValueChange = { onPositionChange(it.toLong()) },
                    onValueChangeFinished = onPositionChangeFinished,
                    colors = sliderColors,
                    modifier = Modifier.padding(horizontal = 0.dp)
                )
            }
            com.metrolist.music.constants.SliderStyle.WAVY -> {
                if (squigglySlider) {
                    com.metrolist.music.ui.component.SquigglySlider(
                        value = position.toFloat(),
                        valueRange = 0f..(if (duration <= 0) 0f else duration.toFloat()),
                        onValueChange = { onPositionChange(it.toLong()) },
                        onValueChangeFinished = onPositionChangeFinished,
                        colors = sliderColors,
                        isPlaying = isPlaying
                    )
                } else {
                    com.metrolist.music.ui.component.WavySlider(
                        value = position.toFloat(),
                        valueRange = 0f..(if (duration <= 0) 0f else duration.toFloat()),
                        onValueChange = { onPositionChange(it.toLong()) },
                        onValueChangeFinished = onPositionChangeFinished,
                        colors = sliderColors,
                        isPlaying = isPlaying
                    )
                }
            }
            com.metrolist.music.constants.SliderStyle.SLIM -> {
                Slider(
                    value = position.toFloat(),
                    valueRange = 0f..(if (duration <= 0) 0f else duration.toFloat()),
                    onValueChange = { onPositionChange(it.toLong()) },
                    onValueChangeFinished = onPositionChangeFinished,
                    colors = sliderColors,
                    thumb = { Spacer(modifier = Modifier.size(0.dp)) },
                    track = { sliderState ->
                        com.metrolist.music.ui.component.PlayerSliderTrack(
                            sliderState = sliderState,
                            colors = sliderColors
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Time Labels exactly matched to Pixel Music's typography
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = com.metrolist.music.utils.makeTimeString(position),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                color = textColor
            )
            Text(
                text = if (duration > 0) com.metrolist.music.utils.makeTimeString(duration) else "--:--",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                color = textColor
            )
        }
    }
}

@Composable
private fun FullPlayerControlsSection(
    context: android.content.Context,
    isPlaying: Boolean,
    isMuted: Boolean,
    isListenTogetherGuest: Boolean,
    playbackState: Int,
    canSkipPrevious: Boolean,
    canSkipNext: Boolean,
    repeatMode: Int,
    isFavorite: Boolean,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onRepeatToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    textButtonColor: Color,
    iconButtonColor: Color,
    sideButtonContainerColor: Color,
    sideButtonContentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Pixel Music's squishy transport controls
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val backInteractionSource = remember { MutableInteractionSource() }
            val nextInteractionSource = remember { MutableInteractionSource() }
            val playPauseInteractionSource = remember { MutableInteractionSource() }

            val isPlayPausePressed by playPauseInteractionSource.collectIsPressedAsState()
            val isBackPressed by backInteractionSource.collectIsPressedAsState()
            val isNextPressed by nextInteractionSource.collectIsPressedAsState()

            val playPauseWeight by animateFloatAsState(
                targetValue = if (isPlayPausePressed) 1.9f else if (isBackPressed || isNextPressed) 1.1f else 1.3f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
                label = "playPauseWeight"
            )

            val backButtonWeight by animateFloatAsState(
                targetValue = if (isBackPressed) 0.65f else if (isPlayPausePressed) 0.35f else 0.45f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
                label = "backButtonWeight"
            )

            val nextButtonWeight by animateFloatAsState(
                targetValue = if (isNextPressed) 0.65f else if (isPlayPausePressed) 0.35f else 0.45f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
                label = "nextButtonWeight"
            )

            androidx.compose.material3.FilledIconButton(
                onClick = onPrevious,
                enabled = canSkipPrevious && !isListenTogetherGuest,
                shape = RoundedCornerShape(50),
                interactionSource = backInteractionSource,
                colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                    containerColor = sideButtonContainerColor,
                    contentColor = sideButtonContentColor,
                ),
                modifier = Modifier
                    .height(68.dp)
                    .weight(backButtonWeight)
            ) {
                Icon(painterResource(R.drawable.skip_previous), null, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.material3.FilledIconButton(
                onClick = onPlayPause,
                shape = RoundedCornerShape(50),
                interactionSource = playPauseInteractionSource,
                colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                    containerColor = textButtonColor,
                    contentColor = iconButtonColor,
                ),
                modifier = Modifier
                    .height(68.dp)
                    .weight(playPauseWeight)
            ) {
                Icon(
                    painter = painterResource(
                        if (isListenTogetherGuest) {
                            if (isMuted) R.drawable.volume_off else R.drawable.volume_up
                        } else if (playbackState == Player.STATE_ENDED) {
                            R.drawable.replay
                        } else {
                            if (isPlaying) R.drawable.pause else R.drawable.play
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.material3.FilledIconButton(
                onClick = onNext,
                enabled = canSkipNext && !isListenTogetherGuest,
                shape = RoundedCornerShape(50),
                interactionSource = nextInteractionSource,
                colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                    containerColor = sideButtonContainerColor,
                    contentColor = sideButtonContentColor,
                ),
                modifier = Modifier
                    .height(68.dp)
                    .weight(nextButtonWeight)
            ) {
                Icon(painterResource(R.drawable.skip_next), null, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pixel Music's Bottom Toggle Row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(60.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val commonModifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()

                // Shuffle (Handled via playlist options natively in Metrolist, so keeping as cosmetic fallback to preserve 3-button symmetry)
                androidx.compose.material3.Surface(
                    modifier = commonModifier,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(60.dp),
                    onClick = { Toast.makeText(context, "Shuffle controlled in queue", Toast.LENGTH_SHORT).show() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.shuffle), null, tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // Repeat
                val repeatActive = repeatMode != Player.REPEAT_MODE_OFF
                androidx.compose.material3.Surface(
                    modifier = commonModifier,
                    color = if (repeatActive) MaterialTheme.colorScheme.secondary else Color.Transparent,
                    contentColor = if (repeatActive) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(60.dp),
                    onClick = onRepeatToggle
                ) {
                    val repeatIcon = when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                        Player.REPEAT_MODE_ALL -> R.drawable.repeat_on
                        else -> R.drawable.repeat
                    }
                    Box(contentAlignment = Alignment.Center) {
                        Icon(painterResource(repeatIcon), null)
                    }
                }

                // Favorite
                androidx.compose.material3.Surface(
                    modifier = commonModifier,
                    color = if (isFavorite) MaterialTheme.colorScheme.tertiary else Color.Transparent,
                    contentColor = if (isFavorite) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(60.dp),
                    onClick = onFavoriteToggle
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(painterResource(if (isFavorite) R.drawable.favorite else R.drawable.favorite_border), null)
                    }
                }
            }
        }
    }
}


