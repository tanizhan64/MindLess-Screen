package com.mindless.screen.presentation.insights

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class InsightsUiState(
    val predictionText: String,
    val personalityText: String,
    val insightMessages: List<String>,
)

class InsightsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        InsightsUiState(
            predictionText = "Tomorrow: ~145 min (Moderate)",
            personalityText = "Social Media Addict (0.48)",
            insightMessages = listOf(
                "High social media share detected",
                "Late-night usage is increasing next-day risk",
            ),
        ),
    )

    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()
}
