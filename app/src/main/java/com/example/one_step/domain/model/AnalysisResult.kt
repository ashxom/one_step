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
)

data class ActionItem(
    val id: String,
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
)
