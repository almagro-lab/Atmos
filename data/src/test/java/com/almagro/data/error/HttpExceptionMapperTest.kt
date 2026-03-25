package com.almagro.data.error

import com.almagro.domain.error.WeatherException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import com.almagro.data.mapper.HttpExceptionMapper

class HttpExceptionMapperTest {

    private val mapper = HttpExceptionMapper()

    @Test
    fun `GIVEN HttpException with 401 WHEN toWeatherException is called THEN returns Unauthorized`() {
        val exception = httpException(401)

        val result = mapper.toWeatherException(exception)

        assertInstanceOf(WeatherException.Unauthorized::class.java, result)
    }

    @Test
    fun `GIVEN HttpException with 404 WHEN toWeatherException is called THEN returns LocationNotFound`() {
        val exception = httpException(404)

        val result = mapper.toWeatherException(exception)

        assertInstanceOf(WeatherException.LocationNotFound::class.java, result)
    }

    @Test
    fun `GIVEN HttpException with 429 WHEN toWeatherException is called THEN returns RateLimitExceeded`() {
        val exception = httpException(429)

        val result = mapper.toWeatherException(exception)

        assertInstanceOf(WeatherException.RateLimitExceeded::class.java, result)
    }

    @Test
    fun `GIVEN HttpException with 500 WHEN toWeatherException is called THEN returns ServiceUnavailable`() {
        val exception = httpException(500)

        val result = mapper.toWeatherException(exception)

        assertInstanceOf(WeatherException.ServiceUnavailable::class.java, result)
    }

    @Test
    fun `GIVEN HttpException with unknown code WHEN toWeatherException is called THEN returns Network`() {
        val exception = httpException(418)

        val result = mapper.toWeatherException(exception)

        assertInstanceOf(WeatherException.Network::class.java, result)
        assertEquals(exception, (result as WeatherException.Network).cause)
    }

    @Test
    fun `GIVEN non-HttpException throwable WHEN toWeatherException is called THEN returns Network`() {
        val ioException = IOException("Connection refused")

        val result = mapper.toWeatherException(ioException)

        assertInstanceOf(WeatherException.Network::class.java, result)
        assertEquals(ioException, (result as WeatherException.Network).cause)
    }

    private fun httpException(code: Int): HttpException =
        HttpException(Response.error<Any>(code, "".toResponseBody(null)))
}
