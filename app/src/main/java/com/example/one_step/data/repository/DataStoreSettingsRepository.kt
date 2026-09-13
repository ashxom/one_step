package com.example.one_step.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.one_step.domain.model.AppSettings
import com.example.one_step.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "one_step_settings")

class DataStoreSettingsRepository(private val context: Context) : SettingsRepository {
    override val settings: Flow<AppSettings> = context.settingsDataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            AppSettings(
                fontScale = preferences[FONT_SCALE] ?: 1f,
                voiceEnabled = preferences[VOICE_ENABLED] ?: true,
                accessibilityEnabled = preferences[ACCESSIBILITY_ENABLED] ?: false,
            )
        }

    override suspend fun setFontScale(fontScale: Float) {
        context.settingsDataStore.edit { it[FONT_SCALE] = fontScale.coerceIn(0.85f, 1.3f) }
    }

    override suspend fun setVoiceEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[VOICE_ENABLED] = enabled }
    }

    override suspend fun setAccessibilityEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[ACCESSIBILITY_ENABLED] = enabled }
    }

    override suspend fun reset() {
        context.settingsDataStore.edit { it.clear() }
    }

    private companion object {
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val ACCESSIBILITY_ENABLED = booleanPreferencesKey("accessibility_enabled")
    }
}
