package com.mindless.screen.data.tracking

import com.mindless.screen.data.local.dao.AggregateDao
import com.mindless.screen.domain.repository.DailySummary
import com.mindless.screen.domain.repository.DailySummaryRepository

class DailySummaryRepositoryImpl(
    private val aggregateDao: AggregateDao
) : DailySummaryRepository {

    override fun latestSummary(): DailySummary? {
        val latest = aggregateDao.latestDailyAggregate() ?: return null
        return DailySummary(
            totalScreenTimeMillis = latest.totalScreenTimeMillis,
            unlockCount = latest.unlockCount,
            addictionScore = latest.addictionScore,
            focusTimeMillis = latest.focusTimeMillis
        )
    }
}
