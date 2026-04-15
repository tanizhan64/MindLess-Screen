package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.SessionEvent
import com.mindless.screen.domain.model.SessionWindow
import org.junit.Assert.assertEquals
import org.junit.Test

class DetectSessionUseCaseTest {

    @Test
    fun endsSession_whenInactivityExceeds30Seconds() {
        val useCase = DetectSessionUseCase(inactivityTimeoutMillis = 30_000)

        val sessionWindow = useCase(
            listOf(
                SessionEvent.ScreenOn(timestampMillis = 0),
                SessionEvent.ForegroundApp(timestampMillis = 1_000, packageName = "com.example.app"),
                SessionEvent.Inactivity(timestampMillis = 32_001)
            )
        )

        assertEquals(SessionWindow.EndReason.INACTIVITY_TIMEOUT, sessionWindow.endReason)
    }

    @Test
    fun doesNotEndSession_whenInactivityIsExactly30Seconds() {
        val useCase = DetectSessionUseCase(inactivityTimeoutMillis = 30_000)

        val sessionWindow = useCase(
            listOf(
                SessionEvent.ScreenOn(timestampMillis = 0),
                SessionEvent.ForegroundApp(timestampMillis = 1_000, packageName = "com.example.app"),
                SessionEvent.Inactivity(timestampMillis = 31_000)
            )
        )

        assertEquals(null, sessionWindow.endReason)
        assertEquals(null, sessionWindow.endEpochMillis)
    }

    @Test
    fun ignoresEventsBeforeFirstScreenOn_whenDeterminingSessionEnd() {
        val useCase = DetectSessionUseCase(inactivityTimeoutMillis = 30_000)

        val sessionWindow = useCase(
            listOf(
                SessionEvent.ScreenOff(timestampMillis = 200),
                SessionEvent.Inactivity(timestampMillis = 500),
                SessionEvent.ScreenOn(timestampMillis = 1_000),
                SessionEvent.ForegroundApp(timestampMillis = 2_000, packageName = "com.example.app"),
                SessionEvent.ScreenOff(timestampMillis = 2_500)
            )
        )

        assertEquals(1_000, sessionWindow.startEpochMillis)
        assertEquals(2_500, sessionWindow.endEpochMillis)
        assertEquals(SessionWindow.EndReason.SCREEN_OFF, sessionWindow.endReason)
    }
}
