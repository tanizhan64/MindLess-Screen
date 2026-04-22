package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.PermissionCapability
import com.mindless.screen.domain.model.StartupState
import com.mindless.screen.domain.repository.PermissionRepository
import com.mindless.screen.domain.repository.SubscriptionRepository
import com.mindless.screen.domain.repository.TrackingRepository

class StartupCoordinatorUseCase(
    private val permissionRepository: PermissionRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val trackingRepository: TrackingRepository,
    private val staleThresholdMinutes: Long = 15
) {

    operator fun invoke(): StartupState {
        val canTrackUsage = permissionRepository.isGranted(PermissionCapability.USAGE_ACCESS)
        val isPremium = subscriptionRepository.isPremium()
        val shouldTriggerBackgroundAggregation =
            if (!canTrackUsage) {
                false
            } else {
                trackingRepository.minutesSinceLastAggregation() > staleThresholdMinutes
            }

        return StartupState(
            canTrackUsage = canTrackUsage,
            isPremium = isPremium,
            shouldTriggerBackgroundAggregation = shouldTriggerBackgroundAggregation
        )
    }
}
