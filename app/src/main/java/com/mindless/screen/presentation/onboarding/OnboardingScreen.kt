package com.mindless.screen.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mindless.screen.domain.model.PermissionState

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = permissionStateMessage(state))

        when (state) {
            PermissionState.UsageAccessRequired -> {
                Button(onClick = { }) {
                    Text(text = "Grant usage access")
                }
            }

            PermissionState.AccessibilityRequired -> {
                Button(onClick = { }) {
                    Text(text = "Enable accessibility service")
                }
            }

            PermissionState.NotificationsOptional -> {
                Button(onClick = { }) {
                    Text(text = "Enable notifications (optional)")
                }
            }

            PermissionState.Ready -> {
                Button(onClick = { }) {
                    Text(text = "Continue")
                }
            }
        }
    }
}

private fun permissionStateMessage(state: PermissionState): String {
    return when (state) {
        PermissionState.UsageAccessRequired -> "Usage access permission is required"
        PermissionState.AccessibilityRequired -> "Accessibility service is required"
        PermissionState.NotificationsOptional -> "Notifications are optional"
        PermissionState.Ready -> "All required permissions are ready"
    }
}
