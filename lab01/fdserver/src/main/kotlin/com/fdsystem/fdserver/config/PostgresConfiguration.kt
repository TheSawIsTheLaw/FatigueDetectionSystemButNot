package com.fdsystem.fdserver.config

import com.google.gson.Gson
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream

@Component
class PostgresConfiguration
{
    data class PostgresConfigData(
        val postgresURL: String,
        val postgresUsername: String,
        val postgresPassword: String
    )

    final val configData: PostgresConfigData

    init
    {
        val stream: InputStream
        try
        {
            stream = File("FDPostgresConf.json").inputStream()
        }
        catch (exc: Exception)
        {
            throw Exception("No influx configuration file")
        }

        val jsonToConvert = stream.bufferedReader().use { it.readText() }
        configData = Gson().fromJson(jsonToConvert, PostgresConfigData::class.java)
    }
}