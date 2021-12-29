package com.fdsystem.fdserver.config

import com.google.gson.Gson
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream

data class PostgresConfigData(
    val postgresURL: String = "",
    val postgresUsername: String = "",
    val postgresPassword: String = ""
)

@Component
class PostgresConfiguration(
    configFile: File = File("FDPostgresConf.json")
) {
    val configData: PostgresConfigData

    init {
        val stream: InputStream = configFile.inputStream()

        val jsonToConvert = stream.bufferedReader().use { it.readText() }
        configData = Gson().fromJson(jsonToConvert, PostgresConfigData::class.java)
    }
}