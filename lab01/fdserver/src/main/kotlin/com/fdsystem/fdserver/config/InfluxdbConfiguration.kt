package com.fdsystem.fdserver.config

import com.google.gson.Gson
import org.springframework.stereotype.Component
import java.io.File

data class InfluxConfigData(
        val influxdbURL: String = "",
        val influxdbAdminToken: String = "",
        val influxdbOrganization: String = ""
)

@Component
class InfluxdbConfiguration(
        configFile: File = File("FDInfluxConf.json")
) {
    val configData: InfluxConfigData

    init {
        val stream = configFile.inputStream()

        // https://github.com/google/gson/issues/1657
        // https://github.com/google/gson/issues/1550
        val jsonToConvert = stream.bufferedReader().use { it.readText() }
        configData = Gson().fromJson(jsonToConvert, InfluxConfigData::class.java)
    }
}