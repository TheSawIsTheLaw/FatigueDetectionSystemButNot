package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.time.Instant

internal class DataServiceTest
{
    val charRepositoryMock: CharRepositoryImpl =
        Mockito.mock(CharRepositoryImpl::class.java)

    private val gotMeasurementsGetTest: List<DSMeasurement> = listOf(
        DSMeasurement("pulse", "60", Instant.MIN),
        DSMeasurement("pulse", "63", Instant.MIN)
    )

    init
    {
        Mockito.`when`(
            charRepositoryMock.get(
                DSDataAccessInfo(
                    "123",
                    "someone",
                    Pair(0, 0),
                    "pulse"
                )
            )
        ).thenReturn(gotMeasurementsGetTest)
    }

    @Test
    fun getMeasurement()
    {
        // Arrange
        // Prepare parameters
        val token = "123"
        val bucketName = "someone"
        val charName = "pulse"

        // Prepare Mock
        val service = DataService(charRepositoryMock)

        // Set private method public
        val requiredPrivateMethod =
            service.javaClass.getDeclaredMethod(
                "getMeasurement",
                String::class.java, String::class.java, String::class.java
            )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(3)
        privateMethodParameters[0] = token
        privateMethodParameters[1] = bucketName
        privateMethodParameters[2] = charName

        // Act
        val returnedMeasurements =
            requiredPrivateMethod.invoke(service, *privateMethodParameters)

        // Assert
        assert(returnedMeasurements == gotMeasurementsGetTest)
    }

    @Test
    fun sendMeasurements()
    {
    }
}