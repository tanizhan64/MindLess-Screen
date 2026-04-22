package com.mindless.screen.domain.model

data class InsightCard(
    val severity: InsightSeverity,
    val message: String
)

enum class InsightSeverity {
    INFO,
    WARNING
}
