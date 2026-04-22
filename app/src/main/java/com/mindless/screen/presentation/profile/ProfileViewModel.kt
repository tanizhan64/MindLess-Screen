package com.mindless.screen.presentation.profile

import androidx.lifecycle.ViewModel
import com.mindless.screen.domain.repository.AiInsightsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val personalitySummary: String,
    val streakBadges: List<String>
)

class ProfileViewModel(
    private val repository: AiInsightsRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            personalitySummary = repository?.latestPersonalitySummary() ?: "Unknown",
            streakBadges = repository?.latestGamificationBadges() ?: emptyList()
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
