package com.example.one_step.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GuideDocumentDao {
    @Transaction
    @Query("SELECT * FROM guide_documents ORDER BY analysisDate DESC")
    abstract fun observeDocuments(): Flow<List<GuideDocumentWithActions>>

    @Query("SELECT * FROM guide_documents WHERE id = :documentId LIMIT 1")
    abstract suspend fun getDocument(documentId: String): GuideDocumentEntity?

    @Query("SELECT * FROM guide_actions WHERE documentId = :documentId ORDER BY sortOrder ASC, rowid ASC")
    abstract suspend fun getActions(documentId: String): List<ActionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertDocument(document: GuideDocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertActions(actions: List<ActionEntity>)

    @Transaction
    open suspend fun insertDocumentWithActions(document: GuideDocumentEntity, actions: List<ActionEntity>) {
        insertDocument(document)
        if (actions.isNotEmpty()) insertActions(actions)
    }

    @Query("UPDATE guide_actions SET completed = 1 WHERE documentId = :documentId AND id = :actionId")
    protected abstract suspend fun markActionCompleted(documentId: String, actionId: String): Int

    @Query("UPDATE guide_documents SET isCompleted = 1 WHERE id = :documentId AND NOT EXISTS (SELECT 1 FROM guide_actions WHERE documentId = :documentId AND completed = 0)")
    protected abstract suspend fun updateDocumentCompletion(documentId: String)

    @Transaction
    open suspend fun markActionCompletedAndUpdateDocument(documentId: String, actionId: String) {
        check(markActionCompleted(documentId, actionId) == 1) {
            "완료할 행동을 찾지 못했습니다."
        }
        updateDocumentCompletion(documentId)
    }

    @Query("DELETE FROM guide_actions")
    protected abstract suspend fun deleteActions()

    @Query("DELETE FROM guide_documents")
    protected abstract suspend fun deleteDocuments()

    @Transaction
    open suspend fun deleteAll() {
        deleteActions()
        deleteDocuments()
    }
}
