package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.SessionEvent
import com.mindless.screen.domain.model.SessionWindow

class DetectSessionUseCase(
    private val inactivityTimeoutMillis: Long = 30_000
) {
    operator fun invoke(events: List<SessionEvent>): SessionWindow {
        val startIndex = events.indexOfFirst { it is SessionEvent.ScreenOn }
        if (startIndex == -1) {
            error("Session requires a ScreenOn event")
        }

        val startEvent = events[startIndex] as SessionEvent.ScreenOn
        var lastActiveMillis = startEvent.timestampMillis

        for (event in events.drop(startIndex)) {
            when (event) {
                is SessionEvent.ScreenOn,
                is SessionEvent.ForegroundApp -> {
                    lastActiveMillis = event.timestampMillis
                }

                is SessionEvent.ScreenOff -> {
                    return SessionWindow(
                        startEpochMillis = startEvent.timestampMillis,
                        endEpochMillis = event.timestampMillis,
                        endReason = SessionWindow.EndReason.SCREEN_OFF
                    )
                }

                is SessionEvent.Inactivity -> {
                    if (event.timestampMillis - lastActiveMillis > inactivityTimeoutMillis) {
                        return SessionWindow(
                            startEpochMillis = startEvent.timestampMillis,
                            endEpochMillis = event.timestampMillis,
                            endReason = SessionWindow.EndReason.INACTIVITY_TIMEOUT
                        )
                    }
                }
            }
        }

        return SessionWindow(startEpochMillis = startEvent.timestampMillis)
    }
}
