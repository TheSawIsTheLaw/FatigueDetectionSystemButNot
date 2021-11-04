package com.fdsystem.fdserver.data

import com.influxdb.exceptions.InfluxException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class InfluxConnectionTest
{
    @Test
    fun getConnectionToDBTestSuccess()
    {
        // Arrange
        val connectionToTest = InfluxConnection(
            "http://localhost:8086",
            "tok", "org"
        )

        // Action
        val influxConnection = try
        {
            connectionToTest.getConnectionToDB()
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(influxConnection != null)
    }

    @Test
    fun getConnectionToDBTestFailureOnNonParseableURL()
    {
        // Arrange
        val connectionToTest = InfluxConnection(
            "lol", "tok", "org"
        )

        // Action
        val influxConnection = try
        {
            connectionToTest.getConnectionToDB()
        }
        catch (exc: InfluxException)
        {
            null
        }

        // Assert
        assert(influxConnection == null)
    }

    @Test
    fun getConnectionWriteTestSuccess()
    {
        // Arrange
        val connectionToTest = InfluxConnection(
            "http://localhost:8086",
            "tok", "org"
        )
        val bucketName = "someone"

        // Action
        val influxConnection = try
        {
            connectionToTest.getConnectionWrite(bucketName)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(influxConnection != null)
    }

    @Test
    fun getConnectionWriteTestFailureOnNonParseableURL()
    {
        // Arrange
        val connectionToTest = InfluxConnection(
            "lol", "tok", "org"
        )
        val bucketName = "someone"

        // Action
        val influxConnection = try
        {
            connectionToTest.getConnectionWrite(bucketName)
        }
        catch (exc: InfluxException)
        {
            null
        }

        // Assert
        assert(influxConnection == null)
    }
}