package com.mindless.screen.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.mindless.screen.domain.repository.DailySummaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(
    val screenTimeTodayLabel: String,
    val addictionScoreLabel: String,
    val unlockCountLabel: String
)

class DashboardViewModel(
    private val dailySummaryRepository: DailySummaryRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private fun buildUiState(): DashboardUiState {
        val summary = dailySummaryRepository?.latestSummary()
        if (summary == null) {
            return DashboardUiState(
                screenTimeTodayLabel = "Screen Time Today",
                addictionScoreLabel = "Addiction Score",
                unlockCountLabel = "Unlock Count"
            )
        }

        val screenMinutes = summary.totalScreenTimeMillis / 60_000
        return DashboardUiState(
            screenTimeTodayLabel = "Screen Time Today: ${screenMinutes}m",
            addictionScoreLabel = "Addiction Score: %.1f".format(summary.addictionScore),
            unlockCountLabel = "Unlock Count: ${summary.unlockCount}"
        )
    }
}
