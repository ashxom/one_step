package com.example.one_step.ui.guide

import androidx.lifecycle.ViewModel
import com.example.one_step.domain.model.ActionItem
import com.example.one_step.domain.model.AnalysisResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface GuideUiState {
    data object Empty : GuideUiState

    data class Running(
        val result: AnalysisResult,
        val currentIndex: Int,
        val completedIds: Set<String> = emptySet(),
        val isPaused: Boolean = false,
        val isSpeaking: Boolean = false,
    ) : GuideUiState

    data class Completed(
        val result: AnalysisResult,
        val completedActions: List<ActionItem>,
    ) : GuideUiState
}

class GuideViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<GuideUiState>(GuideUiState.Empty)
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

    fun start(result: AnalysisResult) {
        if (result.actions.isEmpty()) {
            _uiState.value = GuideUiState.Completed(result, emptyList())
        } else {
            _uiState.value = GuideUiState.Running(result, currentIndex = 0)
        }
    }

    fun completeCurrent() {
        val state = _uiState.value as? GuideUiState.Running ?: return
        val action = state.result.actions.getOrNull(state.currentIndex) ?: return
        val completedIds = state.completedIds + action.id
        if (state.result.actions.all { it.id in completedIds }) {
            _uiState.value = GuideUiState.Completed(state.result, state.result.actions)
        } else {
            val nextIndex = if (state.currentIndex == state.result.actions.lastIndex) {
                state.result.actions.indexOfFirst { it.id !in completedIds }.coerceAtLeast(0)
            } else {
                state.currentIndex + 1
            }
            _uiState.value = state.copy(
                currentIndex = nextIndex,
                completedIds = completedIds,
                isPaused = false,
                isSpeaking = false,
            )
        }
    }

    fun previous() {
        val state = _uiState.value as? GuideUiState.Running ?: return
        if (state.currentIndex > 0) {
            _uiState.value = state.copy(currentIndex = state.currentIndex - 1, isSpeaking = false)
        }
    }

    fun next() {
        val state = _uiState.value as? GuideUiState.Running ?: return
        if (state.currentIndex < state.result.actions.lastIndex) {
            _uiState.value = state.copy(currentIndex = state.currentIndex + 1, isSpeaking = false)
        }
    }

    fun togglePause() {
        val state = _uiState.value as? GuideUiState.Running ?: return
        _uiState.value = state.copy(isPaused = !state.isPaused, isSpeaking = false)
    }

    fun setSpeaking(isSpeaking: Boolean) {
        val state = _uiState.value as? GuideUiState.Running ?: return
        _uiState.value = state.copy(isSpeaking = isSpeaking)
    }
}
