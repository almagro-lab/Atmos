package com.almagro.domain.error

sealed class WeatherException(message: String) : Exception(message) {
    data object LocationNotFound : WeatherException("Location not found")
    data object Unauthorized : WeatherException("Invalid API key")
    data object RateLimitExceeded : WeatherException("Rate limit exceeded, try again later")
    data object ServiceUnavailable : WeatherException("Weather service is unavailable")
    data class Network(override val cause: Throwable) : WeatherException(cause.message ?: "Network error")
}
