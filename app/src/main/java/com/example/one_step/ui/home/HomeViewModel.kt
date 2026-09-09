package com.example.one_step.ui.home

import androidx.lifecycle.ViewModel

/** Presentation-only state for Issue #1. Data sources are connected in later issues. */
class HomeViewModel : ViewModel() {
    val uiState = HomeUiState()
}

data class HomeUiState(
    val activeDocumentTitle: String = "현장체험학습 참가 신청서",
    val activeDocumentSource: String = "늘솔초등학교 • 마감 D-2",
    val completedSteps: Int = 2,
    val totalSteps: Int = 3,
)
