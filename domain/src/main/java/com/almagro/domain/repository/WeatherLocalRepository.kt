package com.almagro.domain.repository

import com.almagro.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherLocalRepository {

    suspend fun save(weather: Weather)

    fun getHistory(): Flow<List<Weather>>
}
