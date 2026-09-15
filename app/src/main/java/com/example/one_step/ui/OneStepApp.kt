package com.example.one_step.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.one_step.data.repository.DataStoreSettingsRepository
import com.example.one_step.navigation.AppDestination
import com.example.one_step.navigation.OneStepNavGraph
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepTextMuted
import com.example.one_step.ui.theme.One_stepTheme

@Composable
fun OneStepApp() {
    val context = LocalContext.current
    val settingsRepository = remember(context) { DataStoreSettingsRepository(context) }
    val settings by settingsRepository.settings.collectAsStateWithLifecycle(
        initialValue = com.example.one_step.domain.model.AppSettings(),
    )
    val baseDensity = LocalDensity.current
    val scaledDensity = remember(baseDensity, settings.fontScale) {
        Density(
            density = baseDensity.density,
            fontScale = baseDensity.fontScale * settings.fontScale,
        )
    }
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomNavigation = AppDestination.bottomNavigation.any { it.route == currentRoute }

    CompositionLocalProvider(LocalDensity provides scaledDensity) {
        One_stepTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = OneStepBackground,
                bottomBar = {
                    if (showBottomNavigation) {
                        OneStepBottomNavigation(
                            currentRoute = currentRoute,
                            onNavigate = { destination ->
                                navController.navigate(destination.route) {
                                    popUpTo(AppDestination.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                },
            ) { innerPadding ->
                androidx.compose.foundation.layout.Box(Modifier.padding(innerPadding)) {
                    OneStepNavGraph(navController, settingsRepository)
                }
            }
        }
    }
}

@Composable
private fun OneStepBottomNavigation(
    currentRoute: String?,
    onNavigate: (AppDestination) -> Unit,
) {
    NavigationBar(
        containerColor = OneStepBackground,
        tonalElevation = 0.dp,
    ) {
        AppDestination.bottomNavigation.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination) },
                icon = { androidx.compose.material3.Icon(destination.icon!!, contentDescription = destination.label) },
                label = { Text(destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OneStepBlue,
                    selectedTextColor = OneStepBlue,
                    indicatorColor = OneStepBlueSoft,
                    unselectedIconColor = OneStepTextMuted,
                    unselectedTextColor = OneStepTextMuted,
                ),
            )
        }
    }
}
