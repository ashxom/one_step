package com.example.one_step.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.one_step.domain.model.GuideRecord
import com.example.one_step.domain.repository.GuideLocalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var repository: GuideLocalRepository? = null
    private var observeJob: Job? = null

    fun attachRepository(localRepository: GuideLocalRepository) {
        if (repository === localRepository) return
        repository = localRepository
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            localRepository.observeRecords().collect { records ->
                _uiState.value = records.firstOrNull()?.toHomeState() ?: HomeUiState()
            }
        }
    }

    private fun GuideRecord.toHomeState(): HomeUiState = HomeUiState(
        hasActiveDocument = true,
        activeDocumentTitle = result.title,
        activeDocumentSource = listOfNotNull(result.documentType, result.deadlineBadge ?: result.deadline).joinToString(" • "),
        completedSteps = completedActionIds.size.coerceAtMost(result.actions.size),
        totalSteps = result.actions.size,
        activeActionIds = result.actions.map { it.id },
        activeActionTitles = result.actions.mapIndexed { index, action -> "${index + 1}. ${action.title}" },
        completedActionIds = completedActionIds,
        nextActionTitle = result.actions.firstOrNull { it.id !in completedActionIds }?.title ?: "모든 단계 완료",
    )
}

data class HomeUiState(
    val hasActiveDocument: Boolean = false,
    val activeDocumentTitle: String = "",
    val activeDocumentSource: String = "",
    val completedSteps: Int = 0,
    val totalSteps: Int = 0,
    val activeActionIds: List<String> = emptyList(),
    val activeActionTitles: List<String> = emptyList(),
    val completedActionIds: Set<String> = emptySet(),
    val nextActionTitle: String = "",
)
