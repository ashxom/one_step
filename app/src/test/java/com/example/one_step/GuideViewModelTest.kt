package com.example.one_step

import com.example.one_step.domain.model.ActionItem
import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.model.GuideRecord
import com.example.one_step.domain.model.analysisDocumentId
import com.example.one_step.domain.repository.GuideLocalRepository
import com.example.one_step.ui.guide.GuideUiState
import com.example.one_step.ui.guide.GuideViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    @Test
    fun selectsFirstIncompleteActionWhenCompletedIdsAreNonSequential() {
        val result = sampleResult()
        val repository = ImmediateGuideLocalRepository(
            GuideRecord(
                id = analysisDocumentId("", result),
                documentText = "",
                analysisDate = 0L,
                result = result,
                completedActionIds = setOf("2"),
            ),
        )
        val viewModel = GuideViewModel()
        viewModel.attachLocalRepository(repository)

        viewModel.start(result)
        assertEquals(0, (viewModel.uiState.value as GuideUiState.Running).currentIndex)

        viewModel.completeCurrent()

        assertEquals(2, (viewModel.uiState.value as GuideUiState.Running).currentIndex)
    }

    @Test
    fun keepsNewGuideStateWhenPreviousCompletionFinishesLate() {
        val repository = DelayedGuideLocalRepository()
        val viewModel = GuideViewModel()
        val firstResult = sampleResult()
        val secondResult = sampleResult(title = "새 안내문")
        viewModel.attachLocalRepository(repository)

        repository.record = GuideRecord(
            id = analysisDocumentId("", firstResult),
            documentText = "",
            analysisDate = 0L,
            result = firstResult,
            completedActionIds = emptySet(),
        )
        viewModel.start(firstResult)
        viewModel.completeCurrent()
        assertTrue(repository.markStarted.isCompleted)

        repository.record = GuideRecord(
            id = analysisDocumentId("", secondResult),
            documentText = "",
            analysisDate = 0L,
            result = secondResult,
            completedActionIds = emptySet(),
        )
        viewModel.start(secondResult)
        repository.allowMark.complete(Unit)

        val running = viewModel.uiState.value as GuideUiState.Running
        assertEquals(secondResult, running.result)
        assertEquals(0, running.currentIndex)
        assertTrue(running.completedIds.isEmpty())
    }

    @Test
    fun usesTheSameDocumentIdForTrimmedText() {
        val result = sampleResult()

        assertEquals(
            analysisDocumentId("안내문 내용", result),
            analysisDocumentId("  \n안내문 내용\t ", result),
        )
    }

    private fun sampleResult(title: String = "현장체험학습") = AnalysisResult(
        title = title,
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

    private class DelayedGuideLocalRepository : GuideLocalRepository {
        val markStarted = CompletableDeferred<Unit>()
        val allowMark = CompletableDeferred<Unit>()
        var record: GuideRecord? = null

        override suspend fun saveAnalysis(documentText: String, result: AnalysisResult): String = "document-id"

        override fun observeRecords(): Flow<List<GuideRecord>> = flowOf(emptyList())

        override suspend fun getRecord(documentId: String): GuideRecord? = record?.takeIf { it.id == documentId }

        override suspend fun markActionCompleted(documentId: String, actionId: String) {
            markStarted.complete(Unit)
            allowMark.await()
        }

        override suspend fun clearAll() = Unit
    }

    private class ImmediateGuideLocalRepository(
        private var record: GuideRecord,
    ) : GuideLocalRepository {
        override suspend fun saveAnalysis(documentText: String, result: AnalysisResult): String = record.id

        override fun observeRecords(): Flow<List<GuideRecord>> = flowOf(listOf(record))

        override suspend fun getRecord(documentId: String): GuideRecord? = record.takeIf { it.id == documentId }

        override suspend fun markActionCompleted(documentId: String, actionId: String) {
            record = record.copy(completedActionIds = record.completedActionIds + actionId)
        }

        override suspend fun clearAll() = Unit
    }
}
