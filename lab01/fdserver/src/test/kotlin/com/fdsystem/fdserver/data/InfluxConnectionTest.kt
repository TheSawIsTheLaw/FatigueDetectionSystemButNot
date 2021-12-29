package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.mothers.InfluxConnectionOMother
import com.influxdb.exceptions.InfluxException
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test

internal class InfluxConnectionTest {
    private val oMother = InfluxConnectionOMother()

    @Test
    fun getConnectionToDBTestSuccess() {
        // Arrange
        val connectionToTest =
            InfluxConnection(oMother.defaultConnectionString, oMother.defaultToken, oMother.defaultOrg)

        // Action

        // Assert
        assertThatNoException().isThrownBy { connectionToTest.getConnectionToDB() }
    }

    @Test
    fun getConnectionToDBTestFailureOnNonParseableURL() {
        // Arrange
        val connectionToTest =
            InfluxConnection(oMother.nonParseableConnectionString, oMother.defaultToken, oMother.defaultOrg)

        // Action

        // Assert
        assertThatExceptionOfType(InfluxException::class.java)
            .isThrownBy { connectionToTest.getConnectionToDB() }
    }

    @Test
    fun getConnectionWriteTestSuccess() {
        // Arrange
        val connectionToTest =
            InfluxConnection(oMother.defaultConnectionString, oMother.defaultToken, oMother.defaultOrg)

        val bucketName = oMother.defaultBucketName

        // Action

        // Assert
        assertThatNoException().isThrownBy { connectionToTest.getConnectionWrite(bucketName) }
    }

    @Test
    fun getConnectionWriteTestFailureOnNonParseableURL() {
        // Arrange
        val connectionToTest =
            InfluxConnection(oMother.nonParseableConnectionString, oMother.defaultToken, oMother.defaultOrg)

        val bucketName = oMother.defaultBucketName

        // Action

        // Assert
        assertThatExceptionOfType(InfluxException::class.java).isThrownBy {
            connectionToTest.getConnectionWrite(
                bucketName
            )
        }
    }
}