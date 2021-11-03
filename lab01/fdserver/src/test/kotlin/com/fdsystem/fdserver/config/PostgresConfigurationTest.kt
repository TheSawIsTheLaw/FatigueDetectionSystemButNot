package com.fdsystem.fdserver.config

import org.apache.commons.logging.LogFactory
import org.junit.jupiter.api.Test

internal class PostgresConfigurationTest
{
    @Test
    fun checkAtCorrectConfigurationFile()
    {
        // Arrange
        val path =
            "./src/main/kotlin/com/fdsystem/fdserver/config/FDPostgresConf.json"

        // Action
        val testConfiguration = PostgresConfiguration(path)

        // Assert
        assert(
            testConfiguration.configData.postgresURL.isNotEmpty() &&
                    testConfiguration.configData.postgresUsername.isNotEmpty() &&
                    testConfiguration.configData.postgresPassword.isNotEmpty()
        )
    }

    @Test
    fun checkAtNotExistingConfigurationFile()
    {
        // Arrange
        var outExceptionMessage = ""

        // Action
        try
        {
            PostgresConfiguration()
        }
        catch (exc: Exception)
        {
            outExceptionMessage = exc.message.toString()
        }

        assert(outExceptionMessage == "No postgres configuration file")
    }

    @Test
    fun checkAtIncorrectConfigurationFile()
    {
        // Arrange
        val path =
            "./src/test/kotlin/com/fdsystem/fdserver/config" +
                    "/IncorrectPostgresConf.json"
        var excMessage = ""

        // Action
        try
        {
            PostgresConfiguration(path)
        }
        catch (exc: Exception)
        {
            excMessage = exc.message.toString()
        }

        LogFactory.getLog(javaClass).debug(excMessage)
        assert(excMessage == "Not enough information in configuration JSON")
    }
}