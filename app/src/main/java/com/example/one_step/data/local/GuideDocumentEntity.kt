package com.example.one_step.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guide_documents")
data class GuideDocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val documentType: String,
    val analysisDate: Long,
    val deadline: String?,
    val location: String?,
    val items: String,
    val phone: String?,
    val cost: String?,
    val caution: String?,
    val deadlineBadge: String?,
    val deadlineDescription: String?,
    val locationDescription: String?,
    val tripTitle: String?,
    val targetGrade: String?,
    val phoneLabel: String?,
    val encouragement: String?,
    val summary: String,
    val documentText: String,
    val isCompleted: Boolean,
)
