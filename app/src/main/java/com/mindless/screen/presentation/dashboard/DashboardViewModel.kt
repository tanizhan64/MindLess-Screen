package com.mindless.screen.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindless.screen.domain.repository.DailySummaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val screenTimeTodayLabel: String,
    val addictionScoreLabel: String,
    val unlockCountLabel: String
)

class DashboardViewModel(
    private val dailySummaryRepository: DailySummaryRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            screenTimeTodayLabel = "",
            addictionScoreLabel = "",
            unlockCountLabel = ""
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val summary = dailySummaryRepository?.latestSummary() ?: return@launch
            val screenMinutes = summary.totalScreenTimeMillis / 60_000
            _uiState.value = DashboardUiState(
                screenTimeTodayLabel = "Screen Time Today: ${screenMinutes}m",
                addictionScoreLabel = "Addiction Score: %.1f".format(summary.addictionScore),
                unlockCountLabel = "Unlock Count: ${summary.unlockCount}"
            )
        }
    }
}
