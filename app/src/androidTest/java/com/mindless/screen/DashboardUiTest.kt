package com.mindless.screen

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

class DashboardUiTest {

    private val permissionOverrideRule = TestRule { base: Statement, _: Description ->
        object : Statement() {
            override fun evaluate() {
                MainActivity.setPermissionOverridesForTesting(
                    mapOf(
                        PermissionCapability.USAGE_ACCESS to true,
                        PermissionCapability.ACCESSIBILITY_SERVICE to true,
                        PermissionCapability.POST_NOTIFICATIONS to true
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
    val ruleChain: TestRule = RuleChain
        .outerRule(permissionOverrideRule)
        .around(composeRule)

    @Test
    fun showsTopStatsCard() {
        composeRule.onNodeWithTag("dashboard_stats_card").assertExists()
        composeRule.onNodeWithText("Screen Time Today").assertExists()
        composeRule.onNodeWithText("Addiction Score").assertExists()
        composeRule.onNodeWithText("Unlock Count").assertExists()

        composeRule.onNodeWithText("View Reports").performClick()
        composeRule.onNodeWithText("Daily Report").assertExists()
        composeRule.onNodeWithText("Total Screen Time:", substring = true).assertExists()
        composeRule.onNodeWithText("Focus Time:", substring = true).assertExists()
    }
}
