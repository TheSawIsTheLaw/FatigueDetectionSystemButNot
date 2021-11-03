package com.fdsystem.fdserver.config

import com.google.gson.Gson
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream

@Component
class InfluxdbConfiguration(
    configPath: String = "FDInfluxConf.json"
)
{
    data class InfluxConfigData(
        val influxdbURL: String,
        val influxdbAdminToken: String,
        val influxdbOrganization: String
    )

    final val configData: InfluxConfigData

    private fun checkIsConfigValid()
    {
        // Yep, it can be null)))))))))))
        // Noicely done, GSON!
        if (configData.influxdbURL.isNullOrEmpty() ||
            configData.influxdbAdminToken.isNullOrEmpty() ||
            configData.influxdbOrganization.isNullOrEmpty()
        )
        {
            throw Exception("Not enough information in configuration JSON")
        }
    }

    init
    {
        val stream: InputStream
        try
        {
            stream = File(configPath).inputStream()
        }
        catch (exc: Exception)
        {
            throw Exception("No influx configuration file")
        }

        val jsonToConvert = stream.bufferedReader().use { it.readText() }
        configData =
            Gson().fromJson(jsonToConvert, InfluxConfigData::class.java)

        checkIsConfigValid()
    }
}