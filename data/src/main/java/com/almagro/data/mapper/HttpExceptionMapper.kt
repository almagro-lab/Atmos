package com.almagro.data.mapper

import javax.inject.Inject
import com.almagro.domain.error.WeatherException
import retrofit2.HttpException

class HttpExceptionMapper @Inject constructor() {

    fun toWeatherException(throwable: Throwable): WeatherException = when (throwable) {
        is HttpException -> mapHttpException(throwable)
        else -> WeatherException.Network(throwable)
    }

    private fun mapHttpException(exception: HttpException): WeatherException = when (exception.code()) {
        401 -> WeatherException.Unauthorized
        404 -> WeatherException.LocationNotFound
        429 -> WeatherException.RateLimitExceeded
        in 500..599 -> WeatherException.ServiceUnavailable
        else -> WeatherException.Network(exception)
    }
}