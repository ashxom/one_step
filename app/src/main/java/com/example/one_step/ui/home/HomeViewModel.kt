package com.example.one_step.ui.home

import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    val uiState = HomeUiState()
}

data class HomeUiState(
    val activeDocumentTitle: String = "현장체험학습 참가 신청서",
    val activeDocumentSource: String = "늘솔초등학교 • 마감 D-2",
    val completedSteps: Int = 2,
    val totalSteps: Int = 3,
)
