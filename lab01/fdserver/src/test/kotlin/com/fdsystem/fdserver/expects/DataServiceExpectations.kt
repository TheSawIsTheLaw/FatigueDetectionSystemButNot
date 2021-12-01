package com.fdsystem.fdserver.expects

import com.fdsystem.fdserver.domain.dtos.MeasurementDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementData
import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import java.time.Instant

data class DataServiceExpectations(
    private val pulseListExample: List<MeasurementData> = listOf(
        MeasurementData("60", Instant.MIN),
        MeasurementData("63", Instant.MIN)
    ),
    private val pulseListExampleWithName: List<DSMeasurement> = listOf(
        DSMeasurement("pulse", "60", Instant.MIN),
        DSMeasurement("pulse", "63", Instant.MIN)
    ),
    private val arterialListExample: List<MeasurementData> = listOf(
        MeasurementData("60", Instant.MIN),
        MeasurementData("63", Instant.MIN)
    ),
    private val arterialListExampleWithName: List<DSMeasurement> = listOf(
        DSMeasurement("arterialpressure", "60", Instant.MIN),
        DSMeasurement("arterialpressure", "63", Instant.MIN)
    ),

    val pulseMeasurementDTO: MeasurementDTO = MeasurementDTO("pulse", pulseListExample),
    val arterialMeasurementDTO: MeasurementDTO = MeasurementDTO("arterialpressure", arterialListExample),
)
