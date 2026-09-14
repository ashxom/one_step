package com.example.one_step.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.one_step.ui.analysis.AnalysisScreen
import com.example.one_step.data.local.OneStepDatabase
import com.example.one_step.data.repository.DataStoreSettingsRepository
import com.example.one_step.data.repository.RoomGuideLocalRepository
import com.example.one_step.ui.camera.CameraScreen
import com.example.one_step.ui.document.DirectInputScreen
import com.example.one_step.ui.document.DocumentSessionViewModel
import com.example.one_step.ui.document.FileImportScreen
import com.example.one_step.ui.guide.GuideScreen
import com.example.one_step.ui.guide.GuideViewModel
import com.example.one_step.ui.history.HistoryScreen
import com.example.one_step.ui.home.HomeScreen
import com.example.one_step.ui.settings.SettingsScreen

@Composable
fun OneStepNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val documentSession: DocumentSessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val guideViewModel: GuideViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val localRepository = remember(context) { RoomGuideLocalRepository(OneStepDatabase.getInstance(context).guideDocumentDao()) }
    val settingsRepository = remember(context) { DataStoreSettingsRepository(context) }
    guideViewModel.attachLocalRepository(localRepository)
    NavHost(navController = navController, startDestination = AppDestination.Home.route) {
        composable(AppDestination.Home.route) {
            HomeScreen(onNavigate = navController::navigate)
        }
        composable(AppDestination.History.route) {
            HistoryScreen(
                repository = localRepository,
                onRecordClick = { record ->
                    documentSession.setDocumentText(record.documentText, record)
                    navController.navigate(AppDestination.Analysis.route)
                },
            )
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen(repository = settingsRepository, localRepository = localRepository)
        }
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
                initialResult = documentSession.selectedRecord?.result,
                onBack = navController::popBackStack,
                localRepository = localRepository,
                onStartGuide = { result ->
                    guideViewModel.start(result, documentSession.documentText)
                    navController.navigate(AppDestination.Guide.route)
                },
            )
        }
        composable(AppDestination.Guide.route) {
            GuideScreen(onBack = navController::popBackStack, repository = localRepository, viewModel = guideViewModel)
        }
    }
}
