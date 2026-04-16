package com.mindless.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.mindless.screen.data.permission.PermissionRepositoryImpl
import com.mindless.screen.domain.model.PermissionCapability
import com.mindless.screen.navigation.MindLessNavHost
import com.mindless.screen.presentation.onboarding.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val permissionOverrides = mutableMapOf<PermissionCapability, Boolean>()
        permissionOverridesForTesting?.let { permissionOverrides.putAll(it) }

        val permissionRepository = PermissionRepositoryImpl(
            context = applicationContext,
            initialGrants = permissionOverrides
        )
        val startInOnboarding = !permissionRepository.isGranted(PermissionCapability.USAGE_ACCESS)

        setContent {
            val onboardingViewModel = remember { OnboardingViewModel(permissionRepository) }
            MindLessNavHost(
                startInOnboarding = startInOnboarding,
                onboardingViewModel = onboardingViewModel
            )
        }
    }

    companion object {
        @Volatile
        private var permissionOverridesForTesting: Map<PermissionCapability, Boolean>? = null

        fun setPermissionOverridesForTesting(overrides: Map<PermissionCapability, Boolean>?) {
            permissionOverridesForTesting = overrides
        }
    }
}
