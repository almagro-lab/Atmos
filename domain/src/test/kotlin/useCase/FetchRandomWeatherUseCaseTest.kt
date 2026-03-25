package useCase

import com.almagro.domain.model.Weather
import com.almagro.domain.repository.WeatherRemoteRepository
import com.almagro.domain.useCase.FetchRandomWeatherUseCase
import com.almagro.domain.utils.LocationGenerator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchRandomWeatherUseCaseTest {

    private val weatherRemoteRepository: WeatherRemoteRepository = mockk()
    private val locationGenerator: LocationGenerator = mockk()

    private lateinit var sut: FetchRandomWeatherUseCase

    @BeforeEach
    fun setUp() {
        sut = FetchRandomWeatherUseCase(weatherRemoteRepository, locationGenerator)
    }

    @Test
    fun `GIVEN repository returns weather WHEN invoke THEN returns successful Result with that weather`() = runTest {
        val expectedWeather = aWeather()

        every { locationGenerator.generateRandomLatLon() } returns Pair(40.0, -3.0)
        coEvery { weatherRemoteRepository.fetchWeather(40.0, -3.0) } returns Result.success(expectedWeather)

        val result = sut()

        assertTrue(result.isSuccess)
        assertSame(expectedWeather, result.getOrNull())
    }

    @Test
    fun `GIVEN repository fails WHEN invoke THEN returns failure Result with the original exception`() = runTest {
        val expectedError = RuntimeException("Network Error")

        every { locationGenerator.generateRandomLatLon() } returns Pair(0.0, 0.0)
        coEvery { weatherRemoteRepository.fetchWeather(any(), any()) } returns Result.failure(expectedError)

        val result = sut()

        assertTrue(result.isFailure)
        assertSame(expectedError, result.exceptionOrNull())
    }

    @Test
    fun `GIVEN generated coordinates WHEN invoke THEN fetches weather for those coordinates`() = runTest {
        val generatedLat = -33.87
        val generatedLon = 151.21

        every { locationGenerator.generateRandomLatLon() } returns Pair(generatedLat, generatedLon)
        coEvery { weatherRemoteRepository.fetchWeather(generatedLat, generatedLon) } returns Result.success(aWeather())
        coEvery { weatherRemoteRepository.fetchWeather(neq(generatedLat), any()) } returns Result.failure(
            IllegalArgumentException("unexpected coordinates")
        )

        val result = sut()

        assertTrue(result.isSuccess)
    }

    private fun aWeather() = Weather(
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
