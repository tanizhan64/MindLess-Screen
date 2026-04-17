package com.mindless.screen.data.tracking

import com.mindless.screen.domain.repository.DailySummary
import com.mindless.screen.domain.repository.DailySummaryRepository

class InMemoryDailySummaryRepository : DailySummaryRepository {
    override suspend fun latestSummary(): DailySummary {
        return DailySummary(
            totalScreenTimeMillis = 7_200_000,
            unlockCount = 42,
            addictionScore = 3.8,
            focusTimeMillis = 3_000_000
        )
    }
}
