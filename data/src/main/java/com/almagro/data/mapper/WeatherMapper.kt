package com.almagro.data.mapper

import com.almagro.data.model.WeatherResponseDto
import com.almagro.domain.model.Weather

private const val ICON_BASE_URL = "https://openweathermap.org/img/wn/"

fun WeatherResponseDto.toDomain(): Weather {
    val condition = weather.firstOrNull()
    return Weather(
        cityName = name,
        country = sys?.country,
        latitude = coord.lat,
        longitude = coord.lon,
        temperature = main.temp,
        condition = condition?.main.orEmpty().replaceFirstChar { it.uppercase() },
        description = condition?.description.orEmpty().replaceFirstChar { it.uppercase() },
        iconUrl = condition?.icon?.let { "$ICON_BASE_URL${it}@2x.png" }.orEmpty(),
        humidity = main.humidity,
        windSpeedMs = wind.speed,
        windDirectionDeg = wind.deg,
        rainLastHourMm = rain?.oneHour,
        timezoneOffsetSeconds = timezone,
    )
}
