package com.example.one_step.ui.camera

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.one_step.data.ocr.EmptyTextException
import com.example.one_step.data.ocr.MlKitTextRecognitionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

enum class OcrStep(val label: String) {
    READ_TEXT("문서 글자 또렷하게 읽기"),
    UNDERSTAND_CONTENT("중요한 일정과 준비물 이해 중"),
    CREATE_STEPS("할 일을 한걸음씩 만들기"),
}

sealed interface OcrState {
    data object Idle : OcrState
    data class Loading(val step: OcrStep) : OcrState
    data class Success(val text: String) : OcrState
    data class Error(val message: String) : OcrState
}

data class CameraUiState(
    val cameraReady: Boolean = false,
    val hasFlash: Boolean = false,
    val flashEnabled: Boolean = false,
    val ocrState: OcrState = OcrState.Idle,
    val selectedImageUri: Uri? = null,
)

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MlKitTextRecognitionRepository(application)
    private val recognitionInProgress = AtomicBoolean(false)

    var uiState: CameraUiState by mutableStateOf(CameraUiState())
        private set

    private fun update(block: (CameraUiState) -> CameraUiState) {
        uiState = block(uiState)
    }

    fun onCameraReady(hasFlash: Boolean) {
        update { it.copy(cameraReady = true, hasFlash = hasFlash) }
    }

    fun onCameraError(message: String) {
        update { it.copy(cameraReady = false, ocrState = OcrState.Error(message)) }
    }

    fun toggleFlash(onChanged: (Boolean) -> Unit) {
        if (!uiState.hasFlash) return
        val enabled = !uiState.flashEnabled
        update { it.copy(flashEnabled = enabled) }
        onChanged(enabled)
    }

    fun onCaptureFailed(message: String) {
        update { it.copy(ocrState = OcrState.Error(message)) }
    }

    fun onImageSelectionCancelled() {
        update { it.copy(ocrState = OcrState.Error("이미지 선택이 취소되었습니다.")) }
    }

    fun recognize(uri: Uri) {
        if (!recognitionInProgress.compareAndSet(false, true)) return
        viewModelScope.launch {
            try {
                update { it.copy(selectedImageUri = uri, ocrState = OcrState.Loading(OcrStep.READ_TEXT)) }
                delay(300)
                update { it.copy(ocrState = OcrState.Loading(OcrStep.UNDERSTAND_CONTENT)) }

                val result = repository.recognize(uri)
                update { it.copy(ocrState = OcrState.Loading(OcrStep.CREATE_STEPS)) }
                delay(250)

                result.fold(
                    onSuccess = { text -> update { it.copy(ocrState = OcrState.Success(text)) } },
                    onFailure = { error ->
                        if (error is CancellationException) throw error
                        val message = when (error) {
                            is EmptyTextException -> error.message.orEmpty()
                            else -> "이미지의 글자를 읽지 못했습니다. 다시 촬영해 주세요."
                        }
                        update { it.copy(ocrState = OcrState.Error(message)) }
                    },
                )
            } finally {
                recognitionInProgress.set(false)
            }
        }
    }

    fun reset() {
        update { it.copy(ocrState = OcrState.Idle, selectedImageUri = null, flashEnabled = false) }
    }

    override fun onCleared() {
        repository.close()
        super.onCleared()
    }
}
