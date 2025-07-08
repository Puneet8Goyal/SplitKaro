package com.puneet8goyal.splitkaro.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// RESPONSIVE SPACING SYSTEM - ADD THIS
object ResponsiveSpacing {
    @Composable
    fun xs() = with(LocalDensity.current) { 4.dp }

    @Composable
    fun sm() = with(LocalDensity.current) { 8.dp }

    @Composable
    fun md() = with(LocalDensity.current) { 12.dp }

    @Composable
    fun lg() = with(LocalDensity.current) { 16.dp }

    @Composable
    fun xl() = with(LocalDensity.current) { 20.dp }

    @Composable
    fun xxl() = with(LocalDensity.current) { 24.dp }

    @Composable
    fun xxxl() = with(LocalDensity.current) { 32.dp }

    @Composable
    fun huge() = with(LocalDensity.current) { 40.dp }

    // Screen-size adaptive spacing
    @Composable
    fun adaptiveHorizontal(): Dp {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        return when {
            screenWidth < 360.dp -> 12.dp  // Small phones
            screenWidth < 480.dp -> 16.dp  // Normal phones
            screenWidth < 600.dp -> 20.dp  // Large phones
            screenWidth < 840.dp -> 24.dp  // Small tablets
            else -> 32.dp                  // Large tablets
        }
    }

    @Composable
    fun adaptiveVertical(): Dp {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        return when {
            screenHeight < 640.dp -> 8.dp   // Short screens
            screenHeight < 800.dp -> 12.dp  // Normal screens
            else -> 16.dp                   // Tall screens
        }
    }
}

// Modern Typography System (2025 standards)
val ModernTypography = androidx.compose.material3.Typography(
    // Display styles - for large headers
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.25).sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // Headline styles - for section headers
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    // Title styles - for card titles and important text
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    // Body styles - for main content
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    // Label styles - for buttons and small text
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

// Enhanced Color Scheme
data class SplitKaroColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val primary: Color,
    val primaryLight: Color,
    val primaryDark: Color,
    val primaryContainer: Color,
    val onPrimary: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val onSecondary: Color,
    val onSecondaryContainer: Color,
    val success: Color,
    val successLight: Color,
    val successContainer: Color,
    val onSuccess: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val warningLight: Color,
    val warningContainer: Color,
    val onWarning: Color,
    val onWarningContainer: Color,
    val error: Color,
    val errorLight: Color,
    val errorContainer: Color,
    val onError: Color,
    val onErrorContainer: Color,
    val info: Color,
    val infoLight: Color,
    val infoContainer: Color,
    val onInfo: Color,
    val onInfoContainer: Color,
    val accent1: Color,
    val accent2: Color,
    val accent3: Color,
    val accent4: Color,
    val border: Color,
    val borderVariant: Color,
    val shadow: Color,
    val scrim: Color,
    val gray50: Color,
    val gray100: Color,
    val gray200: Color,
    val gray300: Color,
    val gray400: Color,
    val gray500: Color,
    val gray600: Color,
    val gray700: Color,
    val gray800: Color,
    val gray900: Color
)

// Modern spacing system
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 40.dp
}

// Modern radius system
object Radius {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val round = 50.dp
}

private val DarkColors = SplitKaroColors(
    background = Color(0xFF0A0A0F),
    surface = Color(0xFF12121A),
    surfaceVariant = Color(0xFF1A1A24),
    surfaceElevated = Color(0xFF1F1F2B),
    surfaceContainer = Color(0xFF161621),
    surfaceContainerHigh = Color(0xFF202030),
    onSurface = Color(0xFFF5F5F7),
    onSurfaceVariant = Color(0xFFB8BBC5),
    primary = Color(0xFF4F7CE8),
    primaryLight = Color(0xFF6B8EEB),
    primaryDark = Color(0xFF3366E0),
    primaryContainer = Color(0xFF1A2332),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFFD4E3FF),
    secondary = Color(0xFF7C7F88),
    secondaryContainer = Color(0xFF232530),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFFE3E4E9),
    success = Color(0xFF34D399),
    successLight = Color(0xFF6EE7B7),
    successContainer = Color(0xFF0F291E),
    onSuccess = Color(0xFF000000),
    onSuccessContainer = Color(0xFFD1FAE5),
    warning = Color(0xFFFBBF24),
    warningLight = Color(0xFFFCD34D),
    warningContainer = Color(0xFF332A0A),
    onWarning = Color(0xFF000000),
    onWarningContainer = Color(0xFFFEF3C7),
    error = Color(0xFFEF4444),
    errorLight = Color(0xFFF87171),
    errorContainer = Color(0xFF3F1515),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFFFEE2E2),
    info = Color(0xFF3B82F6),
    infoLight = Color(0xFF60A5FA),
    infoContainer = Color(0xFF1E293B),
    onInfo = Color(0xFFFFFFFF),
    onInfoContainer = Color(0xFFDBEAFE),
    accent1 = Color(0xFF8B5CF6),
    accent2 = Color(0xFFEC4899),
    accent3 = Color(0xFF06B6D4),
    accent4 = Color(0xFFF59E0B),
    border = Color(0xFF2D2D3A),
    borderVariant = Color(0xFF1F1F2B),
    shadow = Color(0xFF000000),
    scrim = Color(0xFF000000),
    gray50 = Color(0xFFF9FAFB),
    gray100 = Color(0xFFF3F4F6),
    gray200 = Color(0xFFE5E7EB),
    gray300 = Color(0xFFD1D5DB),
    gray400 = Color(0xFF9CA3AF),
    gray500 = Color(0xFF6B7280),
    gray600 = Color(0xFF4B5563),
    gray700 = Color(0xFF374151),
    gray800 = Color(0xFF1F2937),
    gray900 = Color(0xFF111827)
)

private val LightColors = SplitKaroColors(
    background = Color(0xFFFCFCFE),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF8F9FC),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF5F6FA),
    surfaceContainerHigh = Color(0xFFEEEFF4),
    onSurface = Color(0xFF0F0F14),
    onSurfaceVariant = Color(0xFF4A4D57),
    primary = Color(0xFF3366E0),
    primaryLight = Color(0xFF4F7CE8),
    primaryDark = Color(0xFF1A4CB8),
    primaryContainer = Color(0xFFEBF2FF),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF001B3F),
    secondary = Color(0xFF575A63),
    secondaryContainer = Color(0xFFDCDEE8),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF14161F),
    success = Color(0xFF059669),
    successLight = Color(0xFF10B981),
    successContainer = Color(0xFFD1FAE5),
    onSuccess = Color(0xFFFFFFFF),
    onSuccessContainer = Color(0xFF002114),
    warning = Color(0xFFD97706),
    warningLight = Color(0xFFF59E0B),
    warningContainer = Color(0xFFFEF3C7),
    onWarning = Color(0xFFFFFFFF),
    onWarningContainer = Color(0xFF451A03),
    error = Color(0xFFDC2626),
    errorLight = Color(0xFFEF4444),
    errorContainer = Color(0xFFFEE2E2),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF7F1D1D),
    info = Color(0xFF0284C7),
    infoLight = Color(0xFF0EA5E9),
    infoContainer = Color(0xFFE0F2FE),
    onInfo = Color(0xFFFFFFFF),
    onInfoContainer = Color(0xFF0C4A6E),
    accent1 = Color(0xFF7C3AED),
    accent2 = Color(0xFFDB2777),
    accent3 = Color(0xFF0891B2),
    accent4 = Color(0xFFEA580C),
    border = Color(0xFFE2E5EA),
    borderVariant = Color(0xFFF0F2F5),
    shadow = Color(0xFF000000).copy(alpha = 0.04f),
    scrim = Color(0xFF000000),
    gray50 = Color(0xFFF9FAFB),
    gray100 = Color(0xFFF3F4F6),
    gray200 = Color(0xFFE5E7EB),
    gray300 = Color(0xFFD1D5DB),
    gray400 = Color(0xFF9CA3AF),
    gray500 = Color(0xFF6B7280),
    gray600 = Color(0xFF4B5563),
    gray700 = Color(0xFF374151),
    gray800 = Color(0xFF1F2937),
    gray900 = Color(0xFF111827)
)

val LocalSplitKaroColors = staticCompositionLocalOf { LightColors }

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4F7CE8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1A2332),
    onPrimaryContainer = Color(0xFFD4E3FF),
    secondary = Color(0xFF7C7F88),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF232530),
    onSecondaryContainer = Color(0xFFE3E4E9),
    tertiary = Color(0xFF8B5CF6),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF2D1B4E),
    onTertiaryContainer = Color(0xFFE9D7FE),
    error = Color(0xFFEF4444),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF3F1515),
    onErrorContainer = Color(0xFFEE2E2),
    background = Color(0xFF0A0A0F),
    onBackground = Color(0xFFF5F5F7),
    surface = Color(0xFF12121A),
    onSurface = Color(0xFFF5F5F7),
    surfaceVariant = Color(0xFF1A1A24),
    onSurfaceVariant = Color(0xFFB8BBC5),
    outline = Color(0xFF2D2D3A),
    outlineVariant = Color(0xFF1F1F2B),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFF5F5F7),
    inverseOnSurface = Color(0xFF0A0A0F),
    inversePrimary = Color(0xFF3366E0),
    surfaceDim = Color(0xFF0F0F16),
    surfaceBright = Color(0xFF1F1F2B),
    surfaceContainerLowest = Color(0xFF0A0A0F),
    surfaceContainerLow = Color(0xFF12121A),
    surfaceContainer = Color(0xFF161621),
    surfaceContainerHigh = Color(0xFF202030),
    surfaceContainerHighest = Color(0xFF2A2A3B)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3366E0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEBF2FF),
    onPrimaryContainer = Color(0xFF001B3F),
    secondary = Color(0xFF575A63),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCDEE8),
    onSecondaryContainer = Color(0xFF14161F),
    tertiary = Color(0xFF7C3AED),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE9D7FE),
    onTertiaryContainer = Color(0xFF2D1B4E),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    background = Color(0xFFFCFCFE),
    onBackground = Color(0xFF0F0F14),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F0F14),
    surfaceVariant = Color(0xFFF8F9FC),
    onSurfaceVariant = Color(0xFF4A4D57),
    outline = Color(0xFFE2E5EA),
    outlineVariant = Color(0xFFF0F2F5),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF0F0F14),
    inverseOnSurface = Color(0xFFFCFCFE),
    inversePrimary = Color(0xFF4F7CE8),
    surfaceDim = Color(0xFFDEDFE4),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F9FC),
    surfaceContainer = Color(0xFFF5F6FA),
    surfaceContainerHigh = Color(0xFFEEEFF4),
    surfaceContainerHighest = Color(0xFFE8E9EF)
)

@Composable
fun SplitKaroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val splitKaroColors = if (darkTheme) DarkColors else LightColors

    CompositionLocalProvider(LocalSplitKaroColors provides splitKaroColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ModernTypography,
            content = content
        )
    }
}

// Extension to access our custom colors
object AppTheme {
    val colors: SplitKaroColors
        @Composable
        get() = LocalSplitKaroColors.current

    val spacing: Spacing
        @Composable
        get() = Spacing

    val radius: Radius
        @Composable
        get() = Radius
}
