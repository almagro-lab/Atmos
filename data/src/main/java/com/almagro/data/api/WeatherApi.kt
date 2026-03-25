package com.almagro.data.api

import com.almagro.data.model.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun fetchWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponseDto
}
