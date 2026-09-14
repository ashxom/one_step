package com.example.one_step.domain.repository

import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.model.GuideRecord
import kotlinx.coroutines.flow.Flow

interface GuideLocalRepository {
    suspend fun saveAnalysis(documentText: String, result: AnalysisResult): String
    fun observeRecords(): Flow<List<GuideRecord>>
    suspend fun getRecord(documentId: String): GuideRecord?
    suspend fun markActionCompleted(documentId: String, actionId: String)
    suspend fun clearAll()
}
