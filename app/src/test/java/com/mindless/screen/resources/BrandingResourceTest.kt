package com.mindless.screen.resources

import com.mindless.screen.R
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BrandingResourceTest {

    @Test
    fun launcherBrandingResources_arePresent() {
        assertNotEquals(0, R.drawable.app_logo)
        assertNotEquals(0, R.drawable.app_icon)
        assertNotEquals(0, R.drawable.ic_launcher_foreground)
        assertNotEquals(0, R.mipmap.ic_launcher)
        assertNotEquals(0, R.mipmap.ic_launcher_round)
    }
}
