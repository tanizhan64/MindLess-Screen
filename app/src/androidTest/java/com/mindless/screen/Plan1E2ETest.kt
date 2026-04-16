package com.mindless.screen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mindless.screen.domain.model.PermissionCapability
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement

@RunWith(AndroidJUnit4::class)
class Plan1E2ETest {

    private val permissionOverrideRule = TestRule { base: Statement, description: Description ->
        object : Statement() {
            override fun evaluate() {
                val overrides = when (description.methodName) {
                    "whenUsageAccessDenied_onboardingIsShown" -> mapOf(
                        PermissionCapability.USAGE_ACCESS to false,
                        PermissionCapability.ACCESSIBILITY_SERVICE to true,
                        PermissionCapability.POST_NOTIFICATIONS to true
                    )

                    "whenPermissionsReady_dashboardIsShown" -> mapOf(
                        PermissionCapability.USAGE_ACCESS to true,
                        PermissionCapability.ACCESSIBILITY_SERVICE to true,
                        PermissionCapability.POST_NOTIFICATIONS to true
                    )

                    else -> null
                }

                MainActivity.setPermissionOverridesForTesting(overrides)
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
    fun whenUsageAccessDenied_onboardingIsShown() {
        composeRule.onNodeWithText("Usage access permission is required").assertExists()
        composeRule.onNodeWithText("Enable Usage Access").assertExists()
    }

    @Test
    fun whenPermissionsReady_dashboardIsShown() {
        composeRule.onNodeWithText("Dashboard").assertExists()
        composeRule.onNodeWithText("Screen Time Today").assertExists()
    }
}
