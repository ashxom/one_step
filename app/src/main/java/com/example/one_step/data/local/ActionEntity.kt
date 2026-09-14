package com.example.one_step.data.local

import androidx.room.Entity

@Entity(tableName = "guide_actions", primaryKeys = ["id", "documentId"])
data class ActionEntity(
    val id: String,
    val documentId: String,
    val sortOrder: Int,
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
    val completed: Boolean,
)
