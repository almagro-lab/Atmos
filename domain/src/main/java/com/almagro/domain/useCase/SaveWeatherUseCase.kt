package com.almagro.domain.useCase

import com.almagro.domain.model.Weather
import com.almagro.domain.repository.WeatherLocalRepository
import javax.inject.Inject

class SaveWeatherUseCase @Inject constructor(
    private val weatherLocalRepository: WeatherLocalRepository,
) {

    suspend operator fun invoke(weather: Weather) = weatherLocalRepository.save(weather)
}
