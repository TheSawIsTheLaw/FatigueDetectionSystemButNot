package com.fdsystem.fdserver.expects

import com.fdsystem.fdserver.domain.dtos.MeasurementDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementData
import java.time.Instant

data class DataControllerExpectations(
    val successGotPulseMeasurement: MeasurementDTO = MeasurementDTO("pulse", listOf(MeasurementData("30", Instant.MIN))),
    val successGotArterialMeasurement: MeasurementDTO = MeasurementDTO("arterialpressure", listOf(MeasurementData("90", Instant.MIN))),

    val dataServerIsDeadMessage: String = "Data server is dead :(",
    val measurementsWereCarefullySentMessage: String = "Measurements were carefully sent"
)
