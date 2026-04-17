package com.mindless.screen.presentation.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val personalityLabel: String,
    val focusLevelLabel: String,
    val streakLabel: String,
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            personalityLabel = "Personality: Social Media Addict",
            focusLevelLabel = "Focus Level: 2",
            streakLabel = "Streak: 7 days",
        ),
    )

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
