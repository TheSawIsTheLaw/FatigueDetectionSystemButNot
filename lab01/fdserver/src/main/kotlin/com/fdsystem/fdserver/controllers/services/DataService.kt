package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsListDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementData
import com.fdsystem.fdserver.domain.dtos.MeasurementDataWithoutTime
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class DataService(private val charRepository: CharRepositoryImpl)
{
    private fun getMeasurement(
        token: String,
        bucketName: String,
        charName: String
    ): List<DSMeasurement>
    {
        val gotInformation = charRepository.get(
            DSDataAccessInfo(
                token,
                bucketName,
                Pair(0, 0),
                charName
            )
        )

        return gotInformation.map { DSMeasurement(charName, it.value, it.time) }
    }

    fun getMeasurements(
        token: String,
        bucketName: String,
        requiredNames: List<String>
    ): List<MeasurementDTO>
    {
        val outMeasurements: MutableList<MeasurementDTO> =
            mutableListOf()

        for (charName in requiredNames)
        {
            LogFactory.getLog(javaClass).error("Current charName: $charName")
            outMeasurements.add(
                MeasurementDTO(
                    charName, getMeasurement(token, bucketName, charName).map {
                        MeasurementData(
                            it.value, it.time
                        )
                    }
                )
            )
        }

        LogFactory.getLog(javaClass).warn("All is ok, return from get method")
        return outMeasurements
    }

    private fun sendMeasurement(
        token: String,
        bucketName: String,
        charName: String,
        chars: List<MeasurementDataWithoutTime>
    )
    {
        charRepository.add(
            DSDataAddInfo(
                token,
                bucketName, DSMeasurementList
                    (
                    charName,
                    chars.map {
                        DSMeasurement(
                            charName,
                            it.value,
                            Instant.EPOCH
                        )
                    })
            )
        )
    }

    fun sendMeasurements(
        token: String,
        bucketName: String,
        chars: AcceptMeasurementsListDTO
    )
    {
        for (measurement in chars.measurements)
        {
            sendMeasurement(
                token, bucketName,
                measurement.measurement, measurement.values
            )
        }
    }
}