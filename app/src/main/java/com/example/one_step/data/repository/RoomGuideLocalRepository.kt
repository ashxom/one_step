package com.example.one_step.data.repository

import com.example.one_step.data.local.ActionEntity
import com.example.one_step.data.local.GuideDocumentDao
import com.example.one_step.data.local.GuideDocumentEntity
import com.example.one_step.data.local.GuideDocumentWithActions
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
        val documentId = analysisDocumentId(documentText, result)
        val existingDocument = dao.getDocument(documentId)
        val existingActions = dao.getActions(documentId).associateBy { it.id }
        val document = GuideDocumentEntity(
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
                deadlineBadge = result.deadlineBadge,
                deadlineDescription = result.deadlineDescription,
                locationDescription = result.locationDescription,
                tripTitle = result.tripTitle,
                targetGrade = result.targetGrade,
                phoneLabel = result.phoneLabel,
                encouragement = result.encouragement,
                summary = result.summary,
                documentText = documentText,
                isCompleted = existingDocument?.isCompleted ?: false,
        )
        val actions = result.actions.mapIndexed { index, action ->
            action.toEntity(documentId, index, existingActions[action.id]?.completed == true)
        }
        dao.insertDocumentWithActions(document, actions)
        return documentId
    }

    override fun observeRecords(): Flow<List<GuideRecord>> = dao.observeDocuments()
        .map { documents -> documents.map { it.toRecord() } }
        .catch { emit(emptyList()) }

    override suspend fun getRecord(documentId: String): GuideRecord? {
        val document = dao.getDocument(documentId) ?: return null
        return GuideDocumentWithActions(document, dao.getActions(documentId)).toRecord()
    }

    override suspend fun markActionCompleted(documentId: String, actionId: String) {
        dao.markActionCompletedAndUpdateDocument(documentId, actionId)
    }

    override suspend fun clearAll() {
        dao.deleteAll()
    }

    private fun GuideDocumentWithActions.toRecord(): GuideRecord = GuideRecord(
        id = document.id,
        documentText = document.documentText,
        analysisDate = document.analysisDate,
        result = AnalysisResult(
            title = document.title,
            documentType = document.documentType,
            summary = document.summary,
            actions = actions.map(::toDomain),
            deadline = document.deadline,
            location = document.location,
            items = document.items.split(ITEM_SEPARATOR).filter(String::isNotBlank),
            cost = document.cost,
            phone = document.phone,
            caution = document.caution,
            deadlineBadge = document.deadlineBadge,
            deadlineDescription = document.deadlineDescription,
            locationDescription = document.locationDescription,
            tripTitle = document.tripTitle,
            targetGrade = document.targetGrade,
            phoneLabel = document.phoneLabel,
            encouragement = document.encouragement,
        ),
        completedActionIds = actions.filter { it.completed }.mapTo(mutableSetOf()) { it.id },
    )

    private fun ActionItem.toEntity(documentId: String, sortOrder: Int, completed: Boolean) =
        ActionEntity(id, documentId, sortOrder, title, description, estimatedMinutes, completed)

    private fun toDomain(action: ActionEntity) = ActionItem(action.id, action.title, action.description, action.estimatedMinutes)

    companion object {
        private const val ITEM_SEPARATOR = "\u001F"

    }
}
