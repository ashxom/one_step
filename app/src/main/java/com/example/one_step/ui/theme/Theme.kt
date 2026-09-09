package com.example.one_step.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = OneStepBlue,
    onPrimary = OneStepSurface,
    primaryContainer = OneStepBlueSoft,
    onPrimaryContainer = OneStepBlue,
    secondary = OneStepSuccess,
    background = OneStepBackground,
    onBackground = OneStepText,
    surface = OneStepSurface,
    onSurface = OneStepText,
    surfaceVariant = OneStepBlueSoft,
    onSurfaceVariant = OneStepTextMuted,
    outline = OneStepBorder,
)

@Composable
fun One_stepTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
