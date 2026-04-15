package com.mindless.screen.presentation.onboarding

import com.mindless.screen.domain.model.PermissionCapability
import com.mindless.screen.domain.model.PermissionState
import com.mindless.screen.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel(
    private val permissionRepository: PermissionRepository
) {

    private val _uiState = MutableStateFlow<PermissionState>(PermissionState.UsageAccessRequired)
    val uiState: StateFlow<PermissionState> = _uiState.asStateFlow()

    fun refresh() {
        _uiState.value = when {
            !permissionRepository.isGranted(PermissionCapability.USAGE_ACCESS) -> {
                PermissionState.UsageAccessRequired
            }

            !permissionRepository.isGranted(PermissionCapability.ACCESSIBILITY_SERVICE) -> {
                PermissionState.AccessibilityRequired
            }

            !permissionRepository.isGranted(PermissionCapability.POST_NOTIFICATIONS) -> {
                PermissionState.NotificationsOptional
            }

            else -> PermissionState.Ready
        }
    }
}
