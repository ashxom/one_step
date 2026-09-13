package com.example.one_step.ui.guide

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class GuideSpeechController(
    context: Context,
    private val onPlaybackStateChanged: (Boolean) -> Unit,
) : TextToSpeech.OnInitListener {
    private val textToSpeech = TextToSpeech(context.applicationContext, this).also { tts ->
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onPlaybackStateChanged(true)
            }

            override fun onDone(utteranceId: String?) {
                onPlaybackStateChanged(false)
            }

            override fun onError(utteranceId: String?) {
                onPlaybackStateChanged(false)
            }
        })
    }
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
        onPlaybackStateChanged(false)
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    private fun speakNow(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "one-step-guide")
    }
}
