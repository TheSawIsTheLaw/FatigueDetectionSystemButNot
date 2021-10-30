package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsListDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementDataWithoutTime
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class DataService
{
    private lateinit var charRepository: CharRepositoryImpl

    private fun loginToInflux(token: String, org: String)
    {
        charRepository =
            CharRepositoryImpl(NetworkConfig.influxdbURL, token, org)
    }

    private fun getMeasurement(
        bucketName: String,
        charName: String
    ): List<DSMeasurement>
    {
        val gotInformation = charRepository.get(
            DSDataAccessInfo(
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
    ): List<DSMeasurementList>
    {
        loginToInflux(token, NetworkConfig.influxOrganization)

        val outMeasurements: MutableList<DSMeasurementList> =
            mutableListOf()

        for (charName in requiredNames)
        {
            LogFactory.getLog(javaClass).error("Current charName: $charName")
            outMeasurements.add(
                DSMeasurementList(
                    charName, getMeasurement(bucketName, charName)
                )
            )
        }

        LogFactory.getLog(javaClass).warn("All is ok, return from get method")
        return outMeasurements
    }

    private fun sendMeasurement(
        bucketName: String,
        charName: String,
        chars: List<MeasurementDataWithoutTime>
    )
    {
        charRepository.add(
            DSDataAddInfo(
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
        loginToInflux(token, NetworkConfig.influxOrganization)

        for (measurement in chars.measurements)
        {
            sendMeasurement(
                bucketName, measurement.measurement, measurement
                    .values
            )
        }
    }
}