package com.almagro.data.repository

import com.almagro.data.api.WeatherApi
import com.almagro.core.di.IoDispatcher
import com.almagro.data.dao.WeatherDao
import com.almagro.data.mapper.HttpExceptionMapper
import com.almagro.data.mapper.toDomain
import com.almagro.data.mapper.toEntity
import com.almagro.domain.model.Weather
import com.almagro.domain.repository.WeatherLocalRepository
import com.almagro.domain.repository.WeatherRemoteRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val httpExceptionMapper: HttpExceptionMapper,
) : WeatherRemoteRepository, WeatherLocalRepository {

    override suspend fun fetchWeather(lat: Double, lon: Double): Result<Weather> =
        try {
            val weather = withContext(ioDispatcher) {
                api.fetchWeather(lat, lon).toDomain()
            }
            Result.success(weather)
        } catch (e: Exception) {
            Result.failure(httpExceptionMapper.toWeatherException(e))
        }

    override fun getHistory(): Flow<List<Weather>> =
        dao.getHistory().map { list -> list.map { it.toDomain() } }

    override suspend fun save(weather: Weather) = withContext(ioDispatcher) {
        dao.insert(weather.toEntity())
    }
}
