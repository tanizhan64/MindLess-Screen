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
import com.mindless.screen.presentation.onboarding.OnboardingScreen
import com.mindless.screen.presentation.onboarding.OnboardingViewModel
import com.mindless.screen.presentation.reports.DailyReportScreen

private const val ONBOARDING_ROUTE = "onboarding"
private const val DASHBOARD_ROUTE = "dashboard"
private const val DAILY_REPORT_ROUTE = "daily_report"

@Composable
fun MindLessNavHost(
    startInOnboarding: Boolean = false,
    onboardingViewModel: OnboardingViewModel
) {
    val navController = rememberNavController()
    val dashboardViewModel: DashboardViewModel = viewModel()
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
    val startDestination = if (startInOnboarding) ONBOARDING_ROUTE else DASHBOARD_ROUTE

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = ONBOARDING_ROUTE) {
            OnboardingScreen(
                viewModel = onboardingViewModel
            )
        }

        composable(route = DASHBOARD_ROUTE) {
            DashboardScreen(
                uiState = dashboardUiState,
                onViewReportsClick = { navController.navigate(DAILY_REPORT_ROUTE) }
            )
        }

        composable(route = DAILY_REPORT_ROUTE) {
            DailyReportScreen()
        }
    }
}
