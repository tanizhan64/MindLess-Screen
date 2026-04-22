package com.mindless.screen.domain.repository

interface TrackingRepository {
    fun minutesSinceLastAggregation(): Long
}
