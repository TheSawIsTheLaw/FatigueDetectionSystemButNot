package com.fdsystem.fdserver.mothers

import java.io.File

class PostgresConfigFileMother {
    fun getCorrectConfigurationFile(): File {
        return File("./src/test/kotlin/com/fdsystem/fdserver/config/FDPostgresConf.json")
    }

    fun getIncorrectConfigurationFile(): File {
        return File("./src/test/kotlin/com/fdsystem/fdserver/config/IncorrectPostgresConf.json")
    }
}