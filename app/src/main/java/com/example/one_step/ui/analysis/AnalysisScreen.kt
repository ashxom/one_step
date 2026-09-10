package com.example.one_step.ui.analysis

import androidx.compose.runtime.Composable
import com.example.one_step.ui.common.PlaceholderScreen

@Composable
fun AnalysisScreen(onBack: () -> Unit) {
    PlaceholderScreen(
        title = "분석 결과",
        description = "ML Kit 분석 결과를 표시할 화면입니다.",
        onBack = onBack,
    )
}
