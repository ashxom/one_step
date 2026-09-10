package com.example.one_step.domain.usecase

import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.repository.AiAnalysisRepository

class AnalyzeDocumentUseCase(private val repository: AiAnalysisRepository) {
    suspend operator fun invoke(text: String): AnalysisResult = repository.analyzeDocument(text)
}
