package com.mindless.screen.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onViewReportsClick: () -> Unit,
    onViewInsightsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(modifier = Modifier.fillMaxWidth().testTag("dashboard_stats_card")) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Screen Time Today")
                Text(text = uiState.screenTimeTodayLabel)
                Text(text = "Addiction Score")
                Text(text = uiState.addictionScoreLabel)
                Text(text = "Unlock Count")
                Text(text = uiState.unlockCountLabel)
            }
        }

        Button(onClick = onViewReportsClick) {
            Text(text = "View Reports")
        }

        Button(
            onClick = onViewInsightsClick,
            modifier = Modifier.testTag("dashboard_view_insights_button")
        ) {
            Text(text = "View Insights")
        }
    }
}
