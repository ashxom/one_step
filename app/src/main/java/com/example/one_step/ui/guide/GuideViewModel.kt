package com.example.one_step.ui.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.one_step.domain.model.ActionItem
import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.model.analysisDocumentId
import com.example.one_step.domain.repository.GuideLocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface GuideUiState {
    data object Empty : GuideUiState

    data class Running(
        val result: AnalysisResult,
        val currentIndex: Int,
        val documentId: String = analysisDocumentId(result),
        val completedIds: Set<String> = emptySet(),
        val isPaused: Boolean = false,
        val isSpeaking: Boolean = false,
        val isSaving: Boolean = false,
        val saveError: String? = null,
    ) : GuideUiState

    data class Completed(
        val result: AnalysisResult,
        val completedActions: List<ActionItem>,
    ) : GuideUiState
}

class GuideViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<GuideUiState>(GuideUiState.Empty)
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()
    private var localRepository: GuideLocalRepository? = null

    fun attachLocalRepository(repository: GuideLocalRepository) {
        localRepository = repository
    }

    fun resumeLatest() {
        viewModelScope.launch {
            val record = runCatching { localRepository?.observeRecords()?.first()?.firstOrNull() }.getOrNull() ?: return@launch
            start(record.result, record.documentText)
        }
    }

    fun start(result: AnalysisResult, documentText: String? = null) {
        if (result.actions.isEmpty()) {
            _uiState.value = GuideUiState.Completed(result, emptyList())
        } else {
            val documentId = analysisDocumentId(documentText.orEmpty(), result)
            _uiState.value = GuideUiState.Running(result, currentIndex = 0, documentId = documentId)
            viewModelScope.launch {
                val record = runCatching { localRepository?.getRecord(documentId) }.getOrNull() ?: return@launch
                val completedIds = record.completedActionIds
                if (completedIds.isEmpty()) return@launch
                if (result.actions.all { it.id in completedIds }) {
                    _uiState.value = GuideUiState.Completed(result, result.actions)
                } else {
                    val current = _uiState.value as? GuideUiState.Running ?: return@launch
                    if (current.documentId == documentId) {
                        _uiState.value = current.copy(
                            currentIndex = result.actions.indexOfFirst { it.id !in completedIds }.coerceAtLeast(0),
                            completedIds = completedIds,
                        )
                    }
                }
            }
        }
    }

    fun completeCurrent() {
        val state = _uiState.value as? GuideUiState.Running ?: return
        if (state.isSaving) return
        val action = state.result.actions.getOrNull(state.currentIndex) ?: return
        _uiState.value = state.copy(isSaving = true, saveError = null)
        viewModelScope.launch {
            try {
                localRepository?.markActionCompleted(state.documentId, action.id)
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
                        isSaving = false,
                        saveError = null,
                    )
                }
            } catch (error: kotlinx.coroutines.CancellationException) {
                throw error
            } catch (_: Throwable) {
                val current = _uiState.value as? GuideUiState.Running
                if (current?.documentId == state.documentId && current.currentIndex == state.currentIndex) {
                    _uiState.value = current.copy(
                        isSaving = false,
                        saveError = "완료 상태를 저장하지 못했어요. 다시 시도해 주세요.",
                    )
                }
            }
        }
    }

    fun retryCompleteCurrent() = completeCurrent()

    fun previous() {
        val state = _uiState.value as? GuideUiState.Running ?: return
        if (state.currentIndex > 0) {
            _uiState.value = state.copy(currentIndex = state.currentIndex - 1, isSpeaking = false, saveError = null)
        }
    }

    fun next() {
        val state = _uiState.value as? GuideUiState.Running ?: return
        if (state.currentIndex < state.result.actions.lastIndex) {
            _uiState.value = state.copy(currentIndex = state.currentIndex + 1, isSpeaking = false, saveError = null)
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
