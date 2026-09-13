package com.example.one_step

import com.example.one_step.domain.model.ActionItem
import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.ui.guide.GuideUiState
import com.example.one_step.ui.guide.GuideViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class GuideViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun completesActionsInOrderAndShowsCompletion() {
        val viewModel = GuideViewModel()
        val result = sampleResult()

        viewModel.start(result)
        assertEquals(0, (viewModel.uiState.value as GuideUiState.Running).currentIndex)

        viewModel.completeCurrent()
        assertEquals(1, (viewModel.uiState.value as GuideUiState.Running).currentIndex)
        viewModel.completeCurrent()
        assertEquals(2, (viewModel.uiState.value as GuideUiState.Running).currentIndex)
        viewModel.completeCurrent()

        val completed = viewModel.uiState.value as GuideUiState.Completed
        assertEquals(result.actions, completed.completedActions)
    }

    @Test
    fun supportsPreviousPauseAndNext() {
        val viewModel = GuideViewModel()
        viewModel.start(sampleResult())

        viewModel.next()
        assertEquals(1, (viewModel.uiState.value as GuideUiState.Running).currentIndex)
        viewModel.previous()
        assertEquals(0, (viewModel.uiState.value as GuideUiState.Running).currentIndex)
        viewModel.togglePause()
        assertTrue((viewModel.uiState.value as GuideUiState.Running).isPaused)
    }

    private fun sampleResult() = AnalysisResult(
        title = "현장체험학습",
        documentType = "가정통신문",
        summary = "요약",
        actions = listOf(
            ActionItem("1", "첫 번째", "설명", 5),
            ActionItem("2", "두 번째", "설명", 10),
            ActionItem("3", "세 번째", "설명", 3),
        ),
        deadline = null,
        location = null,
        items = emptyList(),
        cost = null,
        phone = null,
        caution = null,
    )
}
