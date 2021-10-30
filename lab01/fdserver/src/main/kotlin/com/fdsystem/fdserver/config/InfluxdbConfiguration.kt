package com.fdsystem.fdserver.config

import com.google.gson.Gson
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream

@Component
class InfluxdbConfiguration
{
    data class InfluxConfigData(
        val influxdbURL: String,
        val influxdbAdminToken: String,
        val influxdbOrganization: String
    )

    final val configData: InfluxConfigData

    init
    {
        val stream: InputStream
        try
        {
            stream = File("FDInfluxConf.json").inputStream()
        }
        catch (exc: Exception)
        {
            throw Exception("No influx configuration file")
        }

        val jsonToConvert = stream.bufferedReader().use { it.readText() }
        configData = Gson().fromJson(jsonToConvert, InfluxConfigData::class.java)
    }
}