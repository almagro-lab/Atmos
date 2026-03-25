package com.almagro.presentation

import com.almagro.domain.error.WeatherException
import com.almagro.domain.model.Weather
import com.almagro.domain.useCase.FetchRandomWeatherUseCase
import com.almagro.domain.useCase.GetWeatherHistoryUseCase
import com.almagro.domain.useCase.SaveWeatherUseCase
import com.almagro.presentation.state.WeatherUiState
import com.almagro.presentation.viewModel.WeatherViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val fetchRandomWeather: FetchRandomWeatherUseCase = mockk()
    private val saveWeather: SaveWeatherUseCase = mockk(relaxed = true)
    private val getWeatherHistory: GetWeatherHistoryUseCase = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { getWeatherHistory() } returns flowOf(emptyList())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN use case succeeds WHEN ViewModel is created THEN state is Success with mapped data`() {
        coEvery { fetchRandomWeather() } returns Result.success(weather)

        val sut = createViewModel()

        val state = sut.uiState.value
        assertInstanceOf(WeatherUiState.Success::class.java, state)
        assertEquals("Madrid, ES", (state as WeatherUiState.Success).data.cityName)
    }

    @Test
    fun `GIVEN use case fails with WeatherException WHEN ViewModel is created THEN state is Error with that exception`() {
        coEvery { fetchRandomWeather() } returns Result.failure(WeatherException.ServiceUnavailable)

        val sut = createViewModel()

        val state = sut.uiState.value
        assertInstanceOf(WeatherUiState.Error::class.java, state)
        assertEquals(WeatherException.ServiceUnavailable, (state as WeatherUiState.Error).exception)
    }

    @Test
    fun `GIVEN use case fails with non-WeatherException WHEN ViewModel is created THEN state is Error with Network wrapping it`() {
        val cause = RuntimeException("Unexpected")
        coEvery { fetchRandomWeather() } returns Result.failure(cause)

        val sut = createViewModel()

        val state = sut.uiState.value
        assertInstanceOf(WeatherUiState.Error::class.java, state)
        val exception = (state as WeatherUiState.Error).exception
        assertInstanceOf(WeatherException.Network::class.java, exception)
        assertEquals(cause, (exception as WeatherException.Network).cause)
    }

    @Test
    fun `GIVEN ViewModel in error state WHEN fetchWeather THEN state transitions to Success`() {
        coEvery { fetchRandomWeather() } returns Result.failure(WeatherException.ServiceUnavailable)
        val sut = createViewModel()
        assertInstanceOf(WeatherUiState.Error::class.java, sut.uiState.value)

        coEvery { fetchRandomWeather() } returns Result.success(weather)
        sut.fetchWeather()

        val state = sut.uiState.value
        assertInstanceOf(WeatherUiState.Success::class.java, state)
        assertEquals("Madrid, ES", (state as WeatherUiState.Success).data.cityName)
    }

    @Test
    fun `GIVEN ViewModel in success state WHEN fetchWeather fails THEN state transitions to Error`() {
        coEvery { fetchRandomWeather() } returns Result.success(weather)
        val sut = createViewModel()
        assertInstanceOf(WeatherUiState.Success::class.java, sut.uiState.value)

        coEvery { fetchRandomWeather() } returns Result.failure(WeatherException.ServiceUnavailable)
        sut.fetchWeather()

        val state = sut.uiState.value
        assertInstanceOf(WeatherUiState.Error::class.java, state)
        assertEquals(WeatherException.ServiceUnavailable, (state as WeatherUiState.Error).exception)
    }

    @Test
    fun `GIVEN use case succeeds WHEN ViewModel is created THEN save is called with the weather`() {
        coEvery { fetchRandomWeather() } returns Result.success(weather)

        createViewModel()

        coVerify { saveWeather(weather) }
    }

    @Test
    fun `GIVEN use case fails WHEN ViewModel is created THEN save is not called`() {
        coEvery { fetchRandomWeather() } returns Result.failure(WeatherException.ServiceUnavailable)

        createViewModel()

        coVerify(exactly = 0) { saveWeather(any()) }
    }

    private fun createViewModel() = WeatherViewModel(
        fetchRandomWeather = fetchRandomWeather,
        saveWeather = saveWeather,
    )

    companion object {
        private val weather = Weather(
            cityName = "Madrid",
            country = "ES",
            latitude = 40.0,
            longitude = -3.0,
            temperature = 22.5,
            condition = "Clear",
            description = "clear sky",
            iconUrl = "https://example.com/icon.png",
            humidity = 45,
            windSpeedMs = 0.62,
            windDirectionDeg = 349,
            rainLastHourMm = null,
            timezoneOffsetSeconds = 7200,
        )
    }
}
