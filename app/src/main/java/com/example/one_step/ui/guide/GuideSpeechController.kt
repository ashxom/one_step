package com.example.one_step.ui.guide

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class GuideSpeechController(context: Context) : TextToSpeech.OnInitListener {
    private val textToSpeech = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var pendingText: String? = null

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            textToSpeech.language = Locale.KOREAN
            pendingText?.let {
                speakNow(it)
                pendingText = null
            }
        }
    }

    fun speak(text: String) {
        if (!ready) pendingText = text else speakNow(text)
    }

    fun stop() {
        pendingText = null
        textToSpeech.stop()
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    private fun speakNow(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "one-step-guide")
    }
}
