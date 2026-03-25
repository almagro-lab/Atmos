package com.almagro.domain.useCase

import javax.inject.Inject
import com.almagro.domain.model.Weather
import com.almagro.domain.repository.WeatherRemoteRepository
import com.almagro.domain.utils.LocationGenerator

class FetchRandomWeatherUseCase @Inject constructor(
    private val weatherRemoteRepository: WeatherRemoteRepository,
    private val locationGenerator: LocationGenerator,
) {

    suspend operator fun invoke(): Result<Weather> {
        val (lat, lon) = locationGenerator.generateRandomLatLon()
        return weatherRemoteRepository.fetchWeather(lat, lon)
    }
}
