package com.almagro.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almagro.domain.useCase.GetWeatherHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import com.almagro.presentation.model.WeatherUi
import com.almagro.presentation.model.toDisplayData

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getWeatherHistory: GetWeatherHistoryUseCase,
) : ViewModel() {

    val history: StateFlow<List<WeatherUi>> = getWeatherHistory()
        .map { list -> list.map { it.toDisplayData() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
