package com.almagro.data.repository

import com.almagro.data.api.WeatherApi
import com.almagro.data.dao.WeatherDao
import com.almagro.data.mapper.HttpExceptionMapper
import com.almagro.data.model.CoordDto
import com.almagro.data.model.MainDto
import com.almagro.data.model.WeatherConditionDto
import com.almagro.data.model.SysDto
import com.almagro.data.model.WeatherResponseDto
import com.almagro.data.model.WindDto
import com.almagro.domain.error.WeatherException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {

    private val api = mockk<WeatherApi>()
    private val dao = mockk<WeatherDao>()
    private val dispatcher = UnconfinedTestDispatcher()
    private val httpExceptionMapper = HttpExceptionMapper()

    private val repository = WeatherRepositoryImpl(api, dao, dispatcher, httpExceptionMapper)

    @Test
    fun `GIVEN api returns valid response WHEN fetchWeather is called THEN returns success with mapped weather`() = runTest {
        val responseDto = weatherDto
        coEvery { api.fetchWeather(any(), any()) } returns responseDto

        val result = repository.fetchWeather(40.42, -3.70)

        assertTrue(result.isSuccess)
        val weather = result.getOrThrow()
        assertEquals("Madrid", weather.cityName)
        assertEquals(40.42, weather.latitude)
        assertEquals(-3.70, weather.longitude)
        assertEquals(25.5, weather.temperature)
        assertEquals("Clear", weather.condition)
        assertEquals("Clear sky", weather.description)
        assertEquals("https://openweathermap.org/img/wn/01d@2x.png", weather.iconUrl)
        assertEquals(60, weather.humidity)
    }

    @Test
    fun `GIVEN api throws IOException WHEN fetchWeather is called THEN returns failure with Network exception`() = runTest {
        val networkError = IOException("Network unreachable")
        coEvery { api.fetchWeather(any(), any()) } throws networkError

        val result = repository.fetchWeather(40.42, -3.70)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertInstanceOf(WeatherException.Network::class.java, exception)
        assertEquals(networkError, (exception as WeatherException.Network).cause)
    }

    @Test
    fun `GIVEN valid coordinates WHEN fetchWeather is called THEN api is invoked with those coordinates`() = runTest {
        coEvery { api.fetchWeather(any(), any()) } returns weatherDto

        repository.fetchWeather(51.51, -0.13)

        coVerify(exactly = 1) { api.fetchWeather(51.51, -0.13) }
    }

    companion object {
        private val weatherDto = WeatherResponseDto(
            coord = CoordDto(lat = 40.42, lon = -3.70),
            weather = listOf(
                WeatherConditionDto(main = "Clear", description = "clear sky", icon = "01d")
            ),
            main = MainDto(temp = 25.5, humidity = 60),
            wind = WindDto(speed = 0.62, deg = 349),
            rain = null,
            sys = SysDto(country = "ES"),
            timezone = 7200,
            name = "Madrid",
        )
    }
}
