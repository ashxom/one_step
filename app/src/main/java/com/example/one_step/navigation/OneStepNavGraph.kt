package com.example.one_step.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.one_step.ui.analysis.AnalysisScreen
import com.example.one_step.ui.camera.CameraScreen
import com.example.one_step.ui.document.DirectInputScreen
import com.example.one_step.ui.document.DocumentSessionViewModel
import com.example.one_step.ui.document.FileImportScreen
import com.example.one_step.ui.guide.GuideScreen
import com.example.one_step.ui.history.HistoryScreen
import com.example.one_step.ui.home.HomeScreen
import com.example.one_step.ui.settings.SettingsScreen

@Composable
fun OneStepNavGraph(navController: NavHostController) {
    val documentSession: DocumentSessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    NavHost(navController = navController, startDestination = AppDestination.Home.route) {
        composable(AppDestination.Home.route) {
            HomeScreen(onNavigate = navController::navigate)
        }
        composable(AppDestination.History.route) { HistoryScreen() }
        composable(AppDestination.Settings.route) { SettingsScreen() }
        composable(AppDestination.Camera.route) {
            CameraScreen(
                onBack = navController::popBackStack,
                onDocumentText = { text ->
                    documentSession.setDocumentText(text)
                    navController.navigate(AppDestination.Analysis.route) {
                        popUpTo(AppDestination.Camera.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppDestination.FileImport.route) {
            FileImportScreen(
                onBack = navController::popBackStack,
                onDocumentText = { text ->
                    documentSession.setDocumentText(text)
                    navController.navigate(AppDestination.Analysis.route) {
                        popUpTo(AppDestination.FileImport.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppDestination.DirectInput.route) {
            DirectInputScreen(
                onBack = navController::popBackStack,
                onDocumentText = { text ->
                    documentSession.setDocumentText(text)
                    navController.navigate(AppDestination.Analysis.route) {
                        popUpTo(AppDestination.DirectInput.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppDestination.Analysis.route) {
            AnalysisScreen(
                documentText = documentSession.documentText,
                onBack = navController::popBackStack,
                onStartGuide = { navController.navigate(AppDestination.Guide.route) },
            )
        }
        composable(AppDestination.Guide.route) { GuideScreen(onBack = navController::popBackStack) }
    }
}
