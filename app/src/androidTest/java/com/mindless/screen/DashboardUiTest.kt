package com.mindless.screen

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class DashboardUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsTopStatsCard() {
        composeTestRule.onNodeWithText("Screen Time Today").assertExists()
        composeTestRule.onNodeWithText("Addiction Score").assertExists()
        composeTestRule.onNodeWithText("Unlock Count").assertExists()
    }
}
