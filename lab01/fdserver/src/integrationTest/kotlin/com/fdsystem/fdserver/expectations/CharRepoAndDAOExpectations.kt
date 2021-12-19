package com.fdsystem.fdserver.expectations

import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import java.time.Instant

class CharRepoAndDAOExpectations {
    val pulseListAtCreation = listOf(
        DSMeasurement("pulse", "60.0", Instant.MAX),
        DSMeasurement("pulse", "70.0", Instant.MAX),
        DSMeasurement("pulse", "75.0", Instant.MAX),
        DSMeasurement("pulse", "70.0", Instant.MAX),
        DSMeasurement("pulse", "30.0", Instant.MAX),
        DSMeasurement("pulse", "0.0", Instant.MAX),
        )
}