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
    primary = DiaryPrimary,
    onPrimary = Color.White,
    background = DiaryBackgroundLight,
    onBackground = DiaryPrimary,
    surface = DiaryCardLight,
    onSurface = DiaryPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Slate600,
    outline = DiaryBorderLight,
    outlineVariant = DiaryBorderLight,
    secondary = Slate500,
    onSecondary = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF1F5F9),
    onPrimary = DiaryBackgroundDark,
    background = DiaryBackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = DiaryCardDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate400,
    outline = DiaryBorderDark,
    outlineVariant = DiaryBorderDark,
    secondary = Slate400,
    onSecondary = DiaryBackgroundDark,
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
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                context
            )
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