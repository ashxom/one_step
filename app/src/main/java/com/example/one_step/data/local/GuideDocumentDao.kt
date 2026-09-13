package com.example.one_step.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GuideDocumentDao {
    @Query("SELECT * FROM guide_documents ORDER BY analysisDate DESC")
    fun observeDocuments(): Flow<List<GuideDocumentEntity>>

    @Query("SELECT * FROM guide_documents WHERE id = :documentId LIMIT 1")
    suspend fun getDocument(documentId: String): GuideDocumentEntity?

    @Query("SELECT * FROM guide_actions WHERE documentId = :documentId ORDER BY rowid ASC")
    suspend fun getActions(documentId: String): List<ActionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: GuideDocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActions(actions: List<ActionEntity>)

    @Query("UPDATE guide_actions SET completed = 1 WHERE documentId = :documentId AND id = :actionId")
    suspend fun markActionCompleted(documentId: String, actionId: String)

    @Query("UPDATE guide_documents SET isCompleted = 1 WHERE id = :documentId AND NOT EXISTS (SELECT 1 FROM guide_actions WHERE documentId = :documentId AND completed = 0)")
    suspend fun updateDocumentCompletion(documentId: String)

    @Query("DELETE FROM guide_actions")
    suspend fun deleteActions()

    @Query("DELETE FROM guide_documents")
    suspend fun deleteDocuments()
}
