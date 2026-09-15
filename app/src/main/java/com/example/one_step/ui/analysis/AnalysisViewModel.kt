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
    data class Success(
        val result: AnalysisResult,
        val persistenceWarning: String? = null,
        val canStartGuide: Boolean = true,
    ) : AnalysisUiState
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
                var canStartGuide = true
                val persistenceWarning = try {
                    if (localRepository != null) {
                        localRepository?.saveAnalysis(documentText, result)
                    }
                    null
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Throwable) {
                    canStartGuide = false
                    "분석 결과는 확인할 수 있지만 기록에 저장하지 못했어요."
                }
                if (requestId == latestRequestId) {
                    uiState = AnalysisUiState.Success(result, persistenceWarning, canStartGuide)
                }
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                if (requestId == latestRequestId) {
                    uiState = AnalysisUiState.Error("안내문 분석에 실패했습니다. 다시 시도해 주세요.")
                }
            }
        }
    }

    fun showResult(result: AnalysisResult) {
        analysisJob?.cancel()
        latestRequestId += 1
        uiState = AnalysisUiState.Success(result)
    }

    override fun onCleared() {
        analysisJob?.cancel()
        super.onCleared()
    }
}
