package com.mindless.screen.presentation.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun InsightsScreen(
    uiState: InsightsUiState,
    onOpenProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Insights", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            Text("Tomorrow Prediction")
            Text(
                text = uiState.predictionText,
                modifier = Modifier.testTag("insights_prediction_value")
            )
        }
        item {
            Text("Personality")
            Text(
                text = uiState.personalityText,
                modifier = Modifier.testTag("insights_personality_value")
            )
        }
        item {
            Text("Gamification")
            Text(uiState.badges.joinToString())
        }
        items(uiState.insightMessages) { message ->
            Text(message)
        }
        item {
            Button(
                onClick = onOpenProfileClick,
                modifier = Modifier.testTag("insights_open_profile_button")
            ) {
                Text("Open Profile")
            }
        }
    }
}
