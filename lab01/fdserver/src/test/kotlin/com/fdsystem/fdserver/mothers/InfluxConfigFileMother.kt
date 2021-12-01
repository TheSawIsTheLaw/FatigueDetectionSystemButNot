package com.fdsystem.fdserver.mothers

import java.io.File

class InfluxConfigFileMother {
    fun getCorrectConfigurationFile(): File {
        return File("./src/test/kotlin/com/fdsystem/fdserver/config/FDInfluxConf.json")
    }

    fun getIncorrectConfigurationFile(): File {
        return File("./src/test/kotlin/com/fdsystem/fdserver/config/IncorrectInfluxConf.json")
    }
}