package com.example.one_step.domain.model

data class AnalysisResult(
    val title: String,
    val documentType: String,
    val summary: String,
    val actions: List<ActionItem>,
    val deadline: String?,
    val location: String?,
    val items: List<String>,
    val cost: String?,
    val phone: String?,
    val caution: String?,
    val deadlineBadge: String? = null,
    val deadlineDescription: String? = null,
    val locationDescription: String? = null,
    val tripTitle: String? = null,
    val targetGrade: String? = null,
    val phoneLabel: String? = null,
    val encouragement: String? = null,
)

data class ActionItem(
    val id: String,
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
)
