package com.almagro.data.mapper

import com.almagro.domain.model.Weather
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WeatherEntityMapperTest {

    @Test
    fun `GIVEN a weather domain model WHEN mapped to entity and back THEN all fields are preserved`() {
        val original = weather()

        val domain = original.toEntity().toDomain()

        assertEquals(original, domain)
    }

    @Test
    fun `GIVEN a weather with null optional fields WHEN mapped to entity and back THEN nulls are preserved`() {
        val original = weather(country = null, rainLastHourMm = null)

        val domain = original.toEntity().toDomain()

        assertEquals(original, domain)
    }

    @Test
    fun `GIVEN a weather domain model WHEN mapped to entity THEN id is zero for auto-generation`() {
        val entity = weather().toEntity()

        assertEquals(0, entity.id)
    }

    private fun weather(
        country: String? = "ES",
        rainLastHourMm: Double? = 3.16,
    ) = Weather(
        cityName = "Madrid",
        country = country,
        latitude = 40.42,
        longitude = -3.70,
        temperature = 25.5,
        condition = "Clear",
        description = "clear sky",
        iconUrl = "https://openweathermap.org/img/wn/01d@2x.png",
        humidity = 60,
        windSpeedMs = 0.62,
        windDirectionDeg = 349,
        rainLastHourMm = rainLastHourMm,
        timezoneOffsetSeconds = 7200,
    )
}
