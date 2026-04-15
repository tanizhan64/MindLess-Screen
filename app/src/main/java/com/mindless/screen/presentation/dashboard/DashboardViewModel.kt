package com.mindless.screen.presentation.dashboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(
    val screenTimeTodayLabel: String = "Screen Time Today",
    val addictionScoreLabel: String = "Addiction Score",
    val unlockCountLabel: String = "Unlock Count"
)

class DashboardViewModel {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
}
