package com.example.one_step.ui.document

import androidx.lifecycle.ViewModel

/** Shared document text hand-off for camera, file and direct-input routes. */
class DocumentSessionViewModel : ViewModel() {
    var documentText: String? = null
        private set

    fun setDocumentText(text: String) {
        documentText = text
    }
}
