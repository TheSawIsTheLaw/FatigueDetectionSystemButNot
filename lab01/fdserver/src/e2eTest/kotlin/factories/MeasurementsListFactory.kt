package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsDTO
import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsListDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementDataWithoutTime

class MeasurementsListFactory {
    fun getMeasurementsListWithBotArterialPressure() = listOf("botArterialPressure")

    fun getMeasurementsToAdd() = AcceptMeasurementsListDTO(
        listOf(
            AcceptMeasurementsDTO(
                "botArterialPressure", listOf(
                    MeasurementDataWithoutTime("0.0")
                )
            )
        )
    )

    fun getMeasurementsToAdd(value: Int) = AcceptMeasurementsListDTO(
        listOf(
            AcceptMeasurementsDTO(
                "botArterialPressure", listOf(
                    MeasurementDataWithoutTime("$value.0")
                )
            )
        )
    )
}