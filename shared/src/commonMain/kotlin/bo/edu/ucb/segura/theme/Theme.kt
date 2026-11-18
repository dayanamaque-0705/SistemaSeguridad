package bo.edu.ucb.segura.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val UcbDarkColorScheme = darkColorScheme(
    primary = UcbBlueAccent,
    onPrimary = UcbTextPrimary,
    primaryContainer = UcbNavyPrimary,
    onPrimaryContainer = UcbTextPrimary,
    secondary = UcbGold,
    onSecondary = UcbNavyDark,
    secondaryContainer = UcbChipDark,
    onSecondaryContainer = UcbGold,
    tertiary = UcbBlueSecondary,
    background = UcbNavyDark,
    onBackground = UcbTextPrimary,
    surface = UcbSurfaceDark,
    onSurface = UcbTextPrimary,
    surfaceVariant = UcbCardDark,
    onSurfaceVariant = UcbTextSecondary,
    error = UcbRedAlert,
    onError = UcbTextPrimary,
    errorContainer = UcbRedAlertBackground,
    onErrorContainer = UcbRedAlertPulse,
    outline = UcbBorderDark
)

@Composable
fun UcbSeguraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = UcbDarkColorScheme,
        content = content
    )
}
