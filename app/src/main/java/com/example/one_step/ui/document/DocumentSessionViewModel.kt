package com.example.one_step.ui.document

import androidx.lifecycle.ViewModel

class DocumentSessionViewModel : ViewModel() {
    var documentText: String? = null
        private set

    fun setDocumentText(text: String) {
        documentText = text
    }
}
