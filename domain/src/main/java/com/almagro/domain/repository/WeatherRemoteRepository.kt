package com.almagro.domain.repository

import com.almagro.domain.model.Weather

interface WeatherRemoteRepository {

    suspend fun fetchWeather(lat: Double, lon: Double): Result<Weather>
}
