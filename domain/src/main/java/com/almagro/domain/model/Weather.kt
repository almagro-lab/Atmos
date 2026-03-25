package com.almagro.domain.model

data class Weather(
    val id: Long = 0L,
    val cityName: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val condition: String,
    val description: String,
    val iconUrl: String,
    val humidity: Int,
    val windSpeedMs: Double,
    val windDirectionDeg: Int,
    val rainLastHourMm: Double?,
    val timezoneOffsetSeconds: Int,
)