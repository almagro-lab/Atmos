package com.almagro.presentation.viewModel

import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almagro.domain.error.WeatherException
import com.almagro.domain.useCase.FetchRandomWeatherUseCase
import com.almagro.domain.useCase.SaveWeatherUseCase
import com.almagro.presentation.model.toDisplayData
import com.almagro.presentation.state.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val fetchRandomWeather: FetchRandomWeatherUseCase,
    private val saveWeather: SaveWeatherUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        fetchWeather()
    }

    fun fetchWeather() {
        _uiState.value = WeatherUiState.Loading
        viewModelScope.launch {
            fetchRandomWeather()
                .onSuccess { weather ->
                    runCatching { saveWeather(weather) }
                    _uiState.value = WeatherUiState.Success(weather.toDisplayData())
                }
                .onFailure { error ->
                    val exception = error as? WeatherException ?: WeatherException.Network(error)
                    _uiState.value = WeatherUiState.Error(exception)
                }
        }
    }
}
