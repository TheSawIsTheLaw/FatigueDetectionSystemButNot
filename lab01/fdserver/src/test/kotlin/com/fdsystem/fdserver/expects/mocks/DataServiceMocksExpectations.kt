package com.fdsystem.fdserver.expects.mocks

import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import java.time.Instant

data class DataServiceMocksExpectations(
    val charGetArterialExample: List<DSMeasurement> = listOf(
        DSMeasurement("arterialpressure", "60", Instant.MIN),
        DSMeasurement("arterialpressure", "63", Instant.MIN)
    ),
    val charGetPulseExample: List<DSMeasurement> = listOf(
        DSMeasurement("pulse", "60", Instant.MIN),
        DSMeasurement("pulse", "63", Instant.MIN)
    )
)
