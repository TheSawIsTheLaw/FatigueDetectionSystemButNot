package com.fdsystem.fdserver.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

internal class InfluxdbConfigurationTest {
    @Test
    fun checkAtCorrectConfigurationFile() {
        // Arrange
        val path =
                "./src/main/kotlin/com/fdsystem/fdserver/config/FDInfluxConf.json"
        val configurationFile = File(path)

        // Action
        val testConfiguration = InfluxdbConfiguration(configurationFile)

        // Assert
        assert(testConfiguration.configData.influxdbAdminToken.isNotEmpty())
        assert(testConfiguration.configData.influxdbURL.isNotEmpty())
        assert(testConfiguration.configData.influxdbOrganization.isNotEmpty())
    }

    @Test
    fun checkAtIncorrectConfigurationFile() {
        // Arrange
        val path =
                "./src/test/kotlin/com/fdsystem/fdserver/config/IncorrectInfluxConf.json"
        val fileWithConfiguration = File(path)

        // Action
        val testConfiguration = InfluxdbConfiguration(fileWithConfiguration)

        // Assert
        assert(testConfiguration.configData.influxdbURL.isEmpty())
        assertEquals("subjects", testConfiguration.configData.influxdbOrganization)
        assertEquals("HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow==",
                testConfiguration.configData.influxdbAdminToken)
    }
}