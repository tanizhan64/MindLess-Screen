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
        val hasUsageAccess = permissionRepository.isGranted(PermissionCapability.USAGE_ACCESS)
        val hasAccessibilityService = permissionRepository.isGranted(PermissionCapability.ACCESSIBILITY_SERVICE)
        val hasPostNotifications = permissionRepository.isGranted(PermissionCapability.POST_NOTIFICATIONS)
        val hasReceiveBootCompleted = permissionRepository.isGranted(PermissionCapability.RECEIVE_BOOT_COMPLETED)

        _uiState.value = when {
            !hasUsageAccess -> PermissionState.UsageAccessRequired
            !hasAccessibilityService -> PermissionState.AccessibilityRequired
            !hasPostNotifications || !hasReceiveBootCompleted -> PermissionState.NotificationsOptional
            else -> PermissionState.Ready
        }
    }
}
