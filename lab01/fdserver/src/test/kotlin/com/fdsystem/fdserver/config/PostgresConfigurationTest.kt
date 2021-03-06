package com.fdsystem.fdserver.config

import com.fdsystem.fdserver.expects.PostgresConfigurationExcpectations
import com.fdsystem.fdserver.mothers.PostgresConfigFileMother
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PostgresConfigurationTest {
    private val configMother = PostgresConfigFileMother()
    private val expectations = PostgresConfigurationExcpectations()

    @Test
    fun checkAtCorrectConfigurationFile() {
        // Arrange
        val configurationFile = configMother.correctConfigurationFile

        // Action
        val testConfiguration = PostgresConfiguration(configurationFile)

        // Assert
        assertEquals(expectations.postgresURL, testConfiguration.configData.postgresURL)
        assertEquals(expectations.postgresUsername, testConfiguration.configData.postgresUsername)
        assertEquals(expectations.postgresPassword, testConfiguration.configData.postgresPassword)
    }

    @Test
    fun checkAtIncorrectConfigurationFile() {
        // Arrange
        val configurationFile = configMother.incorrectConfigurationFile

        // Action
        val testConfiguration = PostgresConfiguration(configurationFile)

        // Assert
        assertEquals(expectations.postgresURL, testConfiguration.configData.postgresURL)
        assertTrue(testConfiguration.configData.postgresUsername.isEmpty())
        assertEquals(expectations.postgresPassword, testConfiguration.configData.postgresPassword)
    }
}