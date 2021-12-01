package com.fdsystem.fdserver.mothers

import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsDTO
import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsListDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementDataWithoutTime

data class DataControllerOMother(
    val successToken: String = "normTok",
    val invalidToken: String = "lol",
    val exceptionToken: String = "serverExcCheck",

    val addDataSuccessList: AcceptMeasurementsListDTO = AcceptMeasurementsListDTO(
        listOf(
            AcceptMeasurementsDTO(
                "pulse", listOf(
                    MeasurementDataWithoutTime("30")
                )
            ),
            AcceptMeasurementsDTO(
                "arterialpressure", listOf(
                    MeasurementDataWithoutTime("90")
                )
            )
        )
    ),

    val pulseAndArterialMeasurementsList: List<String> = listOf("pulse", "arterialpressure")
)
