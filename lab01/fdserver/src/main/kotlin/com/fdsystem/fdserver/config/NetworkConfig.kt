package com.fdsystem.fdserver.config

import com.google.gson.Gson
import java.io.File
import java.io.InputStream

object NetworkConfig
{
    data class NetworkConfigData
        (
        // InfluxDB necessary info
        val influxdbURL: String,
        val influxAdminToken: String,
        val influxOrganization: String,

        // Postgres necessary info
        val postgresURL: String,
        val postgresUsername: String,
        val postgresPassword: String
    )

    val data: NetworkConfigData

    init
    {
        val stream: InputStream
        try
        {
            stream = File("FDConf.json").inputStream()
        }
        catch (exc: Exception)
        {
            throw Exception("No configuration file")
        }
        val jsonToConvert = stream.bufferedReader().use { it.readText() }
        data = Gson().fromJson(jsonToConvert, NetworkConfigData::class.java)
    }

    // InfluxDB necessary info
    val influxdbURL: String = data.influxdbURL
    val influxAdminToken: String = data.influxAdminToken
    val influxOrganization: String = data.influxOrganization

    // Postgres necessary info
    val postgresURL: String = data.postgresURL
    val postgresUsername: String = data.postgresUsername
    val postgresPassword: String = data.postgresPassword
}
