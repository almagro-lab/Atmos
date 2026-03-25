package com.almagro.atmos

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.almagro.presentation.ui.HistoryScreen
import com.almagro.presentation.ui.WeatherScreen

@Composable
fun AtmosNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = WeatherRoute) {
        composable<WeatherRoute> {
            WeatherScreen(onNavigateToHistory = { navController.navigate(HistoryRoute) { launchSingleTop = true } })
        }
        composable<HistoryRoute> {
            HistoryScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
