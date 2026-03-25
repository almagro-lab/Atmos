package com.almagro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_history")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
