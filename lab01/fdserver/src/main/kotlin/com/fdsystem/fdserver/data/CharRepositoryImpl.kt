package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.InfluxdbConfiguration
import com.fdsystem.fdserver.domain.charrepository.CharRepositoryInterface
import com.fdsystem.fdserver.domain.logicentities.*
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Repository
import java.time.Instant

// Вынести private val в конструктор
class InfluxConnection(
    private val connectionString: String,
    private val token: String,
    private val org: String
) {

    fun getConnectionToDB(): InfluxDBClientKotlin {
        return InfluxDBClientKotlinFactory.create(
            connectionString, token
                .toCharArray(), org
        )
    }

    fun getConnectionWrite(bucketName: String): InfluxDBClientKotlin {
        return InfluxDBClientKotlinFactory
            .create(
                connectionString,
                token.toCharArray(),
                org,
                bucketName
            )
    }
}

// Выносим query в отдельный слой
@Repository
class CharRepositoryImpl(private val config: InfluxdbConfiguration) :
    CharRepositoryInterface {
    val influxDAO = InfluxDAO(config)

    private fun get(
        dataAccessInfo: DSDataAccessInfo,
        connection: InfluxConnection
    ): List<DSMeasurement> {
        val timeRange = dataAccessInfo.timeRange
        val measurement = dataAccessInfo.measurementName
        val bucket = dataAccessInfo.bucketName

        val outList: MutableList<DSMeasurement> = mutableListOf()

        val result = influxDAO.get(connection, timeRange, bucket, measurement)

        runBlocking {
            for (i in result) {
                val curVal = i.values
                outList.add(
                    DSMeasurement(
                        curVal["_measurement"].toString(),
                        curVal["_value"].toString(),
                        curVal["_time"] as Instant
                    )
                )
            }
        }

        return outList.toList()
    }

    override fun get(dataAccessInfo: DSDataAccessInfo): List<DSMeasurement> {
        return get(
            dataAccessInfo, InfluxConnection(
                config.configData.influxdbURL,
                dataAccessInfo.token,
                config.configData.influxdbOrganization
            )
        )
    }

    private fun add(dataAddInfo: DSDataAddInfo, connection: InfluxConnection) {
        val bucket = dataAddInfo.bucket
        val measurementList = dataAddInfo.measurementList

        influxDAO.add(connection, bucket, measurementList)
    }

    override fun add(dataAddInfo: DSDataAddInfo) {
        add(
            dataAddInfo, InfluxConnection(
                config.configData.influxdbURL,
                dataAddInfo.token,
                config.configData.influxdbOrganization
            )
        )
    }

    fun getNewTokenForUser(user: USUserCredentials): TokenInformation {
        val newTokenInformation = influxDAO.getNewTokenForUser(user.username)

        return TokenInformation(newTokenInformation.first, newTokenInformation.second)
    }

    fun deleteToken(tokenToDelete: TokenInformation): Boolean {
        return (influxDAO.deleteToken(tokenToDelete.tokenID) != 204)
    }
}