package com.almagro.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.almagro.domain.error.WeatherException
import com.almagro.presentation.model.WeatherUi
import com.almagro.presentation.state.WeatherUiState
import com.almagro.presentation.viewModel.WeatherViewModel

@Composable
fun WeatherScreen(
    onNavigateToHistory: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WeatherScreenContent(
        uiState = uiState,
        onRefresh = viewModel::fetchWeather,
        onNavigateToHistory = onNavigateToHistory,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherScreenContent(
    uiState: WeatherUiState,
    onRefresh: () -> Unit,
    onNavigateToHistory: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "View history",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh weather",
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                when (uiState) {
                    is WeatherUiState.Loading -> LoadingContent()
                    is WeatherUiState.Error -> ErrorContent(
                        message = uiState.exception.message ?: "Unknown error",
                        onRetry = onRefresh,
                    )
                    is WeatherUiState.Success -> WeatherContent(data = uiState.data)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun WeatherScreenLoadingPreview() {
    MaterialTheme {
        WeatherScreenContent(uiState = WeatherUiState.Loading, onRefresh = {}, onNavigateToHistory = {})
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun WeatherScreenErrorPreview() {
    MaterialTheme {
        WeatherScreenContent(
            uiState = WeatherUiState.Error(WeatherException.Network(Exception("Could not load weather. Check your connection."))),
            onRefresh = {},
            onNavigateToHistory = {},
        )
    }
}

@Preview(showBackground = true, name = "Success")
@Composable
private fun WeatherScreenSuccessPreview() {
    MaterialTheme {
        WeatherScreenContent(
            uiState = WeatherUiState.Success(
                data = WeatherUi(
                    cityName = "Cadiz, ES",
                    coordinates = "40.42, -3.70",
                    temperature = "24.5\u00B0C",
                    condition = "Clear",
                    description = "Clear sky",
                    iconUrl = "",
                    humidity = "Humidity: 42%",
                    wind = "Wind: 3.5 m/s \u00B7 180\u00B0",
                    rain = null,
                    timezone = "UTC+1",
                )
            ),
            onRefresh = {},
            onNavigateToHistory = {},
        )
    }
}

@Composable
private fun LoadingContent() {
    CircularProgressIndicator()
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}

@Composable
private fun WeatherContent(data: WeatherUi) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        WeatherIcon(iconUrl = data.iconUrl)
        Spacer(modifier = Modifier.height(14.dp))
        CityName(name = data.cityName)
        Spacer(modifier = Modifier.height(4.dp))
        Coordinates(text = data.coordinates)
        Spacer(modifier = Modifier.height(24.dp))
        Temperature(text = data.temperature)
        Spacer(modifier = Modifier.height(8.dp))
        WeatherCondition(condition = data.condition, description = data.description)
        Spacer(modifier = Modifier.height(16.dp))
        WeatherInfoRow(text = data.humidity)
        Spacer(modifier = Modifier.height(8.dp))
        WeatherInfoRow(text = data.wind)
        data.rain?.let {
            Spacer(modifier = Modifier.height(8.dp))
            WeatherInfoRow(text = it)
        }
        Spacer(modifier = Modifier.height(8.dp))
        WeatherInfoRow(text = data.timezone)
    }
}

@Composable
private fun WeatherIcon(iconUrl: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(128.dp),
    ) {
        AsyncImage(
            model = iconUrl,
            contentDescription = "Weather icon",
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun CityName(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun Coordinates(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun Temperature(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.displayLarge,
    )
}

@Composable
private fun WeatherCondition(condition: String, description: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = condition,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WeatherInfoRow(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
    )
}
