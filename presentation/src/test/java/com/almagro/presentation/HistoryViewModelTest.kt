package com.almagro.presentation

import app.cash.turbine.test
import com.almagro.domain.model.Weather
import com.almagro.domain.useCase.GetWeatherHistoryUseCase
import com.almagro.presentation.viewModel.HistoryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val getWeatherHistory: GetWeatherHistoryUseCase = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN history is empty WHEN ViewModel is created THEN history emits empty list`() = runTest {
        every { getWeatherHistory() } returns flowOf(emptyList())

        val sut = HistoryViewModel(getWeatherHistory)

        sut.history.test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN history has one item WHEN ViewModel is created THEN history emits one mapped WeatherUi`() = runTest {
        every { getWeatherHistory() } returns flowOf(listOf(weather))

        val sut = HistoryViewModel(getWeatherHistory)

        sut.history.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Madrid, ES", result.first().cityName)
            assertEquals("22.5°C", result.first().temperature)
            assertEquals("40.00, -3.00", result.first().coordinates)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN history has multiple items WHEN ViewModel is created THEN all items are mapped in order`() = runTest {
        every { getWeatherHistory() } returns flowOf(listOf(weather, weather.copy(cityName = "London", country = "GB")))

        val sut = HistoryViewModel(getWeatherHistory)

        sut.history.test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Madrid, ES", result[0].cityName)
            assertEquals("London, GB", result[1].cityName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN history item with no city and no country WHEN ViewModel is created THEN cityName shows Unknown`() = runTest {
        every { getWeatherHistory() } returns flowOf(listOf(weather.copy(cityName = "", country = null)))

        val sut = HistoryViewModel(getWeatherHistory)

        sut.history.test {
            assertEquals("Unknown", awaitItem().first().cityName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        private val weather = Weather(
            cityName = "Madrid",
            country = "ES",
            latitude = 40.0,
            longitude = -3.0,
            temperature = 22.5,
            condition = "Clear",
            description = "Clear sky",
            iconUrl = "https://example.com/icon.png",
            humidity = 45,
            windSpeedMs = 0.62,
            windDirectionDeg = 349,
            rainLastHourMm = null,
            timezoneOffsetSeconds = 7200,
        )
    }
}
