package com.example.one_step.data.repository

import com.example.one_step.data.local.ActionEntity
import com.example.one_step.data.local.GuideDocumentDao
import com.example.one_step.data.local.GuideDocumentEntity
import com.example.one_step.domain.model.ActionItem
import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.model.GuideRecord
import com.example.one_step.domain.model.analysisDocumentId
import com.example.one_step.domain.repository.GuideLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class RoomGuideLocalRepository(private val dao: GuideDocumentDao) : GuideLocalRepository {
    override suspend fun saveAnalysis(documentText: String, result: AnalysisResult): String {
        val documentId = analysisDocumentId(result)
        val existingDocument = dao.getDocument(documentId)
        val existingActions = dao.getActions(documentId).associateBy { it.id }
        dao.insertDocument(
            GuideDocumentEntity(
                id = documentId,
                title = result.title,
                documentType = result.documentType,
                analysisDate = System.currentTimeMillis(),
                deadline = result.deadline,
                location = result.location,
                items = result.items.joinToString(ITEM_SEPARATOR),
                phone = result.phone,
                cost = result.cost,
                caution = result.caution,
                summary = result.summary,
                documentText = documentText,
                isCompleted = existingDocument?.isCompleted ?: false,
            ),
        )
        dao.insertActions(result.actions.map { it.toEntity(documentId, existingActions[it.id]?.completed == true) })
        return documentId
    }

    override fun observeRecords(): Flow<List<GuideRecord>> = dao.observeDocuments()
        .map { documents -> documents.mapNotNull { document -> document.toRecord(dao.getActions(document.id)) } }
        .catch { emit(emptyList()) }

    override suspend fun getRecord(documentId: String): GuideRecord? {
        val document = dao.getDocument(documentId) ?: return null
        return document.toRecord(dao.getActions(documentId))
    }

    override suspend fun markActionCompleted(documentId: String, actionId: String) {
        dao.markActionCompleted(documentId, actionId)
        dao.updateDocumentCompletion(documentId)
    }

    override suspend fun clearAll() {
        dao.deleteActions()
        dao.deleteDocuments()
    }

    private fun GuideDocumentEntity.toRecord(actions: List<ActionEntity>): GuideRecord = GuideRecord(
        id = id,
        documentText = documentText,
        analysisDate = analysisDate,
        result = AnalysisResult(
            title = title,
            documentType = documentType,
            summary = summary,
            actions = actions.map(::toDomain),
            deadline = deadline,
            location = location,
            items = items.split(ITEM_SEPARATOR).filter(String::isNotBlank),
            cost = cost,
            phone = phone,
            caution = caution,
        ),
        completedActionIds = actions.filter { it.completed }.mapTo(mutableSetOf()) { it.id },
    )

    private fun ActionItem.toEntity(documentId: String, completed: Boolean) = ActionEntity(id, documentId, title, description, estimatedMinutes, completed)

    private fun toDomain(action: ActionEntity) = ActionItem(action.id, action.title, action.description, action.estimatedMinutes)

    companion object {
        private const val ITEM_SEPARATOR = "\u001F"

    }
}
