package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.InfluxdbConfiguration
import com.fdsystem.fdserver.domain.charrepository.CharRepositoryInterface
import com.fdsystem.fdserver.domain.logicentities.*
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import org.springframework.stereotype.Repository

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
        return influxDAO.get(
            connection,
            dataAccessInfo.timeRange,
            dataAccessInfo.bucketName,
            dataAccessInfo.measurementName
        )
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
        influxDAO.add(connection, dataAddInfo.bucket, dataAddInfo.measurementList)
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