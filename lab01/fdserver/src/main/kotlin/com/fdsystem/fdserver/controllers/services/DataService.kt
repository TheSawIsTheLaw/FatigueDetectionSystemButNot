package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.DataServiceMeasurement
import com.fdsystem.fdserver.domain.DataServiceMeasurementValue
import com.fdsystem.fdserver.domain.DataServiceMeasurements
import com.fdsystem.fdserver.domain.MeasurementDTO
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
        charNames: List<String>
    ): List<List<DataServiceMeasurement>>
    {
        loginToInflux(token, NetworkConfig.influxOrganization)

        val outMeasurements: MutableList<List<DataServiceMeasurement>> =
            mutableListOf()

        for (charName in charNames)
        {
            outMeasurements.add(getMeasurement(bucketName, charName))
        }

        return outMeasurements
    }

    private fun sendMeasurement(
        bucketName: String,
        charName: String,
        chars: List<DataServiceMeasurementValue>
    )
    {
        charRepository.add(bucketName, chars.map {
            MeasurementDTO(
                charName, it
                    .value,
                Instant.MIN
            )
        })
    }

    fun sendMeasurements(
        token: String,
        bucketName: String,
        chars: List<DataServiceMeasurements>
    )
    {
        loginToInflux(token, NetworkConfig.influxOrganization)

        for (char in chars)
        {
            sendMeasurement(bucketName, char.measurement, char.values)
        }
    }
}