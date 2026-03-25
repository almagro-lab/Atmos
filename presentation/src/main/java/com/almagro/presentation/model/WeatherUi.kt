package com.almagro.presentation.model

import com.almagro.domain.model.Weather

data class WeatherUi(
    val id: Long = 0L,
    val cityName: String,
    val coordinates: String,
    val temperature: String,
    val condition: String,
    val description: String,
    val iconUrl: String,
    val humidity: String,
    val wind: String,
    val rain: String?,
    val timezone: String,
)

fun Weather.toDisplayData(): WeatherUi {
    val coordinates = "%.2f, %.2f".format(latitude, longitude)
    val countryName = country
    return WeatherUi(
        id = id,
        cityName = when {
            cityName.isNotEmpty() && countryName != null -> "$cityName, $countryName"
            cityName.isNotEmpty() -> cityName
            countryName != null -> countryName
            else -> "Unknown"
        },
        coordinates = coordinates,
        temperature = "%.1f\u00B0C".format(temperature),
        condition = condition,
        description = description,
        iconUrl = iconUrl,
        humidity = "Humidity: $humidity%",
        wind = "Wind: ${"%.1f".format(windSpeedMs)} m/s \u00B7 $windDirectionDeg\u00B0",
        rain = rainLastHourMm?.let { "Rain: ${"%.1f".format(it)} mm/h" },
        timezone = (timezoneOffsetSeconds / 3600).let { h -> "UTC${if (h >= 0) "+" else ""}$h" },
    )
}
