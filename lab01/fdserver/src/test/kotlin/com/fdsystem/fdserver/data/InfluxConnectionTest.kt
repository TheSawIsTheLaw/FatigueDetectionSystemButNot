package com.fdsystem.fdserver.data

import com.influxdb.exceptions.InfluxException
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class InfluxConnectionTest {
    @Test
    fun getConnectionToDBTestSuccess() {
        // Arrange
        val connectionToTest = InfluxConnection(
            "http://localhost:8086",
            "tok", "org"
        )

        // Action

        // Assert
        assertThatNoException().isThrownBy { connectionToTest.getConnectionToDB() }
    }

    @Test
    fun getConnectionToDBTestFailureOnNonParseableURL() {
        // Arrange
        val connectionToTest = InfluxConnection(
            "lol", "tok", "org"
        )

        // Action

        // Assert
        assertThatExceptionOfType(InfluxException::class.java)
            .isThrownBy { connectionToTest.getConnectionToDB() }
    }

    @Test
    fun getConnectionWriteTestSuccess() {
        // Arrange
        val connectionToTest = InfluxConnection(
            "http://localhost:8086",
            "tok", "org"
        )
        val bucketName = "someone"

        // Action

        // Assert
        assertThatNoException().isThrownBy { connectionToTest.getConnectionWrite(bucketName) }
    }

    @Test
    fun getConnectionWriteTestFailureOnNonParseableURL() {
        // Arrange
        val connectionToTest = InfluxConnection(
            "lol", "tok", "org"
        )
        val bucketName = "someone"

        // Action

        // Assert
        assertThatExceptionOfType(InfluxException::class.java).isThrownBy {
            connectionToTest.getConnectionWrite(
                bucketName
            )
        }
    }
}