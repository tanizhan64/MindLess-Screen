package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.PermissionCapability
import com.mindless.screen.domain.model.StartupState
import com.mindless.screen.domain.repository.PermissionRepository

class StartupCoordinatorUseCase(
    private val permissionRepository: PermissionRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val trackingRepository: TrackingRepository,
    private val staleThresholdMinutes: Long = 15
) {

    operator fun invoke(): StartupState {
        val canTrackUsage = permissionRepository.isGranted(PermissionCapability.USAGE_ACCESS)
        val isPremium = subscriptionRepository.isPremium()
        val minutesSinceLastAggregation = trackingRepository.minutesSinceLastAggregation()

        return StartupState(
            canTrackUsage = canTrackUsage,
            isPremium = isPremium,
            shouldTriggerBackgroundAggregation =
                canTrackUsage && minutesSinceLastAggregation > staleThresholdMinutes
        )
    }
}

interface SubscriptionRepository {
    fun isPremium(): Boolean
}

interface TrackingRepository {
    fun minutesSinceLastAggregation(): Long
}
