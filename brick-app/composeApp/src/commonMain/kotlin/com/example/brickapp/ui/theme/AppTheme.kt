
package com.example.brickapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- Brand Colors ---
private val BrickYellow = Color(0xFFFFD700)
private val BrickOrange = Color(0xFFFF8C00)
private val BrickRed = Color(0xFFE53935)
private val LightSurface = Color(0xFFF8F9FA)
private val DarkSurface = Color(0xFF121212)
private val DarkSurfaceVariant = Color(0xFF1E1E2E)
private val DarkCard = Color(0xFF2A2A3C)

private val DarkColorScheme = darkColorScheme(
    primary = BrickYellow,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3A3A00),
    onPrimaryContainer = BrickYellow,
    secondary = BrickOrange,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3A2400),
    onSecondaryContainer = BrickOrange,
    tertiary = BrickRed,
    onTertiary = Color.White,
    background = DarkSurface,
    onBackground = Color(0xFFE6E1E5),
    surface = DarkSurfaceVariant,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFCF6679),
    onError = Color.Black,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD4A017),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF3CD),
    onPrimaryContainer = Color(0xFF3A2C00),
    secondary = BrickOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF3A2400),
    tertiary = BrickRed,
    onTertiary = Color.White,
    background = LightSurface,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF3EDF7),
    onSurfaceVariant = Color(0xFF49454F),
)

@Composable
fun BrickAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
