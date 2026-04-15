package com.mindless.screen.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val DASHBOARD_ROUTE = "dashboard"

@Composable
fun MindLessNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = DASHBOARD_ROUTE
    ) {
        composable(route = DASHBOARD_ROUTE) {
            Text(text = "Dashboard")
        }
    }
}
