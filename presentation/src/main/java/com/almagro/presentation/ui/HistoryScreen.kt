package com.almagro.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almagro.presentation.model.WeatherUi
import com.almagro.presentation.viewModel.HistoryViewModel

@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    HistoryScreenContent(history = history, onNavigateBack = onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryScreenContent(
    history: List<WeatherUi>,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Recent Locations") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            itemsIndexed(history, key = { _, item -> item.id }) { index, item ->
                HistoryRow(
                    cityName = item.cityName,
                    temperature = item.temperature,
                    coordinates = item.coordinates,
                )
                if (index < history.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(cityName: String, temperature: String, coordinates: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = cityName,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = coordinates,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = temperature,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    val sampleHistory = listOf(
        WeatherUi(
            cityName = "Madrid, ES",
            coordinates = "40.42, -3.70",
            temperature = "22.5\u00B0C",
            condition = "Clear",
            description = "clear sky",
            iconUrl = "",
            humidity = "Humidity: 45%",
            wind = "Wind: 0.6 m/s \u00B7 349\u00B0",
            rain = null,
            timezone = "UTC+2",
        ),
        WeatherUi(
            cityName = "London, GB",
            coordinates = "51.51, -0.13",
            temperature = "15.2\u00B0C",
            condition = "Clouds",
            description = "overcast clouds",
            iconUrl = "",
            humidity = "Humidity: 72%",
            wind = "Wind: 4.1 m/s \u00B7 220\u00B0",
            rain = null,
            timezone = "UTC+0",
        ),
        WeatherUi(
            cityName = "Tokyo, JP",
            coordinates = "35.68, 139.69",
            temperature = "28.0\u00B0C",
            condition = "Rain",
            description = "light rain",
            iconUrl = "",
            humidity = "Humidity: 80%",
            wind = "Wind: 2.3 m/s \u00B7 90\u00B0",
            rain = "Rain: 0.5 mm/h",
            timezone = "UTC+9",
        ),
    )
    MaterialTheme {
        HistoryScreenContent(history = sampleHistory, onNavigateBack = {})
    }
}
