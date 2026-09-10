package com.example.one_step.data.document

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Interface leaves room for a rendered-page OCR implementation for scanned PDFs later. */
interface PdfTextExtractor {
    suspend fun extract(uri: Uri): Result<String>
}

class PdfBoxTextExtractor(context: Context) : PdfTextExtractor {
    private val appContext = context.applicationContext

    init {
        PDFBoxResourceLoader.init(appContext)
    }

    override suspend fun extract(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                PDDocument.load(input).use { document ->
                    PDFTextStripper().getText(document).trim()
                }
            } ?: throw IllegalStateException("PDF를 열 수 없습니다.")
        }
    }
}
