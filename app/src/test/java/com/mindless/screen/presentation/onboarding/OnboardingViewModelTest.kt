package com.mindless.screen.presentation.onboarding

import com.mindless.screen.domain.model.PermissionCapability
import com.mindless.screen.domain.model.PermissionState
import com.mindless.screen.domain.repository.PermissionRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingViewModelTest {

    @Test
    fun usageAccessDenied_setsUsageAccessRequiredState() {
        val permissionRepository = FakePermissionRepository(
            grants = mapOf(
                PermissionCapability.USAGE_ACCESS to false,
                PermissionCapability.ACCESSIBILITY_SERVICE to true,
                PermissionCapability.POST_NOTIFICATIONS to true,
                PermissionCapability.RECEIVE_BOOT_COMPLETED to true
            )
        )
        val viewModel = OnboardingViewModel(permissionRepository)

        viewModel.refresh()

        assertEquals(PermissionState.UsageAccessRequired, viewModel.uiState.value)
    }

    private class FakePermissionRepository(
        private val grants: Map<PermissionCapability, Boolean>
    ) : PermissionRepository {
        override fun isGranted(capability: PermissionCapability): Boolean {
            return grants[capability] ?: false
        }
    }
}
