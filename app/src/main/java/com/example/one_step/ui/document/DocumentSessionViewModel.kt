package com.example.one_step.ui.document

import androidx.lifecycle.ViewModel
import com.example.one_step.domain.model.GuideRecord

class DocumentSessionViewModel : ViewModel() {
    var documentText: String? = null
        private set

    var selectedRecord: GuideRecord? = null
        private set

    fun setDocumentText(text: String, record: GuideRecord? = null) {
        documentText = text
        selectedRecord = record
    }
}
