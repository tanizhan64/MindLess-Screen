package com.mindless.screen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
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
                        PermissionCapability.RECEIVE_BOOT_COMPLETED to true,
                    ),
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
    val ruleChain: TestRule = RuleChain
        .outerRule(permissionOverrideRule)
        .around(composeRule)

    @Test
    fun showsPredictionAndPersonalityCards() {
        composeRule.onNodeWithText("View Insights").assertExists()
        composeRule.onNodeWithText("View Profile").assertExists()

        composeRule.onNodeWithText("View Insights").performClick()
        composeRule.onNodeWithText("Insights").assertExists()
        composeRule.onNodeWithText("Tomorrow Prediction").assertExists()
        composeRule.onNodeWithText("Personality").assertExists()

        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithText("Insights").assertExists()
        composeRule.onNodeWithText("Tomorrow Prediction").assertExists()
        composeRule.onNodeWithText("Personality").assertExists()
        composeRule.onNodeWithText("High social media share detected").assertExists()

        pressBack()

        composeRule.onNodeWithText("View Profile").assertExists()
        composeRule.onNodeWithText("View Profile").performClick()
        composeRule.onNodeWithText("Profile").assertExists()
        composeRule.onNodeWithText("Personality: Social Media Addict").assertExists()
    }
}
