package com.mindless.screen.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class MindLessAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Plan 1 foundation: service declared and active wiring point.
    }

    override fun onInterrupt() {
        // No-op in Plan 1 foundation.
    }
}
