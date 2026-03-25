package com.almagro.presentation

import com.almagro.domain.model.Weather
import com.almagro.presentation.model.toDisplayData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class WeatherUiTest {

    @Test
    fun `GIVEN a weather model WHEN mapped to display data THEN all fields are formatted correctly`() {
        val weather = weather()

        val result = weather.toDisplayData()

        assertEquals("Madrid, ES", result.cityName)
        assertEquals("40.42, -3.70", result.coordinates)
        assertEquals("22.5\u00B0C", result.temperature)
        assertEquals("Clear", result.condition)
        assertEquals("Clear sky", result.description)
        assertEquals("https://example.com/icon.png", result.iconUrl)
        assertEquals("Humidity: 45%", result.humidity)
        assertEquals("Wind: 0.6 m/s \u00B7 349\u00B0", result.wind)
        assertNull(result.rain)
        assertEquals("UTC+2", result.timezone)
    }

    @Test
    fun `GIVEN rain is present WHEN mapped to display data THEN rain field is formatted`() {
        val weather = weather(rainLastHourMm = 3.16)

        val result = weather.toDisplayData()

        assertEquals("Rain: 3.2 mm/h", result.rain)
    }

    @Test
    fun `GIVEN negative timezone offset WHEN mapped to display data THEN timezone shows minus sign`() {
        val weather = weather(timezoneOffsetSeconds = -18000)

        val result = weather.toDisplayData()

        assertEquals("UTC-5", result.timezone)
    }

    @Test
    fun `GIVEN zero timezone offset WHEN mapped to display data THEN timezone shows plus zero`() {
        val weather = weather(timezoneOffsetSeconds = 0)

        val result = weather.toDisplayData()

        assertEquals("UTC+0", result.timezone)
    }

    @Test
    fun `GIVEN country is absent WHEN mapped THEN cityName shows city only`() {
        val result = weather(country = null).toDisplayData()

        assertEquals("Madrid", result.cityName)
    }

    @Test
    fun `GIVEN city name is empty and country is present WHEN mapped THEN cityName shows country only`() {
        val result = weather(cityName = "", country = "ES").toDisplayData()

        assertEquals("ES", result.cityName)
    }

    @Test
    fun `GIVEN both city name and country are absent WHEN mapped THEN cityName shows Unknown`() {
        val result = weather(cityName = "", country = null).toDisplayData()

        assertEquals("Unknown", result.cityName)
    }

    private fun weather(
        cityName: String = "Madrid",
        country: String? = "ES",
        rainLastHourMm: Double? = null,
        timezoneOffsetSeconds: Int = 7200,
    ) = Weather(
        cityName = cityName,
        country = country,
        latitude = 40.42,
        longitude = -3.70,
        temperature = 22.5,
        condition = "Clear",
        description = "Clear sky",
        iconUrl = "https://example.com/icon.png",
        humidity = 45,
        windSpeedMs = 0.62,
        windDirectionDeg = 349,
        rainLastHourMm = rainLastHourMm,
        timezoneOffsetSeconds = timezoneOffsetSeconds,
    )
}
