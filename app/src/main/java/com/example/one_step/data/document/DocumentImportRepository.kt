package com.example.one_step.data.document

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.one_step.data.ocr.TextRecognitionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class DocumentType {
    IMAGE,
    PDF,
}

interface DocumentImportRepository {
    suspend fun import(uri: Uri): Result<String>
    fun close()
}

class DefaultDocumentImportRepository(
    context: Context,
    private val imageOcr: TextRecognitionRepository,
    private val pdfTextExtractor: PdfTextExtractor = PdfBoxTextExtractor(context),
) : DocumentImportRepository {
    private val resolver = context.applicationContext.contentResolver

    override suspend fun import(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            when (resolveType(uri, resolver)) {
                DocumentType.IMAGE -> imageOcr.recognize(uri).getOrThrow()
                DocumentType.PDF -> pdfTextExtractor.extract(uri).getOrThrow()
            }.trim().also { text ->
                if (text.isBlank()) throw EmptyDocumentException()
            }
        }
    }

    override fun close() {
        imageOcr.close()
    }
}

fun resolveType(uri: Uri, resolver: ContentResolver): DocumentType {
    val mimeType = resolver.getType(uri)?.lowercase()
    return when {
        mimeType == "application/pdf" -> DocumentType.PDF
        mimeType == "image/jpeg" || mimeType == "image/jpg" || mimeType == "image/png" -> DocumentType.IMAGE
        else -> {
            val name = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }?.lowercase() ?: uri.lastPathSegment?.lowercase().orEmpty()
            when {
                name.endsWith(".pdf") -> DocumentType.PDF
                name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") -> DocumentType.IMAGE
                else -> throw UnsupportedDocumentException()
            }
        }
    }
}

class UnsupportedDocumentException : IllegalArgumentException("지원하는 파일은 PDF, JPG, JPEG, PNG입니다.")

class EmptyDocumentException : IllegalStateException("파일에서 추출된 내용이 없습니다.")
