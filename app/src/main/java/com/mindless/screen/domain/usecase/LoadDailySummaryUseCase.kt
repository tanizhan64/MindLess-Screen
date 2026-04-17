package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.repository.DailySummaryRepository

class LoadDailySummaryUseCase(
    private val repository: DailySummaryRepository
) {
    operator fun invoke() = repository.latestSummary()
}
