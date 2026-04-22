package com.mindless.screen

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mindless.screen.domain.model.PermissionCapability
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class InsightsUiTest {

    private val permissionOverrideRule = TestRule { base: Statement, _: Description ->
        object : Statement() {
            override fun evaluate() {
                MainActivity.setPermissionOverridesForTesting(
                    mapOf(
                        PermissionCapability.USAGE_ACCESS to true,
                        PermissionCapability.ACCESSIBILITY_SERVICE to true,
                        PermissionCapability.POST_NOTIFICATIONS to true,
                        PermissionCapability.RECEIVE_BOOT_COMPLETED to true
                    )
                )
                try {
                    base.evaluate()
                } finally {
                    MainActivity.setPermissionOverridesForTesting(null)
                }
            }
        }
    }

    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val chain: TestRule = RuleChain.outerRule(permissionOverrideRule).around(composeRule)

    @Test
    fun dashboardToInsights_showsPredictionAndPersonalityValues() {
        composeRule.onNodeWithTag("dashboard_view_insights_button").performClick()
        composeRule.onNodeWithText("Tomorrow Prediction").assertExists()
        composeRule.onNodeWithText("Personality").assertExists()
        composeRule.onNodeWithTag("insights_prediction_value")
            .assertExists()
            .assertTextContains("Tomorrow forecast", substring = true)
        composeRule.onNodeWithTag("insights_personality_value")
            .assertExists()
            .assertTextContains("BALANCED_USER", substring = true)
    }

    @Test
    fun insightsToProfile_navigationWorks() {
        composeRule.onNodeWithTag("dashboard_view_insights_button").performClick()
        composeRule.onNodeWithTag("insights_open_profile_button").performClick()
        composeRule.onNodeWithText("Profile").assertExists()
        composeRule.onNodeWithText("Streak Badges").assertExists()
    }
}
