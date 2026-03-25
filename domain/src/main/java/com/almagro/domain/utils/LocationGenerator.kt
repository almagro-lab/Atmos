package com.almagro.domain.utils

import javax.inject.Inject
import kotlin.random.Random

class LocationGenerator @Inject constructor() {

    fun generateRandomLatLon(): Pair<Double, Double> =
        Pair(
            Random.nextDouble(-90.0, 90.0),
            Random.nextDouble(-180.0, 180.0)
        )
}