package com.mindless.screen.data.tracking

import com.mindless.screen.data.local.dao.AggregateDao
import com.mindless.screen.domain.repository.DailySummary
import com.mindless.screen.domain.repository.DailySummaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailySummaryRepositoryImpl(
    private val aggregateDao: AggregateDao
) : DailySummaryRepository {

    override suspend fun latestSummary(): DailySummary? = withContext(Dispatchers.IO) {
        val latest = aggregateDao.latestDailyAggregate() ?: return@withContext null
        DailySummary(
            totalScreenTimeMillis = latest.totalScreenTimeMillis,
            unlockCount = latest.unlockCount,
            addictionScore = latest.addictionScore,
            focusTimeMillis = latest.focusTimeMillis
        )
    }
}
