package com.example.one_step.domain.repository

import com.example.one_step.domain.model.AnalysisResult

interface AiAnalysisRepository {
    suspend fun analyzeDocument(text: String): AnalysisResult
}
