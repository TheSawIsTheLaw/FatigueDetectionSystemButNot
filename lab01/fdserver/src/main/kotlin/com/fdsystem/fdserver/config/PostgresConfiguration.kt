package com.fdsystem.fdserver.config

import com.google.gson.Gson
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream

@Component
class PostgresConfiguration(
    postgresConfigPath: String = "FDPostgresConf.json"
)
{
    data class PostgresConfigData(
        val postgresURL: String,
        val postgresUsername: String,
        val postgresPassword: String
    )

    final val configData: PostgresConfigData

    private fun checkIsConfigValid()
    {
        // Yep, it can be null
        // Thank you, GSON
        if (configData.postgresPassword.isNullOrEmpty() ||
                configData.postgresURL.isNullOrEmpty() ||
                configData.postgresUsername.isNullOrEmpty())
        {
            throw Exception("Not enough information in configuration JSON")
        }
    }

    init
    {
        val stream: InputStream
        try
        {
            stream = File(postgresConfigPath).inputStream()
        }
        catch (exc: Exception)
        {
            throw Exception("No postgres configuration file")
        }

        val jsonToConvert = stream.bufferedReader().use { it.readText() }
        configData =
            Gson().fromJson(jsonToConvert, PostgresConfigData::class.java)

        checkIsConfigValid()
    }
}