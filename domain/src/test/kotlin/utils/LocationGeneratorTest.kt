package utils

import com.almagro.domain.utils.LocationGenerator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LocationGeneratorTest {

    private val sut = LocationGenerator()

    @Test
    fun `GIVEN LocationGenerator WHEN call generateRandomLatLon THEN return coordinates within valid Earth bounds`() {
        val (lat, lon) = sut.generateRandomLatLon()

        assertTrue(lat in -90.0..90.0, "Latitude $lat out of bounds")
        assertTrue(lon in -180.0..180.0, "Longitude $lon out of bounds")
    }
}