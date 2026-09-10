package com.example.one_step

import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.repository.AiAnalysisRepository
import com.example.one_step.ui.analysis.AnalysisUiState
import com.example.one_step.ui.analysis.AnalysisViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalysisViewModelTest {
    @Test
    fun newerDocumentCancelsPreviousAnalysisAndPublishesLatestResult() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = LatestRequestRepository()
            val viewModel = AnalysisViewModel(repository)

            viewModel.analyze("첫 번째 안내문")
            advanceUntilIdle()
            viewModel.analyze("두 번째 안내문")
            advanceUntilIdle()

            assertEquals(listOf("첫 번째 안내문", "두 번째 안내문"), repository.requests)
            assertTrue(viewModel.uiState is AnalysisUiState.Success)
            assertEquals("두 번째 안내문", (viewModel.uiState as AnalysisUiState.Success).result.title)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

private class LatestRequestRepository : AiAnalysisRepository {
    val requests = mutableListOf<String>()

    override suspend fun analyzeDocument(text: String): AnalysisResult {
        requests += text
        if (text == "첫 번째 안내문") awaitCancellation()
        return AnalysisResult(text, "테스트", "요약", emptyList(), null, null, emptyList(), null, null, null)
    }
}
