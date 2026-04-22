package com.mindless.screen.presentation.insights

import androidx.lifecycle.ViewModel
import com.mindless.screen.domain.repository.AiInsightsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class InsightsUiState(
    val predictionText: String,
    val personalityText: String,
    val insightMessages: List<String>,
    val badges: List<String>
)

class InsightsViewModel(
    private val repository: AiInsightsRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    private fun buildUiState(): InsightsUiState {
        val messages = repository?.latestInsightsMessages() ?: emptyList()
        return InsightsUiState(
            predictionText = messages.firstOrNull() ?: "Prediction unavailable",
            personalityText = repository?.latestPersonalitySummary() ?: "Unknown",
            insightMessages = if (messages.size > 1) messages.drop(1) else emptyList(),
            badges = repository?.latestGamificationBadges() ?: emptyList()
        )
    }
}
