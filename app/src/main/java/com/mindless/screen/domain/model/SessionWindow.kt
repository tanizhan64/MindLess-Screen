package com.mindless.screen.domain.model

data class SessionWindow(
    val startEpochMillis: Long,
    val endEpochMillis: Long? = null,
    val endReason: EndReason? = null
) {
    enum class EndReason {
        SCREEN_OFF,
        INACTIVITY_TIMEOUT
    }
}
