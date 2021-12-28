package factories

import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsDTO
import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsListDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementDataWithoutTime

internal object MeasurementsListFactory {
    fun getMeasurementsList(charName: String) = listOf(charName)

    fun getMeasurementsToAdd(charName: String, value: Int) = AcceptMeasurementsListDTO(
        listOf(
            AcceptMeasurementsDTO(
                charName, listOf(
                    MeasurementDataWithoutTime("$value.0")
                )
            )
        )
    )
}