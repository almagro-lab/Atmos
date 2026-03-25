package com.almagro.data.mapper

import com.almagro.data.model.WeatherEntity
import com.almagro.domain.model.Weather

fun Weather.toEntity(): WeatherEntity = WeatherEntity(
    id = 0,
    cityName = cityName,
    country = country,
    latitude = latitude,
    longitude = longitude,
    temperature = temperature,
    condition = condition,
    description = description,
    iconUrl = iconUrl,
    humidity = humidity,
    windSpeedMs = windSpeedMs,
    windDirectionDeg = windDirectionDeg,
    rainLastHourMm = rainLastHourMm,
    timezoneOffsetSeconds = timezoneOffsetSeconds,
)

fun WeatherEntity.toDomain(): Weather = Weather(
    id = id,
    cityName = cityName,
    country = country,
    latitude = latitude,
    longitude = longitude,
    temperature = temperature,
    condition = condition,
    description = description,
    iconUrl = iconUrl,
    humidity = humidity,
    windSpeedMs = windSpeedMs,
    windDirectionDeg = windDirectionDeg,
    rainLastHourMm = rainLastHourMm,
    timezoneOffsetSeconds = timezoneOffsetSeconds,
)
