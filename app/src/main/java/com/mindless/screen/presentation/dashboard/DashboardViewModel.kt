package com.mindless.screen.presentation.dashboard

import com.mindless.screen.domain.model.StartupState
import com.mindless.screen.domain.usecase.StartupCoordinatorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel(
    private val startupCoordinatorUseCase: StartupCoordinatorUseCase
) {

    private val _startupState = MutableStateFlow(
        StartupState(
            canTrackUsage = false,
            isPremium = false,
            shouldTriggerBackgroundAggregation = false
        )
    )
    val startupState: StateFlow<StartupState> = _startupState.asStateFlow()

    init {
        evaluateStartup()
    }

    fun evaluateStartup() {
        _startupState.value = startupCoordinatorUseCase()
    }
}
