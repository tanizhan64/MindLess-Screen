package com.mindless.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindless.screen.data.insights.InMemoryAiInsightsRepository
import com.mindless.screen.data.tracking.InMemoryDailySummaryRepository
import com.mindless.screen.presentation.dashboard.DashboardScreen
import com.mindless.screen.presentation.dashboard.DashboardViewModel
import com.mindless.screen.presentation.insights.InsightsScreen
import com.mindless.screen.presentation.insights.InsightsViewModel
import com.mindless.screen.presentation.onboarding.OnboardingScreen
import com.mindless.screen.presentation.onboarding.OnboardingViewModel
import com.mindless.screen.presentation.profile.ProfileScreen
import com.mindless.screen.presentation.profile.ProfileViewModel
import com.mindless.screen.presentation.reports.DailyReportScreen

private const val ONBOARDING_ROUTE = "onboarding"
private const val DASHBOARD_ROUTE = "dashboard"
private const val DAILY_REPORT_ROUTE = "daily_report"
private const val INSIGHTS_ROUTE = "insights"
private const val PROFILE_ROUTE = "profile"

@Composable
fun MindLessNavHost(
    startInOnboarding: Boolean = false,
    onboardingViewModel: OnboardingViewModel
) {
    val navController = rememberNavController()
    val dailySummaryRepository = remember { InMemoryDailySummaryRepository() }
    val aiInsightsRepository = remember { InMemoryAiInsightsRepository() }
    val dashboardViewModel = remember { DashboardViewModel(dailySummaryRepository) }
    val insightsViewModel = remember { InsightsViewModel(aiInsightsRepository) }
    val profileViewModel = remember { ProfileViewModel(aiInsightsRepository) }
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
    val insightsUiState by insightsViewModel.uiState.collectAsState()
    val profileUiState by profileViewModel.uiState.collectAsState()
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
                onViewReportsClick = { navController.navigate(DAILY_REPORT_ROUTE) },
                onViewInsightsClick = { navController.navigate(INSIGHTS_ROUTE) }
            )
        }

        composable(route = DAILY_REPORT_ROUTE) {
            DailyReportScreen(dailySummaryRepository = dailySummaryRepository)
        }

        composable(route = INSIGHTS_ROUTE) {
            InsightsScreen(
                uiState = insightsUiState,
                onOpenProfileClick = { navController.navigate(PROFILE_ROUTE) }
            )
        }

        composable(route = PROFILE_ROUTE) {
            ProfileScreen(uiState = profileUiState)
        }
    }
}
