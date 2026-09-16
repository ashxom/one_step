package com.example.one_step.ui.guide

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class GuideSpeechController(
    context: Context,
    private val onPlaybackStateChanged: (Boolean) -> Unit,
    private val onError: (String) -> Unit = {},
) : TextToSpeech.OnInitListener {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var textToSpeech: TextToSpeech? = null
    private var ready = false
    private var pendingText: String? = null

    init {
        runCatching { TextToSpeech(context.applicationContext, this) }
            .onSuccess { tts ->
                textToSpeech = tts
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) = dispatchPlaybackState(true)

                    override fun onDone(utteranceId: String?) = dispatchPlaybackState(false)

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) = reportError("음성 요약을 재생하지 못했어요. 다시 시도해 주세요.")

                    override fun onError(utteranceId: String?, errorCode: Int) =
                        reportError("음성 요약을 재생하지 못했어요. 다시 시도해 주세요.")
                })
            }
            .onFailure {
                reportError("이 기기에서는 음성 요약을 사용할 수 없어요.")
            }
    }

    override fun onInit(status: Int) {
        val tts = textToSpeech ?: return
        if (status != TextToSpeech.SUCCESS) {
            ready = false
            pendingText = null
            reportError("음성 요약을 준비하지 못했어요. 잠시 후 다시 시도해 주세요.")
            return
        }

        val languageResult = tts.setLanguage(Locale.KOREAN)
        if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            ready = false
            pendingText = null
            reportError("한국어 음성을 사용할 수 없어요. 기기 음성 설정을 확인해 주세요.")
            return
        }

        ready = true
        pendingText?.let {
            pendingText = null
            speakNow(it)
        }
    }

    fun speak(text: String) {
        if (text.isBlank()) {
            reportError("읽어드릴 요약 내용이 없어요.")
            return
        }
        if (textToSpeech == null) {
            reportError("이 기기에서는 음성 요약을 사용할 수 없어요.")
            return
        }
        if (!ready) {
            pendingText = text
            return
        }
        speakNow(text)
    }

    fun stop() {
        pendingText = null
        textToSpeech?.stop()
        dispatchPlaybackState(false)
    }

    fun shutdown() {
        pendingText = null
        ready = false
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        dispatchPlaybackState(false)
    }

    private fun speakNow(text: String) {
        val result = textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "one-step-guide")
        if (result != TextToSpeech.SUCCESS) {
            reportError("음성 요약을 재생하지 못했어요. 다시 시도해 주세요.")
        }
    }

    private fun dispatchPlaybackState(isSpeaking: Boolean) {
        mainHandler.post { onPlaybackStateChanged(isSpeaking) }
    }

    private fun reportError(message: String) {
        mainHandler.post {
            onPlaybackStateChanged(false)
            onError(message)
        }
    }
}
