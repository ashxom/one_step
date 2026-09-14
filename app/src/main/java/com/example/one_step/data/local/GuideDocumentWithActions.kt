package com.example.one_step.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class GuideDocumentWithActions(
    @Embedded val document: GuideDocumentEntity,
    @Relation(parentColumn = "id", entityColumn = "documentId")
    val actions: List<ActionEntity>,
)
