package com.fdsystem.fdserver.config

import com.fdsystem.fdserver.expects.InfluxdbConfigurationExpectations
import com.fdsystem.fdserver.mothers.InfluxConfigFileMother
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class InfluxdbConfigurationTest {
    private val motherConfig = InfluxConfigFileMother()
    private val expectations = InfluxdbConfigurationExpectations()

    @Test
    fun checkAtCorrectConfigurationFile() {
        // Arrange
        val configurationFile = motherConfig.getCorrectConfigurationFile()

        // Action
        val testConfiguration = InfluxdbConfiguration(configurationFile)

        // Assert
        assertEquals(expectations.influxdbURL, testConfiguration.configData.influxdbURL)
        assertEquals(expectations.influxdbOrganization, testConfiguration.configData.influxdbOrganization)
        assertEquals(expectations.influxdbAdminToken, testConfiguration.configData.influxdbAdminToken)
    }

    @Test
    fun checkAtIncorrectConfigurationFile() {
        // Arrange
        val configurationFile = motherConfig.getIncorrectConfigurationFile()

        // Action
        val testConfiguration = InfluxdbConfiguration(configurationFile)

        // Assert
        assertTrue(testConfiguration.configData.influxdbURL.isEmpty())
        assertEquals(expectations.influxdbOrganization, testConfiguration.configData.influxdbOrganization)
        assertEquals(expectations.influxdbAdminToken, testConfiguration.configData.influxdbAdminToken)
    }
}