package com.fdsystem.fdserver.domain.service.data

import java.time.Instant

data class DataServiceMeasurement(
    val value: String,
    val time: Instant
)

data class Measurement(
    val measurement: String,
    val values: List<String>
)

data class MeasurementWithTime(
    val measurement: String,
    val values: List<DataServiceMeasurement>
)

data class MeasurementsToSend(
    val measurements: List<Measurement>
)