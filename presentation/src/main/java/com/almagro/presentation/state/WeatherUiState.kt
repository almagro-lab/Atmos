package com.almagro.presentation.state

import com.almagro.domain.error.WeatherException
import com.almagro.presentation.model.WeatherUi

sealed interface WeatherUiState {

    data object Loading : WeatherUiState

    data class Success(val data: WeatherUi) : WeatherUiState

    data class Error(val exception: WeatherException) : WeatherUiState
}