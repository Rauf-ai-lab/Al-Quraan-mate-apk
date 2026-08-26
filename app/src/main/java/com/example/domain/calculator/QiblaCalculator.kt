package com.example.domain.calculator

import kotlin.math.*

data class QiblaResult(
    val bearing: Float,        // Azimuth from True North in degrees (0..360)
    val distanceKm: Double,    // Distance to Kaaba in kilometers
    val directionText: String
)

object QiblaCalculator {
    // Holy Kaaba Coordinates (Makkah al-Mukarramah)
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the Great-Circle initial bearing from the observer's location to the Holy Kaaba.
     */
    fun calculate(userLat: Double, userLng: Double): QiblaResult {
        val phi1 = Math.toRadians(userLat)
        val phi2 = Math.toRadians(KAABA_LATITUDE)
        val deltaLambda = Math.toRadians(KAABA_LONGITUDE - userLng)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360.0) % 360.0

        // Distance using Haversine formula
        val deltaPhi = Math.toRadians(KAABA_LATITUDE - userLat)
        val a = sin(deltaPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = EARTH_RADIUS_KM * c

        val direction = getCardinalDirection(bearing)

        return QiblaResult(
            bearing = bearing.toFloat(),
            distanceKm = round(distance * 10) / 10.0,
            directionText = direction
        )
    }

    private fun getCardinalDirection(angle: Double): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = ((angle + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }
}
