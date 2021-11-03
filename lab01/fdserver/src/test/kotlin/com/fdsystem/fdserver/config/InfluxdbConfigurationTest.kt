package com.fdsystem.fdserver.config

import org.apache.commons.logging.LogFactory
import org.junit.jupiter.api.Test

// Example of english style tests, I guess.
internal class InfluxdbConfigurationTest
{
    @Test
    fun checkAtCorrectConfigurationFile()
    {
        // Arrange
        val path =
            "./src/main/kotlin/com/fdsystem/fdserver/config/FDInfluxConf.json"

        // Action
        val testConfiguration = InfluxdbConfiguration(path)

        // Assert
        assert(
            testConfiguration.configData.influxdbAdminToken.isNotEmpty() &&
                    testConfiguration.configData.influxdbURL.isNotEmpty() &&
                    testConfiguration.configData.influxdbOrganization.isNotEmpty()
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
            InfluxdbConfiguration()
        }
        catch (exc: Exception)
        {
            outExceptionMessage = exc.message.toString()
        }

        assert(outExceptionMessage == "No influx configuration file")
    }

    @Test
    fun checkAtIncorrectConfigurationFile()
    {
        // Arrange
        val path =
            "./src/test/kotlin/com/fdsystem/fdserver/config/IncorrectInfluxConf.json"
        var excMessage = ""

        // Action
        try
        {
            InfluxdbConfiguration(path)
        }
        catch (exc: Exception)
        {
            excMessage = exc.message.toString()
        }

        LogFactory.getLog(javaClass).debug(excMessage)
        assert(excMessage == "Not enough information in configuration JSON")
    }
}