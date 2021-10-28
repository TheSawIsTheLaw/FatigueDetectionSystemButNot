package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.MeasurementDTO
import com.fdsystem.fdserver.domain.service.data.*
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
    ): List<DataServiceMeasurement>
    {
        val gotInformation = charRepository.get(
            bucketName, Pair(0, 0),
            charName
        )

        return gotInformation.map { DataServiceMeasurement(it.value, it.time) }
    }

    fun getMeasurements(
        token: String,
        bucketName: String,
        requiredNames: RequiredMeasurementsNames
    ): List<List<DataServiceMeasurement>>
    {
        loginToInflux(token, NetworkConfig.influxOrganization)

        val outMeasurements: MutableList<List<DataServiceMeasurement>> =
            mutableListOf()

        for (charName in requiredNames.listOfMeasurementsNames)
        {
            outMeasurements.add(getMeasurement(bucketName, charName))
        }

        return outMeasurements
    }

    private fun sendMeasurement(
        bucketName: String,
        charName: String,
        chars: List<String>
    )
    {
        charRepository.add(bucketName, chars.map {
            MeasurementDTO(
                charName,
                it,
                Instant.EPOCH
            )
        })
    }

    fun sendMeasurements(
        token: String,
        bucketName: String,
        chars: MeasurementsToSend
    )
    {
        loginToInflux(token, NetworkConfig.influxOrganization)

        for (char in chars.measurements)
        {
            sendMeasurement(bucketName, char.measurement, char.values)
        }
    }
}