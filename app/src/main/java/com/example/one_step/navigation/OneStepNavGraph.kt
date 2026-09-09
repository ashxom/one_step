package com.example.one_step.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.one_step.ui.analysis.AnalysisScreen
import com.example.one_step.ui.camera.CameraScreen
import com.example.one_step.ui.guide.GuideScreen
import com.example.one_step.ui.history.HistoryScreen
import com.example.one_step.ui.home.HomeScreen
import com.example.one_step.ui.settings.SettingsScreen

@Composable
fun OneStepNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppDestination.Home.route) {
        composable(AppDestination.Home.route) {
            HomeScreen(onNavigate = navController::navigate)
        }
        composable(AppDestination.History.route) { HistoryScreen() }
        composable(AppDestination.Settings.route) { SettingsScreen() }
        composable(AppDestination.Camera.route) { CameraScreen(onBack = navController::popBackStack) }
        composable(AppDestination.Analysis.route) { AnalysisScreen(onBack = navController::popBackStack) }
        composable(AppDestination.Guide.route) { GuideScreen(onBack = navController::popBackStack) }
    }
}
