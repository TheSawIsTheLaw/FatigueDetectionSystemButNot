package com.fdsystem.fdserver.mothers

import java.io.File

data class PostgresConfigFileMother(
    val correctConfigurationFile: File = File("./src/test/kotlin/com/fdsystem/fdserver/config/FDPostgresConf.json"),
    val incorrectConfigurationFile: File = File("./src/test/kotlin/com/fdsystem/fdserver/config/IncorrectPostgresConf.json")
)