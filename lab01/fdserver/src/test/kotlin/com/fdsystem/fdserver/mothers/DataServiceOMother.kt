package com.fdsystem.fdserver.mothers

import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementDataWithoutTime
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import java.time.Instant

data class DataServiceOMother(
    val defaultToken: String = "123",

    val defaultBucket: String = "someone",

    val defaultRequiredNames: List<String> = listOf("pulse", "arterialpressure"),

    val pulseAccessInfo: DSDataAccessInfo = DSDataAccessInfo(
        defaultToken,
        defaultBucket,
        Pair(0, 0),
        "pulse"
    ),
    val arterialAccessInfo: DSDataAccessInfo = DSDataAccessInfo(
        defaultToken,
        defaultBucket,
        Pair(0, 0),
        "arterialpressure"
    ),

    val acceptPulseMeasurements: AcceptMeasurementsDTO = AcceptMeasurementsDTO(
        "pulse", listOf(
            MeasurementDataWithoutTime("34"),
            MeasurementDataWithoutTime("36")
        )
    ),
    val acceptArterialMeasurements: AcceptMeasurementsDTO = AcceptMeasurementsDTO(
        "arterialpressure", listOf(
            MeasurementDataWithoutTime("100"),
            MeasurementDataWithoutTime("200")
        )
    ),

    val arterialDataAddExample: DSDataAddInfo = DSDataAddInfo(
        defaultToken, defaultBucket, DSMeasurementList(
            acceptArterialMeasurements.measurement,
            acceptArterialMeasurements.values.map {
                DSMeasurement(
                    acceptArterialMeasurements.measurement,
                    it.value,
                    Instant.MIN
                )
            }
        )
    ),
    val pulseDataAddExample: DSDataAddInfo = DSDataAddInfo(
        defaultToken, defaultBucket, DSMeasurementList(
            acceptPulseMeasurements.measurement,
            acceptPulseMeasurements.values.map {
                DSMeasurement(
                    acceptPulseMeasurements.measurement,
                    it.value,
                    Instant.MIN
                )
            }
        )
    )
)
