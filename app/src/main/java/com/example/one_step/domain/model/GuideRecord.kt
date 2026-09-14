package com.example.one_step.domain.model

data class GuideRecord(
    val id: String,
    val documentText: String,
    val analysisDate: Long,
    val result: AnalysisResult,
    val completedActionIds: Set<String>,
)

fun analysisDocumentId(documentText: String, result: AnalysisResult): String {
    val source = buildString {
        append(documentText.trim())
        append('|')
        append(result.title)
        append('|')
        append(result.documentType)
        append('|')
        append(result.deadline.orEmpty())
        append('|')
        append(result.actions.joinToString { it.id })
    }
    return java.security.MessageDigest.getInstance("SHA-256")
        .digest(source.toByteArray(Charsets.UTF_8))
        .joinToString(separator = "") { byte -> "%02x".format(byte) }
}

fun analysisDocumentId(result: AnalysisResult): String = analysisDocumentId("", result)

val GuideRecord.completedActionCount: Int
    get() = completedActionIds.size

val GuideRecord.isCompleted: Boolean
    get() = result.actions.isNotEmpty() && result.actions.all { it.id in completedActionIds }
