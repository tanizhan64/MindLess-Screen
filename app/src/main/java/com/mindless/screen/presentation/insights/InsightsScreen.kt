package com.mindless.screen.presentation.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InsightsScreen(
    uiState: InsightsUiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Insights",
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Tomorrow Prediction")
                Text(text = uiState.predictionText)
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Personality")
                Text(text = uiState.personalityText)
            }
        }

        items(uiState.insightMessages) { message ->
            Text(text = message)
        }
    }
}
