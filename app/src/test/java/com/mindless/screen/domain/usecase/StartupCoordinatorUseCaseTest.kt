package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.PermissionCapability
import com.mindless.screen.domain.repository.PermissionRepository
import com.mindless.screen.domain.repository.SubscriptionRepository
import com.mindless.screen.domain.repository.TrackingRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StartupCoordinatorUseCaseTest {

    @Test
    fun permissionDenied_doesNotQueryTrackingAndDoesNotTriggerRefresh() {
        val trackingRepository = CountingTrackingRepository(lastAggregateAgeMinutes = 25)
        val useCase = StartupCoordinatorUseCase(
            permissionRepository = FakePermissionRepository(
                grants = mapOf(PermissionCapability.USAGE_ACCESS to false)
            ),
            subscriptionRepository = FakeSubscriptionRepository(isPremium = false),
            trackingRepository = trackingRepository,
            staleThresholdMinutes = 15
        )

        val state = useCase()

        assertFalse(state.shouldTriggerBackgroundAggregation)
        assertEquals(0, trackingRepository.queryCount)
    }

    @Test
    fun thresholdBoundary_equalToThreshold_doesNotTriggerRefresh() {
        val useCase = StartupCoordinatorUseCase(
            permissionRepository = FakePermissionRepository(
                grants = mapOf(PermissionCapability.USAGE_ACCESS to true)
            ),
            subscriptionRepository = FakeSubscriptionRepository(isPremium = false),
            trackingRepository = CountingTrackingRepository(lastAggregateAgeMinutes = 15),
            staleThresholdMinutes = 15
        )

        val state = useCase()

        assertFalse(state.shouldTriggerBackgroundAggregation)
    }

    @Test
    fun freshAggregate_belowThreshold_doesNotTriggerRefresh() {
        val useCase = StartupCoordinatorUseCase(
            permissionRepository = FakePermissionRepository(
                grants = mapOf(PermissionCapability.USAGE_ACCESS to true)
            ),
            subscriptionRepository = FakeSubscriptionRepository(isPremium = false),
            trackingRepository = CountingTrackingRepository(lastAggregateAgeMinutes = 10),
            staleThresholdMinutes = 15
        )

        val state = useCase()

        assertFalse(state.shouldTriggerBackgroundAggregation)
    }

    @Test
    fun premiumSubscription_propagatesToStartupState() {
        val useCase = StartupCoordinatorUseCase(
            permissionRepository = FakePermissionRepository(
                grants = mapOf(PermissionCapability.USAGE_ACCESS to true)
            ),
            subscriptionRepository = FakeSubscriptionRepository(isPremium = true),
            trackingRepository = CountingTrackingRepository(lastAggregateAgeMinutes = 25),
            staleThresholdMinutes = 15
        )

        val state = useCase()

        assertTrue(state.isPremium)
    }

    @Test
    fun staleAggregate_triggersRefreshFlag() {
        val useCase = StartupCoordinatorUseCase(
            permissionRepository = FakePermissionRepository(
                grants = mapOf(PermissionCapability.USAGE_ACCESS to true)
            ),
            subscriptionRepository = FakeSubscriptionRepository(isPremium = false),
            trackingRepository = CountingTrackingRepository(lastAggregateAgeMinutes = 25),
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

    private class CountingTrackingRepository(
        private val lastAggregateAgeMinutes: Long
    ) : TrackingRepository {
        var queryCount: Int = 0
            private set

        override fun minutesSinceLastAggregation(): Long {
            queryCount += 1
            return lastAggregateAgeMinutes
        }
    }
}
