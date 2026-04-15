package com.mindless.screen.resources

import com.mindless.screen.R
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BrandingResourceTest {

    @Test
    fun launcherForegroundDrawableId_isNonZero() {
        assertNotEquals(0, R.drawable.ic_launcher_foreground)
    }
}
