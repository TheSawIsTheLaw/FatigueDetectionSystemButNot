package com.fdsystem.fdserver.config

import org.apache.commons.logging.LogFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

internal class PostgresConfigurationTest {
    @Test
    fun checkAtCorrectConfigurationFile() {
        // Arrange
        val path =
                "./src/main/kotlin/com/fdsystem/fdserver/config/FDPostgresConf.json"
        val configurationFile = File(path)

        // Action
        val testConfiguration = PostgresConfiguration(configurationFile)

        // Assert
        assertTrue(testConfiguration.configData.postgresURL.isNotEmpty())
        assertTrue(testConfiguration.configData.postgresUsername.isNotEmpty())
        assertTrue(testConfiguration.configData.postgresPassword.isNotEmpty())
    }

    @Test
    fun checkAtIncorrectConfigurationFile() {
        // Arrange
        val path =
                "./src/test/kotlin/com/fdsystem/fdserver/config/IncorrectPostgresConf.json"
        val configurationFile = File(path)

        // Action
        val testConfiguration = PostgresConfiguration(configurationFile)

        // Assert
        assertTrue(testConfiguration.configData.postgresUsername.isEmpty())
        assertEquals("satanIsHere", testConfiguration.configData.postgresPassword)
        assertEquals("postgres:5432/users", testConfiguration.configData.postgresURL)
    }
}