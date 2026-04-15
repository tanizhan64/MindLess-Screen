package com.mindless.screen.domain.model

sealed interface PermissionState {
    data object Ready : PermissionState
    data object UsageAccessRequired : PermissionState
    data object AccessibilityRequired : PermissionState
    data object NotificationsOptional : PermissionState
}
