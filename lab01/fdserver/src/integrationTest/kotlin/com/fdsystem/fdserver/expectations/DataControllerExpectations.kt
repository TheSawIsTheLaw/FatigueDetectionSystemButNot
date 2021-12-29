package com.fdsystem.fdserver.expectations

import com.fdsystem.fdserver.domain.dtos.MeasurementDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementData
import com.fdsystem.fdserver.domain.dtos.ResponseMeasurementsDTO
import java.time.Instant

class DataControllerExpectations {
    val responseMeasurementsDTOWithArterial = ResponseMeasurementsDTO(
        listOf(
            MeasurementDTO(
                "botArterialPressure", listOf(
                    MeasurementData("60.0", Instant.MAX),
                    MeasurementData("90.0", Instant.MAX),
                    MeasurementData("140.0", Instant.MAX),
                    MeasurementData("40.0", Instant.MAX),
                    MeasurementData("0.0", Instant.MAX)
                )
            )
        )
    )

    val responseMeasurementsDTOWithAddedArterial = ResponseMeasurementsDTO(
        listOf(
            MeasurementDTO(
                "botArterialPressure", listOf(
                    MeasurementData("60.0", Instant.MAX),
                    MeasurementData("90.0", Instant.MAX),
                    MeasurementData("140.0", Instant.MAX),
                    MeasurementData("40.0", Instant.MAX),
                    MeasurementData("0.0", Instant.MAX),
                    MeasurementData("0.0", Instant.MAX)
                )
            )
        )
    )
}