package com.example.one_step.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.one_step.domain.model.AppSettings
import com.example.one_step.domain.repository.GuideLocalRepository
import com.example.one_step.domain.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppSettings())
    val uiState: StateFlow<AppSettings> = _uiState.asStateFlow()
    private var repository: SettingsRepository? = null
    private var observeJob: Job? = null

    fun attachRepository(settingsRepository: SettingsRepository) {
        if (repository === settingsRepository) return
        repository = settingsRepository
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            settingsRepository.settings.collect { _uiState.value = it }
        }
    }

    fun setFontScale(value: Float) {
        _uiState.value = _uiState.value.copy(fontScale = value)
        viewModelScope.launch { runCatching { repository?.setFontScale(value) } }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(voiceEnabled = enabled)
        viewModelScope.launch { runCatching { repository?.setVoiceEnabled(enabled) } }
    }

    fun setAccessibilityEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(accessibilityEnabled = enabled)
        viewModelScope.launch { runCatching { repository?.setAccessibilityEnabled(enabled) } }
    }

    fun reset(localRepository: GuideLocalRepository? = null) {
        viewModelScope.launch {
            runCatching { repository?.reset() }
            runCatching { localRepository?.clearAll() }
            _uiState.value = AppSettings()
        }
    }
}
