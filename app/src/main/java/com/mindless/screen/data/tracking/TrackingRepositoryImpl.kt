package com.mindless.screen.data.tracking

import com.mindless.screen.data.local.dao.TrackingDao
import com.mindless.screen.domain.repository.TrackingRepository
import kotlin.math.max

class TrackingRepositoryImpl(
    private val trackingDao: TrackingDao
) : TrackingRepository {

    override fun minutesSinceLastAggregation(): Long {
        val latestEpochMillis = trackingDao.latestDailyAggregateEpochMillis()
        if (latestEpochMillis <= 0L) {
            return Long.MAX_VALUE
        }

        val nowEpochMillis = System.currentTimeMillis()
        val ageMillis = max(0L, nowEpochMillis - latestEpochMillis)
        return ageMillis / 60_000L
    }
}
