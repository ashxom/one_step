package com.example.one_step.ui.camera

import androidx.compose.runtime.Composable
import com.example.one_step.ui.common.PlaceholderScreen

@Composable
fun CameraScreen(onBack: () -> Unit) {
    PlaceholderScreen(
        title = "안내문 촬영",
        description = "CameraX 연결 전의 화면 구조입니다.",
        onBack = onBack,
    )
}
