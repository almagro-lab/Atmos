package com.almagro.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val coord: CoordDto,
    val weather: List<WeatherConditionDto>,
    val main: MainDto,
    val wind: WindDto,
    val rain: RainDto? = null,
    val sys: SysDto? = null,
    val timezone: Int,
    val name: String,
)

@Serializable
data class SysDto(val country: String? = null)

@Serializable
data class CoordDto(
    val lat: Double,
    val lon: Double
)

@Serializable
data class WeatherConditionDto(
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class MainDto(
    val temp: Double,
    val humidity: Int
)

@Serializable
data class WindDto(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null,
)

@Serializable
data class RainDto(
    @SerialName("1h")
    val oneHour: Double,
)
