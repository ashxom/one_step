package com.example.one_step.data.ocr

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface TextRecognitionRepository {
    suspend fun recognize(uri: Uri): Result<String>
    fun close()
}

class MlKitTextRecognitionRepository(context: Context) : TextRecognitionRepository {
    private val appContext = context.applicationContext
    private val recognizer: TextRecognizer = TextRecognition.getClient(
        KoreanTextRecognizerOptions.Builder().build(),
    )

    override suspend fun recognize(uri: Uri): Result<String> = runCatching {
        val image = InputImage.fromFilePath(appContext, uri)
        val text = recognizer.process(image).awaitResult().text.trim()
        if (text.isBlank()) {
            throw EmptyTextException()
        }
        text
    }

    override fun close() {
        recognizer.close()
    }
}

class EmptyTextException : IllegalStateException("이미지에서 인식된 텍스트가 없습니다.")

private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { value ->
        if (continuation.isActive) continuation.resume(value)
    }
    addOnFailureListener { error ->
        if (continuation.isActive) continuation.resumeWithException(error)
    }
    addOnCanceledListener {
        if (!continuation.isActive) return@addOnCanceledListener
        continuation.resumeWithException(IllegalStateException("텍스트 인식이 취소되었습니다."))
    }
}
