package com.mindless.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindless.screen.presentation.dashboard.DashboardScreen
import com.mindless.screen.presentation.dashboard.DashboardViewModel
import com.mindless.screen.presentation.reports.DailyReportScreen

private const val DASHBOARD_ROUTE = "dashboard"
private const val DAILY_REPORT_ROUTE = "daily_report"

@Composable
fun MindLessNavHost() {
    val navController = rememberNavController()
    val viewModel: DashboardViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = DASHBOARD_ROUTE
    ) {
        composable(route = DASHBOARD_ROUTE) {
            DashboardScreen(
                uiState = uiState,
                onViewReportsClick = { navController.navigate(DAILY_REPORT_ROUTE) }
            )
        }

        composable(route = DAILY_REPORT_ROUTE) {
            DailyReportScreen()
        }
    }
}
