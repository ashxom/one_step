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
import kotlinx.coroutines.CancellationException

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppSettings())
    val uiState: StateFlow<AppSettings> = _uiState.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
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
        persist { it.setFontScale(value) }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(voiceEnabled = enabled)
        persist { it.setVoiceEnabled(enabled) }
    }

    fun setAccessibilityEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(accessibilityEnabled = enabled)
        persist { it.setAccessibilityEnabled(enabled) }
    }

    fun reset(localRepository: GuideLocalRepository? = null) {
        viewModelScope.launch {
            var failed = false
            try {
                repository?.reset()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Throwable) {
                failed = true
            }
            try {
                localRepository?.clearAll()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Throwable) {
                failed = true
            }
            if (failed) {
                _errorMessage.value = "설정 또는 기록을 초기화하지 못했어요. 다시 시도해 주세요."
            } else {
                _uiState.value = AppSettings()
                _errorMessage.value = null
            }
        }
    }

    private fun persist(action: suspend (SettingsRepository) -> Unit) {
        viewModelScope.launch {
            try {
                repository?.let { action(it) }
                _errorMessage.value = null
            } catch (error: CancellationException) {
                throw error
            } catch (_: Throwable) {
                _errorMessage.value = "설정을 저장하지 못했어요. 다시 시도해 주세요."
            }
        }
    }
}
