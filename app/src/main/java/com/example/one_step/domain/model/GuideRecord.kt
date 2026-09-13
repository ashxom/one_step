package com.example.one_step.domain.model

data class GuideRecord(
    val id: String,
    val documentText: String,
    val analysisDate: Long,
    val result: AnalysisResult,
    val completedActionIds: Set<String>,
)

fun analysisDocumentId(result: AnalysisResult): String = listOf(
    result.title,
    result.documentType,
    result.deadline.orEmpty(),
    result.actions.joinToString { it.id },
).joinToString("|").hashCode().toString()

val GuideRecord.completedActionCount: Int
    get() = completedActionIds.size

val GuideRecord.isCompleted: Boolean
    get() = result.actions.isNotEmpty() && result.actions.all { it.id in completedActionIds }
