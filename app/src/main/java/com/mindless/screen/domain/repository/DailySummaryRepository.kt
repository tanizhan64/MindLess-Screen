package com.mindless.screen.domain.repository

data class DailySummary(
    val totalScreenTimeMillis: Long,
    val unlockCount: Int,
    val addictionScore: Double,
    val focusTimeMillis: Long
)

interface DailySummaryRepository {
    fun latestSummary(): DailySummary?
}
