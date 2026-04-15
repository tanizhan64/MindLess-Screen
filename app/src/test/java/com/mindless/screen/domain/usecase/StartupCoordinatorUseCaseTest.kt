package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.PermissionCapability
import com.mindless.screen.domain.repository.PermissionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StartupCoordinatorUseCaseTest {

    @Test
    fun staleAggregate_triggersRefreshFlag() {
        val useCase = StartupCoordinatorUseCase(
            permissionRepository = FakePermissionRepository(
                grants = mapOf(PermissionCapability.USAGE_ACCESS to true)
            ),
            subscriptionRepository = FakeSubscriptionRepository(isPremium = false),
            trackingRepository = FakeTrackingRepository(lastAggregateAgeMinutes = 25),
            staleThresholdMinutes = 15
        )

        val state = useCase()

        assertTrue(state.shouldTriggerBackgroundAggregation)
        assertEquals(false, state.isPremium)
    }

    private class FakePermissionRepository(
        private val grants: Map<PermissionCapability, Boolean>
    ) : PermissionRepository {
        override fun isGranted(capability: PermissionCapability): Boolean = grants[capability] ?: false
    }

    private class FakeSubscriptionRepository(
        private val isPremium: Boolean
    ) : SubscriptionRepository {
        override fun isPremium(): Boolean = isPremium
    }

    private class FakeTrackingRepository(
        private val lastAggregateAgeMinutes: Long
    ) : TrackingRepository {
        override fun minutesSinceLastAggregation(): Long = lastAggregateAgeMinutes
    }
}
