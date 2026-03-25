package com.almagro.domain.useCase

import com.almagro.domain.model.Weather
import com.almagro.domain.repository.WeatherLocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherHistoryUseCase @Inject constructor(
    private val weatherLocalRepository: WeatherLocalRepository,
) {

    operator fun invoke(): Flow<List<Weather>> = weatherLocalRepository.getHistory()
}
