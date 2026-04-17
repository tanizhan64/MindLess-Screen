package com.mindless.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindless.screen.domain.repository.DailySummaryRepository
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
    onboardingViewModel: OnboardingViewModel,
    dailySummaryRepository: DailySummaryRepository,
) {
    val navController = rememberNavController()
    val dashboardViewModelFactory = remember(dailySummaryRepository) {
        simpleViewModelFactory { DashboardViewModel(dailySummaryRepository) }
    }
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
            val dashboardViewModel: DashboardViewModel = viewModel(factory = dashboardViewModelFactory)
            val dashboardUiState by dashboardViewModel.uiState.collectAsState()

            DashboardScreen(
                uiState = dashboardUiState,
                onViewReportsClick = { navController.navigate(DAILY_REPORT_ROUTE) },
                onOpenInsightsClick = { navController.navigate(INSIGHTS_ROUTE) },
                onOpenProfileClick = { navController.navigate(PROFILE_ROUTE) },
            )
        }

        composable(route = DAILY_REPORT_ROUTE) {
            DailyReportScreen(dailySummaryRepository = dailySummaryRepository)
        }

        composable(route = INSIGHTS_ROUTE) {
            val insightsViewModel: InsightsViewModel = viewModel()
            val insightsUiState by insightsViewModel.uiState.collectAsState()
            InsightsScreen(uiState = insightsUiState)
        }

        composable(route = PROFILE_ROUTE) {
            val profileViewModel: ProfileViewModel = viewModel()
            val profileUiState by profileViewModel.uiState.collectAsState()
            ProfileScreen(uiState = profileUiState)
        }
    }
}

private fun <T : ViewModel> simpleViewModelFactory(
    creator: () -> T
): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
    }
}
