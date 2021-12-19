package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.InfluxdbConfiguration
import org.junit.jupiter.api.Test
import java.io.File

internal class CharRepositoryImplIntegrationTest {
    val confPath =
        "./src/test/kotlin/com/fdsystem/fdserver/config/FDInfluxConf.json"
    val configuration = InfluxdbConfiguration(File(confPath))

    val repositoryToTest = CharRepositoryImpl(configuration)

    @Test
    fun getTestSuccess() {
        // Arrange
        
        // Act

        // Assert
    }
}