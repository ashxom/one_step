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
    val activeDocumentTitle: String = "현장체험학습 참가 신청서",
    val activeDocumentSource: String = "늘솔초등학교 • 마감 D-2",
    val completedSteps: Int = 2,
    val totalSteps: Int = 3,
    val activeActionIds: List<String> = listOf("1", "2", "3"),
    val activeActionTitles: List<String> = listOf("1. 일정 확인", "2. 준비물 체크", "3. 동의 서명"),
    val completedActionIds: Set<String> = setOf("1", "2"),
    val nextActionTitle: String = "참가 동의 서명하기",
)
