package id.heula.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import heula.composeapp.generated.resources.Res
import heula.composeapp.generated.resources.inter_bold
import heula.composeapp.generated.resources.inter_medium
import heula.composeapp.generated.resources.inter_regular
import heula.composeapp.generated.resources.inter_semibold
import id.heula.domain.model.Theme
import org.jetbrains.compose.resources.Font

// iOS System Colors
val BrandPrimary = Color(0xFF34C759) // iOS System Green
val BrandPrimaryDark = Color(0xFF30D158) // iOS System Green (Dark)
val BrandSoftLight = Color(0xFFE8F8EE)
val BrandSoftDark = Color(0xFF143823)
val DangerRed = Color(0xFFFF3B30) // iOS System Red
val DangerRedDark = Color(0xFFFF453A)

// iOS Light Palette (HIG Grouped Background)
val LightBackground = Color(0xFFF2F2F7) // iOS System Grouped Background
val LightSurface = Color(0xFFFFFFFF) // iOS Card / Elevated Surface
val LightSurface2 = Color(0xFFE5E5EA) // iOS System Gray 5
val LightBorder = Color(0xFFE5E5EA)
val LightContent = Color(0xFF000000)
val LightContentMuted = Color(0xFF8E8E93) // iOS System Gray

// iOS Dark Palette (HIG Dark Grouped Background)
val DarkBackground = Color(0xFF000000) // iOS Pure OLED Black
val DarkSurface = Color(0xFF1C1C1E) // iOS Secondary Grouped
val DarkSurface2 = Color(0xFF2C2C2E) // iOS Tertiary Grouped
val DarkBorder = Color(0xFF38383A)
val DarkContent = Color(0xFFFFFFFF)
val DarkContentMuted = Color(0xFF8E8E93)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandSoftLight,
    onPrimaryContainer = BrandPrimary,
    background = LightBackground,
    onBackground = LightContent,
    surface = LightSurface,
    onSurface = LightContent,
    surfaceVariant = LightSurface2,
    onSurfaceVariant = LightContentMuted,
    outline = LightBorder,
    error = DangerRed,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = BrandSoftDark,
    onPrimaryContainer = Color(0xFF86EFAC),
    background = DarkBackground,
    onBackground = DarkContent,
    surface = DarkSurface,
    onSurface = DarkContent,
    surfaceVariant = DarkSurface2,
    onSurfaceVariant = DarkContentMuted,
    outline = DarkBorder,
    error = DangerRedDark,
    onError = Color.White,
)

@Composable
fun getAppFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_medium, FontWeight.Medium),
        Font(Res.font.inter_semibold, FontWeight.SemiBold),
        Font(Res.font.inter_bold, FontWeight.Bold),
    )
}

@Composable
fun getIosTypography(): Typography {
    val fontFamily = getAppFontFamily()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            letterSpacing = (-0.4).sp,
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            letterSpacing = (-0.4).sp,
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            letterSpacing = (-0.3).sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            letterSpacing = (-0.2).sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            letterSpacing = (-0.4).sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            letterSpacing = (-0.4).sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            letterSpacing = (-0.2).sp,
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            letterSpacing = (-0.1).sp,
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            letterSpacing = (-0.2).sp,
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            letterSpacing = 0.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            letterSpacing = 0.1.sp,
        ),
    )
}

@Composable
fun HeulaTheme(
    userTheme: Theme = Theme.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (userTheme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getIosTypography(),
        content = content,
    )
}
