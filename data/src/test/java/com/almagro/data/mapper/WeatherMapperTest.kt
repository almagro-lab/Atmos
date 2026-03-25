package com.almagro.data.mapper

import com.almagro.data.model.CoordDto
import com.almagro.data.model.MainDto
import com.almagro.data.model.RainDto
import com.almagro.data.model.WeatherConditionDto
import com.almagro.data.model.SysDto
import com.almagro.data.model.WeatherResponseDto
import com.almagro.data.model.WindDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WeatherMapperTest {

    @Test
    fun `GIVEN a full weather response WHEN mapped to domain THEN all fields are populated correctly`() {
        val dto = weatherResponseDto(
            conditions = listOf(
                WeatherConditionDto(main = "Clear", description = "clear sky", icon = "01d")
            )
        )

        val result = dto.toDomain()

        assertEquals("Madrid", result.cityName)
        assertEquals("ES", result.country)
        assertEquals(40.42, result.latitude)
        assertEquals(-3.70, result.longitude)
        assertEquals(25.5, result.temperature)
        assertEquals("Clear", result.condition)
        assertEquals("Clear sky", result.description)
        assertEquals("https://openweathermap.org/img/wn/01d@2x.png", result.iconUrl)
        assertEquals(60, result.humidity)
        assertEquals(0.62, result.windSpeedMs)
        assertEquals(349, result.windDirectionDeg)
        assertEquals(3.16, result.rainLastHourMm)
        assertEquals(7200, result.timezoneOffsetSeconds)
    }

    @Test
    fun `GIVEN a response with no weather conditions WHEN mapped to domain THEN condition fields are empty strings`() {
        val dto = weatherResponseDto(conditions = emptyList())

        val result = dto.toDomain()

        assertEquals("", result.condition)
        assertEquals("", result.description)
        assertEquals("", result.iconUrl)
    }

    @Test
    fun `GIVEN rain is absent WHEN mapped THEN rainLastHourMm is null`() {
        val dto = weatherResponseDto(
            conditions = listOf(
                WeatherConditionDto(main = "Clear", description = "clear sky", icon = "01d")
            ),
            rain = null,
        )

        val result = dto.toDomain()

        assertEquals(null, result.rainLastHourMm)
    }

    @Test
    fun `GIVEN rain is present WHEN mapped THEN rainLastHourMm contains the value`() {
        val dto = weatherResponseDto(
            conditions = listOf(
                WeatherConditionDto(main = "Rain", description = "light rain", icon = "10d")
            ),
            rain = RainDto(oneHour = 5.0),
        )

        val result = dto.toDomain()

        assertEquals(5.0, result.rainLastHourMm)
    }

    @Test
    fun `GIVEN a weather condition with an icon code WHEN mapped to domain THEN icon url includes 2x suffix`() {
        val dto = weatherResponseDto(
            conditions = listOf(
                WeatherConditionDto(main = "Rain", description = "light rain", icon = "10n")
            )
        )

        val result = dto.toDomain()

        assertEquals("https://openweathermap.org/img/wn/10n@2x.png", result.iconUrl)
    }

    @Test
    fun `GIVEN sys is absent WHEN mapped THEN country is null`() {
        val dto = weatherResponseDto(
            conditions = listOf(
                WeatherConditionDto(main = "Clear", description = "clear sky", icon = "01d")
            ),
            sys = null,
        )

        val result = dto.toDomain()

        assertEquals(null, result.country)
    }

    private fun weatherResponseDto(
        conditions: List<WeatherConditionDto>,
        wind: WindDto = WindDto(speed = 0.62, deg = 349),
        rain: RainDto? = RainDto(oneHour = 3.16),
        sys: SysDto? = SysDto(country = "ES"),
        timezone: Int = 7200,
    ) = WeatherResponseDto(
        coord = CoordDto(lat = 40.42, lon = -3.70),
        weather = conditions,
        main = MainDto(temp = 25.5, humidity = 60),
        wind = wind,
        rain = rain,
        sys = sys,
        timezone = timezone,
        name = "Madrid",
    )
}
