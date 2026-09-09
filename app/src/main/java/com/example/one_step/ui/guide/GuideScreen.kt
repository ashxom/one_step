package com.example.one_step.ui.guide

import androidx.compose.runtime.Composable
import com.example.one_step.ui.common.PlaceholderScreen

@Composable
fun GuideScreen(onBack: () -> Unit) {
    PlaceholderScreen(
        title = "한걸음 안내",
        description = "안내문을 쉬운 순서로 풀어드리는 화면입니다.",
        onBack = onBack,
    )
}
