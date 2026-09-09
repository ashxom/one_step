package com.example.one_step.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector? = null,
) {
    data object Home : AppDestination("home", "홈", Icons.Default.Home)
    data object History : AppDestination("history", "기록", Icons.AutoMirrored.Filled.Article)
    data object Settings : AppDestination("settings", "설정", Icons.Default.Settings)
    data object Camera : AppDestination("camera", "안내문 촬영")
    data object Analysis : AppDestination("analysis", "분석 결과")
    data object Guide : AppDestination("guide", "한걸음 안내")

    companion object {
        val bottomNavigation = listOf(Home, History, Settings)
    }
}
