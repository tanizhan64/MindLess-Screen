package com.mindless.screen.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mindless.screen.domain.repository.DailySummaryRepository

data class DailyReportUiState(
    val totalScreenTimeText: String,
    val addictionScoreText: String,
    val unlockCountText: String,
    val focusTimeText: String
)

@Composable
fun DailyReportScreen(
    modifier: Modifier = Modifier,
    dailySummaryRepository: DailySummaryRepository? = null,
) {
    var uiState by remember {
        mutableStateOf(
            DailyReportUiState(
                totalScreenTimeText = "Total Screen Time: 0m",
                addictionScoreText = "Addiction Score: 0.0",
                unlockCountText = "Unlock Count: 0",
                focusTimeText = "Focus Time: 0m"
            )
        )
    }

    LaunchedEffect(dailySummaryRepository) {
        val summary = dailySummaryRepository?.latestSummary() ?: return@LaunchedEffect
        uiState = DailyReportUiState(
            totalScreenTimeText = "Total Screen Time: ${summary.totalScreenTimeMillis / 60_000}m",
            addictionScoreText = "Addiction Score: %.1f".format(summary.addictionScore),
            unlockCountText = "Unlock Count: ${summary.unlockCount}",
            focusTimeText = "Focus Time: ${summary.focusTimeMillis / 60_000}m"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Daily Report",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(text = uiState.totalScreenTimeText)
        Text(text = uiState.addictionScoreText)
        Text(text = uiState.unlockCountText)
        Text(text = uiState.focusTimeText)
    }
}
