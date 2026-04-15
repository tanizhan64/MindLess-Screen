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
}
