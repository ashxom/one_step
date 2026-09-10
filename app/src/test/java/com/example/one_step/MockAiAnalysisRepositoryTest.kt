package com.example.one_step

import com.example.one_step.data.repository.MockAiAnalysisRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MockAiAnalysisRepositoryTest {
    @Test
    fun returnsSampleAnalysisWithRequiredFields() = runBlocking {
        val result = MockAiAnalysisRepository().analyzeDocument("현장체험학습 안내문")

        assertTrue(result.title.isNotBlank())
        assertTrue(result.documentType.isNotBlank())
        assertTrue(result.summary.isNotBlank())
        assertFalse(result.actions.isEmpty())
        assertEquals(3, result.actions.size)
        assertTrue(result.items.isNotEmpty())
        assertTrue(result.deadline?.isNotBlank() == true)
        assertTrue(result.location?.isNotBlank() == true)
        assertTrue(result.cost?.isNotBlank() == true)
        assertTrue(result.phone?.isNotBlank() == true)
        assertTrue(result.caution?.isNotBlank() == true)
    }
}
