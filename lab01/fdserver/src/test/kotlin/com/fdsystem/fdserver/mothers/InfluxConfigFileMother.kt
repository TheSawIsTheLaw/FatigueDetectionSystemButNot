package com.fdsystem.fdserver.mothers

import java.io.File

data class InfluxConfigFileMother(
    val correctConfigurationFile: File = File("./src/test/kotlin/com/fdsystem/fdserver/config/FDInfluxConf.json"),
    val incorrectConfigurationFile: File = File("./src/test/kotlin/com/fdsystem/fdserver/config/IncorrectInfluxConf.json")
)