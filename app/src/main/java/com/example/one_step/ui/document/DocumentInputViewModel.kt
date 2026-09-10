package com.example.one_step.ui.document

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.one_step.data.document.DefaultDocumentImportRepository
import com.example.one_step.data.document.EmptyDocumentException
import com.example.one_step.data.document.UnsupportedDocumentException
import com.example.one_step.data.ocr.MlKitTextRecognitionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

sealed interface DocumentInputState {
    data object Idle : DocumentInputState
    data class Loading(val fileName: String? = null) : DocumentInputState
    data class Success(val documentText: String) : DocumentInputState
    data class Error(val message: String) : DocumentInputState
}

class DocumentInputViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DefaultDocumentImportRepository(
        context = application,
        imageOcr = MlKitTextRecognitionRepository(application),
    )
    private val processing = AtomicBoolean(false)

    var state: DocumentInputState by mutableStateOf(DocumentInputState.Idle)
        private set

    fun importUri(uri: Uri?, fileName: String? = null) {
        if (!processing.compareAndSet(false, true)) return
        if (uri == null) {
            state = DocumentInputState.Error("파일 선택이 취소되었습니다.")
            processing.set(false)
            return
        }
        viewModelScope.launch {
            try {
                state = DocumentInputState.Loading(fileName)
                repository.import(uri).fold(
                    onSuccess = { text -> state = DocumentInputState.Success(text) },
                    onFailure = { error -> state = DocumentInputState.Error(errorMessage(error)) },
                )
            } finally {
                processing.set(false)
            }
        }
    }

    fun submitText(rawText: String) {
        val text = rawText.trim()
        if (text.isBlank()) {
            state = DocumentInputState.Error("안내문 내용을 입력해 주세요.")
            return
        }
        state = DocumentInputState.Success(text)
    }

    fun reset() {
        state = DocumentInputState.Idle
    }

    private fun errorMessage(error: Throwable): String = when (error) {
        is CancellationException -> throw error
        is UnsupportedDocumentException -> error.message.orEmpty()
        is EmptyDocumentException -> error.message.orEmpty()
        else -> "파일 내용을 읽지 못했습니다. 다른 파일로 다시 시도해 주세요."
    }

    override fun onCleared() {
        repository.close()
        super.onCleared()
    }
}
