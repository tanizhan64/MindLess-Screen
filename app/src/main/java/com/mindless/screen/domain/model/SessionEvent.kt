package com.mindless.screen.domain.model

sealed interface SessionEvent {
    val timestampMillis: Long

    data class ScreenOn(
        override val timestampMillis: Long
    ) : SessionEvent

    data class ForegroundApp(
        override val timestampMillis: Long,
        val packageName: String
    ) : SessionEvent

    data class ScreenOff(
        override val timestampMillis: Long
    ) : SessionEvent

    data class Inactivity(
        override val timestampMillis: Long
    ) : SessionEvent
}
