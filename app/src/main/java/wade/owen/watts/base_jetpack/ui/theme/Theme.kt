package wade.owen.watts.base_jetpack.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary            = DiaryForegroundLight,
    onPrimary          = DiaryCardLight,
    background         = DiaryBackgroundLight,
    onBackground       = DiaryForegroundLight,
    surface            = DiaryCardLight,
    onSurface          = DiaryForegroundLight,
    surfaceVariant     = DiarySecondaryLight,   // $--secondary: button/input backgrounds
    onSurfaceVariant   = DiaryMutedFgLight,      // $--muted-foreground: secondary text
    outline            = DiaryBorderLight,
    outlineVariant     = DiaryBorderLight,
    secondary          = DiaryMutedFgLight,
    onSecondary        = DiaryCardLight,
    error              = DiaryDestructiveLight,
    onError            = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary            = DiaryForegroundDark,
    onPrimary          = DiaryCardDark,
    background         = DiaryBackgroundDark,
    onBackground       = DiaryForegroundDark,
    surface            = DiaryCardDark,
    onSurface          = DiaryForegroundDark,
    surfaceVariant     = DiarySecondaryDark,    // $--secondary: button/input backgrounds
    onSurfaceVariant   = DiaryMutedFgDark,       // $--muted-foreground: secondary text
    outline            = DiaryBorderDark,
    outlineVariant     = DiaryBorderDark,
    secondary          = DiaryMutedFgDark,
    onSecondary        = DiaryCardDark,
    error              = DiaryDestructiveDark,
    onError            = DiaryBackgroundDark,
)

@Composable
fun Jetpack_compose_mvvmTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = lightTypography,
        content = content
    )
}
