package com.example.one_step.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.one_step.data.repository.MockAiAnalysisRepository
import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.repository.AiAnalysisRepository
import com.example.one_step.domain.repository.GuideLocalRepository
import com.example.one_step.domain.usecase.AnalyzeDocumentUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface AnalysisUiState {
    data object Idle : AnalysisUiState
    data object Loading : AnalysisUiState
    data class Success(val result: AnalysisResult) : AnalysisUiState
    data class Error(val message: String) : AnalysisUiState
}

class AnalysisViewModel(
    repository: AiAnalysisRepository = MockAiAnalysisRepository(),
    private var localRepository: GuideLocalRepository? = null,
) : ViewModel() {
    private val analyzeDocument = AnalyzeDocumentUseCase(repository)
    private var analysisJob: Job? = null
    private var latestRequestId = 0L

    var uiState: AnalysisUiState by mutableStateOf(AnalysisUiState.Idle)
        private set

    fun attachLocalRepository(repository: GuideLocalRepository) {
        localRepository = repository
    }

    fun analyze(text: String) {
        val documentText = text.trim()
        analysisJob?.cancel()
        val requestId = ++latestRequestId
        if (documentText.isBlank()) {
            uiState = AnalysisUiState.Error("분석할 안내문 내용이 없습니다.")
            return
        }
        analysisJob = viewModelScope.launch {
            uiState = AnalysisUiState.Loading
            try {
                val result = analyzeDocument(documentText)
                try {
                    localRepository?.saveAnalysis(documentText, result)
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Throwable) {
                }
                if (requestId == latestRequestId) {
                    uiState = AnalysisUiState.Success(result)
                }
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                if (requestId == latestRequestId) {
                    uiState = AnalysisUiState.Error("안내문 분석에 실패했습니다. 다시 시도해 주세요.")
                }
            }
        }
    }

    override fun onCleared() {
        analysisJob?.cancel()
        super.onCleared()
    }
}
