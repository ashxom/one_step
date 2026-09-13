package com.example.one_step.domain.repository

import com.example.one_step.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setFontScale(fontScale: Float)
    suspend fun setVoiceEnabled(enabled: Boolean)
    suspend fun setAccessibilityEnabled(enabled: Boolean)
    suspend fun reset()
}
