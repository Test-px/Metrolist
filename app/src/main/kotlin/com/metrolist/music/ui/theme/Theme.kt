package com.metrolist.music.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

// Default Theme Color: PixelMusic's soothing Sage Green
val DefaultThemeColor = Color(0xFF6EDBB1)

// --- Sage Green / Mint Palette (Soothing Default) ---
val SageDarkBackground = Color(0xFF0D1210)
val SageDarkSurface = Color(0xFF151B18)
val SageDarkPrimary = Color(0xFF6EDBB1)
val SageDarkSecondary = Color(0xFF4E9A7E)
val SageDarkTertiary = Color(0xFF8AC7AC)
val SageDarkOnPrimary = Color(0xFF003824)
val SageDarkOnBackground = Color(0xFFE1E3DF)
val SageDarkOnSurface = Color(0xFFE1E3DF)
val SageDarkOnSurfaceVariant = Color(0xFFBFC9C2)

val SageLightBackground = Color(0xFFF3FAF6)
val SageLightSurface = Color(0xFFF7FCFA)
val SageLightPrimary = Color(0xFF1F6C50)
val SageLightOnPrimary = Color(0xFFFFFFFF)
val SageLightPrimaryContainer = Color(0xFFC4F2DB)
val SageLightOnPrimaryContainer = Color(0xFF002114)
val SageLightSecondary = Color(0xFF4C6357)
val SageLightSecondaryContainer = Color(0xFFCEE9DB)
val SageLightOnSecondaryContainer = Color(0xFF092016)
val SageLightTertiary = Color(0xFF3F6555)
val SageLightOnBackground = Color(0xFF191D1A)
val SageLightOnSurface = Color(0xFF191D1A)
val SageLightSurfaceVariant = Color(0xFFDCE5DE)
val SageLightOnSurfaceVariant = Color(0xFF404944)
val SageLightOutline = Color(0xFF707973)

val SageDarkColorScheme = darkColorScheme(
    primary = SageDarkPrimary,
    secondary = SageDarkSecondary,
    tertiary = SageDarkTertiary,
    background = SageDarkBackground,
    surface = SageDarkSurface,
    onPrimary = SageDarkOnPrimary,
    onSecondary = SageDarkOnPrimary,
    onTertiary = SageDarkOnPrimary,
    onBackground = SageDarkOnBackground,
    onSurface = SageDarkOnSurface,
    onSurfaceVariant = SageDarkOnSurfaceVariant,
    error = Color(0xFFFF5252),
    onError = Color.White
)

val SageLightColorScheme = lightColorScheme(
    primary = SageLightPrimary,
    onPrimary = SageLightOnPrimary,
    primaryContainer = SageLightPrimaryContainer,
    onPrimaryContainer = SageLightOnPrimaryContainer,
    secondary = SageLightSecondary,
    onSecondary = SageLightOnPrimary,
    secondaryContainer = SageLightSecondaryContainer,
    onSecondaryContainer = SageLightOnSecondaryContainer,
    tertiary = SageLightTertiary,
    onTertiary = Color.Black,
    background = SageLightBackground,
    onBackground = SageLightOnBackground,
    surface = SageLightSurface,
    onSurface = SageLightOnSurface,
    surfaceVariant = SageLightSurfaceVariant,
    onSurfaceVariant = SageLightOnSurfaceVariant,
    outline = SageLightOutline,
    outlineVariant = SageLightOutline.copy(alpha = 0.6f),
    surfaceTint = SageLightPrimary,
    error = Color(0xFFD32F2F),
    onError = Color.White
)

// --- Classic Purple Palette ---
val PurpleDarkColorScheme = darkColorScheme(
    primary = Color(0xFFB29BF4),
    secondary = Color(0xFFE57399),
    tertiary = Color(0xFFD4B2F7),
    background = Color(0xFF0A0714),
    surface = Color(0xFF13101E),
    onPrimary = Color(0xFF280066),
    onSecondary = Color(0xFF280066),
    onTertiary = Color(0xFF280066),
    onBackground = Color(0xFFE7E3EC),
    onSurface = Color(0xFFE7E3EC),
    onSurfaceVariant = Color(0xFFC9C4D0),
    error = Color(0xFFFF5252),
    onError = Color.White
)

val PurpleLightColorScheme = lightColorScheme(
    primary = Color(0xFF6C4FBB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9E3FB),
    onPrimaryContainer = Color(0xFF1F005C),
    secondary = Color(0xFF825272),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD8EC),
    onSecondaryContainer = Color(0xFF370B2C),
    tertiary = Color(0xFF705574),
    onTertiary = Color.Black,
    background = Color(0xFFF9F7FC),
    onBackground = Color(0xFF1C1A22),
    surface = Color(0xFFFAF9FC),
    onSurface = Color(0xFF1C1A22),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF7A757F),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

// --- Slate Blue Palette ---
val BlueDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7DB0E6),
    secondary = Color(0xFF5A84B0),
    tertiary = Color(0xFF8AB9E6),
    background = Color(0xFF0B0F14),
    surface = Color(0xFF12171E),
    onPrimary = Color(0xFF00315C),
    onSecondary = Color(0xFF00315C),
    onTertiary = Color(0xFF00315C),
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6),
    onSurfaceVariant = Color(0xFFC2C7CF),
    error = Color(0xFFFF5252),
    onError = Color.White
)

val BlueLightColorScheme = lightColorScheme(
    primary = Color(0xFF22588F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC4DEF6),
    onPrimaryContainer = Color(0xFF001C3A),
    secondary = Color(0xFF436080),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC9E2FF),
    onSecondaryContainer = Color(0xFF001D38),
    tertiary = Color(0xFF3E6080),
    onTertiary = Color.Black,
    background = Color(0xFFF3F7FA),
    onBackground = Color(0xFF191C1E),
    surface = Color(0xFFF7FAFC),
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFDFE2E7),
    onSurfaceVariant = Color(0xFF43474B),
    outline = Color(0xFF73777C),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

// --- Sunset Orange Palette ---
val OrangeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF5A873),
    secondary = Color(0xFFB37D56),
    tertiary = Color(0xFFF7BE98),
    background = Color(0xFF120E0A),
    surface = Color(0xFF1A1510),
    onPrimary = Color(0xFF4C1E00),
    onSecondary = Color(0xFF4C1E00),
    onTertiary = Color(0xFF4C1E00),
    onBackground = Color(0xFFECE1DB),
    onSurface = Color(0xFFECE1DB),
    onSurfaceVariant = Color(0xFFD7C4B7),
    error = Color(0xFFFF5252),
    onError = Color.White
)

val OrangeLightColorScheme = lightColorScheme(
    primary = Color(0xFF8F4F20),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFADFC9),
    onPrimaryContainer = Color(0xFF341100),
    secondary = Color(0xFF7E5233),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBC6),
    onSecondaryContainer = Color(0xFF301400),
    tertiary = Color(0xFF79563C),
    onTertiary = Color.Black,
    background = Color(0xFFFAF6F2),
    onBackground = Color(0xFF221A15),
    surface = Color(0xFFFCFAF7),
    onSurface = Color(0xFF221A15),
    surfaceVariant = Color(0xFFF4DFD0),
    onSurfaceVariant = Color(0xFF52443C),
    outline = Color(0xFF85736B),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

// --- Monochrome Yellow Palette ---
val YellowDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFC436),
    secondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFCCCCCC),
    background = Color(0xFF000000),
    surface = Color(0xFF141414),
    onPrimary = Color(0xFF221500),
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFFF0F0F0),
    onSurface = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFFAAAAAA),
    error = Color(0xFFFF5252),
    onError = Color.White
)

val YellowLightColorScheme = lightColorScheme(
    primary = Color(0xFFFFC436),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFFF3D6),
    onPrimaryContainer = Color(0xFF3E2723),
    secondary = Color(0xFF111111),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEAEAEA),
    onSecondaryContainer = Color(0xFF111111),
    tertiary = Color(0xFF757575),
    onTertiary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF111111),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFF4F4F4),
    onSurfaceVariant = Color(0xFF666666),
    outline = Color(0xFFCCCCCC),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Suppress("DEPRECATION")
@Composable
fun PixelMusicStatusBarStyle(
    color: Color,
    useDarkIcons: Boolean = ColorUtils.calculateLuminance(color.toArgb()) > 0.55,
    navigationColor: Color? = null,
    useDarkNavigationIcons: Boolean = navigationColor
        ?.let { ColorUtils.calculateLuminance(it.toArgb()) > 0.55 }
        ?: useDarkIcons
) {
    val view = LocalView.current
    if (view.isInEditMode) return

    val updateNavigationBar = navigationColor != null
    SideEffect {
        val window = view.context.findActivity()?.window ?: return@SideEffect
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
        }

        WindowCompat.getInsetsController(window, view).run {
            isAppearanceLightStatusBars = useDarkIcons

            if (updateNavigationBar) {
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                isAppearanceLightNavigationBars = useDarkNavigationIcons
            }
        }
    }
}

@Composable
fun MetrolistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val useSystemDynamicColor = (themeColor == Color.Transparent && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

    val baseColorScheme = when {
        useSystemDynamicColor -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeColor == DefaultThemeColor -> {
            if (darkTheme) SageDarkColorScheme else SageLightColorScheme
        }
        else -> {
            rememberDynamicColorScheme(
                seedColor = themeColor,
                isDark = darkTheme,
                specVersion = ColorSpec.SpecVersion.SPEC_2025,
                style = PaletteStyle.TonalSpot
            )
        }
    }

    val colorScheme = remember(baseColorScheme, pureBlack, darkTheme) {
        if (darkTheme && pureBlack) {
            baseColorScheme.pureBlack(true)
        } else {
            baseColorScheme
        }
    }

    PixelMusicStatusBarStyle(
        color = colorScheme.background,
        navigationColor = colorScheme.background
    )

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes,
        content = content,
    )
}

/** Stepped Pure AMOLED Black container mapping from PixelMusic */
fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        background = Color.Black,
        surface = Color.Black,
        surfaceVariant = Color(0xFF0A0A0A),
        surfaceContainerLowest = Color.Black,
        surfaceContainerLow = Color(0xFF0A0A0A),
        surfaceContainer = Color(0xFF141414),
        surfaceContainerHigh = Color(0xFF1F1F1F),
        surfaceContainerHighest = Color(0xFF292929)
    ) else this

@Composable
fun rememberAnimatedColorScheme(
    target: ColorScheme,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 650, easing = FastOutSlowInEasing)
): ColorScheme {
    val progress = remember { Animatable(1f) }
    var fromScheme by remember { mutableStateOf(target) }
    var toScheme by remember { mutableStateOf(target) }

    LaunchedEffect(target) {
        if (toScheme == target && progress.value == 1f) return@LaunchedEffect
        fromScheme = lerpColorScheme(fromScheme, toScheme, progress.value)
        toScheme = target
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec)
    }

    val interpolated by remember {
        derivedStateOf { lerpColorScheme(fromScheme, toScheme, progress.value) }
    }
    return interpolated
}

fun lerpColorScheme(from: ColorScheme, to: ColorScheme, t: Float): ColorScheme =
    to.copy(
        primary = lerp(from.primary, to.primary, t),
        onPrimary = lerp(from.onPrimary, to.onPrimary, t),
        primaryContainer = lerp(from.primaryContainer, to.primaryContainer, t),
        onPrimaryContainer = lerp(from.onPrimaryContainer, to.onPrimaryContainer, t),
        inversePrimary = lerp(from.inversePrimary, to.inversePrimary, t),
        secondary = lerp(from.secondary, to.secondary, t),
        onSecondary = lerp(from.onSecondary, to.onSecondary, t),
        secondaryContainer = lerp(from.secondaryContainer, to.secondaryContainer, t),
        onSecondaryContainer = lerp(from.onSecondaryContainer, to.onSecondaryContainer, t),
        tertiary = lerp(from.tertiary, to.tertiary, t),
        onTertiary = lerp(from.onTertiary, to.onTertiary, t),
        tertiaryContainer = lerp(from.tertiaryContainer, to.tertiaryContainer, t),
        onTertiaryContainer = lerp(from.onTertiaryContainer, to.onTertiaryContainer, t),
        background = lerp(from.background, to.background, t),
        onBackground = lerp(from.onBackground, to.onBackground, t),
        surface = lerp(from.surface, to.surface, t),
        onSurface = lerp(from.onSurface, to.onSurface, t),
        surfaceVariant = lerp(from.surfaceVariant, to.surfaceVariant, t),
        onSurfaceVariant = lerp(from.onSurfaceVariant, to.onSurfaceVariant, t),
        surfaceTint = lerp(from.surfaceTint, to.surfaceTint, t),
        inverseSurface = lerp(from.inverseSurface, to.inverseSurface, t),
        inverseOnSurface = lerp(from.inverseOnSurface, to.inverseOnSurface, t),
        error = lerp(from.error, to.error, t),
        onError = lerp(from.onError, to.onError, t),
        errorContainer = lerp(from.errorContainer, to.errorContainer, t),
        onErrorContainer = lerp(from.onErrorContainer, to.onErrorContainer, t),
        outline = lerp(from.outline, to.outline, t),
        outlineVariant = lerp(from.outlineVariant, to.outlineVariant, t),
        scrim = lerp(from.scrim, to.scrim, t),
        surfaceBright = lerp(from.surfaceBright, to.surfaceBright, t),
        surfaceDim = lerp(from.surfaceDim, to.surfaceDim, t),
        surfaceContainer = lerp(from.surfaceContainer, to.surfaceContainer, t),
        surfaceContainerHigh = lerp(from.surfaceContainerHigh, to.surfaceContainerHigh, t),
        surfaceContainerHighest = lerp(from.surfaceContainerHighest, to.surfaceContainerHighest, t),
        surfaceContainerLow = lerp(from.surfaceContainerLow, to.surfaceContainerLow, t),
        surfaceContainerLowest = lerp(from.surfaceContainerLowest, to.surfaceContainerLowest, t)
    )

fun Bitmap.extractThemeColor(): Color = Color(
    Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .rankedColors(1, DefaultThemeColor.toArgb())
        .first()
)

internal fun Palette.rankedColors(
    desiredColorCount: Int,
    fallbackColor: Int,
): List<Int> = Score.score(
    swatches.associate { it.rgb to it.population },
    desiredColorCount,
    fallbackColor,
    true,
)

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
